package com.app.user.bean;

import java.util.Date;

/** 
* @ClassName: UserFeedBackBean 
* @Description: 用户反馈信息实体 
* @author yangjun junyang0825@gmail.com 
* @date 2013-12-28 下午03:32:57  
*/
public class UserFeedBackBean {

	/** 
	* @Fields id : 主键ID
	*/ 
	private String id;
	
	/** 
	* @Fields userId : 用户ID
	*/ 
	private String account;
	
	/** 
	* @Fields content : 反馈内容 
	*/ 
	private String content;
	
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

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
}
