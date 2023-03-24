package com.mo.mosazing.Model;

public class YYYYMMVO{
	private String code;
	private String yyyymm;
	private String y1;
	private String y2;
	private String y3;
	private int rnum;
	public String getCode() {
		return code == null ? "" : code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getYyyymm() {
		return yyyymm == null ? "" : yyyymm;
	}
	public void setYyyymm(String yyyymm) {
		this.yyyymm = yyyymm;
	}
	public int getRnum() {
		return rnum;
	}
	public void setRnum(int rnum) {
		this.rnum = rnum;
	}
	public String getY1() {
		return y1;
	}
	public void setY1(String y1) {
		this.y1 = y1;
	}
	public String getY2() {
		return y2;
	}
	public void setY2(String y2) {
		this.y2 = y2;
	}
	public String getY3() {
		return y3;
	}
	public void setY3(String y3) {
		this.y3 = y3;
	}
}
