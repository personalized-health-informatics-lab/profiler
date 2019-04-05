package com.sora.crawler.crawler.handler;

import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.sora.crawler.crawler.helper.logHelper;
import com.sora.crawler.crawler.httpConnector.httpConnector;
import com.sora.crawler.crawler.model.Paper;


@Component
@Scope("session")
public class contentHandler {
	
//	private boolean ifError = false;
//	public boolean isIfError() {
//		return ifError;
//	}
//	public void setIfError(boolean ifError) {
//		this.ifError = ifError;
//		connector.setIfError(ifError);
//	}
	
	private String databaseName;
//	private String keyWords_prefix = "((\"2018\"[PDAT] : \"2018\"[PDAT]) AND Icahn[All Fields]) AND ";
	private String keyWords_subfix = "[Full Author Name]";
	private String keyWords = "";
	public static final String base = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/";
//	https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?db=pubmed&term=%222018%22[PDAT]%20:%20%222018%22[PDAT])%20AND%20Finkelstein,%20Joseph[Full%20Author%20Name]
//	https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?db=pubmed&term=
//	((%222018%22[PDAT]%20:%20%222018%22[PDAT])%20AND%20Icahn[All%20Fields])%20AND%20Parsons,%20Ramon[Full%20Author%20Name]
	@Autowired
	httpConnector connector;
	
	public String getDatabaseName() {
		return databaseName;
	}
	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}
	public String getKeyWords() {
		return keyWords;
	}
	public void setKeyWords(String keyWords, String year) {
		keyWords = "((\""+year+"\"[PDAT] : \""+year+"\"[PDAT]) AND Icahn[All Fields] AND school[All Fields] AND medicine[All Fields]) AND " + keyWords + keyWords_subfix;
		this.keyWords = keyWords;
	}
	
	public List<String> getEsearchContent() throws Exception {
		String url = base + "esearch"+ ".fcgi?" + "db=" + databaseName + "&term=" + keyWords + "&retmax=500";
//		String url = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?db=pubmed&term=((%222018%22[PDAT]%20:%20%222018%22[PDAT])%20AND%20Icahn[All%20Fields])%20AND%20Finkelstein,%20Joseph[Full%20Author%20Name]&retmax=500";
		url = url.replace(" ", "%20");
		String content = connector.getContent(url);
//		ifError = connector.isIfError();
		return getIds(content);
	}
	
	public List<String> getIds(String content) throws Exception{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();  
		DocumentBuilder builder;  
		try {  
		    builder = factory.newDocumentBuilder();  
		    Document document = builder.parse(new InputSource(new StringReader(content)));  
		    if(document.getElementsByTagName("IdList").getLength()>0) {
			    NodeList idlist = document.getElementsByTagName("IdList").item(0).getChildNodes();
			    List<String> listOfId = new ArrayList<>();
			    for(int x = 0 ; x < idlist.getLength() ; x++) {
			    	String Id = idlist.item(x).getTextContent();
			    	listOfId.add(Id);	
			    }
			    return listOfId;
		    }
		    else return new ArrayList<>();
		} catch (Exception e) {  
		    e.printStackTrace();  
		    throw e;
		} 
	}
	
	public List<Paper> getEfetchContent(String id, String year) throws Exception {
//		System.out.println(id);
		logHelper.info(id);
		String url = base + "efetch" + ".fcgi?" + "db=" + databaseName + "&id=" + id + "&retmode=xml";
		String content = connector.getContent(url);
//		ifError = connector.isIfError();
		Paper paper = new Paper();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();  
		DocumentBuilder builder;  
		try {
			builder = factory.newDocumentBuilder();  
		    Document document = builder.parse(new InputSource(new StringReader(content)));  
			NodeList journalList = document.getElementsByTagName("Journal");
			if(journalList.getLength()!=0) {
				//Update to only connect to server once
				NodeList paperList = document.getElementsByTagName("PubmedArticle");
				List<Paper> resList = new ArrayList<>();
				for(int x = 0 ; x < paperList.getLength() ; x++) {
					Document newDocument = builder.newDocument();
					Node importedNode = newDocument.importNode(paperList.item(x), true);
					newDocument.appendChild(importedNode);
					Paper newPaper = getEfetchPaperContent(newDocument,year);
					resList.add(newPaper);
				}
				return resList;
			}
			else {
				return new ArrayList<>();
			}
		}
		catch(Exception e) {  
		    e.printStackTrace();  
		    throw e;
		}
	}
	
	private Paper getEfetchBookContent(Document document) {
		// TODO Auto-generated method stub
		return null;
	}
	public Paper getEfetchPaperContent(Document document, String year) throws Exception {
		Paper paper = new Paper();
		try {  
			// Set title
		    String title = document.getElementsByTagName("ArticleTitle").item(0).getTextContent();
		    paper.setArticleTitle(title);
		    // Set Author Names
		    NodeList nameList = document.getElementsByTagName("AuthorList").item(0).getChildNodes();
		    paper.setAuthors(new ArrayList<String>());
		    for(int x = 0 ; x < nameList.getLength() ; x++) {
		    	if(nameList.item(x).getNodeName()!="Author") continue;
		    	if((nameList.item(x).getChildNodes().getLength()>3&&!nameList.item(x).getChildNodes().item(3).getNodeName().equals("ForeName")&&nameList.item(x).getChildNodes().item(1).getNodeName().equals("LastName"))||
		    			nameList.item(x).getChildNodes().getLength()<=3&&nameList.item(x).getChildNodes().getLength()>=1&&nameList.item(x).getChildNodes().item(1).getNodeName().equals("LastName")) {
		    		String name = nameList.item(x).getChildNodes().item(1).getTextContent();
		    		paper.getAuthors().add(name);
		    	}
		    	else if(nameList.item(x).getChildNodes().getLength()>3&&nameList.item(x).getChildNodes().item(1).getNodeName().equals("LastName")&&
		    			nameList.item(x).getChildNodes().item(3).getNodeName().equals("ForeName")){
		    		String name = nameList.item(x).getChildNodes().item(1).getTextContent()+", "+nameList.item(x).getChildNodes().item(3).getTextContent();
			    	paper.getAuthors().add(name);
		    	}
		    }
		    // Set Journal Name
		    NodeList journalList = document.getElementsByTagName("Journal").item(0).getChildNodes();
		    for(int x = 0 ; x < journalList.getLength() ; x++) {
		    	if(journalList.item(x).getNodeName()=="Title") {
		    		paper.setJournalName(journalList.item(x).getTextContent());
		    		break;
		    	}
		    }
		    // Set Pages
		    Node pageNode = document.getElementsByTagName("MedlinePgn").item(0);
		    if(pageNode!=null) {
		    	String medlinePgn = document.getElementsByTagName("MedlinePgn").item(0).getTextContent();
			    String[] nums = medlinePgn.split("-");
			    if(nums.length==1) {
			    	paper.setPages(medlinePgn);
			    }
			    else {
			    	if(nums[0].length()>nums[1].length())
			    	nums[1] = nums[0].substring(0, nums[0].length()-nums[1].length()) +nums[1];
				    paper.setPages(nums[0] + "-" + nums[1]);
			    }
		    }
		    // Set Date
		    NodeList dateList = document.getElementsByTagName("DateRevised").item(0).getChildNodes();
		    String dateStr = dateList.item(1).getTextContent() +"-"+ dateList.item(3).getTextContent() +"-"+ dateList.item(5).getTextContent();
		    Date date = new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
		    paper.setPublishTime(date);
		    
		    // Set Volume
		    NodeList VolumeNodes = document.getElementsByTagName("Volume");
		    if(VolumeNodes.getLength()>0) {
		    	String Volume = VolumeNodes.item(0).getTextContent();
			    paper.setVolume(Volume);
		    }
		    // Set Issue
		    NodeList IssueNodes = document.getElementsByTagName("Issue");
		    if(IssueNodes.getLength()>0) {
		    	String Issue = IssueNodes.item(0).getTextContent();
			    paper.setIssue(Issue);
		    }
		    //Set DOI
		    NodeList DOIIssueNode = document.getElementsByTagName("ArticleId");
		    for(int x = 0 ; x < DOIIssueNode.getLength() ; x++) {
		    	Node temp = DOIIssueNode.item(x);
		    	if(DOIIssueNode.item(x).getAttributes().item(0).getTextContent().equals("doi")) {
		    		if(paper.getDOI()==null) {
			    		String DOI = DOIIssueNode.item(x).getTextContent();
			    		paper.setDOI(DOI);
		    		}
		    	}
		    	else if(DOIIssueNode.item(x).getAttributes().item(0).getTextContent().equals("pubmed")) {
		    		if(paper.getPMID()==null) {
		    			String pMID = DOIIssueNode.item(x).getTextContent();
		    			paper.setPMID(pMID);
//			    		System.out.println(pMID);
		    		}
		    		
		    	}
		    }
		    //Set Abstract
		    NodeList AbstractTextNode = document.getElementsByTagName("AbstractText");
		    if(AbstractTextNode.getLength()>0) {
		    	String abstract1 = AbstractTextNode.item(0).getTextContent();
			    paper.setAbstract(abstract1);
		    }
		    //Set DocumentType
		    paper.setDocumentType("Article");
		    //Set publicationStage
		    NodeList publicationStageNode = document.getElementsByTagName("PublicationStatus");
		    if(publicationStageNode.getLength()>0) {
		    	String publicationStage = publicationStageNode.item(0).getTextContent();
			    paper.setPublicationStage(publicationStage);
		    }
		    paper.setYear(year);
		    //Set PMID
		    return paper;
		} catch (Exception e) {  
		    e.printStackTrace();  
		    throw e;
		} 
	}
}
