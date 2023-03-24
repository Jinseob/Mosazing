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
<!-- 	<script data-ad-client="ca-pub-8762790342501585" async src="https://pagead2.googlesyndication.com/pagead/js/adsbygoogle.js"></script> -->
	<link rel="stylesheet" href="/resources/css/jquery-ui.min.css" />
	<link rel="stylesheet" href="/resources/css/style.css" />
	<title>Mosazing</title>
	<script type="text/javascript">
	$(document).ready(function(){

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
	<div class="sign_wrap">
		<p class="sign_top"><a href="/">뭐 사징?</a></p>
		<ul class="sign_form">
			<li class="sign_input">
				<input type="text" class="in_form1" id="email" name="email" value="" placeholder="e-mail"/>
				<input type="text" class="in_form1" id="passwd" name="passwd" value="" placeholder="비밀 번호"/>
				<input type="text" class="in_form1" id="passwdchk" name="passwdchk" value="" placeholder="비밀 번호 확인"/>
			</li>
			<li class="sign_btn">
				<button type="button" class="in_btn1" onclick="javascript:onsignupBtn()">회원 가입</button>
			</li>
			<li>
				<div class="horizontal_line"></div>
			</li>
			<li class="social_btn">
				<button type="button" class="in_btn1 kko" onclick="javascript:onsignupBtn()">카카오 계정으로 회원가입</button>
				<button type="button" class="in_btn1 ggl" onclick="javascript:onsignupBtn()">구글 계정으로 회원 가입</button>
				<button type="button" class="in_btn1 nav" onclick="javascript:onsignupBtn()">네이버 계정으로 회원 가입</button>
				<button type="button" class="in_btn1 fab" onclick="javascript:onsignupBtn()">페이스북 게정으로 회원 가입</button>
			</li>
			<li>
				<div class="horizontal_line"></div>
			</li>
			<li class="sign_btn">
				<button type="button" class="in_Btn1" onclick="javascript:history.back()">뒤로가기</button>
			</li>
		</ul>
	</div>
<!-- <footer>footer</footer> -->
<jsp:include page="/WEB-INF/views/common/common.jsp" flush="true"/>
</body>
</html>
