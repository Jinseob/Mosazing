package com.mo.mosazing;

public class Const {
	// yyyymm 용
	public final static Integer rowCnt		= 3;
	
	// Paging 용.
	public final static Integer length 		= 30;	// Page 당 건수.
//	public final static Integer totalpage 	= 10;	// 목록 화면에 선택할 수 있는 Page 갯 수.
	public final static Integer unitpage 	= 10;	// 목록 화면에 선택할 수 있는 Page 갯 수.
	
	// 한국거래소 데이터 URL http://data.krx.co.kr/comm/bldAttendant/getJsonData.cmd?mktId=ALL&bld=dbms/MDC/STAT/standard/MDCSTAT01501&share=1&money=1&csvxls_isNo=false&trdDd=20210122
	// 네이버 실시간 시세 https://polling.finance.naver.com/api/realtime?query=SERVICE_RECENT_ITEM:코드,코드
	
	// DART URL : 단일 회사 주요 계정
	public final static String url = "https://opendart.fss.or.kr/api/fnlttSinglAcnt.json?crtfc_key=";
	
	// DART URL : 단일 회사 전쳬 계정
	public final static String urlAll = "https://opendart.fss.or.kr/api/fnlttSinglAcntAll.json?crtfc_key=";
	
	// WICS URL : WICS 산업분류. http://www.wiseindex.com/Index/GetIndexComponets?ceil_yn=0&dt=20201102&sec_cd=G2560
	public final static String urlWICSSector = "http://www.wiseindex.com/About/WICS";
	public final static String urlWICS = "http://www.wiseindex.com/Index/GetIndexComponets?ceil_yn=0&dt=";
	
	// 네이버
	public final static String stockUrl = "https://finance.naver.com/item/main.nhn?code=";
	
	// 네이버 실시간 시세
//	public final static String realTimeSise = "https://polling.finance.naver.com/api/realtime.nhn?query=SERVICE_RECENT_ITEM:"; // 2020.11.24 변경됨.
	public final static String realTimeSise = "https://polling.finance.naver.com/api/realtime?query=SERVICE_RECENT_ITEM:";
	
	// crtfc_key : DART
	public final static String crtfc_key = "2aa0ca33aacdea40d0e894b1f0db51769c5be020";
	
	// 네이버 일자별 시세 URL
	public final static String siseUrl = "https://finance.naver.com/item/sise_day.nhn?code=";
	public final static Integer _3year		= 78;	// 3년은 156주. 한 페이지에 2주씩. 총 78페이지 읽어야함.
	
	// 보고서 코드
	public final static String reprt1_code = "11013";	// 1분기
	public final static String reprt2_code = "11012";	// 반기
	public final static String reprt3_code = "11014";	// 3분기
	public final static String reprt4_code = "11011";	// 사업
	
	// 개별/연결구분
	public final static String cfs = "CFS";	// 연결재무제표
	public final static String ofs = "OFS";	// 재무제표
	
	// Status
	public final static String success = "000";	// 연결 성공.
	
	// 데이터 저장 상태
	public final static String plusAuto = "PA";		// 상, 자동 입력.
	public final static String minusAuto = "MA";	// 하, 자동 입력.
	public final static String plusDirect = "PD";	// 상, 수동 입력.
	public final static String minusDirect = "MD";	// 하, 수동 입력.
	
	public final static String yesAuto = "YA";		// 예, 자동 입력.
	public final static String noAuto = "NA";		// 아니오, 자동 입력.
	public final static String yesDirect = "YD";	// 예, 수동 입력.
	public final static String noDirect = "ND";		// 아니오, 수동 입력.
	
	// 파일저장시
	public final static String krw = "원(KRW)";
	public final static Integer kospiCnt = 900;		// 900 보다 작으면 코스피
	public final static Integer kosdaqCnt = 1300;	// 1300 보다 크면 코스닥
//	public final static String filepath = "D:\\Project_Portfolio\\workspace\\Mosazing\\log";
//	public final static String sisefilepath = "D:\\Project_Portfolio\\workspace\\Mosazing\\sise";
}
