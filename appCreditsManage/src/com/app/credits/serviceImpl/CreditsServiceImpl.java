package com.app.credits.serviceImpl;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.app.credits.bean.CreditsBean;
import com.app.credits.service.CreditsService;
import com.app.util.Base64;
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
		
		String userId = "";
		String type = "";
		String credit = "";
		
		if(!CollectionUtils.isEmpty(list)) {
			userId = list.get(0).elementTextTrim("userId");
			type = list.get(0).elementTextTrim("type");
			credit = list.get(0).elementTextTrim("score");
		}
		
		if (StringUtils.isBlank(userId)) {
			response = Util.getResponseForFalse(xmlStr, head, "100", "参数传递错误");
			return response;
		}
		
		//积分入库积分记录表
		String insertStr = "credits.insertUserCredits";
		
		CreditsBean creditsBean = new CreditsBean();
		creditsBean.setId(UUID.randomUUID().toString());
		creditsBean.setCredit(credit);
		creditsBean.setCreditType(type);
		creditsBean.setChannelType("");
		creditsBean.setCreateTime(new Date());
		
		int i = creditsDao.insert(insertStr, creditsBean);
		if(i > 0) {
			Util.getResponseForTrue(head, "");
		}
		
		//对用户总积分处理
		String updateStr = "credits.updateUserTotalCredit";
		int j = creditsDao.update(updateStr, creditsBean);
		
		//返回正确结果
		response = Util.getResponseForTrue(head, "");
		
		
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
		
		String userId = "";
		
		if(!CollectionUtils.isEmpty(list)) {
			userId = list.get(0).elementTextTrim("userId");
		}
		
		if (StringUtils.isBlank(userId)) {
			response = Util.getResponseForFalse(xmlStr, head, "100", "参数传递错误");
			return response;
		}
		
		//获取用户积分记录
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		params.put("creditType", "2");
		
		String queryStr = "credits.retrieveCreditsRecords";
		List<Map<String, Object>> creditsRecordsList = creditsDao.getSearchList(queryStr, params);
		
		StringBuffer creditSb = new StringBuffer();
		
		//返回正确结果
		if(CollectionUtils.isEmpty(creditsRecordsList)) {
			response = Util.getResponseForFalse(xmlStr, head, "100", "没有对应提现记录");
			return response;
		} else {
			for(Map<String, Object> map : creditsRecordsList) {
				creditSb.append("<item>");
				creditSb.append("<createtime>" + map.get("createtime") + "</createtime>");
				creditSb.append("<credit>" + map.get("credit") + "</credit>");
				creditSb.append("</item>");
			}
			
			String encodeStr = creditSb.toString();
			try {
				encodeStr = Base64.encodeBytes(encodeStr.getBytes("UTF-8"));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			response = Util.getResponseForTrue(head, encodeStr);
			
		}
		
		logger.debug("exit CreditsServiceImpl.userCreditsSysn(String xmlStr, Head head) ");
		return response;
	}

}
