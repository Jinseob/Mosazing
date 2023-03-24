// Common Code 가져오기.
function getCommonCode(a){
	var exec = false;
	if(a.id == "wics"){
		if($("#" + a.id + " option").length <= 1)exec = true;
	}
	
	if(exec){
		$.ajax({
 			type: "POST",
//  			async: false,
 			url : "/getCode.json",
 			dataType: "json",
 			data: {type : a.id},
 			global:false,
 			success: function(results, status, xhr){
 				var codeList = results.commonCodeList;
 				
 				if(a.id == "wics"){	// wics 코드.
// 		 				$("#" + a.id).append("<option value=''>--</option>");
	 				$(codeList).each(function(i){
	 					$("#" + a.id).append("<option value='" + codeList[i].cd + "'>" + codeList[i].cdnm + "</option>");
	 				});
 				}
 			},
 			error: function(data){
 				alert("E" + data);
 			}
 		});
	}
}

//차트
function drawChart(id, data){
	var labels = new Array();
	var values = new Array();
	var dataLength = data.length > 9 ? 10 : data.length;
	var max = 0;
	var min = 0;
	var maxIdx = 0;
	var minIdx = 0;
	for(var i = 0; i < dataLength; i++){
		var item = data[i];
		var f = item.f == null ? 0 : item.f;
		var volume = item.volume == null ? 0 : item.volume;
		labels.push(f.toLocaleString() + "~");
		values.push(volume / 1000);
		
		if(volume > max){
			maxIdx = i;
			max = volume;
			$("#" + id).data("maxF", f);
		}
		if(i == 0)min = volume;
		if(volume < min){
			minIdx = i;
			min = volume;
		}
	}
	
	// 1 순위
	const cBgcR = 'rgba(255, 99, 132, 0.2)';
	const cBcR = 'rgba(255, 99, 132, 1)';
	
	// 10 순위
	const cBgcB = 'rgba(54, 162, 235, 0.2)';
	const cBcB = 'rgba(54, 162, 235, 1)';
	
	// 일반
	const cBgc = 'rgba(75, 192, 192, 0.2)';
	const cBc = 'rgba(75, 192, 192, 1)';
	
	var bgcList = new Array();
	var bcList = new Array();
	for(var i = 0; i < dataLength; i++){
		var bgc = cBgc;
		var bc = cBc;
		if(i == maxIdx){
			bgc = cBgcR
			bc = cBcR
		}else if(i == minIdx){
			bgc = cBgcB
			bc = cBcB
		}
		bgcList.push(bgc);
		bcList.push(bc);
	}
	
	var ctx = document.getElementById(id).getContext('2d');
	var myChart = new Chart(ctx, {
	    type: 'horizontalBar',
	    data: {
	        labels: labels,
	        datasets: [{
//		            label: '# of Votes',
	            data: values,
	            backgroundColor: bgcList,
	            borderColor: bcList,
	            borderWidth: 1
	        }]
	    },
	    options: {
	        scales: {
	            yAxes: [{
	                ticks: {
	                    beginAtZero: true
	                }
	            }]
	        },
	        legend:{
	        	display: false
	        }
	    }
	});
}

function onSiseTimer(){
	var now = new Date();
	var startTime = new Date();
	var endTime = new Date();
	
	startTime.setHours(9);
	startTime.setMinutes(0);
	startTime.setSeconds(0);
	
	endTime.setHours(15);
	endTime.setMinutes(30);
	endTime.setSeconds(0);
	
	if(now.getTime() >= startTime.getTime() && now.getTime() <= endTime.getTime()){
		// 9 시에서 16시 사이에는 setInterval 계속 실행.
//		console.log("장내 : " + now);
		setTimeout(onRealTimeSise, 300000); // 5분뒤 함수 호출
	}else{
		// 이외의 시간에는 9시까지 남은 시간을 setTimeout 으로 지정하여 다시 실행하도록
		let hh = now.getHours();
		let sDate = new Date();
		
		if(hh < 24){
			sDate.setDate(sDate.getDate() + 1);
		}
		sDate.setHours(9);
		sDate.setMinutes(0);
		sDate.setSeconds(0);
		
		let waitTime = sDate.getTime() - now.getTime();
//		console.log("장외 : " + now + " - wait Time :" + waitTime);
		setTimeout(onRealTimeSise, waitTime); // 5분뒤 함수 호출
	}
}

// 실시간 시세
function onRealTimeSise_bak(){
	var codeList = $("#codeList").val().replace(/[\[\]]/g, "");
	$.ajax({
		type: "POST",
		url : "/realTimeSise.json",
		dataType: "json",
		global:false,
		data: { codeList : codeList},
		success: function(results){
			var datas = JSON.parse(results.json);
			for(var i = 0; i < datas.length; i++){
				var item = datas[i];
				var sv = item.sv;
				
				$("tr[data-id='" + item.cd + "'] > td > span[data-index='low']").text(item.lv.toLocaleString());	// 저가 (전일보다 높으면 빨강, 낮으면 파랑)
				$("tr[data-id='" + item.cd + "'] > td > span[data-index='high']").text(item.hv.toLocaleString());	// 고가 (전일보다 높으면 빨강, 낮으면 파랑)
				$("tr[data-id='" + item.cd + "'] > td > span[data-index='real']").text(item.nv.toLocaleString());	// 현재가 (전일보다 높으면 빨강, 낮으면 파랑)
				
				$("tr[data-id='" + item.cd + "'] > td > span[data-index='ul']").text("(상: " + item.ul.toLocaleString() + ")");	// 상한가
				$("tr[data-id='" + item.cd + "'] > td > span[data-index='ll']").text("(하: " + item.ll.toLocaleString() + ")");	// 하한가
// 						$("tr[data-id='" + item.cd + "'] > td > span[data-index='ov']").text(item.ov.toLocaleString());	// 시가 (전일보다 높으면 빨강, 낮으면 파랑)
				$("tr[data-id='" + item.cd + "'] > td > span[data-index='sv']").text("전: " + item.sv.toLocaleString());	// 전일
// 						$("tr[data-id='" + item.cd + "'] > td > span[data-index='real']").text();	// 전일대비 퍼센트 (전일보다 현재가가 높으면 빨강 및 플러스, 낮으면 파랑 및 마이너스)
				
				var nv = item.nv;
				var sh = $("tr[data-id='" + item.cd + "'] > td > span[data-index='sh']").text().replace(/,/g, '');
				var mp = Math.round(Number(sh) * Number(nv) / 100000000);
				$("tr[data-id='" + item.cd + "'] > td > span[data-index='mp']").text(mp.toLocaleString());	// 시총
				
				// Style
				// 저가.
				var style = "noneb";
				if(item.lv > sv){
					style = "redb";
				}else if(item.lv < sv){
					style = "blueb";
				}
				$("tr[data-id='" + item.cd + "'] > td > span[data-index='low']").removeClass();
				$("tr[data-id='" + item.cd + "'] > td > span[data-index='low']").addClass(style);
				
				// 고가.
				style = "noneb";
				if(item.hv > sv){
					style = "redb";
				}else if(item.hv < sv){
					style = "blueb";
				}
				$("tr[data-id='" + item.cd + "'] > td > span[data-index='high']").removeClass();
				$("tr[data-id='" + item.cd + "'] > td > span[data-index='high']").addClass(style);
				
				// 현재가.
				style = "noneb";
				if(item.nv > sv){
					style = "redb";
				}else if(item.nv < sv){
					style = "blueb";
				}
				$("tr[data-id='" + item.cd + "'] > td > span[data-index='real']").removeClass();
				$("tr[data-id='" + item.cd + "'] > td > span[data-index='real']").addClass(style);
				
				// 상한가, 하한가, 전일가
				$("tr[data-id='" + item.cd + "'] > td > span[data-index='ul']").removeClass();
				$("tr[data-id='" + item.cd + "'] > td > span[data-index='ul']").addClass("ull");
				$("tr[data-id='" + item.cd + "'] > td > span[data-index='ll']").removeClass();
				$("tr[data-id='" + item.cd + "'] > td > span[data-index='ll']").addClass("ull");
				$("tr[data-id='" + item.cd + "'] > td > span[data-index='sv']").removeClass();
				$("tr[data-id='" + item.cd + "'] > td > span[data-index='sv']").addClass("ull");
				
				// 전일대비
				var arrow = "≡";	//▲▼≡
				var sign = "";		// +-
				style = "noneb";
				if(item.nv > sv){
					style = "redb";
					arrow = "▲";
					sign = "+";
				}else if(item.nv < sv){
					style = "blueb";
					arrow = "▼";
					sign = "-";
				}
				$("tr[data-id='" + item.cd + "'] > td > span[data-index='cv']").text(arrow + item.cv.toLocaleString());	// 전일대비 (전일보다 현재가가 높으면 빨강 및 윗방향화살표, 낮으면 파랑 및 아랫방향 화살표)
				$("tr[data-id='" + item.cd + "'] > td > span[data-index='cr']").text(sign + item.cr.toLocaleString() + "%");	// 전일대비 (전일보다 현재가가 높으면 빨강 및 윗방향화살표, 낮으면 파랑 및 아랫방향 화살표)
				$("tr[data-id='" + item.cd + "'] > td > span[data-index='cv']").removeClass();
				$("tr[data-id='" + item.cd + "'] > td > span[data-index='cv']").addClass(style);
				$("tr[data-id='" + item.cd + "'] > td > span[data-index='cr']").removeClass();
				$("tr[data-id='" + item.cd + "'] > td > span[data-index='cr']").addClass(style);
				
				// 거래량, 거래대금
				$("tr[data-id='" + item.cd + "'] > td > span[data-index='vol']").removeClass();
				$("tr[data-id='" + item.cd + "'] > td > span[data-index='vol']").addClass("ull");
				$("tr[data-id='" + item.cd + "'] > td > span[data-index='vol']").text("V : " + item.aq.toLocaleString());
				
				// 추천가와 현재가 비교
				var sav = 0, bpv = 0, civ = 0;	// 매출, 영업, 당기순이익.
				sav = $("tr[data-id='" + item.cd + "'] > td[data-id='expected-sa']").text().replace(/,/g, '');
				bpv = $("tr[data-id='" + item.cd + "'] > td[data-id='expected-bp']").text().replace(/,/g, '');
				civ = $("tr[data-id='" + item.cd + "'] > td[data-id='expected-ci']").text().replace(/,/g, '');
				val = $("tr[data-id='" + item.cd + "'] > td[data-id='expected-val']").text().replace(/,/g, '');
				
				$("tr[data-id='" + item.cd + "'] > td[data-id='expected-sa']").removeClass();
				$("tr[data-id='" + item.cd + "'] > td[data-id='expected-bp']").removeClass();
				$("tr[data-id='" + item.cd + "'] > td[data-id='expected-ci']").removeClass();
				$("tr[data-id='" + item.cd + "'] > td[data-id='expected-val']").removeClass();
				
				if(Number(sav) > item.nv)$("tr[data-id='" + item.cd + "'] > td[data-id='expected-sa']").addClass("goodprice");
				if(Number(bpv) > item.nv)$("tr[data-id='" + item.cd + "'] > td[data-id='expected-bp']").addClass("goodprice");
				if(Number(civ) > item.nv)$("tr[data-id='" + item.cd + "'] > td[data-id='expected-ci']").addClass("goodprice");
				if(Number(val) > item.nv)$("tr[data-id='" + item.cd + "'] > td[data-id='expected-val']").addClass("goodprice");
			}
			
			onSiseTimer();	// 타이머
		},
		error: function(data){
			alert("에러 발생. " + data);
		}
	})
}

// 2021.02.03 디자인 수정으로 인한 변경.
function onRealTimeSise(){
	var codeList = $("#codeList").val().replace(/[\[\]]/g, "");
	$.ajax({
		type: "POST",
		url : "/realTimeSise.json",
		dataType: "json",
		global:false,
		data: { codeList : codeList},
		success: function(results){
			var datas = JSON.parse(results.json);
			for(var i = 0; i < datas.length; i++){
				var item = datas[i];
				var sv = item.sv;	// 전일가
				var nv = item.nv;	// 현재가
				var cd = item.cd;	// 종목코드
				
				$("div[data-id='" + cd + "'] span[data-index='low']").text(item.lv.toLocaleString());	// 저가 (전일보다 높으면 빨강, 낮으면 파랑)
				$("div[data-id='" + cd + "'] span[data-index='high']").text(item.hv.toLocaleString());	// 고가 (전일보다 높으면 빨강, 낮으면 파랑)
				$("div[data-id='" + cd + "'] span[data-index='real']").text(item.nv.toLocaleString());	// 현재가 (전일보다 높으면 빨강, 낮으면 파랑)
				
				$("div[data-id='" + cd + "'] span[data-index='ul']").text(item.ul.toLocaleString());	// 상한가
				$("div[data-id='" + cd + "'] span[data-index='ll']").text(item.ll.toLocaleString());	// 하한가
// 						$("tr[data-id='" + cd + "'] > td > span[data-index='ov']").text(item.ov.toLocaleString());	// 시가 (전일보다 높으면 빨강, 낮으면 파랑)
				$("div[data-id='" + cd + "'] span[data-index='sv']").text(item.sv.toLocaleString());	// 전일
// 						$("tr[data-id='" + cd + "'] > td > span[data-index='real']").text();	// 전일대비 퍼센트 (전일보다 현재가가 높으면 빨강 및 플러스, 낮으면 파랑 및 마이너스)
				
				var sh = $("div[data-id='" + cd + "'] span[data-index='sh']").text().replace(/,/g, '');
				var mp = Math.round(Number(sh) * Number(nv) / 100000000);
				$("div[data-id='" + cd + "'] span[data-index='mp']").text(mp.toLocaleString());	// 시총
				
				// Style
				// 저가.
				var style = "noneb";
				if(item.lv > sv){
					style = "redb";
				}else if(item.lv < sv){
					style = "blueb";
				}
				$("div[data-id='" + cd + "'] span[data-index='low']").removeClass();
				$("div[data-id='" + cd + "'] span[data-index='low']").addClass(style);
				
				// 고가.
				style = "noneb";
				if(item.hv > sv){
					style = "redb";
				}else if(item.hv < sv){
					style = "blueb";
				}
				$("div[data-id='" + cd + "'] span[data-index='high']").removeClass();
				$("div[data-id='" + cd + "'] span[data-index='high']").addClass(style);
				
				// 현재가.
				style = "none";
				if(nv > sv){
					style = "red";
				}else if(nv < sv){
					style = "blue";
				}
				$("div[data-id='" + cd + "'] span[data-index='real']").removeClass();
				$("div[data-id='" + cd + "'] span[data-index='real']").addClass("sptxt4");
				$("div[data-id='" + cd + "'] span[data-index='real']").addClass(style);
				
//				var val = Number($("div[data-id='" + item.code + "'] span[data-index='real']").text().replace(/,/g, ''));
				var cA = $("#myChartA" + cd);
				var cB = $("#myChartB" + cd);
				var cC = $("#myChartC" + cd);
				var cD = $("#myChartD" + cd);
				if(nv < cA.data("maxF"))cA.parent().addClass("volumeSelected");
				if(nv < cB.data("maxF"))cB.parent().addClass("volumeSelected");
				if(nv < cC.data("maxF"))cC.parent().addClass("volumeSelected");
				if(nv < cD.data("maxF"))cD.parent().addClass("volumeSelected");
				
				// 전일대비
				var arrow = "≡";	//▲▼≡
				var sign = "";		// +-
				style = "none";
				if(item.nv > sv){
					style = "red";
					arrow = "▲";
					sign = "+";
				}else if(item.nv < sv){
					style = "blue";
					arrow = "▼";
					sign = "-";
				}
				$("div[data-id='" + cd + "'] span[data-index='cv']").text(arrow + item.cv.toLocaleString());	// 전일대비 (전일보다 현재가가 높으면 빨강 및 윗방향화살표, 낮으면 파랑 및 아랫방향 화살표)
				$("div[data-id='" + cd + "'] span[data-index='cr']").text(sign + item.cr.toLocaleString() + "%");	// 전일대비 (전일보다 현재가가 높으면 빨강 및 윗방향화살표, 낮으면 파랑 및 아랫방향 화살표)
				$("div[data-id='" + cd + "'] span[data-index='cv']").removeClass();
				$("div[data-id='" + cd + "'] span[data-index='cv']").addClass(style);
				$("div[data-id='" + cd + "'] span[data-index='cr']").removeClass();
				$("div[data-id='" + cd + "'] span[data-index='cr']").addClass(style);
				
				// 거래량, 거래대금
				$("div[data-id='" + cd + "'] span[data-index='vol']").text(item.aq.toLocaleString());
				
				// 추천가와 현재가 비교
				var sav = 0, bpv = 0, civ = 0;	// 매출, 영업, 당기순이익.
				sav = $("div[data-id='" + cd + "'] td[data-id='expected-sa']").text().replace(/,/g, '');
				bpv = $("div[data-id='" + cd + "'] td[data-id='expected-bp']").text().replace(/,/g, '');
				civ = $("div[data-id='" + cd + "'] td[data-id='expected-ci']").text().replace(/,/g, '');
				val = $("div[data-id='" + cd + "'] td[data-id='expected-val']").text().replace(/,/g, '');
				
				$("div[data-id='" + cd + "'] td[data-id='expected-sa']").removeClass();
				$("div[data-id='" + cd + "'] td[data-id='expected-bp']").removeClass();
				$("div[data-id='" + cd + "'] td[data-id='expected-ci']").removeClass();
				$("div[data-id='" + cd + "'] td[data-id='expected-val']").removeClass();
				
				if(Number(sav) > item.nv)$("div[data-id='" + cd + "'] td[data-id='expected-sa']").addClass("goodprice");
				if(Number(bpv) > item.nv)$("div[data-id='" + cd + "'] td[data-id='expected-bp']").addClass("goodprice");
				if(Number(civ) > item.nv)$("div[data-id='" + cd + "'] td[data-id='expected-ci']").addClass("goodprice");
				if(Number(val) > item.nv)$("div[data-id='" + cd + "'] td[data-id='expected-val']").addClass("goodprice");
			}
			
			onSiseTimer();	// 타이머
		},
		error: function(data){
			alert("에러 발생. " + data);
		}
	})
}

// 매물대 최대값
function getMaxVolume(){
	var codeList = $("#codeList").val().replace(/[\[\]]/g, "");
	$.ajax({
		type: "POST",
		url : "/getMaxVolume.json",
		dataType: "json",
		data: { codeList : codeList},
		global:false,
		success: function(results){
			var volumeMaxList = results.volumeMaxList;
			for(var nm in volumeMaxList){
				var list = volumeMaxList[nm];
				for(var i = 0; i < list.length; i++){
					var item = list[i];
					var val = Number($("div[data-id='" + item.code + "'] span[data-index='real']").text().replace(/,/g, ''));
					if(val < item.f && item.volume > 0){
						$("#myChart" + nm + item.code).parent().addClass("volumeSelected");
					}
				}
			}
		},
		error: function(data){
			alert("에러 발생. " + data);
		}
	});
}

//제외.
function onExcludeCode(chk){
	var codeList = $("input[name=codeChk]:checked").serialize();
	var data = codeList + "&exclude=" + chk;
	if(confirm("제외하시겠습니까?")){
		$.ajax({
 			type: "POST",
 			url : "/onExcludeCode.json",
 			dataType: "json",
 			data : data,
 			global:false,
 			success: function(results){
 				alert("제외 완료. 예외 종목에서만 확인할 수 있습니다.");
 			},
 			error: function(data){
 				alert("E" + data);
 			}
 		})
	}
}

// 즐겨찾기
function onFavoriteCode(a){
	var fav = a.className.indexOf("checked") > -1;
	if(fav){
		$(a).removeClass("checked");
	}else{
		$(a).addClass("checked");
	}
	var data = {code : a.dataset.value, favorite : fav == true ? 0 : 1};
	$.ajax({
			type: "POST",
			url : "/favoriteCode.json",
			dataType: "json",
			data : data,
			global:false,
			success: function(results){
				if(fav){
					alert("즐겨 찾기 해제!");
				}else{
					alert("즐겨 찾기 완료!");
				}
			},
			error: function(data){
				alert("E" + data);
			}
		})
}

// AjaxData
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

//AjaxData From To
function ajaxDataFromTo(idx, from, to, totalCnt, gubun){
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
					ajaxDataFromTo(idx, from, to, totalCnt, gubun);
				}
			},
			error: function(data){
				alert("에러 발생. " + data);
			}
		})
}