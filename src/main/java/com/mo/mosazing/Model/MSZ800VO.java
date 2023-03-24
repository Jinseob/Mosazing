package com.mo.mosazing.Model;

import java.math.BigDecimal;

public class MSZ800VO{
	private String wics;
	private String yyyymm;
	private BigDecimal sasum;
	private BigDecimal bpsum;
	private BigDecimal cisum;
	private BigDecimal samax;
	private BigDecimal samin;
	private BigDecimal bpmax;
	private BigDecimal bpmin;
	private BigDecimal cimax;
	private BigDecimal cimin;
	private BigDecimal marketcap;
	
	private String account;
	private BigDecimal sum;
	private BigDecimal max;
	private BigDecimal min;
	
	public String getWics() {
		return wics == null ? "" : wics;
	}
	public void setWics(String wics) {
		this.wics = wics;
	}
	public String getYyyymm() {
		return yyyymm == null ? "" : yyyymm;
	}
	public void setYyyymm(String yyyymm) {
		this.yyyymm = yyyymm;
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
	public BigDecimal getSamax() {
		return samax;
	}
	public void setSamax(BigDecimal samax) {
		this.samax = samax;
	}
	public BigDecimal getSamin() {
		return samin;
	}
	public void setSamin(BigDecimal samin) {
		this.samin = samin;
	}
	public BigDecimal getBpmax() {
		return bpmax;
	}
	public void setBpmax(BigDecimal bpmax) {
		this.bpmax = bpmax;
	}
	public BigDecimal getBpmin() {
		return bpmin;
	}
	public void setBpmin(BigDecimal bpmin) {
		this.bpmin = bpmin;
	}
	public BigDecimal getCimax() {
		return cimax;
	}
	public void setCimax(BigDecimal cimax) {
		this.cimax = cimax;
	}
	public BigDecimal getCimin() {
		return cimin;
	}
	public void setCimin(BigDecimal cimin) {
		this.cimin = cimin;
	}
	public BigDecimal getSum() {
		return sum;
	}
	public void setSum(BigDecimal sum) {
		this.sum = sum;
	}
	public BigDecimal getMax() {
		return max;
	}
	public void setMax(BigDecimal max) {
		this.max = max;
	}
	public BigDecimal getMin() {
		return min;
	}
	public void setMin(BigDecimal min) {
		this.min = min;
	}
	public String getAccount() {
		return account == null ? "" : account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public BigDecimal getMarketcap() {
		return marketcap;
	}
	public void setMarketcap(BigDecimal marketcap) {
		this.marketcap = marketcap;
	}
}
