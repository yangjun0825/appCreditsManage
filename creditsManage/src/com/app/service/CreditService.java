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

	/** 
	* @Title: retrieveCreditByMacList 
	* @Description: 获取mac地址积分
	* @param params
	* @return List<CreditsBean>    返回类型 
	* @throws 
	*/
	public List<CreditsBean> retrieveCreditByMacList(Map<String, Object> params);
	
	/** 
	* @Title: retrieveCreditRecordListByCondition 
	* @Description: 根据条件获取用户积分记录 
	* @param  params
	* @return List<CreditsBean>    返回类型 
	* @throws 
	*/
	public List<CreditsBean> retrieveCreditRecordListByCondition(Map<String, Object> params);
	
	/** 
	* @Title: retrieveUserCreditCount 
	* @Description: 获取用户当天增加的积分总数之和
	* @param  params
	* @return List<CreditsBean>    返回类型 
	* @throws 
	*/
	public List<CreditsBean> retrieveUserCreditCount(Map<String, Object> params);
}
