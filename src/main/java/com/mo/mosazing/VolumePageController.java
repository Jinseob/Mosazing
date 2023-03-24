package com.mo.mosazing;

import java.io.BufferedReader;
import java.io.FileReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.mo.mosazing.Dao.CmmnDao;
import com.mo.mosazing.Model.CompareVO;
import com.mo.mosazing.Model.MSZ120VO;
import com.mo.mosazing.Model.MSZ920VO;
import com.mo.mosazing.Model.PagingVO;
import com.mo.mosazing.Model.SearchOptionVO;
import com.mo.mosazing.Model.YYYYMMVO;

/**
 * Handles requests for the application home page.
 */
@Controller
public class VolumePageController {
	
	private static final Logger logger = LoggerFactory.getLogger(VolumePageController.class);
	
	@Autowired
	private CmmnDao dao;
	
	@RequestMapping(value = "/volumePage1.do", method = RequestMethod.GET)
	public String volumePage1(@ModelAttribute("searchOptionVO") SearchOptionVO searchOptionVO, ModelMap model, 
			HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception{
		
		UserController userCon = new UserController();
		System.out.println(userCon.userCheck(session));
		
		// 검색 조건 저장하는 로직 추가.
		if(!searchOptionVO.isEmpty()) {
			System.out.println(!searchOptionVO.isEmpty());
			dao.insert("common.insertSearchOption", searchOptionVO);
		}
		
		// 갯수 확인.
		int resultCnt = 0;
		List<CompareVO> volmuelist = new ArrayList<CompareVO>();	// JSON 에서 데이터 가지고올 때 사용.
		ArrayList<String> codeList = new ArrayList<String>();
		// 1. 거래량 조건이 있는 경우. 파일에서 코드 불러와서 비교하기.
		if(searchOptionVO.getAccount().equals("VO")) {
		
			// 거래량 외의 조건이 있는 경우 값 가지고 오기.
			ArrayList<String> compareList = new ArrayList<String>();
			Map<String, Integer> chkList = new HashMap<String, Integer>();
			searchOptionVO.setLength(0);
			if(searchOptionVO.getSection().isEmpty()) {
				compareList = (ArrayList<String>) dao.selectList("common.selectListCompanyCode", searchOptionVO);
			}else if(searchOptionVO.getSection().equals("3Y")) {
				compareList = (ArrayList<String>) dao.selectList("common.selectListPlusCode", searchOptionVO);
			}
			searchOptionVO.setLength(Const.length);
			for(int i = 0; i < compareList.size(); i++) {
				chkList.put(compareList.get(i), (i + 1));
			}
			
			// 거래량을 가지고와서 이 기준으로 정렬.
			String path = ResourceUtils.getFile("classpath:sise").getAbsolutePath();
			
			// 저장된 시세 파일에서 거래량 기준으로 나열할 코드 추출.
			FileReader fr = new FileReader(path + "/sise.txt");
			BufferedReader br = new BufferedReader(fr);
			String readLine = null ;
			StringBuffer data = new StringBuffer();
	        while((readLine =  br.readLine()) != null){
	            data.append(readLine);
	        }
	        
	        JSONArray jsonArray = new JSONArray(data.toString());
	        JSONObject jsonData = null;
	        
	        String division = "";
	        // mt = 1 코스피, mt = 2 코스닥.
	        
	        for(int j = 0; j < jsonArray.length(); j++) {
	        	jsonData = new JSONObject();
	        	jsonData = (JSONObject) jsonArray.get(j);
	        	
	        	if(chkList.get(jsonData.getString("cd")) != null) volmuelist.add(new CompareVO(new BigDecimal(jsonData.get("aq").toString()), jsonData.getString("cd")));
	        }
	        
	        // 정렬
	        Collections.sort(volmuelist);
	        if(searchOptionVO.getSort().equals("A")) {
	        	Collections.reverse(volmuelist);
	        }
	        resultCnt = volmuelist.size();
		}else {
			// 2. 거래량 조건이 없는 경우 기존 조건만 쓰기.
			if(searchOptionVO.getSection().isEmpty()) {
				if(searchOptionVO.getAccount().isEmpty()) {
					// 전체
					resultCnt = dao.selectCnt("common.selectCompanyCount", searchOptionVO);
				}else if(searchOptionVO.getAccount().equals("X")){
					// 없음
					resultCnt = dao.selectCnt("common.selectCompanyCountX", searchOptionVO);
				}else {
					// 정렬
					resultCnt = dao.selectCnt("common.selectCompanyCountSort", searchOptionVO);
				}
			}else if(searchOptionVO.getSection().equals("3Y")) {
				resultCnt = dao.selectCnt("common.selectListPlusCount", searchOptionVO);
			}
		}
		model.addAttribute("resultCnt", resultCnt);		// 총 건수 표시.
		
		// 페이징 및 목록.
		// Paging
		CommonController cmmn = new CommonController();
		PagingVO pagingInfo = cmmn.getPagingInfo(searchOptionVO.getCurrpage(), resultCnt);
		model.addAttribute("pagingInfo", pagingInfo);
		
		if(resultCnt > 0) {
			int offset = ( searchOptionVO.getCurrpage() - 1 ) * searchOptionVO.getLength();
			searchOptionVO.setOffset(offset);
			
			if(searchOptionVO.getAccount().equals("VO")){
				// 거래량 순으로 정렬할 경우.
				int length = (offset + Const.length) > resultCnt ? resultCnt : (offset + Const.length);
				for(int x = offset; x < length; x++) codeList.add(volmuelist.get(x).getId());
			}else {
				if(searchOptionVO.getSection().isEmpty()) {
					if(searchOptionVO.getAccount().isEmpty()) {
						// 전체
						codeList = (ArrayList<String>) dao.selectList("common.selectListCompanyCode", searchOptionVO);
					}else if(searchOptionVO.getAccount().equals("X")){	
						// 없음
						codeList = (ArrayList<String>) dao.selectList("common.selectListCompanyCodeX", searchOptionVO);
					}else {
						// 정렬
						codeList = (ArrayList<String>) dao.selectList("common.selectListCompanyCodeSort", searchOptionVO);
					}
				}else if(searchOptionVO.getSection().equals("3Y")) {	// 3년 전체 매출액, 영업익, 순이익 + 인 종목.
					if(searchOptionVO.getAccount().isEmpty()) {
						// 코드 순
						codeList = (ArrayList<String>) dao.selectList("common.selectListPlusCode", searchOptionVO);
					}else {
						// 정렬 조건 순
						codeList = (ArrayList<String>) dao.selectList("common.selectListPlusCodeOrderBy", searchOptionVO);
					}
				}
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
					}
					paramList.add(param);
				}
				
				List<MSZ120VO> resultList = new ArrayList<MSZ120VO>();
				MSZ120VO yyyyHeader = null;
				for(int i = 0; i < paramList.size(); i++) {
					param = paramList.get(i);
					@SuppressWarnings("unchecked")
					List<MSZ120VO> tempList = (List<MSZ120VO>) dao.selectList("common.selectItemList", paramList.get(i));
					
					List<MSZ120VO> itemList = new ArrayList<MSZ120VO>();
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
						
						itemList.add(yyyyHeader);
						for(int j = 0; j < tempList.size(); j++) {
							itemList.add(tempList.get(j));
						}
						yyyyHeader.setList(itemList);
					}else {
						// tempList 첫번째행에 Header 내용 추가 입력.
						yyyyHeader = tempList.get(0);
						yyyyHeader.setOrd("0");
					}
					
					resultList.add(yyyyHeader);
				}
				model.addAttribute("resultList", resultList);
			}
		}
		
		// 업종 코드.
		searchOptionVO.setCdtype("WICS");
		searchOptionVO.setCdlevel(2);
		@SuppressWarnings("unchecked")
		List<MSZ920VO> commonCodeList = (List<MSZ920VO>) dao.selectList("common.selectCommonCodeList", searchOptionVO);
		model.addAttribute("commonCodeList", commonCodeList);

		return "release/volumePage1";
	}
}
