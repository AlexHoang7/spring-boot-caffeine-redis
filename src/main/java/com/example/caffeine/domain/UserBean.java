package com.example.caffeine.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户类
 *
 * @author alex
 */
@Data
public class UserBean implements Serializable {

    private static final long serialVersionUID = 4724826077363257625L;

    private Integer id;

    private String userId;

    private String password;

    private String username;

    private Integer age;

    private String gender;
}