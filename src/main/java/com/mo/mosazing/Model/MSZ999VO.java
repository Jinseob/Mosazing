package com.mo.mosazing.Model;

public class MSZ999VO{
	private String code; 		
	private String step; 
	private String favorite;
	private String exclude;
	public String getCode() {
		return code == null ? "" : code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getStep() {
		return step == null ? "" : step;
	}
	public void setStep(String step) {
		this.step = step;
	}
	public String getFavorite() {
		return favorite;
	}
	public void setFavorite(String favorite) {
		this.favorite = favorite;
	}
	public String getExclude() {
		return exclude;
	}
	public void setExclude(String exclude) {
		this.exclude = exclude;
	}
}
