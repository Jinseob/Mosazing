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
	
	function onExtract(){
		if(confirm("데이터를 추출 하시겠습니까?")){
			var frm = $("#frm").serialize();
			$.ajax({
	 			type: "POST",
	 			url : "/extract.do",
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
</head>
<body>
<form id="frm" name="frm">
	<input type="hidden" name="length" id="length" />
	<input type="hidden" name="offset" id="offset" />
</form>
<h1>1 Step</h1>

<P>영업이익, 매출액, 당기순이익 종목 정리</P>

<table>
	<thead>
		<tr>
			<th>번호</th>
			<th>종목코드</th>
			<th>기업명</th>
			<th>업종코드</th>
			<th>업종</th>
			<th>상장주식수(주)</th>
			<th>자본금(원)</th>
			<th>액면가(원)</th>
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
					<td><c:out value="${item.seqid }" /></td>
					<td><c:out value="${item.code }" /></td>
					<td><c:out value="${item.compnm }" /></td>
					<td><c:out value="${item.type }" /></td>
					<td><c:out value="${item.typenm }" /></td>
					<td><c:out value="${item.share }" /></td>
					<td><c:out value="${item.capital }" /></td>
					<td><c:out value="${item.parvalue }" /></td>
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
		<a href="?page=${paging.idx }" class='<c:if test="${paging.idx eq paging.page }">active</c:if>'>${paging.idx }</a>
	</c:forEach>
	</c:when>
	<c:otherwise>
		<a>1</a>
	</c:otherwise>
</c:choose>
<br/>
<c:choose>
	<c:when test="${fn:length(resultCodeList) > 0 }">
	<c:forEach items="${resultCodeList }" var = "codeList" varStatus="status">
		<c:if test="${status.count % 20 eq 1}">
		<br/>
		</c:if>
		<span>${codeList },</span>
	</c:forEach>
	</c:when>
</c:choose>

<br/>
<c:choose>
	<c:when test="${fn:length(resultNoBondList) > 0 }">
	<c:forEach items="${resultNoBondList }" var = "noBondList" varStatus="status">
		<c:if test="${status.count % 16 eq 1}">
		<br/>
		</c:if>
		<a href="https://finance.naver.com/item/coinfo.nhn?code=${noBondList.code }" target="_blank">${noBondList.code }(${noBondList.compnm })</a>,
	</c:forEach>
	</c:when>
</c:choose>

<div id="button_wrap">
	<button type="button" onclick="javascript:onExtract();">재추출</button>
</div>
</body>
</html>
