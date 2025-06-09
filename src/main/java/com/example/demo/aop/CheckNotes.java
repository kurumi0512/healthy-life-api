package com.example.demo.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD) // 可用在方法上
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckNotes {
}