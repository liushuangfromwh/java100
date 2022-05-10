package com.ls.s06;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: TODO
 * @author: liushuang26
 * @Date: 2022/4/24 11:37
 */
@RestController
@RequestMapping("UserService2Controller")
@Slf4j
public class UserService2Controller {

    @Autowired
    private UserService2 userService2;

    // logging.level.org.springframework.orm.jpa=DEBUG 开启日志 这里是jpa的 看一下mybatis

    @GetMapping("wrong1")
    public int wrong1(@RequestParam("name") String name) {
        try {
            userService2.createUserWrong1(new UserEntity(name));
        } catch (Exception ex) {
            log.error("createUserWrong failed, reason:{}", ex.getMessage());
        }
        return userService2.getUserCount(name);
    }

    @GetMapping("wrong2")
    public int wrong2(@RequestParam("name") String name) {
        try {
            userService2.createUserWrong2(new UserEntity(name));
        } catch (Exception ex) {
            log.error("createUserWrong failed, reason:{}", ex.getMessage());
        }
        return userService2.getUserCount(name);
    }

    @GetMapping("right1")
    public int right1(@RequestParam("name") String name) {
        try {
            userService2.createUserRight1(new UserEntity(name));
        } catch (Exception ex) {
            log.error("createUserWrong failed, reason:{}", ex.getMessage());
        }
        return userService2.getUserCount(name);
    }

    /**
     * 总结
     * 1. 第一，因为配置不正确，导致方法上的事务没生效。我们务必确认调用 @Transactional 注解标记的方法是 public 的，
     * 并且是通过 Spring 注入的 Bean 进行调用的。
     * 2. 第二，因为异常处理不正确，导致事务虽然生效但出现异常时没回滚。Spring 默认只会对标记 @Transactional 注解的方法出现了
     * RuntimeException 和 Error 的时候回滚，如果我们的方法捕获了异常，那么需要通过手动编码处理事务回滚。
     * 如果希望 Spring 针对其他异常也可以回滚，那么可以相应配置 @Transactional 注解的 rollbackFor 和 noRollbackFor
     * 属性来覆盖其默认设置。
     * 3. 第三，如果方法涉及多次数据库操作，并希望将它们作为独立的事务进行提交或回滚，那么我们需要考虑进一步细化配置事务传播方式，
     * 也就是 @Transactional 注解的 Propagation 属性。
     */

    /**
     * todo 思考题
     * 如果要针对 private 方法启用事务，动态代理方式的 AOP 不可行，需要使用静态织入方式的 AOP，也就是在编译期间织入事务增强代码，
     * 可以配置 Spring 框架使用 AspectJ 来实现 AOP。你能否参阅 Spring 的文档“Using @Transactional with AspectJ”试试呢？
     * 注意：AspectJ 配合 lombok 使用，还可能会踩一些坑。
     */
}
