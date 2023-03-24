package com.mo.mosazing;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import com.mo.mosazing.Dao.CmmnDao;
import com.mo.mosazing.Model.MSZ010VO;
import com.mo.mosazing.Model.SearchOptionVO;

@Controller
public class SchedulerController {

	private static final Logger logger = LoggerFactory.getLogger(SchedulerController.class);
	
	@Autowired
	private CmmnDao dao;
	
	// 현재 시세 추출.
	@Scheduled(cron="0 0/5 9-15 ? * MON-FRI")
	public void getNaverSiseScheduler() throws Exception {
		// 1. 전 종목 코드 가져오기
		SearchOptionVO searchOptionVO = new SearchOptionVO();
		searchOptionVO.setLength(0);
		@SuppressWarnings("unchecked")
		List<MSZ010VO> list = (List<MSZ010VO>) dao.selectList("common.selectCompanyList", searchOptionVO);
		
		LogicController logic = new LogicController();
		logic.getNaverSise(list);
	}
}
