package com.proptiger.qa.api.dto;

/**
 * User: Himanshu.Verma
 */
public class RequestDTO {

    private String apiPath;
    private String methodType;
    private String headerJson;
    private String pathParamJson;
    private String queryParamJson;
	private int responseCode;
    private String expectedResponseJson;
    private String requestBody;
    private String expectedDB;

    public String getExpectedDB() {
		return expectedDB;
	}

	public void setExpectedDB(String expectedDB) {
		this.expectedDB = expectedDB;
	}

	public String getRequestBody() {
		return requestBody;
	}

	public void setRequestBody(String requestBody) {
		this.requestBody = requestBody;
	}

	public RequestDTO() {
    }
    
    public String getMethodType() {
        return methodType;
    }

    public void setMethodType(String methodType) {
        this.methodType = methodType;
    }

    public String getApiPath() {
        return apiPath;
    }

    public void setApiPath(String apipath) {
        this.apiPath = apipath;
    }

    public String getHeaderJson() {
        return headerJson;
    }

    public void setHeaderJson(String headerJson) {
        this.headerJson = headerJson;
    }

    public String getPathParamJson() {
        return pathParamJson;
    }

    public void setPathParamJson(String pathParamJson) {
        this.pathParamJson = pathParamJson;
    }

    public String getQueryParamJson() {
        return queryParamJson;
    }

    public void setQueryParamJson(String queryParamJson) {
        this.queryParamJson = queryParamJson;
    }
	
    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public String getExpectedResponseJson() {
        return expectedResponseJson;
    }

    public void setExpectedResponseJson(String responseParamJson) {
        this.expectedResponseJson = responseParamJson;
    }

    @Override
    public String toString() {
        return "RequestDTO{" +
                "apiPath='" + apiPath + '\'' +
                ", methodType='" + methodType + '\'' +
                ", headerJson='" + headerJson + '\'' +
                ", pathParamJson='" + pathParamJson + '\'' +
                ", queryParamJson='" + queryParamJson + '\'' +
                ", requestBody='" + requestBody + '\'' +
                ", responseCode=" + responseCode +
                ", expectedResponseJson='" + expectedResponseJson + '\'' +
                ", expectedDB='" + expectedDB + '\'' +
                '}';
    }
}
