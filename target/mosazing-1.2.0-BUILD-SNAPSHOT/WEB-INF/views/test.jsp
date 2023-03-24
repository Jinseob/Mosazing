<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page session="false" %>
<html>
<head>
	<script src="/resources/js/jquery-3.4.1.min.js"></script>
	<title>Mosazing</title>
	<script type="text/javascript">
	$(document).ready(function(){
		$("#ajaxBtn").click(function(){
			$.ajax({
	 			type: "POST",
	 			url : "/ajaxTest.json",
	 			dataType: "json",
	 			data : {name : "홍길동"},
	 			success: function(results){
	 				alert(results.msg);				
	 			},
	 			error: function(data){
	 				alert("에러 발생. " + data);
	 			}
	 		})
		});
	});
	</script>
</head>
<body>
<form id="frm" name="frm">
	<input type="hidden" name="id" id="id" />
</form>
<h1>Ajax Test</h1>

<div id="button_wrap">
	<button type="button" id="ajaxBtn">Ajax Test</button>
</div>
</body>
</html>
