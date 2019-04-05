package com.sora.crawler.crawler.handler;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFPicture;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.sora.crawler.crawler.helper.logHelper;
import com.sora.crawler.crawler.model.Paper;
import com.sora.crawler.crawler.model.chartModel;

@Component
@Scope("session")
public class ExcelHandler {
	private HashMap<String,String> names = new HashMap<>();
	
	private static String head = "https://www.ncbi.nlm.nih.gov/pubmed/?term=";
	
	private List<HashMap<String,Integer>> nameToNums = new ArrayList<>();
	private List<HashMap<String,Integer>> ProToNums = new ArrayList<>();
	
	private HashMap<String,Integer> map2018 = new HashMap<>();
	private HashMap<String,Integer> map2017 = new HashMap<>();

	public List<HashMap<String, Integer>> getNameToNums() {
		return nameToNums;
	}

	public void setNameToNums(List<HashMap<String, Integer>> nameToNums) {
		this.nameToNums = nameToNums;
	}

	public List<HashMap<String, Integer>> getProToNums() {
		return ProToNums;
	}

	public void setProToNums(List<HashMap<String, Integer>> proToNums) {
		ProToNums = proToNums;
	}

	public void setNames(HashMap<String, String> names) {
		this.names = names;
	}
	
	private List<String> programs = new ArrayList<>();

//	public String PATH = "/Users/sora/Desktop";
	
	public XSSFWorkbook write(List<List<Paper>> listOfYear) throws Exception {
		XSSFWorkbook workbook = new XSSFWorkbook();
		for(int x = 0 ; x < listOfYear.size() ; x++) {
			List<Paper> listOfPaper = listOfYear.get(x);
			XSSFSheet sheet = workbook.createSheet(2017+x+"");
			XSSFRow row ;
			row = sheet.createRow(0);
			setTitle(row);
			for(int y = 0 ; y<listOfPaper.size() ; y++) {
				row = sheet.createRow(y+1);
				setContent(row,listOfPaper.get(y),workbook);
			}
		}
		XSSFSheet sheet = workbook.createSheet("NameSum");
		List<chartModel> list = SummaryOfName(sheet);
		JFreeChart chart = createChrartBar(list);
		passChartToExcel(chart,workbook,sheet);
		sheet = workbook.createSheet("ProgramSum");
		list = SummaryOfProgram(sheet,listOfYear);
		chart = createChrartBar(list);
		passChartToExcel(chart,workbook,sheet);
		return workbook;
	}
	
	
	
	public List<chartModel> SummaryOfName(XSSFSheet sheet) {
		List<chartModel> list = new ArrayList<>();
		XSSFRow row = sheet.createRow(0); 
		Cell cell = row.createCell(0);
		cell.setCellValue("Researcher Name");
		cell = row.createCell(1);
		cell.setCellValue("SUM 2017");
		cell = row.createCell(2);
		cell.setCellValue("SUM 2018");
		int count = 0;
		for(Entry entry: nameToNums.get(0).entrySet()) {
			chartModel cm2017 = new chartModel();
			chartModel cm2018 = new chartModel();
			cm2017.setYear("2017");
			cm2017.setName(entry.getKey()+"");
			cm2017.setNum(entry.getValue()+"");
			cm2018.setYear("2018");
			cm2018.setName(entry.getKey()+"");
			cm2018.setNum(nameToNums.get(1).get(entry.getKey())+"");
			count++;
			row = sheet.createRow(count);
			cell = row.createCell(0);
			cell.setCellValue(entry.getKey()+"");
			cell = row.createCell(1);
			cell.setCellValue(entry.getValue()+"");		
			cell = row.createCell(2);
			cell.setCellValue(nameToNums.get(1).get(entry.getKey())+"");	
			list.add(cm2017);
			list.add(cm2018);
		}
		return list;
	}
	
	public List<chartModel> SummaryOfProgram(XSSFSheet sheet, List<List<Paper>> listOfYear) {
		List<chartModel> list = new ArrayList<>();
		XSSFRow row = sheet.createRow(0);
		Cell cell = row.createCell(0);
		cell.setCellValue("Program Name");
		cell = row.createCell(1);
		cell.setCellValue("Sum 2017");
		cell = row.createCell(2);
		cell.setCellValue("Sum 2018");
		int count = 0;
		
		
		for(Entry entry: map2018.entrySet()) {
			chartModel cm2017 = new chartModel();
			chartModel cm2018 = new chartModel();
			cm2017.setYear("2017");
			cm2017.setName(entry.getKey()+"");
			cm2017.setNum(map2017.get(entry.getKey())+"");
			cm2018.setYear("2018");
			cm2018.setName(entry.getKey()+"");
			cm2018.setNum(entry.getValue()+"");
			count++;
			row = sheet.createRow(count);
			cell = row.createCell(0);
			cell.setCellValue(entry.getKey()+"");
			cell = row.createCell(1);
			cell.setCellValue(map2017.get(entry.getKey())+"");		
			cell = row.createCell(2);
			cell.setCellValue(entry.getValue()+"");	
			list.add(cm2017);
			list.add(cm2018);
		}
		return list;
	}
	public XSSFRichTextString getAllAuthors(Paper paper, XSSFWorkbook workbook, HashMap<String,Integer> listOfIndex) {
		List<String> authors = paper.getAuthors();
		StringBuilder sb = new StringBuilder();
		List<int[]> indexs = new ArrayList<>();
		for(int x = 0 ; x < authors.size() ; x++) {
			String tempName = authors.get(x);
			String[] nameSeprate = tempName.split(", ");
			int start = sb.length();
			String  tempname1 = "";
			String tempname2 = "";
			tempname1=nameSeprate[0];
			if(nameSeprate.length==1) {
				sb.append(nameSeprate[0]);
			}
			else if(nameSeprate.length==2) {
				sb.append(nameSeprate[0] + " " + nameSeprate[1].charAt(0));
				nameSeprate = nameSeprate[1].split(" ");
				if(nameSeprate.length==2&&nameSeprate[1].length()>0) {
					sb.append(".");
					sb.append(nameSeprate[1].charAt(0));
				}
				if(nameSeprate.length>1)
				tempname2 = tempname1 +", "+ nameSeprate[1];
				tempname1+=", "+nameSeprate[0];
				
			}
//			System.out.println(tempname);
//			System.out.println(names.toString());
			tempname1 = tempname1.replace("é", "e");
			tempname2 = tempname2.replace("é", "e");
			tempname1 = tempname1.replace("í", "i");
			tempname2 = tempname2.replace("í", "i");
			if(names.containsKey(tempname1.toLowerCase())) {
				int end = sb.length();
				indexs.add(new int[] {start,end});
				int i = 1;
				if(listOfIndex.containsKey(names.get(tempname1.toLowerCase())))
				i = listOfIndex.get(names.get(tempname1.toLowerCase()))+1;
				listOfIndex.put(names.get(tempname1.toLowerCase()),i);
			}
			else if(names.containsKey(tempname2.toLowerCase())) {
				int end = sb.length();
				indexs.add(new int[] {start,end});
				int i = 1;
				if(listOfIndex.containsKey(names.get(tempname2.toLowerCase())))
				i = listOfIndex.get(names.get(tempname2.toLowerCase()))+1;
				listOfIndex.put(names.get(tempname2.toLowerCase()),i);
			}
			if(x!=authors.size()-1) {
				sb.append(".,");
			}
		}
		XSSFRichTextString richString = new XSSFRichTextString( sb.toString() );
		XSSFFont font= workbook.createFont();
		font.setBold(true);
		for(int y = 0 ; y < indexs.size() ; y++) {
			richString.applyFont(indexs.get(y)[0],indexs.get(y)[1],font);
		}
		return richString;
	}
	
	public void setContent(XSSFRow row, Paper paper, XSSFWorkbook workbook) {
		Cell cell;
		HashMap<String,Integer> listOfIndex = new HashMap<>();
		XSSFRichTextString richString = getAllAuthors(paper,workbook,listOfIndex);
		int count = 0;
		for(int x = programs.size()+1 ; x < programs.size()*2+1 ; x++) {
			cell = row.createCell(x);
			for(Entry<String, Integer> entry: listOfIndex.entrySet()) {
				if(programs.get(x-1-programs.size()).equals(entry.getKey())) {
					count++;
					if(paper.getYear().equals("2018")) {
						if(!map2018.containsKey(entry.getKey())) {
							map2018.put(entry.getKey(), 0);
						}
						map2018.put(entry.getKey(), 1+map2018.get(entry.getKey()));
					}
					else if(paper.getYear().equals("2017")) {
						if(!map2017.containsKey(entry.getKey())) {
							map2017.put(entry.getKey(), 0);
						}
						map2017.put(entry.getKey(), 1+map2017.get(entry.getKey()));
					}
					cell.setCellValue("X");
					if(entry.getValue()>1) {
						Cell celltemp = row.createCell(x-programs.size());
						celltemp.setCellValue("X");
					}
				}
//				else cell.setCellValue("");
			}
		}
		if(count>1) {
			cell = row.createCell(0);
			cell.setCellValue("X");
		}
		int len = programs.size()*2+1;
		cell = row.createCell(0+len);
		cell.setCellValue(richString);
		cell = row.createCell(1+len);
		cell.setCellValue(paper.getArticleTitle());
		cell = row.createCell(2+len);
//		cell.setCellValue(Link));
		String title = paper.getArticleTitle();
		title = title.replace(" ", "+");
		title = title.replace(":", "%3A");
		title = title.replace("+", "%2B");
		title = title.replace("/", "%2F");
		cell.setCellValue(head+title);
		cell = row.createCell(3+len);
		cell.setCellValue(paper.getJournalName());
		cell = row.createCell(4+len);
		cell.setCellValue(paper.getVolume());
		cell = row.createCell(5+len);
		cell.setCellValue(paper.getIssue());
		cell = row.createCell(6+len);
		cell.setCellValue("");
		String[] page = {"0","0"};
		if(paper.getPages()==null) {
			cell = row.createCell(7+len);
			cell.setCellValue("");
			cell = row.createCell(8+len);
			cell.setCellValue("");
		}
		else {
			page = paper.getPages().split("-");
			cell = row.createCell(7+len);
			cell.setCellValue(page[0]);
			cell = row.createCell(8+len);
			if(page.length>1)
			cell.setCellValue(page[1]);
			else cell.setCellValue("");
		}
		int start=0;
		int end=0;
		if(page.length>1) {
			try {
				   start = Integer.parseInt(page[0]);
				   end = Integer.parseInt(page[1]);
				}
				catch (NumberFormatException e)
				{
				   start = 0;
				   end = 0;
				}
		}
		cell = row.createCell(9+len);
		if(start==0&&end==0) {
			cell.setCellValue("");
		}
		else {
			cell.setCellValue(end-start);
		}
		cell = row.createCell(10+len);
		cell.setCellValue(""); //art no
		cell = row.createCell(11+len);
		cell.setCellValue(paper.getDOI());
		cell = row.createCell(12+len);
		cell.setCellValue(paper.getAbstract());
		cell = row.createCell(13+len);
		cell.setCellValue(paper.getDocumentType());
		cell = row.createCell(14+len);
		cell.setCellValue(paper.getPublicationStage());
		cell = row.createCell(15+len);
		cell.setCellValue(""); //access type
		cell = row.createCell(16+len);
		cell.setCellValue("pubmed");
		cell = row.createCell(17+len);
		cell.setCellValue(paper.getPMID());
		cell = row.createCell(18+len);
		cell.setCellValue(paper.getYear());
		for(int x = 0 ; x < paper.getAuthors().size() ; x++) {
			cell = row.createCell(19+x+len);
			cell.setCellValue(paper.getAuthors().get(x));
		}
	}
	
	public void setTitle(XSSFRow row) {
		
		Cell cell;
		cell = row.createCell(0);
		cell.setCellValue("Inter");
		for(int x = 1 ; x < programs.size()+1 ; x++) {
			cell = row.createCell(x);
			cell.setCellValue(programs.get(x-1)+" Intra");
		}
		for(int x = programs.size()+1 ; x < programs.size()*2 +1; x++) {
			cell = row.createCell(x);
			cell.setCellValue(programs.get(x-1-programs.size()));
		}
		int len = programs.size()*2+1;
		cell = row.createCell(0+len);
		cell.setCellValue("Authors");
		cell = row.createCell(1+len);
		cell.setCellValue("Title");
		cell = row.createCell(2+len);
		cell.setCellValue("Link");
		cell = row.createCell(3+len);
		cell.setCellValue("Source title");
		cell = row.createCell(4+len);
		cell.setCellValue("Volume");
		cell = row.createCell(5+len);
		cell.setCellValue("Issue");
		cell = row.createCell(6+len);
		cell.setCellValue("Art. No.");
		cell = row.createCell(7+len);
		cell.setCellValue("Page start");
		cell = row.createCell(8+len);
		cell.setCellValue("Page end");
		cell = row.createCell(9+len);
		cell.setCellValue("Page count");
		cell = row.createCell(10+len);
		cell.setCellValue("Cited by");
		cell = row.createCell(11+len);
		cell.setCellValue("DOI");
		cell = row.createCell(12+len);
		cell.setCellValue("Abstract");
		cell = row.createCell(13+len);
		cell.setCellValue("Document Type");
		cell = row.createCell(14+len);
		cell.setCellValue("Publication Stage");
		cell = row.createCell(15+len);
		cell.setCellValue("Access Type");
		cell = row.createCell(16+len);
		cell.setCellValue("Source");
		cell = row.createCell(17+len);
		cell.setCellValue("PMID");
		cell = row.createCell(18+len);
		cell.setCellValue("Year");
		cell = row.createCell(19+len);
		cell.setCellValue("Authors Full Name");
		
	}
	
	public HashMap<String,String> getAuthorsName(InputStream fis) throws Exception{
//		System.out.println("Reading name...");
		logHelper.info("Reading name...");
	    XSSFWorkbook workbook = new XSSFWorkbook(fis);
	    XSSFSheet sheet = workbook.getSheetAt(0);
	    XSSFRow row;
	    Iterator<Row> rowIterator = sheet.rowIterator();
//	    List<String> listOfName = new ArrayList<>();
	    HashMap<String, String> nameToProgram = new HashMap<>();
	    HashSet<String> programs = new HashSet<>();
	    rowIterator.next();
	    while(rowIterator.hasNext()) {
	    	row = (XSSFRow) rowIterator.next();
	        Cell nameCell = row.getCell(0);
	        String name = "";
	        if(nameCell==null) break;
            switch(nameCell.getCellType() ) {
            case NUMERIC:
            	double val = nameCell.getNumericCellValue();
                System.out.print(val+"\t\t");
                break ;
            case STRING:
                String str = nameCell.getStringCellValue();
                name = str;
                break;
            }
	        //plus proposed program
            
            Cell programCell = row.getCell(1);
            String program = "";
            switch(programCell.getCellType() ) {
            case NUMERIC:
            	double val = programCell.getNumericCellValue();
                System.out.print(val+"\t\t");
                break ;
            case STRING:
                String str = programCell.getStringCellValue();
                program = str;
                programs.add(program);           
                break;
            }
            if(name.length()>0&&program.length()>0)
            nameToProgram.put(name, program);
	    }
	    this.programs = new ArrayList<>(programs);
	    fis.close();
	    workbook.close();
	    return nameToProgram;
	}
	
	public void passChartToExcel(JFreeChart myPieChart,Workbook my_workbook,XSSFSheet my_sheet) {
		int width=640; /* Width of the chart */
        int height=480; /* Height of the chart */
        float quality=1; /* Quality factor */
		ByteArrayOutputStream chart_out = new ByteArrayOutputStream();
		try {
			ChartUtilities.writeChartAsJPEG(chart_out,quality,myPieChart,width,height);
			InputStream feed_chart_to_excel=new ByteArrayInputStream(chart_out.toByteArray());
            byte[] bytes = IOUtils.toByteArray(feed_chart_to_excel);
            /* Add picture to workbook */
            int my_picture_id = my_workbook.addPicture(bytes, Workbook.PICTURE_TYPE_JPEG);
            /* We can close Piped Input Stream. We don't need this */
            feed_chart_to_excel.close();
            /* Close PipedOutputStream also */
            chart_out.close();
            /* Create the drawing container */
            XSSFDrawing drawing = my_sheet.createDrawingPatriarch();
            /* Create an anchor point */
            ClientAnchor my_anchor = new XSSFClientAnchor();
            my_anchor.setCol1(4);
            my_anchor.setRow1(5);
            /* Invoke createPicture and pass the anchor point and ID */
            XSSFPicture  my_picture = drawing.createPicture(my_anchor, my_picture_id);
            /* Call resize method, which resizes the image */
            my_picture.resize();
            
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	public JFreeChart createChrartBar(List<chartModel> list) {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		for(int i=0;list!=null && i<list.size();i++){
			chartModel cm = list.get(i);
			dataset.addValue(Integer.parseInt(cm.getNum()),cm.getYear(),cm.getName());
		}
		JFreeChart  chart = ChartFactory.createBarChart("Difference",
				"Name",
				"Number",
				dataset, 
				PlotOrientation.VERTICAL, 
				true, 
				true, 
				true); 
		
		
		return chart;
	}
	
}
