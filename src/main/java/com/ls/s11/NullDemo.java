package com.ls.s11;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description: TODO
 * @author: liushuang26
 * @Date: 2022/5/6 18:24
 */
@Slf4j
@RestController("NullDemo")
public class NullDemo {



    /**
     * <pre>
     *      对入参 Integer i 进行 +1 操作
     *      对入参 String s 进行比较操作，判断内容是否等于"OK"；
     *      对入参 String s 和入参 String t 进行比较操作，判断两者是否相等；
     *      对 new 出来的 ConcurrentHashMap 进行 put 操作，Key 和 Value 都设置为 null。
     * </pre>
     *
     * @param fooService
     * @param i
     * @param s
     * @param t
     * @return
     * @author liushuang26
     */
    private List<String> wrongMethod(FooService fooService, Integer i, String s, String t) {
        //不能具体指导是哪个地方npe了,
        log.info("result {} {} {} {}", i + 1, s.equals("OK"), s.equals(t),
                new ConcurrentHashMap<String, String>().put(null, null));
        if (fooService.getBarService().bar().equals("OK"))
            log.info("OK");
        return null;
    }

    /**
     * <pre>
     *      wrong 方法的入参 test 是一个由 0 和 1 构成的、长度为 4 的字符串，第几位设置为 1 就代表第几个参数为 null，
     *      用来控制 wrongMethod 方法的 4 个入参，以模拟各种空指针情况：
     * </pre>
     *
     * @param test
     * @return
     * @author liushuang26
     */
    @GetMapping("wrong")
    public int wrong(@RequestParam(value = "test", defaultValue = "1111") String test) {
        return wrongMethod(test.charAt(0) == '1' ? null : new FooService(),
                test.charAt(1) == '1' ? null : 1,
                test.charAt(2) == '1' ? null : "OK",
                test.charAt(3) == '1' ? null : "OK").size();
    }

    class FooService {
        @Getter
        private BarService barService;

    }

    class BarService {
        String bar() {
            return "OK";
        }
    }
}
