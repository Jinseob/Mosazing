package com.mo.mosazing.Model;

import java.math.BigDecimal;

public class CompareVO implements Comparable<CompareVO>{
	private String id;
	private BigDecimal value;
	public String getId() {
		return id == null ? "" : id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public BigDecimal getValue() {
		return value;
	}
	public void setValue(BigDecimal value) {
		this.value = value;
	}
	
	public CompareVO(BigDecimal value, String id) {
		this.value = value;
		this.id = id;
	}
	
	@Override
	public int compareTo(CompareVO o) {
		// TODO Auto-generated method stub
		return o.getValue().compareTo(this.value);
	}
}
