package com.app.version.bean;

import java.util.Date;

public class VersionBean {

	/** 
	* @Fields id : 主键ID
	*/ 
	private String id;
	
	/** 
	* @Fields version : 版本号
	*/ 
	private String version;
	
	/** 
	* @Fields isNew : 是否是新版本，0-是  1-否
	*/ 
	private String isNew;
	
	/** 
	* @Fields isEnforce : 是否强制升级，0-是， 1-否
	*/ 
	private String isEnforce;
	
	/** 
	* @Fields vsDesc : 版本描述 
	*/ 
	private String vsDesc;
	
	/** 
	* @Fields vsPath : 版本URL路径
	*/ 
	private String vsPath;
	
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

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getIsNew() {
		return isNew;
	}

	public void setIsNew(String isNew) {
		this.isNew = isNew;
	}

	public String getIsEnforce() {
		return isEnforce;
	}

	public void setIsEnforce(String isEnforce) {
		this.isEnforce = isEnforce;
	}

	public String getVsDesc() {
		return vsDesc;
	}

	public void setVsDesc(String vsDesc) {
		this.vsDesc = vsDesc;
	}

	public String getVsPath() {
		return vsPath;
	}

	public void setVsPath(String vsPath) {
		this.vsPath = vsPath;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
}
