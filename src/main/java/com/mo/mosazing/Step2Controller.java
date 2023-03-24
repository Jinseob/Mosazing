package com.mo.mosazing;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.mo.mosazing.Dao.CmmnDao;
import com.mo.mosazing.Model.MSZ120VO;
import com.mo.mosazing.Model.MSZ920VO;
import com.mo.mosazing.Model.PagingVO;
import com.mo.mosazing.Model.SearchOptionVO;
import com.mo.mosazing.Model.YYYYMMVO;

/**
 * Handles requests for the application home page.
 */
@Controller
public class Step2Controller {
	
	private static final Logger logger = LoggerFactory.getLogger(Step2Controller.class);
	
	@Autowired
	private CmmnDao dao;
	
	// 매물대 추출.
	@RequestMapping(value = "/secondStep.do", method = RequestMethod.GET)
	public String secondStep(@ModelAttribute("searchOptionVO") SearchOptionVO searchOptionVO, ModelMap model, 
			HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception{
				
		int resultCnt = dao.selectCnt("common.selectListPlusCount", searchOptionVO);
		model.addAttribute("resultCnt", resultCnt);		// 총 건수 표시.
		
		if(resultCnt > 0) {
			int totalPage = (int) Math.ceil((double)resultCnt / Const.length ); // 10 종목씩.
//			int totalPage = (int) Math.ceil((double)resultCnt / Const.length );
//			model.addAttribute("totalPage", totalPage);
			
			// Paging 용 정보를 다 만들어서 배열로 넣어준다면? page, currpage, href, 총 페이지는 배열의 수
//			int unitPage = ( searchOptionVO.getPage() - 1 ) / Const.totalpage;
			PagingVO pagingVO = null;
			List<PagingVO> pagingList = new ArrayList<PagingVO>();
			for(int i = 1; i <= totalPage; i++) {
				pagingVO = new PagingVO();
//			pagingVO.setIdx((unitPage * Const.totalpage) + i);
				pagingVO.setIdx(i);
				pagingVO.setCurrpage(searchOptionVO.getCurrpage());
				pagingList.add(pagingVO);
			}
			model.addAttribute("pagingList", pagingList);
			
			int offset = ( searchOptionVO.getCurrpage() - 1 ) * searchOptionVO.getLength();
			searchOptionVO.setOffset(offset);
			
			// 수익 중인 종목 추출.
			ArrayList<String> codeList = new ArrayList<String>();
			if(searchOptionVO.getAccount().isEmpty()) {
				// 코드 순
				codeList = (ArrayList<String>) dao.selectList("common.selectListPlusCode", searchOptionVO);
			}else {
				// 정렬 조건 순
				codeList = (ArrayList<String>) dao.selectList("common.selectListPlusCodeOrderBy", searchOptionVO);
			}
			model.addAttribute("codeList", codeList);
			
			if(codeList.size() > 0) {
				searchOptionVO.setRowCnt(3);
				searchOptionVO.setList(codeList);
				@SuppressWarnings("unchecked")
				List<YYYYMMVO> yyyymmList = (List<YYYYMMVO>) dao.selectList("common.selectYYYYMM", searchOptionVO);
				
				YYYYMMVO item = null;
				YYYYMMVO param = null;
				List<YYYYMMVO> paramList = new ArrayList<YYYYMMVO>();
				for(int i = 0; i < codeList.size(); i++) {
					int cnt = 0;
//					int chkCnt = 0;
//					System.out.print(codeList.get(i) + " : ");
					param = new YYYYMMVO();
					param.setCode(codeList.get(i));
					for(int j = 0; j < yyyymmList.size(); j++) {
						item = new YYYYMMVO();
						item = yyyymmList.get(j);
						if(codeList.get(i).equals(item.getCode())) {
							cnt++;
							switch (item.getRnum()) {
							case 1:
								param.setY1(item.getYyyymm());
								break;
							case 2:
								param.setY2(item.getYyyymm());
								break;
							case 3:
								param.setY3(item.getYyyymm());
								break;
							}
						}else {
							if(cnt > 0) cnt++;
						}
						if(cnt == 3) break;
//						chkCnt++;
					}
//					System.out.println(chkCnt);
					paramList.add(param);
				}
				
				List<MSZ120VO> resultList = new ArrayList<MSZ120VO>();
				MSZ120VO yyyyHeader = null;
				for(int i = 0; i < paramList.size(); i++) {
					param = paramList.get(i);
					@SuppressWarnings("unchecked")
					List<MSZ120VO> tempList = (List<MSZ120VO>) dao.selectList("common.selectItemList", paramList.get(i));
					
					if(tempList.get(0).getCnt() > 0) {
						// Header 추가.
						yyyyHeader = new MSZ120VO();
						yyyyHeader.setCode(tempList.get(0).getCode());
						yyyyHeader.setCnt(tempList.get(0).getCnt());
						yyyyHeader.setOrd("0");
						yyyyHeader.setDivision(tempList.get(0).getDivision());
						yyyyHeader.setCompnm(tempList.get(0).getCompnm());
						yyyyHeader.setShare(tempList.get(0).getShare());
						yyyyHeader.setAccount_nm("연도");
						yyyyHeader.setFavorite(tempList.get(0).getFavorite());
						yyyyHeader.setExpectedval(tempList.get(0).getExpectedval());
						
						yyyyHeader.setAccount("YY");
						yyyyHeader.setYy1(param.getY1());
						yyyyHeader.setYy2(param.getY2());
						yyyyHeader.setYy3(param.getY3());
						resultList.add(yyyyHeader);
					}
					
					// 이후 데이터 추가.
					for(int j = 0; j < tempList.size(); j++) {
						resultList.add(tempList.get(j));
					}
//					System.out.println(paramList.get(i).getCode() + " : " + paramList.get(i).getY1() + " : " + paramList.get(i).getY2() + " : " + paramList.get(i).getY3());
				}
				
				model.addAttribute("resultList", resultList);
			}
			
			// 업종 코드.
			searchOptionVO.setCdtype("WICS");
			searchOptionVO.setCdlevel(2);
			@SuppressWarnings("unchecked")
			List<MSZ920VO> commonCodeList = (List<MSZ920VO>) dao.selectList("common.selectCommonCodeList", searchOptionVO);
			model.addAttribute("commonCodeList", commonCodeList);
		}
		return "release/3plusVolumePage";
	}
}
