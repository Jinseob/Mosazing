<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page session="false" %>
<html>
<head>
	<!-- Chart.js -->
	<script src="/resources/js/Chart.bundle.js"></script>
	<!-- Chart.js End -->
	<script src="/resources/js/jquery-3.4.1.min.js"></script>
	<script src="/resources/js/jquery-ui.min.js"></script>
	<script src="/resources/js/common.js"></script>
	<link rel="stylesheet" href="/resources/css/jquery-ui.min.css" />
	<link rel="stylesheet" href="/resources/css/style.css" />
	<title>Mosazing</title>
	<script type="text/javascript">
	$(document).ready(function(){
		getVolume();
		onRealTimeSise("Y");
		getMaxVolume();
		setInterval("onRealTimeSise()", 5000); // 매 5000ms(5초)가 지날 때마다 ozit_timer_test() 함수를 실행합니다.
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
	
	function getVolume(){
		var codeList = $("#codeList").val().replace(/[\[\]]/g, "");
		$.ajax({
 			type: "POST",
 			url : "/getVolume.json",
 			dataType: "json",
 			data: { codeList : codeList},
 			global:false,
 			success: function(results){
//  				var data = JSON.parse(results.json);
				// 데이터가 없는 경우 처리하는 상태값 필요.
				var codeList = results.searchOptionVO.codeList;
				var volumeList = results.volumeList;
				var chkCodes = "";
				var vData = new Array();
				for(var i = 0; i < codeList.length; i++){
					chkCodes += codeList[i] + ",";	
				}
				
				for(var nm in volumeList){
					var list = volumeList[nm];
					for(var i = 0; i < list.length; i++){
						var item = list[i];
						if(chkCodes.indexOf(item.code) > -1){
							vData.push(item);
						}
						if(vData.length >= 10){
							drawChart('myChart' + nm + item.code, vData);
							vData = new Array();
						}
					}
				}
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
	<input type="hidden" name="page" id="page"/>
	<input type="hidden" name="length" id="length" />
	<input type="hidden" name="offset" id="offset" />
<h1>Step 2</h1>

<P>매물대 정리</P>
<select name="division">
	<option value="" <c:if test="${searchOptionVO.division eq '' }">selected</c:if>>전체 증시</option>
	<option value="KOSPI" <c:if test="${searchOptionVO.division eq 'KOSPI' }">selected</c:if>>KOSPI</option>
	<option value="KOSDAQ" <c:if test="${searchOptionVO.division eq 'KOSDAQ' }">selected</c:if>>KOSDAQ</option>
</select>
<select name="bsns_year" data-id="syear"></select>
<select name="type">
	<option value="" <c:if test="${searchOptionVO.type eq '' }">selected</c:if>>없음</option>
	<option value="SA" <c:if test="${searchOptionVO.type eq 'SA' }">selected</c:if>>매출액</option>
	<option value="BP" <c:if test="${searchOptionVO.type eq 'BP' }">selected</c:if>>영업익</option>
	<option value="CI" <c:if test="${searchOptionVO.type eq 'CI' }">selected</c:if>>순이익</option>
</select>
<select name="sort">
	<option value="" <c:if test="${searchOptionVO.sort eq '' }">selected</c:if>>없음</option>
	<option value="D" <c:if test="${searchOptionVO.sort eq 'D' }">selected</c:if>>높은순</option>
	<option value="A" <c:if test="${searchOptionVO.sort eq 'A' }">selected</c:if>>낮은순</option>
</select>
<button type="button" onclick="javascript:onMovePage('volumePage1')">전체보기</button>
<div id="sortVolumeOption">
	<label for="checkA">반기</label><input type="checkbox" id="checkA" name="checkA" <c:if test="${searchOptionVO.checkA eq 'on' }">checked</c:if>>
	<label for="checkB">1년</label><input type="checkbox" id="checkB" name="checkB" <c:if test="${searchOptionVO.checkB eq 'on' }">checked</c:if>>
	<label for="checkC">2년</label><input type="checkbox" id="checkC" name="checkC" <c:if test="${searchOptionVO.checkC eq 'on' }">checked</c:if>>
	<label for="checkD">3년</label><input type="checkbox" id="checkD" name="checkD" <c:if test="${searchOptionVO.checkD eq 'on' }">checked</c:if>>
</div>
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
</form>

<input type="hidden" name="codeList" id="codeList" value="${codeList}"/>
<table class="table_border">
	<colgroup>
		<col width="120px" />
<%-- 		<col width="40px" /> --%>
		<col width="90px" />
		<col width="80px" />
		<col width="90px" />
		<col width="90px" />
		<col width="90px" />
		<col width="90px" />
		<col width="90px" />
		<col width="130px" />
		<col width="260px" />
		<col width="260px" />
		<col width="260px" />
		<col width="260px" />
	</colgroup>
	<thead>
		<tr>
			<th rowspan="2">기업명</th>
<!-- 			<th>타입</th> -->
			<th rowspan="2">과목</th>
			<th colspan="3">(억원)</th>
			<th rowspan="2"r>추천가</th>
			<th>저가</th>
			<th>고가</th>
			<th>현재가</th>
			<th rowspan="2">반기 매물대 가격(천)</th>
			<th rowspan="2">1년 매물대 가격(천)</th>
			<th rowspan="2">2년 매물대 가격(천)</th>
			<th rowspan="2">3년 매물대 가격(천)</th>
		</tr>
		<tr>
			<th>YY</th>
			<th>YY-1</th>
			<th>YY-2</th>
			<th>(하한)</th>
			<th>(상한)</th>
			<th>(전일)</th>
		</tr>		
	</thead>
	<tbody>
		<c:choose>
			<c:when test="${fn:length(resultList) > 0 }">
				<c:forEach items="${resultList }" var="item" varStatus="status">
				<tr data-id="${item.code }">
					<c:if test="${item.ord eq 1}">
						<td rowspan="${item.cnt}">
							<c:out value="${item.division }" /><br/>
							<c:out value="${item.compnm }" /><br/>
							<a href="https://finance.naver.com/item/main.nhn?code=${item.code }" target="_blank" ><c:out value="${item.code }" /></a><br/>
							<span data-index="sh"><fmt:formatNumber value="${item.share }" pattern="#,###" /></span><br/>
							<span data-index="mp" class="none">-</span>(억원) 
						</td>
<%-- 						<td rowspan="3"><c:out value="${item.type }" /></td> --%>
					</c:if>
					<td><c:out value="${item.account_nm }" /></td>
					<c:choose>
						<c:when test="${item.ord eq 4}">
							<td align="right" class='<c:if test="${item.y1 < 0}">red</c:if> <c:if test="${item.y1 > 9 and item.y1 < 11}">goodroe</c:if>'><fmt:formatNumber value="${item.y1 }" pattern="#,###.##" /></td>
							<td align="right" class='<c:if test="${item.y2 < 0}">red</c:if> <c:if test="${item.y2 > 9 and item.y2 < 11}">goodroe</c:if>'><fmt:formatNumber value="${item.y2 }" pattern="#,###.##" /></td>
							<td align="right" class='<c:if test="${item.y3 < 0}">red</c:if> <c:if test="${item.y3 > 9 and item.y3 < 11}">goodroe</c:if>'><fmt:formatNumber value="${item.y3 }" pattern="#,###.##" /></td>
						</c:when>
						<c:otherwise>
							<td align="right" class='<c:if test="${item.y1 < 0}">red</c:if>'><fmt:formatNumber value="${item.y1 }" pattern="#,###" /></td>
							<td align="right" class='<c:if test="${item.y2 < 0}">red</c:if>'><fmt:formatNumber value="${item.y2 }" pattern="#,###" /></td>
							<td align="right" class='<c:if test="${item.y3 < 0}">red</c:if>'><fmt:formatNumber value="${item.y3 }" pattern="#,###" /></td>	
						</c:otherwise>
					</c:choose>
					<c:choose>
						<c:when test="${item.account eq 'BP' }">
						<td align="right" data-id="expected-bp"><fmt:formatNumber value="${item.expectedbp }" pattern="#,###" /></td>
						<c:set var="expectedval" value="${item.expectedval}"/>
						</c:when>
						<c:when test="${item.account eq 'SA' }">
						<td align="right" data-id="expected-sa"><fmt:formatNumber value="${item.expectedsa }" pattern="#,###" /></td>
						</c:when>
						<c:when test="${item.account eq 'CI' }">
						<td align="right" data-id="expected-ci"><fmt:formatNumber value="${item.expectedci }" pattern="#,###" /></td>
						</c:when>
						<c:otherwise>
						<td align="right" data-id="expected-val"><fmt:formatNumber value="${expectedval }" pattern="#,###" /></td>
						</c:otherwise>
					</c:choose>
<%-- 					<td>${status.index % 3 } | ${status.count % 3 } | ${status.index / 3 } | ${status.count / 3 } | ${status.index % 3 eq 0 }</td> --%>
					<c:if test="${item.ord eq 1 }">
						<td rowspan="${item.cnt}" align="center"><span data-index="low">저가</span><br/><span data-index="ll">하한가</span></td>
						<td rowspan="${item.cnt}" align="center"><span data-index="high">고가</span><br/><span data-index="ul">상한가</span></td>
						<td rowspan="${item.cnt}" align="center"><span data-index="real">현재가</span><br/><span data-index="sv">전일가</span><br/><span data-index="cv">전일대비</span>|<span data-index="cr">전일대비%</span><span data-index="vol">거래량</span></td>
						<td rowspan="${item.cnt}">
							<div style="width: 250px; height: 150px;">
								<canvas id="myChartA${item.code}" width="250" height="150"></canvas>
							</div>
						</td>
						<td rowspan="${item.cnt}">
							<div style="width: 250px; height: 150px;">
								<canvas id="myChartB${item.code}" width="250" height="150"></canvas>
							</div>
						</td>
						<td rowspan="${item.cnt}">
							<div style="width: 250px; height: 150px;">
								<canvas id="myChartC${item.code}" width="250" height="150"></canvas>
							</div>
						</td>
						<td rowspan="${item.cnt}">
							<div style="width: 250px; height: 150px;">
								<canvas id="myChartD${item.code}" width="250" height="150"></canvas>
							</div>
						</td>					
					</c:if>
				</tr>
				</c:forEach>
			</c:when>
			<c:otherwise>
				<tr>
					<td colspan="13">데이터가 없습니다.</td>
				</tr>	
			</c:otherwise>
		</c:choose>		
	</tbody>
</table>
<c:choose>
	<c:when test="${fn:length(pagingList) > 0 }">
	<c:forEach items="${pagingList }" var="paging">
		<a href="javascript:void(0)" onclick="javascript:onPaging(${paging.idx });" class='<c:if test="${paging.idx eq paging.page }">active</c:if>'>${paging.idx }</a>
	</c:forEach>
	</c:when>
	<c:otherwise>
		<a>1</a>
	</c:otherwise>
</c:choose>
<br/>
<div id="button_wrap">
<!-- 	<button type="button" onclick="javascript:onDaySise();">일자별 시세 추출</button> -->
<!-- 	<button type="button" onclick="javascript:onExtractVolume();">매물대 추출</button> -->
<!-- 	<button type="button" onclick="javascript:getMarketCap();">전체 업종 시가 총액 추출</button> -->

<!-- 	<button type="button" onclick="javascript:onExtract();">재추출</button> -->
</div>
<div id="selBtn_wrp">
	<a href="javascript:void(0)" onclick="javascript:onMovePage('home');">Home</a>
	<a href="javascript:void(0)" onclick="javascript:onMovePage('financialPage1');">재무제표 정리</a>
	<a href="javascript:void(0)" onclick="javascript:onMovePage('log');">Log 화면</a>
<!-- 	<a href="/firstStep.do" >로직 1단계</a> -->
<!-- 	<a href="/thirdStep.do" >로직 3단계</a> -->
</div>

<jsp:include page="/WEB-INF/views/common/common.jsp" flush="true"/>
</body>
</html>
