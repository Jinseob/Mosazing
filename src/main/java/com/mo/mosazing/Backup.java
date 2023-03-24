package com.mo.mosazing;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import com.mo.mosazing.Model.SearchOptionVO;

public class Backup {
	@RequestMapping(value = "/DataRegister.json", method=RequestMethod.POST)
	public ModelAndView NewsRegister(@ModelAttribute("searchOptionVO") SearchOptionVO searchOptionVO, ModelMap model, HttpServletRequest request, 
			HttpServletResponse response, HttpSession session, BindingResult bindingResult) throws Exception{
	
		
		ModelAndView modelAndView = new ModelAndView();
		MappingJackson2JsonView jsonView = new MappingJackson2JsonView();
		modelAndView.setView(jsonView);
		
//		@SuppressWarnings("unchecked")
//		List<MSZ010VO> companyList = (List<MSZ010VO>) dao.selectList("common.selectCompanyList", searchOptionVO);
//		
//		String status = "";
//		if(companyList.size() > 0) {
//			status = "S";
//		}else {
//			status = "N";
//		}	
//		model.addAttribute("status", status);
//		
//		MSZ010VO company = null;
//		MSZ040VO fnLog = null;
//		String bsns_year = "2019";
//		int companyListSize = companyList.size();
////		int companyListSize = 1;
//		for(int i = 0; i < companyListSize; i++) {
//			company = companyList.get(i);
//			
//			/* 전체 계정 가져오는 부분. 주석
//			// 단일회사 전체 계정
//			String urlAll = Const.urlAll + Const.crtfc_key + "&corp_code=" + company.getDartcd() + "&bsns_year=" + bsns_year + "&reprt_code=" + Const.reprt4_code + "&fs_div=" + Const.cfs;
////			String urlAll = "https://opendart.fss.or.kr/api/fnlttSinglAcntAll.json?crtfc_key=2aa0ca33aacdea40d0e894b1f0db51769c5be020&corp_code=00357935&bsns_year=2019&reprt_code=11011&fs_div=CFS"; // 089470
////			String urlAll = "https://opendart.fss.or.kr/api/fnlttSinglAcntAll.json?crtfc_key=2aa0ca33aacdea40d0e894b1f0db51769c5be020&corp_code=00126487&bsns_year=2019&reprt_code=11011&fs_div=CFS"; // 007700
//			JSONObject jsonObjectAll = readJsonFromUrl(urlAll);
//			
//			if(jsonObjectAll.get("status").toString().equals(Const.success)) {
//				JSONArray jsonArray = jsonObjectAll.getJSONArray("list");
//				// Log 용 데이터 생성.
//				fnLog.setFn_cnt(jsonArray.length());
//				// Log 용 데이터 생성.
//				System.out.println(searchOptionVO.getOffset() + " : "+ company.getCode() + " : " + urlAll);
//				
//				MSZ030VO msz030vo = null;
//				List<MSZ030VO> list = new ArrayList<MSZ030VO>();
//				
//				double thstrm_amount, frmtrm_amount, bfefrmtrm_amount;
//				for(int j = 0; j < jsonArray.length(); j++) {
////					System.out.println(jsonArray.get(j).toString());
//					JSONObject item = new JSONObject(jsonArray.get(j).toString());
//					msz030vo = new MSZ030VO();
//					msz030vo.setCode(company.getCode());
//					msz030vo.setRcept_no(item.has("rcept_no") ? item.get("rcept_no").toString() : "");
//					msz030vo.setReprt_code(item.has("reprt_code") ? item.get("reprt_code").toString() : "");
//					msz030vo.setBsns_year(item.has("bsns_year") ? item.get("bsns_year").toString() : "");
//					msz030vo.setSj_div(item.has("sj_div") ? item.get("sj_div").toString() : "");
//					msz030vo.setSj_nm(item.has("sj_nm") ? item.get("sj_nm").toString() : "");
//					msz030vo.setAccount_id(item.has("account_id") ? item.get("account_id").toString() : "");
//					msz030vo.setAccount_nm(item.has("account_nm") ? item.get("account_nm").toString() : "");
//					msz030vo.setAccount_detail(item.has("account_detail") ? item.get("account_detail").toString() : "");
//					msz030vo.setThstrm_nm(item.has("thstrm_nm") ? item.get("thstrm_nm").toString() : "");
//					thstrm_amount = item.has("thstrm_amount") ? (item.get("thstrm_amount").toString().equals("") ? 0 : Double.parseDouble(item.get("thstrm_amount").toString())) : 0;
//					msz030vo.setThstrm_amount(thstrm_amount);
//					msz030vo.setFrmtrm_nm(item.has("frmtrm_nm") ? item.get("frmtrm_nm").toString() : "");
//					frmtrm_amount = item.has("frmtrm_amount") ? (item.get("frmtrm_amount").toString().equals("") ? 0 : Double.parseDouble(item.get("frmtrm_amount").toString())) : 0;
//					msz030vo.setFrmtrm_amount(frmtrm_amount);
//					msz030vo.setBfefrmtrm_nm(item.has("bfefrmtrm_nm") ? item.get("bfefrmtrm_nm").toString() : "");
//					bfefrmtrm_amount = item.has("bfefrmtrm_amount") ? (item.get("bfefrmtrm_amount").toString().equals("") ? 0 : Double.parseDouble(item.get("bfefrmtrm_amount").toString())) : 0;
//					msz030vo.setBfefrmtrm_amount(bfefrmtrm_amount);
//					msz030vo.setOrd(item.has("ord") ? item.get("ord").toString() : "");
//					
////					dao.insert("insertFnlttSinglAcntAll", msz030vo);
//					list.add(msz030vo);
//				}
//				
//				dao.insert("insertFnlttSinglAcntAll", list);
//			}
//			dao.insert("insertFnLog", fnLog);
//			*/
//			
//			// 단일회사 요약 계정. 전체 계정으로 처리하기.
//			String url = Const.url + Const.crtfc_key + "&corp_code=" + company.getDartcd() + "&bsns_year=" + bsns_year + "&reprt_code=" + Const.reprt4_code;
//			JSONObject jsonObject = readJsonFromUrl(url);
//			
//			// Log 용 데이터 생성.
//			fnLog = new MSZ040VO();
//			fnLog.setCode(company.getCode());
//			fnLog.setBsns_year(bsns_year);
//			fnLog.setUrl(url);
//			fnLog.setStatus(jsonObject.get("status").toString());
//			// Log 용 데이터 생성.
//			
//			if(jsonObject.get("status").equals(Const.success)) {
//				JSONArray jsonArray = jsonObject.getJSONArray("list");
//				// Log 용 데이터 생성.
//				fnLog.setFn_cnt(jsonArray.length());
//				// Log 용 데이터 생성.
//				System.out.println(searchOptionVO.getOffset() + " : "+ company.getCode() + " : " + url);
//				MSZ020VO msz020vo = null;
//				List<MSZ020VO> list = new ArrayList<MSZ020VO>();
//				BigDecimal frmtrm_amount, thstrm_amount, bfefrmtrm_amount;
//				for(int j = 0; j < jsonArray.length(); j++) {
////					System.out.println(jsonArray.get(j).toString());
//					JSONObject item = new JSONObject(jsonArray.get(j).toString());
//					msz020vo = new MSZ020VO();
//					msz020vo.setCode(company.getCode());
//					msz020vo.setRcept_no(item.has("rcept_no") ? item.get("rcept_no").toString() : "");
//					msz020vo.setOrd(item.has("ord") ? item.get("ord").toString() : "");
//					msz020vo.setReprt_code(item.has("reprt_code") ? item.get("reprt_code").toString() : "");
//					msz020vo.setBsns_year(item.has("bsns_year") ? item.get("bsns_year").toString() : "");
//					msz020vo.setSj_div(item.has("sj_div") ? item.get("sj_div").toString() : "");
//					msz020vo.setSj_nm(item.has("sj_nm") ? item.get("sj_nm").toString() : "");
//					msz020vo.setFs_div(item.has("fs_div") ? item.get("fs_div").toString() : "");
//					msz020vo.setFs_nm(item.has("fs_nm") ? item.get("fs_nm").toString() : "");
//					msz020vo.setAccount_nm(item.has("account_nm") ? item.get("account_nm").toString() : "");
//					msz020vo.setFrmtrm_nm(item.has("frmtrm_nm") ? item.get("frmtrm_nm").toString() : "");
//					msz020vo.setFrmtrm_dt(item.has("frmtrm_dt") ? item.get("frmtrm_dt").toString() : "");
//					msz020vo.setThstrm_nm(item.has("thstrm_nm") ? item.get("thstrm_nm").toString() : "");
//					msz020vo.setThstrm_dt(item.has("thstrm_dt") ? item.get("thstrm_dt").toString() : "");
//					msz020vo.setBfefrmtrm_nm(item.has("bfefrmtrm_nm") ? item.get("bfefrmtrm_nm").toString() : "");
//					msz020vo.setBfefrmtrm_dt(item.has("bfefrmtrm_dt") ? item.get("bfefrmtrm_dt").toString() : "");
//					
//					frmtrm_amount = (BigDecimal) (item.has("frmtrm_amount") ? (item.get("frmtrm_amount").toString().equals("-") ? 0 : (item.get("frmtrm_amount").toString().replaceAll(",", ""))) : 0);
//					msz020vo.setFrmtrm_amount(frmtrm_amount);
//					thstrm_amount = (BigDecimal) (item.has("thstrm_amount") ? (item.get("thstrm_amount").toString().equals("-") ? 0 : (item.get("thstrm_amount").toString().replaceAll(",", ""))) : 0);
//					msz020vo.setThstrm_amount(thstrm_amount);
//					bfefrmtrm_amount = (BigDecimal) (item.has("bfefrmtrm_amount") ? (item.get("bfefrmtrm_amount").toString().equals("-") ? 0 : (item.get("bfefrmtrm_amount").toString().replaceAll(",", ""))) : 0);
//					msz020vo.setBfefrmtrm_amount(bfefrmtrm_amount);
//					
////					dao.insert("insertFnlttSinglAcnt", msz020vo);
//					list.add(msz020vo);
//				}
////				
//				dao.insert("common.insertFnlttSinglAcnt", list);
//			}
//			dao.insert("common.insertFnLog", fnLog);
//		}
//		
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
	
	// 사채 없는 종목 추출. 이전 버전.
//		@RequestMapping(value = "/secondStep.do", method = RequestMethod.GET)
//		public String secondStep(@ModelAttribute("searchOptionVO") SearchOptionVO searchOptionVO, ModelMap model, 
//				HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception{
//					
//			int resultCnt = dao.selectCnt("common.selectListNoBondCount", searchOptionVO);
//			model.addAttribute("resultCnt", resultCnt);		// 총 건수 표시.
//			int totalPage = (int) Math.ceil((double)resultCnt / Const.length );
////			model.addAttribute("totalPage", totalPage);
//			
//			// Paging 용 정보를 다 만들어서 배열로 넣어준다면? page, currpage, href, 총 페이지는 배열의 수
////			int unitPage = ( searchOptionVO.getPage() - 1 ) / Const.totalpage;
//			PagingVO pagingVO = null;
//			List<PagingVO> pagingList = new ArrayList<PagingVO>();
//			for(int i = 1; i <= totalPage; i++) {
//				pagingVO = new PagingVO();
////				pagingVO.setIdx((unitPage * Const.totalpage) + i);
//				pagingVO.setIdx(i);
//				pagingVO.setPage(searchOptionVO.getPage());
//				pagingList.add(pagingVO);
//			}
//			model.addAttribute("pagingList", pagingList);
//			
//			int offset = ( searchOptionVO.getPage() - 1 ) * searchOptionVO.getLength();
//			searchOptionVO.setOffset(offset);
//			
//			// 사채가 없는 종목 추출.
//			@SuppressWarnings("unchecked")
//			ArrayList<String> noBondList = (ArrayList<String>) dao.selectList("common.selectListNoBond", searchOptionVO);
//			
//			// 사채가 없는 종목 정보 추출
//			@SuppressWarnings("unchecked")
//			List<MSZ020VO> resultList = (List<MSZ020VO>) dao.selectList("common.selectListNoBondInfo", noBondList);
//			model.addAttribute("resultList", resultList);
//			
//			return "step2";
//		}
}
