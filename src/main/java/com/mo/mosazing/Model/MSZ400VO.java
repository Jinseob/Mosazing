package com.mo.mosazing.Model;

import java.math.BigDecimal;

public class MSZ400VO{
	private int idx;
	private String code;
	private String date;
	private String type;
	private BigDecimal volume;
	private BigDecimal f;
	private BigDecimal t;
	
	private BigDecimal marketcap;
	public int getIdx() {
		return idx;
	}
	public void setIdx(int idx) {
		this.idx = idx;
	}
	public String getCode() {
		return code == null ? "" : code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getDate() {
		return date == null ? "" : date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getType() {
		return type == null ? "" : type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public BigDecimal getVolume() {
		return volume;
	}
	public void setVolume(BigDecimal volume) {
		this.volume = volume;
	}
	public BigDecimal getF() {
		return f;
	}
	public void setF(BigDecimal f) {
		this.f = f;
	}
	public BigDecimal getT() {
		return t;
	}
	public void setT(BigDecimal t) {
		this.t = t;
	}
	public BigDecimal getMarketcap() {
		return marketcap;
	}
	public void setMarketcap(BigDecimal marketcap) {
		this.marketcap = marketcap;
	}
}
