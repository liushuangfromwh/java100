package com.ls.s06;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * @Description: 事务回滚测试
 * 事务回滚是指将该事务已经完成对数据库的更新操作撤销，在事务中，每个正确的原子都会被顺序执行，知道遇到错误的原子操作。
 * @author: liushuang26
 * @Date: 2022/4/24 9:39
 */

@Service
@Slf4j
public class UserService {

    @Autowired
    private UserService self;
    @Autowired
    private UserRepository userRepository;

    /**
     * 一个公共方法供Controller调用，内部调用事务性的私有方法,事务不会生效
     * 原因: Spring 默认通过动态代理的方式实现 AOP，对目标方法进行增强，private 方法无法代理到，
     * Spring 自然也无法动态增强事务处理逻辑。
     * CGLIB 通过继承方式实现代理类，private 方法在子类不可见，自然也就无法进行事务增强；
     */
    public int createUserWrong1(String name) {
        try {
            this.createUserPrivate1(new UserEntity(name));
        } catch (Exception ex) {
            log.error("create user failed because {}", ex.getMessage());
        }
        return getUserCount(name);
    }

    //标记了@Transactional的private方法
    @Transactional
    private void createUserPrivate1(UserEntity entity) {
        //逻辑省略 回滚的时候数据的操作
        userRepository.save(entity);
        if (entity.getName().contains("test")) {
            throw new RuntimeException("invalid username!");
        }

    }

    //根据用户名查询用户数
    public int getUserCount(String name) {
        return userRepository.findByName(name).size();
    }

    /**
     * createUserPrivate2改为public就会生效吗? 也不会生效.为什么?
     * 必须通过代理过的类从外部调用目标方法才能生效。
     * this 指针代表对象自己，Spring 不可能注入 this，所以通过 this 访问方法必然不是代理。
     */
    public int createUserWrong2(String name) {
        try {
            this.createUserPublic(new UserEntity(name));
            //这里可以通过自己注入自己,来保证事务正常生效
            //self.createUserPrivate2(new UserEntity(name));
        } catch (Exception ex) {
            log.error("create user failed because {}", ex.getMessage());
        }
        return getUserCount(name);
    }


    @Transactional
    public void createUserPublic(UserEntity entity) {
        //逻辑省略
        userRepository.save(entity);
        if (entity.getName().contains("test")) {
            throw new RuntimeException("invalid username!");
        }
    }

    //异常无法传播出方法，导致事务无法回滚
    @Transactional
    public void createUserWrong3(String name) {
        try {
            userRepository.save(new UserEntity(name));
            throw new RuntimeException("error");
        } catch (Exception ex) {
            log.error("create user failed", ex);
        }
    }

    /**
     * @Transactional 生效的条件
     * 1. 被标记的方法需要抛出异常 例如createUserWrong1上加上@Transactional
     * 2. 默认情况下，出现 RuntimeException（非受检异常）或 Error 的时候，Spring 才会回滚事务
     */
    //即使出了受检异常也无法让事务回滚  这里受检异常是IOException
    //怎么解决呢?
    //在注解中声明，期望遇到所有的 Exception 都回滚事务（来突破默认不回滚受检异常的限制）：
    //@Transactional
    @Transactional(rollbackFor = Exception.class)
    public void createUserWrong4(String name) throws IOException {
        userRepository.save(new UserEntity(name));
        otherTask();
    }

    //因为文件不存在，一定会抛出一个IOException
    private void otherTask() throws IOException {
        Files.readAllLines(Paths.get("file-that-not-exist"));
    }


    @Transactional
    public void createUserRight1(String name) {
        try {
            userRepository.save(new UserEntity(name));
            throw new RuntimeException("error");
        } catch (Exception ex) {
            log.error("create user failed", ex);
            //手动设置回滚 让save回滚
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
    }


    //todo 思考题 手动写一下aop切面的实现
}