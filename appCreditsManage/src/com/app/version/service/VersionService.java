package com.app.version.service;

import com.app.vo.Head;

/** 
* @ClassName: VersionService 
* @Description: 版本管理
* @author yangjun 
* @date Jan 12, 2014 4:04:46 PM  
*/
public interface VersionService {

	/** 
	* @Title: versionUpdate 
	* @Description: 版本升级接口 
	* @param  xmlStr
	* @param  head
	* @return String    返回类型 
	* @throws 
	*/
	public String versionUpdate(String xmlStr, Head head) throws Exception ; 
	
}
