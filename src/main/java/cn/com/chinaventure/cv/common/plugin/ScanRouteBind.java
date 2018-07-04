/*
 * 投中信息
 * Copyright 2018 (C) All Rights Reserved.
 */
package cn.com.chinaventure.cv.common.plugin;

import java.io.File;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jfinal.config.Routes;

import cn.com.chinaventure.cv.common.annotation.RouteBind;

/**
 * Created by zhangshenglong on 2018年4月12日 下午6:02:20.
 * 
 */
public class ScanRouteBind {

	private static Logger log = LoggerFactory.getLogger(ScanRouteBind.class);

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void scanRoute(Routes me) throws Exception{
		List<Class> list= ClassSearcher.findClasses();
		if(list!=null&&list.isEmpty()==false){
			for(Class clz:list){
				RouteBind rb= (RouteBind) clz.getAnnotation(RouteBind.class);
				if(rb!=null){
					if("".equals(rb.value())){
						String clzDir=(new File(clz.getResource("").getPath())).getName();//存放@RouteBind的目录
						log.error(clzDir +" Directory path of the @RouteBind can't be empty, can be set to @RouteBind(path = \""+clzDir+"\")");
					}
					String route = rb.value() ;
					log.debug("Increase the request address: "+route);
					me.add(route, clz);
//					me.add(route, clz, rb.viewPath());
				}
			}
		}
	}
}
