/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package io.renren.common.user;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 登录用户信息
 *
 * @author Mark sunlightcs@gmail.com
 */
@Data
public class UserDetail implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String username;
    private String realName;
    private String headUrl;
    private Integer gender;
    private String email;
    private String mobile;
    private Long deptId;
    private Long businessId;
    private String password;
    private Integer status;
    private Integer superAdmin;
    private String idCard;
    private Integer businessIsPerfect;
    private Integer sobIsPerfect;
    private Integer type;
    private String dataAuthority;
    private String regionName;//区域名称
    private Integer superTenant;
    private Long sobId;
    private String sobName;
    private String expirationDate;
    /**
     * 部门数据权限
     */
    private List<Long> deptIdList;
    /**
     * 租户编码
     */
    private Long tenantCode;
    private String beEntrusted;
}