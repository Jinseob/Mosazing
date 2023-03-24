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
		
	});
	
	// 저장된 재무제표 정보에서 매출액, 영업이익, 당기순이익이 플러스인 종목 추출.
	function onExtract(){
		if(confirm("데이터를 추출 하시겠습니까?")){
			var frm = $("#frm").serialize();
			$.ajax({
	 			type: "POST",
	 			url : "/extract1.json",
	 			dataType: "json",
	 			data : frm,
	 			success: function(results){
	 				alert("데이터를 가지고 왔습니다. 새로고침 하세요.");
	 				location.reload(true);
	 			},
	 			error: function(data){
	 				alert("에러 발생. " + data);
	 			}
	 		})
		}
	}
	
	// 산업분류별 합계
	function onSumFinancial(){
		if(confirm("산업분류별 데이터를 추출 하시겠습니까?")){
			var frm = $("#frm").serialize();
			$.ajax({
	 			type: "POST",
	 			url : "/sumFinancial.json",
	 			dataType: "json",
	 			data : frm,
	 			success: function(results){
	 				alert("데이터를 가지고 왔습니다. 새로고침 하세요.");
	 				location.reload(true);
	 			},
	 			error: function(data){
	 				alert("에러 발생. " + data);
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
	</style>
</head>
<body>
<form id="frm" name="frm">
	<input type="hidden" name="page" id="page" value="${paging.idx }"/>
<h1>Step 1</h1>

<P>영업이익, 매출액, 당기순이익 종목 정리</P>
<select name="bsns_year">
	<option value="2017">2017</option>
	<option value="2018">2018</option>
	<option value="2019" selected="selected">2019</option>
</select>
</form>

<table>
	<thead>
		<tr>
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
					<c:if test="${status.index % 3 eq 0 }">
						<td rowspan="3">
							<c:out value="${item.division }" /><br/>
							<c:out value="${item.compnm }" /><br/>
							<a href="https://finance.naver.com/item/main.nhn?code=${item.code }" target="_blank" ><c:out value="${item.code }" /></a>
						</td>
					</c:if>
					<td><c:out value="${item.account_nm }" /></td>
					<td align="right"><fmt:formatNumber value="${item.y1 }" pattern="#,###" /></td>
					<td align="right"><fmt:formatNumber value="${item.y2 }" pattern="#,###" /></td>
					<td align="right"><fmt:formatNumber value="${item.y3 }" pattern="#,###" /></td>
					<c:if test="${status.index % 3 eq 0 }">
						<td rowspan="3" align="right"><fmt:formatNumber value="${item.sasum }" pattern="#,###" /></td>
						<td rowspan="3" align="right"><fmt:formatNumber value="${item.bpsum }" pattern="#,###" /></td>
						<td rowspan="3" align="right"><fmt:formatNumber value="${item.cisum }" pattern="#,###" /></td>
						<td rowspan="3" align="right"><fmt:formatNumber value="${item.marketcap }" pattern="#,###" /></td>
						<td rowspan="3" align="center"><c:out value="${item.cap_dt }" /></td>
						<td rowspan="3" align="center"><c:out value="${item.cdnm }" /></td>
					</c:if>
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
		<a href="?page=${paging.idx }" class='<c:if test="${paging.idx eq paging.page }">active</c:if>'>${paging.idx }</a>
	</c:forEach>
	</c:when>
	<c:otherwise>
		<a>1</a>
	</c:otherwise>
</c:choose>
<br/>
<div id="button_wrap">
	<button type="button" onclick="javascript:onExtract();">재추출</button>
	<button type="button" onclick="javascript:onSumFinancial();">산업분류별 합계 추출</button>
</div>
<div id="selBtn_wrp">
	<a href="/" >Home</a>
	<a href="/secondStep.do" >로직 2단계</a>
</div>
</body>
</html>
