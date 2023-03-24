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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import com.mo.mosazing.Dao.CmmnDao;
import com.mo.mosazing.Model.MSZ020VO;
import com.mo.mosazing.Model.MSZ030VO;
import com.mo.mosazing.Model.PagingVO;
import com.mo.mosazing.Model.SearchOptionVO;

/**
 * Handles requests for the application home page.
 */
@Controller
public class Step3Controller {
	
	private static final Logger logger = LoggerFactory.getLogger(Step3Controller.class);
	
	@Autowired
	private CmmnDao dao;
	
	@RequestMapping(value = "/extract2.json", method=RequestMethod.POST)
	public ModelAndView extract2(@ModelAttribute("searchOptionVO") SearchOptionVO searchOptionVO, ModelMap model, HttpServletRequest request, 
			HttpServletResponse response, HttpSession session, BindingResult bindingResult) throws Exception{
			
		ModelAndView modelAndView = new ModelAndView();
		MappingJackson2JsonView jsonView = new MappingJackson2JsonView();
		modelAndView.setView(jsonView);
	
		// 수익 중인 종목 추출.
		@SuppressWarnings("unchecked")
		ArrayList<String> plusCodeList = (ArrayList<String>) dao.selectList("common.selectListPlusCode", searchOptionVO);
		
		// 수익 중인 종목 중 사채를 사용하는 종목 추출.
		@SuppressWarnings("unchecked")
		List<MSZ030VO> bondList = (List<MSZ030VO>) dao.selectList("common.selectBond", plusCodeList);

		List<MSZ030VO> list = new ArrayList<MSZ030VO>();
		MSZ030VO result = null;
		for(int i = 0; i < bondList.size(); i++) {
			result = new MSZ030VO();
			result = bondList.get(i);
			result.setType(Const.yesAuto);
			list.add(result);
			
			if(list.size() > 0 && list.size() % 1000 == 0 && i != 0) {
				dao.insert("common.insertExtract2", list);
				list = new ArrayList<MSZ030VO>();
			}
			if(i == bondList.size() - 1 && list.size() > 0) {
				dao.insert("common.insertExtract2", list);
			}
		}
		System.out.println(list.size());
		
		return modelAndView;
	}
	
	@RequestMapping(value = "/thirdStep.do", method = RequestMethod.GET)
	public String thirdStep(@ModelAttribute("searchOptionVO") SearchOptionVO searchOptionVO, ModelMap model, 
			HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception{
				
		int resultCnt = dao.selectCnt("common.selectListNoBondCount", searchOptionVO);
		model.addAttribute("resultCnt", resultCnt);		// 총 건수 표시.
		int totalPage = (int) Math.ceil((double)resultCnt / Const.length );
//		model.addAttribute("totalPage", totalPage);
		
		// Paging 용 정보를 다 만들어서 배열로 넣어준다면? page, currpage, href, 총 페이지는 배열의 수
//		int unitPage = ( searchOptionVO.getPage() - 1 ) / Const.totalpage;
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
		
		// 사채가 없는 종목 추출.
		@SuppressWarnings("unchecked")
		ArrayList<String> noBondList = (ArrayList<String>) dao.selectList("common.selectListNoBond", searchOptionVO);
		
		// 사채가 없는 종목 정보 추출
		@SuppressWarnings("unchecked")
		List<MSZ020VO> resultList = (List<MSZ020VO>) dao.selectList("common.selectListNoBondInfo", noBondList);
		model.addAttribute("resultList", resultList);
		
		return "step2";
	}
}
