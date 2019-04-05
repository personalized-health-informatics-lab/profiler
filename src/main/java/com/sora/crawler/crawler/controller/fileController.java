package com.sora.crawler.crawler.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;


import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.ResourceUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.sora.crawler.crawler.handler.ExcelHandler;
import com.sora.crawler.crawler.model.Paper;
import com.sora.crawler.crawler.service.AsyncTaskService;
import com.sora.crawler.crawler.service.IPaperService;

@Controller
@Validated
@Scope("session")
public class fileController {
	
	
	private final ResourceLoader resourceLoader;

	  @Autowired
	  public fileController(ResourceLoader resourceLoader) {
	    this.resourceLoader = resourceLoader;
	  }
	
	@Autowired
	IPaperService paperService;
	
	@Autowired
	ExcelHandler excelHandler;
	
	@Autowired 
	private AsyncTaskService asyncTaskService;
	
//	List<Paper> listOfPaper ;
	
	String fileName;
	boolean firstRun;
	
	@PostConstruct  
    public void init(){  
//        listOfPaper = new ArrayList<>();  
		firstRun = true;
        fileName="";
    }  
	
	
	@RequestMapping("/")
	public String index(ModelMap modelMap) throws FileNotFoundException {
		File img = new File(ResourceUtils.getURL("classpath:").getPath());
        img = new File(img.getAbsolutePath()+"/static/img/","test.jpg");
        modelMap.put("img",resourceLoader.getResource(img.getAbsolutePath()));
		return "file";
	}
	
	@RequestMapping(value = "/uploadFileAction", method = RequestMethod.POST)
    public String uploadFileAction(@RequestParam("uploadFile") MultipartFile uploadFile, @RequestParam("id") Long id, ModelMap modelMap) throws Exception {
		if(!asyncTaskService.isFinish()) return "redirect:/";
		firstRun = false;
		asyncTaskService.setFinish(false);
		InputStream fis = null;
        OutputStream outputStream = null;
		
		try {
			
			DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss"); 
            Calendar calendar = Calendar.getInstance(); 
            String dateName = df.format(calendar.getTime())+uploadFile.getOriginalFilename();

			
//	        asyncTaskService.setSts(0);
            File upload;
            if(System.getProperty("os.name").equals("Linux")) {
            	upload = new File("/root/tmp/files/",dateName);
            }
            else {
            	upload = new File("C:\\files\\",dateName);
            }
//            File path = new File(ResourceUtils.getURL("classpath:").getPath());
//            File upload = new File(path.getAbsolutePath()+"/static/files/",dateName);
            if(!upload.exists()) {
            	upload.getParentFile().mkdirs(); 
            	upload.createNewFile();
            }
            fis = uploadFile.getInputStream();
            outputStream = new FileOutputStream(upload);
            IOUtils.copy(fis,outputStream);
	        asyncTaskService.executeAsyncTask(dateName);	
	        
	        
	        return "redirect:/";
        }catch(Exception e) {
        	e.printStackTrace();
        	throw e;
        }
    }
	
	@RequestMapping(value="/status")
	@ResponseBody
	public String isFinish() {
		if(asyncTaskService.isError()) {
			return "error! try later.";
		}
		if(firstRun) {
			return "";
		}
		else if(asyncTaskService.isFinish()) {
			return "done";
		}
		else {
			return "wait..."+"\\"+asyncTaskService.getSearchingName();
		}
		
//		sts = asyncTaskService.getSts();
//		switch (sts) {
//		case 0:
//			return "";
//		case 1:
//			return "wait...";
//		case 2:
//			return "done";
//		case 3:
//			return "error";
//		}
//		return "";
	}
	
	@RequestMapping("downloadOutputTemplate")
	public void downloadOutputTemplate(HttpServletRequest request, HttpServletResponse response)throws Exception {
//		Resource resource = new ClassPathResource("templates/examples/Example Output.xlsx");
//		File file = resource.getFile();
		File file = new File("/root/tmp/files/Example Output.xlsx");
		FileInputStream fis = new FileInputStream(file);
		response.setHeader("Content-Disposition", "attachment; filename="+file.getName());
        IOUtils.copy(fis,response.getOutputStream());
        response.flushBuffer();

	}
	
	@RequestMapping("downloadInputTemplate")
	public void downloadInputTemplate(HttpServletRequest request, HttpServletResponse response)throws Exception {
//		Resource resource = new ClassPathResource("templates/examples/Input Template.xlsx");
//		File file = resource.getFile();
		File file = new File("/root/tmp/files/Input Template.xlsx");
		FileInputStream fis = new FileInputStream(file);
		response.setHeader("Content-Disposition", "attachment; filename="+file.getName());
        IOUtils.copy(fis,response.getOutputStream());
        response.flushBuffer();

	}
	
    @RequestMapping("downloadFileAction")
    public void downloadFileAction(HttpServletRequest request, HttpServletResponse response) throws Exception {
//    	if(sts!=2) return;
    	if(!asyncTaskService.isFinish()) return;
        response.setCharacterEncoding(request.getCharacterEncoding());
        response.setContentType("application/octet-stream");
        List<List<Paper>> listOfYear = asyncTaskService.getListOfPaper();
        fileName = asyncTaskService.getFileName();
        XSSFWorkbook workbook = null;
    	try {
    		excelHandler.setNames(asyncTaskService.getNames());
    		excelHandler.setNameToNums(asyncTaskService.getNameToNums());
    		excelHandler.setProToNums(asyncTaskService.getProToNums());
			workbook = excelHandler.write(listOfYear);
			response.setHeader("Content-Disposition", "attachment; filename="+ "output"+fileName);
			workbook.write(response.getOutputStream());
            response.flushBuffer();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
//			listOfPaper = new ArrayList<>();
			asyncTaskService.setNames(new HashMap<String,String>());
			asyncTaskService.setListOfYear(new ArrayList<>());
			firstRun = true;
			if(workbook!=null) {
				try {
					workbook.close();
				} catch (IOException e) {
					e.printStackTrace();
					throw e;
				}
			}
		}   
    }
}
