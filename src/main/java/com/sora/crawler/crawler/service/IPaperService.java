package com.sora.crawler.crawler.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.sora.crawler.crawler.handler.contentHandler;
import com.sora.crawler.crawler.helper.logHelper;
import com.sora.crawler.crawler.model.Paper;



@Service
@Scope("session")
public class IPaperService {
	
	@Autowired
	contentHandler handler;
	
	private HashSet<String> Ids = new HashSet<>();
	
	private List<HashMap<String,Integer>> nameToNums = new ArrayList<>(); 
	
	private List<HashMap<String,Integer>> proToNums = new ArrayList<>(); 
//	private boolean ifError = false;
//	public boolean isIfError() {
//		return ifError;
//	}
//
//	public void setIfError(boolean ifError) {
//		this.ifError = ifError;
//		handler.setIfError(false);
//	}

	public List<HashMap<String,Integer>> getNameToNums() {
		//TODO
		
		return nameToNums;
	}

	public  List<HashMap<String,Integer>> getProToNums() {
		//TODO
		
		return proToNums;
	}

	public void setProToNums(List<HashMap<String, Integer>> proToNums) {
		this.proToNums = proToNums;
	}

	public void setNameToNums(List<HashMap<String, Integer>> nameToNums) {
		this.nameToNums = nameToNums;
	}

	public void resetId() {
		Ids = new HashSet<>();
	}
	
	public List<Paper> Search(String keyWord, String database, String year, String program) throws Exception{
		handler.setDatabaseName(database);
		handler.setKeyWords(keyWord, year);
		
		List<String> listOfId = handler.getEsearchContent();
		String ids = "";
		if(nameToNums.size()==0) {
			nameToNums.add(new HashMap<>());
			nameToNums.add(new HashMap<>());
			proToNums.add(new HashMap<>());
			proToNums.add(new HashMap<>());
		}
		
		
		int count = 0;
		for(int x = 0 ; x < listOfId.size() ; x++) {
			if(Ids.contains(listOfId.get(x))) {
				continue;
			}
			count++;
			ids += listOfId.get(x);
			if(x!=listOfId.size()-1) {
				ids += ",";
			}
			Ids.add(listOfId.get(x));
		}
		if(year.equals("2017")) {
			nameToNums.get(0).put(keyWord, count);
//			if(proToNums.get(1).containsKey(program)) {
//				proToNums.get(1).put(program, proToNums.get(1).get(program)+listOfId.size());
//			}
//			else {
//				proToNums.get(1).put(program, listOfId.size());
//			}
		}
		else if(year.equals("2018")) {
			nameToNums.get(1).put(keyWord, count);
//			if(proToNums.get(0).containsKey(program)) {
//				proToNums.get(0).put(program, proToNums.get(0).get(program)+listOfId.size());
//			}
//			else {
//				proToNums.get(0).put(program, listOfId.size());
//			}
		}
//		ifError = handler.isIfError();
//		System.out.println("Analysing Data...");
		logHelper.info("Analysing Data...");
		return handler.getEfetchContent(ids,year);
	}
}
