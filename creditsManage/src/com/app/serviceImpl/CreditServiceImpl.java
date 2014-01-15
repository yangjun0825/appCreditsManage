package com.app.serviceImpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.credits.bean.CreditsBean;
import com.app.credits.bean.WithDrawBean;
import com.app.service.CreditService;
import com.app.util.Constant;
import com.app.util.MyBatisDao;

@Service
public class CreditServiceImpl implements CreditService {

	@Autowired
	private MyBatisDao<Map<String, Object>> creditsDao;
	
	private Log logger = LogFactory.getLog(CreditServiceImpl.class);	
	
	/* (非 Javadoc) 
	* <p>Title: retrieveCreditWitHDrawList</p> 
	* <p>Description:获取用户提现积分记录 </p> 
	* @param params
	* @return 
	* @see com.app.service.CreditService#retrieveCreditWitHDrawList(java.util.Map) 
	*/
	public List<WithDrawBean> retrieveCreditWitHDrawList(Map<String, Object> params) {
		logger.debug("enter CreditServiceImpl.retrieveCreditWitHDrawList(Map<String, Object> params)");
		
		List<WithDrawBean> creditsWsList = new ArrayList<WithDrawBean>();
		
		//获取用户积分记录
		String queryStr = "creditsWithDraw.retrieveWdCreditsRecords";
		
		List<Map<String, Object>> creditsRecordsList = creditsDao.getSearchList(queryStr, params);
		
		if(logger.isDebugEnabled()) {
			logger.debug("[creditsWsRecordsList] = " + creditsRecordsList);
		}
		
		if(CollectionUtils.isNotEmpty(creditsRecordsList)) {
			for(Map<String, Object> map : creditsRecordsList) {
				WithDrawBean withDrawBean = new WithDrawBean();
				withDrawBean.setId((String)map.get("id"));
				withDrawBean.setCashType((String)map.get("cashtype"));
				withDrawBean.setCashAccount((String)map.get("cashaccount"));
				withDrawBean.setCredit((String)map.get("credit"));
				withDrawBean.setCreateTime((Date)map.get("createtime"));
				withDrawBean.setIsComplete((String)map.get("iscomplete"));
				
				creditsWsList.add(withDrawBean);
			}
		}
		
		logger.debug("exit CreditServiceImpl.retrieveCreditWitHDrawList(Map<String, Object> params)");
		return creditsWsList;
	}

	/* (非 Javadoc) 
	* <p>Title: completeWithDraw</p> 
	* <p>Description:完成体现操作 </p> 
	* @param params
	* @return 
	* @see com.app.service.CreditService#completeWithDraw(java.util.Map) 
	*/
	public String completeWithDraw(Map<String, Object> params) {
		logger.debug("enter CreditServiceImpl.completeWithDraw(Map<String, Object> params)");
		
		String withDrawResult = Constant.failure;
		
		//提现第一步：将用户的待提现积分减少相应积分
		String updateStr = "credits.updateUserPendCredit";
		int i = creditsDao.update(updateStr, params);
		
		//体现第二步：将用户的积分表中的对应的提现请求标记为已经完成
		String updateStrC = "creditsWithDraw.updateWdCreditInfo";
		params.put("isComplete", Constant.complete_withdraw);
		int j = creditsDao.update(updateStrC, params);
		
		if(logger.isDebugEnabled()) {
			logger.debug("[updateResult] = " + i + " " + j);
		}
		
		if(i > 0 && j > 0) {
			withDrawResult = Constant.success;
		}
		
		logger.debug("exit CreditServiceImpl.completeWithDraw(Map<String, Object> params)");
		return withDrawResult;
	}

}
