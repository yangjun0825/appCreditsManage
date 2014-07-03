package com.app.credits.serviceImpl;

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
import org.springframework.util.ObjectUtils;

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
		String macAddress = "";
		String channelType = "";
		
		if(CollectionUtils.isNotEmpty(list)) {
			account = list.get(0).elementTextTrim("accout");
			type = list.get(0).elementTextTrim("type");
			credit = list.get(0).elementTextTrim("credit");
			cashType = list.get(0).elementTextTrim("cashtype");
			cashAccount = list.get(0).elementTextTrim("cashaccout");
			macAddress = list.get(0).elementTextTrim("macaddress");
			channelType = list.get(0).elementTextTrim("creditType");
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug("[account] = " + account + " [type] = " + type + " [credit] = " + credit 
					  + " [cashType] = " + cashType + " [cashAccount] = " + cashAccount
					  + " [macAddress] = " + macAddress + " [channelType] = " + channelType);
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
			creditsBean.setChannelType(channelType);
			creditsBean.setMacAddress(macAddress);
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
			
			//如果用户增加积分大于150，则表示该用户在刷积分，将用户状态更改为无效
//			if(Integer.parseInt(credit) > 150) {
//				
//				String update2Str = "user.updateUserInfo";
//				
//				Map<String, Object> params2 = new HashMap<String, Object>();
//				params2.put("account", account);
//				params2.put("state", Constant.state_invalid);
//				params2.put("createTime", new Date());
//				
//				int i2 = creditsDao.update(update2Str, params2);
//				
//				if(logger.isDebugEnabled()) {
//					logger.debug("[UserInfoDb2UpdateResult] = " + i2);
//				}
//				
//				response = Util.getResponseForFalse(xmlStr, head, "104", "帐号异常，请联系管理员！（可能因为黑名单）");
//				return response;
//			}
			
			if(Constant.channelType_load.equals(channelType)) {
				//查询账号信息
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
				
				if(CollectionUtils.isNotEmpty(userInfoList) && userInfoList.get(0) != null) {
					//获取上线信息，如果上线信息不为空，则上线也要增加积分
					String linkId = (String)userInfoList.get(0).get("linkid");
					
					if(logger.isDebugEnabled()) {
						logger.debug("[linkId] = " + linkId);
					}
					
					if(StringUtils.isNotBlank(linkId) && !"10000".equals(linkId.trim())) {
						//获取用户积分记录
						Map<String, Object> paramWds = new HashMap<String, Object>();
						paramWds.put("account", account);
						paramWds.put("creditType", Constant.add_credit);
						paramWds.put("channelType", channelType);
						
						String queryWdStr = "credits.retrieveCreditsRecords";
						List<Map<String, Object>> creditsRecordsList = creditsDao.getSearchList(queryWdStr, paramWds);
						
						if(logger.isDebugEnabled()) {
							logger.debug("[creditsRecordsList] = " + creditsRecordsList);
						}
						
						if(CollectionUtils.isNotEmpty(creditsRecordsList)) {
							//如果第一次增加积分，则该用户上线增加30,第二次增加50，第三次增加50
							if(creditsRecordsList.size() <= 2) {
								String insertStr2 = "credits.insertUserCredits";
								
								CreditsBean creditsBean2 = new CreditsBean();
								creditsBean2.setId(UUID.randomUUID().toString());
								creditsBean2.setAccount(linkId);
								
								if(creditsRecordsList.size() == 1) {
									creditsBean2.setCredit(Constant.award_first_load);
								} 
								
								if(creditsRecordsList.size() == 2) {
									creditsBean2.setCredit(Constant.award_second_load);
								}
								
								creditsBean2.setCreditType(type);
								creditsBean2.setChannelType(Constant.channelType_invite);
								creditsBean2.setCreateTime(new Date());
								
								int i2 = creditsDao.insert(insertStr2, creditsBean2);
								
								if(logger.isDebugEnabled()) {
									logger.debug("[creditDb2InsertResult] = " + i2);
								}
								
								//对用户总积分处理，增加用户总积分
								String updateStr2 = "credits.updateUserTotalCreditForAdd";
								Map<String, Object> params2 = new HashMap<String, Object>();
								
								if(creditsRecordsList.size() == 1) {
									params2.put("credit", Constant.award_first_load);
								} 
								
								if(creditsRecordsList.size() == 2) {
									params2.put("credit", Constant.award_second_load);
								}
								
								params2.put("account", linkId);
								int j2 = creditsDao.update(updateStr2, params2);
								
								if(logger.isDebugEnabled()) {
									logger.debug("[creditDb2UpdateResult] = " + j2);
								}
							}
						}
					}
				}
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
			if(CollectionUtils.isNotEmpty(userInfoList) && userInfoList.get(0) != null) {
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
		//params.put("isComplete", Constant.not_complete_withdraw);
		
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
		
		if(CollectionUtils.isNotEmpty(userInfoList) && userInfoList.get(0) != null) {
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

	/* (非 Javadoc) 
	* <p>Title: userDaliyTaskProcess</p> 
	* <p>Description: 用户每日任务处理</p> 
	* @param xmlStr
	* @param head
	* @return
	* @throws Exception 
	* @see com.app.credits.service.CreditsService#userDaliyTaskProcess(java.lang.String, com.app.vo.Head) 
	*/
	@Override
	public String userDaliyTaskProcess(String xmlStr, Head head) throws Exception {
		logger.debug("enter CreditsServiceImpl.userDaliyTaskProcess(String xmlStr, Head head)");
		
		String response = "";
		
		List<Element> list = Util.getRequestDataByXmlStr(xmlStr, "svccont/pra/item");
		
		String account = "";
		
		String taskType = "";
		
		if(!CollectionUtils.isEmpty(list)) {
			account = list.get(0).elementTextTrim("accout");
			taskType = list.get(0).elementTextTrim("Tasktype");
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug("[account] = " + account + " [taskType] = " + taskType);
		}
		
		if(StringUtils.isBlank(account) || StringUtils.isBlank(taskType)) {
			response = Util.getResponseForFalse(xmlStr, head, "102", "无效请求");
			return response;
		}
		
		//查询该用户今天是否已经分享过
		String queryWdStr = "credits.retrieveCreditsRecords";
		Map<String, Object> paramWds = new HashMap<String, Object>();
		paramWds.put("account", account);
		paramWds.put("creditType", Constant.add_credit);
		
		if(Constant.task_share.equals(taskType)) {
			paramWds.put("channelType", Constant.channelType_share);
		}
		
		if(Constant.task_lottery.equals(taskType)) {
			paramWds.put("channelType", Constant.channelType_drawLottery);
		}
		
		paramWds.put("queryByDate", Constant.add_credit);
		
		List<Map<String, Object>> creditsRecordsList = creditsDao.getSearchList(queryWdStr, paramWds);
		
		if(logger.isDebugEnabled()) {
			logger.debug("[creditsRecordsList] = " + creditsRecordsList);
		}
		
		//如果查询不到记录，则表示此次为该用户第做任务，此时需要奖励积分，为5-15个积分，如果是分享，则奖励5个积分
		if(CollectionUtils.isEmpty(creditsRecordsList)) {
			HashSet<Integer> set = new HashSet<Integer>();  
			Util.randomSet(5, 10, 1, set);
		    int creditRandom = 0;
		    
		    for (int j : set) {  
		    	creditRandom = j;
		    }  
		    
		    //积分入库积分记录表
			String insertStr = "credits.insertUserCredits";
			
			CreditsBean creditsBean = new CreditsBean();
			creditsBean.setId(UUID.randomUUID().toString());
			creditsBean.setAccount(account);
			
//			if(Constant.task_lottery.equals(taskType)) {
//				creditsBean.setCredit(creditRandom + "");
//			}
//			
//			if(Constant.task_share.equals(taskType)) {
//				creditsBean.setCredit("5");
//			}
			
			creditsBean.setCredit("1");
			
			creditsBean.setCreditType(Constant.add_credit);
			
			if(Constant.task_lottery.equals(taskType)) {
				creditsBean.setChannelType(Constant.channelType_drawLottery);
			}
			
			if(Constant.task_share.equals(taskType)) {
				creditsBean.setChannelType(Constant.channelType_share);
			}
			
			creditsBean.setCreateTime(new Date());
			
			int i = creditsDao.insert(insertStr, creditsBean);
			
			if(logger.isDebugEnabled()) {
				logger.debug("[creditDb2InsertResult] = " + i);
			}
			
			//对用户总积分处理，增加用户总积分
			String updateStr = "credits.updateUserTotalCreditForAdd";
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("credit", creditRandom);
			params.put("account", account);
			int j = creditsDao.update(updateStr, params);
			
			if(logger.isDebugEnabled()) {
				logger.debug("[creditDb2UpdateResult] = " + j);
			}
		    
			//获取用户信息
			String queryStr = "user.retrieveUserInfo";
			
			Map<String, Object> userParams = new HashMap<String, Object>();
			userParams.put("account", account);
			List<Map<String, Object>> userInfoList = creditsDao.getSearchList(queryStr, userParams);
			
			String awardCredit = "5";
			
			if(Constant.task_lottery.equals(taskType)) {
				creditsBean.setCredit(creditRandom + "");
			}
			
			String str = "<cjcredit>" + awardCredit + "</cjcredit><credit>"+ userInfoList.get(0).get("totalcredit") +"</credit>";
			
			response = Util.getResponseForTrue(head, str);
			
		} else {
			if(Constant.task_lottery.equals(taskType)) {
				response = Util.getResponseForFalse(xmlStr, head, "102", "今天已经参加抽奖，请明天再来把！");
			}
			
			if(Constant.task_share.equals(taskType)) {
				response = Util.getResponseForFalse(xmlStr, head, "102", "今天已经分享过了哦！");
			}
		}
		
		logger.debug("exit CreditsServiceImpl.userDaliyTaskProcess(String xmlStr, Head head)");
		return response;
	}

	/* (非 Javadoc) 
	* <p>Title: userPromoteProcess</p> 
	* <p>Description: 软件推广处理</p> 
	* @param xmlStr
	* @param head
	* @return
	* @throws Exception 
	* @see com.app.credits.service.CreditsService#userPromoteProcess(java.lang.String, com.app.vo.Head) 
	*/
	@Override
	public String userPromoteProcess(String xmlStr, Head head) throws Exception {
		logger.debug("enter CreditsServiceImpl.userPromoteProcess(String xmlStr, Head head) throws Exception");
		
		String response = "";
		
		List<Element> list = Util.getRequestDataByXmlStr(xmlStr, "svccont/pra/item");
		
		String account = "";
		
		if(!CollectionUtils.isEmpty(list)) {
			account = list.get(0).elementTextTrim("accout");
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug("[account] = " + account);
		}
		
		if(StringUtils.isBlank(account)) {
			response = Util.getResponseForFalse(xmlStr, head, "102", "无效请求");
			return response;
		}
		
		
		StringBuffer userSb = new StringBuffer();
		
		userSb.append("<awardt>150</awardt>");
		userSb.append("<awardone>20</awardone>");
		userSb.append("<awardtwo>30</awardtwo>");
		userSb.append("<awardthree>50</awardthree>");
		userSb.append("<awardfour>50</awardfour>");
		
		//获取用户所有下线总数
		String queryStr = "user.retrieveUserInfo";
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("linkId", account);
		List<Map<String, Object>> userInfoList = creditsDao.getSearchList(queryStr, params);
		
		if(logger.isDebugEnabled()) {
			logger.debug("[userInfoList] = " + userInfoList);
			if(userInfoList != null) {
				logger.debug("[userInfoListSize] = " + userInfoList.size());
			}
		}
		
		if(CollectionUtils.isNotEmpty(userInfoList)) {
			if(userInfoList.size()%2 == 1) {
				userSb.append("<offlinet>"+ (userInfoList.size()/2 + 1) +"</offlinet>");
			} else {
				userSb.append("<offlinet>"+ userInfoList.size()/2 +"</offlinet>");
			}
			
		} else {
			userSb.append("<offlinet>0</offlinet>");
		}
		
		
		//获取用户增加的总积分数
		Map<String, Object> paramWds = new HashMap<String, Object>();
		paramWds.put("account", account);
		paramWds.put("creditType", Constant.add_credit);
		paramWds.put("channelType", Constant.channelType_invite);

		String queryAddStr = "credits.retrieveUserRelatedCreditCount";
		List<Map<String, Object>> creditsRecordsList = creditsDao.getSearchList(queryAddStr, paramWds);
		
		if(logger.isDebugEnabled()) {
			logger.debug("[creditsRecordsList] = " + creditsRecordsList);
		}
		
		if(CollectionUtils.isNotEmpty(creditsRecordsList) && creditsRecordsList.get(0) != null) {
			userSb.append("<rewarded>"+ creditsRecordsList.get(0).get("credit") +"</rewarded>");
		} else {
			userSb.append("<rewarded>0</rewarded>");
		}
		
		
		String linkApkPrefix = Integer.toHexString(Integer.parseInt(account));
		
		String linkUrl = "http://115.29.46.58:8082/creditsManage/version/" + linkApkPrefix + ".apk";
		
		userSb.append("<loadpath>" + linkUrl + "</loadpath>");
		
		String encodeStr = userSb.toString();
		if(logger.isDebugEnabled()) {
			logger.debug("[linkApkPrefix] = " + linkApkPrefix + " [encodeStr] = " + encodeStr);
		}
		
		response = Util.getResponseForTrue(head, encodeStr);
		
		if(logger.isDebugEnabled()) {
			logger.debug("[response] = " + response);
		}
		
		logger.debug("exit CreditsServiceImpl.userPromoteProcess(String xmlStr, Head head) throws Exception");
		
		return response;
	}
}
