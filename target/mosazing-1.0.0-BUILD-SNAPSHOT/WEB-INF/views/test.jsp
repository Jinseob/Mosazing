<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page session="false" %>
<html>
<head>
	<script src="/resources/js/jquery-3.4.1.min.js"></script>
	<link rel="stylesheet" href="/resources/css/style.css" />
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
<header>
	<h1>주식 종목 정리</h1>
</header>
<nav>
	<div id="searchOption">
		<form id="frm" name="frm">
			<input type="hidden" name="currpage" id="currpage"/>
			
		<select name="division">
			<option value="" <c:if test="${searchOptionVO.division eq '' }">selected</c:if>>전체 증시</option>
			<option value="KOSPI" <c:if test="${searchOptionVO.division eq 'KOSPI' }">selected</c:if>>KOSPI</option>
			<option value="KOSDAQ" <c:if test="${searchOptionVO.division eq 'KOSDAQ' }">selected</c:if>>KOSDAQ</option>
		</select>
		<!-- <select name="bsns_year" data-id="syear"></select> -->
		<select name="account">
			<option value="" <c:if test="${searchOptionVO.account eq '' }">selected</c:if>>전체</option>
			<option value="SA" <c:if test="${searchOptionVO.account eq 'SA' }">selected</c:if>>매출액</option>
			<option value="BP" <c:if test="${searchOptionVO.account eq 'BP' }">selected</c:if>>영업익</option>
			<option value="CI" <c:if test="${searchOptionVO.account eq 'CI' }">selected</c:if>>순이익</option>
			<option value="VO" <c:if test="${searchOptionVO.account eq 'VO' }">selected</c:if>>거래량</option>
			<option value="X" <c:if test="${searchOptionVO.account eq 'X' }">selected</c:if>>없음</option>
		</select>
		<select name="sort">
			<option value="" <c:if test="${searchOptionVO.sort eq '' }">selected</c:if>>전체</option>
			<option value="D" <c:if test="${searchOptionVO.sort eq 'D' }">selected</c:if>>높은순</option>
			<option value="A" <c:if test="${searchOptionVO.sort eq 'A' }">selected</c:if>>낮은순</option>
		</select>
		<select name="section">
			<option value="" <c:if test="${searchOptionVO.section eq '' }">selected</c:if>>전체</option>
			<option value="3Y" <c:if test="${searchOptionVO.section eq '3Y' }">selected</c:if>>3년통합</option>
		</select>
		<!-- <button type="button" onclick="javascript:onMovePage('secondStep')">3년간 수익중인 종목만 보기</button> -->
		<div id="sortVolumeOption">
			<label for="checkA">½년</label><input type="checkbox" id="checkA" name="checkA" <c:if test="${searchOptionVO.checkA eq 'on' }">checked</c:if>>
			<label for="checkB">1년</label><input type="checkbox" id="checkB" name="checkB" <c:if test="${searchOptionVO.checkB eq 'on' }">checked</c:if>>
			<label for="checkC">2년</label><input type="checkbox" id="checkC" name="checkC" <c:if test="${searchOptionVO.checkC eq 'on' }">checked</c:if>>
			<label for="checkD">3년</label><input type="checkbox" id="checkD" name="checkD" <c:if test="${searchOptionVO.checkD eq 'on' }">checked</c:if>>
		</div>
		<div>
			<input type="text" id="code" name="code" value="${searchOptionVO.code }" placeholder="종목 코드 검색"/>
			<select id="wics" name="wics">
				<option value="">업종 전체</option>
				<c:forEach items="${commonCodeList }" var="list">
					<option value="${list.cd }" <c:if test="${list.cd eq searchOptionVO.wics }">selected</c:if>>${list.cdnm }</option>
				</c:forEach>
			</select>
			<button type="button" onclick="javascrit:onSearchBtn()">검색</button>
		<%-- 	<input type="checkbox" id="exclude" name="exclude" <c:if test="${searchOptionVO.exclude eq 'on'}">checked</c:if>/><label for="exclude">예외 종목</label> --%>
			<input type="checkbox" id="favorite" name="favorite" <c:if test="${searchOptionVO.favorite eq 'on'}">checked</c:if>/><label for="favorite">즐겨 찾기</label>
		</div>
		<!-- <div> -->
		<!-- 	<button type="button" onclick="javascript:onExcludeCode(1);" >제외</button> -->
		<!-- </div> -->
		</form>
	</div>
	<div id="mo_kakao_adfit">
<!-- 		<ins class="kakao_ad_area" style="display:none;"  -->
<!-- 		 data-ad-unit    = "DAN-JgYOvK5vhRZspIoa"  -->
<!-- 		 data-ad-width   = "320"  -->
<!-- 		 data-ad-height  = "100" -->
<!-- 		 style="float:right;"></ins>  -->
<!-- 		<script type="text/javascript" src="//t1.daumcdn.net/kas/static/ba.min.js" async></script> -->
	</div>
</nav>

<div id="button_wrap">
	<button type="button" id="ajaxBtn">Ajax Test</button>
</div>
<div id="paging_wrap">
	<a href="javascript:void(0)">이전</a>
	<a href="javascript:void(0)">1</a>
	<a href="javascript:void(0)">2</a>
	<a href="javascript:void(0)" class="active">3</a>
	<a href="javascript:void(0)">4</a>
	<a href="javascript:void(0)">5</a>
	<a href="javascript:void(0)">6</a>
	<a href="javascript:void(0)">7</a>
	<a href="javascript:void(0)">8</a>
	<a href="javascript:void(0)">9</a>
	<a href="javascript:void(0)">10</a>
	<a href="javascript:void(0)">다음</a>
</div>
</body>
</html>
