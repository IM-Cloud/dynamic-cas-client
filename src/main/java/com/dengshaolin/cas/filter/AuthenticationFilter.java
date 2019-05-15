package com.dengshaolin.cas.filter;

import org.jasig.cas.client.authentication.DefaultGatewayResolverImpl;
import org.jasig.cas.client.authentication.GatewayResolver;
import org.jasig.cas.client.util.AbstractCasFilter;
import org.jasig.cas.client.util.CommonUtils;
import org.jasig.cas.client.validation.Assertion;

import com.dengshaolin.decrypt.Decrypt;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Filter implementation to intercept all requests and attempt to authenticate
 * the user by redirecting them to CAS (unless the user has a ticket).
 * <p>
 * This filter allows you to specify the following parameters (at either the context-level or the filter-level):
 * <ul>
 * <li><code>casServerLoginUrl</code> - the url to log into CAS, i.e. https://cas.rutgers.edu/login</li>
 * <li><code>renew</code> - true/false on whether to use renew or not.</li>
 * <li><code>gateway</code> - true/false on whether to use gateway or not.</li>
 * </ul>
 *
 * <p>Please see AbstractCasFilter for additional properties.</p>
 */
public class AuthenticationFilter extends AbstractCasFilter {
    /**
     * The URL to the CAS Server login.
     */
//    private String casServerLoginUrl;

    /**
     * Whether to send the renew request or not.
     */
    private boolean renew = false;

    /**
     * Whether to send the gateway request or not.
     */
    private boolean gateway = false;

    private String casUrlKey = null;

    private boolean urlEncrypted = true;

    private Decrypt decryptor = null;

    private String key = null;
    
    private GatewayResolver gatewayStorage = new DefaultGatewayResolverImpl();

    protected void initInternal(final FilterConfig filterConfig) throws ServletException {
        if (!isIgnoreInitConfiguration()) {
            super.initInternal(filterConfig);
//            setCasServerLoginUrl(getPropertyFromInitParams(filterConfig, "casServerLoginUrl", null));
//            log.trace("Loaded CasServerLoginUrl parameter: " + this.casServerLoginUrl);
            setRenew(parseBoolean(getPropertyFromInitParams(filterConfig, "renew", "false")));
            log.trace("Loaded renew parameter: " + this.renew);
            setGateway(parseBoolean(getPropertyFromInitParams(filterConfig, "gateway", "false")));
            log.trace("Loaded gateway parameter: " + this.gateway);
            setCasUrlKey(getPropertyFromInitParams(filterConfig, "casUrlKey", "cas_url"));
            log.trace("Loaded casUrlKey parameter: " + this.casUrlKey);
            setUrlEncrypted(parseBoolean(getPropertyFromInitParams(filterConfig, "urlEncrypted", "false")));
            setKey(getPropertyFromInitParams(filterConfig, "key", null));

            final String decryptorClass = getPropertyFromInitParams(filterConfig, "decryptor", null);
            final String gatewayStorageClass = getPropertyFromInitParams(filterConfig, "gatewayStorageClass", null);

            if (gatewayStorageClass != null) {
                try {
                    this.gatewayStorage = (GatewayResolver) Class.forName(gatewayStorageClass).newInstance();
                } catch (final Exception e) {
                    log.error(e,e);
                    throw new ServletException(e);
                }
            }

            if (decryptorClass != null) {
                try {
                    this.decryptor = (Decrypt) Class.forName(decryptorClass).newInstance();
                } catch (final Exception e) {
                    log.error(e,e);
                    throw new ServletException(e);
                }
            }
        }
    }

    public void init() {
        super.init();
//        CommonUtils.assertNotNull(this.casServerLoginUrl, "casServerLoginUrl cannot be null.");
    }

    public final void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse, final FilterChain filterChain) throws IOException, ServletException {
        final HttpServletRequest request = (HttpServletRequest) servletRequest;
        final HttpServletResponse response = (HttpServletResponse) servletResponse;
        final HttpSession session = request.getSession(false);
        final Assertion assertion = session != null ? (Assertion) session.getAttribute(CONST_CAS_ASSERTION) : null;

        if (assertion != null) {
            filterChain.doFilter(request, response);
            return;
        }

        final String serviceUrl = constructServiceUrl(request, response);
        final String ticket = CommonUtils.safeGetParameter(request,getArtifactParameterName());
        final boolean wasGatewayed = this.gatewayStorage.hasGatewayedAlready(request, serviceUrl);

        if (CommonUtils.isNotBlank(ticket) || wasGatewayed) {
            filterChain.doFilter(request, response);
            return;
        }

        final String modifiedServiceUrl;

        log.debug("no ticket and no assertion found");
        if (this.gateway) {
            log.debug("setting gateway attribute in session");
            modifiedServiceUrl = this.gatewayStorage.storeGatewayInformation(request, serviceUrl);
        } else {
            modifiedServiceUrl = serviceUrl;
        }

        if (log.isDebugEnabled()) {
            log.debug("Constructed service url: " + modifiedServiceUrl);
        }

        String casUrlInUrl = request.getParameter(casUrlKey);
        if (CommonUtils.isBlank(casUrlInUrl))
        {
            log.error("cas url not provide, could not redirect to login");
            servletResponse.getWriter().print("cas url not provide.");
            servletResponse.getWriter().flush();
            return;
        }
        String casUrl = "";
        if (urlEncrypted) {
            if (null != decryptor) {
                try {
                    casUrl = decryptor.decrypt(key, casUrlInUrl);
                } catch (Exception e) {
                    log.error("decrypt cas url failed", e);
                    servletResponse.getWriter().print("decrypt cas url failed. " + e.getLocalizedMessage());
                    servletResponse.getWriter().flush();
                    return;
                }
            } else {
                return;
            }
        } else {
            casUrl = casUrlInUrl;
        }

        casUrl += "/login";
        if (log.isDebugEnabled()) {
            log.debug("cas url: " + casUrl);
        }

        final String urlToRedirectTo = CommonUtils.constructRedirectUrl(casUrl, getServiceParameterName(), modifiedServiceUrl, this.renew, this.gateway);

        if (log.isDebugEnabled()) {
            log.debug("redirecting to \"" + urlToRedirectTo + "\"");
        }

        response.sendRedirect(urlToRedirectTo);
    }

    public final void setRenew(final boolean renew) {
        this.renew = renew;
    }

    public final void setGateway(final boolean gateway) {
        this.gateway = gateway;
    }

//    public final void setCasServerLoginUrl(final String casServerLoginUrl) {
//        this.casServerLoginUrl = casServerLoginUrl;
//    }

    public void setCasUrlKey(String casUrlKey) {
        this.casUrlKey = casUrlKey;
    }

    public final void setGatewayStorage(final GatewayResolver gatewayStorage) {
        this.gatewayStorage = gatewayStorage;
    }

    public void setUrlEncrypted(boolean urlEncrypted) {
        this.urlEncrypted = urlEncrypted;
    }

    public void setDecryptor(String decryptCls) {
        if (null == decryptCls) {
            return;
        }
        try {
            decryptor = (Decrypt)(Class.forName(decryptCls).newInstance());
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            log.error("construct decrypt object failed", e);
        }
    }

    public void setKey(String key) {
        this.key = key;
    }
}
