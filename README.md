# SpringBoot 整合Shiro 之 自定义Filter



> 结合上一篇 【Spring Boot 整合 Shiro】，第一次使用之后，但发现，Shiro过滤器对被 劫持 的API路径，若没“login.jsp”，则会直接返回 404 ，很不和谐。因此，捣鼓一下，自定义FIlter，通过自定义对其进行授权认证。



## 1. 自定义 Filter

```java
@Slf4j
public class CustomFormAuthenticationFilter extends FormAuthenticationFilter {

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue){
        return false;
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        if (isLoginRequest(request, response)) {
            if (!isLoginSubmission(request, response)) {
                if (log.isTraceEnabled()) {
                    log.trace("Attempting to access a path which requires authentication.  Forwarding to the " +
                            "Authentication url [" + getLoginUrl() + "]");
                }

                HttpServletResponse httpServletResponse = (HttpServletResponse) response;
                httpServletResponse.setContentType("application/json;charset=UTF-8");
                httpServletResponse.setStatus(HttpStatus.CONFLICT.value());
                JSONObject json = new JSONObject();
                json.put("message","没有权限访问");
                Writer writer = httpServletResponse.getWriter();
                writer.write(json.toJSONString());
                writer.flush();
                writer.close();
            }else {
                return executeLogin(request, response);
            }
        }
        return false;
    }
}
```



根据Shiro默认的过滤器链，我们可以通过继承，并将自身Filter添加到其过滤器链中

```java
anon(AnonymousFilter.class),
authc(FormAuthenticationFilter.class),
authcBasic(BasicHttpAuthenticationFilter.class),
logout(LogoutFilter.class),
noSessionCreation(NoSessionCreationFilter.class),
perms(PermissionsAuthorizationFilter.class),
port(PortFilter.class),
rest(HttpMethodPermissionFilter.class),
roles(RolesAuthorizationFilter.class),
ssl(SslFilter.class),
user(UserFilter.class);
```

## 2. 重新配置ShiroConfig

```java
@Configuration
@ConfigurationProperties(prefix = "shiro")
public class ShiroConfig {

    private final static String AUTHC_STR = "authc";
    private final static String ANON_STR = "anon";

    @Getter
    @Setter
    private List<String> anon_uri;

    /**
     * 验证授权、认证
     *
     * @return shiroRealm 授权认证
     */
    @Bean
    public ShiroRealm shiroRealm(){
        return new ShiroRealm();
    }

    /**
     * session manager
     *
     * @param shiroRealm  授权认证
     * @return  安全管理
     */
    @Bean
    @ConditionalOnClass(ShiroRealm.class)
    public SecurityManager securityManager(ShiroRealm shiroRealm){
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(shiroRealm);
        return securityManager;
    }
    
    @Bean
    public CustomFormAuthenticationFilter customAuthenticationFilter(){
        return new CustomFormAuthenticationFilter();
    }

    /**
     * Filter工厂，设置对应的过滤条件和跳转条件
     *
     * @param securityManager session 管理
     * @return shiro 过滤工厂
     */
    @Bean
    @ConditionalOnClass(value = {CustomFormAuthenticationFilter.class,SecurityManager.class})
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager, CustomFormAuthenticationFilter customAuthenticationFilter) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);

        // 自定义过滤器
        Map<String, Filter> filterMap = shiroFilterFactoryBean.getFilters();
        filterMap.put("restful_return", customAuthenticationFilter);

        shiroFilterFactoryBean.setFilters(filterMap);

        //URI过滤
        Map<String,String> map = Maps.newLinkedHashMap();
        
        //可过滤的接口路径
        anon_uri.forEach(item -> map.put(item,ANON_STR));

        //所有路径进行校验
        map.put("/api/**",AUTHC_STR);

        shiroFilterFactoryBean.setFilterChainDefinitionMap(map);

        return shiroFilterFactoryBean;
    }


}
```



主要两点：

### 2.1.注入CustomFormAuthenticationFilter

```java
@Bean
public CustomFormAuthenticationFilter customAuthenticationFilter(){
	return new CustomFormAuthenticationFilter();
}
```

### 2.2 加入过滤器链

```java
// 自定义过滤器
Map<String, Filter> filterMap = shiroFilterFactoryBean.getFilters();
filterMap.put("restful_return", customAuthenticationFilter);

shiroFilterFactoryBean.setFilters(filterMap);
```


