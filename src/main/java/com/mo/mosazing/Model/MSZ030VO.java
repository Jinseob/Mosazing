package com.mo.mosazing.Model;

import java.math.BigDecimal;

public class MSZ030VO extends MSZ010VO{
	private String report_id; 			//int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '재무제표 index',
	private String rcept_no; 			//int(14) unsigned zerofill DEFAULT NULL COMMENT '접수번호. 공시뷰어 연결',
	private String reprt_code; 			//varchar(5) DEFAULT NULL COMMENT '1분기:11013/반기:11012/3분기:11014/사업:11011',
	private String bsns_year; 			//char(4) DEFAULT NULL COMMENT '사업 연도',
	private String sj_div; 				//char(2) DEFAULT NULL COMMENT '재무제표구분',
	private String sj_nm; 				//varchar(10) DEFAULT NULL COMMENT '재무제표명',
	private String account_id; 			//varchar(20) DEFAULT NULL COMMENT '계정ID',
	private String account_nm; 			//varchar(20) DEFAULT NULL COMMENT '계정명',
	private String account_detail; 		//varchar(20) DEFAULT NULL COMMENT '계정상세',
	private String thstrm_nm; 			//varchar(20) DEFAULT NULL COMMENT '당기명',
	private BigDecimal thstrm_amount; 		//decimal(16,0) DEFAULT NULL COMMENT '당기금액',
	private BigDecimal thstrm_add_amount; 	//decimal(16,0) DEFAULT NULL COMMENT '당기누적금액',
	private String frmtrm_nm; 			//varchar(20) DEFAULT NULL COMMENT '전기명',
	private BigDecimal frmtrm_amount; 		//decimal(16,0) DEFAULT NULL COMMENT '전기금액',
	private String frmtrm_q_nm; 		//varchar(20) DEFAULT NULL COMMENT '전기명(분/반기)',
	private BigDecimal frmtrm_q_amount; 	//decimal(16,0) DEFAULT NULL COMMENT '전기금액(분/반기)	',
	private BigDecimal frmtrm_add_amount; 	//decimal(16,0) DEFAULT NULL COMMENT '전기누적금액',
	private String bfefrmtrm_nm; 		//varchar(20) DEFAULT NULL COMMENT '전전기명',
	private BigDecimal bfefrmtrm_amount; 	//decimal(16,0) DEFAULT NULL COMMENT '전전기금액',
	private String ord; 				//varchar(2) DEFAULT NULL COMMENT '계정과목 정렬순서',
	public String getReport_id() {
		return report_id == null ? "" : report_id;
	}
	public void setReport_id(String report_id) {
		this.report_id = report_id;
	}
	public String getRcept_no() {
		return rcept_no == null ? "" : rcept_no;
	}
	public void setRcept_no(String rcept_no) {
		this.rcept_no = rcept_no;
	}
	public String getReprt_code() {
		return reprt_code == null ? "" : reprt_code;
	}
	public void setReprt_code(String reprt_code) {
		this.reprt_code = reprt_code;
	}
	public String getBsns_year() {
		return bsns_year == null ? "" : bsns_year;
	}
	public void setBsns_year(String bsns_year) {
		this.bsns_year = bsns_year;
	}
	public String getSj_div() {
		return sj_div == null ? "" : sj_div;
	}
	public void setSj_div(String sj_div) {
		this.sj_div = sj_div;
	}
	public String getSj_nm() {
		return sj_nm == null ? "" : sj_nm;
	}
	public void setSj_nm(String sj_nm) {
		this.sj_nm = sj_nm;
	}
	public String getAccount_id() {
		return account_id == null ? "" : account_id;
	}
	public void setAccount_id(String account_id) {
		this.account_id = account_id;
	}
	public String getAccount_nm() {
		return account_nm == null ? "" : account_nm;
	}
	public void setAccount_nm(String account_nm) {
		this.account_nm = account_nm;
	}
	public String getAccount_detail() {
		return account_detail == null ? "" : account_detail;
	}
	public void setAccount_detail(String account_detail) {
		this.account_detail = account_detail;
	}
	public String getThstrm_nm() {
		return thstrm_nm == null ? "" : thstrm_nm;
	}
	public void setThstrm_nm(String thstrm_nm) {
		this.thstrm_nm = thstrm_nm;
	}
	public BigDecimal getThstrm_amount() {
		return thstrm_amount;
	}
	public void setThstrm_amount(BigDecimal thstrm_amount) {
		this.thstrm_amount = thstrm_amount;
	}
	public BigDecimal getThstrm_add_amount() {
		return thstrm_add_amount;
	}
	public void setThstrm_add_amount(BigDecimal thstrm_add_amount) {
		this.thstrm_add_amount = thstrm_add_amount;
	}
	public String getFrmtrm_nm() {
		return frmtrm_nm == null ? "" : frmtrm_nm;
	}
	public void setFrmtrm_nm(String frmtrm_nm) {
		this.frmtrm_nm = frmtrm_nm;
	}
	public BigDecimal getFrmtrm_amount() {
		return frmtrm_amount;
	}
	public void setFrmtrm_amount(BigDecimal frmtrm_amount) {
		this.frmtrm_amount = frmtrm_amount;
	}
	public String getFrmtrm_q_nm() {
		return frmtrm_q_nm == null ? "" : frmtrm_q_nm;
	}
	public void setFrmtrm_q_nm(String frmtrm_q_nm) {
		this.frmtrm_q_nm = frmtrm_q_nm;
	}
	public BigDecimal getFrmtrm_q_amount() {
		return frmtrm_q_amount;
	}
	public void setFrmtrm_q_amount(BigDecimal frmtrm_q_amount) {
		this.frmtrm_q_amount = frmtrm_q_amount;
	}
	public BigDecimal getFrmtrm_add_amount() {
		return frmtrm_add_amount;
	}
	public void setFrmtrm_add_amount(BigDecimal frmtrm_add_amount) {
		this.frmtrm_add_amount = frmtrm_add_amount;
	}
	public String getBfefrmtrm_nm() {
		return bfefrmtrm_nm == null ? "" : bfefrmtrm_nm;
	}
	public void setBfefrmtrm_nm(String bfefrmtrm_nm) {
		this.bfefrmtrm_nm = bfefrmtrm_nm;
	}
	public BigDecimal getBfefrmtrm_amount() {
		return bfefrmtrm_amount;
	}
	public void setBfefrmtrm_amount(BigDecimal bfefrmtrm_amount) {
		this.bfefrmtrm_amount = bfefrmtrm_amount;
	}
	public String getOrd() {
		return ord == null ? "" : ord;
	}
	public void setOrd(String ord) {
		this.ord = ord;
	}
}
