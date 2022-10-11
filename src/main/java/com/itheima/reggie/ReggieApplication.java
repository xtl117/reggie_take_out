package com.itheima.reggie;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author XTL117
 * @version 1.0
 */
@ServletComponentScan
@SpringBootApplication
@EnableTransactionManagement    //开启事务
@Slf4j
public class ReggieApplication {
    public static void main(String[] args) {
        SpringApplication.run(ReggieApplication.class,args);
        log.info("项目启动成功");
        System.out.println("修改了一次代码");
        System.out.println("修改第二次代码");

    }
}
