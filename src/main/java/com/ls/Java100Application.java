package com.ls;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


/**
 * @author ziya
 * @date 2021/3/2 12:18 上午
 */
@SpringBootApplication
@MapperScan("com.ls.mapper")
public class Java100Application {
    public static void main(String[] args) {
        SpringApplication.run(Java100Application.class);
    }
}
