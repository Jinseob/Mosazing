<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<script type="text/javascript">
	$(document).ready(function(){
		// From To Dialog
		$("#fromToDialog").dialog({
			autoOpen: false,
	      	resizable: false,
	      	height: "auto",
	      	width: 400,
	      	modal: true,
	      	position: { my: "center", at: "top", of: window },
	    });
		
		// File Upload Dialog
		$("#uploadDialog").dialog({
			autoOpen: false,
	      	resizable: false,
	      	height: "auto",
	      	width: 400,
	      	modal: true,
	      	position: { my: "center", at: "top", of: window },
	    });
		
		// Confirm Dialog
		$("#confirmDialogY").dialog({
			autoOpen: false,
	      	resizable: false,
	      	height: "auto",
	      	width: 400,
	      	modal: true,
	      	position: { my: "center", at: "top", of: window },
	    });
		
		$("#loading-dialog").dialog({
			autoOpen: false,
			modal: true,
			position: { my: "center", at: "top", of: window },
		});
		
		$("#progress-dialog").dialog({
			autoOpen: false,
			modal: true,
			position: { my: "center", at: "top", of: window },
		});
		
		// 즐겨찾기
		$("input[name=codeFav]").click(function(){
			onFavoriteCode(this);
		});
		
		var progressbar = $( "#progressbar" ),
		progressLabel = $(".progress-label");
		
		progressbar.progressbar({
			value: false,
			change: function() {
				progressLabel.text( "Current Progress: " + progressbar.progressbar( "value" ) + "% " + "(" + progressbar.data("idx") + "/" + progressbar.data("total") + ")");
			},
			complete: function() {
				var dialog = $("#progress-dialog");
				progressLabel.text( "Complete!" );
				dialog.dialog( "option", "buttons", [{
					text: "Close",
					click: function(){
						dialog.dialog("close");
						location.reload(true);
					}
		        }]);
				$(".ui-dialog button").last().trigger( "focus" );
			}
		});
		
		// 기준 년월.
		var today = new Date();
		var year = today.getFullYear();
		var month = today.getMonth();
		if(month < 4) year = year - 1;
		for(var i = 0; i < 3; i++){
			year = year - 1;
			if(i == 0){
				$("[data-id='syear']").append("<option value='" + year + "' selected='selected'>" + year + "</option>");	
			}else{
				$("[data-id='syear']").append("<option value='" + year + "'>" + year + "</option>");
			}
		}
		
	});
	
	$(document).ajaxStart(function(){
		$("#loading-dialog").dialog("open");
	});
	$(document).ajaxStop(function(){
		$("#loading-dialog").dialog("close");
	});
// 	$(document).ajaxError(function(){
// 		$("#loading-dialog").dialog("close");
// 	});
// 	$(document).ajaxComplete(function(){
// 		$("#loading-dialog").dialog("close");
// 	});

	function progress(value, idx, total) {
		var progressbar = $( "#progressbar" );
		var val = value > 0 ? value : 0;
 
		progressbar.data("idx", idx);
		progressbar.data("total", total);
		progressbar.progressbar( "value", val);
 
// 		if ( val < 99 ) {
// 			setTimeout( progress, 80 );
// 		}
    }
</script>

<!-- From To Dialog -->
<div id="fromToDialog">
<!-- 	<label>년도 : </label> -->
<!-- 	<select name="syear" data-id="syear"></select> -->
	<label><span>From : </span></label>
	<input type="text" id="fPage" class="ftinput" value="${searchOptionVO.page }" /> ~ 
	<label><span>To : </span></label>
	<input type="text" id="tPage" class="ftinput" value="${fn:length(pagingList) }" />
</div>

<!-- File Upload Dialog -->
<div id="uploadDialog">
	<label><span>파일 업로드</span></label>
<!-- 	<input type="radio" id="kospi" name="division" value="KOSPI" /><label for="kospi">KOSPI</label> -->
<!-- 	<input type="radio" id="kosdaq" name="division" value="KOSDAQ" /><label for="kosdaq">KOSDAQ</label> -->
	<input type="file" id="fileupload" />
</div>

<!-- Confirm Dialog -->
<div id="confirmDialogY">
<!-- 	<label>적용 년도 : </label> -->
<!-- 	<select name='syear' id="syear" data-id='syear'></select> -->
</div>

<!-- Progress -->
<div id="progress-dialog" title="Notice">
	<div id="progressbar"><div class="progress-label">Processing...</div></div>
</div>

<!-- Loading -->
<div id="loading-dialog" title="Notice">
	<p>
    <span class="ui-icon ui-icon-circle-check" style="float:left; margin:0 7px 30px 0;">
    </span>
    Loading...
  	</p>
</div>
