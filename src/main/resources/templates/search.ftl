<!DOCTYPE html>

<html lang="en-US">
<head> 
	<meta charset="utf-8">
	<title> Paper Search </title>
</head>

<body>
	<form role="search" id="form-search-paper" method="post" enctype="multipart/form-data" action="/search">
		<div class="form-group">
            <label for="form-search-paper-keyWords">Key Words:</label>
            <input type="text" class="form-control" id="form-search-paper-keyWords"  name="keyWords" >
            <#if error_msg??>
            <div>${error_msg}</div>
            </#if>
            
        </div>
        <div class="form-group clearfix">
	        <button type="submit" class="btn pull-right btn-default" id="keyWords-submit">Search</button>
	    </div>
	</form>
	<form role="download" id="download-search-paper" method="post" action="/download">
		<div class="form-group clearfix">
	        <button type="submit" class="btn pull-right btn-default" id="download-paper">Download</button>
	    </div>
		<#if download_msg??>
            <div>${download_msg}</div>
        </#if>
	</form>
	<form role="listOfPaper" id="form-list-paper" method="get" enctype="multipart/form-data">
	    <#if paperlist??>
	    <#list paperlist as paper> 
		    
		    	<#list paper.authors as author> 
	                ${author}
	            </#list>
		    	<div> ${paper.articleTitle} </div>
		    	<div> ${paper.journalName} </div>
		    	<div> ${paper.publishTime} </div>
		    	<#if paper.pages??>
		    	<div> ${paper.pages} </div>
		    	</#if>
		    	<br />
		   
	    </#list>
	    </#if>
	</form>
	
</body>


</html>