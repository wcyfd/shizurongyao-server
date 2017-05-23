package com.randioo.shizurongyao_server.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.randioo.randioo_server_base.annotation.MyBatisGameDaoAnnotation;
import com.randioo.randioo_server_base.db.BaseDao;
import com.randioo.shizurongyao_server.entity.po.PointRank;

@MyBatisGameDaoAnnotation
public interface PointRankDao extends BaseDao<PointRank> {
	List<PointRank> selectPointRank(@Param("clanId") int clanId, @Param("rankCount") int rankCount);
}
