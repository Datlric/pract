package com.pract.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pract.domain.User;
import com.pract.mapper.UserMapper;
import com.pract.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public List<User> getAll() {
        return userMapper.getAll();
    }

    @Transactional(rollbackFor = {Exception.class})
    @Override
    public int SaveOneUser(User user) throws Exception {
        String result = userMapper.selectUserByUsername(user.getUsername());
        if (result != null) {
            return -1;
        } else {
            return userMapper.saveOne(user);
        }
    }

    @Override
    public PageInfo<?> selectByPage(Integer pageNum, Integer pageSize, String search) {
        PageHelper.startPage(pageNum, pageSize);
        PageHelper.orderBy("id");
        return new PageInfo<User>(userMapper.getAll(search));
    }

    @Transactional(rollbackFor = {Exception.class})
    @Override
    public Integer deleteUserInfo(Integer id) {
        return userMapper.deleteById(id);
    }


    @Transactional(rollbackFor = {Exception.class})
    @Override
    public Integer updateUserInfo(User user) {
        return userMapper.updateById(user);
    }

    @Override
    public User userLogin(User user) {
        return userMapper.selectOne(user);
    }

}
