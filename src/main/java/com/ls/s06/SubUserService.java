package com.ls.s06;

import com.ls.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Description: TODO
 * @author: liushuang26
 * @Date: 2022/4/24 11:34
 */

@Service
@Slf4j
public class SubUserService {

    @Autowired
    private UserMapper userMapper;

    @Transactional
    public void createSubUserWithExceptionWrong(User entity) {
        log.info("createSubUserWithExceptionWrong start");
        userMapper.insert(entity);
        throw new RuntimeException("invalid status");
    }

    //也就是执行到这个方法时需要开启新的事务，并挂起当前事务：
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createSubUserWithExceptionRight1(User entity) {
        log.info("createSubUserWithExceptionWrong start");
        userMapper.insert(entity);
        throw new RuntimeException("invalid status");
    }
}
