package com.proptiger.qa.util;

import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.JSONValue;

/**
 * User: Himanshu.Verma
 */

public class DBConnectionUtil {
	final static String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	final static String beta_dbhost = PropertiesUtil.getEnvConfigProperty("beta_dbhost");
	// static String beta_dbhost = "beta-db.proptiger-ws.com";
	public static String db_Url = "jdbc:mysql://" + beta_dbhost;

	// Database credentials
	// final static String user = "root";
	// final static String password = "betaroot1234";
	final static String user = PropertiesUtil.getEnvConfigProperty("beta_dbuserName");
	final static String password = PropertiesUtil.getEnvConfigProperty("beta_dbpassword");
	public static Connection conn = null;
	public static Statement stmt = null;
	public static ArrayList<String> selectResponse = null;

	public static void createDBConnection() throws SQLException {

		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(db_Url, user, password);
			stmt = conn.createStatement();
			System.out.println("Connected to database");
		} catch (SQLException se) {
			// Handle errors for JDBC
			se.printStackTrace();
		} catch (Exception e) {
			// Handle errors for Class.forName
			e.printStackTrace();
		}
	}

	public static ArrayList<String> selectLimitedResponsewithFilters(String dbName, String tableName,
			ArrayList<String> selectQuery, LinkedHashMap<Object, Object> whereQuery) throws SQLException {
		selectResponse = new ArrayList<String>();
		String selectQuerySql = null;
		String whereQuerySql = null;
		try {
			if (selectQuery.size() > 0) {
				selectQuerySql = selectQuery.get(0).toString();

				for (int i = 1; i <= selectQuery.size() - 1; i++) {
					selectQuerySql = selectQuerySql + "," + selectQuery.get(i);
				}
			} else {
				selectQuerySql = selectQuery.get(0).toString();
			}

			ArrayList<String> whereQueryKeyList = new ArrayList<String>();
			ArrayList<String> whereQueryValueList = new ArrayList<String>();

			for (Object key : whereQuery.keySet()) {
				whereQueryKeyList.add(key.toString());
				whereQueryValueList.add(whereQuery.get(key).toString());
			}

			if (whereQueryKeyList.size() > 0) {
				whereQuerySql = whereQueryKeyList.get(0).toString() + "=" + whereQueryValueList.get(0);

				for (int i = 1; i <= whereQueryKeyList.size() - 1; i++) {
					whereQuerySql = whereQuerySql + " and " + whereQueryKeyList.get(i).toString() + "="
							+ whereQueryValueList.get(i);
				}
				/*
				 * whereQuerySql = whereQuerySql + " and " +
				 * whereQueryKeyList.get(whereQueryKeyList.size() -
				 * 1).toString() + "=" +
				 * whereQueryValueList.get(whereQueryValueList.size() -
				 * 1).toString();
				 */
			} else {
				whereQuerySql = whereQuerySql + whereQueryKeyList.get(0).toString().toString() + "="
						+ whereQueryValueList.get(0);
			}

			String sql = "select " + selectQuerySql + " from " + dbName + "." + tableName + " where " + whereQuerySql
					+ ";";
			System.out.println(sql);
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				for (int i = 0; i < selectQuery.size(); i++) {
					selectResponse.add(rs.getString(selectQuery.get(i)));
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return selectResponse;
	}

	public static ArrayList<String> selectLimitedResponse(String dbName, String tableName,
			ArrayList<String> selectQuery) throws SQLException {
		selectResponse = new ArrayList<String>();
		String selectQuerySql = null;
		try {
			if (selectQuery.size() > 0) {
				selectQuerySql = selectQuery.get(0).toString();

				for (int i = 1; i <= selectQuery.size() - 1; i++) {
					selectQuerySql = selectQuerySql + "," + selectQuery.get(i);
				}
			} else {
				selectQuerySql = selectQuery.get(0).toString();
			}

			String sql = "select " + selectQuerySql + " from " + dbName + "." + tableName + ";";
			// System.out.println(sql);
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				for (int i = 0; i < selectQuery.size(); i++) {
					selectResponse.add(rs.getString(selectQuery.get(i)));
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return selectResponse;
	}

	public static void deleteQuerywithFilters(String dbName, String tableName,
			LinkedHashMap<String, ArrayList<Object>> whereQuery) throws SQLException {
		String whereQuerySql = "";
		ArrayList<Object> whereQueryList = new ArrayList<Object>();
		try {
			if (whereQuery.size() > 0) {
				for (String key : whereQuery.keySet()) {
					whereQueryList = whereQuery.get(key);
					if (whereQueryList.size() > 0) {
						whereQuerySql = "'" + whereQueryList.get(0).toString() + "'";
						for (int i = 1; i <= whereQueryList.size() - 1; i++) {
							whereQuerySql = whereQuerySql + "," + "'" + whereQueryList.get(i) + "'";
						}
						whereQuerySql = key + " in (" + whereQuerySql + ")";
						String sql = "delete from " + dbName + "." + tableName + " where " + whereQuerySql + ";";
						System.out.println(sql);
						stmt.executeUpdate(sql);
					} else {
						System.out.println("Can not delete as size: " + whereQueryList.size());
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static ResultSet runQuery(String sql) throws SQLException {
		selectResponse = new ArrayList<String>();
		// System.out.println(sql);
		ResultSet rs = stmt.executeQuery(sql);
		return rs;
	}

	public static void closeDBConnection() {
		try {
			if (stmt != null)
				stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			if (conn != null)
				conn.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		System.out.println("DB Connection Closed!");
	}

	public static void main(String args[]) throws Exception {
		createDBConnection();

		/*
		 * LinkedHashMap<String, ArrayList<Object>> whereQuery = new
		 * LinkedHashMap<String, ArrayList<Object>>(); ArrayList<Object>
		 * whereList = new ArrayList<Object>();
		 * whereList.add("9a159672f4f78a74bb62f149ff66bfed");
		 * //whereList.add("3464492"); whereQuery.put("original_hash",
		 * whereList); //whereQuery.put("active", whereList1);
		 * deleteQuerywithFilters("proptiger", "Image", whereQuery); //String
		 * sql=
		 * "delete from proptiger.Image where original_hash ='9a159672f4f78a74bb62f149ff66bfed';"
		 * ;
		 */
		closeDBConnection();
	}

}
