package com.sora.crawler.crawler.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.sora.crawler.crawler.handler.ExcelHandler;
import com.sora.crawler.crawler.model.Paper;
import com.sora.crawler.crawler.service.IPaperService;

@Controller
@Validated
@Scope("session")
@RequestMapping("/test")
public class crawlerController {

	@Autowired
	IPaperService paperService;
	
	@Autowired
	ExcelHandler excelHandler;
	
	List<Paper> listOfPaper;
	
	@RequestMapping("/")
	public String index() {
		return "search";
	}
	
	@RequestMapping("/search")
	public String search(ModelMap modelMap, @RequestParam("keyWords") String keyWords) throws Exception {
		if(keyWords.isEmpty()) {
			modelMap.addAttribute("error_msg", "Key words cannot be empty!");
			modelMap.put("paperlist", new ArrayList<>());
		}
		else {
//			listOfPaper = paperService.Search(keyWords, "pubmed","2019");
		}
		return "search";
	}
	
	@RequestMapping("/download")
	public String download(ModelMap modelMap) throws IOException {
		if(listOfPaper==null||listOfPaper.size()==0) {
			modelMap.put("download_msg", "No Paper has been found");
		}
		else {
			modelMap.put("paperlist", listOfPaper);
			modelMap.addAttribute("error_msg", "Finish Searching. You can download now!");
			modelMap.put("download_msg", "download success");
		}
		return "search";
	}
	
}
