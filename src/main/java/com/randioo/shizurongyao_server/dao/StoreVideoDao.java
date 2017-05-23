package com.randioo.shizurongyao_server.dao;

import com.randioo.randioo_server_base.annotation.MyBatisGameDaoAnnotation;
import com.randioo.randioo_server_base.db.BaseDao;
import com.randioo.shizurongyao_server.entity.bo.StoreVideo;

@MyBatisGameDaoAnnotation
public interface StoreVideoDao extends BaseDao<StoreVideo>{
	

	public Integer getMaxStoreVideoId();
}
