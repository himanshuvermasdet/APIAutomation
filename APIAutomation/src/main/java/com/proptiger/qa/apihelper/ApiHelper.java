package com.proptiger.qa.apihelper;

import java.io.File;

/**
 * User: Himanshu.Verma
 */

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpMessage;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.testng.Assert;

import com.proptiger.qa.api.dto.RequestDTO;
import com.proptiger.qa.api.test.MediaServiceTestClass;
import com.proptiger.qa.apibase.APITestBase;
import com.proptiger.qa.listener.TestListener;
import com.proptiger.qa.util.DataProviderUtil;
import com.proptiger.qa.util.JavaUtil;
import com.proptiger.qa.util.JsonUtil;
import com.proptiger.qa.util.LoggerUtil;
import com.proptiger.qa.util.PropertiesUtil;
import com.proptiger.qa.util.RequestUtil;
import com.proptiger.qa.util.ResponseUtil;
import com.proptiger.qa.util.DataProviderUtil.mapKeys;
import com.proptiger.qa.util.LoggerUtil.LogLevel;

public class ApiHelper {

	public static final String CONTENT_TYPE = HTTP.CONTENT_TYPE;
	public static final String ACCEPT = "Accept";

	public enum replaceWhere {
		RequestBody, RequestHeader, RequestPath, QueryParams, PathParams
	};

	/**
	 * This function is used to get the response of the api, which we get
	 * through curl hit
	 * 
	 *
	 *
	 * @param baseUrl
	 *            : baseUrl for http request for ex. http://www.nearbuy.com/
	 * @param requestDTO
	 *            : requestDTO containing request information
	 * 
	 * @param uploadFlag
	 *            :if the API is uploading a file or not
	 * @return : it returns HttpResponse containing status code & response data
	 */
	public HttpRequestBase prepareHTTPRequest(RequestDTO requestDTO, String baseUrl, boolean uploadFlag)
			throws Exception {
		try {
			if (requestDTO != null) {
				JSONParser parser = new JSONParser();
				// set path params
				assignPathParams(requestDTO, parser);

				// set url endpoint
				URIBuilder uriBuilder = new URIBuilder(baseUrl + requestDTO.getApiPath());

				// set query params
				assignQueryParams(uriBuilder, requestDTO.getQueryParamJson(), parser);
				HttpRequestBase httpRequest = prepareHttpRequest(requestDTO.getMethodType());
				httpRequest.setURI(uriBuilder.build());
				if (uploadFlag) {
					addRequestBodyMultiPart(httpRequest, requestDTO.getRequestBody(), APITestBase.uploadList, parser);
				} else {
					assignHeaders(httpRequest, requestDTO.getHeaderJson(), parser);
					addRequestBody(httpRequest, requestDTO.getRequestBody());
				}
				// execute request
				return httpRequest;
			}
		} catch (Exception e) {
			TestListener.setException(e.getMessage(), e.getStackTrace());
			e.printStackTrace();
			throw e;
		}
		throw new IllegalArgumentException("RequestDTO(" + requestDTO + ") cannot be empty");
	}

	private void addRequestBody(HttpRequestBase httpRequest, String requestBody) throws UnsupportedEncodingException {
		if (StringUtils.isNotBlank(requestBody) && httpRequest instanceof HttpEntityEnclosingRequest) {
			final StringEntity entity = new StringEntity(requestBody);
			// System.out.println("requestBody "+requestBody);
			// System.out.println("CONTENT_TYPE
			// "+httpRequest.getFirstHeader(CONTENT_TYPE));
			entity.setContentType(httpRequest.getFirstHeader(CONTENT_TYPE));
			((HttpEntityEnclosingRequest) httpRequest).setEntity(entity);
			((HttpEntityEnclosingRequest) httpRequest).setHeader("Content-type", "application/json");
		} else {
			LoggerUtil.setlog(LogLevel.INFO, "Could not set requestBody in this request");
		}
	}

	private void addRequestBodyMultiPart(HttpRequestBase httpRequest, String requestBody, ArrayList<Object> uploadItems,
			JSONParser parser) throws ParseException, IOException {
		if (StringUtils.isNotBlank(requestBody) && httpRequest instanceof HttpEntityEnclosingRequest) {
			MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
			entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
			// System.out.println("requestBody "+requestBody);

			if (StringUtils.isNotBlank(requestBody)) {
				final JSONObject requestBodyMap = (JSONObject) parser.parse(requestBody);
				for (Object k : requestBodyMap.keySet()) {
					final String key = (String) k;
					String value = "";
					if (requestBodyMap.get(key) != null) {
						if (uploadItems.contains(key)) {
							File file = new File(requestBodyMap.get(key).toString());
							// System.out.println("key "+key+" "+"file
							// "+file.getAbsolutePath());
							entityBuilder.addBinaryBody(key, file);
						} else {
							value = (String) requestBodyMap.get(key).toString();
							// System.out.println("key "+key+" "+"value
							// "+value);
							entityBuilder.addTextBody(key, value);
						}
					} else {
						value = (String) requestBodyMap.get(key);
						entityBuilder.addTextBody(key, value);
					}

				}
				HttpEntity entity = entityBuilder.build();
				((HttpEntityEnclosingRequest) httpRequest).setEntity(entity);
				// ((HttpEntityEnclosingRequest)
				// httpRequest).setHeader("Content-type",
				// "multipart/form-data");

			} else {
				LoggerUtil.setlog(LogLevel.INFO, "Could not set requestBody in this request");
			}
		}
	}

	private void assignHeaders(HttpRequestBase httpRequest, String headerJson, JSONParser parser)
			throws ParseException {
		// default headers
		httpRequest.addHeader(CONTENT_TYPE, "application/json");
		if (HttpPost.METHOD_NAME.equalsIgnoreCase(httpRequest.getMethod())
				|| HttpPut.METHOD_NAME.equalsIgnoreCase(httpRequest.getMethod())) {
			httpRequest.addHeader(ACCEPT, "application/json");
		}

		// custom header
		if (StringUtils.isNotBlank(headerJson)) {
			final JSONObject headerMap = (JSONObject) parser.parse(headerJson);
			for (Object k : headerMap.keySet()) {
				final String key = (String) k;
				final String value = (String) headerMap.get(key).toString();
				if (StringUtils.isNotEmpty(value)) {
					httpRequest.setHeader(key, value);
				}
			}
		}
	}

	private HttpRequestBase prepareHttpRequest(String methodType) {
		if (HttpPost.METHOD_NAME.equalsIgnoreCase(methodType)) {
			return new HttpPost();
		} else if (HttpPut.METHOD_NAME.equalsIgnoreCase(methodType)) {
			return new HttpPut();
		} else if (HttpDelete.METHOD_NAME.equalsIgnoreCase(methodType)) {
			return new HttpDelete();
		} else {
			return new HttpGet();
		}
	}

	private void assignQueryParams(URIBuilder uriBuilder, String queryParamJson, JSONParser parser)
			throws ParseException {
		if (StringUtils.isNotBlank(queryParamJson)) {
			final JSONObject queryParamMap = (JSONObject) parser.parse(queryParamJson);
			for (Object k : queryParamMap.keySet()) {
				final String key = (String) k;
				String value = "";
				if (queryParamMap.get(key) != null) {
					value = (String) queryParamMap.get(key).toString();
				} else
					value = (String) queryParamMap.get(key);
				// if(StringUtils.isNotEmpty(value)) {
				// System.out.println("QP KEY "+key);
				// System.out.println("QP value "+value);
				uriBuilder.addParameter(key, value);
				// System.out.println("uriBuilder "+uriBuilder);
				// }
			}
		}
	}

	// private void assignRequestParams(URIBuilder uriBuilder, String
	// requestParamKeyJson, String requestParamValueJson, JSONParser parser)
	// throws ParseException {
	// if(StringUtils.isNotBlank(requestParamKeyJson) &&
	// StringUtils.isNotBlank(requestParamValueJson)) {
	//
	// //final JSONObject requestParamKeyMap = (JSONObject)
	// parser.parse(requestParamKeyJson);
	// //final JSONObject requestParamValueMap = (JSONObject)
	// parser.parse(requestParamValueJson);
	// //for (Object k : requestParamValueMap.keySet()) {
	// String key = (String) requestParamKeyJson;
	// String value= (String)requestParamValueJson;
	//
	// //System.out.println("requestParamKey: "+key);
	// //System.out.println("requestParamValue: "+value);
	// //String key=requestParamKeyJson;
	// //String value=requestParamValueJson;
	// uriBuilder.addParameter(key, value);
	// }
	// }

	private void assignPathParams(RequestDTO requestDTO, JSONParser parser) throws ParseException {

		String endPoint = requestDTO.getApiPath();
		// System.out.println("endPoint "+endPoint);
		final String pathParamJson = requestDTO.getPathParamJson();
		// System.out.println("pathParamJson: "+pathParamJson);
		if (StringUtils.isNotBlank(pathParamJson)) {
			final JSONObject pathParamMap = (JSONObject) parser.parse(pathParamJson);
			for (Object k : pathParamMap.keySet()) {
				// System.out.println("endpoint: "+endPoint);
				String key = (String) k;
				// System.out.println("key: "+key);
				String value = (String) pathParamMap.get(key).toString();
				if (StringUtils.isNotEmpty(value)) {
					// System.out.println("value: "+value);
					int i = 0;
					while (i < endPoint.length()) {
						if (endPoint.contains(key))
							endPoint = endPoint.replace("{" + key + "}", value);
						i++;
					}
					// System.out.println("endpoint::::::"+endPoint);
					// endPoint=endPoint.replaceFirst("\\{.*?\\}", value)
				}
			}
		}

		requestDTO.setApiPath(endPoint);

	}

	/**
	 * This function is used to get the "successful" field value from response
	 * json
	 * 
	 * @param httpResponse:
	 *            HttpResponse object
	 * @return : it returns a string containing response body
	 */
	// changed the visibility to public from private
	public String getApiResponse(HttpResponse httpResponse) throws IOException {
		final HttpEntity entity = httpResponse.getEntity();
		return entity != null ? EntityUtils.toString(entity) : null;
	}

	/**
	 * This Function is used to get the specific response value from the
	 * Pre-Requisite sheet
	 * 
	 * @param fileName
	 *            : File name of the Pre-requisite Api.
	 * @param sheetName
	 *            : Sheet Name of the Pre-requisite Api.
	 * @param dataProviderName
	 *            : Data Provider name of the Pre-requisite Api.
	 * @param httpClient
	 *            : HttpClient object from ApiTestBase.
	 * @param replacePreRequisiteMapBody
	 *            : Map which needs to be replace in the request, key tells
	 *            where replacement is required, value or inner map contains the
	 *            value to be replaced corresponding to the key.
	 * @param valueToGetFromResponseMap
	 *            : This map contains the value which we need to fetch from the
	 *            response with Key as the element name from the response and
	 *            value as its index.
	 * @param baseUrl
	 *            : BaseUrl of the Api from the Config.
	 * @return : returns true if we have a response false incase of exception.
	 * @throws Exception
	 */
	public boolean returnValueFromRespJson(String fileName, String sheetName, String dataProviderName,
			CloseableHttpClient httpClient, Map<replaceWhere, Map<String, String>> replacePreRequisiteMapMapBody,
			Map<String, Object> valueToGetFromResponseMap, String baseUrl) throws Exception {

		Object[][] excelData = DataProviderUtil.provideDataMap(fileName, sheetName, dataProviderName);
		excelData = DataProviderUtil.sheetMapToDPMap(excelData);

		// Expect only one row hence reading the row at index 0 only
		RequestDTO requestDTO = (RequestDTO) ((HashMap) excelData[0][1]).get(mapKeys.reqDTO.toString());

		for (replaceWhere key : replacePreRequisiteMapMapBody.keySet()) {
			if (replaceWhere.RequestBody.toString().equals(key.toString()))
				requestDTO.setRequestBody(JavaUtil.replacePreRequisite(replacePreRequisiteMapMapBody.get(key),
						requestDTO.getRequestBody()));
			else if (replaceWhere.RequestHeader.toString().equals(key.toString()))
				requestDTO.setHeaderJson(JavaUtil.replacePreRequisite(replacePreRequisiteMapMapBody.get(key),
						requestDTO.getHeaderJson()));
			else if (replaceWhere.RequestPath.toString().equals(key.toString()))
				requestDTO.setPathParamJson(JavaUtil.replacePreRequisite(replacePreRequisiteMapMapBody.get(key),
						requestDTO.getPathParamJson()));
			else if (replaceWhere.QueryParams.toString().equals(key.toString()))
				requestDTO.setQueryParamJson(JavaUtil.replacePreRequisite(replacePreRequisiteMapMapBody.get(key),
						requestDTO.getQueryParamJson()));
			else if (replaceWhere.PathParams.toString().equals(key.toString()))
				requestDTO.setPathParamJson(JavaUtil.replacePreRequisite(replacePreRequisiteMapMapBody.get(key),
						requestDTO.getPathParamJson()));

		}
		return returnValueFromRespJson(requestDTO, httpClient, valueToGetFromResponseMap, baseUrl);
	}

	/**
	 * This Function is used to get the specific response value from the
	 * Pre-Requisite sheet
	 * 
	 * @param fileName
	 *            : File name of the Pre-requisite Api.
	 * @param sheetName
	 *            : Sheet Name of the Pre-requisite Api.
	 * @param dataProviderName
	 *            : Data Provider name of the Pre-requisite Api.
	 * @param httpClient
	 *            : HttpClient object from ApiTestBase.
	 * @param replacePreRequisiteMapBody
	 *            : Map which needs to be replace in the request,
	 * @param valueToGetFromResponseMap
	 *            : This map contains the value which we need to fetch from the
	 *            response with Key as the element name from the response and
	 *            value as its index.
	 * @param baseUrl
	 *            : BaseUrl of the Api from the Config.
	 * @return : returns true if we have a response false incase of exception.
	 * @throws Exception
	 */
	public boolean returnValueFromRespJson(String fileName, String sheetName, String dataProviderName,
			CloseableHttpClient httpClient, Map<String, String> replacePreRequisiteMapBody, replaceWhere replaceString,
			Map<String, Object> valueToGetFromResponseMap, String baseUrl) throws Exception {

		try {

			boolean flag = false;

			Object[][] excelData = DataProviderUtil.provideDataMap(fileName, sheetName, dataProviderName);

			flag = returnValueFromRespJson(excelData, httpClient, replacePreRequisiteMapBody, replaceString,
					valueToGetFromResponseMap, baseUrl);

			return flag;

		} catch (Exception e) {
			TestListener.setException(e.getMessage(), e.getStackTrace());
			e.printStackTrace();
			throw e;
			// return false;
		}

	}

	/**
	 * This Function is used to get the specific response value from the
	 * Pre-Requisite sheet incase we have three level of dependency.
	 * 
	 * @param excelData
	 *            : Excel data in 2D array.
	 * @param httpClient
	 *            : HttpClient object from ApiTestBase.
	 * @param replacePreRequisiteMapBody
	 *            : Map which needs to be replace in the request.
	 * @param replaceString
	 *            : String in which the replacePreRequisiteMapBody will be
	 *            replacing the values.Values to be taken from enum
	 *            replaceWhere.
	 * @param valueToGetFromResponseMap
	 *            : This map contains the value which we need to fetch from the
	 *            response with Key as the element name from the response and
	 *            value as its index.
	 * @param baseUrl
	 *            : BaseUrl of the Api from the Config.
	 * @return : returns true if we have a response false incase of exception.
	 * @throws Exception
	 */
	public boolean returnValueFromRespJson(String[][] excelData, CloseableHttpClient httpClient,
			Map<String, String> replacePreRequisiteMapBody, replaceWhere replaceString,
			Map<String, Object> valueToGetFromResponseMap, String baseUrl) throws Exception {

		RequestDTO requestDTO = new RequestDTO();

		requestDTO = RequestUtil.createRequestDTO(excelData, requestDTO);

		if ((Object) replaceString != null) {
			if (replaceWhere.RequestBody.toString().equals(replaceString.toString()))
				requestDTO.setRequestBody(
						JavaUtil.replacePreRequisite(replacePreRequisiteMapBody, requestDTO.getRequestBody()));
			else if (replaceWhere.RequestHeader.toString().equals(replaceString.toString()))
				requestDTO.setHeaderJson(
						JavaUtil.replacePreRequisite(replacePreRequisiteMapBody, requestDTO.getHeaderJson()));
			else if (replaceWhere.RequestPath.toString().equals(replaceString.toString()))
				requestDTO.setPathParamJson(
						JavaUtil.replacePreRequisite(replacePreRequisiteMapBody, requestDTO.getPathParamJson()));
			else if (replaceWhere.QueryParams.toString().equals(replaceString.toString()))
				requestDTO.setQueryParamJson(
						JavaUtil.replacePreRequisite(replacePreRequisiteMapBody, requestDTO.getQueryParamJson()));
			else if (replaceWhere.PathParams.toString().equals(replaceString.toString()))
				requestDTO.setPathParamJson(
						JavaUtil.replacePreRequisite(replacePreRequisiteMapBody, requestDTO.getPathParamJson()));

		}

		return returnValueFromRespJson(requestDTO, httpClient, valueToGetFromResponseMap, baseUrl);
	}

	/**
	 * This Function is used to get the specific response value from the
	 * Pre-Requisite sheet incase we have three level of dependency.
	 * 
	 * @param excelData
	 *            : Excel data in 2D array.
	 * @param httpClient
	 *            : HttpClient object from ApiTestBase.
	 * @param replacePreRequisiteMapBody
	 *            : Map which needs to be replace in the request.
	 * @param replaceString
	 *            : String in which the replacePreRequisiteMapBody will be
	 *            replacing the values.Values to be taken from enum
	 *            replaceWhere.
	 * @param valueToGetFromResponseMap
	 *            : This map contains the value which we need to fetch from the
	 *            response with Key as the element name from the response and
	 *            value as its index.
	 * @param baseUrl
	 *            : BaseUrl of the Api from the Config.
	 * @return : returns true if we have a response false incase of exception.
	 * @throws Exception
	 */
	public boolean returnValueFromRespJson(Object[][] excelData, CloseableHttpClient httpClient,
			Map<String, String> replacePreRequisiteMapBody, replaceWhere replaceString,
			Map<String, Object> valueToGetFromResponseMap, String baseUrl) throws Exception {

		excelData = DataProviderUtil.sheetMapToDPMap(excelData);

		// Expect only one row hence reading the row at index 0 only
		RequestDTO requestDTO = (RequestDTO) ((HashMap) excelData[0][1]).get(mapKeys.reqDTO.toString());

		if ((Object) replaceString != null) {
			if (replaceWhere.RequestBody.toString().equals(replaceString.toString()))
				requestDTO.setRequestBody(
						JavaUtil.replacePreRequisite(replacePreRequisiteMapBody, requestDTO.getRequestBody()));
			else if (replaceWhere.RequestHeader.toString().equals(replaceString.toString()))
				requestDTO.setHeaderJson(
						JavaUtil.replacePreRequisite(replacePreRequisiteMapBody, requestDTO.getHeaderJson()));
			else if (replaceWhere.RequestPath.toString().equals(replaceString.toString()))
				requestDTO.setPathParamJson(
						JavaUtil.replacePreRequisite(replacePreRequisiteMapBody, requestDTO.getPathParamJson()));
			else if (replaceWhere.QueryParams.toString().equals(replaceString.toString()))
				requestDTO.setQueryParamJson(
						JavaUtil.replacePreRequisite(replacePreRequisiteMapBody, requestDTO.getQueryParamJson()));
			else if (replaceWhere.PathParams.toString().equals(replaceString.toString()))
				requestDTO.setPathParamJson(
						JavaUtil.replacePreRequisite(replacePreRequisiteMapBody, requestDTO.getPathParamJson()));

		}

		return returnValueFromRespJson(requestDTO, httpClient, valueToGetFromResponseMap, baseUrl);
	}

	/**
	 * This Function is used to get the specific response value from the
	 * Pre-Requisite sheet incase we have three level of dependency.
	 * 
	 * @param excelData
	 *            : Excel data in 2D array.
	 * @param httpClient
	 *            : HttpClient object from ApiTestBase.
	 * @param replacePreRequisiteMapBody
	 *            : Map which needs to be replace in the request.
	 * @param replaceString
	 *            : String in which the replacePreRequisiteMapBody will be
	 *            replacing the values.Values to be taken from enum
	 *            replaceWhere.
	 * @param valueToGetFromResponseMap
	 *            : This map contains the value which we need to fetch from the
	 *            response with Key as the element name from the response and
	 *            value as its index.
	 * @param baseUrl
	 *            : BaseUrl of the Api from the Config.
	 * @return : returns true if we have a response false incase of exception.
	 * @throws Exception
	 */
	public boolean returnValueFromRespJson(RequestDTO requestDTO, CloseableHttpClient httpClient,
			Map<String, Object> valueToGetFromResponseMap, String baseUrl) throws Exception {

		try {

			String apiResponse = null;
			boolean flag = true;

			apiResponse = getResponse(requestDTO, createRequest(requestDTO, baseUrl, httpClient, true));
			// validateRequest(requestDTO,apiResponse);

			if (!valueToGetFromResponseMap.isEmpty())
				flag = JsonUtil.returnValueFromResponse(apiResponse, valueToGetFromResponseMap);

			if (!flag)
				throw new Exception();

			// TestListener.setLog(LogStatus.INFO, "<pre>"+"Value to get from
			// Pre-Requisite API ::
			// "+valueToGetFromResponseMap.toString()+"</pre>");
			LoggerUtil.setlog(LogLevel.INFO, "<pre>" + "Value to get from Pre-Requisite API :: "
					+ valueToGetFromResponseMap.toString() + "</pre>");

			return flag;

		} catch (Exception e) {
			TestListener.setException(e.getMessage(), e.getStackTrace());
			e.printStackTrace();
			throw e;
			// return false;
		}
	}

	/**
	 * This Function is used to create the request for the running test case
	 * 
	 * @param requestDTO
	 *            : Request object
	 * @param baseUrl
	 *            : Base Url from the config file.
	 * @param httpClient
	 *            : HttpClient Object from ApiTestBase.
	 * @throws IOException 
	 */

	public HttpResponse createRequest(RequestDTO requestDTO, String baseUrl, CloseableHttpClient httpClient,
			boolean uploadFlag) throws IOException { 
		String apiResponse = null;
		ApiHelper apiHelper = new ApiHelper();
		HttpResponse httpResponse = null;
		final HttpRequestBase httpRequest;

		try {

			httpRequest = prepareHTTPRequest(requestDTO, baseUrl, uploadFlag);

			LoggerUtil.setlog(LogLevel.INFO, "<pre>" + "Trying Request as: " + httpRequest.toString() + "</pre>");
			if (requestDTO.getRequestBody() != null) {
				LoggerUtil.setlog(LogLevel.INFO,
						"<pre>" + "Request Body: " + requestDTO.getRequestBody().toString() + "</pre>");
			}

			if (requestDTO.getPathParamJson() != null) {
				LoggerUtil.setlog(LogLevel.INFO,
						"<pre>" + "Request Path Param :: " + requestDTO.getPathParamJson().toString() + "</pre>");
			}

			if (requestDTO.getQueryParamJson() != null) {
				LoggerUtil.setlog(LogLevel.INFO,
						"<pre>" + "Request Query Param :: " + requestDTO.getQueryParamJson().toString() + "</pre>");
			}

			Assert.assertNotNull(httpRequest);
			// httpClient = HttpClients.createDefault();
			PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
			cm.setMaxTotal(100);
			httpClient = HttpClients.custom().setConnectionManager(cm).build();
			httpResponse = httpClient.execute(httpRequest);
			Assert.assertNotNull(httpResponse);
			LoggerUtil.setlog(LogLevel.INFO,
					"<pre>" + "Actual Response Code :: " + httpResponse.getStatusLine().getStatusCode() + "</pre>");
			Assert.assertEquals(httpResponse.getStatusLine().getStatusCode(), requestDTO.getResponseCode());
		} catch (AssertionError e) {
			TestListener.setException(e.getMessage(), e.getStackTrace());
			e.printStackTrace();
			if (requestDTO.getResponseCode() != httpResponse.getStatusLine().getStatusCode()) {
				LoggerUtil.setlog(LogLevel.FAIL,
						"<pre>" + "Assertion failure. " + "Response Code doesnot match. Received: "
								+ httpResponse.getStatusLine().getStatusCode() + "</pre>");
				apiResponse = apiHelper.getApiResponse(httpResponse);
				System.out.println("apiResponse " + apiResponse);
			}
			throw e;
		}

		catch (Exception e) {
			TestListener.setException(e.getMessage(), e.getStackTrace());
			e.printStackTrace();
			LoggerUtil.setlog(LogLevel.FAIL, "<pre>" + "Response could not be validated" + "</pre>");
			throw new RuntimeException(e);
		}
		return httpResponse;
	}

	/**
	 * This Function is used to get the request for the running test case
	 * 
	 * @param requestDTO
	 *            : Request object
	 * @param httpResponse
	 *            : HttpResponse of API
	 */

	public String getResponse(RequestDTO requestDTO, HttpResponse httpResponse) {
		String apiResponse = null;
		ApiHelper apiHelper = new ApiHelper();
		try {
			apiResponse = apiHelper.getApiResponse(httpResponse);
			if (apiResponse != null)
				LoggerUtil.setlog(LogLevel.INFO, "<pre>" + "Actual Response Body :: " + apiResponse + "</pre>");
			Assert.assertNotNull(apiResponse);

		} catch (AssertionError e) {
			TestListener.setException(e.getMessage(), e.getStackTrace());
			e.printStackTrace();
			if (apiResponse == null || apiResponse.isEmpty()) {
				LoggerUtil.setlog(LogLevel.FAIL, "<pre>" + "Assertion failure" + "API Response is empty" + "</pre>");
			}
			throw e;
		} catch (Exception e) {
			TestListener.setException(e.getMessage(), e.getStackTrace());
			e.printStackTrace();
			LoggerUtil.setlog(LogLevel.FAIL, "<pre>" + "Response could not be validated" + "</pre>");
			throw new RuntimeException(e);
		}
		return apiResponse;
	}

	/**
	 * This Function is used to validate the request for the running test case
	 * 
	 * @param requestDTO
	 *            : Request object
	 * @param apiResponse
	 *            : apiResponse
	 */

	public void validateRequest(RequestDTO requestDTO, String apiResponse) {

		try {
			if (requestDTO.getExpectedResponseJson() != null) {
				ResponseUtil.validateActualResponseWithExpected(requestDTO.getExpectedResponseJson(), apiResponse);
			}
		} catch (AssertionError e) {
			e.printStackTrace();
			if (apiResponse == null || apiResponse.isEmpty()) {
				LoggerUtil.setlog(LogLevel.FAIL, "<pre>" + "Assertion failure. " + "API Response is empty" + "</pre>");
			} else {
				LoggerUtil.setlog(LogLevel.FAIL,
						"<pre>" + "Assertion failure. " + "API Response doesnot match" + "</pre>");
				LoggerUtil.setlog(LogLevel.INFO,
						"<pre>" + TestListener.setException(e.getMessage(), e.getStackTrace()) + "</pre>");
			}
			throw e;
		}

		catch (Exception e) {
			e.printStackTrace();
			LoggerUtil.setlog(LogLevel.FAIL, "<pre>" + "Response could not be validated" + "</pre>");
			LoggerUtil.setlog(LogLevel.INFO,
					"<pre>" + TestListener.setException(e.getMessage(), e.getStackTrace()) + "</pre>");
			throw new RuntimeException(e);
		}
	}
}
