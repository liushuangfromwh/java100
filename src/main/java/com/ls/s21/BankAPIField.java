package com.ls.s21;

import java.lang.annotation.*;

/**
 * @Description: TODO
 * @author: liushuang26
 * @Date: 2022/5/5 16:33
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
@Inherited
public @interface BankAPIField {
    int order() default -1;
    int length() default -1;
    String type() default "";
}
