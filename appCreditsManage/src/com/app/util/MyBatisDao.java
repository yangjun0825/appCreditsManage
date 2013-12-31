package com.app.util;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * 根据业务进行数据操作调用的Dao
 * 
 * @author pwp
 * 
 * @param <T>
 */
@Repository
public class MyBatisDao<T> {

	private Logger logger = Logger.getLogger(MyBatisDao.class);

	@Autowired
	private SqlSessionTemplate sqlSession;

	/**
	 * 获取检索记录
	 * 
	 * @param queryStr
	 * @param params
	 * @return
	 */
	public List<T> getSearchList(String queryStr, Object params) {
		try {
			if (StringUtils.isBlank(queryStr)) {
				logger.warn("queryStr is required");
				return null;
			}
			return this.sqlSession.selectList(queryStr, params);
		} catch (Exception e) {
			logger.warn("数据库连接异常！");
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取记录总数
	 * 
	 * @param countStr
	 * @param params
	 * @return
	 */
	public Integer getSearchSize(String countStr, Object params) {
		try {
			if (StringUtils.isBlank(countStr)) {
				logger.warn("countStr is required");
				return null;
			}
			Integer totalCount = 0;
			List<Map<String, Object>> list = this.sqlSession.selectList(countStr, params);
			if (list.size() > 0) {
				list.get(0).get("totalCount");
				totalCount = Integer.parseInt(ObjectUtils.toString(list.get(0).get("totalCount")));
			}
			return totalCount;
		} catch (Exception e) {
			logger.warn("数据库连接异常！");
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 分页查询结果返回
	 * 
	 * @param queryStr
	 * @param countStr
	 * @param c
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public PageContainer getSearchPage(String queryStr, String countStr, ConditionContainer c) {
		try {
			if (StringUtils.isBlank(queryStr)) {
				logger.warn("=======queryStr is null");
				return null;
			}
			PageContainer pc = new PageContainer();
			Map<String, Object> params = c.getCondition();
			Integer totalCount = getSearchSize(countStr, params);
			if (totalCount > 0) {
				params.put("startIndex", c.getStartIndex());
				params.put("maxIndex", c.getMaxIndex());
				if (StringUtils.isNotBlank(c.getSortItem())) {
					params.put("sortItem", c.getSortItem());
					params.put("sortType", c.getSortType());
				}
				List<Map<String, Object>> pageList = (List<Map<String, Object>>) getSearchList(queryStr, params);
				pc.getGrid().setPageList(pageList);
				pc.setTotalCount(totalCount);
			}
			return pc;
		} catch (Exception e) {
			logger.warn("数据库连接异常！");
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 更新
	 * 
	 * @param updateStr
	 * @param params
	 * @return
	 */
	public int update(String updateStr, Object params) {
		try {
			if (StringUtils.isBlank(updateStr)) {
				logger.warn("=======updateStr is null");
				return 0;
			}
			int result = 0;
			result = this.sqlSession.update(updateStr, params);
			return result;
		} catch (Exception e) {
			logger.warn("数据库连接异常！");
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 插入
	 * 
	 * @param insertStr
	 * @param params
	 * @return
	 */
	public int insert(String insertStr, Object params) {
		try {
			if (StringUtils.isBlank(insertStr)) {
				logger.warn("=======insertStr is null");
				return 0;
			}
			int result = 0;
			result = this.sqlSession.insert(insertStr, params);
			return result;
		} catch (Exception e) {
			logger.warn("数据库连接异常！");
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 删除
	 * 
	 * @param delStr
	 * @param params
	 * @return
	 */
	public int delete(String delStr, Object params) {
		try {
			if (StringUtils.isBlank(delStr)) {
				logger.warn("=======delstr is null");
				return 0;
			}
			int result = 0;
			result = this.sqlSession.delete(delStr, params);
			return result;
		} catch (Exception e) {
			logger.warn("数据库连接异常！");
			e.printStackTrace();
		}
		return 0;
	}
}
