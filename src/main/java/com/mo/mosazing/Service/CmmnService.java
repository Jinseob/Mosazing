package com.mo.mosazing.Service;

import java.util.List;

public interface CmmnService {
	public List<?> selectList(String queryId, Object paramVO) throws Exception;
	
	public Object select(String queryId, Object paramVO) throws Exception;
	
	public int selectCnt(String queryId, Object paramVO) throws Exception;
	
	public void insert(String queryId, Object paramVO) throws Exception;
	
	public String insert_return(String queryId, Object paramVO) throws Exception;
	
	public void update(String queryId, Object paramVO) throws Exception;
	
	public void delete(String queryId, Object paramVO) throws Exception;
}
