package com.x.sp.example.spring;

import com.x.sp.example.spring.entity.Animal;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @Author: Yan
 * @date 2019-04-03
 * @Version: 1.0
 */
public class Simple {

    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("Simple.xml");

        Animal animal = (Animal) context.getBean("animal");

        System.out.println(animal);
    }
}
