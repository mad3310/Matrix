package com.letv.portal.dao;

import java.util.List;
import java.util.Map;

import com.letv.common.dao.IBaseDao;
import com.letv.portal.model.UserModel;

public interface IUserDao extends IBaseDao<UserModel> {
	  void updateByMap(Map<String, Object> params);

	List<UserModel> selectByEmail(String email);
}
