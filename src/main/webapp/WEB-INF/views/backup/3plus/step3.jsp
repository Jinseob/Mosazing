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
	</script>
</head>
<body>
<form id="frm" name="frm">
	<input type="hidden" name="length" id="length" />
	<input type="hidden" name="offset" id="offset" />
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
			<th>종목코드</th>
			<th>기업명</th>
			<th>타입</th>
			<th>과목</th>
			<th>YYYY</th>
			<th>YYYY - 1</th>
			<th>YYYY - 2</th>
		</tr>	
	</thead>
	<tbody>
		<c:choose>
			<c:when test="${fn:length(resultList) > 0 }">
				<c:forEach items="${resultList }" var="item">
				<tr>
					<td><a href="https://finance.naver.com/item/coinfo.nhn?code=${item.code }" target="_blank" ><c:out value="${item.code }" /></a></td>
					<td><c:out value="${item.compnm }" /></td>
					<td><c:out value="${item.type }" /></td>
					<td><c:out value="${item.account_nm }" /></td>
					<td><fmt:formatNumber value="${item.thstrm_amount }" /></td>
					<td><fmt:formatNumber value="${item.frmtrm_amount }" /></td>
					<td><fmt:formatNumber value="${item.bfefrmtrm_amount }" /></td>
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
<div id="selBtn_wrp">
	<a href="/secondStep.do" >로직 2단계</a>
</div>
<div id="button_wrap">
	<button type="button" onclick="javascript:onExtract();">재추출</button>
</div>
</body>
</html>
