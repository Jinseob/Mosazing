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
		var companyCnt = '${resultCnt}';
		var playCnt = parseInt(companyCnt / 500) + 1;
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
	
	function btnGetNaverData(){
		$("#fromToDialog").dialog({
			title : "네이버 증시 재무제표 추출 범위 지정",
			buttons: {
				"Accept": function() {
	          		$( this ).dialog( "close" );
	          		getNaverDataP();
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
// 	          		var division = $("input[name='division']:checked").val();
// 	        		if(division == null || division == ""){
// 	        			alert("분류를 선택해주세요.");
// 	        			return;
// 	        		}
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
	 			url : "/DataRegister.json",
	 			dataType: "json",
	 			data : frm,
	 			success: function(results){
	 				var result = results.result;
	 				if(results.status == "S"){
	 					alert("재무제표 " + currCnt + " 가져오기 성공!");
	 				}
	 			},
	 			error: function(data){
	 				alert("E" + data);
	 			}
	 		})
		}
	}
	
	// 네이버에서 재무제표 정보 가져오기. Progress Bar 사용.
	function getNaverDataP(){
		var fPage = $("#fPage").val();
		var tPage = $("#tPage").val();
		
		if(confirm(fPage + " ~ " + tPage + " 까지 재무제표를 가져오시겠습니까?")){
			$("#progress-dialog").dialog({
				title: "재무제표 가져오기"
			}).dialog("open");
			var totalCnt = parseInt(tPage) - parseInt(fPage) + 1;
			var idx = 1;
			var from = fPage;
			var to = tPage;
			ajaxDataFromTo(idx, from, to, totalCnt, "getNaverData");
		}
	}
	
	function btnFromToDaySise(){
		$("#fromToDialog").dialog({
			title : "일자별 시세 추출 범위 지정",
			buttons: {
				"Accept": function() {
	          		$( this ).dialog( "close" );
// 	        		onFromToDaySise();
	          		onFromToDaySiseP();
	        	},
	        	Cancel: function() {
	          		$( this ).dialog( "close" );
	        	}
			}
		});
		$("#fromToDialog").dialog("open");
	}
	
	function btnFromToVolume(){
		$("#fromToDialog").dialog({
			title : "매물대 추출 범위 지정",
			buttons: {
				"Accept": function() {
	          		$( this ).dialog( "close" );
// 	          		onFromToExtractVolume();
	          		onFromToExtractVolumeP();
	        	},
	        	Cancel: function() {
	          		$( this ).dialog( "close" );
	        	}
			}
		});
		$("#fromToDialog").dialog("open");
	}
	
	function btnCompareVolumeSise(){
		$("#fromToDialog").dialog({
			title : "비교 범위 지정",
			buttons: {
				"Accept": function() {
	          		$( this ).dialog( "close" );
// 	          		onCompareVolumeSise();
	          		onCompareVolumeSiseP();
	        	},
	        	Cancel: function() {
	          		$( this ).dialog( "close" );
	        	}
			}
		});
		$("#fromToDialog").dialog("open");
	}
	
	// From To 페이지 일자별 시세 추출. Progress Bar 사용.
	function onFromToDaySiseP(){
		var fPage = $("#fPage").val();
		var tPage = $("#tPage").val();
		
		if(confirm(fPage + " ~ " + tPage + " 까지 일자별 시세를 추출 하시겠습니까?")){
			$("#progress-dialog").dialog({
				title: "일자별 시세 추출"
			}).dialog("open");
			var totalCnt = parseInt(tPage) - parseInt(fPage) + 1;
			var idx = 1;
			var from = fPage;
			var to = tPage;
			ajaxDataFromTo(idx, from, to, totalCnt, "fromToDaySise");
		}
	}
	
	// From To 페이지 매물대 추출. Progress Bar 사용.
	function onFromToExtractVolumeP(){
		var fPage = $("#fPage").val();
		var tPage = $("#tPage").val();
		
		if(confirm(fPage + " ~ " + tPage + " 까지 매물대를 추출 하시겠습니까?")){
			$("#progress-dialog").dialog({
				title: "매물대 추출"
			}).dialog("open");
			var totalCnt = parseInt(tPage) - parseInt(fPage) + 1;
			var idx = 1;
			var from = fPage;
			var to = tPage;
			ajaxDataFromTo(idx, from, to, totalCnt, "fromToExtractVolume");
		}
	}
	
	// 매물대와 시세 비교. Progress Bar 사용.
	function onCompareVolumeSiseP(){
		var fPage = $("#fPage").val();
		var tPage = $("#tPage").val();
		
		if(confirm(fPage + " ~ " + tPage + " 까지 매물대와 시세를 비교 하시겠습니까?")){
			$("#progress-dialog").dialog({
				title: "매물대와 시세 비교"
			}).dialog("open");
			var totalCnt = parseInt(tPage) - parseInt(fPage) + 1;
			var idx = 1;
			var from = fPage;
			var to = tPage;
			ajaxDataFromTo(idx, from, to, totalCnt, "compareVolumeSise");
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
// 		var division = $("input[name='division']:checked").val();
		var fileinfo = $("#fileupload")[0].files[0];
		
		var form = $("#frmFile")[0];
		var formData = new FormData(form);
		formData.append("file", fileinfo);
// 		formData.append("division", division);
		
		if(confirm("종목 정보를 갱신하시겠습니까?\n기존 데이터는 삭제됩니다.")){
			$.ajax({
	 			type: "POST",
	 			url : "/refreshCorpV1.json",
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
	
	// 현재 화면 일자별 시세 추출
	function onCurrPageDaySise(){
		if(confirm("현재 화면 일자별 시세를 추출 하시겠습니까?")){
			var codeList = $("#codeList").val().replace(/[\[\]]/g, "");
			$.ajax({
				type: "POST",
	 			url : "/currPageDaySise.json",
	 			dataType: "json",
	 			data: { codeList : codeList},
	 			success: function(results){
	 				alert("일자별 시세를 가지고 왔습니다. 새로고침 하세요.");
	 				location.reload(true);
	 			},
	 			error: function(data){
	 				alert("에러 발생. " + data);
	 			}
	 		})
		}
	}
	
	// 현재 페이지 매물대 추출.
	function onExtractVolume(){
		if(confirm("매물대 데이터를 추출 하시겠습니까?")){
			var codeList = $("#codeList").val().replace(/[\[\]]/g, "");
			$.ajax({
	 			type: "POST",
	 			url : "/extractVolume.json",
	 			dataType: "json",
	 			data: { codeList : codeList},
	 			success: function(results){
	 				alert("매물대 데이터를 가지고 왔습니다. 새로고침 하세요.");
	 				location.reload(true);
	 			},
	 			error: function(data){
	 				alert("에러 발생. " + data);
	 			}
	 		})
		}
	}
	
	// 저장된 재무제표 정보에서 매출액, 영업이익, 당기순이익이 플러스인 종목 추출.
	function onExtract(){
		var dialog = $("#confirmDialogY").dialog({
			title : "3 재무 이익 종목을 추출하시겠습니까?",
			buttons:{
				OK : function(){
					$(this).dialog("close");
					var data = {bsns_year : $("#syear").val()};
					ajaxData(data, "extract1");
				},
				Cancel : function(){
					$(this).dialog("close");
				}
			}
		});
		dialog.dialog("open");
	}
	
	// 산업분류별 합계
	function onSumFinancial(){
		var dialog = $("#confirmDialogY").dialog({
			title : "산업분류별 데이터를 추출 하시겠습니까?",
			buttons:{
				OK : function(){
					$(this).dialog("close");
					var data = {bsns_year : $("#syear").val()};
					ajaxData(data, "sumFinancial");
				},
				Cancel : function(){
					$(this).dialog("close");
				}
			}
		});
		dialog.dialog("open");		
	}
	
	// 전체 업종 시가 총액 추출.
	function getMarketCap(){
		var dialog = $("#confirmDialogY").dialog({
			title : "전체 업종 시가 총액을 추출 하시겠습니까?",
			buttons:{
				OK : function(){
					$(this).dialog("close");
					var data = {bsns_year : $("#syear").val()};
					ajaxData(data, "getMarketCapNaver");
				},
				Cancel : function(){
					$(this).dialog("close");
				}
			}
		});
		dialog.dialog("open");
	}
	</script>
</head>
<body>
<form id="frm" name="frm">
	<input type="hidden" name="page" id="page" />
	<input type="hidden" name="length" id="length" />
	<input type="hidden" name="offset" id="offset" />

<h1>국내 주식 종목 관리 화면</h1>
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
	<input type="checkbox" id="exclude" name="exclude" <c:if test="${searchOptionVO.exclude eq 'on'}">checked</c:if>/><label for="exclude">예외 종목</label>
	<input type="checkbox" id="favorite" name="favorite" <c:if test="${searchOptionVO.favorite eq 'on'}">checked</c:if>/><label for="favorite">즐겨 찾기</label>
</div>
<P>전체 종목 (KOSPI / KOSDAQ)</P>
<div>
	<button type="button" onclick="javascript:onExcludeCode(1);" >제외</button>
</div>
</form>

<form id="frmFile" name="frmFile">
	<input type="hidden" id="fileinfo" />
</form>

<table class="table_border">
	<colgroup>
		<col width="20px" />
		<col width="20px" />
		<col width="60px" />
		<col width="200px" />
		<col width="80px" />
		<col width="200px" />
		<col width="150px" />
		<col width="80px" />
		<col width="120px" />
		<col width="120px" />
		<col width="120px" />
	</colgroup>
	<thead>
		<tr>
			<th>선택</th>
			<th>즐찾</th>
			<th>종목코드</th>
			<th>기업명</th>
			<th>시장구분</th>
			<th>WICS</th>
<!-- 			<th>주식종류</th> -->
			<th>상장주식수(주)</th>
			<th>액면가</th>
			<th>시세 갱신일</th>
			<th>매물대 갱신일</th>
			<th>비교 갱신일</th>
		</tr>	
	</thead>
	<tbody>
		<c:choose>
			<c:when test="${fn:length(companyList) > 0 }">
				<jsp:useBean id="now" class="java.util.Date" />
<%-- 				<fmt:parseDate var="sTime1" value="${now }" pattern="yyyyMMdd"/> --%>
<%-- 				<fmt:parseDate var="sTime2" value="1600" pattern="HHmm"/> --%>
<%-- 				<fmt:parseNumber var="sHH" value="${((now + sTime) / (1000*60)) / 60 }" /> --%>
				<c:forEach items="${companyList }" var="item">
				<tr>
					<td align="center"><input type="checkbox" name="codeChk" value="${item.code }"/></td>
					<td align="center"><input type="checkbox" name="codeFav" value="${item.code }" <c:if test="${item.favorite eq 1 }">checked</c:if>/></td>
					<td><c:out value="${item.code }" /></td>
					<td><c:out value="${item.compnm }" /></td>
					<td><c:out value="${item.division }" /></td>
					<td><c:out value="${item.wicsnm }" /></td>
<!-- 					<td>주식종류</td> -->
					<td align="right"><fmt:formatNumber value="${item.share }" pattern="#,###" /></td>
					<td align="right"><fmt:formatNumber value="${item.parvalue }" pattern="#,###" /></td>
					<td align="center" <c:if test="${item.siseChk eq 'Y' }">class="renewed"</c:if>><fmt:formatDate value="${item.sise_dt }" pattern="yyyy-MM-dd"/></td>
					<td align="center" <c:if test="${item.volumeChk eq 'Y' }">class="renewed"</c:if>><fmt:formatDate value="${item.volume_dt }" pattern="yyyy-MM-dd"/></td>
					<td align="center" <c:if test="${item.compareChk eq 'Y' }">class="renewed"</c:if>><fmt:formatDate value="${item.compare_dt }" pattern="yyyy-MM-dd"/></td>
				</tr>
				</c:forEach>
			</c:when>
			<c:otherwise>
				<tr>
					<td colspan="12">데이터가 없습니다.</td>
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
	1. <button type="button" onclick="javascript:btnFileUpload();" >종목 정보 갱신용 파일 업로드</button> <a href="http://data.krx.co.kr/contents/MDC/MDI/mdiLoader/index.cmd?menuId=MDC0201020101" target="_blank">한국거래소</a><br/>
	2. <button type="button" onclick="javascript:btnGetNaverData();" >Naver 재무제표 가져오기</button><br/>
	3. <button type="button" onclick="javascript:getWICS();" >WICS 산업분류 가져오기</button>
	<button type="button" onclick="javascript:sortByWICS();" >WICS 기준 종목 분류하기</button><br/>
	4. <button type="button" onclick="javascript:onExtract();">3 재무 이익 종목 추출</button>
	<button type="button" onclick="javascript:onSumFinancial();">산업분류별 합계 추출</button>
	<button type="button" onclick="javascript:getMarketCap();">전체 업종 시가 총액 추출</button><br/>
	
	5. <button type="button" onclick="javascript:btnFromToDaySise();">지정 페이지 일자별 시세 추출</button>
	<button type="button" onclick="javascript:onCurrPageDaySise();">현재 페이지 일자별 시세 추출</button><br/>
	6. <button type="button" onclick="javascript:btnFromToVolume();">지정 페이지 매물대 추출</button>
	<button type="button" onclick="javascript:onExtractVolume();">현재 페이지 매물대 추출</button><br/>
	7. <button type="button" onclick="javascript:btnCompareVolumeSise();">매물대와 시세 비교</button>
</div>
<div id="selBtn_wrp">
	<a href="javascript:void(0)" onclick="javascript:onMovePage('volumePage1');">시세 및 매물대 정리</a>
	<a href="javascript:void(0)" onclick="javascript:onMovePage('log');">Log 화면</a>
<!-- 	<a href="/firstStep.do" >로직 1단계</a> -->
</div>

<jsp:include page="/WEB-INF/views/common/common.jsp" flush="true"/>
</body>
</html>
