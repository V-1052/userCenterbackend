package com.yanshui.usercenter.model.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * user
 * @TableName user
 */
@TableName(value = "user")
@Data
public class User implements java.io.Serializable {
    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * username
     */
    @TableField(value = "username")
    private String username;

    /**
     * userAccount
     */
    @TableField(value = "userAccount")
    private String userAccount;

    /**
     * userAvatarUrl
     */
    @TableField(value = "avatarUrl")
    private String avatarUrl;

    /**
     * gender
     */
    @TableField(value = "gender")
    private Integer gender;

    /**
     * userPassword
     */
    @TableField(value = "userPassword")
    private String userPassword;

    /**
     * phone
     */
    @TableField(value = "phone")
    private String phone;

    /**
     * email
     */
    @TableField(value = "email")
    private String email;

    /**
     * userStatus 0-normal 1-banned
     */
    @TableField(value = "userStatus")
    private Integer userStatus;

    /**
     * createTime
     */
    @TableField(value = "createTime")
    private Date createTime;

    /**
     * updateTime
     */
    @TableField(value = "updateTime")
    private Date updateTime;

    /**
     * isDelete 0-not deleted 1-deleted
     */
    @TableField(value = "isDelete")
    private Integer isDelete;

    /**
     * userRole 0-default 1-admin
     */
    @TableField(value = "userRole")
    private Integer userRole;
}