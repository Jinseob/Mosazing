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
<!-- 	<script data-ad-client="ca-pub-8762790342501585" async src="https://pagead2.googlesyndication.com/pagead/js/adsbygoogle.js"></script> -->
	<link rel="stylesheet" href="/resources/css/jquery-ui.min.css" />
	<link rel="stylesheet" href="/resources/css/style.css" />
	<title>Mosazing</title>
	<script type="text/javascript">
	$(document).ready(function(){
		if($("#codeList").val().replace(/[\[\]]/g, "") != ""){
			getVolume();
		}
		
		$("[data-hcd]").on("click", function(e){ajaxData(e.currentTarget.dataset, "chkPortalUrl", "N");});
	});
	
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
							drawChart('myChart' + nm + item.code, vData);	// 차트 및 최대, 최소 처리.
							vData = new Array();
						}
					}
				}
				onRealTimeSise();
 			},
 			error: function(data){
 				alert("에러 발생. " + data);
 			}
 		})
	}
	</script>
</head>
<body>
<header>
	<div id="header_wrap">
		<div id="header_top">
			<h1>뭐 사징?</h1>
		</div>
		<div id="searchOption">
			<form id="frm" name="frm">
				<input type="hidden" name="currpage" id="currpage"/>
				<div class="row">
					<select name="division">
						<option value="" <c:if test="${searchOptionVO.division eq '' }">selected</c:if>>전체 증시</option>
						<option value="KOSPI" <c:if test="${searchOptionVO.division eq 'KOSPI' }">selected</c:if>>KOSPI</option>
						<option value="KOSDAQ" <c:if test="${searchOptionVO.division eq 'KOSDAQ' }">selected</c:if>>KOSDAQ</option>
					</select>
					<select name="account">
						<option disabled selected>정렬 종류</option>
						<option value="" <c:if test="${searchOptionVO.account eq '' }">selected</c:if>>전체</option>
						<option value="SA" <c:if test="${searchOptionVO.account eq 'SA' }">selected</c:if>>매출액</option>
						<option value="BP" <c:if test="${searchOptionVO.account eq 'BP' }">selected</c:if>>영업익</option>
						<option value="CI" <c:if test="${searchOptionVO.account eq 'CI' }">selected</c:if>>순이익</option>
						<option value="VO" <c:if test="${searchOptionVO.account eq 'VO' }">selected</c:if>>거래량</option>
						<option value="X" <c:if test="${searchOptionVO.account eq 'X' }">selected</c:if>>없음</option>
					</select>
					<select name="sort">
						<option value="" <c:if test="${searchOptionVO.sort eq '' }">selected</c:if>>정렬순</option>
						<option value="D" <c:if test="${searchOptionVO.sort eq 'D' }">selected</c:if>>높은순</option>
						<option value="A" <c:if test="${searchOptionVO.sort eq 'A' }">selected</c:if>>낮은순</option>
					</select>
					<select name="section">
						<option value="" <c:if test="${searchOptionVO.section eq '' }">selected</c:if>>기타 조건</option>
						<option value="3Y" <c:if test="${searchOptionVO.section eq '3Y' }">selected</c:if>>3년통합</option>
					</select>
					<label>기간별 적정가</label>
					<div id="sortVolumeOption">
						<input type="checkbox" id="checkA" name="checkA" <c:if test="${searchOptionVO.checkA eq 'on' }">checked</c:if>><label for="checkA" class="checkboxLabel">6개월</label>
						<input type="checkbox" id="checkB" name="checkB" <c:if test="${searchOptionVO.checkB eq 'on' }">checked</c:if>><label for="checkB" class="checkboxLabel">1년</label>
						<input type="checkbox" id="checkC" name="checkC" <c:if test="${searchOptionVO.checkC eq 'on' }">checked</c:if>><label for="checkC" class="checkboxLabel">2년</label>
						<input type="checkbox" id="checkD" name="checkD" <c:if test="${searchOptionVO.checkD eq 'on' }">checked</c:if>><label for="checkD" class="checkboxLabel">3년</label>
					</div>
				</div>
				<div class="row">
					<select id="wics" name="wics">
						<option value="">업종 전체</option>
						<c:forEach items="${commonCodeList }" var="list">
							<option value="${list.cd }" <c:if test="${list.cd eq searchOptionVO.wics }">selected</c:if>>${list.cdnm }</option>
						</c:forEach>
					</select>
					<input type="text" id="code" name="code" value="${searchOptionVO.code }" placeholder="종목 코드"/>
					<button type="button" class="searchBtn" onclick="javascrit:onSearchBtn()">검색</button>
				</div>
			</form>
		</div>
	</div>
</header>
<input type="hidden" name="codeList" id="codeList" value="${codeList}"/>
<article>
	<div id="paging_wrap">
		<c:if test="${pagingInfo.prevpage ne 0 }"><a href="javascript:void(0)" onclick="javascript:onPaging(${pagingInfo.prevpage });">&lt;이전</a></c:if>
		<c:choose>
			<c:when test="${fn:length(pagingInfo.pagelist) > 0 }">
			<c:forEach items="${pagingInfo.pagelist }" var="paging">
				<a href="javascript:void(0)" onclick="javascript:onPaging(${paging });" class='<c:if test="${paging eq pagingInfo.currpage }">active</c:if>'>${paging }</a>
			</c:forEach>
			</c:when>
			<c:otherwise>
				<a>1</a>
			</c:otherwise>
		</c:choose>
		<c:if test="${pagingInfo.nextpage ne 0 }"><a href="javascript:void(0)" onclick="javascript:onPaging(${pagingInfo.nextpage });">다음&gt;</a></c:if>
	</div>
	<div id="contents_wrap">
		<div id="data_tables">
			<c:choose>
				<c:when test="${fn:length(resultList) > 0 }">
					<c:forEach items="${resultList }" var="item" varStatus="status">
					<c:if test="${item.ord eq 0}">
					<div data-id="${item.code }" class="table_row">
						<div class="info_a">
							<div class="row">
								<div class="left">
									<div class="sptxt1"><span><c:out value="${item.code }" /></span> <span><c:out value="${item.division }" /></span></div>
									<div class="sptxt2"><span><c:out value="${item.compnm }" /></span></div>
								</div>
							</div>
							<div class="row">
								<div><span data-index="real" class="sptxt4">현재가</span> <span class="sptxt5">(전일가 </span><span data-index="sv" class="sptxt5">-</span><span class="sptxt5">) </span></div>
							</div>
							<div class="row"><div class="sptxt6"><span data-index="cv">-</span>|<span data-index="cr">-</span></div></div>
							<div class="row">
								<span class="sptxt10"><a href="https://finance.naver.com/item/main.nhn?code=${item.code }" target="_blank" class="naver_detail_link" data-hcd="${item.code }" data-portal="naver">네이버 상세</a></span>
								<span class="sptxt10"><a href="https://finance.daum.net/quotes/A${item.code }#home" target="_blank" class="daum_detail_link" data-hcd="${item.code }" data-portal="daum">다음 상세</a></span>
							</div>
						</div>
						<div class="info_b">
							<div class="row"><div class="sptxt8">고가 <span data-index="high">-</span> (상한가 <span data-index="ul">-</span>)</div></div>
							<div class="row"><div class="sptxt8">저가 <span data-index="low">-</span> (하한가 <span data-index="ll">-</span>)</div></div>
							<div class="horizontal_line"></div>
							<div class="row">
								<div class="left"><span class="sptxt9">주식수</span></div>
								<div class="right"><span data-index="sh" class="sptxt9"><fmt:formatNumber value="${item.share }" pattern="#,###" /></span></div>
							</div>
							<div class="row">
								<div class="left"><span class="sptxt9">시가총액</span></div>
								<div class="right"><span data-index="mp" class="sptxt9">-</span><span class="sptxt9"> 억원</span></div>
							</div>
							<div class="row">
								<div class="left"><span class="sptxt9">거래량</span></div>
								<div class="right"><span data-index="vol" class="sptxt9">-</span></div>
							</div>
						</div>
						<div class="info_financial">
						<table>
							<c:choose>
								<c:when test="${fn:length(item.list) > 0 }">
									<c:forEach items="${item.list }" var="subitem">
									<tr>
									<td class="account_nm" style="font-weight: bold;">
										<c:out value="${subitem.account_nm }" /><c:if test="${subitem.ord eq 1 or subitem.ord eq 2 or subitem.ord eq 3}">(억)</c:if>
									</td>
									<c:choose>
										<c:when test="${subitem.ord eq 0}">
											<td align="center" style="font-weight: bold;"><c:out value="${subitem.yy1 }" /></td>
											<td align="center" style="font-weight: bold;"><c:out value="${subitem.yy2 }" /></td>
											<td align="center" style="font-weight: bold;"><c:out value="${subitem.yy3 }" /></td>
										</c:when>
										<c:when test="${subitem.ord eq 4}">
											<td align="right" class='<c:if test="${subitem.y1 < 0}">red</c:if> <c:if test="${subitem.y1 > 9 and subitem.y1 < 11}">bestroe</c:if> <c:if test="${subitem.y1 >= 11}">goodroe</c:if>'><fmt:formatNumber value="${subitem.y1 }" pattern="#,###.##" /></td>
											<td align="right" class='<c:if test="${subitem.y2 < 0}">red</c:if> <c:if test="${subitem.y2 > 9 and subitem.y2 < 11}">bestroe</c:if> <c:if test="${subitem.y2 >= 11}">goodroe</c:if>'><fmt:formatNumber value="${subitem.y2 }" pattern="#,###.##" /></td>
											<td align="right" class='<c:if test="${subitem.y3 < 0}">red</c:if> <c:if test="${subitem.y3 > 9 and subitem.y3 < 11}">bestroe</c:if> <c:if test="${subitem.y3 >= 11}">goodroe</c:if>'><fmt:formatNumber value="${subitem.y3 }" pattern="#,###.##" /></td>
										</c:when>
										<c:when test="${subitem.ord eq 5 or subitem.ord eq 6}">
											<td align="right" ><fmt:formatNumber value="${subitem.y1 }" pattern="#,###.##" /></td>
											<td align="right" ><fmt:formatNumber value="${subitem.y2 }" pattern="#,###.##" /></td>
											<td align="right" ><fmt:formatNumber value="${subitem.y3 }" pattern="#,###.##" /></td>
										</c:when>
										<c:otherwise>
											<td align="right" class='<c:if test="${subitem.y1 < 0}">red</c:if>'><fmt:formatNumber value="${subitem.y1 }" pattern="#,###" /></td>
											<td align="right" class='<c:if test="${subitem.y2 < 0}">red</c:if>'><fmt:formatNumber value="${subitem.y2 }" pattern="#,###" /></td>
											<td align="right" class='<c:if test="${subitem.y3 < 0}">red</c:if>'><fmt:formatNumber value="${subitem.y3 }" pattern="#,###" /></td>	
										</c:otherwise>
									</c:choose>
									<c:choose>
										<c:when test="${subitem.account eq 'BP' }">
										<td align="right" data-id="expected-bp"><fmt:formatNumber value="${subitem.expectedbp }" pattern="#,###" /></td>
										<c:set var="expectedval" value="${subitem.expectedval}"/>
										</c:when>
										<c:when test="${subitem.account eq 'SA' }">
										<td align="right" data-id="expected-sa"><fmt:formatNumber value="${subitem.expectedsa }" pattern="#,###" /></td>
										</c:when>
										<c:when test="${subitem.account eq 'CI' }">
										<td align="right" data-id="expected-ci"><fmt:formatNumber value="${subitem.expectedci }" pattern="#,###" /></td>
										</c:when>
										<c:when test="${subitem.account eq 'ROE' }">
										<td align="right" data-id="expected-val"><fmt:formatNumber value="${expectedval }" pattern="#,###" /></td>
										</c:when>
										<c:when test="${subitem.account eq 'YY' }">
										<td align="center" style="font-weight: bold;">추천가(원)</td>
										</c:when>
										<c:otherwise>
										<td align="center">-</td>
										</c:otherwise>
									</c:choose>
									</tr>
									</c:forEach>							
								</c:when>
								<c:otherwise>
									<tr>
										<td>-</td>
									</tr>
								</c:otherwise>
							</c:choose>
						</table>
						</div>
						<div class="info_volume">
							<c:if test="${status.first eq true}">
								<div class="info_volume_header">
									<div class="row">
										<span>6개월 매물대</span>
										<span>1년 매물대</span>
										<span>2년 매물대</span>
										<span>3년 매물대</span>
									</div>
								</div>
							</c:if>
							<div class="row">
								<div style="width: 280px; height: 150px;">
									<canvas id="myChartA${item.code}" width="280" height="150"></canvas>
								</div>
								<div style="width: 280px; height: 150px;">
									<canvas id="myChartB${item.code}" width="280" height="150"></canvas>
								</div>
								<div style="width: 280px; height: 150px;">
									<canvas id="myChartC${item.code}" width="280" height="150"></canvas>
								</div>
								<div style="width: 280px; height: 150px;">
									<canvas id="myChartD${item.code}" width="280" height="150"></canvas>
								</div>
							</div>
						</div>
					</div>
					</c:if>
					</c:forEach>
				</c:when>
				<c:otherwise>
				데이터가 없습니다.
				</c:otherwise>
			</c:choose>
		</div>
	</div>
	<div id="paging_wrap">
		<c:if test="${pagingInfo.prevpage ne 0 }"><a href="javascript:void(0)" onclick="javascript:onPaging(${pagingInfo.prevpage });">&lt;이전</a></c:if>
		<c:choose>
			<c:when test="${fn:length(pagingInfo.pagelist) > 0 }">
			<c:forEach items="${pagingInfo.pagelist }" var="paging">
				<a href="javascript:void(0)" onclick="javascript:onPaging(${paging });" class='<c:if test="${paging eq pagingInfo.currpage }">active</c:if>'>${paging }</a>
			</c:forEach>
			</c:when>
			<c:otherwise>
				<a>1</a>
			</c:otherwise>
		</c:choose>
		<c:if test="${pagingInfo.nextpage ne 0 }"><a href="javascript:void(0)" onclick="javascript:onPaging(${pagingInfo.nextpage });">다음&gt;</a></c:if>
	</div>
	<div id="button_wrap">
	</div>
	<div id="selBtn_wrp">
	</div>
</article>
<!-- <footer>footer</footer> -->
<jsp:include page="/WEB-INF/views/common/common.jsp" flush="true"/>
</body>
</html>
