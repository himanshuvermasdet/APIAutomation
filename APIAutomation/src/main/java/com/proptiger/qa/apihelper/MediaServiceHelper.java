package com.proptiger.qa.apihelper;

import java.io.StringWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.JSONValue;

import com.proptiger.qa.api.test.MediaServiceTestClass;
import com.proptiger.qa.util.DBConnectionUtil;

public class MediaServiceHelper {

	String sql = null;
	ResultSet rs = null;

	public static ArrayList<Object> getObjectIdsSQL(String imageTypeId) throws SQLException {

		ArrayList<Object> objectTypeList = new ArrayList<Object>();
		String sql = "select count(object_id) count,object_id from proptiger.Image where imageType_id=" + imageTypeId+";";
		// System.out.println(sql);
		ResultSet rs = DBConnectionUtil.runQuery(sql);
		while (rs.next()) {
			// objectIds.put("object_id", objectTypeList);
			objectTypeList.add(rs.getInt("count"));
			objectTypeList.add(rs.getString("object_id"));
		}
		return objectTypeList;

	}
	
	public static ArrayList<Object> getActiveObjectIdsSQL(String imageTypeId) throws SQLException {

		ArrayList<Object> objectTypeList = new ArrayList<Object>();
		String sql = "select count(object_id) count,object_id from proptiger.Image where active=1 and imageType_id=" + imageTypeId+";";
		//System.out.println(sql);
		ResultSet rs = DBConnectionUtil.runQuery(sql);
		while (rs.next()) {
			// objectIds.put("object_id", objectTypeList);
			objectTypeList.add(rs.getInt("count"));
			objectTypeList.add(rs.getString("object_id"));
		}
		return objectTypeList;

	}
	
	public static ArrayList<Object> getpreReqActiveObjectIdsSQL(String imageTypeId, long objectId) throws SQLException {

		ArrayList<Object> objectTypeList = new ArrayList<Object>();
		String sql = "select count(object_id) count,object_id from proptiger.Image where active=1 and imageType_id=" + imageTypeId+" and object_id="+objectId+";";
		//System.out.println(sql);
		ResultSet rs = DBConnectionUtil.runQuery(sql);
		while (rs.next()) {
			// objectIds.put("object_id", objectTypeList);
			objectTypeList.add(rs.getInt("count"));
			objectTypeList.add(rs.getString("object_id"));
		}
		return objectTypeList;

	}

	public static LinkedHashMap<String, ArrayList<String>> getImageIDsSQL(String objectType, int domainId,
			LinkedHashMap<String, ArrayList<String>> imageID) throws SQLException {

		LinkedHashMap<Object, Object> whereQuery = new LinkedHashMap<Object, Object>();
		ArrayList<String> imageIds = new ArrayList<String>();
		whereQuery.put("domainId", domainId);
		String sql = "select a.id from proptiger.ImageType a join proptiger.ObjectType b on (a.ObjectType_id=b.id) where a.domainId="
				+ whereQuery.get("domainId") + " and b.type='" + objectType + "' group by (a.type);";
		ResultSet rs = DBConnectionUtil.runQuery(sql);
		while (rs.next()) {
			// System.out.println("rs.getString(id) "+rs.getString("id"));
			imageIds.add(rs.getString("id"));
		}
		imageID.put(objectType, imageIds);
		// System.out.println("imageID "+imageID);
		return imageID;

	}
	
	public static int getImageActiveStatusSQL(String imageTypeId) throws SQLException {

		String sql = "select active from proptiger.Image where id=" + imageTypeId;
		int activeStatus=0;
		// System.out.println(sql);
		ResultSet rs = DBConnectionUtil.runQuery(sql);
		while (rs.next()) {
			// objectIds.put("object_id", objectTypeList);
			activeStatus=rs.getInt("active");
		}
		return activeStatus;

	}

	public static LinkedHashMap<String, ArrayList<String>> getImageTypesSQL(String imageTypeId) throws SQLException {

		LinkedHashMap<String, ArrayList<String>> imageType = new LinkedHashMap<String, ArrayList<String>>();
		ArrayList<String> imageTypes = new ArrayList<String>();
		ArrayList<String> imageTypesDisplayNames = new ArrayList<String>();

		String sql = "select a.type,a.display_name from proptiger.ImageType a where a.id=" + imageTypeId + ";";
		ResultSet rs = DBConnectionUtil.runQuery(sql);
		// System.out.println(sql);
		while (rs.next()) {
			imageTypes.add(rs.getString("type"));
			imageTypesDisplayNames.add(rs.getString("display_name"));
		}
		imageType.put("type", imageTypes);
		imageType.put("display_name", imageTypesDisplayNames);

		return imageType;

	}

	public static LinkedHashMap<String, String> replaceResponseBody(LinkedHashMap<Object, Object> whereQuery,
			HashMap<Object, Object> testSpecificDataMap, String mediaType) throws SQLException {
		String objectTypeId = null;
		String mediaTypeId = null;
		String mediaDuplicacyRuleId = null;
		ArrayList<String> selectQuery = new ArrayList<String>();
		ArrayList<String> selectResponse = new ArrayList<String>();
		selectQuery.add("ImageType_id");
		selectQuery.add("object_id");
		selectQuery.add("path");
		selectQuery.add("page_url");
		selectQuery.add("status_id");
		selectQuery.add("size_in_bytes");
		selectQuery.add("width");
		selectQuery.add("height");
		selectQuery.add("watermark_name");
		selectQuery.add("seo_name");
		selectQuery.add("alt_text");
		selectQuery.add("latitude");
		selectQuery.add("longitude");
		selectQuery.add("description");
		selectQuery.add("priority");
		
		selectResponse = DBConnectionUtil.selectLimitedResponsewithFilters("proptiger", "Image", selectQuery,
				whereQuery);
		// System.out.println(selectQuery); 
		LinkedHashMap<String, String> replaceResponseBody = new LinkedHashMap<String, String>();
		replaceResponseBody.put("id", whereQuery.get("id").toString());
		replaceResponseBody.put("imageTypeId", selectResponse.get(0));
		replaceResponseBody.put("objectId", selectResponse.get(1));
		replaceResponseBody.put("path", selectResponse.get(2));
		replaceResponseBody.put("pageUrl", selectResponse.get(3));
		replaceResponseBody.put("statusId", selectResponse.get(4));
		replaceResponseBody.put("sizeInBytes", selectResponse.get(5));
		replaceResponseBody.put("width", selectResponse.get(6));
		replaceResponseBody.put("height", selectResponse.get(7));
		replaceResponseBody.put("waterMarkName", selectResponse.get(8));
		replaceResponseBody.put("seoName", selectResponse.get(9));
		replaceResponseBody.put("altText", selectResponse.get(10));
		replaceResponseBody.put("latitude", selectResponse.get(11));
		replaceResponseBody.put("longitude", selectResponse.get(12));
		replaceResponseBody.put("description", selectResponse.get(13));
		replaceResponseBody.put("priority", selectResponse.get(14));
		replaceResponseBody.put("domainId", String.valueOf(MediaServiceTestClass.domainId));

		replaceResponseBody.put("absolutePath", MediaServiceTestClass.absolutePath + selectResponse.get(2).toString()
				+ selectResponse.get(9).toString());
		replaceResponseBody.put("IdimageType", selectResponse.get(0).toString());

		String objectType = "select id from proptiger.ObjectType where type='"
				+ testSpecificDataMap.get("objectType").toString() + "';";
		ResultSet objectTypers = DBConnectionUtil.runQuery(objectType);
		while (objectTypers.next()) {
			objectTypeId = objectTypers.getString("id");
		}
		replaceResponseBody.put("idObjectType", objectTypeId);
		replaceResponseBody.put("objectTypeId", objectTypeId);

		replaceResponseBody.put("objectType", testSpecificDataMap.get("objectType").toString());

		String mediaId = "select id from proptiger.media_types where name='" + mediaType + "';";
		ResultSet mediaIdrs = DBConnectionUtil.runQuery(mediaId);
		while (mediaIdrs.next()) {
			mediaTypeId = mediaIdrs.getString("id");
		}
		replaceResponseBody.put("idMediaType", mediaTypeId);
		replaceResponseBody.put("mediaTypeId", mediaTypeId);

		replaceResponseBody.put("imageType", testSpecificDataMap.get("imageType").toString());
		replaceResponseBody.put("ImageTypedisplayName", testSpecificDataMap.get("imageTypeDisplayName").toString());

		String imageTypeDisplayName = "select display_name,priority from proptiger.ImageType where id="
				+ selectResponse.get(0).toString();
		ResultSet imageTypeDisplayNamers = DBConnectionUtil.runQuery(imageTypeDisplayName);
		while (imageTypeDisplayNamers.next()) {
			replaceResponseBody.put("ImageTypedisplayName", imageTypeDisplayNamers.getString("display_name"));
			replaceResponseBody.put("ImageTypePriority", imageTypeDisplayNamers.getString("priority"));
		}

		String objectMediaTypes = "select media_duplicacy_rule_id,image_sitemap_enabled from proptiger.object_media_types where media_type_id="
				+ mediaTypeId + " and ObjectType_id=" + objectTypeId + " and type='"
				+ testSpecificDataMap.get("imageType").toString() + "' and domainId=" + MediaServiceTestClass.domainId
				+ ";";
		ResultSet objectMediaTypesrs = DBConnectionUtil.runQuery(objectMediaTypes);
		while (objectMediaTypesrs.next()) {
			mediaDuplicacyRuleId = objectMediaTypesrs.getString("media_duplicacy_rule_id");
			replaceResponseBody.put("imageSitemapEnabled", objectMediaTypesrs.getString("image_sitemap_enabled"));
		}
		replaceResponseBody.put("idMediaDuplicacyRule", mediaDuplicacyRuleId);

		String mediaDuplicacyRule = "select rule from proptiger.media_duplicacy_rule where id=" + mediaDuplicacyRuleId;
		ResultSet mediaDuplicacyRulers = DBConnectionUtil.runQuery(mediaDuplicacyRule);
		while (mediaDuplicacyRulers.next()) {
			replaceResponseBody.put("duplicacyRule", mediaDuplicacyRulers.getString("rule"));
		}

		return replaceResponseBody;

	}

	public static JSONArray convertResultSetIntoJSON(ResultSet resultSet) throws Exception {
		LinkedHashMap<Object, Object> map = new LinkedHashMap<Object, Object>();
		;
		JSONArray jsonArray = new JSONArray();
		while (resultSet.next()) {
			int total_rows = resultSet.getMetaData().getColumnCount();
			JSONObject obj = new JSONObject();

			for (int i = 0; i < total_rows; i++) {
				String columnName = resultSet.getMetaData().getColumnLabel(i + 1).toLowerCase();
				Object columnValue = resultSet.getObject(i + 1);
				// if value in DB is null, then we set it to default value
				if (columnValue == null) {
					columnValue = "null";
				}
				if (columnName.equalsIgnoreCase("resolution")) {
					columnName = "label";
				}

				if (obj.has(columnName)) {
					columnName += "1";
				}
				// obj.put(columnName, columnValue);
				map.put(columnName, columnValue);
			}
			// jsonArray.put(map);
			StringWriter out = new StringWriter();
			JSONValue.writeJSONString(map, out);
			String jsonText = out.toString();

			jsonArray.put(jsonText);

		}
		return jsonArray;
	}
	/*
	 * public static void main(String args[]) throws SQLException{
	 * LinkedHashMap<String, ArrayList<String>> objectTypeSql= new
	 * LinkedHashMap<String, ArrayList<String>>(); ArrayList<String>
	 * objectTypeSqlValue= new ArrayList<String>();
	 * objectTypeSqlValue.add("state"); objectTypeSqlValue.add("city");
	 * objectTypeSql.put("objectType", objectTypeSqlValue);
	 * DBConnectionUtil.createDBConnection();
	 * System.out.println(getObjectIdsSQL("519"));
	 * DBConnectionUtil.closeDBConnection();
	 * 
	 * }
	 */
}
