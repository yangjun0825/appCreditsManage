package com.app.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

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
	@RequestMapping("showCreditWdList.do")
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
	* @Title: retrieveCreditWithDrawDetailList 
	* @Description: 获取提现详细信息
	* @param    设定文件 
	* @return ModelAndView    返回类型 
	* @throws 
	*/
	@RequestMapping("showCreditWdDetailList.do")
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
		WithDrawBean bean = new WithDrawBean();
		bean.setAccount("1212121");
		bean.setCashType("1");
		bean.setCashAccount("aasdasdf");
		wdCreditsList.add(bean);
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
	@RequestMapping("withDraw.do")
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
}
