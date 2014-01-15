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
import com.app.credits.bean.WithDrawBean;
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
		String cashType = "";
		String cashAccount = "";
		
		if(CollectionUtils.isNotEmpty(list)) {
			account = list.get(0).elementTextTrim("accout");
			type = list.get(0).elementTextTrim("type");
			credit = list.get(0).elementTextTrim("credit");
			cashType = list.get(0).elementTextTrim("cashtype");
			cashAccount = list.get(0).elementTextTrim("cashaccout");
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug("[account] = " + account + " [type] = " + type + " [credit] = " + credit 
					  + " [cashType] = " + cashType + " [cashAccount] = " + cashAccount);
		}
		
		if (StringUtils.isBlank(account) || StringUtils.isBlank(type)
				|| StringUtils.isBlank(credit)) {
			response = Util.getResponseForFalse(xmlStr, head, "102", "无效请求");
			return response;
		}
		
		if(Constant.withdraw_credit.equals(type)) {
			if(StringUtils.isBlank(cashAccount) || StringUtils.isBlank(cashType)) {
				response = Util.getResponseForFalse(xmlStr, head, "102", "无效请求");
				return response;
			}
		}
		
		//积分类型 1 增加 2 提现请求
		//如果是增加积分，则直接入库,同时将总积分增加
		if(Constant.add_credit.equals(type)) {
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
			
			//对用户总积分处理，增加用户总积分
			String updateStr = "credits.updateUserTotalCreditForAdd";
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("credit", credit);
			params.put("account", account);
			int j = creditsDao.update(updateStr, params);
			
			if(logger.isDebugEnabled()) {
				logger.debug("[creditDbUpdateResult] = " + j);
			}
			
		} else {
			//如果是提现请求积分，则先判断总积分是否足够
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
					
					//积分入库积分记录表
					String insertStr = "creditsWithDraw.insertUserWdCredits";
					
					WithDrawBean withDrawBean = new WithDrawBean();
					withDrawBean.setId(UUID.randomUUID().toString());
					withDrawBean.setAccount(account);
					withDrawBean.setCredit(credit);
					withDrawBean.setCashType(cashType);
					withDrawBean.setCashAccount(cashAccount);
					withDrawBean.setIsComplete(Constant.not_complete_withdraw);
					withDrawBean.setCreateTime(new Date());
					
					int i = creditsDao.insert(insertStr, withDrawBean);
					
					if(logger.isDebugEnabled()) {
						logger.debug("[creditDbInsertWsResult] = " + i);
					}
					
					if(i > 0) {
						response = Util.getResponseForTrue(head, "");
					}
					
					//对用户总积分处理,减去相应的用户总积分,同时增加待提现积分
					String updateStr = "credits.updateUserTotalCreditForWithDraw";
					Map<String, Object> params = new HashMap<String, Object>();
					params.put("credit", credit);
					params.put("account", account);
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
			
		}
		
		//同步之后再次查询用户总积分
		String queryStrAfSysn = "user.retrieveUserInfo";
		
		Map<String, Object> userParamsAfSysn = new HashMap<String, Object>();
		userParamsAfSysn.put("account", account);
		List<Map<String, Object>> userInfoAfSysnList = creditsDao.getSearchList(queryStrAfSysn, userParamsAfSysn);
		
		if(logger.isDebugEnabled()) {
			logger.debug("[userInfoAfSysnList] = " + userInfoAfSysnList);
			if(userInfoAfSysnList != null) {
				logger.debug("[userInfoAfSysnListSize] = " + userInfoAfSysnList.size());
			}
		}
		
		if(CollectionUtils.isNotEmpty(userInfoAfSysnList)) {
			StringBuffer userSb = new StringBuffer();
			
			for(Map<String, Object> map : userInfoAfSysnList) {
				userSb.append("<credits>" + map.get("totalcredit") + "</credits>");
				userSb.append("<pendcredits>" + map.get("pendcredit") + "</pendcredits>");
			}
			
			String encodeStr = userSb.toString();
			if(logger.isDebugEnabled()) {
				logger.debug("[encodeStr] = " + encodeStr);
			}
			
			response = Util.getResponseForTrue(head, encodeStr);
		} else {
			String encodeStr = "<credits></credits><pendcredits></pendcredits>";
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
		params.put("isComplete", Constant.not_complete_withdraw);
		
		String queryStr = "creditsWithDraw.retrieveWdCreditsRecords";
		List<Map<String, Object>> creditsRecordsList = creditsDao.getSearchList(queryStr, params);
		
		if(logger.isDebugEnabled()) {
			logger.debug("[creditsRecordsList] = " + creditsRecordsList);
		}
		
		StringBuffer creditSb = new StringBuffer();
		creditSb.append("<contentitem>");
		
		//返回正确结果
		if(CollectionUtils.isNotEmpty(creditsRecordsList)) {
			for(Map<String, Object> map : creditsRecordsList) {
				creditSb.append("<item>");
				creditSb.append("<time>" + map.get("createtime") + "</time>");
				creditSb.append("<credit>" + map.get("credit") + "</credit>");
				creditSb.append("<cashtype>" + map.get("cashtype") + "</cashtype>");
				creditSb.append("<cashaccount>" + map.get("cashaccount") + "</cashaccount>");
				creditSb.append("<comtype>" + map.get("iscomplete") + "</comtype>");
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

	/* (非 Javadoc) 
	* <p>Title: userWithdrawProcess</p> 
	* <p>Description: 处理用户提现请求</p> 
	* @param xmlStr
	* @param head
	* @return
	* @throws Exception 
	* @see com.app.credits.service.CreditsService#userWithdrawProcess(java.lang.String, com.app.vo.Head) 
	*/
	@Override
	public String userWithdrawProcess(String xmlStr, Head head) throws Exception {
		logger.debug("enter CreditsServiceImpl.userWithdrawProcess(String xmlStr, Head head) " + "[xmlStr] = " + xmlStr);
		
		String response = "";
		
		List<Element> list = Util.getRequestDataByXmlStr(xmlStr, "svccont/pra/item");
		
		String type = "";
		
		String account = "";
		
		String money = "";
		
		String zfbAccount = "";
		
		if(!CollectionUtils.isEmpty(list)) {
			type = list.get(0).elementTextTrim("type");
			account = list.get(0).elementTextTrim("accout");
			money = list.get(0).elementTextTrim("money");
			zfbAccount = list.get(0).elementTextTrim("zfbaccout");
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug("[account] = " + account + " [money] = " + money 
					  + " [type] = " + type + " [zfbAccount] = " + zfbAccount);
		}
		
		if(StringUtils.isBlank(type) || StringUtils.isBlank(account) 
				|| StringUtils.isBlank(money) || StringUtils.isBlank(zfbAccount)) {
			response = Util.getResponseForFalse(xmlStr, head, "102", "无效请求");
			return response;
		}
		
		//如果请求类型为type=2,表示操作是完成提现，则只需更新提现记录即可
//		if(Constant.withdraw_complete.equals(type)) {
//			String updateWdStr = "credits.updateUserWithDrawMoney";
//			Map<String, Object> paramUps = new HashMap<String, Object>();
//			paramUps.put("credit", 0);
//			paramUps.put("account", account);
//			paramUps.put("creditType", Constant.withdraw_credit);
//			int k = creditsDao.update(updateWdStr, paramUps);
//			
//			if(logger.isDebugEnabled()) {
//				logger.debug("[creditDbWDUpdatResult] = " + k);
//			}
//			
//			if(k > 0) {
//				return Util.getResponseForTrue(head, "");
//			} else {
//				return Util.getResponseForFalse(xmlStr, head, "106", "无有效的提现请求");
//			}
//		}
		
		
		//用户提现请求，先查询用户总积分
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
		
		if(CollectionUtils.isNotEmpty(userInfoList)) {
			String totalCredit = (String)userInfoList.get(0).get("totalcredit");
			
			int totalCredit_i = Integer.parseInt(totalCredit);
			
			int money_i = Integer.parseInt(money);
			
			if(logger.isDebugEnabled()) {
				logger.debug("[totalCredit] = " + totalCredit + " [totalCredit_i] = " + totalCredit_i + " [money_i] = " + money_i);
			} 
			
			if(totalCredit_i - money_i >= 0 ) {
				
				//将支付宝账户更新到用户表中
				String updateZfbStr = "credits.updateUserInfo";
				Map<String, Object> paramZfbs = new HashMap<String, Object>();
				paramZfbs.put("zfbAccount", zfbAccount);
				paramZfbs.put("account", account);
				int m = creditsDao.update(updateZfbStr, paramZfbs);
				
				if(logger.isDebugEnabled()) {
					logger.debug("[creditDbZfbUpdateResult] = " + m);
				}
				
				//当积分足够时，将总积分减去用户提现请求积分
				String updateStr = "credits.updateUserTotalCreditForMin";
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("credit", money);
				params.put("account", account);
				int j = creditsDao.update(updateStr, params);
				
				if(logger.isDebugEnabled()) {
					logger.debug("[creditDbUpdateResult] = " + j);
				}
				
				//然后在积分表中插入一条提现记录,如果再次提现，则只更新记录
				
				//先查询提现记录，有，则更新，无，则插入
				//获取用户积分记录
				Map<String, Object> paramWds = new HashMap<String, Object>();
				paramWds.put("account", account);
				paramWds.put("creditType", Constant.withdraw_credit);
				
				String queryWdStr = "credits.retrieveCreditsRecords";
				List<Map<String, Object>> creditsRecordsList = creditsDao.getSearchList(queryWdStr, paramWds);
				
				if(logger.isDebugEnabled()) {
					logger.debug("[creditsRecordsList] = " + creditsRecordsList);
				}
				
				if(CollectionUtils.isEmpty(creditsRecordsList)) {
					String insertStr = "credits.insertUserCredits";
					
					CreditsBean creditsBean = new CreditsBean();
					creditsBean.setId(UUID.randomUUID().toString());
					creditsBean.setAccount(account);
					creditsBean.setCredit(money);
					creditsBean.setCreditType(Constant.withdraw_credit);
					creditsBean.setChannelType("");
					creditsBean.setCreateTime(new Date());
					
					int i = creditsDao.insert(insertStr, creditsBean);
					
					if(logger.isDebugEnabled()) {
						logger.debug("[creditDbInsertResult] = " + i);
					}
					
					if(i > 0 && j > 0) {
						response = Util.getResponseForTrue(head, "");
					}
				} else {
					String updateWdStr = "credits.updateUserWithDrawMoney";
					Map<String, Object> paramUps = new HashMap<String, Object>();
					paramUps.put("credit", money);
					paramUps.put("account", account);
					paramUps.put("creditType", Constant.withdraw_credit);
					int k = creditsDao.update(updateWdStr, paramUps);
					
					if(logger.isDebugEnabled()) {
						logger.debug("[creditDbWDUpdatResult] = " + k);
					}
					
					if(j > 0 && k >0) {
						response = Util.getResponseForTrue(head, "");
					}
				}
				
			} else {
				response = Util.getResponseForFalse(xmlStr, head, "105", "账余额不足，无法提现");
				return response;
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
			if(userInfoAfSysnList != null) {
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
		
		
		logger.debug("exit CreditsServiceImpl.userWithdrawProcess(String xmlStr, Head head) " + "[xmlStr] = " + xmlStr);
		return response;
	}

}
