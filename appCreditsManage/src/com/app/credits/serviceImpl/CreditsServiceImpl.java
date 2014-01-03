package com.app.credits.serviceImpl;

import java.io.UnsupportedEncodingException;
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
import com.app.util.Base64;
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
	public String userCreditsSysn(String xmlStr, Head head) {
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
			response = Util.getResponseForFalse(xmlStr, head, "101", "参数传递错误");
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
		
		//对用户总积分处理
		String updateStr = "credits.updateUserTotalCredit";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("credit", credit);
		int j = creditsDao.update(updateStr, params);
		
		if(logger.isDebugEnabled()) {
			logger.debug("[creditDbUpdateResult] = " + j);
		}
		
		//返回正确结果
		response = Util.getResponseForTrue(head, "");
		
		if(logger.isDebugEnabled()) {
			logger.debug("[response] = " + response);
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
	public String userCreditsRecords(String xmlStr, Head head) {
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
			response = Util.getResponseForFalse(xmlStr, head, "100", "参数传递错误");
			return response;
		}
		
		//获取用户积分记录
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("account", account);
		params.put("creditType", Constant.min_credit);
		
		String queryStr = "credits.retrieveCreditsRecords";
		List<Map<String, Object>> creditsRecordsList = creditsDao.getSearchList(queryStr, params);
		
		StringBuffer creditSb = new StringBuffer();
		
		//返回正确结果
		if(CollectionUtils.isEmpty(creditsRecordsList)) {
			response = Util.getResponseForFalse(xmlStr, head, "101", "没有对应提现记录");
			return response;
		} else {
			for(Map<String, Object> map : creditsRecordsList) {
				creditSb.append("<item>");
				creditSb.append("<time>" + map.get("createtime") + "</time>");
				creditSb.append("<credit>" + map.get("credit") + "</credit>");
				creditSb.append("</item>");
			}
			
			String encodeStr = creditSb.toString();
			if(logger.isDebugEnabled()) {
				logger.debug("[encodeStr] = " + encodeStr);
			}
			try {
				encodeStr = Base64.encodeBytes(encodeStr.getBytes("UTF-8"));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			response = Util.getResponseForTrue(head, encodeStr);
			
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug("[response] = " + response);
		}
		
		logger.debug("exit CreditsServiceImpl.userCreditsSysn(String xmlStr, Head head) ");
		return response;
	}

}
