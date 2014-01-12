package com.app.user.bean;

import java.util.Date;

/** 
* @ClassName: UserBean 
* @Description: 用户信息实体
* @author yangjun junyang0825@gmail.com 
* @date 2013-12-28 下午03:32:40  
*/
public class UserBean {
	
	/** 
	* @Fields account : 用户账号，主键
	*/ 
	private String account;
	
	/** 
	* @Fields password : 用户密码
	*/ 
	private String password;
	
	/** 
	* @Fields totalCredit : 用户总积分 
	*/ 
	private String totalCredit;
	
	/** 
	* @Fields pendCredit : 用户待处理积分
	*/ 
	private String pendCredit;
	
	/** 
	* @Fields zfbAccount : 支付宝账号 
	*/ 
	private String zfbAccount;
	
	/** 
	* @Fields createTime : 创建时间
	*/ 
	private Date createTime;

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getTotalCredit() {
		return totalCredit;
	}

	public void setTotalCredit(String totalCredit) {
		this.totalCredit = totalCredit;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPendCredit() {
		return pendCredit;
	}

	public void setPendCredit(String pendCredit) {
		this.pendCredit = pendCredit;
	}

	public String getZfbAccount() {
		return zfbAccount;
	}

	public void setZfbAccount(String zfbAccount) {
		this.zfbAccount = zfbAccount;
	}
	
	
}
