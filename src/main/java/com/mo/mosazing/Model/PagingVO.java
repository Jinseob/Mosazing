package com.mo.mosazing.Model;

import java.util.List;

import com.mo.mosazing.Const;

public class PagingVO {
	private Integer length;			// 출력 길이.
	private Integer offset;			// offset.
	private Integer pageoffset;		// 한 화면에 총 건수 기준 offset.
	private Integer nextpage;			// 다음 Page.
	private Integer prevpage;			// 이전 Page.
	private Integer currpage;			// 현재 Page.
	private Integer totalpage;			// 전체 Page.
	private Integer fpage;			// From Page
	private Integer tpage;			// To Page.
	private Integer idx;			// Index.
	private Integer unitpage;			// Index.
	private List<Integer> pagelist;	// 페이지 List
	
	public Integer getLength() {
		return length == null ? Const.length : length;
	}
	public void setLength(Integer length) {
		this.length = length;
	}
	public void setOffset(Integer offset) {
		this.offset = offset;
	}
	public int getOffset() {
		return offset == null ? 0 : offset;
	}
	public Integer getNextpage() {
		return nextpage == null ? 0 : nextpage;
	}
	public void setNextpage(Integer nextpage) {
		this.nextpage = nextpage;
	}
	public Integer getPrevpage() {
		return prevpage == null ? 0 : prevpage;
	}
	public void setPrevpage(Integer prevpage) {
		this.prevpage = prevpage;
	}
	public Integer getCurrpage() {
		return currpage == null ? 1 : currpage;
	}
	public void setCurrpage(Integer currpage) {
		this.currpage = currpage;
	}
	public Integer getTotalpage() {
		return totalpage == null ? 0 : totalpage;
	}
	public void setTotalpage(Integer totalpage) {
		this.totalpage = totalpage;
	}
	public Integer getPageoffset() {
		return pageoffset == null ? 0 : pageoffset;
	}
	public void setPageoffset(Integer pageoffset) {
		this.pageoffset = pageoffset;
	}
	public Integer getIdx() {
		return idx == null ? 1 : idx;
	}
	public void setIdx(Integer idx) {
		this.idx = idx;
	}
	public Integer getUnitpage() {
		return unitpage == null ? Const.unitpage : unitpage;
	}
	public void setUnitpage(Integer unitpage) {
		this.unitpage = unitpage;
	}
	public Integer getFpage() {
		return fpage;
	}
	public void setFpage(Integer fpage) {
		this.fpage = fpage;
	}
	public Integer getTpage() {
		return tpage;
	}
	public void setTpage(Integer tpage) {
		this.tpage = tpage;
	}
	public List<Integer> getPagelist() {
		return pagelist;
	}
	public void setPagelist(List<Integer> pagelist) {
		this.pagelist = pagelist;
	}
}
