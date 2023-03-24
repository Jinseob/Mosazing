package com.mo.mosazing;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.ResourceUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import com.mo.mosazing.Dao.CmmnDao;
import com.mo.mosazing.Model.FileVO;
import com.mo.mosazing.Model.SearchOptionVO;

/**
 * Handles requests for the application home page.
 */
@Controller
public class UserController {
	
	private static final Logger logger = LoggerFactory.getLogger(UserController.class);
	
	@Autowired
	private CmmnDao dao;
	
	/**
	 * Simply selects the home view to render by returning its name.
	 */
	// 로그인 여부 확인.
	public boolean userCheck(HttpSession session) throws Exception{
		boolean chk = false;
		
		String id = (String) (session.getAttribute("sessionId") == null ? "" : session.getAttribute("sessionId"));
		if(id.isEmpty()) {
			System.out.println("비어있음");
			session.setAttribute("sessionId", "Hong@hong.com");
		}else {
			chk = true;
			System.out.println(session.getAttribute("sessionId").toString());
		}
		
		return chk;
	}
	
	// 로그인 화면
	@RequestMapping(value = "/loginPage.do")
	public String loginPage(@ModelAttribute("searchOptionVO") SearchOptionVO searchOptionVO, ModelMap model, 
			HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception{
		
		return "release/loginPage";
	}
	
	// 로그인 Process
	
	// 가입 화면
	@RequestMapping(value = "/signupPage.do")
	public String signupPage(@ModelAttribute("searchOptionVO") SearchOptionVO searchOptionVO, ModelMap model, 
			HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception{
		
		return "release/signupPage";
	}
	
	// 가입 Process
	
	// 사용자 정보 화면
	
	// Log File Open
//	@RequestMapping(value = "/userInfo.json")
//	public ModelAndView userInfo(@ModelAttribute("fileVO") FileVO fileVO, ModelMap model, HttpServletRequest request, 
//			HttpServletResponse response, HttpSession session, BindingResult bindingResult) throws Exception{
//			
//		ModelAndView modelAndView = new ModelAndView();
//		MappingJackson2JsonView jsonView = new MappingJackson2JsonView();
//		modelAndView.setView(jsonView);
//	
//		String path = ResourceUtils.getFile("classpath:log").getAbsolutePath();
//		try {
//			FileReader fr = new FileReader(path + "/" + fileVO.getFilenm());
//			BufferedReader br = new BufferedReader(fr);
//			String readLine = null ;
//			StringBuffer data = new StringBuffer();
//	        while((readLine =  br.readLine()) != null){
//	            System.out.println(readLine);
//	            data.append(readLine);
//	            data.append("\r\n");
//	        }
//	        model.addAttribute("filedata", data);
//		}catch(IOException e) {
//			e.printStackTrace();
//		}
//		
//		return modelAndView;
//	}
}
