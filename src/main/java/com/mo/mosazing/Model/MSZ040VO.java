package com.mo.mosazing.Model;

public class MSZ040VO{
	private String code; 		//int(6) DEFAULT NULL COMMENT '종목코드',
	private String bsns_year; 	//char(4) DEFAULT NULL COMMENT '사업 연도',
	private int fn_cnt; 		//int(3) DEFAULT NULL COMMENT '재무제표 내역 건수',
	private String url; 		//varchar(1000) DEFAULT NULL COMMENT 'URL',
	private String status; 		//char(3) DEFAULT NULL COMMENT '상태',
	public String getCode() {
		return code == null ? "" : code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getBsns_year() {
		return bsns_year == null ? "" : bsns_year;
	}
	public void setBsns_year(String bsns_year) {
		this.bsns_year = bsns_year;
	}
	public int getFn_cnt() {
		return fn_cnt;
	}
	public void setFn_cnt(int fn_cnt) {
		this.fn_cnt = fn_cnt;
	}
	public String getUrl() {
		return url == null ? "" : url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getStatus() {
		return status == null ? "" : status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
}
