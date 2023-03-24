<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ page session="false"%>
<html>
<head>
<script src="/resources/js/jquery-3.4.1.min.js"></script>
<script src="/resources/js/jquery-ui.min.js"></script>
<script src="/resources/js/common.js"></script>
<link rel="stylesheet" href="/resources/css/style.css" />
<link rel="stylesheet" href="/resources/css/jquery-ui.min.css" />
<title>Mosazing</title>
<script type="text/javascript">
	$(document).ready(function(){

	});
	
	function onSearchBtn(){
		$("#frm").attr({"action":location.pathname, "method":"GET"}).submit();
	}
	
	function onPaging(idx){
		$("#page").val(idx);
		$("#frm").attr({"action":location.pathname, "method":"GET"}).submit();
	}
	
	function onMovePage(page){
		$("#frm").attr({"action":"/" + page + ".do", "method":"GET"}).submit();
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
	
	function ajaxData(data, url){
		$.ajax({
 			type: "POST",
 			url : "/" + url + ".json",
 			dataType: "json",
 			data : data,
 			success: function(results){
 				alert("데이터를 가지고 왔습니다. 새로고침 하세요.");
 				location.reload(true);
 			},
 			error: function(data){
 				alert("에러 발생. " + data);
 			}
 		})
	}
	</script>
</head>
<body>
	<form id="frm" name="frm">
		<input type="hidden" name="page" id="page" value="${paging.idx }" />
		<h1>Step 1</h1>

		<P>영업이익, 매출액, 당기순이익 종목 정리</P>
<!-- 		<select name="bsns_year" data-id="syear"></select> -->
		<div id="searchOption">
			<span>종목 코드 검색</span><input type="text" id="code" name="code"
				value="${searchOptionVO.code }" /> <span>업종 검색</span> <select
				id="wics" name="wics">
				<option value="">전체</option>
				<c:forEach items="${commonCodeList }" var="list">
					<option value="${list.cd }"
						<c:if test="${list.cd eq searchOptionVO.wics }">selected</c:if>>${list.cdnm }</option>
				</c:forEach>
			</select>
			<button type="button" onclick="javascrit:onSearchBtn()">검색</button>
			<input type="checkbox" id="exclude" name="exclude"
				<c:if test="${searchOptionVO.exclude eq 'on'}">checked</c:if> /><label
				for="exclude">예외 종목</label> <input type="checkbox" id="favorite"
				name="favorite"
				<c:if test="${searchOptionVO.favorite eq 'on'}">checked</c:if> /><label
				for="favorite">즐겨 찾기</label>
		</div>
	</form>

	<table class="table_border">
		<thead>
			<tr>
				<th rowspan="2">선택</th>
				<th rowspan="2">즐찾</th>
				<th rowspan="2">기업명</th>
				<th rowspan="2">과목</th>
				<th colspan="3">(억원)</th>
				<th colspan="4">동 업종(억원)</th>
				<th rowspan="2">전체 합계 추출일</th>
				<th rowspan="2">업종</th>
			</tr>
			<tr>
				<th>YY</th>
				<th>YY - 1</th>
				<th>YY - 2</th>
				<th>전체 매출액</th>
				<th>전체 영업익</th>
				<th>전체 순이익</th>
				<th>전체 시총</th>
			</tr>
		</thead>
		<tbody>
			<c:choose>
				<c:when test="${fn:length(resultList) > 0 }">
					<c:forEach items="${resultList }" var="item" varStatus="status">
						<tr>
							<c:choose>
								<c:when test="${item.account ne ''}">
									<c:if test="${item.ord eq 1 }">
										<td rowspan="${item.cnt}" align="center"><input
											type="checkbox" name="codeChk" value="${item.code }" /></td>
										<td rowspan="${item.cnt}" align="center"><input
											type="checkbox" name="codeFav" value="${item.code }"
											<c:if test="${item.favorite eq 1 }">checked</c:if> /></td>
										<td rowspan="${item.cnt}">${item.account}<c:out
												value="${item.division }" /><br /> <c:out
												value="${item.compnm }" /><br /> <a
											href="https://finance.naver.com/item/main.nhn?code=${item.code }"
											target="_blank"><c:out value="${item.code }" /></a>
										</td>
									</c:if>
									<td><c:out value="${item.account_nm }" /></td>
									<c:choose>
										<c:when test="${item.ord eq 4 }">
											<td align="right"
												class='<c:if test="${item.y1 < 0}">red</c:if> <c:if test="${item.y1 > 9 and item.y1 < 11}">goodroe</c:if>'><fmt:formatNumber
													value="${item.y1 }" pattern="#,###.##" /></td>
											<td align="right"
												class='<c:if test="${item.y2 < 0}">red</c:if> <c:if test="${item.y2 > 9 and item.y2 < 11}">goodroe</c:if>'><fmt:formatNumber
													value="${item.y2 }" pattern="#,###.##" /></td>
											<td align="right"
												class='<c:if test="${item.y3 < 0}">red</c:if> <c:if test="${item.y3 > 9 and item.y3 < 11}">goodroe</c:if>'><fmt:formatNumber
													value="${item.y3 }" pattern="#,###.##" /></td>
										</c:when>
										<c:otherwise>
											<td align="right"
												class='<c:if test="${item.y1 < 0}">red</c:if>'><fmt:formatNumber
													value="${item.y1 }" pattern="#,###" /></td>
											<td align="right"
												class='<c:if test="${item.y2 < 0}">red</c:if>'><fmt:formatNumber
													value="${item.y2 }" pattern="#,###" /></td>
											<td align="right"
												class='<c:if test="${item.y3 < 0}">red</c:if>'><fmt:formatNumber
													value="${item.y3 }" pattern="#,###" /></td>
										</c:otherwise>
									</c:choose>
									<c:if test="${item.ord eq 1 }">
										<td rowspan="${item.cnt}" align="right"><fmt:formatNumber
												value="${item.sasum }" pattern="#,###" /></td>
										<td rowspan="${item.cnt}" align="right"><fmt:formatNumber
												value="${item.bpsum }" pattern="#,###" /></td>
										<td rowspan="${item.cnt}" align="right"><fmt:formatNumber
												value="${item.cisum }" pattern="#,###" /></td>
										<td rowspan="${item.cnt}" align="right"><fmt:formatNumber
												value="${item.marketcap }" pattern="#,###" /></td>
										<td rowspan="${item.cnt}" align="center"><c:out
												value="${item.cap_dt }" /></td>
										<td rowspan="${item.cnt}" align="center"><c:out
												value="${item.cdnm }" /></td>
									</c:if>
								</c:when>
								<c:otherwise>
									<td align="center"><input type="checkbox" name="codeChk"
										value="${item.code }" /></td>
									<td align="center"><input type="checkbox" name="codeFav"
										value="${item.code }"
										<c:if test="${item.favorite eq 1 }">checked</c:if> /></td>
									<td>${item.division }<br /> ${item.compnm }<br /> <a
										href="https://finance.naver.com/item/main.nhn?code=${item.code }"
										target="_blank">${item.code }</a>
									</td>
									<td colspan="4" align="center">-</td>
									<td align="right"><fmt:formatNumber value="${item.sasum }"
											pattern="#,###" /></td>
									<td align="right"><fmt:formatNumber value="${item.bpsum }"
											pattern="#,###" /></td>
									<td align="right"><fmt:formatNumber value="${item.cisum }"
											pattern="#,###" /></td>
									<td align="right"><fmt:formatNumber
											value="${item.marketcap }" pattern="#,###" /></td>
									<td align="center"><c:out value="${item.cap_dt }" /></td>
									<td align="center"><c:out value="${item.cdnm }" /></td>
								</c:otherwise>
							</c:choose>
						</tr>
					</c:forEach>
				</c:when>
				<c:otherwise>
					<tr>
						<td colspan="11">데이터가 없습니다.</td>
					</tr>
				</c:otherwise>
			</c:choose>
		</tbody>
	</table>
	<c:choose>
		<c:when test="${fn:length(pagingList) > 0 }">
			<c:forEach items="${pagingList }" var="paging">
				<%-- 		<a href="?page=${paging.idx }" class='<c:if test="${paging.idx eq paging.page }">active</c:if>'>${paging.idx }</a> --%>
				<a href="javascript:void(0)"
					onclick="javascript:onPaging(${paging.idx });"
					class='<c:if test="${paging.idx eq paging.page }">active</c:if>'>${paging.idx }</a>
			</c:forEach>
		</c:when>
		<c:otherwise>
			<a>1</a>
		</c:otherwise>
	</c:choose>
	<br />
	<div id="button_wrap">
		<button type="button" onclick="javascript:onExtract();">3 재무
			이익 종목 추출</button>
		<button type="button" onclick="javascript:onSumFinancial();">산업분류별
			합계 추출</button>
		<button type="button" onclick="javascript:getMarketCap();">전체
			업종 시가 총액 추출</button>
		<button type="button" onclick="javascript:onExcludeCode(1);">제외</button>
	</div>
	<div id="selBtn_wrp">
		<a href="javascript:void(0)" onclick="javascript:onMovePage('home');">Home</a>
		<a href="javascript:void(0)"
			onclick="javascript:onMovePage('volumePage1');">시세 및 매물대 정리</a> <a
			href="javascript:void(0)" onclick="javascript:onMovePage('log');">Log
			화면</a>
	</div>

	<jsp:include page="/WEB-INF/views/common/common.jsp" />
</body>
</html>
