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
import com.mo.mosazing.Model.PagingVO;
import com.mo.mosazing.Model.SearchOptionVO;

/**
 * Handles requests for the application home page.
 */
@Controller
public class Step1Controller {
	
	private static final Logger logger = LoggerFactory.getLogger(Step1Controller.class);
	
	@Autowired
	private CmmnDao dao;
	
	@RequestMapping(value = "/firstStep.do", method = RequestMethod.GET)
	public String firstStep(@ModelAttribute("searchOptionVO") SearchOptionVO searchOptionVO, ModelMap model, 
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
			@SuppressWarnings("unchecked")
			ArrayList<String> plusCodeList = (ArrayList<String>) dao.selectList("common.selectListPlusCode", searchOptionVO);
			
			@SuppressWarnings("unchecked")
			List<MSZ120VO> resultList = (List<MSZ120VO>) dao.selectList("common.selectListPlus", plusCodeList);
			model.addAttribute("resultList", resultList);
		}
		
		return "3plus/step1";
	}
}
