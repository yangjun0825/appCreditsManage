package com.app.user.serviceImpl;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
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
import com.app.user.bean.UserBean;
import com.app.user.bean.UserFeedBackBean;
import com.app.user.service.UserService;
import com.app.util.Constant;
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
		
		String version = head.getVersion();
		
		if(logger.isDebugEnabled()) {
			logger.debug("[version] = " + version);
		}
		
		if(StringUtils.isBlank(version)) {
			response = Util.getResponseForFalse(xmlStr, head, "102", "该版本爱攒钱已经不再支持，请重新下载新版爱攒钱，谢谢您的支持！");
			return response;
		}
		
		version = version.replace(".", "");
    	
    	int version_i = Integer.parseInt(version);
		
		if(version_i < 200) {
			response = Util.getResponseForFalse(xmlStr, head, "102", "该版本爱攒钱已经不再支持，请重新下载新版爱攒钱，谢谢您的支持！");
			return response;
		}
		
		List<Element> list = Util.getRequestDataByXmlStr(xmlStr, "svccont/pra/item");
		
		String account = "";
		String pwd = "";
		
		if(CollectionUtils.isNotEmpty(list)) {
			account = list.get(0).elementTextTrim("accout");
			pwd = list.get(0).elementTextTrim("pwd");
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug("[account] = " + account + " [pwd] = " + pwd);
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
		
		String state = (String)userInfoList.get(0).get("state");
		
		if(logger.isDebugEnabled()) {
			logger.debug("[userState] = " + state);
		}
		
		if(Constant.state_invalid.equals(state)) {
			response = Util.getResponseForFalse(xmlStr, head, "104", "帐号异常，请联系管理员！（可能因为黑名单）");
			return response;
		}
		
		StringBuffer userSb = new StringBuffer();
		
		for(Map<String, Object> map : userInfoList) {
			userSb.append("<credits>" + map.get("totalcredit") + "</credits>");
			userSb.append("<pendcredits>" + map.get("pendcredit") + "</pendcredits>");
			userSb.append("<zfbaccout>" + map.get("zfbaccount") + "</zfbaccout>");
			userSb.append("<telaccout>" + map.get("telaccount") + "</telaccout>");
			userSb.append("<qqaccout>" + map.get("qqaccount") + "</qqaccout>");
			userSb.append("<state>" + map.get("state") + "</state>");
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

	/* (非 Javadoc) 
	* <p>Title: userInfoModify</p> 
	* <p>Description:修改用户信息 </p> 
	* @param xmlStr
	* @param head
	* @return
	* @throws Exception 
	* @see com.app.user.service.UserService#userInfoModify(java.lang.String, com.app.vo.Head) 
	*/
	@Override
	public String userInfoModify(String xmlStr, Head head) throws Exception {
		logger.debug("enter UserServiceImpl.userFeedBack(String xmlStr, Head head)");
		
		String response = "";
		
		List<Element> list = Util.getRequestDataByXmlStr(xmlStr, "svccont/pra/item");
		
		String account = "";
		String zfbAccount  = "";
		String telAccount  = "";
		String qqAccount  = "";
		
		if(CollectionUtils.isNotEmpty(list)) {
			account = list.get(0).elementTextTrim("accout");
			zfbAccount = list.get(0).elementTextTrim("zfbaccout");
			telAccount = list.get(0).elementTextTrim("telaccout");
			qqAccount = list.get(0).elementTextTrim("qqaccout"); 
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug("[account] = " + account + " [zfbAccount] = " + zfbAccount 
					  + " [telAccount] = " + telAccount + " [qqAccount] = " + qqAccount);
		}
		
		String updateStr = "user.updateUserInfo";
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("account", account);
		params.put("createTime", new Date());
		
		
		if(StringUtils.isNotBlank(zfbAccount)) {
			params.put("zfbAccount", zfbAccount);
		}
		
		if(StringUtils.isNotBlank(telAccount)) {
			params.put("telAccount", telAccount);
		}
		
		if(StringUtils.isNotBlank(qqAccount)) {
			params.put("qqAccount", qqAccount);
		}
		
		int i = userDao.update(updateStr, params);
		
		if(logger.isDebugEnabled()) {
			logger.debug("[UserInfoDbUpdateResult] = " + i);
		}
		
		//获取用户信息
		String queryStr = "user.retrieveUserInfo";
		
		Map<String, Object> userParams = new HashMap<String, Object>();
		userParams.put("account", account);
		List<Map<String, Object>> userInfoList = userDao.getSearchList(queryStr, userParams);
		
		if(CollectionUtils.isEmpty(userInfoList)) {
			response = Util.getResponseForFalse(xmlStr, head, "103", "该账号不存在");
			return response;
		}
		
		StringBuffer userSb = new StringBuffer();
		
		for(Map<String, Object> map : userInfoList) {
			userSb.append("<accont>" + map.get("account") + "</accont>");
			userSb.append("<credits>" + map.get("totalcredit") + "</credits>");
			userSb.append("<pendcredits>" + map.get("pendCredit") + "</pendcredits>");
			userSb.append("<zfbaccout>" + map.get("zfbaccount") + "</zfbaccout>");
			userSb.append("<telaccout>" + map.get("telaccount") + "</telaccout>");
			userSb.append("<qqaccout>" + map.get("qqaccount") + "</qqaccout>");
		}
		
		String encodeStr = userSb.toString();
		if(logger.isDebugEnabled()) {
			logger.debug("[encodeStr] = " + encodeStr);
		}
		
		response = Util.getResponseForTrue(head, encodeStr);
		
		if(logger.isDebugEnabled()) {
			logger.debug("[response] = " + response);
		}
		
		
		logger.debug("exit UserServiceImpl.userFeedBack(String xmlStr, Head head)");
		return response;
	}

	/* (非 Javadoc) 
	* <p>Title: userAutoRegister</p> 
	* <p>Description: 用户自动注册</p> 
	* @param xmlStr
	* @param head
	* @return
	* @throws Exception 
	* @see com.app.user.service.UserService#userAutoRegister(java.lang.String, com.app.vo.Head) 
	*/
	@Override
	public String userAutoRegister(String xmlStr, Head head) throws Exception {
		logger.debug("enter UserServiceImpl.userAutoRegister(String xmlStr, Head head)");
		
		String response = "";
		
		List<Element> list = Util.getRequestDataByXmlStr(xmlStr, "svccont/pra/item");
		
		String imei = "";
		String imsi = "";
		String linkId  = "";
		String version = head.getVersion();
		
		if(logger.isDebugEnabled()) {
			logger.debug("[version] = " + version);
		}
		
		if(StringUtils.isBlank(version)) {
			response = Util.getResponseForFalse(xmlStr, head, "102", "该版本爱攒钱已经不再支持，请重新下载新版爱攒钱，谢谢您的支持！");
			return response;
		}
		
		version = version.replace(".", "");
    	
    	int version_i = Integer.parseInt(version);
		
		if(version_i < 200) {
			response = Util.getResponseForFalse(xmlStr, head, "102", "该版本爱攒钱已经不再支持，请重新下载新版爱攒钱，谢谢您的支持！");
			return response;
		}
		
		if(CollectionUtils.isNotEmpty(list)) {
			imei = list.get(0).elementTextTrim("imei");
			linkId = list.get(0).elementTextTrim("linkId");
			imsi = list.get(0).elementTextTrim("imsi");
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug("[imei] = " + imei + " [imsi] = " + imsi +" [linkId] = " + linkId);
		}
		
		if (StringUtils.isBlank(imei) || StringUtils.isBlank(imsi)) {
			response = Util.getResponseForFalse(xmlStr, head, "102", "无效请求");
			return response;
		}
		
		if(StringUtils.isNotBlank(linkId)) {
			linkId = Integer.valueOf(linkId, 16).toString();
		}
		
		//查询该IMEI是否存在
		String queryStr = "user.retrieveUserInfo";
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("imei", imei);
		List<Map<String, Object>> userInfoList = userDao.getSearchList(queryStr, params);
		
		if(logger.isDebugEnabled()) {
			logger.debug("[userInfoList] = " + userInfoList);
			if(userInfoList != null) {
				logger.debug("[userInfoListSize] = " + userInfoList.size());
			}
		}
		
		//如果查询到数据，说明该手机已经注册过
		if(CollectionUtils.isNotEmpty(userInfoList) && userInfoList.get(0) != null) {
			
			String state = (String)userInfoList.get(0).get("state");
			
			String userImsi = (String)userInfoList.get(0).get("imsi");
			
			if(logger.isDebugEnabled()) {
				logger.debug("[userState] = " + state + " [userImsi] = " + userImsi);
			}
			
			if(Constant.state_invalid.equals(state)) {
				response = Util.getResponseForFalse(xmlStr, head, "104", "帐号异常，请联系管理员！（可能因为黑名单）");
				return response;
			}
			
			StringBuffer userSb = new StringBuffer();
			
			for(Map<String, Object> map : userInfoList) {
				
				if(StringUtils.isNotBlank((String)map.get("account"))) {
					userSb.append("<accout>" + map.get("account") + "</accout>");
				} else {
					userSb.append("<accout></accout>");
				}
				
				if(StringUtils.isNotBlank((String)map.get("totalcredit"))) {
					userSb.append("<credits>" + map.get("totalcredit") + "</credits>");
				} else {
					userSb.append("<credits></credits>");
				}
				
				userSb.append("<jlcredit></jlcredit>");
				
				if(StringUtils.isNotBlank((String)map.get("pendcredit"))) {
					userSb.append("<pendcredits>" + map.get("pendcredit") + "</pendcredits>");
				} else {
					userSb.append("<pendcredits></pendcredits>");
				}
				
				if(StringUtils.isNotBlank((String)map.get("zfbaccount"))) {
					userSb.append("<zfbaccout>" + map.get("zfbaccount") + "</zfbaccout>");
				} else {
					userSb.append("<zfbaccout></zfbaccout>");
				}
				
				if(StringUtils.isNotBlank((String)map.get("telaccount"))) {
					userSb.append("<telaccout>" + map.get("telaccount") + "</telaccout>");
				} else {
					userSb.append("<telaccout></telaccout>");
				}
				
				if(StringUtils.isNotBlank((String)map.get("qqaccount"))) {
					userSb.append("<qqaccout>" + map.get("qqaccount") + "</qqaccout>");
				} else {
					userSb.append("<qqaccout></qqaccout>");
				}
				
				if(StringUtils.isNotBlank((String)map.get("state"))) {
					userSb.append("<state>" + map.get("state") + "</state>");
				} else {
					userSb.append("<state>1</state>");
				}
				
			}
			
			String encodeStr = userSb.toString();
			if(logger.isDebugEnabled()) {
				logger.debug("[encodeStr] = " + encodeStr);
			}
			
			return Util.getResponseForTrue(head, encodeStr);
		}
		
		//生成一个8位的随机数字账号
		HashSet<Integer> set = new HashSet<Integer>();  
	    Util.randomSet(10000000,99999999,1,set);
	    
	    int accountRandom = 0;
	    
	    for (int j : set) {  
	    	accountRandom = j;
	    }  
		
	    if(logger.isDebugEnabled()) {
	    	logger.debug("[accountRandom] = " + accountRandom);
	    }
	    
	    //用户信息入库
		UserBean userBean = new UserBean();
		userBean.setAccount(accountRandom + "");
		userBean.setPassword("aizanqi");
		userBean.setTotalCredit("10");
		userBean.setImei(imei);
		userBean.setImsi(imsi);
		userBean.setLinkId(linkId);
		
		userBean.setCreateTime(new Date());
		String insertStr = "user.insertUser";
		int i = userDao.insert(insertStr, userBean);
		
		if(logger.isDebugEnabled()) {
			logger.debug("[registerDb2InsertResult] = " + i);
		}
	    
		//如果数据插入成功，则返回信息,有上线的话，同时增加上线的奖励积分
		if(i > 0) {
			StringBuffer userSb = new StringBuffer();
			userSb.append("<accout>" + accountRandom + "</accout>");
			userSb.append("<credits>10</credits>");
			userSb.append("<jlcredit>10</jlcredit>");
			userSb.append("<pendcredits></pendcredits>");
			userSb.append("<zfbaccout></zfbaccout>");
			userSb.append("<telaccout></telaccout>");
			userSb.append("<qqaccout></qqaccout>");
			userSb.append("<state>1</state>");
				
			String encodeStr = userSb.toString();
			if(logger.isDebugEnabled()) {
				logger.debug("[encodeStr] = " + encodeStr);
			}
			
			response = Util.getResponseForTrue(head, encodeStr);
			
			if(logger.isDebugEnabled()) {
				logger.debug("[response] = " + response);
			}
			
			if(StringUtils.isNotBlank(linkId) && !"10000".equals(linkId)) {
				
				//先查询上线用户是否存在，存在才给上线增加积分
				//查询该账号是否存在
				String queryUserStr = "user.retrieveUserInfo";
				
				Map<String, Object> paramsUser = new HashMap<String, Object>();
				paramsUser.put("account", linkId);
				List<Map<String, Object>> userList = userDao.getSearchList(queryUserStr, paramsUser);
				
				if(logger.isDebugEnabled()) {
					logger.debug("[userList] = " + userList);
					if(userList != null) {
						logger.debug("[userListSize] = " + userList.size());
					}
				}
				
				if(userList != null && userList.size() > 0) {
					//获取用户所有下线总数
					String queryStr3 = "user.retrieveUserInfo";
					
					Map<String, Object> params3 = new HashMap<String, Object>();
					params3.put("linkId", linkId);
					List<Map<String, Object>> totalUserInfoList = userDao.getSearchList(queryStr3, params3);
					
					if(logger.isDebugEnabled()) {
						logger.debug("[totalUserInfoList] = " + totalUserInfoList);
						if(userInfoList != null) {
							logger.debug("[totalUserInfoListSize] = " + totalUserInfoList.size());
						}
					}
					
					//如果用户的下线没有或者下线总数为奇数，则将上线总积分增加20
					if(CollectionUtils.isEmpty(totalUserInfoList) || totalUserInfoList.size() % 2 == 1) {
						//积分入库积分记录表
						String insertStr2 = "credits.insertUserCredits";
						
						CreditsBean creditsBean = new CreditsBean();
						creditsBean.setId(UUID.randomUUID().toString());
						creditsBean.setAccount(linkId);
						creditsBean.setCredit(Constant.award_first_install);
						creditsBean.setCreditType(Constant.add_credit);
						creditsBean.setChannelType(Constant.channelType_invite);
						creditsBean.setCreateTime(new Date());
						
						int i2 = userDao.insert(insertStr2, creditsBean);
						
						if(logger.isDebugEnabled()) {
							logger.debug("[creditDb2InsertResult] = " + i2);
						}
						
						//对用户总积分处理，增加用户总积分
						String updateStr2 = "credits.updateUserTotalCreditForAdd";
						Map<String, Object> params2 = new HashMap<String, Object>();
						params2.put("credit", Constant.award_first_install);
						params2.put("account", linkId);
						int j = userDao.update(updateStr2, params2);
						
						if(logger.isDebugEnabled()) {
							logger.debug("[creditDbUpdateResult] = " + j);
						}
					}
				}
			}
		}
		
		logger.debug("exit UserServiceImpl.userAutoRegister(String xmlStr, Head head)");
		return response;
	}
}
