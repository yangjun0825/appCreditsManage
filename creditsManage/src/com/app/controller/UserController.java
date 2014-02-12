package com.app.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.app.service.UserService;
import com.app.user.bean.UserBean;
import com.app.util.Constant;
import com.app.util.SpringUtils;

@Controller
@RequestMapping("user")
public class UserController {
	
	@Autowired
	private UserService userService;
	
	private Log logger = LogFactory.getLog(UserController.class);	
	
	/** 
	* @Title: userLogin 
	* @Description: 登录处理
	* @param  request
	* @param  response    设定文件 
	* @return void    返回类型 
	* @throws 
	*/
	@RequestMapping("login.do")
	public void userLogin(HttpServletRequest request, HttpServletResponse response) {
		
		logger.debug("enter UserController.userLogin(HttpServletRequest request, HttpServletResponse response)");
		
		String account = request.getParameter("account");
		
		String password = request.getParameter("password");
		
		if(logger.isDebugEnabled()) {
			logger.debug("[account] = " + account + " [password] = " + password);
		}
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("account", account);
		params.put("password", password);
		
		List<UserBean> userList= userService.retrieveUserInfoList(params);
		
		if(CollectionUtils.isNotEmpty(userList)) {
			SpringUtils.renderText(response, Constant.success);
		} else {
			SpringUtils.renderText(response, Constant.failure);
		}
		
		logger.debug("exit UserController.userLogin(HttpServletRequest request, HttpServletResponse response)");
	}
	
	/** 
	* @Title: indexPage 
	* @Description: 跳转登录首页 
	* @return ModelAndView    返回类型 
	* @throws 
	*/
	@RequestMapping("indexPage.do")
	public ModelAndView indexPage() {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("/view/indexPage");
		return mav;
	}
	
	/** 
	* @Title: freezeUser 
	* @Description: 冻结用户 
	* @param  request
	* @param  response    
	* @return void    返回类型 
	* @throws 
	*/
	@RequestMapping("freezeUser.do")
	public void freezeUser(HttpServletRequest request, HttpServletResponse response) {
		
		logger.debug("enter UserController.freezeUser(HttpServletRequest request, HttpServletResponse response)");
		
		String account = request.getParameter("account");
		
		if(logger.isDebugEnabled()) {
			logger.debug("[account] = " + account);
		}
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("account", account);
		
		String result = userService.freezeUser(params);
		
		if(Constant.success.equals(result)) {
			SpringUtils.renderText(response, Constant.success);
		} else {
			SpringUtils.renderText(response, Constant.failure);
		}
		
		logger.debug("exit UserController.freezeUser(HttpServletRequest request, HttpServletResponse response)");
	}
	
}
