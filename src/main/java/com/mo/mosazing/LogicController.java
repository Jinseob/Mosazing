package com.mo.mosazing;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import org.springframework.util.ResourceUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import com.mo.mosazing.Dao.CmmnDao;
import com.mo.mosazing.Model.MSZ010VO;
import com.mo.mosazing.Model.MSZ300VO;
import com.mo.mosazing.Model.MSZ400VO;
import com.mo.mosazing.Model.MSZ800VO;
import com.mo.mosazing.Model.MSZ920VO;
import com.mo.mosazing.Model.SearchOptionVO;

@Controller
public class LogicController {

	private static final Logger logger = LoggerFactory.getLogger(Step2Controller.class);
	
	@Autowired
	private CmmnDao dao;
	
	// 현재 페이지 일자별 시세 추출.
	@RequestMapping(value = "/currPageDaySise.json")
	public ModelAndView currPageDaySise(@ModelAttribute("searchOptionVO") SearchOptionVO searchOptionVO, ModelMap model, HttpServletRequest request, 
			HttpServletResponse response, HttpSession session, BindingResult bindingResult) throws Exception{
			
		ModelAndView modelAndView = new ModelAndView();
		MappingJackson2JsonView jsonView = new MappingJackson2JsonView();
		modelAndView.setView(jsonView);
	
		ArrayList<String> codeList = (ArrayList<String>) searchOptionVO.getCodeList();
		
		@SuppressWarnings("unchecked")
		List<MSZ300VO> maxList = (List<MSZ300VO>) dao.selectList("common.selectListMaxDate", codeList);
		
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
		int plusCodeListSize = codeList.size();
		boolean chk = false;
//			int plusCodeListSize = 1;
		for(int i = 0; i < plusCodeListSize; i++) {
			code = codeList.get(i);
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
							
							System.out.println(sise.getDate().replace(".", "-") + " code : " + code + " : " + sise.getCode());
							if(maxInfo != null && maxInfo.getDate().equals(sise.getDate().replace(".", "-"))) {
								chk = true;
								dao.update("common.updateDaySise", sise);
								break;	// 현재 for 문 나오기.
							}else {
								list.add(sise);
//								for(int j = 0; j < textlist.length; j++){
//									System.out.print(code + " : " + textlist[j] + " : ");
//									System.out.println();
//								}
							}
						}
					}
				}
				
				if(chk) {
					chk = false;
					break;
				}
			}
			
//			TimeUnit.SECONDS.sleep(1);	// 1초씩 딜레이 주기.
			// 10개 회사마다 저장하기.
			if(list.size() > 0) {
				dao.insert("common.insertDaySise", list);
				list = new ArrayList<MSZ300VO>();
				
//					if(i % 10 == 0) {
//						dao.insert("common.insertExtract3", list);
//						list = new ArrayList<MSZ300VO>();
//					}
//					if(i == plusCodeListSize) {
//						dao.insert("common.insertExtract3", list);
//					}
			}
		}
		
		return modelAndView;
	}
	
	// From To 페이지 일자별 시세 추출.
	@RequestMapping(value = "/fromToDaySise.json")
	public ModelAndView fromToDaySise(@ModelAttribute("searchOptionVO") SearchOptionVO searchOptionVO, ModelMap model, HttpServletRequest request, 
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
		for(int i = fPage; i <= tPage; i++) {
			int offset = ( i - 1 ) * searchOptionVO.getLength();
			searchOptionVO.setOffset(offset);
			ArrayList<String> codeList = new ArrayList<String>();
			if(searchOptionVO.getType().isEmpty()) {
				// 코드 순
				codeList = (ArrayList<String>) dao.selectList("common.selectListCompanyCode", searchOptionVO);
			}else {
				// 정렬 조건 순
				codeList = (ArrayList<String>) dao.selectList("common.selectListCompanyCodeOrderBy", searchOptionVO);
			}
			
			@SuppressWarnings("unchecked")
			List<MSZ300VO> maxList = (List<MSZ300VO>) dao.selectList("common.selectListMaxDate", codeList);
			
			// 일별 : 5개월
			// 주별 : 1년
			// 월별 : 3년 -> 3년치 정보 가져오면 1년, 5개월 해결됨. 1년에 52주 이므로 3년은 156주.
			// 한 페이지에 2주치 정보 포함. 즉, 78번 호출 해야함.
			// 호출 도중 값이 없을 경우 빠져나오도록 하는 로직 추가.
			
			msg = i + " 페이지 일자별 시세 추출 시작!" + sdf.format(Calendar.getInstance().getTime());
			fileCon.fileCreate("", "", msg, true);
			
			String url = "";
			String code = "";
			Document doc = null;
			List<MSZ300VO> list = new ArrayList<MSZ300VO>();
			MSZ300VO sise = new MSZ300VO();
			MSZ300VO maxInfo = new MSZ300VO();
			int codeListSize = codeList.size();
			boolean chk = false;
//						int plusCodeListSize = 1;
			for(int j = 0; j < codeListSize; j++) {
				code = codeList.get(j);
				msg = code + ",";
				if(codeListSize - 1 == j) {
					fileCon.fileCreate("", "", msg, true);
				}else {
					fileCon.fileCreate("", "", msg, false);
				}
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
								
								if(maxInfo != null && maxInfo.getDate().equals(sise.getDate().replace(".", "-"))) {
									chk = true;
									dao.update("common.updateDaySise", sise);
									break;	// 현재 for 문 나오기.
								}else {
									list.add(sise);
//									for(int k = 0; k < textlist.length; k++){
//										System.out.print(code + " : " + textlist[k] + " : ");
//										System.out.println();
//									}
								}
							}
						}
					}
					
					if(chk) {
						chk = false;
						break;
					}
				}
				
//				TimeUnit.SECONDS.sleep(1);	// 5초씩 딜레이 주기.
				// 10개 회사마다 저장하기.
				if(list.size() > 0) {
					dao.insert("common.insertDaySise", list);
					list = new ArrayList<MSZ300VO>();
				}
			}
		}
		
		msg = "일자별 시세 추출 완료!";
		fileCon.fileCreate("", "", msg, true);
		
		return modelAndView;
	}
	
	// From To 매물대 추출.
	@RequestMapping(value = "/fromToExtractVolume.json")
	public ModelAndView fromToExtractVolume(@ModelAttribute("searchOptionVO") SearchOptionVO searchOptionVO, ModelMap model, HttpServletRequest request, 
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
//		System.out.println(fPage + " ~ " + tPage + " 매물대 추출 시작! : " + sdf.format(Calendar.getInstance().getTime()));
		SearchOptionVO code = null;
		
		FileController fileCon = new FileController();
		String msg = new String();
		for(int i = fPage; i <= tPage; i++) {
			int offset = ( i - 1 ) * searchOptionVO.getLength();
			searchOptionVO.setOffset(offset);
			@SuppressWarnings("unchecked")
			ArrayList<String> codeList = (ArrayList<String>) dao.selectList("common.selectListCompanyCode", searchOptionVO);
			
			// 일별 : 5개월
			// 주별 : 1년
			// 월별 : 3년 -> 3년치 정보 가져오면 1년, 5개월 해결됨. 1년에 52주 이므로 3년은 156주.
			// 한 페이지에 2주치 정보 포함. 즉, 78번 호출 해야함.
			// 호출 도중 값이 없을 경우 빠져나오도록 하는 로직 추가.
			
			String status = "N";
			if(codeList.size() > 0) { // -> 0 보다 커야 됨. 임시 테스트를 위해 작은걸로 바꿔둠.
				searchOptionVO.setList(codeList);

				msg = i + " 페이지 매물대 추출 시작!" + sdf.format(Calendar.getInstance().getTime());
				fileCon.fileCreate("", "", msg, true);
				// 반기
//				System.out.print("반기 매물대 - ");
//				for(int j = 0; j < codeList.size(); j++) {
//					code = new SearchOptionVO();
//					code.setCode(codeList.get(j));
//					code.setVolumeType("A");
//					dao.insert("common.updateVolumeByCode", code);
//					System.out.print(codeList.get(j) + " : ");
//				}
				searchOptionVO.setVolumeType("A");
				dao.insert("common.updateVolumeByCodeR2", searchOptionVO);
//				System.out.println();
				
				// 1년
//				System.out.print("1년 매물대 - ");
//				for(int j = 0; j < codeList.size(); j++) {
//					code = new SearchOptionVO();
//					code.setCode(codeList.get(j));
//					code.setVolumeType("B");
//					dao.insert("common.updateVolumeByCode", code);
//					System.out.print(codeList.get(j) + " : ");
//				}
				searchOptionVO.setVolumeType("B");
				dao.insert("common.updateVolumeByCodeR2", searchOptionVO);
//				System.out.println();
				
				// 2년
//				System.out.print("2년 매물대 - ");
//				for(int j = 0; j < codeList.size(); j++) {
//					code = new SearchOptionVO();
//					code.setCode(codeList.get(j));
//					code.setVolumeType("C");
//					dao.insert("common.updateVolumeByCode", code);
//					System.out.print(codeList.get(j) + " : ");
//				}
				searchOptionVO.setVolumeType("C");
				dao.insert("common.updateVolumeByCodeR2", searchOptionVO);
//				System.out.println();
				
				// 3년
//				System.out.print("3년 매물대 - ");
//				for(int j = 0; j < codeList.size(); j++) {
//					code = new SearchOptionVO();
//					code.setCode(codeList.get(j));
//					code.setVolumeType("D");
//					dao.insert("common.updateVolumeByCode", code);
//					System.out.print(codeList.get(j) + " : ");
//				}
				searchOptionVO.setVolumeType("D");
				dao.insert("common.updateVolumeByCodeR2", searchOptionVO);
//				System.out.println();
//				status = "Y";
			}
//			model.addAttribute("status", status);
		}
		
		msg = "매물대 추출 끝!";
		fileCon.fileCreate("", "", msg, true);
		
		return modelAndView;
	}
	
	// 매물대 추출.
	@RequestMapping(value = "/extractVolume.json")
	public ModelAndView extractVolume(@ModelAttribute("searchOptionVO") SearchOptionVO searchOptionVO, ModelMap model, HttpServletRequest request, 
		HttpServletResponse response, HttpSession session, BindingResult bindingResult) throws Exception{
				
		ModelAndView modelAndView = new ModelAndView();
		MappingJackson2JsonView jsonView = new MappingJackson2JsonView();
		modelAndView.setView(jsonView);
	
		System.out.println("매물대 추출 시작!");
		
		ArrayList<String> plusCodeList = (ArrayList<String>) searchOptionVO.getCodeList();
		
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
		
		System.out.println("매물대 추출 끝!");
		
		return modelAndView;
	}
	
	// 네이버 실시간 시세 가져오는 부분.
	@RequestMapping(value = "/realTimeSise.json")
	public ModelAndView realTimeSise(@ModelAttribute("searchOptionVO") SearchOptionVO searchOptionVO, ModelMap model, HttpServletRequest request, 
			HttpServletResponse response, HttpSession session, BindingResult bindingResult) throws Exception{
			
		ModelAndView modelAndView = new ModelAndView();
		MappingJackson2JsonView jsonView = new MappingJackson2JsonView();
		modelAndView.setView(jsonView);
	
		// Naver 에서 가져오는 방식. Client 에서 여러번 호출하면 문제 발생 가능성 있음
//		ArrayList<String> plusCodeList = (ArrayList<String>) searchOptionVO.getCodeList();
//		
//		String siseUrlA = Const.realTimeSise;
//		for(int i = 0; i < plusCodeList.size(); i++) {
//			siseUrlA = siseUrlA + plusCodeList.get(i) + ",";
//		}
//		
//		Document doc = Jsoup.connect(siseUrlA).get();
//		model.addAttribute("json", doc.select("body").text());
		
		// 서버에 저장된 파일에서 가져오는 방식.
		ArrayList<String> codeList = (ArrayList<String>) searchOptionVO.getCodeList();
		
		String path = ResourceUtils.getFile("classpath:sise").getAbsolutePath();
		try {
			FileReader fr = new FileReader(path + "/sise.txt");
			BufferedReader br = new BufferedReader(fr);
			String readLine = null ;
			StringBuffer data = new StringBuffer();
	        while((readLine =  br.readLine()) != null){
	            data.append(readLine);
	        }
	        
	        JSONArray jsonArray = new JSONArray(data.toString());
	        Map<String, JSONObject> tempData = new HashMap<String, JSONObject>();
	        JSONObject jsonData = null;
	        // 빠른 검색을 위해 Map 에 데이터 정리.
	        for(int i = 0; i < jsonArray.length(); i++) {
	        	jsonData = new JSONObject();
	        	jsonData = (JSONObject) jsonArray.get(i);
	        	tempData.put(jsonData.getString("cd"), jsonData);
	        }
	        
	        JSONArray resultDatas = new JSONArray();
	        for(int i = 0; i < codeList.size(); i++) {
	        	resultDatas.put(tempData.get(codeList.get(i)));
	        }
	        
	        model.addAttribute("json", resultDatas.toString());
		}catch(IOException e) {
			e.printStackTrace();
		}
		
		return modelAndView;
	}
	
	// 전체 업종 시가 총액 가지고 오는 부분.
	@RequestMapping(value = "/getMarketCap.json")
	public ModelAndView getMarketCap(@ModelAttribute("searchOptionVO") SearchOptionVO searchOptionVO, ModelMap model, HttpServletRequest request, 
			HttpServletResponse response, HttpSession session, BindingResult bindingResult) throws Exception{
			
		ModelAndView modelAndView = new ModelAndView();
		MappingJackson2JsonView jsonView = new MappingJackson2JsonView();
		modelAndView.setView(jsonView);
	
		searchOptionVO.setCdtype("WICS");
		searchOptionVO.setCdlevel(2);
		@SuppressWarnings("unchecked")
		List<MSZ920VO> wicsList = (List<MSZ920VO>) dao.selectList("common.selectWICSList", searchOptionVO);
		
		// 공휴일 제외. 영업일 기준으로 넣는 로직 추가.
		String format = "yyyyMMdd";
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, (cal.get(cal.DAY_OF_MONTH) - 1));
			String url = Const.urlWICS + sdf.format(cal.getTime()) + "&sec_cd=";
//		String url = Const.urlWICS + "20201106" + "&sec_cd=";
		
		CommonController cmmn = new CommonController();
		MSZ920VO wicsItem = null;
		int cnt = wicsList.size();
//			int cnt = 1;
		List<MSZ400VO> tempList = null;
		List<MSZ400VO> companyList = null;
		MSZ400VO compInfo = null;
		BigDecimal sum = null;
		MSZ800VO aggItem = null;
		for(int i = 0; i < cnt; i++) {
			// 업종별 코드 및 시총, 볼륨 가져오는 부분. 시총은 백만원 단위. 1조 단위로 변경 표시.
			tempList = new ArrayList<MSZ400VO>();
			companyList = new ArrayList<MSZ400VO>();
			wicsItem = new MSZ920VO();
			wicsItem = wicsList.get(i);
			JSONObject jsonObject = cmmn.readJson(url + wicsItem.getCd());
			JSONArray jsonArray = jsonObject.getJSONArray("list");
			for(int j = 0; j < jsonArray.length(); j++) {
				compInfo = new MSZ400VO();
				JSONObject obj = (JSONObject) jsonArray.get(j);
				compInfo.setCode(obj.get("CMP_CD").toString());
				compInfo.setMarketcap(new BigDecimal(obj.get("MKT_VAL").toString()));
				tempList.add(compInfo);
			}
			
			// DB 에 저장되어있는 종목들과 비교하여 엄선된 종목들만 추출.
			searchOptionVO.setWics(wicsItem.getCd());
			@SuppressWarnings("unchecked")
			ArrayList<String> codeList = (ArrayList<String>) dao.selectList("common.selectListCodeByWICS", searchOptionVO);
			for(int x = 0; x < tempList.size(); x++) {
				compInfo = new MSZ400VO();
				compInfo = tempList.get(x);
				for(int y = 0; y < codeList.size(); y++) {
					System.out.print(compInfo.getCode() + " : " + codeList.get(y));
					if(compInfo.getCode().equals(codeList.get(y))){
						companyList.add(compInfo);
						break;
					}
				}
			}
			
			// 추출한 종목의 시총의 합을 구하는 부분.
			sum = new BigDecimal(0);
			for(int j = 0; j < companyList.size(); j++) {
				compInfo = new MSZ400VO();
				compInfo = companyList.get(j);
				sum = sum.add(compInfo.getMarketcap());
				System.out.println(compInfo.getCode() + " - " + compInfo.getMarketcap() + " : " + sum);
			}

			// DB 에 업종별로 업데이트 하는 부분.
			if(companyList.size() > 0) {
				aggItem = new MSZ800VO();
				aggItem.setWics(wicsItem.getCd());
				aggItem.setYyyymm("2019.12");
				aggItem.setMarketcap(sum.divide(new BigDecimal(100)));	// 억단위 맞추기.
				
				dao.update("common.updateMarketCap", aggItem);
			}
		}
		
		return modelAndView;
	}
	
	// 매출액, 영업익, 당기순이익 관련 상태값 저장 및 3년 모두 플러스인 종목 추출.
	@RequestMapping(value = "/extract1.json", method=RequestMethod.POST)
	public ModelAndView extract1(@ModelAttribute("searchOptionVO") SearchOptionVO searchOptionVO, ModelMap model, HttpServletRequest request, 
			HttpServletResponse response, HttpSession session, BindingResult bindingResult) throws Exception{
			
		ModelAndView modelAndView = new ModelAndView();
		MappingJackson2JsonView jsonView = new MappingJackson2JsonView();
		modelAndView.setView(jsonView);
	
		// 페이지 별로 진행.
		// A 방법
		// 1. 코드별 yyyymm 을 추출. selectYYYYMM 사용. 3년치
		
		// 2. 코드, yyyymm List 를 param 으로 던져서 프로시저를 돌린다.
		
		// B 방법
		// 1. 프로시저로 코드만 넘겨서 코드별 yyyymm 을 추출.
		
//			searchOptionVO.setBsns_year("2019");	// 현재는 2020 으로 고정.
		// 재추출하는 프로시저 실행.
		dao.update("common.updateFinancialStatus", searchOptionVO);
		
		return modelAndView;
	}
	
	// 업종별 영업익 합, 업종별 영업익 합 - 최대값 - 최소값, 업종별 순이익 합, 업종별 순이익 합 - 최대값 - 최소값, 업종별 매출액 합, 업종별 매출액 합 - 최대값 - 최소값 
	@RequestMapping(value = "/sumFinancial.json", method=RequestMethod.POST)
	public ModelAndView sumFinancial(@ModelAttribute("searchOptionVO") SearchOptionVO searchOptionVO, ModelMap model, HttpServletRequest request, 
			HttpServletResponse response, HttpSession session, BindingResult bindingResult) throws Exception{
			
		ModelAndView modelAndView = new ModelAndView();
		MappingJackson2JsonView jsonView = new MappingJackson2JsonView();
		modelAndView.setView(jsonView);
	
		// WICS 업종 분류 기준
		searchOptionVO.setCdtype("WICS");
		searchOptionVO.setCdlevel(2);
		@SuppressWarnings("unchecked")
		List<MSZ920VO> wicsList = (List<MSZ920VO>) dao.selectList("common.selectWICSList", searchOptionVO);
		
		int cnt = wicsList.size();
		MSZ800VO sum = null;
		MSZ800VO sumItem = null;
		for(int i = 0; i < cnt; i++) {
			// WICS 별로 처리.
			searchOptionVO.setWics(wicsList.get(i).getCd());
			@SuppressWarnings("unchecked")
			ArrayList<String> plusCodeList = (ArrayList<String>) dao.selectList("common.selectListCodeByWICS", searchOptionVO);
			
			if(plusCodeList.size() > 0) {
				searchOptionVO.setList(plusCodeList);
				// 업종별, 계정별 최신 년도 합.
				@SuppressWarnings("unchecked")
				List<MSZ800VO> sumList = (List<MSZ800VO>) dao.selectList("common.selectSumListByWICS", searchOptionVO);
				System.out.println(wicsList.get(i).getCd() + " : " + plusCodeList.size() + " 건");
				
				int sumListSize = sumList.size();
				if(sumListSize >  0) {
					sum = new MSZ800VO();
					sum.setWics(searchOptionVO.getWics());
					for(int j = 0; j < sumListSize; j++) {
						sumItem = sumList.get(j);
						if(sumItem.getAccount().equals("BP")) {
							sum.setBpmax(sumItem.getMax());
							sum.setBpmin(sumItem.getMin());
							sum.setBpsum(sumItem.getSum());
						}else if(sumList.get(j).getAccount().equals("SA")){
							sum.setSamax(sumItem.getMax());
							sum.setSamin(sumItem.getMin());
							sum.setSasum(sumItem.getSum());
						}else if(sumList.get(j).getAccount().equals("CI")){
							sum.setCimax(sumItem.getMax());
							sum.setCimin(sumItem.getMin());
							sum.setCisum(sumItem.getSum());
						}
					}
					
					// Insert or Update
					dao.insert("common.insertSumByWICS", sum);
				}
			}
		}
		
		// 업종별 영업익 합, 업종별 영업익 합 - (최소값 + 최대값)
		// 업종별 순이익 합, 업종별 순이익 합 - (최소값 + 최대값)
		// 업종별 매출액 합, 업종별 매출액 합 - (최소값 + 최대값)
		// 업종별 최고값, 최저값.
		// DB 에 저장해두기. 네이버에서 재무제표 추출후 무조건 돌리는 방향으로 생각하기.
		// 별도로도 실행 가능.
		
		// 영업익 기준 예상 시총. 이를 바탕으로 추측되는 적정 주식가격.
		// 순이익 기준 예상 시총. 이를 바탕으로 추측되는 적정 주식가격.
		// 매출액 기준 예상 시총. 이를 바탕으로 추측되는 적정 주식가격.
		
		// 매수 적정가 추측.
		// 매물대 보다 낮은 가격.
		// 전체 매매 볼륨 기준 분포 비율을 산정하여 매물대가 높은 가격에 반영하여 적정 가격 추측.
		
//			searchOptionVO.setBsns_year("2020");	// 현재는 2020 으로 고정.
//			// 재추출하는 프로시저 실행.
//			dao.update("common.updateFinancialStatus", searchOptionVO);
		
		return modelAndView;
	}
	
	// 전체 업종 시가 총액 가지고 오는 부분.
	@RequestMapping(value = "/getMarketCapNaver.json")
	public ModelAndView getMarketCapNaver(@ModelAttribute("searchOptionVO") SearchOptionVO searchOptionVO, ModelMap model, HttpServletRequest request, 
			HttpServletResponse response, HttpSession session, BindingResult bindingResult) throws Exception{
			
		ModelAndView modelAndView = new ModelAndView();
		MappingJackson2JsonView jsonView = new MappingJackson2JsonView();
		modelAndView.setView(jsonView);
	
		String siseUrl = new String();
		
		searchOptionVO.setCdtype("WICS");
		searchOptionVO.setCdlevel(2);
		@SuppressWarnings("unchecked")
		List<MSZ920VO> wicsList = (List<MSZ920VO>) dao.selectList("common.selectWICSList", searchOptionVO);
		
		CommonController cmmn = new CommonController();
		JSONObject jsonObject = null;
		JSONObject result = null;
		JSONArray areas = null;
		JSONObject area = null;
		JSONArray datas = null;
		JSONObject data = null;
		MSZ010VO uData = null;
		MSZ800VO aggItem = null;
		int cnt = wicsList.size();
//		int cnt = 1;
		for(int i = 0; i < cnt; i++) {
			searchOptionVO.setWics(wicsList.get(i).getCd());
			@SuppressWarnings("unchecked")
			ArrayList<String> codeList = (ArrayList<String>) dao.selectList("common.selectListCodeByWICS", searchOptionVO);
			
			siseUrl = Const.realTimeSise;
			for(int j = 0; j < codeList.size(); j++) {
				siseUrl = siseUrl + codeList.get(j) + ",";
			}
			jsonObject = cmmn.readJson(siseUrl);
			if(!jsonObject.get("resultCode").equals("success")) continue;
			result = (JSONObject) jsonObject.get("result");
			areas = (JSONArray) result.get("areas");
			area = (JSONObject) areas.get(0);
			datas = (JSONArray) area.get("datas");
			
			Map<String, BigDecimal> siseList = new HashMap<String, BigDecimal>();
			BigDecimal val = null;
			for(int j = 0; j < datas.length(); j++) {
				data = (JSONObject) datas.get(j);
				val = new BigDecimal(data.get("nv").toString());
				siseList.put(data.get("cd").toString(), val);	// 현재가 추출.
			}
			
			@SuppressWarnings("unchecked")
			List<MSZ010VO> companyList = (List<MSZ010VO>) dao.selectList("common.selectCompanyInfoList", codeList);
			System.out.println(companyList);
			
			BigDecimal share = new BigDecimal(0);
			BigDecimal nv = new BigDecimal(0);
			BigDecimal aggValue = new BigDecimal(0);
			MSZ010VO company = null;
			for(int j = 0; j < companyList.size(); j++) {
				company = new MSZ010VO();
				company = companyList.get(j);
				
				share = new BigDecimal(company.getShare());
				nv = siseList.get(company.getCode());
				aggValue = aggValue.add(share.multiply(nv));
			}
			
			aggItem = new MSZ800VO();
			aggItem.setWics(company.getWics());
//			aggItem.setYyyymm(searchOptionVO.getBsns_year() + ".12");
			aggItem.setMarketcap(aggValue.divide(new BigDecimal(100000000)).setScale(2, BigDecimal.ROUND_HALF_UP));
			
			dao.update("common.updateMarketCap", aggItem);
		}
		return modelAndView;
	}
	
	// 매물대 조회.
	@RequestMapping(value = "/getVolume.json")
	public ModelAndView getVolume(@ModelAttribute("searchOptionVO") SearchOptionVO searchOptionVO, ModelMap model, HttpServletRequest request, 
			HttpServletResponse response, HttpSession session, BindingResult bindingResult) throws Exception{
			
		ModelAndView modelAndView = new ModelAndView();
		MappingJackson2JsonView jsonView = new MappingJackson2JsonView();
		modelAndView.setView(jsonView);
	
		ArrayList<String> codeList = (ArrayList<String>) searchOptionVO.getCodeList();
		// 매물대 조회.
		
		Map<String, List<MSZ400VO>> volumeList = new HashMap<String, List<MSZ400VO>>();
		searchOptionVO.setList(codeList);
		
		// 반기 매물대 Select
		searchOptionVO.setVolumeType("A");
		@SuppressWarnings("unchecked")
		List<MSZ400VO> volumeAList = (List<MSZ400VO>) dao.selectList("common.selectVolumeByCode", searchOptionVO);
//		jsonArray = new JSONArray(volumeAList);
		volumeList.put("A", volumeAList);
//		model.addAttribute("A", volumeAList);
		
		// 1년 매물대 Select
		searchOptionVO.setVolumeType("B");
		@SuppressWarnings("unchecked")
		List<MSZ400VO> volumeBList = (List<MSZ400VO>) dao.selectList("common.selectVolumeByCode", searchOptionVO);
//		jsonArray = new JSONArray(volumeBList);
//		model.addAttribute("B", volumeBList);
		volumeList.put("B", volumeBList);
		
		// 2년 매물대 Select
		searchOptionVO.setVolumeType("C");
		@SuppressWarnings("unchecked")
		List<MSZ400VO> volumeCList = (List<MSZ400VO>) dao.selectList("common.selectVolumeByCode", searchOptionVO);
//		jsonArray = new JSONArray(volumeCList);
//		model.addAttribute("C", volumeCList);
		volumeList.put("C", volumeCList);
		
		// 3년 매물대 Select
		searchOptionVO.setVolumeType("D");
		@SuppressWarnings("unchecked")
		List<MSZ400VO> volumeDList = (List<MSZ400VO>) dao.selectList("common.selectVolumeByCode", searchOptionVO);
//		jsonArray = new JSONArray(volumeDList);
//		model.addAttribute("D", volumeDList);
		volumeList.put("D", volumeDList);
		
		model.addAttribute("volumeList", volumeList);
		
		return modelAndView;
	}
	
	// Max 매물대 조회.
	@RequestMapping(value = "/getMaxVolume.json")
	public ModelAndView getMaxVolume(@ModelAttribute("searchOptionVO") SearchOptionVO searchOptionVO, ModelMap model, HttpServletRequest request, 
			HttpServletResponse response, HttpSession session, BindingResult bindingResult) throws Exception{
			
		ModelAndView modelAndView = new ModelAndView();
		MappingJackson2JsonView jsonView = new MappingJackson2JsonView();
		modelAndView.setView(jsonView);
	
		ArrayList<String> codeList = (ArrayList<String>) searchOptionVO.getCodeList();

		Map<String, List<MSZ400VO>> volumeMaxList = new HashMap<String, List<MSZ400VO>>();
		searchOptionVO.setList(codeList);
		
		// 반기 Max
		searchOptionVO.setVolumeType("A");
		@SuppressWarnings("unchecked")
		List<MSZ400VO> volumeMaxAList = (List<MSZ400VO>) dao.selectList("common.selectVolumeMaxBycode", searchOptionVO);
		volumeMaxList.put("A", volumeMaxAList);
		
		// 1년 Max
		searchOptionVO.setVolumeType("B");
		@SuppressWarnings("unchecked")
		List<MSZ400VO> volumeMaxBList = (List<MSZ400VO>) dao.selectList("common.selectVolumeMaxBycode", searchOptionVO);
		volumeMaxList.put("B", volumeMaxBList);
		
		// 2년 Max
		searchOptionVO.setVolumeType("C");
		@SuppressWarnings("unchecked")
		List<MSZ400VO> volumeMaxCList = (List<MSZ400VO>) dao.selectList("common.selectVolumeMaxBycode", searchOptionVO);
		volumeMaxList.put("C", volumeMaxCList);
		
		// 3년 Max
		searchOptionVO.setVolumeType("D");
		@SuppressWarnings("unchecked")
		List<MSZ400VO> volumeMaxDList = (List<MSZ400VO>) dao.selectList("common.selectVolumeMaxBycode", searchOptionVO);
		volumeMaxList.put("D", volumeMaxDList);
		
		model.addAttribute("volumeMaxList", volumeMaxList);
		
		return modelAndView;
	}
	
	// 네이버 실시간 시세와 매물대 비교.
	@RequestMapping(value = "/compareVolumeSise.json")
	public ModelAndView compareVolumeSise(@ModelAttribute("searchOptionVO") SearchOptionVO searchOptionVO, ModelMap model, HttpServletRequest request, 
			HttpServletResponse response, HttpSession session, BindingResult bindingResult) throws Exception{
			
		ModelAndView modelAndView = new ModelAndView();
		MappingJackson2JsonView jsonView = new MappingJackson2JsonView();
		modelAndView.setView(jsonView);
	
		int resultCnt = dao.selectCnt("common.selectCompanyCount", searchOptionVO);
		model.addAttribute("resultCnt", resultCnt);		// 총 건수 표시.
		
		int totalPage = (int) Math.ceil((double)resultCnt / Const.length );
		int fPage = searchOptionVO.getFpage() != null ? searchOptionVO.getFpage() : 1;	// 시작 화면
		int tPage = searchOptionVO.getTpage() != null ? searchOptionVO.getTpage() : totalPage;	// 마지막 화면.
		
		FileController fileCon = new FileController();
		String msg = new String();
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		MSZ010VO uData = null;
		List<MSZ010VO> uDataList = new ArrayList<MSZ010VO>();
		for(int i = fPage; i <= tPage; i++) {
			int offset = ( i - 1 ) * searchOptionVO.getLength();
			searchOptionVO.setOffset(offset);
			@SuppressWarnings("unchecked")
			ArrayList<String> codeList = (ArrayList<String>) dao.selectList("common.selectListCompanyCode", searchOptionVO);
		
			if(codeList.size() < 1) continue;	// 종목이 있는 경우만 진행. 
			dao.delete("common.deleteVolumeStatus", codeList);
//			msg = i + " 페이지 매물대와 시세 비교 시작!" + sdf.format(Calendar.getInstance().getTime());
			msg = i + " 페이지 " + codeList.size() + " 건 삭제 완료! - " + sdf.format(Calendar.getInstance().getTime());
			fileCon.fileCreate("", "", msg, true);
			
			// 서버에 저장된 파일에서 가져오는 방식.
			String path = ResourceUtils.getFile("classpath:sise").getAbsolutePath();
			try {
				FileReader fr = new FileReader(path + "/sise.txt");
				BufferedReader br = new BufferedReader(fr);
				String readLine = null ;
				StringBuffer data = new StringBuffer();
		        while((readLine =  br.readLine()) != null){
		            data.append(readLine);
		        }
		        
		        JSONArray jsonArray = new JSONArray(data.toString());
		        Map<String, BigDecimal> siseList = new HashMap<String, BigDecimal>();
		        JSONObject jsonData = null;
		        // 빠른 검색을 위해 Map 에 데이터 정리.
		        for(int j = 0; j < jsonArray.length(); j++) {
		        	jsonData = new JSONObject();
		        	jsonData = (JSONObject) jsonArray.get(j);
		        	siseList.put(jsonData.getString("cd"), new BigDecimal(jsonData.get("nv").toString()));
		        }
		        
		        searchOptionVO.setList(codeList);
				// 반기 Max
				searchOptionVO.setVolumeType("A");
				@SuppressWarnings("unchecked")
				List<MSZ400VO> volumeMaxAList = (List<MSZ400VO>) dao.selectList("common.selectVolumeMaxBycode", searchOptionVO);
				
				// 1년 Max
				searchOptionVO.setVolumeType("B");
				@SuppressWarnings("unchecked")
				List<MSZ400VO> volumeMaxBList = (List<MSZ400VO>) dao.selectList("common.selectVolumeMaxBycode", searchOptionVO);
				
				// 2년 Max
				searchOptionVO.setVolumeType("C");
				@SuppressWarnings("unchecked")
				List<MSZ400VO> volumeMaxCList = (List<MSZ400VO>) dao.selectList("common.selectVolumeMaxBycode", searchOptionVO);
				
				// 3년 Max
				searchOptionVO.setVolumeType("D");
				@SuppressWarnings("unchecked")
				List<MSZ400VO> volumeMaxDList = (List<MSZ400VO>) dao.selectList("common.selectVolumeMaxBycode", searchOptionVO);
				
				// 비교하는 부분
				String code = null;
				MSZ400VO vItem = null;
				String vStatus = new String();	// 상태값. 해당 기간 Max 매물대 가격보다 전일가가 낮으면 Type 표기.
				msg = new String();
				for(int x = 0; x < codeList.size(); x++) {
					code = new String();
					code = codeList.get(x);
					vStatus = new String();
					msg = code + " : ";
					fileCon.fileCreate("", "", msg, false);
					// 반기 확인
					for(int j = 0; j < volumeMaxAList.size(); j++) {
						vItem = new MSZ400VO();
						vItem = volumeMaxAList.get(j);
						if(code.equals(vItem.getCode())) {
							if(siseList.get(code).compareTo(vItem.getF()) < 0) {
								vStatus += "A";
							}
							volumeMaxAList.remove(j);
							break;
						}
					}
					
					// 1년 확인
					for(int j = 0; j < volumeMaxBList.size(); j++) {
						vItem = new MSZ400VO();
						vItem = volumeMaxBList.get(j);
						if(code.equals(vItem.getCode())) {
							if(siseList.get(code).compareTo(vItem.getF()) < 0) {
								vStatus += "B";
							}
							volumeMaxBList.remove(j);
							break;
						}
					}
					
					// 2년 확인
					for(int j = 0; j < volumeMaxCList.size(); j++) {
						vItem = new MSZ400VO();
						vItem = volumeMaxCList.get(j);
						if(code.equals(vItem.getCode())) {
							if(siseList.get(code).compareTo(vItem.getF()) < 0) {
								vStatus += "C";
							}
							volumeMaxCList.remove(j);
							break;
						}
					}
					
					// 3년 확인
					for(int j = 0; j < volumeMaxDList.size(); j++) {
						vItem = new MSZ400VO();
						vItem = volumeMaxDList.get(j);
						if(code.equals(vItem.getCode())) {
							if(siseList.get(code).compareTo(vItem.getF()) < 0) {
								vStatus += "D";
							}
							volumeMaxDList.remove(j);
							break;
						}
					}
					
//					if(!vStatus.isEmpty()) {
						uData = new MSZ010VO();
						uData.setCode(code);
						uData.setVstatus(vStatus);
						
						uDataList.add(uData);
//					}
				}
				fileCon.fileCreate("", "", "", true);
				if(uDataList.size() > 100 || (i == tPage && uDataList.size() > 0)) {
					msg = "InsertVolumeStatus : " + uDataList.size();
					fileCon.fileCreate("", "", msg, true);
					dao.insert("common.insertVolumeStatus", uDataList);
					uDataList = new ArrayList<MSZ010VO>();
				}
				msg = "진행완료 : 매물대 시세 비교";
				fileCon.fileCreate("", "", msg, true);
			}catch(IOException e) {
				e.printStackTrace();
			}
			
			// 네이버 실시간 시세 가져와서 Map 에 넣는 부분.
//			String siseUrl = Const.realTimeSise;
//			for(int j = 0; j < codeList.size(); j++) {
//				siseUrl += codeList.get(j) + ",";
//			}
//			
//			jsonObject = home.readJson(siseUrl);
//			if(!jsonObject.get("resultCode").equals("success")) continue;
//			result = (JSONObject) jsonObject.get("result");
//			areas = (JSONArray) result.get("areas");
//			area = (JSONObject) areas.get(0);
//			datas = (JSONArray) area.get("datas");
//			
//			Map<String, BigDecimal> siseList = new HashMap<String, BigDecimal>();
//			BigDecimal val = null;
//			for(int j = 0; j < datas.length(); j++) {
//				data = (JSONObject) datas.get(j);
//				if(data.get("ms").toString().equals("CLOSE")) {	// 장 종료 후 는 종가로, 종료 전은 전일가로 처리.
//					val = new BigDecimal(data.get("nv").toString());
//				}else {
//					val = new BigDecimal(data.get("sv").toString());
//				}
//				siseList.put(data.get("cd").toString(), val);
//			}
			// 네이버 실시간 시세 가져와서 Map 에 넣는 부분.
		}
		
		return modelAndView;
	}
	
	// 상장 회사 갱신. 한국거래소 상장 회사 검색.
	@RequestMapping(value = "/refreshCorp.json")
	public ModelAndView refreshCorp(@ModelAttribute("searchOptionVO") SearchOptionVO searchOptionVO, ModelMap model, MultipartHttpServletRequest request, 
			HttpServletResponse response, HttpSession session, BindingResult bindingResult) throws Exception{
		
		ModelAndView modelAndView = new ModelAndView();
		MappingJackson2JsonView jsonView = new MappingJackson2JsonView();
		modelAndView.setView(jsonView);
		
		InputStream fileStream = null;
		BufferedReader buf = null;
		String division = request.getParameter("division");
		try {
			MultipartFile file = (MultipartFile) request.getFile("file");
			fileStream = file.getInputStream();
			buf = new BufferedReader(new InputStreamReader(fileStream));
			String line = new String();
			String[] infoList = new String[3];
			String tText = new String();
			int idx = 0;
			int noidx = 0;
			List<MSZ010VO> itemList = new ArrayList<MSZ010VO>();
			MSZ010VO item = null;
			while((line = buf.readLine()) != null) {
				if(idx > 0) {
//					if(idx > 2)break;
					if(line.indexOf(Const.krw) < 0) {
						noidx++;
						continue;	// 빈값의 경우 채워서 넣어주기.
					}
					tText = line;	// 임시 변수에 저장.
					
					// 숫자값들 처리하는 부분. ,와 "" 제거하는 로직.
					Pattern ptrn = Pattern.compile("\"(.*?)\"");	// 따옴표 안에 있는 패턴 추출.
					Matcher matcher = ptrn.matcher(tText);
					while(matcher.find()) {
						tText = matcher.replaceFirst(matcher.group().replace(",", "").replace("\"", ""));
						matcher = ptrn.matcher(tText);
					}
					
					infoList = tText.split(",");
					
					item = new MSZ010VO();
					// 0:번호, 1:종목코드, 2:기업명, 3:업종코드, 4:업종명, 5:상장주식수, 6:자본금(원), 7:액면가(원), 8:통화구분, 9:대표전화, 10:주소, 11:총카운트(X)
					item.setCode(infoList[1]);
					item.setCompnm(infoList[2]);
					item.setType(infoList[3]);
					item.setTypenm(infoList[4]);
					item.setShare(infoList[5]);
					item.setCapital(infoList[6]);
					item.setParvalue(infoList[7]);
					item.setCurrency(infoList[8]);
					item.setTel(infoList[9]);
					item.setAddress(infoList[10]);
					
					// 분류는 입력 받은 것으로.
					item.setDivision(division);
					
					itemList.add(item);
				}
				idx++;
			}
			
			// 총 라인수와 데이터의 총 카운트 수가 같고. 이게 1000보다 작으면 코스피. 크면 코스닥. 그리고 기존에 저장되어 있는 임시 데이터 하나의 값과 비교.
			boolean typeChk = false;
			if(division.equals("KOSPI") && idx < Const.kospiCnt && (noidx + idx - 1) == Integer.parseInt(infoList[11])) {
				typeChk = true;
			}else if(division.equals("KOSDAQ") && idx > Const.kosdaqCnt && (noidx + idx - 1) == Integer.parseInt(infoList[11])) {
				typeChk = true;
			}
			
			if(itemList.size() > 0 && typeChk) {
				// Table copy 후
				dao.insert("common.copyCorpInfoToTemp", item);
				
				// delete 후
				dao.delete("common.deleteCorpInfo", item);
				
				// insert
				dao.insert("common.insertCorpInfo", itemList);
				
				model.addAttribute("status", "S");
				model.addAttribute("msg", idx + " 건의 " + division + " 종목이 갱신되었습니다.");
			}else {
				model.addAttribute("status", "E");
				model.addAttribute("msg", "분류가 잘못 지정되었습니다.\n확인 ");
			}
		}catch(IOException e) {
			e.printStackTrace();
		}finally {
			fileStream.close();
			buf.close();
		}
		
		
		
//		불러온 파일 넣는 부분.		
//		String param = "code=" + "19wdIC5PGedtn/Fz2nC8+9DNrhvHQvXxpRYxxUtMx1LTPBT3FDsIn6OEJHSx6OzZ9oML058K/GWyRwYdy7WkSr2u+715+KrJxWjgGPCnBO5R0kNWTGxpYJhtVONjCJ6wm+oW+sw4u/U7KQoKtrZImaH4gNujE4GmqZQddl6pJsbEigVtkzYxprXh447zb+Hm46Paqliie6KnODnLImoTcUj+oI46pqfdAInGEZTPVwsKRASWB9r6UJYRSU0FYLMuVGEZGBCVW0j3MP5HxUYAqKbvIlmdUlnEoLVe89Md9GgdF3F1SkKUB+DXkG2eAhi95BRAKYEktYcSXOyv9+4aMucBAXu583ePrIjtSxIyB3VQ1lr1OHJACYCqqnHQgahHO/hiKrlax4K7gQ4mMKG4OwW6QrlaDojWYpMu898k9EkULQD2kGKBTJwA1wGj48qcsjdiyfWy1gIZzNy/o1idYCfAJTC1AFWmG3jJtdz9f+cVzwcny6EKwHQljtZAW14IJj9QvTNPLu0EsNeckabjDQ==";
//		String link = "http://file.krx.co.kr/download.jspx";
//		
//		URL url = new URL(link);
//		URLConnection conn = url.openConnection();
//		
//		conn.setDoOutput(true);
//		conn.setUseCaches(false);
//		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
//		
//		DataOutputStream out = null;
//		
//		try {
//			out = new DataOutputStream(conn.getOutputStream());
//			out.writeBytes(param);
//			out.flush();
//		}finally {
//			if(out != null) out.close();
//		}
//		
//		InputStream is = conn.getInputStream();
//		Scanner scan = new Scanner(is);
		
		
		return modelAndView;
	}
	
	// 상장 회사 갱신. 한국거래소 상장 회사 검색. File Upload
	@RequestMapping(value = "/refreshCorpV1.json")
	public ModelAndView refreshCorpV1(@ModelAttribute("searchOptionVO") SearchOptionVO searchOptionVO, ModelMap model, MultipartHttpServletRequest request, 
			HttpServletResponse response, HttpSession session, BindingResult bindingResult) throws Exception{
		
		ModelAndView modelAndView = new ModelAndView();
		MappingJackson2JsonView jsonView = new MappingJackson2JsonView();
		modelAndView.setView(jsonView);
		
		InputStream fileStream = null;
		BufferedReader buf = null;
		try {
			MultipartFile file = (MultipartFile) request.getFile("file");
			fileStream = file.getInputStream();
			buf = new BufferedReader(new InputStreamReader(fileStream, "MS949"));
			String line = new String();
			String[] infoList = new String[3];
			String tText = new String();
			int idx = 0;
			int noidx = 0;
			int kospiCnt = 0;
			int kosdaqCnt = 0;
			List<MSZ010VO> kospiList = new ArrayList<MSZ010VO>();
			List<MSZ010VO> kosdaqList = new ArrayList<MSZ010VO>();
			MSZ010VO item = null;
			while((line = buf.readLine()) != null) {
				if(idx > 0) {
					tText = line;	// 임시 변수에 저장. ISO-8859-1, UTF-8, EUC-KR, MS949
					
					// 숫자값들 처리하는 부분. ,와 "" 제거하는 로직.
					Pattern ptrn = Pattern.compile("\"(.*?)\"");	// 따옴표 안에 있는 패턴 추출.
					Matcher matcher = ptrn.matcher(tText);
					while(matcher.find()) {
						tText = matcher.replaceFirst(matcher.group().replace(",", "").replace("\"", ""));
						matcher = ptrn.matcher(tText);
					}
					
					infoList = tText.split(",");
					
					// 숫자 판별 부분.
					try {
						Double.parseDouble(infoList[0]);
					}catch (NumberFormatException e) {
						noidx++;
						continue;
					}
					
					// 통화구분.
					if(infoList[12].indexOf(Const.krw) < 0) {
						if(infoList[12].equals("")) infoList[12] = Const.krw;
						else {
							noidx++;				
							continue;	// 빈값의 경우 채워서 넣어주기.
						}
					}
					
					item = new MSZ010VO();
					// 0:종목코드, 1:종목명, 2:시장구분, 3:소속부, 4:소속부, 5:업종코드, 6:업종명, 7:결산월, 8:지정자문인, 9:상장주식수, 10:액면가, 11:자본금, 12:통화구분, 13:대표이사, 14:대표전화, 15:주소
					item.setCode(infoList[0]);
					item.setCompnm(infoList[1]);
					item.setDivision(infoList[2]);
					item.setType(infoList[5]);
					item.setTypenm(infoList[6]);
					item.setShare(infoList[9]);
					item.setParvalue(infoList[10].equals("무액면") ? "0" : infoList[10]);
					item.setCapital(infoList[11]);
					item.setCurrency(infoList[12]);
//					item.setTel(infoList[14]);
//					item.setAddress(infoList[15]);
					
					// 분류는 입력 받은 것으로.
					if(item.getDivision().equals("KOSPI")) {
						kospiList.add(item);
					}
					
					if(item.getDivision().equals("KOSDAQ")) {
						kosdaqList.add(item);
					}
				}
				idx++;
			}
			
			if(kospiList.size() > 0) {
				// Table copy 후
				dao.insert("common.copyCorpInfoToTemp", kospiList.get(0));
				
				// delete 후
				dao.delete("common.deleteCorpInfo", kospiList.get(0));
				
				// insert
				dao.insert("common.insertCorpInfo", kospiList);
			}
			
			if(kosdaqList.size() > 0) {
				// Table copy 후
				dao.insert("common.copyCorpInfoToTemp", kosdaqList.get(0));
				
				// delete 후
				dao.delete("common.deleteCorpInfo", kosdaqList.get(0));
				
				// insert
				dao.insert("common.insertCorpInfo", kosdaqList);
			}
			
			model.addAttribute("status", "S");
			model.addAttribute("msg", "KOSPI " + kospiList.size() + " 건, KOSDAQ " + kosdaqList.size() + " 건의 종목이 갱신되었습니다.");
		}catch(IOException e) {
			e.printStackTrace();
		}finally {
			fileStream.close();
			buf.close();
		}
		
		return modelAndView;
	}
	
	@RequestMapping(value = "/onExcludeCode.json")
	public ModelAndView onExcludeCode(@ModelAttribute("searchOptionVO") SearchOptionVO searchOptionVO, ModelMap model, HttpServletRequest request, 
			HttpServletResponse response, HttpSession session, BindingResult bindingResult) throws Exception{
		
		ModelAndView modelAndView = new ModelAndView();
		MappingJackson2JsonView jsonView = new MappingJackson2JsonView();
		modelAndView.setView(jsonView);
		
		String[] codeList = request.getParameterValues("codeChk");
		String exclude = request.getParameter("exclude");
		
		searchOptionVO.setExclude(exclude);
		for(int i = 0; i < codeList.length; i++) {
			searchOptionVO.setCode(codeList[i]);
			dao.update("common.updateStatus", searchOptionVO);
		}
		
		return modelAndView;
	}
	
	@RequestMapping(value = "/favoriteCode.json")
	public ModelAndView favoriteCode(@ModelAttribute("searchOptionVO") SearchOptionVO searchOptionVO, ModelMap model, HttpServletRequest request, 
			HttpServletResponse response, HttpSession session, BindingResult bindingResult) throws Exception{
		
		ModelAndView modelAndView = new ModelAndView();
		MappingJackson2JsonView jsonView = new MappingJackson2JsonView();
		modelAndView.setView(jsonView);
		
		String code = request.getParameter("code");
		String favorite = request.getParameter("favorite");
		
		searchOptionVO.setCode(code);
		searchOptionVO.setFavorite(favorite);
		dao.update("common.updateFavorite", searchOptionVO);
		
		return modelAndView;
	}
	
	public void getNaverSise(List<MSZ010VO> list) {
		// 2. 네이버 시세 가져오기 (전 종목) 500개씩 가져오는것은 문제없음.
		List<String> urlList = new ArrayList<String>();
		String url = Const.realTimeSise;
		int divNum = 500;
		for(int i = 0; i < list.size(); i++) {
			url += list.get(i).getCode() + ",";
			if(i % divNum == divNum - 1) {
				System.out.println("현재카운트 : " + i);
				urlList.add(url);
				url = Const.realTimeSise;
			}else if(i == list.size() - 1) {
				System.out.println("마지막카운트 : " + i);
				urlList.add(url);
				url = Const.realTimeSise;
			}
		}
		
		CommonController cmmn = new CommonController();
		JSONObject jsonObject = null;
		JSONObject result = null;
		JSONArray areas = null;
		JSONObject area = null;
		JSONArray datas = null;
		JSONObject data = null;
		JSONArray resultDatas = new JSONArray();
		for(int i = 0; i < urlList.size(); i++) {
			System.out.println(urlList.get(i));
			try {
				jsonObject = cmmn.readJson(urlList.get(i));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(!jsonObject.get("resultCode").equals("success")) continue;
			result = (JSONObject) jsonObject.get("result");
			areas = (JSONArray) result.get("areas");
			area = (JSONObject) areas.get(0);
			datas = (JSONArray) area.get("datas");
			for(int j = 0; j < datas.length(); j++) {
				data = (JSONObject) datas.get(j);
				resultDatas.put(data);
			}
		}
		
		// 3. 가져온 JSON 데이터 그대로 파일에 저장하기.
		FileController fileCon = new FileController();
		fileCon.siseFileCreate(resultDatas.toString());
	}
}
