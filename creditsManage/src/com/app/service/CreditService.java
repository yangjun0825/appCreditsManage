package com.app.service;

import java.util.List;
import java.util.Map;

import com.app.credits.bean.CreditsBean;
import com.app.credits.bean.WithDrawBean;

public interface CreditService {

	/** 
	* @Title: retrieveCreditWitHDrawList 
	* @Description: 获取用户体现积分记录
	* @param  params
	* @return List<WithDrawBean>    返回类型 
	* @throws 
	*/
	public List<WithDrawBean> retrieveCreditWitHDrawList(Map<String, Object> params);
	
	/** 
	* @Title: completeWithDraw 
	* @Description: 完成体现操作
	* @param params
	* @return String    返回类型 
	* @throws 
	*/
	public String completeWithDraw(Map<String, Object> params);
	
}
