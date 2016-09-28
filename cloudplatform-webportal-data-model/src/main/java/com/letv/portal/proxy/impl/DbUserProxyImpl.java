package com.letv.portal.proxy.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.letv.common.exception.ValidateException;
import com.letv.common.session.SessionServiceImpl;
import com.letv.common.util.PasswordRandom;
import com.letv.portal.enumeration.DbStatus;
import com.letv.portal.enumeration.DbUserRoleStatus;
import com.letv.portal.enumeration.DbUserStatus;
import com.letv.portal.model.DbUserModel;
import com.letv.portal.proxy.IDbUserProxy;
import com.letv.portal.python.service.IBuildTaskService;
import com.letv.portal.service.IBaseService;
import com.letv.portal.service.IDbUserService;
import com.mysql.jdbc.StringUtils;


@Component
public class DbUserProxyImpl extends BaseProxyImpl<DbUserModel> implements
		IDbUserProxy{

	private final static Logger logger = LoggerFactory.getLogger(DbUserProxyImpl.class);

	@Autowired
	private IDbUserService dbUserService;
	@Autowired
	private IBuildTaskService buildTaskService;
	@Autowired(required=false)
	private SessionServiceImpl sessionService;

	@Value("${default.db.ro.name}")
	private String DEFAULT_DB_RO_NAME;

	@Override
	public IBaseService<DbUserModel> getService() {
		return dbUserService;
	}

	@Override
	public void saveAndBuild(DbUserModel dbUserModel) {

		String[] ips = dbUserModel.getAcceptIp().split(",");
		StringBuffer ids = new StringBuffer();
		String password = PasswordRandom.genStr();
		for (String ip : ips) {
			dbUserModel.setAcceptIp(ip);
			dbUserModel.setPassword(password);
			this.dbUserService.insert(dbUserModel);
			ids.append(dbUserModel.getId()).append(",");
		}
		this.buildTaskService.buildUser(ids.substring(0, ids.length()-1));
	}

	@Override
	public void saveAndBuild(List<DbUserModel> users) {
		if(users.isEmpty()) {
			throw new ValidateException("参数不能为空：users is null");
		}
		StringBuffer ids = new StringBuffer();
		for (DbUserModel dbUser : users) {
			dbUser.setCreateUser(sessionService.getSession().getUserId());
			dbUser.setStatus(DbUserStatus.DEFAULT.getValue());
			this.dbUserService.insert(dbUser);
			ids.append(dbUser.getId()).append(",");
		}
		this.buildTaskService.buildUser(ids.substring(0, ids.length()-1));
	}


	@Override
	public void saveAndBuild(DbUserModel dbUserModel, String ips, String types) {
		if(StringUtils.isNullOrEmpty(ips) || StringUtils.isNullOrEmpty(types) || null == dbUserModel) {
			throw new ValidateException("参数不能为空");
		}
		List<DbUserModel> users = this.transToDbUser(dbUserModel, ips, types);

		this.saveAndBuild(users);
	}

	@Override
	public void updateUserAuthority(DbUserModel dbUserModel, String ips, String types) {
		if(StringUtils.isNullOrEmpty(ips) || StringUtils.isNullOrEmpty(types) || null == dbUserModel) {
			throw new ValidateException("参数不能为空");
		}
		List<String> arryIps = Arrays.asList(ips.split(","));
		List<String> arryTypes = Arrays.asList(types.split(","));
		List<String> formIps = new ArrayList<String>(arryIps);
		List<String> formTypes = new ArrayList<String>(arryTypes);

		List<DbUserModel> adds = new ArrayList<DbUserModel>();
		List<DbUserModel> removes = new ArrayList<DbUserModel>();
		List<DbUserModel> updates = new ArrayList<DbUserModel>();

		List<DbUserModel> oldUsers = this.dbUserService.selectByDbIdAndUsername(dbUserModel.getDbId(), dbUserModel.getUsername());
		boolean flag = true;
		Integer maxConcurrency = dbUserModel.getMaxConcurrency();
		if(null != oldUsers && oldUsers.size()>0) {//如果老用户有备注，新用户也添加备注/密码
			dbUserModel.setDescn(oldUsers.get(0).getDescn());
			dbUserModel.setPassword(oldUsers.get(0).getPassword());
		}
		for (DbUserModel dbUser : oldUsers) {
			String ip = dbUser.getAcceptIp();
			for (int i = 0; i < arryIps.size(); i++) {
				if(ip.equals(arryIps.get(i))) {
					int formType = Integer.parseInt(arryTypes.get(i));
					dbUser.setType(formType);
					dbUser.setMaxConcurrency(maxConcurrency);
					updates.add(dbUser);
					formIps.remove(arryIps.get(i));
					formTypes.remove(arryTypes.get(i));
					flag = false;
					break;
				}
			}
			if(flag) {
				//都不相同，删除。继续遍历
				removes.add(dbUser);
			}
			flag = true;
		}
		//剩余的，新增。
		adds = this.transToDbUser(dbUserModel, formIps, formTypes);

		//分别操作 新增、修改、删除。
		if(!adds.isEmpty()) {
			this.addUserAuthority(adds);
		}
		if(!updates.isEmpty()) {
			//兼容老数据,去掉重复违法数据。
			Set<String> updateIps = new HashSet<String>();
			for (DbUserModel ip : updates) {
				if(updateIps.contains(ip.getAcceptIp())) {
					removes.add(ip);
				}
				updateIps.add(ip.getAcceptIp());
			}
		}
		if(!updates.isEmpty()) {
			this.updateUserAuthority(updates);
		}
		if(!removes.isEmpty()) {
			this.deleteAndBuild(removes);
		}
	}

	@Override
	public void updateDbUser(List<DbUserModel> users) {
		StringBuffer ids = new StringBuffer();
		for (DbUserModel dbUser : users) {
			this.dbUserService.update(dbUser);
			ids.append(dbUser.getId()).append(",");
		}
		this.buildTaskService.buildUser(ids.toString());
	}

    private void addUserAuthority(List<DbUserModel> users) {
        if(users.isEmpty()) {
            throw new ValidateException("参数不能为空：users is null");
        }
        StringBuffer ids = new StringBuffer();
        for (DbUserModel dbUser : users) {
            dbUser.setCreateUser(sessionService.getSession().getUserId());
            dbUser.setStatus(DbUserStatus.DEFAULT.getValue());
            this.dbUserService.insert(dbUser);
            ids.append(dbUser.getId()).append(",");
        }
        this.buildTaskService.updateAuthority(ids.substring(0, ids.length() - 1));
    }

    private void updateUserAuthority(List<DbUserModel> users) {
        if(users.isEmpty()) {
            throw new ValidateException("参数不能为空：users is null");
        }
        StringBuffer ids = new StringBuffer();
        for (DbUserModel dbUser : users) {
            this.dbUserService.update(dbUser);
            ids.append(dbUser.getId()).append(",");
        }
        this.buildTaskService.updateAuthority(ids.toString());
    }
	public void deleteDbUser(String dbUserId){
        this.buildTaskService.deleteDbUser(dbUserId);
		String[] ids = dbUserId.split(",");
		for (String id : ids) {
			DbUserModel dbUserModel = new DbUserModel();
			dbUserModel.setId(Long.parseLong(id));
			this.dbUserService.delete(dbUserModel);
		}
	}

	@Override
	public void deleteAndBuild(List<DbUserModel> users) {
		if(users.isEmpty()) {
			throw new ValidateException("参数不能为空：users is null");
		}
		StringBuffer ids = new StringBuffer();
		for (DbUserModel dbUserModel : users) {
			ids.append(dbUserModel.getId()).append(",");
		}
		this.deleteDbUser(ids.substring(0, ids.length()-1));

	}

	public void buildDbUser(String DbUserId){
		this.buildTaskService.buildUser(DbUserId);
	}

	private List<DbUserModel> transToDbUser(DbUserModel dbUserModel,String ips,String types) {
		List<DbUserModel> users = new ArrayList<DbUserModel>();
		if(StringUtils.isNullOrEmpty(ips) || StringUtils.isNullOrEmpty(types)) {
			return users;
		}
		String[] arryIps = ips.split(",");
		String[] arryTypes = types.split(",");

		return this.transToDbUser(dbUserModel, arryIps, arryTypes);
	}
	private List<DbUserModel> transToDbUser(DbUserModel dbUserModel,String[] ips,String[] types) {
		List<DbUserModel> users = new ArrayList<DbUserModel>();
		for (int i = 0; i < ips.length; i++) {
			DbUserModel dbUser = new DbUserModel();
			BeanUtils.copyProperties(dbUserModel, dbUser);
			dbUser.setAcceptIp(ips[i]);
			dbUser.setType(Integer.parseInt(types[i]));

			List<DbUserModel> existUsers = this.dbUserService.selectByIpAndUsername(dbUser);

			if(existUsers.isEmpty())
				users.add(dbUser);
		}
		return users;
	}
	private List<DbUserModel> transToDbUser(DbUserModel dbUserModel,List<String> ips,List<String> types) {
		List<DbUserModel> users = new ArrayList<DbUserModel>();
		for (int i = 0; i < ips.size(); i++) {
			DbUserModel dbUser = new DbUserModel();
			BeanUtils.copyProperties(dbUserModel, dbUser);
			dbUser.setAcceptIp(ips.get(i));
			dbUser.setType(Integer.parseInt(types.get(i)));
			users.add(dbUser);
		}
		return users;
	}

	@Override
	public void saveOrUpdateIps(Long dbId, String ips) {
		String[] arrs = ips.split(",");
		List<String> newIps = new ArrayList<String>();
		Set<String> tempSet = new HashSet<String>(); //使用set去重
		for (String ip : arrs) {
			if(ip.contains("\n")) {
				String[] arrByN = ip.split("\n");
				for (String string : arrByN) {
					tempSet.add(string);
				}
			} else {
				tempSet.add(ip);
			}
		}
		newIps.addAll(tempSet);

		List<String> oldIps = this.dbUserService.selectIpsFromUser(dbId);

		List<String> temp = new ArrayList<String>(newIps);
		newIps.removeAll(oldIps); //add ips
		oldIps.removeAll(temp); //remove ips

		//add new ips in dbUser
		for (String newIp : newIps) {
			DbUserModel dbUser = new DbUserModel();
			dbUser.setDbId(dbId);
			dbUser.setUsername(DEFAULT_DB_RO_NAME);
			dbUser.setAcceptIp(newIp);
			dbUser.setType(DbUserRoleStatus.RO.getValue());
			dbUser.setMaxConcurrency(50);
			dbUser.setReadWriterRate("2:1");
			dbUser.setStatus(DbStatus.NORMAL.getValue());
			this.insert(dbUser);
		}
		//remove ips in dbUser
		Map<String, Object> params = new HashMap<String,Object>();
		for (String oldIp : oldIps) {
			params.put("dbId", dbId);
			params.put("acceptIp", oldIp);
			params.put("name4Ip", DEFAULT_DB_RO_NAME);
			List<DbUserModel> dbUsers = this.selectByMap(params);
			for (DbUserModel dbUser : dbUsers) {
				this.delete(dbUser);
			}
			params.remove("name4Ip");
			List<DbUserModel> realUsers = this.selectByMap(params);
			if(!realUsers.isEmpty()) {
				this.deleteAndBuild(realUsers);
			}

		}
	}
	
	public boolean checkIp(Long dbId, String ip) {
		Map<String, Object> params = new HashMap<String,Object>();
		params.put("dbId", dbId);
		params.put("acceptIp", ip);
		List<DbUserModel> realUsers = this.selectByMap(params);
		if(!realUsers.isEmpty() && realUsers.size()>1) {
			return true;
		}
		return false;
	}

	@Override
	public void updateSecurity(Long dbId, String username, String password) {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("dbId", dbId);
		map.put("username", username);
		List<DbUserModel> dbUsers = this.dbUserService.selectByMap(map);
		for (DbUserModel dbUserModel : dbUsers) {
			dbUserModel.setPassword(password);
		}
		this.updateDbUser(dbUsers);
	}

	@Override
	public void deleteAndBuild(Long dbId, String username) {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("dbId", dbId);
		map.put("username", username);
		this.deleteAndBuild(this.dbUserService.selectByMap(map));
	}

}
