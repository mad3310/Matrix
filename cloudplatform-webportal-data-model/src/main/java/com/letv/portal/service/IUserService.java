package com.letv.portal.service;

import java.util.List;
import java.util.Map;

import com.letv.portal.enumeration.UserStatus;
import com.letv.portal.model.UserModel;


public interface IUserService extends IBaseService<UserModel>{
	
	public void saveUserObject(UserModel user);
	
	public UserModel saveUserObjectWithSpecialName(String userName,String loginIp,String email);
	
	public UserModel getUserByName(String userName);
	
	public void updateUserStauts(Long userid,UserStatus status);
	
	public void updateUserLoginInfo(UserModel user,String currentLoginIp);
	
	public boolean existUserByUserName(String userName);
	
	public UserModel getUserById(Long userId);

	public UserModel getUserByNameAndEmail(String userNamePassport, String email);
	
	public void updateByMap(Map<String, Object> params);

	List<UserModel> selectByEmail(String email);
}
