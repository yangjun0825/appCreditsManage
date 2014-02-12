package com.app.service;

import java.util.List;
import java.util.Map;

import com.app.user.bean.UserBean;

/** 
* @ClassName: UserService 
* @Description: 用户登录
* @author yangjun 
* @date Jan 13, 2014 9:47:01 AM  
*/
public interface UserService {
	
	/** 
	* @Title: userLogin 
	* @Description: 获取用户信息
	* @param  params
	* @return List<UserBean>    返回类型 
	* @throws 
	*/
	public List<UserBean> retrieveUserInfoList(Map<String, Object> params);
	
	/** 
	* @Title: freezeUser 
	* @Description: 冻结账户
	* @return String    返回类型 
	* @throws 
	*/
	public String freezeUser(Map<String, Object> params);
	
}
