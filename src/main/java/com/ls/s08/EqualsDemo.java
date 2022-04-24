package com.ls.s08;

import lombok.extern.slf4j.Slf4j;

/**
 * @Description: TODO
 * @author: liushuang26
 * @Date: 2022/4/24 17:04
 */
@Slf4j
public class EqualsDemo {

    public static void main(String[] args) {

        Integer a = 127; //Integer.valueOf(127)
        Integer b = 127; //Integer.valueOf(127)
        log.info("\nInteger a = 127;\n" +
                "Integer b = 127;\n" +
                "a == b ? {}",a == b);    // true

        Integer c = 128; //Integer.valueOf(128)
        Integer d = 128; //Integer.valueOf(128)
        log.info("\nInteger c = 128;\n" +
                "Integer d = 128;\n" +
                "c == d ? {}", c == d);   //false

        Integer e = 127; //Integer.valueOf(127)
        Integer f = new Integer(127); //new instance
        log.info("\nInteger e = 127;\n" +
                "Integer f = new Integer(127);\n" +
                "e == f ? {}", e == f);   //false

        Integer g = new Integer(127); //new instance
        Integer h = new Integer(127); //new instance
        log.info("\nInteger g = new Integer(127);\n" +
                "Integer h = new Integer(127);\n" +
                "g == h ? {}", g == h);  //false

        Integer i = 128; //unbox 拆箱
        int j = 128;
        log.info("\nInteger i = 128;\n" +
                "int j = 128;\n" +
                "i == j ? {}", i == j); //true


    }

    /**
     * Java 的字符串常量池机制。首先要明确的是其设计初衷是节省内存。当代码中出现双引号形式创建字符串对象时，
     * JVM 会先对这个字符串进行检查，如果字符串常量池中存在相同内容的字符串对象的引用，则将这个引用返回；
     * 否则，创建新的字符串对象，然后将这个引用放入字符串常量池，并返回该引用。
     * 这种机制，就是字符串驻留或池化。
     */
    public void StringRun() {

        String a = "1";
        String b = "1";
        log.info("\nString a = \"1\";\n" +
                "String b = \"1\";\n" +
                "a == b ? {}", a == b); //true

        String c = new String("2");
        String d = new String("2");
        log.info("\nString c = new String(\"2\");\n" +
                "String d = new String(\"2\");" +
                "c == d ? {}", c == d); //false

        //intern会走常量池
        String e = new String("3").intern();
        String f = new String("3").intern();
        log.info("\nString e = new String(\"3\").intern();\n" +
                "String f = new String(\"3\").intern();\n" +
                "e == f ? {}", e == f); //true

        String g = new String("4");
        String h = new String("4");
        log.info("\nString g = new String(\"4\");\n" +
                "String h = new String(\"4\");\n" +
                "g == h ? {}", g.equals(h)); //true
    }
}
