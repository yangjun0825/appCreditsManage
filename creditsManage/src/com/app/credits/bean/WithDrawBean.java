package com.app.credits.bean;

import java.util.Date;

public class WithDrawBean {
	
	/** 
	* @Fields id : 主键ID
	*/ 
	private String id;
	
	/** 
	* @Fields account : 用户账号
	*/ 
	private String account;
	
	/** 
	* @Fields credit : 提现积分
	*/ 
	private String credit;
	
	/** 
	* @Fields cashType : 提现类型 1支付宝，2qq币，3话费
	*/ 
	private String cashType;
	
	/** 
	* @Fields cashAccount : 提款账号
	*/ 
	private String cashAccount;
	
	/** 
	* @Fields isComplete : 完成类型 1 已完成 2 待提现 
	*/ 
	private String isComplete;
	
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

	public String getCashType() {
		return cashType;
	}

	public void setCashType(String cashType) {
		this.cashType = cashType;
	}

	public String getCashAccount() {
		return cashAccount;
	}

	public void setCashAccount(String cashAccount) {
		this.cashAccount = cashAccount;
	}

	public String getIsComplete() {
		return isComplete;
	}

	public void setIsComplete(String isComplete) {
		this.isComplete = isComplete;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
}
