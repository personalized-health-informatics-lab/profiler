<!DOCTYPE html>
<html lang="en" xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8"/>
    <title>Title</title>
    
	<script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/1.11.1/jquery.min.js"></script>
	<link rel="stylesheet" href="/css/theme.css" />
</head>

<div class="header">
<h1>Welcome to Profiler!</h1>
</div>

<div class="briefInstruction">
	If you're new to Profiler, click 'Instructions' for directions and how to use this site.
</div>

<body  ms-controller="viewmodel"> 
<div class="input-form">
<form id="upload" enctype="multipart/form-data" method="post" action="/uploadFileAction">
    <div class="input-group file-upload">
    	<label class="input-group-btn">
    	<span class="btn btn-light">
    		Browse
    	 <input type="file" name="uploadFile" id="file" class="inputfile" accept=".xlsx" onchange="handleFile()"/>
    	</span>
    	</label>
    	<input type="text" class="form-control" id="fileName" readonly>
    	<input type="button" class="button button1" value="Upload and Search" onclick="doUpload()"/>
    </div>
    <input type="hidden" name="id" value="12"/>
</form>
</div>
<div id="divCheckbox" class="wait-pic">
	<span class="status"> </span>
	<input type="text" style="display: none;" id="nameBox" class="searching-content" id="searching-content" readonly>
</div>
<br>
<br>
<div class="get-result">
	<input id = "down" type="button" class="button button2" value="Get Results" onclick="doDownload()" disabled="true"/>
	<input id = "explain" type="button" class="button button2" value="Instructions" onclick="showBg()"/>
	<form id="download" method="post" action="/downloadFileAction">
	</form>
</div>


</form>




<div id="dialog" style="display:hide">
        
    <div class="tutorial"> 
    Profiler Instructions<br><br>
    Profiler is a powerful tool that searches Pubmed based on your list of names and codes. It produces a spreadsheet that features lots of useful information about the authors, including how many publications per year, which journals they are featured in, etc. Once the search is complete, you can download the spreadsheet with its analysis! <br><br><br>
    1.	Profiler accepts only Excel Spreadsheets, formatted with headers<br>
	2.	Column 'A' should have a header like ‘Researcher Name’; names in this column should follow the format ‘Last Name, First Name’<br>
	3.	Column 'B' should have a header like 'Program Name'; you can choose to put a two- or three-digit code here, and the authors will be sorted according to these codes<br>
	4.	When ready to upload your spreadsheet, click ‘Browse’, then select your file<br>
	5.	To begin the search, click 'Upload and Search'<br>
	6.  When the search is complete, click 'Get Results!' to download your results.<br>
	<img src="/img/template.png" >
	</div>
	
	<div class="get-example-inside">
		Click here to download:<br>
		<input id = "inputExample" type="button" class="button button2" value="Input Template" onclick="doDownloadInputExample()"/>
		<input id = "outputExample" type="button" class="button button2" value="Output Example" onclick="doDownloadOutputExample()"/>
		<form id="downloadOutputExample" method="post" action="/downloadOutputTemplate">
		</form>
		<form id="downloadIntputExample" method="post" action="/downloadInputTemplate">
		</form>
	</div>
	<div class="closs-button">
    <input id = "close" type="button" class="button button2" value="Close" onclick="closeBg()"/>
    </div>
    
</div>


<script type="text/javascript">
	var file = document.getElementById("file");
	var fileName = document.getElementById("fileName");
	$('#file').on('change',()=>{
		var arr=file.value.split('\\');
        var name=arr[arr.length-1];
		fileName.value = name;
	})
	
    function doUpload() {
        var upl = document.getElementById("upload");
        upl.submit();
    }
    function doDownload() {
        var upl = document.getElementById("download");
        upl.submit();
    }
    function doDownloadOutputExample() {
        var upl = document.getElementById("downloadOutputExample");
        upl.submit();
    }
    function doDownloadInputExample() {
        var upl = document.getElementById("downloadIntputExample");
        upl.submit();
    }
    
    check();
    function check(){
    	let interval = setInterval(function(){
	    	$.ajax({
	            type: "get",
	            url: "/status",  
	            data: "",
	            success: function (data) {
	                console.log(data);
	                
	                if(data!="done")
	                $("#down").attr("disabled","true");
	                else
	                $("#down").removeAttr("disabled");
	                if(data.substring(0,7)=="wait..."){
	                	$("#nameBox").removeAttr("style");
	                	var arr=data.split('\\')
	                	$(".status").text("working on...");
	                	$(".searching-content").val(arr[1]);
	                }
	                else{
	                	$("#nameBox").attr("style","display: none;");
	                	$(".status").text(data);
	                }
	            }
	        });
	    },1000)
    }
    
	function closeBg() {
        $("#dialog").hide();
    }
	function showBg() {
        $("#dialog").show();
    }
    
    $(document).mouseup(function(e){
	  var _con = $("#dialog");   // 设置目标区域
	  if(!_con.is(e.target) && _con.has(e.target).length === 0){ // Mark 1
	    $("#dialog").hide();
	  }
	});
</script>

</body>
</html>
