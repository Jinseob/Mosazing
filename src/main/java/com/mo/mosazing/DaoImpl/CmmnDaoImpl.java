package com.mo.mosazing.DaoImpl;

import java.util.List;

import javax.annotation.Resource;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import com.mo.mosazing.Dao.CmmnDao;

@Repository
public class CmmnDaoImpl implements CmmnDao {
	@Resource(name="sqlSession")
	private  SqlSession sqlSession;

	@Override
	public List<?> selectList(String queryId, Object paramVO) throws Exception {
		return sqlSession.selectList(queryId, paramVO);
	}

	@Override
	public Object select(String queryId, Object paramVO) throws Exception {
		return sqlSession.selectOne(queryId, paramVO);
	}

	@Override
	public int selectCnt(String queryId, Object paramVO) throws Exception {
		return (Integer)sqlSession.selectOne(queryId, paramVO);
	}

	@Override
	public void insert(String queryId, Object paramVO) throws Exception {
		sqlSession.insert(queryId, paramVO);
	}

	@Override
	public void update(String queryId, Object paramVO) throws Exception {
		sqlSession.update(queryId, paramVO);
	}

	@Override
	public void delete(String queryId, Object paramVO) throws Exception {
		sqlSession.delete(queryId, paramVO);
	}

	@Override
	public String insert_return(String queryId, Object paramVO) throws Exception {
		// TODO Auto-generated method stub
		return Integer.toString(sqlSession.insert(queryId, paramVO));
	}
}
