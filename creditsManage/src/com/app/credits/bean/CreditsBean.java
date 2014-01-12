package com.app.credits.bean;

import java.util.Date;

/** 
* @ClassName: CreditsBean 
* @Description: 用户积分实体类
* @author yangjun junyang0825@gmail.com 
* @date 2013-12-28 下午05:31:03  
*/
public class CreditsBean {

	/** 
	* @Fields id : 主键ID
	*/ 
	private String id;
	
	/** 
	* @Fields userId : 用户ID 
	*/ 
	private String account;
	
	/** 
	* @Fields credit : 积分
	*/ 
	private String credit;
	
	/** 
	* @Fields creditType : 积分类型 
	*/ 
	private String creditType;
	
	/** 
	* @Fields channelType : 渠道类型
	*/ 
	private String channelType;
	
	/** 
	* @Fields isomplete : 是否已经完成 
	*/ 
	private String isComplete;
	
	/** 
	* @Fields createTime : 创建时间
	*/ 
	private Date createTime;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}


	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getCredit() {
		return credit;
	}

	public void setCredit(String credit) {
		this.credit = credit;
	}

	public String getCreditType() {
		return creditType;
	}

	public void setCreditType(String creditType) {
		this.creditType = creditType;
	}

	public String getChannelType() {
		return channelType;
	}

	public void setChannelType(String channelType) {
		this.channelType = channelType;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getIsComplete() {
		return isComplete;
	}

	public void setIsComplete(String isComplete) {
		this.isComplete = isComplete;
	}
	
}
