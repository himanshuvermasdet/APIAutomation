package com.proptiger.qa.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.JsonNode;

/**
 * User: Himanshu.Verma
 */
public class JavaUtil {

	/**
	 * This function is used to replace values from the Map in the string.
	 * @param replacePreRequisite : Map who's values to be replaced in the string.
	 * @param queryParamJson : Text in which the replacement needs to be done.
	 * @return
	 */
	public static String replacePreRequisite(Map<String, String> replacePreRequisite,String queryParamJson) {

		if (queryParamJson == null)
			return null;
		String patternString = "__(" + StringUtils.join(replacePreRequisite.keySet(), "|") + ")__";
		Pattern pattern = Pattern.compile(patternString);
		Matcher matcher = pattern.matcher(queryParamJson);
		StringBuffer sb = new StringBuffer();
		while(matcher.find()) {
			//			System.out.println("Value inside Matcher ::" +replacePreRequisite.get(matcher.group(1)));
			matcher.appendReplacement(sb, (String) replacePreRequisite.get(matcher.group(1)));
		}
		matcher.appendTail(sb);

		return sb.toString();

	}
	
	
}
