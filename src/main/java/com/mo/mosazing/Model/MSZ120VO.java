package com.mo.mosazing.Model;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

public class MSZ120VO extends MSZ010VO{
	private String bsns_year;
	private String yyyymm;
	private String account;
	private String account_nm;
	private BigDecimal amount;
	private BigDecimal y3;
	private BigDecimal y2;
	private BigDecimal y1;
	private BigDecimal y0;
	
	private String yy3;
	private String yy2;
	private String yy1;
	
	private String ord;
	
	private BigDecimal expectedsa;
	private BigDecimal expectedbp;
	private BigDecimal expectedci;
	private BigDecimal expectedval;
	
	private BigDecimal sasum;
	private BigDecimal bpsum;
	private BigDecimal cisum;
	private BigDecimal marketcap;
	private Date cap_dt;
	private String cdnm;
	
	private int cnt;
	
	private List<MSZ120VO> list;
	
	public String getBsns_year() {
		return bsns_year == null ? "" : bsns_year;
	}
	public void setBsns_year(String bsns_year) {
		this.bsns_year = bsns_year;
	}
	public String getYyyymm() {
		return yyyymm == null ? "" : yyyymm;
	}
	public void setYyyymm(String yyyymm) {
		this.yyyymm = yyyymm;
	}
	public String getAccount() {
		return account == null ? "" : account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getAccount_nm() {
		return account_nm == null ? "" : account_nm;
	}
	public void setAccount_nm(String account_nm) {
		this.account_nm = account_nm;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public String getOrd() {
		return ord == null ? "" : ord;
	}
	public void setOrd(String ord) {
		this.ord = ord;
	}
	public BigDecimal getY3() {
		return y3;
	}
	public void setY3(BigDecimal y3) {
		this.y3 = y3;
	}
	public BigDecimal getY2() {
		return y2;
	}
	public void setY2(BigDecimal y2) {
		this.y2 = y2;
	}
	public BigDecimal getY1() {
		return y1;
	}
	public void setY1(BigDecimal y1) {
		this.y1 = y1;
	}
	public BigDecimal getY0() {
		return y0;
	}
	public void setY0(BigDecimal y0) {
		this.y0 = y0;
	}
	public BigDecimal getExpectedsa() {
		return expectedsa;
	}
	public void setExpectedsa(BigDecimal expectedsa) {
		this.expectedsa = expectedsa;
	}
	public BigDecimal getExpectedbp() {
		return expectedbp;
	}
	public void setExpectedbp(BigDecimal expectedbp) {
		this.expectedbp = expectedbp;
	}
	public BigDecimal getExpectedci() {
		return expectedci;
	}
	public void setExpectedci(BigDecimal expectedci) {
		this.expectedci = expectedci;
	}
	public BigDecimal getExpectedval() {
		return expectedval;
	}
	public void setExpectedval(BigDecimal expectedval) {
		this.expectedval = expectedval;
	}
	public BigDecimal getSasum() {
		return sasum;
	}
	public void setSasum(BigDecimal sasum) {
		this.sasum = sasum;
	}
	public BigDecimal getBpsum() {
		return bpsum;
	}
	public void setBpsum(BigDecimal bpsum) {
		this.bpsum = bpsum;
	}
	public BigDecimal getCisum() {
		return cisum;
	}
	public void setCisum(BigDecimal cisum) {
		this.cisum = cisum;
	}
	public BigDecimal getMarketcap() {
		return marketcap;
	}
	public void setMarketcap(BigDecimal marketcap) {
		this.marketcap = marketcap;
	}
	public Date getCap_dt() {
		return cap_dt;
	}
	public void setCap_dt(Date cap_dt) {
		this.cap_dt = cap_dt;
	}
	public String getCdnm() {
		return cdnm == null ? "" : cdnm;
	}
	public void setCdnm(String cdnm) {
		this.cdnm = cdnm;
	}
	public int getCnt() {
		return cnt;
	}
	public void setCnt(int cnt) {
		this.cnt = cnt;
	}
	public String getYy3() {
		return yy3 == null ? "" : yy3;
	}
	public void setYy3(String yy3) {
		this.yy3 = yy3;
	}
	public String getYy2() {
		return yy2 == null ? "" : yy2;
	}
	public void setYy2(String yy2) {
		this.yy2 = yy2;
	}
	public String getYy1() {
		return yy1 == null ? "" : yy1;
	}
	public void setYy1(String yy1) {
		this.yy1 = yy1;
	}
	public List<MSZ120VO> getList() {
		return list;
	}
	public void setList(List<MSZ120VO> list) {
		this.list = list;
	}
}
