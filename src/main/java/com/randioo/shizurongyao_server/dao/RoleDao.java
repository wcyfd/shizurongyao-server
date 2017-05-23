package com.randioo.shizurongyao_server.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.randioo.randioo_server_base.annotation.MyBatisGameDaoAnnotation;
import com.randioo.randioo_server_base.db.BaseDao;
import com.randioo.shizurongyao_server.entity.bo.Role;

@MyBatisGameDaoAnnotation
public interface RoleDao extends BaseDao<Role> {
	Role get(@Param("account") String account, @Param("roleId") int id);

	List<String> getAllAccounts();

	List<String> getAllNames();

	public Integer getMaxRoleId();
	
	List<Role> getAllRoles();

}
