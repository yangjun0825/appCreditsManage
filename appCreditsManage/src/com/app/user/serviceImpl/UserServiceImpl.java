package com.app.user.serviceImpl;

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

import com.app.user.bean.UserBean;
import com.app.user.bean.UserFeedBackBean;
import com.app.user.service.UserService;
import com.app.util.MyBatisDao;
import com.app.util.Util;
import com.app.vo.Head;

/** 
* @ClassName: UserRegisterServiceImpl 
* @Description: 用户实现类
* @author yangjun junyang0825@gmail.com 
* @date 2013-12-28 上午10:50:44  
*/

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private MyBatisDao<Map<String, Object>> userDao;
	
	private Log logger = LogFactory.getLog(UserServiceImpl.class);	
	
	/* (非 Javadoc) 
	* <p>Title: userRegister</p> 
	* <p>Description: 用户注册</p> 
	* @param xmlStr
	* @param head
	* @return 
	* @see com.app.user.service.UserRegisterService#userRegister(java.lang.String) 
	*/
	@Override
	public String userRegister(String xmlStr, Head head) throws Exception {
		logger.debug("enter UserServiceImpl.userRegister(String xmlStr, Head head)");
		
		String response = "";
		
		List<Element> list = Util.getRequestDataByXmlStr(xmlStr, "svccont/pra/item");
		
		String account = "";
		String password = "";
		
		if(!CollectionUtils.isEmpty(list)) {
			account = list.get(0).elementTextTrim("accout");
			password = list.get(0).elementTextTrim("pwd");
		}
		
		if (StringUtils.isBlank(account) || StringUtils.isBlank(password)) {
			response = Util.getResponseForFalse(xmlStr, head, "102", "无效请求");
			return response;
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug("[account] = " + account + " [password] = " + password);
		}
		
		//查询该账号是否存在
		String queryStr = "user.retrieveUserInfo";
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("account", account);
		List<Map<String, Object>> userInfoList = userDao.getSearchList(queryStr, params);
		
		if(logger.isDebugEnabled()) {
			logger.debug("[userInfoList] = " + userInfoList);
			if(userInfoList != null) {
				logger.debug("[userInfoListSize] = " + userInfoList.size());
			}
		}
		
		//如果查询不到数据，说明该账号不存在，可以添加
		if(CollectionUtils.isNotEmpty(userInfoList)) {
			return Util.getResponseForFalse(xmlStr, head, "101", "该手机已经注册过，请直接登录");
		}
		
		//用户信息入库
		UserBean userBean = new UserBean();
		userBean.setAccount(account);
		userBean.setPassword(password);
		userBean.setTotalCredit("0");
		userBean.setCreateTime(new Date());
		String insertStr = "user.insertUser";
		int i = userDao.insert(insertStr, userBean);
		
		if(logger.isDebugEnabled()) {
			logger.debug("[registerDbInsertResult] = " + i);
		}
		
		if(i > 0) {
			//返回正确结果
			response = Util.getResponseForTrue(head, "");
		}
		
		logger.debug("exit UserServiceImpl.userRegister(String xmlStr, Head head)");
		return response;
	}

	/* (非 Javadoc) 
	* <p>Title: userLogin</p> 
	* <p>Description:用户登录处理 </p> 
	* @param xmlStr
	* @param head
	* @return 
	* @see com.app.user.service.UserService#userLogin(java.lang.String, com.app.vo.Head) 
	*/
	@Override
	public String userLogin(String xmlStr, Head head) throws Exception {
		logger.debug("enter UserServiceImpl.userLogin(String xmlStr, Head head)");
		
		String response = "";
		
		List<Element> list = Util.getRequestDataByXmlStr(xmlStr, "svccont/pra/item");
		
		String account = "";
		String pwd = "";
		
		if(CollectionUtils.isNotEmpty(list)) {
			account = list.get(0).elementTextTrim("accout");
			pwd = list.get(0).elementTextTrim("pwd");
		}
		
		if (StringUtils.isBlank(account) || StringUtils.isBlank(pwd)) {
			response = Util.getResponseForFalse(xmlStr, head, "102", "无效请求");
			return response;
		}
		
		//用户登录鉴权
		String queryStr = "user.retrieveUserInfo";
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("account", account);
		params.put("password", pwd);
		List<Map<String, Object>> userInfoList = userDao.getSearchList(queryStr, params);
		
		if(CollectionUtils.isEmpty(userInfoList)) {
			response = Util.getResponseForFalse(xmlStr, head, "103", "密码不匹配");
			return response;
		}
		
		StringBuffer userSb = new StringBuffer();
		
		for(Map<String, Object> map : userInfoList) {
			userSb.append("<credits>" + map.get("totalcredit") + "</credits>");
		}
		
		String encodeStr = userSb.toString();
		if(logger.isDebugEnabled()) {
			logger.debug("[encodeStr] = " + encodeStr);
		}
		
		response = Util.getResponseForTrue(head, encodeStr);
		
		if(logger.isDebugEnabled()) {
			logger.debug("[response] = " + response);
		}
		
		logger.debug("exit UserServiceImpl.userLogin(String xmlStr, Head head)");
		return response;
	}

	/* (非 Javadoc) 
	* <p>Title: userFeedBack</p> 
	* <p>Description: 保存用户反馈信息</p> 
	* @param xmlStr
	* @param head
	* @return 
	* @see com.app.user.service.UserService#userFeedBack(java.lang.String, com.app.vo.Head) 
	*/
	@Override
	public String userFeedBack(String xmlStr, Head head) throws Exception {
		logger.debug("enter UserServiceImpl.userFeedBack(String xmlStr, Head head) " + "[xmlStr] = " + xmlStr);
		
		String response = "";
		
		List<Element> list = Util.getRequestDataByXmlStr(xmlStr, "svccont/pra/item");
		
		String account = "";
		String content = "";
		
		if(CollectionUtils.isNotEmpty(list)) {
			account = list.get(0).elementTextTrim("accout");
			content = list.get(0).elementTextTrim("msg");
		}
		
		//保存用户反馈信息
		UserFeedBackBean feedBackBean = new UserFeedBackBean();
		feedBackBean.setId(UUID.randomUUID().toString());
		feedBackBean.setAccount(account);
		feedBackBean.setContent(content);
		feedBackBean.setCreateTime(new Date());
		
		String insertStr = "userFeedBack.insertUserFeedBack";
		int i = userDao.insert(insertStr, feedBackBean);
		
		if(logger.isDebugEnabled()) {
			logger.debug("[feedBackDbInsertResult] = " + i);
		}
		
		if(i > 0) {
			response = Util.getResponseForTrue(head, "");
		} else {
			response = Util.getResponseForFalse(xmlStr, head, "101", "反馈信息保存失败");
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug("[response] = " + response);
		}
		
		logger.debug("exit UserServiceImpl.userFeedBack(String xmlStr, Head head)");
		return response;
	}

}
