package com.ls.s06;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @Description: TODO
 * @author: liushuang26
 * @Date: 2022/4/24 9:26
 */
@Data
@TableName("user")
public class User {

    private Long id;
    private String name;
    private Integer age;
    private String email;

    public User() { }

    public User(String name) {
        this.name = name;
    }
}
