package com.example.caffeine.service;

import com.example.caffeine.domain.UserBean;

/**
 * 用户服务
 *
 * @author Alex
 */
public interface IUserService {

    /**
     * 根据id查找用户
     *
     * @param id id
     * @return 用户
     */
    UserBean findById(final int id);
}