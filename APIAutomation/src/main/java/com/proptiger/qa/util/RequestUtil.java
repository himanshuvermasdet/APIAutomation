package com.proptiger.qa.util;

import java.util.HashMap;

import com.proptiger.qa.api.dto.RequestDTO;

/**
 * User: Himanshu.Verma
 */
public class RequestUtil {

	/**
	 * This function is used to convert the excel data to the RequestDto object
	 * @param excelData : excel data 
	 * @param requestDTO : RequestDto object
	 * @return requestDto object with all the values set
	 */
	public static RequestDTO createRequestDTO(Object[][] excelData,RequestDTO requestDTO ){

//		requestDTO.setApiPath(excelData[0][3]);
//		requestDTO.setMethodType(excelData[0][4]);
//		requestDTO.setHeaderJson(excelData[0][5]);
//		requestDTO.setQueryParamJson(excelData[0][6]);
//		requestDTO.setPathParamJson(excelData[0][7]);
//		requestDTO.setRequestBody(excelData[0][8]);
//		requestDTO.setResponseCode(Integer.parseInt(excelData[0][9]));
//		requestDTO.setExpectedResponseJson(excelData[0][10]);
//		requestDTO.setExpectedDB(excelData[0][11]);
		
		HashMap<String, String> inputMap = new HashMap(((HashMap<String, String>)excelData[0][0]));
		requestDTO.setApiPath(inputMap.remove("apipath"));
		requestDTO.setMethodType(inputMap.remove("method"));
		requestDTO.setHeaderJson(inputMap.remove("headers"));
		requestDTO.setQueryParamJson(inputMap.remove("query params"));
		requestDTO.setPathParamJson(inputMap.remove("path params"));
		requestDTO.setRequestBody(inputMap.remove("request body"));
		requestDTO.setResponseCode(Integer.parseInt(inputMap.remove("status")));
		requestDTO.setExpectedResponseJson(inputMap.remove("expected response"));
		requestDTO.setExpectedDB(inputMap.remove("expectedDB"));

		return requestDTO;
	}

}
