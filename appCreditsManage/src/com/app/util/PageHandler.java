package com.app.util;


import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;

/**
 * 页面查询条件收集，dao返回结果集收集
 * 
 * @author pwp
 * 
 */
@SuppressWarnings("all")
public class PageHandler {

	private final PageContainer page;

	private ConditionContainer conditionContainer;

	private HttpServletRequest request;

	private Logger logger = Logger.getLogger(PageHandler.class);

	public PageHandler(HttpServletRequest request) {
		page = new PageContainer();
		conditionContainer = new ConditionContainer();
		this.request = request;
		webInitialize();
	}

	public ConditionContainer getConditionContainer() {
		return conditionContainer;
	}

	public void setConditionContainer(ConditionContainer conditionContainer) {
		this.conditionContainer = conditionContainer;
	}

	public PageContainer getPage() {
		return page;
	}

	public void setPage(PageContainer page) {
		this.page.setGrid(page.getGrid());
		if (this.page.getPageRowCount() != null
				&& (conditionContainer.getMaxIndex() == null || conditionContainer.getMaxIndex().intValue() != 0)) {
			this.page.setTotalCount(page.getTotalCount());
			this.page.setTotalPages();
		}
	}

	/**
	 * 页面参数初始化获取
	 */
	public void webInitialize() {
		page.setCurrentPage(NumberUtils.toInt(request.getParameter("page")));
		page.setPageRowCount(StringUtils.isNotBlank(request.getParameter("rows")) ? NumberUtils.toInt(request
				.getParameter("rows")) : null);
		conditionContainer.setCondition(getPageParameters());
		conditionContainer.setSortType(request.getParameter("sord"));
		conditionContainer.setSortItem(request.getParameter("sidx"));
		if (page.getPageRowCount() == null) {
			conditionContainer.setStartIndex(0);
			conditionContainer.setMaxIndex(0);
		} else if (page.getPageRowCount().intValue() == 0) {
			conditionContainer.setStartIndex((page.getCurrentPage() - 1) * page.getPageRowCount());
			conditionContainer.setMaxIndex(null);
		} else {
			conditionContainer.setStartIndex((page.getCurrentPage() - 1) * page.getPageRowCount());
			conditionContainer.setMaxIndex(conditionContainer.getStartIndex() + page.getPageRowCount());
		}
	}

	/**
	 * 获取页面查询条件（封装成map）
	 * 
	 * @return
	 */
	public Map<String, Object> getPageParameters() {
		Map<String, Object> conditions = new HashMap<String, Object>();
		Map<String, Object> hideConditions = new HashMap<String, Object>();
		boolean search = (StringUtils.isNotBlank(request.getParameter("search")) && "true".equals(request
				.getParameter("search"))) ? true : false;
		boolean hideSearch = (StringUtils.isNotBlank(request.getParameter("hideSearch")) && "true".equals(request
				.getParameter("hideSearch"))) ? true : false;
		if (search) {
			conditions = parseParams(request.getParameter("filters"));
		}
		if (hideSearch) {
			hideConditions = parseParams(request.getParameter("hideParams"));
			conditions.putAll(hideConditions);
		}
		return conditions;
	}

	public Map<String, Object> parseParams(String filters) {
		Map<String, Object> conditions = new HashMap<String, Object>();
		// {"rules":[{"field":"disName","data":"该号码后见面"},{"field":"disReason","data":"ggg"}]}
		JSONObject searchJson = JSONObject.fromObject(filters);
		JSONArray filedArray = (JSONArray) searchJson.get("rules");
		for (int i = 0; i < filedArray.size(); i++) {
			String field = ((JSONObject) filedArray.get(i)).get("field").toString();
			String value = ((JSONObject) filedArray.get(i)).get("data").toString();
			if (value != null && !value.equals("")) {
				try {
					if (value.equals(new String(value.getBytes("ISO-8859-1"), "ISO-8859-1"))) {
						value = new String(value.getBytes("ISO-8859-1"), "UTF-8");
					}
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
			conditions.put(field, value);
		}
		return conditions;
	}

	/**
	 * 向前段返回grid数据
	 * 
	 * 
	 */
	public void outPut(HttpServletResponse response) {
		Iterator<Map<String, Object>> dataIt = page.getGrid().getPageList().iterator();
		JSONObject json = new JSONObject();
		json.put("page", page.getCurrentPage());
		json.put("total", page.getTotalPages());
		json.put("records", page.getTotalCount());
		JSONArray rows = new JSONArray();
		while (dataIt.hasNext()) {
			HashMap<String, Object> datamap = (HashMap<String, Object>) dataIt.next();
			Iterator map = datamap.entrySet().iterator();
			JSONObject cell = new JSONObject();
			while (map.hasNext()) {
				Map.Entry entry = (Map.Entry) map.next();
				if ("id".equals(entry.getKey())) {
					cell.put("id", entry.getValue());
				}
				cell.put(entry.getKey(), entry.getValue());
			}
			rows.add(cell);
			logger.info("要返回的json对象：\n" + cell.toString());
		}
		json.put("rows", rows);
		logger.debug("要返回的json对象：\n" + json.toString());
		SpringUtils.renderJson(response, json.toString());
	}
}
