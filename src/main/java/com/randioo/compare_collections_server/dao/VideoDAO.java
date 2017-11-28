package com.randioo.compare_collections_server.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.randioo.compare_collections_server.entity.bo.Role;
import com.randioo.compare_collections_server.entity.bo.VideoData;
import com.randioo.randioo_server_base.annotation.MyBatisGameDaoAnnotation;
import com.randioo.randioo_server_base.db.BaseDAO;

@MyBatisGameDaoAnnotation
public interface VideoDAO extends BaseDAO<Role> {

	List<VideoData> get(@Param("roleId") int id);
	
	void insert(VideoData v);
	
	VideoData getById(@Param("ID") int id);
}
