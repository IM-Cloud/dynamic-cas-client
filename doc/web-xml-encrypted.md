访问: 

`https://sp-ip/index/?cas_url=GEGKf1cvvuZdohJN5JhOpurqEdNagGJdwMoVsrmtf2tsncDDdHmr4UpiOgKGsedqJ7qOA0xet31iifXDcpkzTqvqbv35e3%2B9htDoYfSSppmRDNrp8bapNGueutyArYRsOSKLmXJi3ixEZ1KbgnYCeBwp4ppv8rlx3f08g5%2BlI5wkMP1F4Unt%2BIiEVXNbUBRg1hkAT4PD0g8kZJEe2RJkdz5Lazvf10bgg3M7OS4bc7QHOQz0ui%2Bq2zsR3M6oGrLgH8LnFNUykvJqOSpl1YzcHTZ03qmELpzS5SFNcBKUvML4XBBFI9qDBbpN7jsyYCr1KzapwitNFCDT8mUhD%2FAuzw%3D%3D`

```xml
<!DOCTYPE web-app PUBLIC
 "-//Sun Microsystems, Inc.//DTD Web Application 2.3//EN"
 "http://java.sun.com/dtd/web-app_2_3.dtd" >

<web-app>
    <!-- Single sign on -->
    <listener>
        <listener-class>org.jasig.cas.client.session.SingleSignOutHttpSessionListener</listener-class>
    </listener>
    <filter>
        <filter-name>CAS Single Sign Out Filter</filter-name>
        <filter-class>org.jasig.cas.client.session.SingleSignOutFilter</filter-class>
    </filter>
    <filter>
        <filter-name>CASFilter</filter-name>
        <filter-class>com.dengshaolin.cas.filter.AuthenticationFilter</filter-class>
        <init-param>
            <param-name>serverName</param-name>
            <param-value>https://sp-ip</param-value>
        </init-param>
        <init-param>
            <param-name>redirectAfterValidation</param-name>
            <param-value>true</param-value>
        </init-param>
        <init-param>
            <param-name>urlEncrypted</param-name>
            <param-value>true</param-value>
        </init-param>
        <init-param>
            <param-name>decryptor</param-name>
            <param-value>com.dengshaolin.decrypt.RSADecrypt</param-value>
        </init-param>
        <init-param>
            <param-name>key</param-name>
            <param-value>MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAiJ6h3T0zsrUFOOqDR4LxrOYxKy2jtYoDiC8wsQrpvAzbM+QCIdY0kT87V7Vxzr7l0GPTgHHKE5wLYzqCafgKDj4qo8odV9To/CsrwJ3vMm/6ZHShjDFdy4n4jmMpKcvJWrvx5sBlYpVD8s9haHDmAj1sqWDhqFGpYIu1FJHoRopZD53uCw1GTOa+qqFER6huzB74LVA2/LTjCUHmlikUhbaLRKKm3GBq1noZ8CyBzAJXq8FOO+ogUbz3cLudAbGsL+lhDpMfMRoY8Ju5Mzd+HpTwittHBOjK/0xM1PGO1Bp2oomDMmqYqTEovwVcHsUJdCYLZ7NLiMHJ5F3h2p4XxwIDAQAB</param-value>
        </init-param>
    </filter>
    <filter>
        <filter-name>CAS Validation Filter</filter-name>
        <filter-class>
            com.dengshaolin.cas.filter.Cas20ProxyReceivingTicketValidationFilter
        </filter-class>
        <init-param>
            <param-name>serverName</param-name>
            <param-value>https://sp-ip</param-value>
        </init-param>
        <init-param>
            <param-name>urlEncrypted</param-name>
            <param-value>true</param-value>
        </init-param>
        <init-param>
            <param-name>decryptor</param-name>
            <param-value>com.dengshaolin.decrypt.RSADecrypt</param-value>
        </init-param>
        <init-param>
            <param-name>key</param-name>
            <param-value>MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAiJ6h3T0zsrUFOOqDR4LxrOYxKy2jtYoDiC8wsQrpvAzbM+QCIdY0kT87V7Vxzr7l0GPTgHHKE5wLYzqCafgKDj4qo8odV9To/CsrwJ3vMm/6ZHShjDFdy4n4jmMpKcvJWrvx5sBlYpVD8s9haHDmAj1sqWDhqFGpYIu1FJHoRopZD53uCw1GTOa+qqFER6huzB74LVA2/LTjCUHmlikUhbaLRKKm3GBq1noZ8CyBzAJXq8FOO+ogUbz3cLudAbGsL+lhDpMfMRoY8Ju5Mzd+HpTwittHBOjK/0xM1PGO1Bp2oomDMmqYqTEovwVcHsUJdCYLZ7NLiMHJ5F3h2p4XxwIDAQAB</param-value>
        </init-param>
    </filter>
    <filter>
        <filter-name>CAS HttpServletRequest Wrapper Filter</filter-name>
        <filter-class>
            org.jasig.cas.client.util.HttpServletRequestWrapperFilter
        </filter-class>
    </filter>
    <filter>
        <filter-name>CAS Assertion Thread Local Filter</filter-name>
        <filter-class>org.jasig.cas.client.util.AssertionThreadLocalFilter</filter-class>
    </filter>
    <filter-mapping>
        <filter-name>CAS Single Sign Out Filter</filter-name>
        <url-pattern>/*</url-pattern>
    </filter-mapping>
    <filter-mapping>
        <filter-name>CASFilter</filter-name>
        <url-pattern>/*</url-pattern>
    </filter-mapping>
    <filter-mapping>
        <filter-name>CAS Validation Filter</filter-name>
        <url-pattern>/*</url-pattern>
    </filter-mapping>
    <filter-mapping>
        <filter-name>CAS HttpServletRequest Wrapper Filter</filter-name>
        <url-pattern>/*</url-pattern>
    </filter-mapping>
    <filter-mapping>
        <filter-name>CAS Assertion Thread Local Filter</filter-name>
        <url-pattern>/*</url-pattern>
    </filter-mapping>
    <!-- Single sign on end -->

    <session-config>
        <session-timeout>20</session-timeout>
        <tracking-mode>COOKIE</tracking-mode>
    </session-config>

    <welcome-file-list>
        <welcome-file>index.html</welcome-file>
    </welcome-file-list>
</web-app>
```

请替换以上相关变量：

```text
cas-ip: cas所在服务器地址
sp-ip：服务ip
```