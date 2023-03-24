package com.mo.mosazing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import com.mo.mosazing.Dao.CmmnDao;
import com.mo.mosazing.Model.FileVO;
import com.mo.mosazing.Model.SearchOptionVO;

/**
 * Handles requests for the application home page.
 */
@Controller
public class LogPageController {
	
	private static final Logger logger = LoggerFactory.getLogger(LogPageController.class);
	
	@Autowired
	private CmmnDao dao;
	
	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "/log.do", method = RequestMethod.GET)
	public String logPage(@ModelAttribute("searchOptionVO") SearchOptionVO searchOptionVO, ModelMap model, 
			HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception{
		
		String path = ResourceUtils.getFile("classpath:log").getAbsolutePath();
		File dir = new File(path);
		FileFilter filter = new FileFilter() {
			
			@Override
			public boolean accept(File pathname) {
				// TODO Auto-generated method stub
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
				int passCnt = 0;
				boolean passChk = false;
				if(pathname.getName().startsWith(sdf.format(Calendar.getInstance().getTime()))) passCnt++;
				if(pathname.getName().endsWith("txt")) passCnt++;
				if(passCnt == 2) passChk = true;
				
				return passChk;
			}
		};
		
		File files[] = dir.listFiles(filter);
		List<FileVO> fileList = new ArrayList<FileVO>();
		FileVO file = null;
		for(int i = 0; i < files.length; i++) {
			file = new FileVO();
			file.setFilenm(files[i].getName());
			fileList.add(file);
		}
		model.addAttribute("fileList", fileList);
		
		return "admin/log";
	}
	
	// Log File Open
	@RequestMapping(value = "/openLogFile.json")
	public ModelAndView openLogFile(@ModelAttribute("fileVO") FileVO fileVO, ModelMap model, HttpServletRequest request, 
			HttpServletResponse response, HttpSession session, BindingResult bindingResult) throws Exception{
			
		ModelAndView modelAndView = new ModelAndView();
		MappingJackson2JsonView jsonView = new MappingJackson2JsonView();
		modelAndView.setView(jsonView);
	
		String path = ResourceUtils.getFile("classpath:log").getAbsolutePath();
		try {
			FileReader fr = new FileReader(path + "/" + fileVO.getFilenm());
			BufferedReader br = new BufferedReader(fr);
			String readLine = null ;
			StringBuffer data = new StringBuffer();
	        while((readLine =  br.readLine()) != null){
	            System.out.println(readLine);
	            data.append(readLine);
	            data.append("\r\n");
	        }
	        model.addAttribute("filedata", data);
		}catch(IOException e) {
			e.printStackTrace();
		}
		
		return modelAndView;
	}
}
