package com.boot.bootmybatisnginxinsuranceuser;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.service","com.controller"})
@MapperScan(basePackages = {"com.mapper"})
public class BootmybatisNginxInsuranceUserApplication {

    public static void main(String[] args) {
        SpringApplication.run(BootmybatisNginxInsuranceUserApplication.class, args);
    }

}
