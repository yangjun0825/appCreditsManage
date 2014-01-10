package com.app.credits.serviceImpl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.credits.bean.CreditsBean;
import com.app.credits.service.CreditsService;
import com.app.util.Constant;
import com.app.util.MyBatisDao;
import com.app.util.Util;
import com.app.vo.Head;

/** 
* @ClassName: CreditsServiceImpl 
* @Description: 用户积分处理
* @author yangjun junyang0825@gmail.com 
* @date 2013-12-28 下午12:21:40  
*/
@Service
public class CreditsServiceImpl implements CreditsService {

	@Autowired
	private MyBatisDao<Map<String, Object>> creditsDao;
	
	private Log logger = LogFactory.getLog(CreditsServiceImpl.class);	
	
	/* (非 Javadoc) 
	* <p>Title: userCreditsSysn</p> 
	* <p>Description: 用户积分同步</p> 
	* @return 
	* @see com.app.credits.service.CreditsService#userCreditsSysn() 
	*/
	@Override
	public String userCreditsSysn(String xmlStr, Head head) throws Exception {
		logger.debug("enter CreditsServiceImpl.userCreditsSysn(String xmlStr, Head head) " + "[xmlStr] = " + xmlStr);
		
		String response = "";
		
		List<Element> list = Util.getRequestDataByXmlStr(xmlStr, "svccont/pra/item");
		
		String account = "";
		String type = "";
		String credit = "";
		String channelType = "";
		
		if(CollectionUtils.isNotEmpty(list)) {
			account = list.get(0).elementTextTrim("accout");
			type = list.get(0).elementTextTrim("type");
			credit = list.get(0).elementTextTrim("credit");
			channelType = list.get(0).elementTextTrim("channelType");
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug("[account] = " + account + " [type] = " + type + " [credit] = " + credit + " [channelType] = " + channelType);
		}
		
		if (StringUtils.isBlank(account) || StringUtils.isBlank(type)
				|| StringUtils.isBlank(credit)) {
			response = Util.getResponseForFalse(xmlStr, head, "102", "无效请求");
			return response;
		}
		
		//积分入库积分记录表
		String insertStr = "credits.insertUserCredits";
		
		CreditsBean creditsBean = new CreditsBean();
		creditsBean.setId(UUID.randomUUID().toString());
		creditsBean.setAccount(account);
		creditsBean.setCredit(credit);
		creditsBean.setCreditType(type);
		creditsBean.setChannelType("");
		creditsBean.setCreateTime(new Date());
		
		int i = creditsDao.insert(insertStr, creditsBean);
		
		if(logger.isDebugEnabled()) {
			logger.debug("[creditDbInsertResult] = " + i);
		}
		
		if(i > 0) {
			Util.getResponseForTrue(head, "");
		}
		
		//查询用户总积分
		String queryStr = "user.retrieveUserInfo";
		
		Map<String, Object> userParams = new HashMap<String, Object>();
		userParams.put("account", account);
		List<Map<String, Object>> userInfoList = creditsDao.getSearchList(queryStr, userParams);
		
		if(logger.isDebugEnabled()) {
			logger.debug("[userInfoList] = " + userInfoList);
			if(userInfoList != null) {
				logger.debug("[userInfoListSize] = " + userInfoList.size());
			}
		}
		
		//如果查询到的用户总积分比用户同步的积分大，才允许提取积分
		if(CollectionUtils.isNotEmpty(userInfoList)) {
			String totalCredit = (String)userInfoList.get(0).get("totalcredit");
			
			int totalCredit_i = Integer.parseInt(totalCredit);
			
			int credit_i = Integer.parseInt(credit);
			
			if(logger.isDebugEnabled()) {
				logger.debug("[totalCredit] = " + totalCredit + " [totalCredit_i] = " + totalCredit_i + " [credit_i] = " + credit_i);
			}
			
			if(totalCredit_i - credit_i >= 0) {
				//对用户总积分处理
				String updateStr = "credits.updateUserTotalCredit";
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("credit", credit);
				int j = creditsDao.update(updateStr, params);
				
				if(logger.isDebugEnabled()) {
					logger.debug("[creditDbUpdateResult] = " + j);
				}
				
				if(j > 0) {
					//返回正确结果
					response = Util.getResponseForTrue(head, "");
					
					if(logger.isDebugEnabled()) {
						logger.debug("[response] = " + response);
					}
				}
			} else {
				return Util.getResponseForFalse(xmlStr, head, "105", "余额不足，无法提现");
			}
		} else {
			return Util.getResponseForFalse(xmlStr, head, "101", "不存在的账号");
		}
		
		//同步之后再次查询用户总积分
		String queryStrAfSysn = "user.retrieveUserInfo";
		
		Map<String, Object> userParamsAfSysn = new HashMap<String, Object>();
		userParamsAfSysn.put("account", account);
		List<Map<String, Object>> userInfoAfSysnList = creditsDao.getSearchList(queryStrAfSysn, userParamsAfSysn);
		
		if(logger.isDebugEnabled()) {
			logger.debug("[userInfoAfSysnList] = " + userInfoAfSysnList);
			if(userInfoList != null) {
				logger.debug("[userInfoAfSysnListSize] = " + userInfoAfSysnList.size());
			}
		}
		
		if(CollectionUtils.isNotEmpty(userInfoAfSysnList)) {
			StringBuffer userSb = new StringBuffer();
			
			for(Map<String, Object> map : userInfoAfSysnList) {
				userSb.append("<credits>" + map.get("totalcredit") + "</credits>");
			}
			
			String encodeStr = userSb.toString();
			if(logger.isDebugEnabled()) {
				logger.debug("[encodeStr] = " + encodeStr);
			}
			
			response = Util.getResponseForTrue(head, encodeStr);
		} else {
			String encodeStr = "<credits></credits>";
			response = Util.getResponseForTrue(head, encodeStr);
		}
		
		logger.debug("exit CreditsServiceImpl.userCreditsSysn(String xmlStr, Head head) ");
		return response;
	}

	/* (非 Javadoc) 
	* <p>Title: userCreditsRecords</p> 
	* <p>Description: 获取用户积分记录</p> 
	* @param xmlStr
	* @param head
	* @return 
	* @see com.app.credits.service.CreditsService#userCreditsRecords(java.lang.String, com.app.vo.Head) 
	*/
	@Override
	public String userCreditsRecords(String xmlStr, Head head) throws Exception {
		logger.debug("enter CreditsServiceImpl.userCreditsSysn(String xmlStr, Head head) " + "[xmlStr] = " + xmlStr);
		
		String response = "";
		
		List<Element> list = Util.getRequestDataByXmlStr(xmlStr, "svccont/pra/item");
		
		String account = "";
		
		if(!CollectionUtils.isEmpty(list)) {
			account = list.get(0).elementTextTrim("accout");
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug("[account] = " + account);
		}
		
		if (StringUtils.isBlank(account)) {
			response = Util.getResponseForFalse(xmlStr, head, "102", "无效请求");
			return response;
		}
		
		//获取用户积分记录
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("account", account);
		params.put("creditType", Constant.min_credit);
		
		String queryStr = "credits.retrieveCreditsRecords";
		List<Map<String, Object>> creditsRecordsList = creditsDao.getSearchList(queryStr, params);
		
		StringBuffer creditSb = new StringBuffer();
		creditSb.append("<contentitem>");
		
		//返回正确结果
		if(CollectionUtils.isNotEmpty(creditsRecordsList)) {
			for(Map<String, Object> map : creditsRecordsList) {
				creditSb.append("<item>");
				creditSb.append("<time>" + map.get("createtime") + "</time>");
				creditSb.append("<credit>" + map.get("credit") + "</credit>");
				creditSb.append("</item>");
			}
		}
		creditSb.append("</contentitem>");
		
		String encodeStr = creditSb.toString();
		if(logger.isDebugEnabled()) {
			logger.debug("[encodeStr] = " + encodeStr);
		}
		response = Util.getResponseForTrue(head, encodeStr);
		
		if(logger.isDebugEnabled()) {
			logger.debug("[response] = " + response);
		}
		
		logger.debug("exit CreditsServiceImpl.userCreditsSysn(String xmlStr, Head head) ");
		return response;
	}

}
