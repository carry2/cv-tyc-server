/*
 * 投中信息
 * Copyright 2017 (C) All Rights Reserved.
 */
package cn.com.chinaventure.cv.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.*;

public class DbUtil {
	private static final Logger log = LoggerFactory.getLogger(DbUtil.class);

	private static final String DRIVER_NAME = "org.apache.hive.jdbc.HiveDriver";
	private static String JDBC_URL = "jdbc:hive2://39.106.211.132:10000";
	private String DB_NAME = "cvs2_compare";
	private static String USER_NAME = "admin";
	private static final String USER_PWD = "admin";

	static {
		try {
			Class.forName(DRIVER_NAME);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public DbUtil(){}

	public DbUtil(String dbName){
		this.DB_NAME = dbName;
	}

	public Connection getconn() {
		Connection conn = null;
		try {
			log.info("使用默认数据库 <{}>", DB_NAME);
			conn = DriverManager.getConnection(JDBC_URL.concat("/").concat(DB_NAME), USER_NAME, USER_PWD);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return conn;
	}

	public void closeConn(Connection conn) {
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public List<Map<Object, Object>> query(String sql){
		Connection conn = getconn();
		PreparedStatement ps;
		List<Map<Object, Object>> result = null;
		try {
			ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			result = result(rs);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return result;
	}

	private List<Map<Object, Object>> result(ResultSet rs) {
		ResultSetMetaData metaData = null;
		List<Map<Object, Object>> list = new ArrayList<>();
		int columnCount = 0;
		try {
			metaData = rs.getMetaData();

			columnCount = metaData.getColumnCount();
			// 生成列名大小的数组，因为rs的列标从1开始，因此这个列名数组也从1开始用，因此数组长度+1
			String[] cols = new String[columnCount+1];
			String[] split = null;
			// 将列名存放于数组中，判断如果列名中有表的名称就去除掉
			for (int i = 1; i <= columnCount; i++) {
				split = metaData.getColumnLabel(i).split("\\.");
				cols[i] = split[split.length > 1 ? 1 : 0];
			}

			// 将结果存放于List的Map中
			Map<Object, Object> map = null;
			while (rs.next()) {
				map = new HashMap<>();
				for (int i = 1; i <= columnCount; i++) {
					map.put(cols[i], rs.getObject(i));
				}
				list.add(map);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return list;
	}

	public static void main(String[] args) {
		DbUtil util = new DbUtil();
		List<Map<Object, Object>> list = util.query("select cv_id from  company_compare_2080318 limit 10");
		System.out.println(list.size()+","+list);
		for (Map<Object, Object> map : list) {
			Iterator<Object> it = map.keySet().iterator();
			while (it.hasNext()) {
				Object key = it.next();
				System.out.print(key +" -> "+ map.get(key) +", ");
			}
			System.out.println();
			System.out.println();
		}
	}

}
