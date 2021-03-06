package com.app.credits.service;

import com.app.vo.Head;

/** 
* @ClassName: CreditsService 
* @Description: 积分业务处理类
* @author yangjun junyang0825@gmail.com 
* @date 2013-12-28 下午12:17:49  
*/
public interface CreditsService {
	
	/** 
	* @Title: userCreditsSysn 
	* @Description: 用户积分同步
	* @return String    返回类型 
	* @throws 
	*/
	public String userCreditsSysn(String xmlStr, Head head) throws Exception;
	
	/** 
	* @Title: userCreditsRecords 
	* @Description: 获取用户积分记录
	* @param  xmlStr
	* @param  head
	* @return String    返回类型 
	* @throws 
	*/
	public String userCreditsRecords(String xmlStr, Head head) throws Exception;
	
	/** 
	* @Title: userWithdrawProcess 
	* @Description: 处理用户提现请求
	* @param  xmlStr
	* @param  head
	* @param @throws Exception    设定文件 
	* @return String    返回类型 
	* @throws 
	*/
	public String userWithdrawProcess(String xmlStr, Head head) throws Exception;
	
	/** 
	* @Title: userDaliyTaskProcess 
	* @Description: 用户每日任务
	* @param  xmlStr
	* @param  head
	* @param @throws Exception    设定文件 
	* @return String    返回类型 
	* @throws 
	*/
	public String userDaliyTaskProcess(String xmlStr, Head head) throws Exception;
	
	/** 
	* @Title: userPromoteProcess 
	* @Description: 软件推广处理
	* @param  xmlStr
	* @param  head
	* @param @throws Exception    设定文件 
	* @return String    返回类型 
	* @throws 
	*/
	public String userPromoteProcess(String xmlStr, Head head) throws Exception;
}
