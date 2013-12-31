package com.app.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 前段页面grid内容
 * 
 * @author yangjun
 * 
 */
public class Grid implements Serializable {

	private static final long serialVersionUID = 6940351452392840926L;

	private List<Map<String, Object>> pageList;// 返回结果集

	public Grid() {
		pageList = new ArrayList<Map<String, Object>>();
	}

	public List<Map<String, Object>> getPageList() {
		return pageList;
	}

	public void setPageList(List<Map<String, Object>> pageList) {
		this.pageList = pageList;
	}
}
