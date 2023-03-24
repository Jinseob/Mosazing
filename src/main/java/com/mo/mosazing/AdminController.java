package com.mo.mosazing;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
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
import com.mo.mosazing.Model.CollectorVO;
import com.mo.mosazing.Model.MSZ010VO;
import com.mo.mosazing.Model.MSZ120VO;
import com.mo.mosazing.Model.MSZ920VO;
import com.mo.mosazing.Model.PagingVO;
import com.mo.mosazing.Model.SearchOptionVO;

/**
 * Handles requests for the application home page.
 */
@Controller
public class AdminController {
	
	private static final Logger logger = LoggerFactory.getLogger(AdminController.class);
	
	@Autowired
	private CmmnDao dao;
	
	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/adminPage.do", method = RequestMethod.GET)
	public String adminPage(@ModelAttribute("searchOptionVO") SearchOptionVO searchOptionVO, ModelMap model, 
			HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception{
		
		int resultCnt = dao.selectCnt("common.selectCompanyCount", searchOptionVO);
		model.addAttribute("resultCnt", resultCnt);		// 총 건수 표시.
		
		// Paging
		CommonController cmmn = new CommonController();
		PagingVO pagingInfo = cmmn.getPagingInfo(searchOptionVO.getCurrpage(), resultCnt);
		model.addAttribute("pagingInfo", pagingInfo);
		
		int offset = ( searchOptionVO.getCurrpage() - 1 ) * searchOptionVO.getLength();
		searchOptionVO.setOffset(offset);
		
		@SuppressWarnings("unchecked")
		List<MSZ010VO> companyList = (List<MSZ010VO>) dao.selectList("common.selectCompanyList", searchOptionVO);
		
		if(companyList.size() > 0) {
			Calendar sCal = Calendar.getInstance();
			int date = sCal.get(sCal.DATE);
			int hour = sCal.get(sCal.HOUR_OF_DAY);
			if(hour < 16) {
				sCal.set(Calendar.DATE, date - 1);
			}
			
			sCal.set(Calendar.HOUR_OF_DAY, 16);
			sCal.set(Calendar.MINUTE, 0);
			sCal.set(Calendar.SECOND, 0);
			
			// 시세, 매물대 갱신일
			@SuppressWarnings("unchecked")
			List<MSZ010VO> renewList = (List<MSZ010VO>) dao.selectList("common.selectRenewList", companyList);
			for(int i = 0; i < renewList.size(); i++) {
				for(int j = 0; j < companyList.size(); j++) {
					if(companyList.get(j).getCode().equals(renewList.get(i).getCode())) {
						companyList.get(j).setSise_dt(renewList.get(i).getSise_dt());
						companyList.get(j).setVolume_dt(renewList.get(i).getVolume_dt());
						companyList.get(j).setCompare_dt(renewList.get(i).getCompare_dt());
						if(companyList.get(j).getSise_dt() != null && companyList.get(j).getSise_dt().compareTo(sCal.getTime()) > 0) companyList.get(j).setSiseChk("Y");
						if(companyList.get(j).getVolume_dt() != null && companyList.get(j).getVolume_dt().compareTo(sCal.getTime()) > 0) companyList.get(j).setVolumeChk("Y");
						if(companyList.get(j).getCompare_dt() != null && companyList.get(j).getCompare_dt().compareTo(sCal.getTime()) > 0) companyList.get(j).setCompareChk("Y");
						break;
					}
				}
			}
		}
		model.addAttribute("companyList", companyList);
		
		// 업종 코드.
		searchOptionVO.setCdtype("WICS");
		searchOptionVO.setCdlevel(2);
		@SuppressWarnings("unchecked")
		List<MSZ920VO> commonCodeList = (List<MSZ920VO>) dao.selectList("common.selectCommonCodeList", searchOptionVO);
		model.addAttribute("commonCodeList", commonCodeList);
		
		return "admin/adminPage";
	}
	
	// 네이버 재무제표 가져오는 부분.
	@RequestMapping(value = "/getNaverData.json")
	public ModelAndView getNaverData(@ModelAttribute("searchOptionVO") SearchOptionVO searchOptionVO, ModelMap model, HttpServletRequest request, 
			HttpServletResponse response, HttpSession session, BindingResult bindingResult) throws Exception{
			
		ModelAndView modelAndView = new ModelAndView();
		MappingJackson2JsonView jsonView = new MappingJackson2JsonView();
		modelAndView.setView(jsonView);
	
		int resultCnt = dao.selectCnt("common.selectCompanyCount", searchOptionVO);
		model.addAttribute("resultCnt", resultCnt);		// 총 건수 표시.
		
		int totalPage = (int) Math.ceil((double)resultCnt / Const.length );
		int fPage = searchOptionVO.getFpage() != null ? searchOptionVO.getFpage() : 1;	// 시작 화면
		int tPage = searchOptionVO.getTpage() != null ? searchOptionVO.getTpage() : totalPage;	// 마지막 화면.
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		FileController fileCon = new FileController();
		String msg = new String();
		
//		SearchOptionVO code = null;
		for(int i = fPage; i <= tPage; i++) {
			int offset = ( i - 1 ) * searchOptionVO.getLength();
			searchOptionVO.setOffset(offset);
			@SuppressWarnings("unchecked")
			ArrayList<String> codeList = (ArrayList<String>) dao.selectList("common.selectListCompanyCode", searchOptionVO);
		
			Document doc = null;
		
			int listLength = codeList.size();
//			int listLength = 2;

			msg = i + " 네이버 재무제표 추출 시작!" + sdf.format(Calendar.getInstance().getTime());
			fileCon.fileCreate("", "", msg, true);
			
			int cnt = 3;	// 3번 진행. 3년전부터 +1년까지해서 총 4번. 예상값은 사용하지 않는다.
			List<MSZ120VO> list = new ArrayList<MSZ120VO>();
			MSZ010VO stock = null;
			int insCnt = 0;
//			int iCnt = 1;
			for(int j = 0; j < listLength; j++) {
				msg = codeList.get(j) + ",";
				if(listLength - 1 == j) {
					fileCon.fileCreate("", "", msg, true);
				}else {
					fileCon.fileCreate("", "", msg, false);
				}
				String url = Const.stockUrl + codeList.get(j);
				doc = Jsoup.connect(url).get();
				Elements table = doc.select(".tb_type1_ifrs");
				if(table.isEmpty()) {
					stock = new MSZ010VO();
					stock.setCode(codeList.get(j));
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
					Elements bodylist4 = body.get(5).select("td");	// ROE
					Elements bodylist5 = body.get(10).select("td");	// PER
					Elements bodylist6 = body.get(12).select("td");	// PBR
					
	//				String[] headslist = heads.get(1).select("th").text().split(" ");	// 연도
	//				String[] bodylist1 = body.get(0).select("td").text().split(" ");	// 매출액
	//				String[] bodylist2 = body.get(1).select("td").text().split(" ");	// 영업이익
	//				String[] bodylist3 = body.get(2).select("td").text().split(" ");	// 당기순이익
					
					MSZ120VO a = null;
					boolean delChk = false;	// Delete 한번만 실행하기위한 변수.
					for(int x = 0; x < cnt; x++) {
						if(headslist.get(x).text().equals("")) continue;
						// 매출액
						a = new MSZ120VO();
						a.setCode(codeList.get(j));
						a.setYyyymm(headslist.get(x).text().substring(0,  7));
						a.setAccount("SA");	// Sales
						a.setAccount_nm("매출액");
						a.setOrd("1");
						a.setAmount(new BigDecimal(bodylist1.get(x).text().equals("") ? "0" : bodylist1.get(x).text().equals("-") ? "0" : bodylist1.get(x).text().replaceAll(",", "")));
						list.add(a);
						
						// 영업이익
						a = new MSZ120VO();
						a.setCode(codeList.get(j));
						a.setYyyymm(headslist.get(x).text().substring(0,  7));
						a.setAccount("BP");	// Bussiness Profit
						a.setAccount_nm("영업익");
						a.setOrd("2");
						a.setAmount(new BigDecimal(bodylist2.get(x).text().equals("") ? "0" : bodylist2.get(x).text().equals("-") ? "0" : bodylist2.get(x).text().replaceAll(",", "")));
						list.add(a);
						
						// 당기순이익
						a = new MSZ120VO();
						a.setCode(codeList.get(j));
						a.setYyyymm(headslist.get(x).text().substring(0,  7));
						a.setAccount("CI");	// Current Income
						a.setAccount_nm("순이익");
						a.setOrd("3");
						a.setAmount(new BigDecimal(bodylist3.get(x).text().equals("") ? "0" : bodylist3.get(x).text().equals("-") ? "0" : bodylist3.get(x).text().replaceAll(",", "")));
						list.add(a);

						// ROE
						a = new MSZ120VO();
						a.setCode(codeList.get(j));
						a.setYyyymm(headslist.get(x).text().substring(0,  7));
						a.setAccount("ROE");	// Current Income
						a.setAccount_nm("ROE");
						a.setOrd("4");
						a.setAmount(new BigDecimal(bodylist4.get(x).text().equals("") ? "0" : bodylist4.get(x).text().equals("-") ? "0" : bodylist4.get(x).text().replaceAll(",", "")));
						list.add(a);
						
						// PER
						a = new MSZ120VO();
						a.setCode(codeList.get(j));
						a.setYyyymm(headslist.get(x).text().substring(0,  7));
						a.setAccount("PER");	// Current Income
						a.setAccount_nm("PER");
						a.setOrd("5");
						a.setAmount(new BigDecimal(bodylist5.get(x).text().equals("") ? "0" : bodylist5.get(x).text().equals("-") ? "0" : bodylist5.get(x).text().replaceAll(",", "")));
						list.add(a);
						
						// PBR
						a = new MSZ120VO();
						a.setCode(codeList.get(j));
						a.setYyyymm(headslist.get(x).text().substring(0,  7));
						a.setAccount("PBR");	// Current Income
						a.setAccount_nm("PBR");
						a.setOrd("6");
						a.setAmount(new BigDecimal(bodylist6.get(x).text().equals("") ? "0" : bodylist6.get(x).text().equals("-") ? "0" : bodylist6.get(x).text().replaceAll(",", "")));
						list.add(a);
						
						// 처음 한번만 실행. yyyymm 보다 큰 경우 삭제. 종목별로 삭제.
						if(!delChk) {
							String yyyy = a.getYyyymm().substring(0, 4);
							if(yyyy.length() == 4) {
								dao.delete("common.deleteStockInfoByNaver", a);
								delChk = true;
							}
						}
					}
					
//					if(i % 10 == 9)System.out.println();
					if(insCnt % 100 == 99) {
						dao.insert("common.updateStockInfoByNaver", list);
						list = new ArrayList<MSZ120VO>();
						insCnt = 0;
					}else {
						insCnt++;	// Insert Count 를 더해준다.
					}
				}
			}
			// 마지막에 Insert 가 안된 구문 Insert 하기
			if(insCnt > 0) {
				dao.insert("common.updateStockInfoByNaver", list);
			}
		}
		
		msg = "네이버 재무제표 추출 완료!";
		fileCon.fileCreate("", "", msg, true);
		
		return modelAndView;
	}
	
	// WICS 산업 분류 기준 가져오기.
	@RequestMapping(value = "/getWICS.json", method=RequestMethod.POST)
	public ModelAndView getWICS(@ModelAttribute("searchOptionVO") SearchOptionVO searchOptionVO, ModelMap model, HttpServletRequest request, 
			HttpServletResponse response, HttpSession session, BindingResult bindingResult) throws Exception{
			
		ModelAndView modelAndView = new ModelAndView();
		MappingJackson2JsonView jsonView = new MappingJackson2JsonView();
		modelAndView.setView(jsonView);
		
		Document doc = Jsoup.connect(Const.urlWICSSector).get();
		Elements tr = doc.select(".sector tbody tr");
		
		MSZ920VO level1 = null;
		MSZ920VO level2 = null;
		MSZ920VO level3 = null;
		
		int tdSize = 0;
		for(int i = 0; i < tr.size(); i++) {
			Elements td = tr.get(i).select("td");
			tdSize = td.size();
			if(tdSize == 6) {	// Lv 1, Lv 2, Lv 3
				level1 = new MSZ920VO();
				level2 = new MSZ920VO();
				level3 = new MSZ920VO();
				// Lv 1
				level1.setCdtype("WICS");
				level1.setParentcd("G");
				level1.setCdlevel(1);
				level1.setCd("G" + td.get(0).text());
				level1.setCdnm(td.get(1).text());
				dao.insert("common.insertWICSCode", level1);
//				level1List.add(level1);
				// Lv 2
				level2.setCdtype("WICS");
				level2.setParentcd(level1.getCd());
				level2.setCdlevel(2);
				level2.setCd("G" + td.get(2).text());
				level2.setCdnm(td.get(3).text());
				dao.insert("common.insertWICSCode", level2);
//				level2List.add(level2);
				// Lv 3
				level3.setCdtype("WICS");
				level3.setParentcd(level2.getCd());
				level3.setCdlevel(3);
				level3.setCd("G" + td.get(4).text());
				level3.setCdnm(td.get(5).text());
				dao.insert("common.insertWICSCode", level3);
//				level3List.add(level3);
			}else if(tdSize == 4) {	// Lv 2, Lv 3
				level2 = new MSZ920VO();
				level3 = new MSZ920VO();
				// Lv 2
				level2.setCdtype("WICS");
				level2.setParentcd(level1.getCd());
				level2.setCdlevel(2);
				level2.setCd("G" + td.get(0).text());
				level2.setCdnm(td.get(1).text());
				dao.insert("common.insertWICSCode", level2);
//				level2List.add(level2);
				// Lv 3
				level3.setCdtype("WICS");
				level3.setParentcd(level2.getCd());
				level3.setCdlevel(3);
				level3.setCd("G" + td.get(2).text());
				level3.setCdnm(td.get(3).text());
				dao.insert("common.insertWICSCode", level3);
//				level3List.add(level3);
			}else if(tdSize == 2) {	// Lv 3
				level3 = new MSZ920VO();
				// Lv 3
				level3.setCdtype("WICS");
				level3.setParentcd(level2.getCd());
				level3.setCdlevel(3);
				level3.setCd("G" + td.get(0).text());
				level3.setCdnm(td.get(1).text());
				dao.insert("common.insertWICSCode", level3);
//				level3List.add(level3);
			}
		}
		
		return modelAndView;
	}
	
	// 종목을 WICS 기준으로 분류하기. 업종 구분. 
	@RequestMapping(value = "/sortByWICS.json", method=RequestMethod.POST)
	public ModelAndView sortByWICS(@ModelAttribute("searchOptionVO") SearchOptionVO searchOptionVO, ModelMap model, HttpServletRequest request, 
			HttpServletResponse response, HttpSession session, BindingResult bindingResult) throws Exception{
			
		ModelAndView modelAndView = new ModelAndView();
		MappingJackson2JsonView jsonView = new MappingJackson2JsonView();
		modelAndView.setView(jsonView);
		
		searchOptionVO.setCdtype("WICS");
		searchOptionVO.setCdlevel(2);
		@SuppressWarnings("unchecked")
		List<MSZ920VO> wicsList = (List<MSZ920VO>) dao.selectList("common.selectWICSList", searchOptionVO);
		
		CommonController cmmn = new CommonController();
		
		String format = "yyyyMMdd";
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, (cal.get(cal.DAY_OF_MONTH) - 1));
		String url = Const.urlWICS + sdf.format(cal.getTime()) + "&sec_cd=";	// sector 에 있는 값들을 가지고와서
//		String url = Const.urlWICS + "20201122" + "&sec_cd=";
		// 영업일 확인을 위한 단순 반복문.
//		for(int i = 0; i < 20; i++) {
//			JSONObject jsonObject = cmmn.readJson(url + "G10");
//			JSONArray list = (JSONArray) jsonObject.get("list");
//			if(list.length() < 1) {
//				cal.set(Calendar.DAY_OF_MONTH, (cal.get(cal.DAY_OF_MONTH) - 1));
//				url = Const.urlWICS + sdf.format(cal.getTime()) + "&sec_cd=";
//			}else {
//				break;
//			}
//		}
		
		
		MSZ920VO wicsItem = null;
		int cnt = wicsList.size();
//		int cnt = 1;
		ArrayList<String> companyList = null;
		for(int i = 0; i < cnt; i++) {
			companyList = new ArrayList<String>();
			wicsItem = new MSZ920VO();
			wicsItem = wicsList.get(i);
			if(wicsItem.getCd().equals("G0000"))continue;	// 미분류는 나중에 따로 진행.
			JSONObject jsonObject = cmmn.readJson(url + wicsItem.getCd());
			JSONArray jsonArray = jsonObject.getJSONArray("list");
			for(int j = 0; j < jsonArray.length(); j++) {
				JSONObject obj = (JSONObject) jsonArray.get(j);
				companyList.add(obj.get("CMP_CD").toString());
			}
			System.out.print(i + " : " + wicsItem.getCdnm() + " - 진행");
			if(companyList.size() > 0) {
				searchOptionVO.setWics(wicsItem.getCd());
				searchOptionVO.setList(companyList);
				dao.update("common.updateWICSInfo", searchOptionVO);
				System.out.println(" : " + companyList.size() + " 건 - 완료");
			}
		}
		
		// WICS 미분류에 G0000 넣어주기.
		if(cnt > 0) {
			searchOptionVO.setWics("G0000");
			dao.update("common.updateWICSInfoUndivide", searchOptionVO);
		}
		
		return modelAndView;
	}
	
	// 현재 시세 가지고오기.
	@RequestMapping(value = "/getCurrSise.json", method=RequestMethod.POST)
	public ModelAndView getCurrSise(@ModelAttribute("searchOptionVO") SearchOptionVO searchOptionVO, ModelMap model, HttpServletRequest request, 
			HttpServletResponse response, HttpSession session, BindingResult bindingResult) throws Exception{
			
		ModelAndView modelAndView = new ModelAndView();
		MappingJackson2JsonView jsonView = new MappingJackson2JsonView();
		modelAndView.setView(jsonView);
		
		// 1. 전 종목 코드 가져오기
		searchOptionVO.setLength(0);
		@SuppressWarnings("unchecked")
		List<MSZ010VO> list = (List<MSZ010VO>) dao.selectList("common.selectCompanyList", searchOptionVO);
		
		LogicController logic = new LogicController();
		logic.getNaverSise(list);
		
		return modelAndView;
	}
	
	@RequestMapping(value = "/testPage.do", method = RequestMethod.GET)
	public String testPage(@ModelAttribute("searchOptionVO") SearchOptionVO searchOptionVO, ModelMap model, 
			HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception{
		
		return "test";
	}
	
	@RequestMapping(value = "/chkPortalUrl.json", method=RequestMethod.POST)
	public ModelAndView linkHistoryCollector(@ModelAttribute("collectorVO") CollectorVO collectorVO, ModelMap model, HttpServletRequest request, 
			HttpServletResponse response, HttpSession session, BindingResult bindingResult) throws Exception{
			
		ModelAndView modelAndView = new ModelAndView();
		MappingJackson2JsonView jsonView = new MappingJackson2JsonView();
		modelAndView.setView(jsonView);
		
		dao.insert("common.insertClickDetail", collectorVO);
		
		return modelAndView;
	}
	
}
