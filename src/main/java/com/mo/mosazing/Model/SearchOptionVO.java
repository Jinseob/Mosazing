package com.mo.mosazing.Model;

import java.util.List;

import com.mo.mosazing.Const;

public class SearchOptionVO extends PagingVO{
	// 검색용
	private String val;		// 검색값
	
	// Type
	private String section;		// 3년, 2년, 1년 등 섹션별로 확인.
	private String type;		// 타입
	private String wics;		// wics 코드
	private String division;	// division 코드
	private String account;		// account 코드
	
	// 라인수
	private int line;	// 원하는 라인 수 만큼 출력
	
	private String bsns_year;	// 회계연도.
	private String code;		// 회사코드.
	private String volumeType;
	private String exclude;		// 상태값.
	private String favorite;	// 즐겨찾기
	
	private Integer rowCnt;			// yyyymm 에 사용할 조건.
	
	private List<String> list;
	private List<String> codeList;
	
	// 정렬용
	private String sort;		// 정렬용 SD : SA DESC, SA : SA ASC, BD : Business DESC, BA : Business ASC, CD : CI DESC, CA : CI ASC

	// Common 용 CD Type
	private String cdtype;	// 타입
	private int cdlevel; 	// CD Level
	
	// 매물대 정렬용
	private String checkA;
	private String checkB;
	private String checkC;
	private String checkD;
	
	public String getVal() {
		return val == null ? "" : val;
	}

	public void setVal(String val) {
		this.val = val;
	}

	public String getSection() {
		return section == null ? "" : section;
	}

	public void setSection(String section) {
		this.section = section;
	}

	public int getLine() {
		return line;
	}

	public void setLine(int line) {
		this.line = line;
	}

	public String getBsns_year() {
		return bsns_year == null ? "" : bsns_year;
	}

	public void setBsns_year(String bsns_year) {
		this.bsns_year = bsns_year;
	}

	public String getSort() {
		return sort == null ? "" : sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public List<String> getList() {
		return list;
	}

	public void setList(List<String> list) {
		this.list = list;
	}

	public String getCode() {
		return code == null ? "" : code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getVolumeType() {
		return volumeType == null ? "" : volumeType;
	}

	public void setVolumeType(String volumeType) {
		this.volumeType = volumeType;
	}

	public String getType() {
		return type == null ? "" : type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getWics() {
		return wics == null ? "" : wics;
	}

	public void setWics(String wics) {
		this.wics = wics;
	}

	public String getDivision() {
		return division == null ? "" : division;
	}

	public void setDivision(String division) {
		this.division = division;
	}

	public String getAccount() {
		return account == null ? "" : account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public List<String> getCodeList() {
		return codeList;
	}

	public void setCodeList(List<String> codeList) {
		this.codeList = codeList;
	}

	public String getCdtype() {
		return cdtype == null ? "" : cdtype;
	}

	public void setCdtype(String cdtype) {
		this.cdtype = cdtype;
	}

	public int getCdlevel() {
		return cdlevel;
	}

	public void setCdlevel(int cdlevel) {
		this.cdlevel = cdlevel;
	}

	public String getCheckA() {
		return checkA == null ? "" : checkA;
	}

	public void setCheckA(String checkA) {
		this.checkA = checkA;
	}

	public String getCheckB() {
		return checkB == null ? "" : checkB;
	}

	public void setCheckB(String checkB) {
		this.checkB = checkB;
	}

	public String getCheckC() {
		return checkC == null ? "" : checkC;
	}

	public void setCheckC(String checkC) {
		this.checkC = checkC;
	}

	public String getCheckD() {
		return checkD == null ? "" : checkD;
	}

	public void setCheckD(String checkD) {
		this.checkD = checkD;
	}


	public String getExclude() {
		return exclude;
	}

	public void setExclude(String exclude) {
		this.exclude = exclude;
	}

	public String getFavorite() {
		return favorite == null ? "" : favorite;
	}

	public void setFavorite(String favorite) {
		this.favorite = favorite;
	}

	public int getRowCnt() {
		return rowCnt == null ? Const.rowCnt : rowCnt;
	}

	public void setRowCnt(int rowCnt) {
		this.rowCnt = rowCnt;
	}

	public boolean isEmpty() {
		boolean chk = true;
		
//		// 전체 증시 체크
		if(!this.getDivision().isEmpty()) return false;
//		System.out.println(this.getDivision() + " : " + chk);
		
		// 종류 체크
		if(!this.getAccount().isEmpty()) return false;
//		System.out.println(this.getAccount() + " : " + chk);
		
		// 정렬 기준 체크
		if(!this.getSort().isEmpty()) return false;
//		System.out.println(this.getSort() + " : " + chk);
		
		// 기타 조건 체크
		if(!this.getSection().isEmpty()) return false;
//		System.out.println(this.getSection() + " : " + chk);
		
		// 매물대 기준 체크
		if(!this.getCheckA().isEmpty()) return false;
		if(!this.getCheckB().isEmpty()) return false;
		if(!this.getCheckC().isEmpty()) return false;
		if(!this.getCheckD().isEmpty()) return false;
		
		// 업종 구분 체크
		if(!this.getWics().isEmpty()) return false;
//		System.out.println(this.getWics() + " : " + chk);
		
		// 종목 코드 체크
		if(!this.getCode().isEmpty()) return false;
//		System.out.println(this.getCode() + " : " + chk);
		
		return chk;
	}
}
