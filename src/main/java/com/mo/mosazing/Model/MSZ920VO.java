package com.mo.mosazing.Model;

public class MSZ920VO{
	private String cdtype; 		
	private String parentcd; 		
	private String cd;
	private String cdnm;
	private int cdlevel;
	public String getCdtype() {
		return cdtype == null ? "" : cdtype;
	}
	public void setCdtype(String cdtype) {
		this.cdtype = cdtype;
	}
	public String getParentcd() {
		return parentcd == null ? "" : parentcd;
	}
	public void setParentcd(String parentcd) {
		this.parentcd = parentcd;
	}
	public String getCd() {
		return cd == null ? "" : cd;
	}
	public void setCd(String cd) {
		this.cd = cd;
	}
	public String getCdnm() {
		return cdnm == null ? "" : cdnm;
	}
	public void setCdnm(String cdnm) {
		this.cdnm = cdnm;
	}
	public int getCdlevel() {
		return cdlevel;
	}
	public void setCdlevel(int cdlevel) {
		this.cdlevel = cdlevel;
	}
}
