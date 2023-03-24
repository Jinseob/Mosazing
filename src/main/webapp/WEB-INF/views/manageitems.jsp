<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page session="false" %>
<html>
<head>
	<script src="/resources/js/jquery-3.4.1.min.js"></script>
	<script src="/resources/js/jquery-ui.min.js"></script>
<!-- 	<script src="/resources/js/common.js"></script> -->
	<link rel="stylesheet" href="/resources/css/jquery-ui.min.css" />
	<title>Mosazing</title>
	<script type="text/javascript">
	$(document).ready(function(){
		var companyCnt = '${resultCnt}';
		var playCnt = parseInt(companyCnt / 500) + 1;
// 		var currCnt = 0;
		
		// 2020.10.28 수정.
// 		for(var i = 0; i < playCnt; i++){
// 			$("#fnBtn_wrap").append("<button type='button' onclick='javascript:getFndata(" + i + ");' >재무제표 가져오기 - " + i + "</button>");
// 		}
		
		// From To Dialog
		$("#fromToDialog").dialog({
			autoOpen: false,
	      	resizable: false,
	      	height: "auto",
	      	width: 400,
	      	modal: true,
	      	position: { my: "center", at: "top", of: window },
	    });
		
		// File Upload Dialog
		$("#uploadDialog").dialog({
			autoOpen: false,
	      	resizable: false,
	      	height: "auto",
	      	width: 400,
	      	modal: true,
	      	position: { my: "center", at: "top", of: window },
	    });
	});
	
	function onSearchBtn(){
		$("#frm").attr({"action":"/home.do", "method":"GET"}).submit();
	}
	
	function onPaging(idx){
		$("#page").val(idx);
		$("#frm").attr({"action":location.pathname, "method":"GET"}).submit();
	}
	
	function onMovePage(page){
		$("#frm").attr({"action":"/" + page + ".do", "method":"GET"}).submit();
	}
	
// 	function onProcess(){
// 		$("#offset").val(currCnt);
// 		$("#length").val(unit);
// 		getFndata();
// 	}
	
	function btnGetNaverData(){
		$("#fromToDialog").dialog({
			title : "네이버 증시 재무제표 추출 범위 지정",
			buttons: {
				"Accept": function() {
	          		$( this ).dialog( "close" );
	        		getNaverData();
	        	},
	        	Cancel: function() {
	          		$( this ).dialog( "close" );
	        	}
			}
		});
		$("#fromToDialog").dialog("open");
	}
	
	function btnFileUpload(){
		$("#uploadDialog").dialog({
			title : "상장 정보 갱신 파일 업로드",
			buttons: {
				"Accept": function() {
	          		var division = $("input[name='division']:checked").val();
	        		if(division == null || division == ""){
	        			alert("분류를 선택해주세요.");
	        			return;
	        		}
	        		var fileinfo = $("#fileupload")[0].files[0];
	        		if(fileinfo == null){
	        			alert("파일을 선택해주세요.");
	        			return;
	        		}
	          		
	        		$( this ).dialog( "close" );
	        		onFileUpload();
	        	},
	        	Cancel: function() {
	          		$( this ).dialog( "close" );
	        	}
			}
		});
		$("#uploadDialog").dialog("open");
	}
	
	// 재무제표 정보 가져오기.
	function getFndata(currCnt){
		if(confirm(currCnt + "번째 진행하시겠습니까?")){
			var unit = 500;
			$("#length").val(unit);
			$("#offset").val(currCnt * unit);
			var frm = $("#frm").serialize();
			$.ajax({
	 			type: "POST",
	//  			async: false,
	 			url : "/DataRegister.json",
	 			dataType: "json",
	 			data : frm,
	 			success: function(results){
	 				var result = results.result;
	 				if(results.status == "S"){
	 					alert("재무제표 " + currCnt + " 가져오기 성공!");
	//  					currCnt = currCnt + 1;
	//  					getFndata();
	 				}
	 			},
	 			error: function(data){
	 				alert("E" + data);
	 			}
	 		})
		}
	}
	
	// 네이버에서 재무제표 정보 가져오기.
	function getNaverData(){
		var fPage = $("#fPage").val();
		var tPage = $("#tPage").val();
		var code = $("#code").val();
		var wics = $("#wics").val();
// 		var sort = $("#sort").val();
		
		if(confirm(fPage + " ~ " + tPage + " 까지 재무제표 정보를 가져오겠습니까?")){
// 			var frm = $("#frm").serialize();
			var data = {
				fpage : fPage,
				tpage : tPage,
				code : code,
				wics : wics
			};
			$.ajax({
	 			type: "POST",
	 			url : "/getNaverData.json",
	 			dataType: "json",
	 			data : data,
	 			success: function(results){
	 				console.log("성공");
	 			},
	 			error: function(data){
	 				alert("E" + data);
	 			}
	 		})
		}
	}
	
	// WICS 산업 분류 정보 가져오기.
	function getWICS(){
		if(confirm("WICS 산업분류 정보를 가져오겠습니까?")){
			var frm = $("#frm").serialize();
			$.ajax({
	 			type: "POST",
	 			url : "/getWICS.json",
	 			dataType: "json",
	 			data : frm,
	 			success: function(results){
	 				console.log("성공");
	 			},
	 			error: function(data){
	 				alert("E" + data);
	 			}
	 		})
		}
	}
	
	// 종목을 WICS 기준으로 분류하기. 업종 구분.
	function sortByWICS(){
		if(confirm("모든 종목을 WICS 기준으로 업종 구분을 하시겠습니까?")){
			var frm = $("#frm").serialize();
			$.ajax({
	 			type: "POST",
	 			url : "/sortByWICS.json",
	 			dataType: "json",
	 			data : frm,
	 			success: function(results){
	 				console.log("성공");
	 			},
	 			error: function(data){
	 				alert("E" + data);
	 			}
	 		})
		}
	}
	
	// 상장 정보 갱신
	function onFileUpload(){
		var division = $("input[name='division']:checked").val();
		var fileinfo = $("#fileupload")[0].files[0];
		
		var form = $("#frmFile")[0];
		var formData = new FormData(form);
		formData.append("file", fileinfo);
		formData.append("division", division);
		
		if(confirm("종목 정보를 갱신하시겠습니까?\n기존 데이터는 삭제됩니다.")){
			$.ajax({
	 			type: "POST",
	 			url : "/refreshCorp.json",
// 	 			dataType: "json",
				contentType : false,
				processData : false,
	 			data : formData,
	 			success: function(results){
	 				alert(results.msg);
	 			},
	 			error: function(data){
	 				alert("E" + data);
	 			}
	 		})
		}
	}
	
	// 제외.
	function onExcludeCode(){
		var data = $("input[name=codeChk]:checked").serialize();
		if(confirm("제외하시겠습니까?")){
			$.ajax({
	 			type: "POST",
	 			url : "/onExcludeCode.json",
	 			dataType: "json",
	 			data : data,
	 			success: function(results){
	 				alert(results.msg);
	 			},
	 			error: function(data){
	 				alert("E" + data);
	 			}
	 		})
		}
	}
	</script>
	<style type="text/css">
	.red{
		font-weight: bold;
		color: red;
	}
	.blue{
		font-weight: bold;
		color: blue;
	}
	.none{
		font-weight: bold;
		color: black;
	}
	.ull{
		font-weight: bold;
		color: 5D5D5D;
		font-size: 0.8rem;
	}
	.active{
		font-weight: bold;
	}
	.table_border{
		border-collapse: collapse;
		border: 1px solid black;
	}
	.table_border th{
		border: 1px solid black;
	}
	.table_border td{
		border: 1px solid black;
	}
	.ftinput{
		width: 50px;
	}
	</style>
</head>
<body>
<form id="frm" name="frm">
	<input type="hidden" name="page" id="page" />
	<input type="hidden" name="length" id="length" />
	<input type="hidden" name="offset" id="offset" />

<h1>국내 주식 종목</h1>
<div id="searchOption">
	<span>종목 코드 검색</span><input type="text" id="code" name="code" value="${searchOptionVO.code }"/>
	<span>업종 검색</span>
	<select id="wics" name="wics">
		<option value="">전체</option>
		<c:forEach items="${commonCodeList }" var="list">
			<option value="${list.cd }" <c:if test="${list.cd eq searchOptionVO.wics }">selected</c:if>>${list.cdnm }</option>
		</c:forEach>
	</select>
	<button type="button" onclick="javascrit:onSearchBtn()">검색</button>
</div>
<P>전체 종목 (KOSPI / KOSDAQ)</P>
</form>

<!-- From To Dialog -->
<div id="fromToDialog">
	<label><span>From : </span></label>
	<input type="text" id="fPage" class="ftinput" value="${searchOptionVO.page }" /> ~ 
	<label><span>To : </span></label>
	<input type="text" id="tPage" class="ftinput" value="${fn:length(pagingList) }" />
</div>

<!-- File Upload Dialog -->
<div id="uploadDialog">
	<label><span>파일 업로드</span></label>
	<input type="radio" id="kospi" name="division" value="KOSPI" /><label for="kospi">KOSPI</label>
	<input type="radio" id="kosdaq" name="division" value="KOSDAQ" /><label for="kosdaq">KOSDAQ</label>
	<input type="file" id="fileupload" />
</div>

<form id="frmFile" name="frmFile">
	<input type="hidden" id="fileinfo" />
</form>

<table class="table_border">
	<thead>
		<tr>
			<th>선택</th>
			<th>종목코드</th>
			<th>기업명</th>
<!-- 			<th>업종코드</th> -->
<!-- 			<th>업종</th> -->
			<th>WICS</th>
			<th>상장주식수(주)</th>
			<th>자본금(억원)</th>
			<th>액면가</th>
			<th>통화구분</th>
			<th>대표전화</th>
			<th>주소</th>
		</tr>	
	</thead>
	<tbody>
		<c:choose>
			<c:when test="${fn:length(companyList) > 0 }">
				<c:forEach items="${companyList }" var="item">
				<tr>
					<td><input type="checkbox" name="codeChk" value="${item.code }"/></td>
					<td><c:out value="${item.code }" /></td>
					<td><c:out value="${item.compnm }" /></td>
<%-- 					<td><c:out value="${item.type }" /></td> --%>
<%-- 					<td><c:out value="${item.typenm }" /></td> --%>
					<td><c:out value="${item.wicsnm }" /></td>
					<td align="right"><fmt:formatNumber value="${item.share }" pattern="#,###" /></td>
					<td align="right"><fmt:formatNumber value="${item.capital / 100000000 }" pattern="#,###" /></td>
					<td align="right"><fmt:formatNumber value="${item.parvalue }" pattern="#,###" /></td>
					<td><c:out value="${item.currency }" /></td>
					<td><c:out value="${item.tel }" /></td>
					<td><c:out value="${item.address }" /></td>
				</tr>
				</c:forEach>
			</c:when>
			<c:otherwise>
				<tr>
					<td colspan="10">데이터가 없습니다.</td>
				</tr>	
			</c:otherwise>
		</c:choose>		
	</tbody>
</table>
<c:choose>
	<c:when test="${fn:length(pagingList) > 0 }">
	<c:forEach items="${pagingList }" var="paging">
<%-- 		<a href="?page=${paging.idx }" class='<c:if test="${paging.idx eq paging.page }">active</c:if>'>${paging.idx }</a> --%>
		<a href="javascript:void(0)" onclick="javascript:onPaging(${paging.idx });" class='<c:if test="${paging.idx eq paging.page }">active</c:if>'>${paging.idx }</a>
	</c:forEach>
	</c:when>
	<c:otherwise>
		<a>1</a>
	</c:otherwise>
</c:choose>
<br/>
<!-- 2020.10.28 수정 -->
<!-- <div id="fnBtn_wrap"> -->

<!-- </div> -->
<div>
	<button type="button" onclick="javascript:btnGetNaverData();" >Naver 재무제표 가져오기</button>
	<button type="button" onclick="javascript:getWICS();" >WICS 산업분류 가져오기</button>
	<button type="button" onclick="javascript:sortByWICS();" >WICS 기준 종목 분류하기</button>
	<button type="button" onclick="javascript:btnFileUpload();" >파일 업로드</button>
	<button type="button" onclick="javascript:onExcludeCode();" >제외</button>
<!-- 	<button type="button" onclick="javascript:onRefreshCorp();" >상장 정보 갱신</button> -->
</div>
<div id="selBtn_wrp">
	<a href="javascript:void(0)" onclick="javascript:onMovePage('financialPage1');">재무제표 정리</a>
<!-- 	<a href="/firstStep.do" >로직 1단계</a> -->
</div>
</body>
</html>
