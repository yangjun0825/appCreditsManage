package com.app.util;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 页面参数容器
 * 
 * @author yangjun
 * 
 */
public class ConditionContainer implements Serializable {

	private static final long serialVersionUID = -961739367383768134L;

	private Integer startIndex;// 分页的开始位置

	private Integer maxIndex;// 分页的结束位置

	private String sortType; // 排序类型

	private String sortItem;// 排序字段

	private Map<String, Object> condition = new HashMap<String, Object>();// 用于保存页面查询条件参数

	public Integer getStartIndex() {
		return startIndex;
	}

	public void setStartIndex(Integer startIndex) {
		this.startIndex = startIndex;
	}

	public Integer getMaxIndex() {
		return maxIndex;
	}

	public void setMaxIndex(Integer maxIndex) {
		this.maxIndex = maxIndex;
	}

	public Map<String, Object> getCondition() {
		return condition;
	}

	public void setCondition(Map<String, Object> condition) {
		this.condition = condition;
	}

	public String getSortType() {
		return sortType;
	}

	public void setSortType(String sortType) {
		this.sortType = sortType;
	}

	public String getSortItem() {
		return sortItem;
	}

	public void setSortItem(String sortItem) {
		this.sortItem = sortItem;
	}
}
