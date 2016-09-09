package com.letv.portal.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.letv.common.dao.IBaseDao;
import com.letv.common.dao.QueryParam;
import com.letv.common.email.ITemplateMessageSender;
import com.letv.common.paging.impl.Page;
import com.letv.common.session.SessionServiceImpl;
import com.letv.common.util.PasswordRandom;
import com.letv.portal.dao.IDbUserDao;
import com.letv.portal.enumeration.DbUserRoleStatus;
import com.letv.portal.model.DbModel;
import com.letv.portal.model.DbUserModel;
import com.letv.portal.service.IDbService;
import com.letv.portal.service.IDbUserService;
import com.mysql.jdbc.StringUtils;


@Service("dbUserService")
public class DbUserServiceImpl extends BaseServiceImpl<DbUserModel> implements
		IDbUserService{
	
	private final static Logger logger = LoggerFactory.getLogger(DbUserServiceImpl.class);
	
	@Resource
	private IDbUserDao dbUserDao;
	@Resource
	private IDbService dbService;
	
	@Autowired
	private ITemplateMessageSender defaultEmailSender;
	
	@Value("${default.db.ro.name}")
	private String DEFAULT_DB_RO_NAME;
	
	@Autowired(required=false)
	private SessionServiceImpl sessionService;
	
	@Value("${db.mysql.user.reserve.keywords}")
	private String dbUserKeywords;
	
	public DbUserServiceImpl() {
		super(DbUserModel.class);
	}
	
	@Override
	public IBaseDao<DbUserModel> getDao() {
		return dbUserDao;
	}
	
	@Override
	public List<DbUserModel> selectByDbId(Long dbId) {
		return this.dbUserDao.selectByDbId(dbId);
	}

	@Override
	public Page findPagebyParams(Map<String, Object> params, Page page) {
		QueryParam param = new QueryParam(params,page);
		page.setData(this.dbUserDao.selectPageByMap(param));
		page.setTotalRecords(this.dbUserDao.selectByMapCount(params));
		return page;
	}

	@Override
	public Map<String, Object> selectCreateParams(Long id,boolean isVip) {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("id", id);
		if(isVip) {
			params.put("type", "mclustervip");
		} else {
			params.put("zookeeperId", 1);
		}
		return this.dbUserDao.selectCreateParams(params);
	}

	@Override
	public void updateStatus(DbUserModel dbUserModel) {
		this.dbUserDao.updateStatus(dbUserModel);
	}
	
	@Override
	public List<DbUserModel> selectByIpAndUsername(DbUserModel dbUserModel){
		return this.dbUserDao.selectByIpAndUsername(dbUserModel);
	}
	
	@Override
	public void insert(DbUserModel dbUserModel){
		dbUserModel = setConnectionsParams(dbUserModel);
		super.insert(dbUserModel);
	}
	@Override
	public void update(DbUserModel dbUserModel) {
		dbUserModel = setConnectionsParams(dbUserModel);
		super.update(dbUserModel);
	}
	private DbUserModel setConnectionsParams(DbUserModel dbUserModel){
		int maxConcurrency = dbUserModel.getMaxConcurrency();
		dbUserModel.setMaxUserConnections(maxConcurrency);
		dbUserModel.setMaxConnectionsPerHour(maxConcurrency*2*60*60);
		dbUserModel.setMaxQueriesPerHour(maxConcurrency*2*60*60);
		dbUserModel.setMaxUpdatesPerHour(maxConcurrency*60*60);
		return dbUserModel;
	}
	public void insertDbUserAndAcceptIp(DbUserModel dbUserModel){
		String[] ips = dbUserModel.getAcceptIp().split(",");		
		for (String ip : ips) {
			dbUserModel.setAcceptIp(ip);
			this.dbUserDao.insert(dbUserModel);
		}
	}
	/**
	 * Methods Name: updateDbUser <br>
	 * Description: 修改dbUser信息<br>
	 * @author name: wujun
	 * @param dbUserModel
	 */
	public void updateDbUser(DbUserModel dbUserModel){
		this.dbUserDao.update(dbUserModel);
	}
	/**
	 * Methods Name: deleteDbUser <br>
	 * Description: 删除dbUser信息<br>
	 * @author name: wujun
	 * @param dbUserModel
	 */
	public void deleteDbUser(String dbUserId){
		String[] ids = dbUserId.split(",");
		for (String id : ids) {
			DbUserModel dbUserModel = new DbUserModel();
			dbUserModel.setId(Long.parseLong(id));
			this.dbUserDao.delete(dbUserModel);
		}	
	}

	@Override
	public void deleteByDbId(Long dbId) {
		this.dbUserDao.deleteByDbId(dbId);
		
	}
	/**
	 * Methods Name: buildDbUser <br>
	 * Description: 审批DbUser<br>
	 * @author name: wujun
	 * @param dbUserId
	 */
	public void buildDbUser(String dbUserId){
		DbUserModel dbUserModel = new DbUserModel();
		dbUserModel.setId(Long.parseLong(dbUserId));
		this.dbUserDao.update(dbUserModel);
	}

	@Override
	public List<DbUserModel> selectGroupByName(Map<String, Object> params) {
		return this.dbUserDao.selectGroupByName(params);
	}
	
	@Override
	public List<String> selectIpsFromUser(Long dbId) {
		List<DbUserModel> dbUsers = this.selectIpsByDbIdAndUsername(dbId, DEFAULT_DB_RO_NAME);
		List<String> ips = new ArrayList<String>();
		for (DbUserModel dbUser : dbUsers) {
			ips.add(dbUser.getAcceptIp());
		}
		return ips;
	}

	@Override
	public List<Map<String,Object>> selectMarkIps4dbUser(Long dbId,String username) {
		List<String> all =  this.selectIpsFromUser(dbId);
		
		List<Map<String,Object>> selected = new ArrayList<Map<String,Object>>();
		
		if(!StringUtils.isNullOrEmpty(username)) {
			List<DbUserModel> dbUsers = this.selectByDbIdAndUsername(dbId, username);
			for (DbUserModel dbUser : dbUsers) {
				Map<String,Object> data = new HashMap<String,Object>();
				String ip = dbUser.getAcceptIp();
				data.put("addr",ip);
				data.put("type", dbUser.getType());
				data.put("used", 1);
				selected.add(data);
				
				if(all.contains(ip)) {
					all.remove(ip);
				}
			}
		}
		for (String ip : all) { //未使用ip
			Map<String,Object> data = new HashMap<String,Object>();
			data.put("addr",ip);
			data.put("used", 0);
			selected.add(data);
		}
		return selected;
	}

	@Override
	public List<DbUserModel> selectByDbIdAndUsername(Long dbId, String username) {
		Map<String, Object> params = new HashMap<String,Object>();
		params.put("dbId", dbId);
		params.put("username", username);
		List<DbUserModel> dbUsers = this.selectByMap(params);
		return dbUsers;
	}
	
	private List<DbUserModel> selectIpsByDbIdAndUsername(Long dbId, String name4Ip) {
		Map<String, Object> params = new HashMap<String,Object>();
		params.put("dbId", dbId);
		params.put("name4Ip",DEFAULT_DB_RO_NAME);
		List<DbUserModel> dbUsers = this.selectByMap(params);
		return dbUsers;
	}
	
	@Override
	public void createDefalutAdmin(Long dbId){
		DbUserModel dbUserModel = new DbUserModel();
		dbUserModel.setDbId(dbId);
		dbUserModel.setUsername("admin");
		dbUserModel.setPassword(PasswordRandom.genStr());
		dbUserModel.setAcceptIp("%");
     	dbUserModel.setType(DbUserRoleStatus.MANAGER.getValue());
		dbUserModel.setMaxConcurrency(50);
		dbUserModel.setReadWriterRate("2:1");
		dbUserModel.setCreateUser(sessionService.getSession().getUserId());
		this.insert(dbUserModel);
	}

	@Override
	public void updateDescnByUsername(DbUserModel dbUserModel) {
		this.dbUserDao.updateDescnByUsername(dbUserModel);
		
	}

	@Override
	public boolean isLegalDbUserName(String dbUserName, Long dbId) {
		return StringUtils.isNullOrEmpty(dbUserName) || isExistDbName(dbUserName, dbId) || isKeyword(dbUserName) ? false : true;
	}
	
	/*
	 * 判断用户是否与数据库名称相同
	 */
	private boolean isExistDbName(String dbUserName, Long dbId) {
		DbModel dbModel = dbService.selectById(dbId);
		return null!=dbModel && dbUserName.equals(dbModel.getDbName())  ? true : false;
	}
	
	/*
	 * 是否属于数据库用户关键字
	 */
	private boolean isKeyword(String dbUserName) {
		if(StringUtils.isNullOrEmpty(dbUserKeywords))
			return false;
		
		String[] keywords = dbUserKeywords.split(",");
		List<String> keywordList = Arrays.asList(keywords);
		if(keywordList.contains(dbUserName))
			return true;
		return false;
	}
	
	public boolean checkIp(Long dbId, String ip) {
		Map<String, Object> params = new HashMap<String,Object>();
		params.put("dbId", dbId);
		params.put("acceptIp", ip);
		List<DbUserModel> realUsers = this.selectByMap(params);
		if(realUsers.isEmpty()) {
			return false;
		}
		return true;
	}

	@Override
	public void updateUserByDbId(Long mclusterId, Long userId) {
		this.dbUserDao.updateUserByDbId(mclusterId, userId);
	}
}
