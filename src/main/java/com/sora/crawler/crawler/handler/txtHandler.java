package com.sora.crawler.crawler.handler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.List;

import com.sora.crawler.crawler.model.Paper;



public class txtHandler {
	
	private String windowsPath = "C:/";
	private String MacPath = "/Users/sora/Downloads/";
	private String filenameTemp;

	
	public boolean creatTxtFile(String name) throws IOException {
		boolean flag = false;
		String sysName = System.getProperty("os.name");
		if(sysName.equals("Mac OS X")||sysName.equals("Mac OS")) { 
			filenameTemp = MacPath + name + ".txt";
		}
		else if(sysName.equals("Windows")) {
			filenameTemp = windowsPath + name + ".txt";
		}
		File filename = new File(filenameTemp);
		if (!filename.exists()) {
			filename.createNewFile();
			flag = true;
		}
		return flag;
	}
	
	public boolean writeTxtFile(List<Paper> listOfPaper) throws IOException {
		// 先读取原有文件内容，然后进行写入操作
		boolean flag = false;
		
		String temp = "";
 
		FileInputStream fis = null;
		InputStreamReader isr = null;
		BufferedReader br = null;
 
		FileOutputStream fos = null;
		PrintWriter pw = null;
		try {
			File file = new File(filenameTemp);
			fis = new FileInputStream(file);
			isr = new InputStreamReader(fis);
			br = new BufferedReader(isr);
			StringBuffer buf = new StringBuffer();
 
			
			while((temp = br.readLine()) != null) {
				buf = buf.append(temp);
				buf = buf.append(System.getProperty("line.separator"));
			}
			for(int x = 0 ; x < listOfPaper.size(); x++) {
				buf = writeAPaperToTxt(buf,listOfPaper.get(x));
			}
 
			fos = new FileOutputStream(file);
			pw = new PrintWriter(fos);
			pw.write(buf.toString().toCharArray());
			pw.flush();
			flag = true;
		} catch (IOException e1) {
			// TODO 自动生成 catch 块
			throw e1;
		} finally {
			if (pw != null) {
				pw.close();
			}
			if (fos != null) {
				fos.close();
			}
			if (br != null) {
				br.close();
			}
			if (isr != null) {
				isr.close();
			}
			if (fis != null) {
				fis.close();
			}
		}
		return flag;
	}
	
	public StringBuffer writeAPaperToTxt(StringBuffer buf, Paper paper) {
		buf = buf.append(paper.getArticleTitle());
		buf = buf.append(System.getProperty("line.separator"));
		for(int x = 0 ; x < paper.getAuthors().size() ; x++) {
			buf = buf.append(paper.getAuthors().get(x));
			if(x!=paper.getAuthors().size()-1)
			buf = buf.append(" | ");
		}
		buf = buf.append(System.getProperty("line.separator"));
		buf = buf.append(paper.getJournalName());
		buf = buf.append(System.getProperty("line.separator"));
		if(paper.getPages()!=null) {
			buf = buf.append(paper.getPages());
			buf = buf.append(System.getProperty("line.separator"));
		}
		buf = buf.append(paper.getPublishTime());
		buf = buf.append(System.getProperty("line.separator"));
		buf = buf.append(System.getProperty("line.separator"));
		return buf;
	}


}
