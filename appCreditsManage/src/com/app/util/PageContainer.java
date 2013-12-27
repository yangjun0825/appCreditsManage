package com.app.util;

import java.io.Serializable;

/**
 * 分页容器
 * 
 * @author pwp
 * 
 */
public class PageContainer implements Serializable {

	private static final long serialVersionUID = 2017635551607417015L;

	private Integer currentPage = new Integer(1); // 当前第几页

	private Integer totalPages = new Integer(1); // 总页数

	private Integer totalCount = new Integer(0);// 总行数

	private Integer pageRowCount;// 当前页显示的行数

	private Grid grid; // 显示数据

	public PageContainer() {
		grid = new Grid();
	}

	public Integer getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(Integer currentPage) {
		this.currentPage = currentPage;
	}

	public Integer getTotalPages() {
		return totalPages;
	}

	public void setTotalPages() {
		if (pageRowCount > 0) {
			this.totalPages = (totalCount % pageRowCount == 0) ? totalCount / pageRowCount : totalCount / pageRowCount
					+ 1;
		}
	}

	public Integer getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(Integer totalCount) {
		this.totalCount = totalCount;
	}

	public Integer getPageRowCount() {
		return pageRowCount;
	}

	public void setPageRowCount(Integer pageRowCount) {
		this.pageRowCount = pageRowCount;
	}

	public Grid getGrid() {
		return grid;
	}

	public void setGrid(Grid grid) {
		this.grid = grid;
	}
}
