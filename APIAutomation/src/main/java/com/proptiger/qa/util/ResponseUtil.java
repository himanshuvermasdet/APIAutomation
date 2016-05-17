package com.proptiger.qa.util;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.entity.ContentType;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.simple.parser.ParseException;
import org.testng.asserts.SoftAssert;

import com.proptiger.qa.listener.TestListener;
import com.proptiger.qa.util.LoggerUtil.LogLevel;
import com.proptiger.qa.apibase.APITestBase;

/**
 * User: Himanshu.Verma
 */
public class ResponseUtil {

	//	static StringBuffer jsonPath = new StringBuffer();
	
	

	/**
	 * This function is used to match the actual result with the expected result from the API response.
	 * @param expectedResponseJson : Expected Json String
	 * @param actualResponseJSON : Actual Json String
	 * @param contentTypeHeader - Content Type of the Header
	 * @throws ParseException
	 * @throws IOException
	 */
	public static void validateActualResponseWithExpected(String expectedResponseJson, String actualResponseJSON,Header contentTypeHeader) throws ParseException, IOException {
		if (StringUtils.isNotBlank(expectedResponseJson) && contentTypeHeader != null && StringUtils.isNotBlank(contentTypeHeader.getValue())) {
			final String contentTypeValue = contentTypeHeader.getValue();
			if (contentTypeValue.contains(ContentType.APPLICATION_JSON.getMimeType())|| contentTypeValue.contains(ContentType.APPLICATION_FORM_URLENCODED.getMimeType())) {
				compare(expectedResponseJson, actualResponseJSON);
			}
		}

	}

	public static void validateActualResponseWithExpected(String expectedResponseJson, String actualResponseJSON) throws ParseException, IOException {
		if (StringUtils.isNotBlank(expectedResponseJson) && StringUtils.isNotBlank(actualResponseJSON)) 
		{
			compare(expectedResponseJson, actualResponseJSON);
		}
	}

	private synchronized static void compare(String expected, String actual) throws IOException {
		
		ObjectMapper mapper = new ObjectMapper();
		System.out.println("expected: "+expected);
		System.out.println("actual: "+actual);
		
		JsonNode expectedRootNode = mapper.readTree(expected);
		JsonNode actualRootNode = mapper.readTree(actual);
		compareNodes(expectedRootNode, actualRootNode);
	}

	private static void compareNodes(JsonNode expectedNode, JsonNode actualNode) {
		//ArrayList<Object> a = new ArrayList<Object>();
		//ArrayList<Object> b = new ArrayList<Object>();

		try {
			if (expectedNode.isContainerNode()) {
				Iterator<Map.Entry<String, JsonNode>> expectedChildNodes = expectedNode.getFields();
				Iterator<JsonNode> expectedNodeArrayIterator = expectedNode.getElements();


				if (expectedChildNodes.hasNext()) { //if expectedNode is an Object, and fields exist i.e.general case
					//System.out.println("Entered object general case");
					while (expectedChildNodes.hasNext()) {
						Map.Entry<String, JsonNode> stringJsonNodeEntry = expectedChildNodes.next();
						//					jsonPath.append(stringJsonNodeEntry.getKey()).append("-->");
						//System.out.println("stringJsonNodeEntry "+stringJsonNodeEntry);
						JsonNode actualJsonNode = actualNode.path(stringJsonNodeEntry.getKey());
						compareNodes(stringJsonNodeEntry.getValue(), actualJsonNode);
					}
				} else if (expectedNodeArrayIterator.hasNext()) { //if expectedNode is a Json Array, and child elements exist i.e.general case
					//System.out.println("Entered array general case");
					Iterator<JsonNode> actualNodeArrayIterator = actualNode.getElements();

					assertEquals("JsonNodeFailure: Array Size Mismatch", expectedNode.size(), actualNode.size());
					
					while (expectedNodeArrayIterator.hasNext()){
	
						//a.add(expectedNodeArrayIterator.next());
						//b.add(actualNodeArrayIterator.next());
						compareNodes(expectedNodeArrayIterator.next(), (actualNodeArrayIterator.hasNext()) ? actualNodeArrayIterator.next() : actualNode.path("new missingnode()"));
					}
					//System.out.println("sd "+a);
					//System.out.println("sds "+b);
				} else { //base case
					//System.out.println("Entered empty container base case");
					//APITestBase.softAssert.assertTrue(!actualNode.isMissingNode(), "JsonNodeFailure: Missing Node");
					//APITestBase.softAssert.assertTrue( !actualNode.iterator().hasNext(), "JsonNodeFailure: Child Elements Present");
					assertTrue("JsonNodeFailure: Missing Node", !actualNode.isMissingNode());
					assertTrue("JsonNodeFailure: Child Elements Present", !actualNode.iterator().hasNext());
				}
			}

			if (expectedNode.isValueNode()) { //base case
				assertTrue("JsonNodeFailure: Missing Node. Actual Node is ::"+actualNode+ " and expected node is ::"+expectedNode, !actualNode.isMissingNode());
				if(!expectedNode.asText().equals("_NotToAssert_")){
					//APITestBase.softAssert.assertEquals(actualNode, expectedNode, "JsonNodeFailure and path is :: "); 
					assertEquals("JsonNodeFailure and path is :: " , expectedNode, actualNode);
				}

			}
		} catch (Exception | Error e) {
			TestListener.setException(e.getMessage(), e.getStackTrace());
			//LoggerUtil.setlog(LogLevel.INFO, "<pre>" + TestListener.setException(e.getMessage(), e.getStackTrace())+ "</pre>");
//			e.printStackTrace();
			throw e;
		}
		finally{
			//APITestBase.softAssert.assertAll();
		}
	}
}

