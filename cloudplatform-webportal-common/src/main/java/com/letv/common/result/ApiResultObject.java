package com.letv.common.result;


/**Program Name: ApiResultObject <br>
 * Description:  if result is null or result is error,setUrl into url field.<br>
 * @author name: howie <br>
 * Written Date: 2015年7月1日 <br>
 * Modified By: <br>
 * Modified Date: <br>
 */
public class ApiResultObject {
	private String result;
	private String url;
	private Boolean analyzeResult;//分析后结果
	
	public ApiResultObject(String result,String url) {
		this.result = result;
		this.url = url;
	}
	
	public ApiResultObject() {
		
	}
	
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public Boolean getAnalyzeResult() {
		return analyzeResult;
	}
	public void setAnalyzeResult(Boolean analyzeResult) {
		this.analyzeResult = analyzeResult;
	}
	
}
