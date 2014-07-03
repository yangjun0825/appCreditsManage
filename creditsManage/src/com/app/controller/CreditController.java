package com.app.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;


import com.app.credits.bean.CreditsBean;
import com.app.credits.bean.WithDrawBean;
import com.app.service.CreditService;
import com.app.service.UserService;
import com.app.user.bean.UserBean;
import com.app.util.Constant;
import com.app.util.SpringUtils;

@Controller
@RequestMapping("credit")
public class CreditController {

	@Autowired
	private UserService userService;
	
	@Autowired
	private CreditService creditService;
	
	private Log logger = LogFactory.getLog(CreditController.class);	
	
	/** 
	* @Title: showCreditWithDrawList 
	* @Description: 展示提现列表
	* @return ModelAndView    返回类型 
	* @throws 
	*/
	@RequestMapping("showCreditWdList.apk")
	public ModelAndView showCreditWithDrawList() {
		
		logger.debug("enter CreditController.showCreditWithDrawList()");
		
		ModelAndView mav = new ModelAndView();
		
		//获取用户待体现记录
		Map<String, Object> params = new HashMap<String, Object>();
		List<UserBean> userList = userService.retrieveUserInfoList(params);
		
		mav.addObject("userList", userList);
		
		mav.setViewName("/view/creditWithDrawList");
		
		logger.debug("exit CreditController.showCreditWithDrawList()");
		return mav;
	}
	
	/** 
	* @Title: showCreditAddInfoList 
	* @Description: 获取用户增加积分记录
	* @return ModelAndView    返回类型 
	* @throws 
	*/
	@RequestMapping("showCreditAddList.apk")
	public ModelAndView showCreditAddInfoList(HttpServletRequest request) {
		
		logger.debug("enter CreditController.showCreditAddInfoList()");
		
		String account = request.getParameter("account");
		
		if(logger.isDebugEnabled()) {
			logger.debug("[account] = " + account);
		}
		
		ModelAndView mav = new ModelAndView();
		
		//获取用户待体现记录
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("creditType", Constant.add_credit);
		params.put("account", account);
		List<CreditsBean> creditsList = creditService.retrieveCreditRecordListByCondition(params);
		
		mav.addObject("creditsList", creditsList);
		
		mav.setViewName("/view/creditAddDetailList");
		
		logger.debug("exit CreditController.showCreditWithDrawList()");
		return mav;
	}
	
	/** 
	* @Title: retrieveCreditWithDrawDetailList 
	* @Description: 获取提现详细信息
	* @param    设定文件 
	* @return ModelAndView    返回类型 
	* @throws 
	*/
	@RequestMapping("showCreditWdDetailList.apk")
	public ModelAndView retrieveCreditWithDrawDetailList(HttpServletRequest request) {
		
		logger.debug("enter CreditController.retrieveCreditWithDrawDetailList()");
		
		String account = request.getParameter("account");
		
		if(logger.isDebugEnabled()) {
			logger.debug("[account] = " + account);
		}
		
		ModelAndView mav = new ModelAndView();
		
		//获取用户待提现记录
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("account",account);
		params.put("isComplete",Constant.not_complete_withdraw);
		List<WithDrawBean> wdCreditsList = creditService.retrieveCreditWitHDrawList(params);
		
		if(CollectionUtils.isNotEmpty(wdCreditsList)) {
			for(WithDrawBean bean : wdCreditsList) {
				String credit = bean.getCredit();
				
				if(StringUtils.isNotBlank(credit)) {
					double credit_d = Double.parseDouble(credit);
					credit_d = credit_d/100;
					bean.setCredit(credit_d+"");
				}
			}
		}
		
		mav.addObject("wdCreditsList", wdCreditsList);
		
		mav.setViewName("/view/creditWithDrawDetailList");
		
		logger.debug("exit CreditController.retrieveCreditWithDrawDetailList()");
		return mav;
	}
	
	
	/** 
	* @Title: completeWithDraw 
	* @Description: 完成提现 
	* @return void    返回类型 
	* @throws 
	*/
	@RequestMapping("withDraw.apk")
	public void completeWithDraw(HttpServletRequest request, HttpServletResponse response) {
		logger.debug("enter CreditController.completeWithDraw(HttpServletRequest request, HttpServletResponse response)");
		
		String account = request.getParameter("account");
		
		String id = request.getParameter("id");
		
		String credit = request.getParameter("credit");
		
		if(logger.isDebugEnabled()) {
			logger.debug("[account] = " + account + " [id] = " + id + " [credit] = " + credit);
		}
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("account", account);
		params.put("id", id);
		params.put("credit", credit);
		String withDrawResult = creditService.completeWithDraw(params);
		
		if(logger.isDebugEnabled()) {
			logger.debug("[withDrawResult] = " + withDrawResult);
		}
		
		SpringUtils.renderText(response, withDrawResult);
		
		logger.debug("exit CreditController.completeWithDraw(HttpServletRequest request, HttpServletResponse response)");
	}
	
	/** 
	* @Title: retrieveUserCreditRecordByMac 
	* @Description: 查询mac地址对应的所有积分
	* @return ModelAndView    返回类型 
	* @throws 
	*/
	@RequestMapping("macCredit.apk")
	public ModelAndView retrieveUserCreditRecordByMac() {
		
		logger.debug("enter CreditController.retrieveUserCreditRecordByMac()");
		
		ModelAndView mav = new ModelAndView();
		
		//获取mac地址对应的积分记录
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("creditType", Constant.add_credit);
		List<CreditsBean> creditsMacList = creditService.retrieveCreditByMacList(params);
		
		mav.addObject("creditsMacList", creditsMacList);
		
		mav.setViewName("/view/creditMacList");
		
		logger.debug("exit CreditController.retrieveUserCreditRecordByMac()");
		return mav;
	}
	
	/** 
	* @Title: retrieveUserCreditListByCondtion 
	* @Description: 根据条件获取用户账户信息
	* @return ModelAndView    返回类型 
	* @throws 
	*/
	@RequestMapping("userInfo.apk")
	public ModelAndView retrieveUserInfoListByCondtion(HttpServletRequest request) {
		logger.debug("enter CreditController.retrieveUserCreditListByCondtion(HttpServletRequest requestty)");
		
		String macAddress = request.getParameter("macAddress");
		if(logger.isDebugEnabled()) {
			logger.debug("[macAddress] = " + macAddress);
		}
		
		//获取用户信息
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("macAddress", macAddress);
		List<UserBean> userList = userService.retrieveUserInfoList(params);
		ModelAndView mav = new ModelAndView();
		mav.addObject("userList", userList);
		
		logger.debug("exit CreditController.retrieveUserCreditListByCondtion(HttpServletRequest requestty)");
		
		mav.setViewName("/view/userInfoList");	
		return mav;
	}
	
	/** 
	* @Title: retrieveUserCreditCount 
	* @Description: 获取所有用户当天的所有增加积分
	* @return ModelAndView    返回类型 
	* @throws 
	*/
	@RequestMapping("accCreditCount.apk")
	public ModelAndView retrieveUserCreditCount() {
		
		logger.debug("enter CreditController.retrieveUserCreditCount()");
		
		ModelAndView mav = new ModelAndView();
		
		//获取账户对应的积分记录
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("creditType", Constant.add_credit);
		List<CreditsBean> creditsMacList = creditService.retrieveUserCreditCount(params);
		
		mav.addObject("creditsCountList", creditsMacList);
		
		mav.setViewName("/view/creditAddAccList");
		
		logger.debug("exit CreditController.retrieveUserCreditCount()");
		return mav;
	}
	
	/** 
	* @Title: showAccAddCreditList 
	* @Description: 展示积分的详细增加信息
	* @param request
	* @return ModelAndView    返回类型 
	* @throws 
	*/
	@RequestMapping("showAccAddCreditList.apk")
	public ModelAndView showAccAddCreditList(HttpServletRequest request) {
		
		logger.debug("enter CreditController.showAccAddCreditList()");
		
		String account = request.getParameter("account");
		
		if(logger.isDebugEnabled()) {
			logger.debug("[account] = " + account);
		}
		
		ModelAndView mav = new ModelAndView();
		
		//获取用户待提现记录
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("account", account);
		params.put("creditType", Constant.add_credit);
		List<CreditsBean> creditsList = creditService.retrieveCreditRecordListByCondition(params);
		
		mav.addObject("creditsList", creditsList);
		
		mav.setViewName("/view/creditAddInfoList");
		
		
		logger.debug("exit CreditController.showAccAddCreditList()");
		return mav;
	}
}
