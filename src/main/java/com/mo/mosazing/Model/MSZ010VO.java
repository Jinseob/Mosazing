package com.mo.mosazing.Model;

import java.sql.Timestamp;

public class MSZ010VO extends MSZ999VO {
	private String seqid;
	private String code;
	private String dartcd;
	private String compnm;
	private String type;
	private String typenm;
	private String share;
	private String capital;
	private String parvalue;
	private String currency;
	private String tel;
	private String address;
	private String status;
	private String division;
	private String wics;
	private String wicsnm;
	private String vstatus;	// 매물대, 전일가 비교. 전일가가 낮은 경우 매물대 타입 표기.
	private Timestamp sise_dt;	// 시세 갱신일
	private Timestamp volume_dt;	// 매물대 갱신일
	private Timestamp compare_dt;	// 비교 갱신일
	private String siseChk;
	private String volumeChk;
	private String compareChk;
	public String getSeqid() {
		return seqid == null ? "" : seqid;
	}
	public void setSeqid(String seqid) {
		this.seqid = seqid;
	}
	public String getCode() {
		return code == null ? "" : code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getDartcd() {
		return dartcd == null ? "" : dartcd;
	}
	public void setDartcd(String dartcd) {
		this.dartcd = dartcd;
	}
	public String getCompnm() {
		return compnm == null ? "" : compnm;
	}
	public void setCompnm(String compnm) {
		this.compnm = compnm;
	}
	public String getType() {
		return type == null ? "" : type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getTypenm() {
		return typenm == null ? "" : typenm;
	}
	public void setTypenm(String typenm) {
		this.typenm = typenm;
	}
	public String getShare() {
		return share == null ? "" : share;
	}
	public void setShare(String share) {
		this.share = share;
	}
	public String getCapital() {
		return capital == null ? "" : capital;
	}
	public void setCapital(String capital) {
		this.capital = capital;
	}
	public String getParvalue() {
		return parvalue == null ? "" : parvalue;
	}
	public void setParvalue(String parvalue) {
		this.parvalue = parvalue;
	}
	public String getCurrency() {
		return currency == null ? "" : currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public String getTel() {
		return tel == null ? "" : tel;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}
	public String getAddress() {
		return address == null ? "" : address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getStatus() {
		return status == null ? "" : status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getDivision() {
		return division == null ? "" : division;
	}
	public void setDivision(String division) {
		this.division = division;
	}
	public String getWics() {
		return wics == null ? "" : wics;
	}
	public void setWics(String wics) {
		this.wics = wics;
	}
	public String getWicsnm() {
		return wicsnm == null ? "" : wicsnm;
	}
	public void setWicsnm(String wicsnm) {
		this.wicsnm = wicsnm;
	}
	public String getVstatus() {
		return vstatus == null ? "" : vstatus;
	}
	public void setVstatus(String vstatus) {
		this.vstatus = vstatus;
	}
	public Timestamp getSise_dt() {
		return sise_dt;
	}
	public void setSise_dt(Timestamp sise_dt) {
		this.sise_dt = sise_dt;
	}
	public Timestamp getVolume_dt() {
		return volume_dt;
	}
	public void setVolume_dt(Timestamp volume_dt) {
		this.volume_dt = volume_dt;
	}
	public Timestamp getCompare_dt() {
		return compare_dt;
	}
	public void setCompare_dt(Timestamp compare_dt) {
		this.compare_dt = compare_dt;
	}
	public String getSiseChk() {
		return siseChk;
	}
	public void setSiseChk(String siseChk) {
		this.siseChk = siseChk;
	}
	public String getVolumeChk() {
		return volumeChk;
	}
	public void setVolumeChk(String volumeChk) {
		this.volumeChk = volumeChk;
	}
	public String getCompareChk() {
		return compareChk;
	}
	public void setCompareChk(String compareChk) {
		this.compareChk = compareChk;
	}
}
