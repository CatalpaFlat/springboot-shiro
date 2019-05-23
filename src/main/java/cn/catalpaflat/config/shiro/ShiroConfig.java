package cn.catalpaflat.config.shiro;

import cn.catalpaflat.config.shiro.filter.CustomFormAuthenticationFilter;
import cn.catalpaflat.config.shiro.realm.ShiroRealm;
import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.Setter;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
