package com.app.serviceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.service.UserService;
import com.app.user.bean.UserBean;
import com.app.util.MyBatisDao;
import com.app.util.Util;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private MyBatisDao<Map<String, Object>> userDao;
	
	private Log logger = LogFactory.getLog(UserServiceImpl.class);	
	
	/* (非 Javadoc) 
	* <p>Title: userLogin</p> 
	* <p>Description:获取用户信息 </p> 
	* @param params
	* @return 
	* @see com.app.service.UserService#userLogin(java.util.Map) 
	*/
	public List<UserBean> retrieveUserInfoList(Map<String, Object> params) {
		logger.debug("enter UserServiceImpl.userLogin(Map<String, Object> params)");
		
		List<UserBean> userList = new ArrayList<UserBean>();
		
		//用户登录鉴权
		String queryStr = "user.retrieveUserInfo"; 
		List<Map<String, Object>> userInfoList = userDao.getSearchList(queryStr, params);
		
		if(logger.isDebugEnabled()) {
			logger.debug("[userInfoList] = " + userInfoList);
		}
		
		if(CollectionUtils.isNotEmpty(userInfoList)) {
			
			for(Map<String, Object> map : userInfoList) {
				UserBean userBean = new UserBean();
				
				userBean.setAccount((String)map.get("account"));
				userBean.setPassword((String)map.get("password"));
				userBean.setPendCredit((String)map.get("pendcredit"));
				
				userList.add(userBean);
			}
			
		}
		
		
		logger.debug("exit UserServiceImpl.userLogin(Map<String, Object> params)");
		return userList;
	}

}
