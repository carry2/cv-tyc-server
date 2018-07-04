/*
 * 投中信息
 * Copyright 2018 (C) All Rights Reserved.
 */
package cn.com.chinaventure.cv.common;

import cn.com.chinaventure.cv.common.interceptor.ExceptionInterceptor;
import cn.com.chinaventure.cv.common.model._MappingKit;
import com.feizhou.swagger.config.routes.SwaggerRoutes;
import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.tx.TxByMethodRegex;
import com.jfinal.plugin.activerecord.tx.TxByMethods;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.template.Engine;

import cn.com.chinaventure.cv.common.log.Slf4jLogFactory;
import cn.com.chinaventure.cv.common.plugin.ScanRouteBind;
import com.jfplugin.mail.MailPlugin;

/**
 * Created by zhangshenglong on 2018年4月12日 下午5:55:57.
 * 
 */
public class ServerConfig extends JFinalConfig {
	
	/**
	 * 配置常量
	 */
	public void configConstant(Constants me) {
		me.setLogFactory(new Slf4jLogFactory());
		// 加载少量必要配置，随后可用PropKit.get(...)获取值
		PropKit.use("config.properties");
		me.setDevMode(PropKit.getBoolean("devMode", false));
		
	}

	/**
	 * 配置路由
	 */
	public void configRoute(Routes me) {
//		me.add("/", IndexController.class, "/index"); // 第三个参数为该Controller的视图存放路径
		try {
			ScanRouteBind.scanRoute(me);
			me.add(new SwaggerRoutes());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	public void configEngine(Engine me) {
		// me.addSharedFunction("/common/_layout.html");
		// me.addSharedFunction("/common/_paginate.html");
	}

	/**
	 * 配置插件
	 */
	public void configPlugin(Plugins me) {


		//配置邮件
		me.add(new MailPlugin(PropKit.use("mail.properties").getProperties()));
		// 配置 druid 数据库连接池插件
		DruidPlugin druidPlugin = new DruidPlugin(PropKit.get("db1_prism1jdbcUrl"), PropKit.get("user"),
				PropKit.get("password").trim());
		me.add(druidPlugin);

		// 配置ActiveRecord插件
		ActiveRecordPlugin arp = new ActiveRecordPlugin("db1_prism1",druidPlugin);
		// 所有映射在 MappingKit 中自动化搞定
		_MappingKit.mapping(arp);
		me.add(arp);

		// 配置 druid 数据库连接池插件
		DruidPlugin druidPlugin2= new DruidPlugin(PropKit.get("db2_prism1jdbcUrl"), PropKit.get("user"),
				PropKit.get("password").trim());
		me.add(druidPlugin2);

		// 配置ActiveRecord插件
		ActiveRecordPlugin arp2 = new ActiveRecordPlugin("db2_prism1",druidPlugin2);
		// 所有映射在 MappingKit 中自动化搞定
		_MappingKit.mapping(arp2);
		me.add(arp2);

		// 配置 druid 数据库连接池插件
		DruidPlugin druidPlugin3= new DruidPlugin(PropKit.get("db4_prism1jdbcUrl"), PropKit.get("user"),
				PropKit.get("password").trim());
		me.add(druidPlugin3);

		// 配置ActiveRecord插件
		ActiveRecordPlugin arp3 = new ActiveRecordPlugin("db4_prism1",druidPlugin3);
		// 所有映射在 MappingKit 中自动化搞定
		_MappingKit.mapping(arp3);
		me.add(arp3);


		// 配置 druid 数据库连接池插件
		DruidPlugin druidPlugin4= new DruidPlugin(PropKit.get("db3_tmdatabasejdbcUrl"), PropKit.get("user"),
				PropKit.get("password").trim());
		me.add(druidPlugin4);

		// 配置ActiveRecord插件
		ActiveRecordPlugin arp4 = new ActiveRecordPlugin("db3_tmdatabase",druidPlugin4);
		// 所有映射在 MappingKit 中自动化搞定
		_MappingKit.mapping(arp4);
		me.add(arp4);

		// 配置 druid 数据库连接池插件
		DruidPlugin druidPlugin5= new DruidPlugin(PropKit.get("db3_patentjdbcUrl"), PropKit.get("user"),
				PropKit.get("password").trim());
		me.add(druidPlugin5);

		// 配置ActiveRecord插件
		ActiveRecordPlugin arp5 = new ActiveRecordPlugin("db3_patent",druidPlugin5);
		// 所有映射在 MappingKit 中自动化搞定
		_MappingKit.mapping(arp5);
		me.add(arp5);

		// 配置 druid 数据库连接池插件
		DruidPlugin druidPlugin6= new DruidPlugin(PropKit.get("db3_prism_cqjdbcUrl"), PropKit.get("user"),
				PropKit.get("password").trim());
		me.add(druidPlugin6);

		// 配置ActiveRecord插件
		ActiveRecordPlugin arp6 = new ActiveRecordPlugin("db3_prism_cq",druidPlugin6);
		// 所有映射在 MappingKit 中自动化搞定
		_MappingKit.mapping(arp6);
		me.add(arp6);

		// 配置 druid 数据库连接池插件
		DruidPlugin druidPlugin7= new DruidPlugin(PropKit.get("db3_prism1jdbcUrl"), PropKit.get("user"),
				PropKit.get("password").trim());
		me.add(druidPlugin7);

		// 配置ActiveRecord插件
		ActiveRecordPlugin arp7 = new ActiveRecordPlugin("db3_prism1",druidPlugin7);
		// 所有映射在 MappingKit 中自动化搞定
		_MappingKit.mapping(arp7);
		me.add(arp7);

	}

	public static DruidPlugin createDruidPlugin() {
		//new DruidPlugin(PropKit.get("jdbcUrl"), PropKit.get("user"), PropKit.get("password").trim())
		return null;
	}

	/**
	 * 配置全局拦截器
	 */
	public void configInterceptor(Interceptors me) {
		me.add(new TxByMethodRegex("(.save*.*|.update*.*)"));
		me.add(new TxByMethods("save", "update"));
		me.add(new ExceptionInterceptor());
	}

	/**
	 * 配置处理器
	 */
	public void configHandler(Handlers me) {

	}
	
}
