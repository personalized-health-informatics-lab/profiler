package com.sora.crawler.crawler.model;

import java.util.Date;
import java.util.HashSet;
import java.util.List;

public class Paper {

	private List<String> authors;
	private String articleTitle;
	private String journalName;
	private Date publishTime;
	private String pages;
	private String Volume;
	private String Issue;
	private String DOI;
	private String Abstract;
	private String DocumentType;
	private String publicationStage;
	private String PMID;
	private String Year;
	
	
	public String getYear() {
		return Year;
	}
	public void setYear(String year) {
		Year = year;
	}
	public String getDOI() {
		return DOI;
	}
	public void setDOI(String dOI) {
		DOI = dOI;
	}
	public String getAbstract() {
		return Abstract;
	}
	public void setAbstract(String abstract1) {
		Abstract = abstract1;
	}
	public String getDocumentType() {
		return DocumentType;
	}
	public void setDocumentType(String documentType) {
		DocumentType = documentType;
	}
	public String getPublicationStage() {
		return publicationStage;
	}
	public void setPublicationStage(String publicationStage) {
		this.publicationStage = publicationStage;
	}
	public String getPMID() {
		return PMID;
	}
	public void setPMID(String pMID) {
		PMID = pMID;
	}
	public String getVolume() {
		return Volume;
	}
	public void setVolume(String volume) {
		Volume = volume;
	}
	public String getIssue() {
		return Issue;
	}
	public void setIssue(String issue) {
		Issue = issue;
	}
	public List<String> getAuthors() {
		return authors;
	}
	public void setAuthors(List<String> authors) {
		this.authors = authors;
	}
	public String getArticleTitle() {
		return articleTitle;
	}
	public void setArticleTitle(String articleTitle) {
		this.articleTitle = articleTitle;
	}
	public String getJournalName() {
		return journalName;
	}
	public void setJournalName(String journalName) {
		this.journalName = journalName;
	}
	public String getPublishTime() {
		return publishTime.toString();
	}
	public void setPublishTime(Date publishTime) {
		this.publishTime = publishTime;
	}
	public String getPages() {
		return pages;
	}
	public void setPages(String pages) {
		this.pages = pages;
	}
	
}
