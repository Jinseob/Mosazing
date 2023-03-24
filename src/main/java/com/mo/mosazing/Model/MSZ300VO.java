package com.mo.mosazing.Model;

import java.math.BigDecimal;

public class MSZ300VO{
	private String code;
	private String date;
	private BigDecimal endvalue;
	private BigDecimal startvalue;
	private BigDecimal highvalue;
	private BigDecimal lowvalue;
	private BigDecimal volume;
	private BigDecimal fluctuation;
	private BigDecimal f;
	private BigDecimal t;
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
	public BigDecimal getEndvalue() {
		return endvalue;
	}
	public void setEndvalue(BigDecimal endvalue) {
		this.endvalue = endvalue;
	}
	public BigDecimal getStartvalue() {
		return startvalue;
	}
	public void setStartvalue(BigDecimal startvalue) {
		this.startvalue = startvalue;
	}
	public BigDecimal getHighvalue() {
		return highvalue;
	}
	public void setHighvalue(BigDecimal highvalue) {
		this.highvalue = highvalue;
	}
	public BigDecimal getLowvalue() {
		return lowvalue;
	}
	public void setLowvalue(BigDecimal lowvalue) {
		this.lowvalue = lowvalue;
	}
	public BigDecimal getVolume() {
		return volume;
	}
	public void setVolume(BigDecimal volume) {
		this.volume = volume;
	}
	public BigDecimal getFluctuation() {
		return fluctuation;
	}
	public void setFluctuation(BigDecimal fluctuation) {
		this.fluctuation = fluctuation;
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
}
