package com.mo.mosazing.ServiceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mo.mosazing.Dao.CmmnDao;
import com.mo.mosazing.Service.CmmnService;

@Service
public class CmmnServiceImpl implements CmmnService{
	@Autowired
	private CmmnDao cmmnDao;
	
	@Override
	public List<?> selectList(String queryId, Object paramVO) throws Exception {
		return cmmnDao.selectList(queryId, paramVO);
	}

	@Override
	public Object select(String queryId, Object paramVO) throws Exception {
		return cmmnDao.select(queryId, paramVO);
	}

	@Override
	public int selectCnt(String queryId, Object paramVO) throws Exception {
		return cmmnDao.selectCnt(queryId, paramVO);
	}

	@Override
	public void insert(String queryId, Object paramVO) throws Exception {
		cmmnDao.insert(queryId, paramVO);
	}
	
	@Override
	public String insert_return(String queryId, Object paramVO) throws Exception {
		return (String) cmmnDao.insert_return(queryId, paramVO);
	}

	@Override
	public void update(String queryId, Object paramVO) throws Exception {
		cmmnDao.update(queryId, paramVO);
	}

	@Override
	public void delete(String queryId, Object paramVO) throws Exception {
		cmmnDao.delete(queryId, paramVO);
	}
}
