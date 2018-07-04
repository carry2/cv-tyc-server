/*
 * 投中信息
 * Copyright 2018 (C) All Rights Reserved.
 */
package cn.com.chinaventure.cv.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by zhangshenglong on 2018年4月12日 下午6:00:40.
 * 
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface RouteBind {

	/**对应的路径名 已/开头*/
//	String path() default "";
//	/**视图所在目录*/
//	String viewPath() default "";

	String value();
}
