<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page session="false" %>
<html>
<head>
	<script src="/resources/js/jquery-3.4.1.min.js"></script>
	<script src="/resources/js/jquery-ui.min.js"></script>
	<script src="/resources/js/common.js"></script>
	<link rel="stylesheet" href="/resources/css/jquery-ui.min.css" />
	<link rel="stylesheet" href="/resources/css/style.css" />
	<title>Mosazing</title>
	<script type="text/javascript">
	$(document).ready(function(){
		
	});
	
	function openLogFile(filenm){
		$.ajax({
 			type: "POST",
 			url : "/openLogFile.json",
 			data : {filenm : filenm},
 			success: function(results){
 				var filedata = results.filedata;
 				$("[data-id='filedata']").text(filedata);
 			},
 			error: function(data){
 				alert("E" + data);
 			}
 		})
	}
	</script>
</head>
<body>
<form id="frm" name="frm">
</form>

<h1>로그 확인</h1>

<div id="contents" class="two-step-table">
<div class="leftside">
<table class="table_border">
	<thead>
		<tr>
			<th>파일명</th>
		</tr>	
	</thead>
	<tbody>
		<c:choose>
			<c:when test="${fn:length(fileList) > 0 }">
				<c:forEach items="${fileList }" var="item">
				<tr>
					<td><a href="javascript:void(0)" onclick="javascript:openLogFile('${item.filenm }');">${item.filenm }</a></td>
				</tr>
				</c:forEach>
			</c:when>
			<c:otherwise>
				<tr>
					<td>데이터가 없습니다.</td>
				</tr>	
			</c:otherwise>
		</c:choose>		
	</tbody>
</table>
</div>
<div class="rightside">
	<table class="table_border">
		<tr>
			<td>
				<textarea data-id="filedata" rows="50" cols="250" readonly="readonly"></textarea>
			</td>
		</tr>
	</table>
</div>
</div>

<div id="selBtn_wrp">
	<a href="javascript:void(0)" onclick="javascript:onMovePage('adminPage');">adminPage</a>
<!-- 	<a href="javascript:void(0)" onclick="javascript:onMovePage('financialPage1');">재무제표 정리</a> -->
	<a href="javascript:void(0)" onclick="javascript:onMovePage('volumePage1');">시세 및 매물대 정리</a>
</div>
</body>
</html>
