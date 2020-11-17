/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package io.renren.common.user;

import io.renren.modules.sys.entity.SysUserEntity;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.Subject;

/**
 * 用户
 *
 * @author Mark sunlightcs@gmail.com
 */
public class SecurityUser {

    public static Subject getSubject() {
        try {
            return SecurityUtils.getSubject();
        }catch (Exception e){
            return null;
        }
    }

    /**
     * 获取用户信息
     */
    public static SysUserEntity getUser() {
        Subject subject = getSubject();
        if(subject == null){
            return new SysUserEntity();
        }

        SysUserEntity user = (SysUserEntity)subject.getPrincipal();
        if(user == null){
            return new SysUserEntity();
        }

        return user;
    }

    /**
     * 获取用户ID
     */
    public static Long getUserId() {
        return getUser().getUserId();
    }

    public static void setUser(UserDetail user) {
        Subject subject = SecurityUtils.getSubject();
        PrincipalCollection principalCollection = subject.getPrincipals();
        String realmName = principalCollection.getRealmNames().iterator().next();
        PrincipalCollection newPrincipalCollection =
                new SimplePrincipalCollection(user, realmName);
        //subject.runAs(newPrincipalCollection);
        subject.getSession().setAttribute("org.apache.shiro.subject.support.DefaultSubjectContext_PRINCIPALS_SESSION_KEY", newPrincipalCollection);
    }

}