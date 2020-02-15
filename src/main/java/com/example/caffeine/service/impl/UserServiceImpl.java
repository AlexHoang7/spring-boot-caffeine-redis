package com.example.caffeine.service.impl;

import com.example.caffeine.dao.IUserBeanMapper;
import com.example.caffeine.domain.UserBean;
import com.example.caffeine.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 用户服务
 *
 * @author Alex
 */
@Slf4j
@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private IUserBeanMapper userBeanMapper;

    @Override
    public UserBean findById(int id) {
        UserBean userBean = userBeanMapper.selectByPrimaryKey(id);
        return userBean;
    }
}
