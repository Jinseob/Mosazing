package com.mo.mosazing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
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
import com.mo.mosazing.Model.MSZ010VO;
import com.mo.mosazing.Model.MSZ020VO;
import com.mo.mosazing.Model.MSZ030VO;
import com.mo.mosazing.Model.MSZ040VO;
import com.mo.mosazing.Model.MSZ120VO;
import com.mo.mosazing.Model.MSZ300VO;
import com.mo.mosazing.Model.MSZ400VO;
import com.mo.mosazing.Model.MSZ999VO;
import com.mo.mosazing.Model.PagingVO;
import com.mo.mosazing.Model.SearchOptionVO;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {
	
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	
	@Autowired
	private CmmnDao dao;
	
	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(@ModelAttribute("searchOptionVO") SearchOptionVO searchOptionVO, ModelMap model, 
			HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception{
		
		int resultCnt = dao.selectCnt("common.selectCompanyCount", searchOptionVO);
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
			pagingVO.setPage(searchOptionVO.getPage());
			pagingList.add(pagingVO);
		}
		model.addAttribute("pagingList", pagingList);
		
		int offset = ( searchOptionVO.getPage() - 1 ) * searchOptionVO.getLength();
		searchOptionVO.setOffset(offset);
		
		@SuppressWarnings("unchecked")
		List<MSZ010VO> companyList = (List<MSZ010VO>) dao.selectList("common.selectCompanyList", searchOptionVO);
		model.addAttribute("companyList", companyList);
		
		return "home";
	}
	
	@RequestMapping(value = "/DataRegister.json", method=RequestMethod.POST)
	public ModelAndView NewsRegister(@ModelAttribute("searchOptionVO") SearchOptionVO searchOptionVO, ModelMap model, HttpServletRequest request, 
			HttpServletResponse response, HttpSession session, BindingResult bindingResult) throws Exception{
		
		ModelAndView modelAndView = new ModelAndView();
		MappingJackson2JsonView jsonView = new MappingJackson2JsonView();
		modelAndView.setView(jsonView);
		
		@SuppressWarnings("unchecked")
		List<MSZ010VO> companyList = (List<MSZ010VO>) dao.selectList("common.selectCompanyList", searchOptionVO);
		
		String status = "";
		if(companyList.size() > 0) {
			status = "S";
		}else {
			status = "N";
		}	
		model.addAttribute("status", status);
		
		MSZ010VO company = null;
		MSZ040VO fnLog = null;
		String bsns_year = "2019";
		int companyListSize = companyList.size();
//		int companyListSize = 1;
		for(int i = 0; i < companyListSize; i++) {
			company = companyList.get(i);
			
			/* 전체 계정 가져오는 부분. 주석
			// 단일회사 전체 계정
			String urlAll = Const.urlAll + Const.crtfc_key + "&corp_code=" + company.getDartcd() + "&bsns_year=" + bsns_year + "&reprt_code=" + Const.reprt4_code + "&fs_div=" + Const.cfs;
//			String urlAll = "https://opendart.fss.or.kr/api/fnlttSinglAcntAll.json?crtfc_key=2aa0ca33aacdea40d0e894b1f0db51769c5be020&corp_code=00357935&bsns_year=2019&reprt_code=11011&fs_div=CFS"; // 089470
//			String urlAll = "https://opendart.fss.or.kr/api/fnlttSinglAcntAll.json?crtfc_key=2aa0ca33aacdea40d0e894b1f0db51769c5be020&corp_code=00126487&bsns_year=2019&reprt_code=11011&fs_div=CFS"; // 007700
			JSONObject jsonObjectAll = readJsonFromUrl(urlAll);
			
			if(jsonObjectAll.get("status").toString().equals(Const.success)) {
				JSONArray jsonArray = jsonObjectAll.getJSONArray("list");
				// Log 용 데이터 생성.
				fnLog.setFn_cnt(jsonArray.length());
				// Log 용 데이터 생성.
				System.out.println(searchOptionVO.getOffset() + " : "+ company.getCode() + " : " + urlAll);
				
				MSZ030VO msz030vo = null;
				List<MSZ030VO> list = new ArrayList<MSZ030VO>();
				
				double thstrm_amount, frmtrm_amount, bfefrmtrm_amount;
				for(int j = 0; j < jsonArray.length(); j++) {
//					System.out.println(jsonArray.get(j).toString());
					JSONObject item = new JSONObject(jsonArray.get(j).toString());
					msz030vo = new MSZ030VO();
					msz030vo.setCode(company.getCode());
					msz030vo.setRcept_no(item.has("rcept_no") ? item.get("rcept_no").toString() : "");
					msz030vo.setReprt_code(item.has("reprt_code") ? item.get("reprt_code").toString() : "");
					msz030vo.setBsns_year(item.has("bsns_year") ? item.get("bsns_year").toString() : "");
					msz030vo.setSj_div(item.has("sj_div") ? item.get("sj_div").toString() : "");
					msz030vo.setSj_nm(item.has("sj_nm") ? item.get("sj_nm").toString() : "");
					msz030vo.setAccount_id(item.has("account_id") ? item.get("account_id").toString() : "");
					msz030vo.setAccount_nm(item.has("account_nm") ? item.get("account_nm").toString() : "");
					msz030vo.setAccount_detail(item.has("account_detail") ? item.get("account_detail").toString() : "");
					msz030vo.setThstrm_nm(item.has("thstrm_nm") ? item.get("thstrm_nm").toString() : "");
					thstrm_amount = item.has("thstrm_amount") ? (item.get("thstrm_amount").toString().equals("") ? 0 : Double.parseDouble(item.get("thstrm_amount").toString())) : 0;
					msz030vo.setThstrm_amount(thstrm_amount);
					msz030vo.setFrmtrm_nm(item.has("frmtrm_nm") ? item.get("frmtrm_nm").toString() : "");
					frmtrm_amount = item.has("frmtrm_amount") ? (item.get("frmtrm_amount").toString().equals("") ? 0 : Double.parseDouble(item.get("frmtrm_amount").toString())) : 0;
					msz030vo.setFrmtrm_amount(frmtrm_amount);
					msz030vo.setBfefrmtrm_nm(item.has("bfefrmtrm_nm") ? item.get("bfefrmtrm_nm").toString() : "");
					bfefrmtrm_amount = item.has("bfefrmtrm_amount") ? (item.get("bfefrmtrm_amount").toString().equals("") ? 0 : Double.parseDouble(item.get("bfefrmtrm_amount").toString())) : 0;
					msz030vo.setBfefrmtrm_amount(bfefrmtrm_amount);
					msz030vo.setOrd(item.has("ord") ? item.get("ord").toString() : "");
					
//					dao.insert("insertFnlttSinglAcntAll", msz030vo);
					list.add(msz030vo);
				}
				
				dao.insert("insertFnlttSinglAcntAll", list);
			}
			dao.insert("insertFnLog", fnLog);
			*/
			
			// 단일회사 요약 계정. 전체 계정으로 처리하기.
			String url = Const.url + Const.crtfc_key + "&corp_code=" + company.getDartcd() + "&bsns_year=" + bsns_year + "&reprt_code=" + Const.reprt4_code;
			JSONObject jsonObject = readJsonFromUrl(url);
			
			// Log 용 데이터 생성.
			fnLog = new MSZ040VO();
			fnLog.setCode(company.getCode());
			fnLog.setBsns_year(bsns_year);
			fnLog.setUrl(url);
			fnLog.setStatus(jsonObject.get("status").toString());
			// Log 용 데이터 생성.
			
			if(jsonObject.get("status").equals(Const.success)) {
				JSONArray jsonArray = jsonObject.getJSONArray("list");
				// Log 용 데이터 생성.
				fnLog.setFn_cnt(jsonArray.length());
				// Log 용 데이터 생성.
				System.out.println(searchOptionVO.getOffset() + " : "+ company.getCode() + " : " + url);
				MSZ020VO msz020vo = null;
				List<MSZ020VO> list = new ArrayList<MSZ020VO>();
				BigDecimal frmtrm_amount, thstrm_amount, bfefrmtrm_amount;
				for(int j = 0; j < jsonArray.length(); j++) {
//					System.out.println(jsonArray.get(j).toString());
					JSONObject item = new JSONObject(jsonArray.get(j).toString());
					msz020vo = new MSZ020VO();
					msz020vo.setCode(company.getCode());
					msz020vo.setRcept_no(item.has("rcept_no") ? item.get("rcept_no").toString() : "");
					msz020vo.setOrd(item.has("ord") ? item.get("ord").toString() : "");
					msz020vo.setReprt_code(item.has("reprt_code") ? item.get("reprt_code").toString() : "");
					msz020vo.setBsns_year(item.has("bsns_year") ? item.get("bsns_year").toString() : "");
					msz020vo.setSj_div(item.has("sj_div") ? item.get("sj_div").toString() : "");
					msz020vo.setSj_nm(item.has("sj_nm") ? item.get("sj_nm").toString() : "");
					msz020vo.setFs_div(item.has("fs_div") ? item.get("fs_div").toString() : "");
					msz020vo.setFs_nm(item.has("fs_nm") ? item.get("fs_nm").toString() : "");
					msz020vo.setAccount_nm(item.has("account_nm") ? item.get("account_nm").toString() : "");
					msz020vo.setFrmtrm_nm(item.has("frmtrm_nm") ? item.get("frmtrm_nm").toString() : "");
					msz020vo.setFrmtrm_dt(item.has("frmtrm_dt") ? item.get("frmtrm_dt").toString() : "");
					msz020vo.setThstrm_nm(item.has("thstrm_nm") ? item.get("thstrm_nm").toString() : "");
					msz020vo.setThstrm_dt(item.has("thstrm_dt") ? item.get("thstrm_dt").toString() : "");
					msz020vo.setBfefrmtrm_nm(item.has("bfefrmtrm_nm") ? item.get("bfefrmtrm_nm").toString() : "");
					msz020vo.setBfefrmtrm_dt(item.has("bfefrmtrm_dt") ? item.get("bfefrmtrm_dt").toString() : "");
					
					frmtrm_amount = (BigDecimal) (item.has("frmtrm_amount") ? (item.get("frmtrm_amount").toString().equals("-") ? 0 : (item.get("frmtrm_amount").toString().replaceAll(",", ""))) : 0);
					msz020vo.setFrmtrm_amount(frmtrm_amount);
					thstrm_amount = (BigDecimal) (item.has("thstrm_amount") ? (item.get("thstrm_amount").toString().equals("-") ? 0 : (item.get("thstrm_amount").toString().replaceAll(",", ""))) : 0);
					msz020vo.setThstrm_amount(thstrm_amount);
					bfefrmtrm_amount = (BigDecimal) (item.has("bfefrmtrm_amount") ? (item.get("bfefrmtrm_amount").toString().equals("-") ? 0 : (item.get("bfefrmtrm_amount").toString().replaceAll(",", ""))) : 0);
					msz020vo.setBfefrmtrm_amount(bfefrmtrm_amount);
					
//					dao.insert("insertFnlttSinglAcnt", msz020vo);
					list.add(msz020vo);
				}
//				
				dao.insert("common.insertFnlttSinglAcnt", list);
			}
			dao.insert("common.insertFnLog", fnLog);
		}
		
		return modelAndView;
	}
	
	private String jsonReadAll(Reader reader) throws IOException {

		StringBuilder sb = new StringBuilder();

		int cp;

		while ((cp = reader.read()) != -1) {
			sb.append((char) cp);
		}

		return sb.toString();

	}

	private JSONObject readJsonFromUrl(String url) throws IOException, JSONException {

		InputStream is = new URL(url).openStream();

		try {
			BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
			String jsonText = jsonReadAll(rd);
			JSONObject json = new JSONObject(jsonText);
			return json;
		} finally {
			is.close();
		}

	}
	
	@RequestMapping(value = "/firstStep.do", method = RequestMethod.GET)
	public String firstStep(@ModelAttribute("searchOptionVO") SearchOptionVO searchOptionVO, ModelMap model, 
			HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception{
		
		int resultCnt = dao.selectCnt("common.selectListPlusCount", searchOptionVO);
		model.addAttribute("resultCnt", resultCnt);		// 총 건수 표시.
		
		if(resultCnt > 0) {
			int totalPage = (int) Math.ceil((double)resultCnt / Const.length ); // 10 종목씩.
//		int totalPage = (int) Math.ceil((double)resultCnt / Const.length );
//		model.addAttribute("totalPage", totalPage);
			
			// Paging 용 정보를 다 만들어서 배열로 넣어준다면? page, currpage, href, 총 페이지는 배열의 수
//		int unitPage = ( searchOptionVO.getPage() - 1 ) / Const.totalpage;
			PagingVO pagingVO = null;
			List<PagingVO> pagingList = new ArrayList<PagingVO>();
			for(int i = 1; i <= totalPage; i++) {
				pagingVO = new PagingVO();
//			pagingVO.setIdx((unitPage * Const.totalpage) + i);
				pagingVO.setIdx(i);
				pagingVO.setPage(searchOptionVO.getPage());
				pagingList.add(pagingVO);
			}
			model.addAttribute("pagingList", pagingList);
			
			int offset = ( searchOptionVO.getPage() - 1 ) * searchOptionVO.getLength();
			searchOptionVO.setOffset(offset);
			
			// 수익 중인 종목 추출.
			@SuppressWarnings("unchecked")
			ArrayList<String> plusCodeList = (ArrayList<String>) dao.selectList("common.selectListPlusCode", searchOptionVO);
			
			@SuppressWarnings("unchecked")
			List<MSZ120VO> resultList = (List<MSZ120VO>) dao.selectList("common.selectListPlus", plusCodeList);
			model.addAttribute("resultList", resultList);
		}
		
		return "step1";
	}
	
	// 매출액, 영업익, 당기순이익 관련 상태값 저장 및 3년 모두 플러스인 종목 추출.
	@RequestMapping(value = "/extract1.json", method=RequestMethod.POST)
	public ModelAndView extract1(@ModelAttribute("searchOptionVO") SearchOptionVO searchOptionVO, ModelMap model, HttpServletRequest request, 
			HttpServletResponse response, HttpSession session, BindingResult bindingResult) throws Exception{
			
		ModelAndView modelAndView = new ModelAndView();
		MappingJackson2JsonView jsonView = new MappingJackson2JsonView();
		modelAndView.setView(jsonView);
	
		searchOptionVO.setBsns_year("2019");	// 현재는 2020 으로 고정.
		// 재추출하는 프로시저 실행.
		dao.update("common.updateFinancialStatus", searchOptionVO);
		
		return modelAndView;
	}
	
	/*
	// 매출액, 영업익, 당기순이익. 가져오는 부분. DB 에 있는 데이터중. 2020.10.28 수정
	@RequestMapping(value = "/extract1.json", method=RequestMethod.POST)
	public ModelAndView extract1(@ModelAttribute("searchOptionVO") SearchOptionVO searchOptionVO, ModelMap model, HttpServletRequest request, 
			HttpServletResponse response, HttpSession session, BindingResult bindingResult) throws Exception{
			
		ModelAndView modelAndView = new ModelAndView();
		MappingJackson2JsonView jsonView = new MappingJackson2JsonView();
		modelAndView.setView(jsonView);
		
		// 0. bsns_year 기준 한국거래소에서 가져온 데이터가 있는지 확인.
		int yearChk = dao.selectCnt("common.select020TBYear", searchOptionVO);
		
		// 0-1 있으면 삭제 후 가져오는 로직 타기.
		if(yearChk > 0) {
			// 0. DB 에 저장된 데이터중 100TB 에 저장한 데이터가 있는지 확인. 있으면 삭제 후 진행.
			int dataChk = dao.selectCnt("common.selectCountCFSYear", searchOptionVO);
			
			if(dataChk > 0) {
				dao.delete("common.deleteCFSYear", searchOptionVO);
			}
			
			// 1. 주요 계정에서 영업이익, 매출액, 당기순이익 정보 가져오기. CFS 기준
			@SuppressWarnings("unchecked")
			List<MSZ020VO> listCFS = (List<MSZ020VO>) dao.selectList("common.selectListCFS", searchOptionVO);
			
			// 2. Null 종목 추려서 OFS 기준으로 영업이익, 매출액, 당기순이익 정보 가져오기.
			List<String> listNull1 = new ArrayList<String>();
			List<MSZ020VO> list = new ArrayList<MSZ020VO>();
			MSZ020VO item1 = null;
			for(int i = 0; i < listCFS.size(); i++) {
				item1 = new MSZ020VO();
				item1 = listCFS.get(i);
				
				if(item1.getAccount_nm().isEmpty() || item1.getAccount_nm().equals("") || item1.getAccount_nm() == null) {
					listNull1.add(item1.getCode());
				}else {
					if(item1.getCnt() < 3) {
						item1.setType(Const.minusAuto);
					}else {
						item1.setType(Const.plusAuto);
					}
					list.add(item1);
				}
				if(list.size() > 0 && list.size() % 1000 == 0 && i != 0) {
					dao.insert("common.insertExtract1", list);
					list = new ArrayList<MSZ020VO>();
				}
				if(i == listCFS.size() - 1 && list.size() > 0) {
					dao.insert("common.insertExtract1", list);
				}
			}
			
			if(listNull1.size() > 0) {
				searchOptionVO.setList(listNull1);
				
				// 3. 주요 계정에서 영업이익, 매출액, 당기순이익 정보 가져오기. OFS 기준
				@SuppressWarnings("unchecked")
				List<MSZ020VO> listOFSIS = (List<MSZ020VO>) dao.selectList("common.selectListOFSIS", searchOptionVO);
				
				// Null 종목 추리는 용.
				List<MSZ999VO> listNull2 = new ArrayList<MSZ999VO>();
				MSZ999VO nullItem = null;
				
				List<MSZ020VO> list2 = new ArrayList<MSZ020VO>();
				MSZ020VO item2 = null;
				for(int i = 0; i < listOFSIS.size(); i++) {
					item2 = new MSZ020VO();
					item2 = listOFSIS.get(i);
					if(item2.getAccount_nm().isEmpty() || item2.getAccount_nm().equals("") || item2.getAccount_nm() == null) {
						nullItem = new MSZ999VO();
						nullItem.setCode(item2.getCode());
						nullItem.setStep("1");
						nullItem.setStatus("Y");
						listNull2.add(nullItem);
					}else {
						if(item2.getCnt() < 3) {
							item2.setType(Const.minusAuto);
						}else {
							item2.setType(Const.plusAuto);
						}
						list2.add(item2);
					}
					if(list2.size() > 0 && list2.size() % 1000 == 0 && i != 0) {
						dao.insert("common.insertExtract1", list2);
						list2 = new ArrayList<MSZ020VO>();
					}
					if(i == listOFSIS.size() - 1 && list2.size() > 0) {
						dao.insert("common.insertExtract1", list2);
					}
				}
				
				// 5. Null 종목 추려서 보여주기. IFRS XBRL 재무제표 제출 의무가 없는 회사. 금융업의 경우 이런 경우가 많음. 네이버에서 정보 추출하기.
				dao.insert("common.insertNullItems", listNull2);
			}
		}else {	// 0-2 없으면 조회하지 않고 재추출 필요 알림 주기.
			
		}
		
		return modelAndView;
	}
	*/
	
//	@RequestMapping(value = "/firstStep.do", method = RequestMethod.GET)
//	public String firstStep(@ModelAttribute("searchOptionVO") SearchOptionVO searchOptionVO, ModelMap model, 
//			HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception{
//		
//		// 1. 주요 게정에서 영업이익, 매출액, 당기순이익 정보 가져오기. CFS 기준
//		@SuppressWarnings("unchecked")
//		List<MSZ020VO> listCFS = (List<MSZ020VO>) dao.selectList("common.selectListPlusCFS", searchOptionVO);
//		List<MSZ020VO> resultAll = new ArrayList<MSZ020VO>();
//		List<MSZ020VO> resultCFS = new ArrayList<MSZ020VO>();
//		MSZ020VO result = null;
//		ArrayList<String> codeList = new ArrayList<String>();
//		for(int i = 0; i < listCFS.size(); i++) {
//			result = new MSZ020VO();
//			result = listCFS.get(i);
//			if(result.getCnt() == 3) {
//				resultCFS.add(result);
//				resultAll.add(result);
//				codeList.add(result.getCode());
//			}
//		}
//		model.addAttribute("resultCFS", resultCFS);
//		
//		// 2. 주요 게정에서 영업이익, 매출액, 당기순이익 정보 가져오기. OFS 기준
//		@SuppressWarnings("unchecked")
//		List<MSZ020VO> listOFSIS = (List<MSZ020VO>) dao.selectList("common.selectListPlusOFSIS", searchOptionVO);
//		List<MSZ020VO> resultOFSIS = new ArrayList<MSZ020VO>();
//		for(int i = 0; i < listOFSIS.size(); i++) {
//			result = new MSZ020VO();
//			result = listOFSIS.get(i);
//			if(result.getCnt() == 3) {
//				resultOFSIS.add(result);
//				resultAll.add(result);
//				codeList.add(result.getCode());
//			}
//		}
//		model.addAttribute("resultOFSIS", resultOFSIS);
//		model.addAttribute("resultAll", resultAll);
//		System.out.println(resultAll.size());
//		
//		HashSet<String> codeList2 = new HashSet<String>(codeList);
//		ArrayList<String> codeList3 = new ArrayList<String>(codeList2);
//		model.addAttribute("resultCodeList", codeList3);
//		System.out.println(codeList3);
//		
//		
//		@SuppressWarnings("unchecked")
//		List<MSZ020VO> bondList = (List<MSZ020VO>) dao.selectList("common.selectNoBond", codeList3);
//		List<MSZ020VO> resultNoBondList = new ArrayList<MSZ020VO>();
//		List<MSZ020VO> resultBondList = new ArrayList<MSZ020VO>();
//		
//		for(int i = 0; i < bondList.size(); i++) {
//			result = new MSZ020VO();
//			result = bondList.get(i);
//			if(result.getCnt() > 0) {
//				resultBondList.add(result);
//			}else {
//				resultNoBondList.add(result);
//			}
//		}
//		System.out.println(resultBondList);
//		model.addAttribute("resultNoBondList", resultNoBondList);
//		
//		// 3. IFRS 기준으로 되어 있지 않은 회사 목록. Null 기준으로 확인.
////		@SuppressWarnings("unchecked")
////		List<MSZ020VO> listOFSIS = (List<MSZ020VO>) dao.selectList("common.selectListPlusOFSIS", searchOptionVO);
////		List<MSZ020VO> resultOFSIS = new ArrayList<MSZ020VO>();
////		for(int i = 0; i < listOFSIS.size(); i++) {
////			result = new MSZ020VO();
////			result = listOFSIS.get(i);
////			if(result.getCnt() == 3) {
////				resultOFSIS.add(result);
////			}
////		}
////		model.addAttribute("resultOFSIS", resultOFSIS);
//		
//		return "step1";
//	}
	
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
	
	// 사채 없는 종목 추출. 이전 버전.
//	@RequestMapping(value = "/secondStep.do", method = RequestMethod.GET)
//	public String secondStep(@ModelAttribute("searchOptionVO") SearchOptionVO searchOptionVO, ModelMap model, 
//			HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception{
//				
//		int resultCnt = dao.selectCnt("common.selectListNoBondCount", searchOptionVO);
//		model.addAttribute("resultCnt", resultCnt);		// 총 건수 표시.
//		int totalPage = (int) Math.ceil((double)resultCnt / Const.length );
////		model.addAttribute("totalPage", totalPage);
//		
//		// Paging 용 정보를 다 만들어서 배열로 넣어준다면? page, currpage, href, 총 페이지는 배열의 수
////		int unitPage = ( searchOptionVO.getPage() - 1 ) / Const.totalpage;
//		PagingVO pagingVO = null;
//		List<PagingVO> pagingList = new ArrayList<PagingVO>();
//		for(int i = 1; i <= totalPage; i++) {
//			pagingVO = new PagingVO();
////			pagingVO.setIdx((unitPage * Const.totalpage) + i);
//			pagingVO.setIdx(i);
//			pagingVO.setPage(searchOptionVO.getPage());
//			pagingList.add(pagingVO);
//		}
//		model.addAttribute("pagingList", pagingList);
//		
//		int offset = ( searchOptionVO.getPage() - 1 ) * searchOptionVO.getLength();
//		searchOptionVO.setOffset(offset);
//		
//		// 사채가 없는 종목 추출.
//		@SuppressWarnings("unchecked")
//		ArrayList<String> noBondList = (ArrayList<String>) dao.selectList("common.selectListNoBond", searchOptionVO);
//		
//		// 사채가 없는 종목 정보 추출
//		@SuppressWarnings("unchecked")
//		List<MSZ020VO> resultList = (List<MSZ020VO>) dao.selectList("common.selectListNoBondInfo", noBondList);
//		model.addAttribute("resultList", resultList);
//		
//		return "step2";
//	}
	
	// 매물대 추출.
	@RequestMapping(value = "/secondStep.do", method = RequestMethod.GET)
	public String secondStep(@ModelAttribute("searchOptionVO") SearchOptionVO searchOptionVO, ModelMap model, 
			HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception{
				
		int resultCnt = dao.selectCnt("common.selectListPlusCount", searchOptionVO);
		model.addAttribute("resultCnt", resultCnt);		// 총 건수 표시.
		int totalPage = (int) Math.ceil((double)resultCnt / Const.length ); // 10 종목씩.
//		int totalPage = (int) Math.ceil((double)resultCnt / Const.length );
//		model.addAttribute("totalPage", totalPage);
		
		// Paging 용 정보를 다 만들어서 배열로 넣어준다면? page, currpage, href, 총 페이지는 배열의 수
//		int unitPage = ( searchOptionVO.getPage() - 1 ) / Const.totalpage;
		PagingVO pagingVO = null;
		List<PagingVO> pagingList = new ArrayList<PagingVO>();
		for(int i = 1; i <= totalPage; i++) {
			pagingVO = new PagingVO();
//			pagingVO.setIdx((unitPage * Const.totalpage) + i);
			pagingVO.setIdx(i);
			pagingVO.setPage(searchOptionVO.getPage());
			pagingList.add(pagingVO);
		}
		model.addAttribute("pagingList", pagingList);
		
		int offset = ( searchOptionVO.getPage() - 1 ) * searchOptionVO.getLength();
		searchOptionVO.setOffset(offset);
		
		// 수익 중인 종목 추출.
		@SuppressWarnings("unchecked")
		ArrayList<String> plusCodeList = (ArrayList<String>) dao.selectList("common.selectListPlusCode", searchOptionVO);
		
		@SuppressWarnings("unchecked")
		List<MSZ020VO> resultList = (List<MSZ020VO>) dao.selectList("common.selectListPlus", plusCodeList);
		model.addAttribute("resultList", resultList);
		
		// 매물대 조회.
		JSONArray jsonArray = null;
		
		searchOptionVO.setList(plusCodeList);
		// 반기 매물대 Select
		searchOptionVO.setVolumeType("A");
		@SuppressWarnings("unchecked")
		List<MSZ400VO> volumeAList = (List<MSZ400VO>) dao.selectList("common.selectVolumeByCode", searchOptionVO);
		jsonArray = new JSONArray(volumeAList);
		model.addAttribute("volumeAList", jsonArray);
		
		// 1년 매물대 Select
		searchOptionVO.setVolumeType("B");
		@SuppressWarnings("unchecked")
		List<MSZ400VO> volumeBList = (List<MSZ400VO>) dao.selectList("common.selectVolumeByCode", searchOptionVO);
		jsonArray = new JSONArray(volumeBList);
		model.addAttribute("volumeBList", jsonArray);
		
		// 2년 매물대 Select
		searchOptionVO.setVolumeType("C");
		@SuppressWarnings("unchecked")
		List<MSZ400VO> volumeCList = (List<MSZ400VO>) dao.selectList("common.selectVolumeByCode", searchOptionVO);
		jsonArray = new JSONArray(volumeCList);
		model.addAttribute("volumeCList", jsonArray);
		
		// 3년 매물대 Select
		searchOptionVO.setVolumeType("D");
		@SuppressWarnings("unchecked")
		List<MSZ400VO> volumeDList = (List<MSZ400VO>) dao.selectList("common.selectVolumeByCode", searchOptionVO);
		jsonArray = new JSONArray(volumeDList);
		model.addAttribute("volumeDList", jsonArray);
		
		
//		@SuppressWarnings("unchecked")
//		List<MSZ300VO> volumeList = (List<MSZ300VO>) dao.selectList("common.selectVolumeByCode", plusCodeList);
		
		// 매물대 추출 후 DB 에 저장하여 단순 select 하는 로직으로 변경. 아래는 추출하는 extractVolume 으로 이동.
		/*
		if(plusCodeList.size() < 0) { // -> 0 보다 커야 됨. 임시 테스트를 위해 작은걸로 바꿔둠.
			List<MSZ300VO> volumeAList = new ArrayList<MSZ300VO>();	// 반기
			List<MSZ300VO> volumeBList = new ArrayList<MSZ300VO>();	// 1년
			List<MSZ300VO> volumeCList = new ArrayList<MSZ300VO>();	// 2년
			List<MSZ300VO> volumeDList = new ArrayList<MSZ300VO>();	// 3년
			JSONArray jsonArray = null;
//		List<MSZ300VO> tempList = new ArrayList<MSZ300VO>();
			SearchOptionVO code = null;
			// 반기
			for(int i = 0; i < plusCodeList.size(); i++) {
				code = new SearchOptionVO();
				code.setCode(plusCodeList.get(i));
				code.setVolumeType("A");
				@SuppressWarnings("unchecked")
				List<MSZ300VO> tempList = (List<MSZ300VO>) dao.selectList("common.selectVolumeByCode", code);
				volumeAList.addAll(tempList);
			}
			jsonArray = new JSONArray(volumeAList);
			model.addAttribute("volumeAList", jsonArray);
			
			// 1년
			for(int i = 0; i < plusCodeList.size(); i++) {
				code = new SearchOptionVO();
				code.setCode(plusCodeList.get(i));
				code.setVolumeType("B");
				@SuppressWarnings("unchecked")
				List<MSZ300VO> tempList = (List<MSZ300VO>) dao.selectList("common.selectVolumeByCode", code);
				volumeBList.addAll(tempList);
			}
			jsonArray = new JSONArray(volumeBList);
			model.addAttribute("volumeBList", jsonArray);
			
			// 2년
			for(int i = 0; i < plusCodeList.size(); i++) {
				code = new SearchOptionVO();
				code.setCode(plusCodeList.get(i));
				code.setVolumeType("C");
				@SuppressWarnings("unchecked")
				List<MSZ300VO> tempList = (List<MSZ300VO>) dao.selectList("common.selectVolumeByCode", code);
				volumeCList.addAll(tempList);
			}
			jsonArray = new JSONArray(volumeCList);
			model.addAttribute("volumeCList", jsonArray);
			
			// 3년
			for(int i = 0; i < plusCodeList.size(); i++) {
				code = new SearchOptionVO();
				code.setCode(plusCodeList.get(i));
				code.setVolumeType("D");
				@SuppressWarnings("unchecked")
				List<MSZ300VO> tempList = (List<MSZ300VO>) dao.selectList("common.selectVolumeByCode", code);
				volumeDList.addAll(tempList);
			}
			jsonArray = new JSONArray(volumeDList);
			model.addAttribute("volumeDList", jsonArray);
			
		}
		*/
		
		return "step2";
	}
	
	// 일자별 시세 추출.
	@RequestMapping(value = "/daySise.json")
	public ModelAndView daySise(@ModelAttribute("searchOptionVO") SearchOptionVO searchOptionVO, ModelMap model, HttpServletRequest request, 
			HttpServletResponse response, HttpSession session, BindingResult bindingResult) throws Exception{
			
		ModelAndView modelAndView = new ModelAndView();
		MappingJackson2JsonView jsonView = new MappingJackson2JsonView();
		modelAndView.setView(jsonView);
	
		int offset = ( searchOptionVO.getPage() - 1 ) * searchOptionVO.getLength();
		searchOptionVO.setOffset(offset);
		
		// 수익 중인 종목 추출.
		@SuppressWarnings("unchecked")
		ArrayList<String> plusCodeList = (ArrayList<String>) dao.selectList("common.selectListPlusCode", searchOptionVO);
		
		@SuppressWarnings("unchecked")
		List<MSZ300VO> maxList = (List<MSZ300VO>) dao.selectList("common.selectListMaxDate", plusCodeList);
		
		// 일별 : 5개월
		// 주별 : 1년
		// 월별 : 3년 -> 3년치 정보 가져오면 1년, 5개월 해결됨. 1년에 52주 이므로 3년은 156주.
		// 한 페이지에 2주치 정보 포함. 즉, 78번 호출 해야함.
		// 호출 도중 값이 없을 경우 빠져나오도록 하는 로직 추가.
		
		String url = "";
		String code = "";
		Document doc = null;
		List<MSZ300VO> list = new ArrayList<MSZ300VO>();
		MSZ300VO sise = new MSZ300VO();
		MSZ300VO maxInfo = new MSZ300VO();
		int plusCodeListSize = plusCodeList.size();
		boolean chk = false;
//		int plusCodeListSize = 1;
		for(int i = 0; i < plusCodeListSize; i++) {
			code = plusCodeList.get(i);
			for(int x = 0; x < maxList.size(); x++) {
				maxInfo = new MSZ300VO();
				maxInfo = maxList.get(x);
				if(maxInfo.getCode().equals(code))break;
				else maxInfo = new MSZ300VO();
			}
			
			// 한 회사에서 3년치 데이터 가지고 오기. 총 156주. 한 페이지에 2주씩 있으므로 78번 호출.
			// 78번 호출중 3년치 데이터가 없는 경우는 중간에 끊어야함. 한 페이지에 10라인이 있어야 함. 만약 10라인이 안되면 그다음 페이지는 없음.
			for(int x = 0; x < Const._3year; x++) {
				String idx = Integer.toString(x + 1);
				url = Const.siseUrl + code + "&page=" + idx;
				doc = Jsoup.connect(url).get();
				Elements eles = doc.select(".type2 tr");
				Elements navi = doc.select(".Nnavi .on");
				String page = navi.text();
				
				if(page.equals(idx)) {
					for(Element ele : eles) {
						if(!ele.select("td").text().equals("")) {	// 값이 없는 경우는 제외.
							String text = ele.select("td").text();
							String[] textlist = text.split(" ");
							
							sise = new MSZ300VO();
							sise.setCode(code);
							sise.setDate(textlist[0]);												// 날짜
							sise.setEndvalue(new BigDecimal(textlist[1].replaceAll(",", "")));		// 종가
							sise.setFluctuation(new BigDecimal(textlist[2].replaceAll(",", "")));	// 전일비
							sise.setStartvalue(new BigDecimal(textlist[3].replaceAll(",", "")));	// 시가
							sise.setHighvalue(new BigDecimal(textlist[4].replaceAll(",", "")));		// 고가
							sise.setLowvalue(new BigDecimal(textlist[5].replaceAll(",", "")));		// 저가
							sise.setVolume(new BigDecimal(textlist[6].replaceAll(",", "")));		// 거래량
							
							System.out.println(sise.getDate().replace(".", "-") + " code : " + code + " : " + sise.getCode() + " : " + maxInfo.getCode());
							if(maxInfo != null && maxInfo.getDate().equals(sise.getDate().replace(".", "-"))) {
								chk = true;
								dao.update("common.updateDaySise", sise);
								break;	// 현재 for 문 나오기.
							}else {
								list.add(sise);
								for(int j = 0; j < textlist.length; j++){
									System.out.print(code + " : " + textlist[j] + " : ");
									System.out.println();
								}
							}
						}
					}
				}
				
				TimeUnit.SECONDS.sleep(1);	// 1초씩 딜레이 주기.
				if(chk) {
					chk = false;
					break;
				}
			}
			
			// 10개 회사마다 저장하기.
			if(list.size() > 0) {
				dao.insert("common.insertDaySise", list);
				list = new ArrayList<MSZ300VO>();
				
//				if(i % 10 == 0) {
//					dao.insert("common.insertExtract3", list);
//					list = new ArrayList<MSZ300VO>();
//				}
//				if(i == plusCodeListSize) {
//					dao.insert("common.insertExtract3", list);
//				}
			}
		}
		
		return modelAndView;
	}
	
	// 매물대 추출.
	@RequestMapping(value = "/extractVolume.json")
	public ModelAndView extractVolume(@ModelAttribute("searchOptionVO") SearchOptionVO searchOptionVO, ModelMap model, HttpServletRequest request, 
		HttpServletResponse response, HttpSession session, BindingResult bindingResult) throws Exception{
				
		ModelAndView modelAndView = new ModelAndView();
		MappingJackson2JsonView jsonView = new MappingJackson2JsonView();
		modelAndView.setView(jsonView);
	
		int offset = ( searchOptionVO.getPage() - 1 ) * searchOptionVO.getLength();
		searchOptionVO.setOffset(offset);
		
		// 수익 중인 종목 추출.
		@SuppressWarnings("unchecked")
		ArrayList<String> plusCodeList = (ArrayList<String>) dao.selectList("common.selectListPlusCode", searchOptionVO);
		
		@SuppressWarnings("unchecked")
		List<MSZ300VO> maxList = (List<MSZ300VO>) dao.selectList("common.selectListMaxDate", plusCodeList);
		
		// 일별 : 5개월
		// 주별 : 1년
		// 월별 : 3년 -> 3년치 정보 가져오면 1년, 5개월 해결됨. 1년에 52주 이므로 3년은 156주.
		// 한 페이지에 2주치 정보 포함. 즉, 78번 호출 해야함.
		// 호출 도중 값이 없을 경우 빠져나오도록 하는 로직 추가.
		
		String status = "N";
		if(plusCodeList.size() > 0) { // -> 0 보다 커야 됨. 임시 테스트를 위해 작은걸로 바꿔둠.
			SearchOptionVO code = null;
			// 반기
			for(int i = 0; i < plusCodeList.size(); i++) {
				code = new SearchOptionVO();
				code.setCode(plusCodeList.get(i));
				code.setVolumeType("A");
				dao.insert("common.updateVolumeByCode", code);
			}
			
			// 1년
			for(int i = 0; i < plusCodeList.size(); i++) {
				code = new SearchOptionVO();
				code.setCode(plusCodeList.get(i));
				code.setVolumeType("B");
				dao.insert("common.updateVolumeByCode", code);
			}
			
			// 2년
			for(int i = 0; i < plusCodeList.size(); i++) {
				code = new SearchOptionVO();
				code.setCode(plusCodeList.get(i));
				code.setVolumeType("C");
				dao.insert("common.updateVolumeByCode", code);
			}
			
			// 3년
			for(int i = 0; i < plusCodeList.size(); i++) {
				code = new SearchOptionVO();
				code.setCode(plusCodeList.get(i));
				code.setVolumeType("D");
				dao.insert("common.updateVolumeByCode", code);
			}
			status = "Y";
		}
		model.addAttribute("status", status);
		
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
			pagingVO.setPage(searchOptionVO.getPage());
			pagingList.add(pagingVO);
		}
		model.addAttribute("pagingList", pagingList);
		
		int offset = ( searchOptionVO.getPage() - 1 ) * searchOptionVO.getLength();
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
	
	// 네이버 실시간 시세 가져오는 부분.
	@RequestMapping(value = "/realTimeSise.json")
	public ModelAndView realTimeSise(@ModelAttribute("searchOptionVO") SearchOptionVO searchOptionVO, ModelMap model, HttpServletRequest request, 
			HttpServletResponse response, HttpSession session, BindingResult bindingResult) throws Exception{
			
		ModelAndView modelAndView = new ModelAndView();
		MappingJackson2JsonView jsonView = new MappingJackson2JsonView();
		modelAndView.setView(jsonView);
	
		int offset = ( searchOptionVO.getPage() - 1 ) * searchOptionVO.getLength();
		searchOptionVO.setOffset(offset);
		
		// 수익 중인 종목 추출.
		@SuppressWarnings("unchecked")
		ArrayList<String> plusCodeList = (ArrayList<String>) dao.selectList("common.selectListPlusCode", searchOptionVO);
		
		String siseUrlA = Const.realTimeSise;
//		String siseUrlB = Const.realTimeSise;
		for(int i = 0; i < plusCodeList.size(); i++) {
			siseUrlA = siseUrlA + plusCodeList.get(i) + ",";
//			if(i < maxCnt) {
//				siseUrlA = siseUrlA + plusCodeList.get(i) + ",";
//			}else {
//				siseUrlB = siseUrlB + plusCodeList.get(i) + ",";
//			}
		}
		
		System.out.println("siseUrlA : " + siseUrlA);
//		System.out.println("siseUrlB : " + siseUrlB);
		
		Document doc = Jsoup.connect(siseUrlA).get();
		model.addAttribute("json", doc.select("body").text());
		
		return modelAndView;
	}
	
	
	// 네이버 재무제표 가져오는 부분.
	@RequestMapping(value = "/getData.json")
	public ModelAndView getData(@ModelAttribute("searchOptionVO") SearchOptionVO searchOptionVO, ModelMap model, HttpServletRequest request, 
			HttpServletResponse response, HttpSession session, BindingResult bindingResult) throws Exception{
			
		ModelAndView modelAndView = new ModelAndView();
		MappingJackson2JsonView jsonView = new MappingJackson2JsonView();
		modelAndView.setView(jsonView);
	
		searchOptionVO.setLength(0);
		
		@SuppressWarnings("unchecked")
		List<MSZ010VO> companyList = (List<MSZ010VO>) dao.selectList("common.selectCompanyList", searchOptionVO);
		model.addAttribute("companyList", companyList);
		
		Document doc = null;
		
		int listLength = companyList.size();
//		int listLength = 2101;
		
		int cnt = 4;	// 4번 진행. 3년전부터 +1년까지해서 총 4번.
		List<MSZ120VO> list = new ArrayList<MSZ120VO>();
		MSZ010VO stock = null;
		for(int i = 2218; i < listLength; i++) {
			String url = Const.stockUrl + companyList.get(i).getCode();
			doc = Jsoup.connect(url).get();
			Elements table = doc.select(".tb_type1_ifrs");
			if(table.isEmpty()) {
				stock = new MSZ010VO();
				stock.setCode(companyList.get(i).getCode());
				stock.setStatus("EX");
				dao.update("common.updateStockMainInfo", stock);
			}else {
				Elements heads = table.select("thead tr");
				Elements body = table.select("tbody tr");
				
				if(heads.size() < 3) continue;
				if(body.size() < 3) continue;
				Elements headslist = heads.get(1).select("th");	// 연도
				Elements bodylist1 = body.get(0).select("td");	// 매출액
				Elements bodylist2 = body.get(1).select("td");	// 영업이익
				Elements bodylist3 = body.get(2).select("td");	// 당기순이익
				
//				String[] headslist = heads.get(1).select("th").text().split(" ");	// 연도
//				String[] bodylist1 = body.get(0).select("td").text().split(" ");	// 매출액
//				String[] bodylist2 = body.get(1).select("td").text().split(" ");	// 영업이익
//				String[] bodylist3 = body.get(2).select("td").text().split(" ");	// 당기순이익
				
				MSZ120VO a = null;
				for(int x = 0; x < cnt; x++) {
					if(headslist.get(x).text().equals("")) continue;
					// 매출액
					a = new MSZ120VO();
					a.setCode(companyList.get(i).getCode());
					a.setYyyymm(headslist.get(x).text().substring(0,  7));
					a.setAccount("SA");	// Sales
					a.setAccount_nm("매출액");
					a.setOrd("1");
					a.setAmount(new BigDecimal(bodylist1.get(x).text().equals("") ? "0" : bodylist1.get(x).text().equals("-") ? "0" : bodylist1.get(x).text().replaceAll(",", "")));
					list.add(a);
					
					// 영업이익
					a = new MSZ120VO();
					a.setCode(companyList.get(i).getCode());
					a.setYyyymm(headslist.get(x).text().substring(0,  7));
					a.setAccount("BP");	// Bussiness Profit
					a.setAccount_nm("영업이익");
					a.setOrd("2");
					a.setAmount(new BigDecimal(bodylist2.get(x).text().equals("") ? "0" : bodylist2.get(x).text().equals("-") ? "0" : bodylist2.get(x).text().replaceAll(",", "")));
					list.add(a);
					
					// 당기순이익
					a = new MSZ120VO();
					a.setCode(companyList.get(i).getCode());
					a.setYyyymm(headslist.get(x).text().substring(0,  7));
					a.setAccount("CI");	// Current Income
					a.setAccount_nm("당기순이익");
					a.setOrd("3");
					a.setAmount(new BigDecimal(bodylist3.get(x).text().equals("") ? "0" : bodylist3.get(x).text().equals("-") ? "0" : bodylist3.get(x).text().replaceAll(",", "")));
					list.add(a);
					
				}
				
				System.out.print(companyList.get(i).getCode() +  " : ");
				if(i % 20 == 19)System.out.println();
				if(i % 100 == 99 || i == (listLength - 1)) {
					System.out.println("insert : " + i);
					dao.insert("common.updateStockInfoByNaver", list);
					list = new ArrayList<MSZ120VO>();
				}
			}
		}
		
		return modelAndView;
	}
}
