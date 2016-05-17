package com.proptiger.qa.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.proptiger.qa.listener.TestListener;

/**
 * User: Himanshu.Verma
 */
public class JsonUtil {


	/**
	 * This Function is used to return the value from the api response depending on the map provided 
	 * @param apiResponse : Api response from which the values needs to be fetched
	 * @param valueToGetFromResponseMap : This map contains the value which we need to fetch from the response with Key as the element name from the response and value as its index
	 * @throws Exception 
	 */

	public static boolean returnValueFromResponse(String apiResponse,Map<String, Object> valueToGetFromResponseMap) throws Exception{

		List<JsonNode> nameNode = null;
		try {

			ObjectMapper mapper = new ObjectMapper();
			JsonNode rootNode = mapper.readTree(apiResponse);

			for (String keys : valueToGetFromResponseMap.keySet()) {
				int index = 0;
				nameNode = rootNode.findParents(keys);
				if (nameNode.size() > 0){
					if (valueToGetFromResponseMap.get(keys) != null) {
						if (!valueToGetFromResponseMap.get(keys).equals("")){
							index = (int)valueToGetFromResponseMap.get(keys);
						}
					}
					JsonNode val = nameNode.get(index).get(keys);
					if(val.getClass().getSimpleName().equals("TextNode")) 
						valueToGetFromResponseMap.put(keys, (Object)val.textValue());
					else 
						valueToGetFromResponseMap.put(keys, (Object)val);
				}
			}

			return true;
		} catch (JsonProcessingException e) {
			TestListener.setException(e.getMessage(),e.getStackTrace());
			throw e;
			//			return false;
		} catch (IOException e) {
			TestListener.setException(e.getMessage(),e.getStackTrace());
			throw e;
			//			return false;
		}
	}
	
}
