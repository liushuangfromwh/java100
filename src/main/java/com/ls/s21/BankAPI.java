package com.ls.s21;

import java.lang.annotation.*;

/**
 * @Description: TODO
 * @author: liushuang26
 * @Date: 2022/5/5 16:33
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Inherited
public @interface BankAPI {
    String desc() default "";
    String url() default "";
}
