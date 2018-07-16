package org.gil.activemq.server.model;

public class NewsPub {

	/**
	 * 新闻id
	 */
	private long id;
	
	/**
	 * 标题
	 */
	private String title;

	/**
	 * 原文url
	 */
	private String url;
	
	/**
	 * 栏目代码
	 */
	private String sectionCode;
	
	/**
	 * 区域代码
	 */
	private String areaCode;
	
	/**
	 * 信息来源代码
	 */
	private String sourceCode;
	
	/**
	 * 公司代码
	 */
	private String companyCode;
	
	/**
	 * 发布时间
	 */
	private String publishDate;
	
	/**
	 * 是否消极
	 */
	private int isNegative;
	
	/**
	 * 行业代码
	 */
	private String industryCode;
	
	/**
	 * 聚源代码
	 */
	private String gilCode;
	
	/**
	 * 板块代码
	 */
	private String sectorCode;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getSectionCode() {
		return sectionCode;
	}

	public void setSectionCode(String sectionCode) {
		this.sectionCode = sectionCode;
	}

	public String getAreaCode() {
		return areaCode;
	}

	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}

	public String getSourceCode() {
		return sourceCode;
	}

	public void setSourceCode(String sourceCode) {
		this.sourceCode = sourceCode;
	}

	public String getCompanyCode() {
		return companyCode;
	}

	public void setCompanyCode(String companyCode) {
		this.companyCode = companyCode;
	}

	public String getPublishDate() {
		return publishDate;
	}

	public void setPublishDate(String publishDate) {
		this.publishDate = publishDate;
	}

	public int isNegative() {
		return isNegative;
	}

	public void setNegative(int isNegative) {
		this.isNegative = isNegative;
	}

	public String getIndustryCode() {
		return industryCode;
	}

	public void setIndustryCode(String industryCode) {
		this.industryCode = industryCode;
	}

	public String getGilCode() {
		return gilCode;
	}

	public void setGilCode(String gilCode) {
		this.gilCode = gilCode;
	}

	public String getSectorCode() {
		return sectorCode;
	}

	public void setSectorCode(String sectorCode) {
		this.sectorCode = sectorCode;
	}
	
	
}
