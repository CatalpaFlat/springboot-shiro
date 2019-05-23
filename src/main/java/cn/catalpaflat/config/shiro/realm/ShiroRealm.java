package cn.catalpaflat.config.shiro.realm;

import cn.catalpaflat.model.po.UserPO;
import cn.catalpaflat.repository.ShiroPermissionRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
public class ShiroRealm extends AuthorizingRealm {

    @Autowired
    private ShiroPermissionRepository shiroPermissionRepository;

    /**
     * 授权
     *
     * @param principalCollection 主要信息
     * @return 授权信息
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        if (log.isInfoEnabled()){
            log.info("Authorization begin");
        }
        String name= (String) principalCollection.getPrimaryPrincipal();
        List<String> role = shiroPermissionRepository.queryRoleByName(name);
        if (role.isEmpty()){
            SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
            simpleAuthorizationInfo.addRoles(role);
            return simpleAuthorizationInfo;
        }
        return null;
    }

    /**
     * 认证
     *
     * @param authenticationToken 认证token
     * @return 认证结果
     * @throws AuthenticationException 认证异常
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        if (log.isInfoEnabled()){
            log.info("Authentication begin");
        }

        UsernamePasswordToken token = (UsernamePasswordToken) authenticationToken;

        Object principal =token.getPrincipal();
        Object credentials = token.getCredentials();

        //校验用户名
        checkBlank(principal,"用户名不能为空");
        //校验密码
        checkBlank(credentials,"密码不能为空");

        //校验姓名
        String username = (String) principal;
        UserPO userPO = shiroPermissionRepository.findAllByName(username);
        if (userPO == null){
            throw new AccountException("用户名错误");
        }

        //校验密码
        String password = (String) credentials;
        if (!StringUtils.equals(password,userPO.getPassword())){
            throw new AccountException("密码错误");
        }

        return new SimpleAuthenticationInfo(principal, password, getName());
    }

    private void checkBlank(Object obj,String message){
        if (obj instanceof String){
            if (StringUtils.isBlank((String) obj)){
                throw new AccountException(message);
            }
        }else if (obj == null){
            throw new AccountException(message);
        }
    }
}
