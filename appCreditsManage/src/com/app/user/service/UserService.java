package com.app.user.service;

import com.app.vo.Head;


/** 
* @ClassName: UserRegisterService 
* @Description: 用户接口
* @author yangjun junyang0825@gmail.com 
* @date 2013-12-28 上午10:48:42  
*/
public interface UserService {

	/** 
	* @Title: userRegister 
	* @Description: 用户注册
	* @param  xmlStr
	* @param  head
	* @return String    返回类型 
	* @throws 
	*/
	public String userRegister(String xmlStr, Head head) throws Exception ;
	
	/** 
	* @Title: userRegister 
	* @Description: 用户自动注册
	* @param  xmlStr
	* @param  head
	* @return String    返回类型 
	* @throws 
	*/
	public String userAutoRegister(String xmlStr, Head head) throws Exception ;
	
	/** 
	* @Title: userLogin 
	* @Description: 用户登录
	* @param  xmlStr
	* @param  head
	* @return String    返回类型 
	* @throws 
	*/
	public String userLogin(String xmlStr, Head head) throws Exception;
	
	/** 
	* @Title: userFeedBack 
	* @Description: 用户反馈 
	* @param xmlStr
	* @param head
	* @return String    返回类型 
	* @throws 
	*/
	public String userFeedBack(String xmlStr, Head head) throws Exception;
	
	/** 
	* @Title: userInfoModify 
	* @Description: 用户信息修改 
	* @param  xmlStr
	* @param  head
	* @param @throws Exception    设定文件 
	* @return String    返回类型 
	* @throws 
	*/
	public String userInfoModify(String xmlStr, Head head) throws Exception;
	
}
