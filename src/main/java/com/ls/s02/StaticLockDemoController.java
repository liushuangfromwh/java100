package com.ls.s02;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.crypto.Data;
import java.util.stream.IntStream;

/**
 * @Description: TODO
 * @author: liushuang26
 * @Date: 2022/4/23 13:18
 */
@RestController
@RequestMapping("StaticLockDemoController")
public class StaticLockDemoController {

    /**
     * <pre>
     *      结果不等于指定的次数
     *
     * </pre>
     *
     * @param count
     * @return
     * @author liushuang26
     */
    @GetMapping("wrong")
    public int wrong(@RequestParam(value = "count", defaultValue = "1000000") int count) {
        StaticLockDemo.reset();
        //多线程循环一定次数调用Data类不同实例的wrong方法
        //这里多个不同的实例
        IntStream.rangeClosed(1, count).parallel().forEach(i -> new StaticLockDemo().wrong());
        return StaticLockDemo.getCounter();
    }
}
