package com.mo.mosazing.Model;

public class CollectorVO {
	// 검색용
	private String hcd;			// link 선택 코드
	private String portal;		// 선택 포털
	public String getHcd() {
		return hcd == null ? "" : hcd;
	}
	public void setHcd(String hcd) {
		this.hcd = hcd;
	}
	public String getPortal() {
		return portal == null ? "" : portal;
	}
	public void setPortal(String portal) {
		this.portal = portal;
	}
}
