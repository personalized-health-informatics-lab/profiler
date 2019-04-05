package com.sora.crawler.crawler.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import com.sora.crawler.crawler.handler.ExcelHandler;
import com.sora.crawler.crawler.helper.logHelper;
import com.sora.crawler.crawler.model.Paper;

@Service
@Scope("session")
public class AsyncTaskService {
	
	@Autowired
	IPaperService paperService;
	
	@Autowired
	ExcelHandler excelHandler;
	
	List<List<Paper>> listOfYear = new ArrayList<>();
	
	String fileName = "";
	
	String searchingName = "";
	
	boolean isFinish = true;
	
	boolean isError = false;
	
	private List<HashMap<String,Integer>> nameToNums = new ArrayList<>(); 
	
	private List<HashMap<String,Integer>> proToNums = new ArrayList<>();
	
	
	
	public List<HashMap<String, Integer>> getNameToNums() {
		return nameToNums;
	}

	public void setNameToNums(List<HashMap<String, Integer>> nameToNums) {
		paperService.setNameToNums(new ArrayList<>());
		this.nameToNums = nameToNums;
	}

	public List<HashMap<String, Integer>> getProToNums() {
		return proToNums;
	}

	public void setProToNums(List<HashMap<String, Integer>> proToNums) {
		paperService.setNameToNums(new ArrayList<>());
		this.proToNums = proToNums;
	}

	public boolean isFinish() {
		return isFinish;
	}

	private HashMap<String,String> names = new HashMap<>();
	
    public void setNames(HashMap<String,String> names) {
		this.names = names;
	}

	public HashMap<String,String> getNames() {
		return names;
	}

	public List<List<Paper>> getListOfPaper() {
		return listOfYear;
	}

	@Async
    public List<List<Paper>> executeAsyncTask(String dateName) throws Exception {
		setProToNums(new ArrayList<>());
		setNameToNums(new ArrayList<>());
		isFinish = false;
		ArrayList<Paper> listOfPaper1 = new ArrayList<>();
		ArrayList<Paper> listOfPaper2 = new ArrayList<>();
		ArrayList<Paper> listOfPaper3 = new ArrayList<>();
		listOfYear.add(listOfPaper1);
		listOfYear.add(listOfPaper2);
		listOfYear.add(listOfPaper3);
//		paperService.setIfError(false);
//		sts = 1;
    	InputStream fis = null;
    	fileName = dateName;
        HashMap<String,String> nameToProgram;
        File upload;
        if(System.getProperty("os.name").equals("Linux")) {
        	upload = new File("/root/tmp/files/",dateName);
        }
        else {
        	upload = new File("C:\\files\\",dateName);
        }
        try {
        	isError = false;
            fis = new FileInputStream(upload);
            nameToProgram = excelHandler.getAuthorsName(fis);
			for(Map.Entry<String, String> entry: nameToProgram.entrySet()) {
				String tempname = entry.getKey();
				String[] seprate = tempname.split(",");
				while(seprate[1].charAt(0)==' ') seprate[1] = seprate[1].substring(1);
				tempname = seprate[0] + ", " + seprate[1];
				names.put(tempname.toLowerCase(),entry.getValue());
				searchingName = tempname;
				for(int x = 2019 ; x >= 2017 ; x--) {
					List<Paper> tempList = paperService.Search(tempname, "pubmed",x+"",entry.getValue());
					if(tempList.size()>0) {
						listOfYear.get(x-2017).addAll(tempList);
					}
				}
				TimeUnit.SECONDS.sleep(1);
			}
//			System.out.println("finish");
			this.setNameToNums(paperService.getNameToNums());
			this.setProToNums(paperService.getProToNums());
			logHelper.info("finish");
			return listOfYear;
		} catch (Exception e) {
			e.printStackTrace();
			isError = true;
			throw e;
		}finally {
			upload.delete();
			paperService.resetId();
			isFinish = true;
			searchingName = "";
			if(fis!=null) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
					throw e;
				}
			}
		}
    }

	public List<List<Paper>> getListOfYear() {
		return listOfYear;
	}

	public void setListOfYear(List<List<Paper>> listOfYear) {
		this.listOfYear = listOfYear;
	}

	public void setFinish(boolean isFinish) {
		this.isFinish = isFinish;
	}

	public String getSearchingName() {
		return searchingName;
	}

	public boolean isError() {
		return isError;
	}

	public String getFileName() {
		return fileName;
	}
}
