package com.example.caffeine.dao;

import com.example.caffeine.domain.UserBean;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 用户操作接口
 *
 * @author alex
 */
@Repository
public interface IUserBeanMapper {

    int deleteByPrimaryKey(Integer id);

    int insert(UserBean record);

    int insertSelective(UserBean record);

    UserBean selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(UserBean record);

    int updateByPrimaryKey(UserBean record);

    List<UserBean> selectUserBeanList();
}