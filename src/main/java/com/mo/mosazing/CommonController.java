package com.mo.mosazing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import com.mo.mosazing.Dao.CmmnDao;
import com.mo.mosazing.Model.MSZ920VO;
import com.mo.mosazing.Model.PagingVO;
import com.mo.mosazing.Model.SearchOptionVO;

@Controller
public class CommonController {

	private static final Logger logger = LoggerFactory.getLogger(Step2Controller.class);
	
	@Autowired
	private CmmnDao dao;
	
	// 일자별 시세 추출.
	@RequestMapping(value = "/getCode.json")
	public ModelAndView daySise(@ModelAttribute("searchOptionVO") SearchOptionVO searchOptionVO, ModelMap model, HttpServletRequest request, 
			HttpServletResponse response, HttpSession session, BindingResult bindingResult) throws Exception{
			
		ModelAndView modelAndView = new ModelAndView();
		MappingJackson2JsonView jsonView = new MappingJackson2JsonView();
		modelAndView.setView(jsonView);
	
		@SuppressWarnings("unchecked")
		List<MSZ920VO> commonCodeList = (List<MSZ920VO>) dao.selectList("common.selectCommonCodeList", searchOptionVO);
		model.addAttribute("commonCodeList", commonCodeList);
		
		return modelAndView;
	}
	
	public PagingVO getPagingInfo(int currpage, int totalcnt) {
		
		int prevpage = 0, nextpage = 0, totalpage = 0;
		PagingVO pagingInfo = new PagingVO();
		List<Integer> pagingList = new ArrayList<Integer>();
		
		if(totalcnt > 0) {	// 검색 결과 있는 경우
			totalpage = (int) Math.ceil((double)totalcnt / Const.length );
			
			if(currpage % Const.unitpage == 0) {
				prevpage = ((currpage / Const.unitpage) - 1) * Const.unitpage;
			}else {
				prevpage = (currpage / Const.unitpage) * Const.unitpage;
			}
			
			nextpage = prevpage + Const.unitpage + 1;
			if(nextpage >= totalpage) nextpage = 0;
			
			pagingInfo.setTotalpage(totalpage);
			pagingInfo.setCurrpage(currpage);
			pagingInfo.setPrevpage(prevpage);
			pagingInfo.setNextpage(nextpage);
			
			int cnt = 0;
			for(int i = prevpage + 1; i <= totalpage; i++) {
				cnt++;
				pagingList.add(i);
				if(cnt == Const.unitpage)break;
			}
		}else {		// 검색 결과 없는 경우.
			pagingInfo.setTotalpage(totalpage);
			pagingInfo.setCurrpage(1);
			pagingInfo.setPrevpage(prevpage);
			pagingInfo.setNextpage(nextpage);
		}
		pagingInfo.setPagelist(pagingList);
		
		return pagingInfo;
	}
	
	// JSON
	public JSONObject readJson(String url) throws IOException, JSONException{
		
		JSONObject obj = readJsonFromUrl(url);
		
		return obj;
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
		// redirect 가 없는 경우 사용 방법.
		// 1. 
//		InputStream is = new URL(url).openStream();
		
		// 2.
//		HttpURLConnection conn = null;
//		URL uri = new URL(url);
//		conn = (HttpURLConnection)uri.openConnection();

		// redirect 가 있는 경우 사용 방법.
		URL uri = new URL(url);
		URLConnection connection = uri.openConnection();
		String redirect = connection.getHeaderField("Location");
		if(redirect != null) {
			connection = new URL(redirect).openConnection();
		}

		try {
			BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream(), Charset.forName("UTF-8")));
//			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String jsonText = jsonReadAll(rd);
			JSONObject json = new JSONObject(jsonText);
			return json;
		} finally {
			
//			conn.close();
		}

	}
}
