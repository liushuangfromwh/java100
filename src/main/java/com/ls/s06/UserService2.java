package com.ls.s06;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ls.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Description: 事务的传播机制
 * @author: liushuang26
 * @Date: 2022/4/24 11:33
 */
@Service
@Slf4j
public class UserService2 {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private SubUserService subUserService;

    /**
     * 我们期望子用户的注册作为一个事务单独回滚，不影响主用户的注册，这样的逻辑可以实现吗？
     * @param entity
     */
    @Transactional
    public void createUserWrong1(User entity) {
        createMainUser(entity);
        subUserService.createSubUserWithExceptionWrong(entity);
    }

    /**
     * 即使异常在这里被catch住,但是
     * @param entity
     */
    @Transactional
    public void createUserWrong2(User entity) {
        createMainUser(entity);
        try{
            subUserService.createSubUserWithExceptionWrong(entity);
        } catch (Exception ex) {
            // 虽然捕获了异常，但是因为没有开启新事务，而当前事务因为异常已经被标记为rollback了，所以最终还是会回滚。
            //原因是，主方法注册主用户的逻辑和子方法注册子用户的逻辑是同一个事务，子逻辑标记了事务需要回滚，主逻辑自然也不能提交了。
            log.error("create sub user error:{}", ex.getMessage());
        }
    }

    @Transactional
    public void createUserRight1(User entity) {
        createMainUser(entity);
        try{
            subUserService.createSubUserWithExceptionRight1(entity);
        } catch (Exception ex) {
            //createSubUserWithExceptionRight1中开启了一个新的事务,子事务不会影响调用者的事务
            log.error("create sub user error:{}", ex.getMessage());
        }
    }

    private void createMainUser(User entity) {
        userMapper.insert(entity);
        log.info("createMainUser finish");
    }

    public int getUserCount(String name) {
        LambdaQueryWrapper<User> lambdaQuery = Wrappers.lambdaQuery();
        lambdaQuery.eq(User::getName,name);
        List<User> users = userMapper.selectList(lambdaQuery);
        return users.size();
    }
}
