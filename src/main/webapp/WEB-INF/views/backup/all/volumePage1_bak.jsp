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
		if($("#codeList").val() != ""){
			getVolume();
			onRealTimeSise("Y");
			getMaxVolume();
			setInterval("onRealTimeSise()", 5000); // 매 5000ms(5초)가 지날 때마다 ozit_timer_test() 함수를 실행합니다.
		}
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
	
	// 사채 데이터 추출. 이 부분은 사실 불확실해서 제외 하는 방향으로 생각
	function onExtract(){
		if(confirm("사채 데이터를 추출 하시겠습니까?")){
			var frm = $("#frm").serialize();
			$.ajax({
	 			type: "POST",
	 			url : "/extract2.json",
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
			ajaxData(idx, from, to, totalCnt, "fromToDaySise");
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
			ajaxData(idx, from, to, totalCnt, "fromToExtractVolume");
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
			ajaxData(idx, from, to, totalCnt, "compareVolumeSise");
		}
	}
	
	// Ajax
	function ajaxData(idx, from, to, totalCnt, gubun){
		var code = $("#code").val();
		var wics = $("#wics").val();
		var type = $("#type").val();
		var sort = $("#sort").val();
		
		var url = "/" + gubun + ".json";
		var data = {
				fpage : parseInt(from) + parseInt(idx) - 1,
				tpage : parseInt(from) + parseInt(idx) - 1,
				code : code,
				wics : wics,
				type : type,
				sort : sort
		}
		$.ajax({
 			type: "POST",
 			url : url,
 			dataType: "json",
 			data: data,
 			global:false,
 			success: function(results){
 				var percent = parseInt(100 / totalCnt * idx);
 				progress(percent, idx, totalCnt);
 				idx++;
 				if(idx <= totalCnt){
 					ajaxData(idx, from, to, totalCnt, gubun);
 				}
 			},
 			error: function(data){
 				alert("에러 발생. " + data);
 			}
 		})
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
	
// 	function onRecommentSellPrice(){
// 		if(confirm("추천 매도가를 추출 하시겠습니까?")){
// 			var frm = $("#frm").serialize();
// 			$.ajax({
// 	 			type: "GET",
// 	 			url : "/recommendSellPrice.json" + location.search,
// 	 			dataType: "json",
// 	 			success: function(results){
// 	 				alert("데이터를 가지고 왔습니다. 새로고침 하세요.");
// 	 				location.reload(true);
// 	 			},
// 	 			error: function(data){
// 	 				alert("에러 발생. " + data);
// 	 			}
// 	 		})
// 		}
// 	}
	
	// 매물대 조회.
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
				
				// 차트
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
	<input type="hidden" name="lastpage" id="lastpage"/>
	<input type="hidden" name="length" id="length" />
	<input type="hidden" name="offset" id="offset" />
<h1>Step 2</h1>

<P>매물대 정리</P>
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
<button type="button" onclick="javascript:onMovePage('secondStep')">3년간 수익중인 종목만 보기</button>
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
	<input type="checkbox" id="exclude" name="exclude" <c:if test="${searchOptionVO.exclude eq 'on'}">checked</c:if>/><label for="exclude">예외 종목</label>
	<input type="checkbox" id="favorite" name="favorite" <c:if test="${searchOptionVO.favorite eq 'on'}">checked</c:if>/><label for="favorite">즐겨 찾기</label>
</div>
</form>

<input type="hidden" name="codeList" id="codeList" value="${codeList}"/>
<table class="table_border">
	<colgroup>
		<col width="20px" />
		<col width="20px" />
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
			<th rowspan="2">선택</th>
			<th rowspan="2">즐찾</th>
			<th rowspan="2">기업명</th>
<!-- 			<th>타입</th> -->
			<th rowspan="2">과목</th>
			<th colspan="3">(억원)</th>
			<th rowspan="2">추천가</th>
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
					<c:choose>
						<c:when test="${item.account ne ''}">
							<c:if test="${item.ord eq 1}">
								<td rowspan="${item.cnt}" align="center"><input type="checkbox" name="codeChk" value="${item.code }"/></td>
								<td rowspan="${item.cnt}" align="center"><input type="checkbox" name="codeFav" value="${item.code }" <c:if test="${item.favorite eq 1 }">checked</c:if>/></td>
								<td rowspan="${item.cnt}">
									<c:out value="${item.division }" /><br/>
									<c:out value="${item.compnm }" /><br/>
									<a href="https://finance.naver.com/item/main.nhn?code=${item.code }" target="_blank" ><c:out value="${item.code }" /></a><br/>
									<span data-index="sh"><fmt:formatNumber value="${item.share }" pattern="#,###" /></span><br/>
									<span data-index="mp" class="none">-</span>(억원) 
								</td>
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
								<td rowspan="${item.cnt}" align="center"><span data-index="real">현재가</span><br/><span data-index="sv">전일가</span><br/><span data-index="cv">전일대비</span>|<span data-index="cr">전일대비%</span><br/><span data-index="vol">거래량</span></td>
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
						</c:when>
						<c:otherwise>
							<td align="center"><input type="checkbox" name="codeChk" value="${item.code }"/></td>
							<td align="center"><input type="checkbox" name="codeFav" value="${item.code }" <c:if test="${item.favorite eq 1 }">checked</c:if>/></td>
							<td>
								<c:out value="${item.division }" /><br/>
								<c:out value="${item.compnm }" /><br/>
								<a href="https://finance.naver.com/item/main.nhn?code=${item.code }" target="_blank" ><c:out value="${item.code }" /></a><br/>
								<span data-index="sh"><fmt:formatNumber value="${item.share }" pattern="#,###" /></span><br/>
								<span data-index="mp" class="none">-</span>(억원) 
							</td>
							<td colspan="5" align="center">-</td>
							<td align="center"><span data-index="low">저가</span><br/><span data-index="ll">하한가</span></td>
							<td align="center"><span data-index="high">고가</span><br/><span data-index="ul">상한가</span></td>
							<td align="center"><span data-index="real">현재가</span><br/><span data-index="sv">전일가</span><br/><span data-index="cv">전일대비</span>|<span data-index="cr">전일대비%</span><br/><span data-index="vol">거래량</span></td>
							<td>
								<div style="width: 250px; height: 150px;">
									<canvas id="myChartA${item.code}" width="250" height="150"></canvas>
								</div>
							</td>
							<td>
								<div style="width: 250px; height: 150px;">
									<canvas id="myChartB${item.code}" width="250" height="150"></canvas>
								</div>
							</td>
							<td>
								<div style="width: 250px; height: 150px;">
									<canvas id="myChartC${item.code}" width="250" height="150"></canvas>
								</div>
							</td>
							<td>
								<div style="width: 250px; height: 150px;">
									<canvas id="myChartD${item.code}" width="250" height="150"></canvas>
								</div>
							</td>
						</c:otherwise>
					</c:choose>
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
	<c:forEach items="${pagingList }" var="paging" varStatus="status">
<%-- 		<a href="?page=${paging.idx }" class='<c:if test="${paging.idx eq paging.page }">active</c:if>'>${paging.idx }</a> --%>
		<a href="javascript:void(0)" onclick="javascript:onPaging(${paging.idx });" class='<c:if test="${paging.idx eq paging.page }">active</c:if>'>${paging.idx }</a>
	</c:forEach>
	</c:when>
	<c:otherwise>
		<a>1</a>
	</c:otherwise>
</c:choose>
<br/>
<div id="button_wrap">
	<button type="button" onclick="javascript:btnFromToDaySise();">지정 페이지 일자별 시세 추출</button>
	<button type="button" onclick="javascript:onCurrPageDaySise();">현재 페이지 일자별 시세 추출</button>
	<button type="button" onclick="javascript:btnFromToVolume();">지정 페이지 매물대 추출</button>
	<button type="button" onclick="javascript:onExtractVolume();">현재 페이지 매물대 추출</button>
	<button type="button" onclick="javascript:btnCompareVolumeSise();">매물대와 시세 비교</button>
	<button type="button" onclick="javascript:onExcludeCode(1);" >제외</button>

<!-- 	<button type="button" onclick="javascript:onExtract();">재추출</button> -->
</div>
<div id="selBtn_wrp">
	<a href="javascript:void(0)" onclick="javascript:onMovePage('home');">Home</a>
	<a href="javascript:void(0)" onclick="javascript:onMovePage('financialPage1');">재무제표 정리</a>
	<a href="javascript:void(0)" onclick="javascript:onMovePage('log');">Log 화면</a>
<!-- 	<a href="/thirdStep.do" >로직 3단계</a> -->
</div>

<jsp:include page="/WEB-INF/views/common/common.jsp" flush="true"/>
</body>
</html>
