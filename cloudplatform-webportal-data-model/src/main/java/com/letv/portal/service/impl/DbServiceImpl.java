package com.letv.portal.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.letv.common.dao.IBaseDao;
import com.letv.common.dao.QueryParam;
import com.letv.common.email.SimpleTextEmailSender;
import com.letv.common.exception.ValidateException;
import com.letv.common.paging.impl.Page;
import com.letv.common.session.SessionServiceImpl;
import com.letv.portal.dao.IContainerDao;
import com.letv.portal.dao.IDbDao;
import com.letv.portal.dao.IIpResourceDao;
import com.letv.portal.model.ContainerModel;
import com.letv.portal.model.DbModel;
import com.letv.portal.model.MclusterModel;
import com.letv.portal.service.IContainerService;
import com.letv.portal.service.IDbService;
import com.letv.portal.service.IDbUserService;
import com.letv.portal.service.IMclusterService;

@Service("dbService")
public class DbServiceImpl extends BaseServiceImpl<DbModel> implements
		IDbService {

	private final static Logger logger = LoggerFactory
			.getLogger(DbServiceImpl.class);

	@Resource
	private IDbDao dbDao;
	@Resource
	private IIpResourceDao ipResourceDao;
	@Resource
	private SimpleTextEmailSender simpleTextEmailSender;

	@Autowired(required = false)
	private SessionServiceImpl sessionService;
	@Autowired
	private IDbUserService dbUserService;
	@Autowired
	private IContainerService containerService;
	@Autowired
	private IMclusterService mclusterService;

	@Resource
	private IContainerDao containerDao;

	public DbServiceImpl() {
		super(DbModel.class);
	}

	@Override
	public IBaseDao<DbModel> getDao() {
		return this.dbDao;
	}

	@Override
	public Page findPagebyParams(Map<String, Object> params, Page page) {
		QueryParam param = new QueryParam(params, page);
		page.setData(this.dbDao.selectPageByMap(param));
		page.setTotalRecords(this.dbDao.selectByMapCount(params));
		return page;

	}

	@Override
	public Map<String, Object> selectCreateParams(Long dbId, boolean isVip) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", dbId);
		if (isVip) {
			params.put("type", "mclustervip");
		} else {
			params.put("zookeeperId", 1);
		}
		return this.dbDao.selectCreateParams(params);
	}

	@Override
	public List<DbModel> selectByDbNameForValidate(String dbName,
			Long createUser) {
		if (StringUtils.isEmpty(dbName) || null == createUser)
			throw new ValidateException("参数不合法");
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("dbName", dbName);
		params.put("createUser", createUser);
		return this.dbDao.selectByDbNameForValidate(params);
	}

	@Override
	public void deleteByMclusterId(Long mclusterId) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("mclusterId", mclusterId);
		List<DbModel> dbs = this.dbDao.selectByMap(map);
		for (DbModel dbModel : dbs) {
			this.delete(dbModel);
			this.dbUserService.deleteByDbId(dbModel.getId());
		}
	}

	@Override
	public DbModel dbList(Long dbId) {
		if (dbId == null)
			throw new ValidateException("参数不合法");
		DbModel db = this.selectById(dbId);
		if (db == null)
			throw new ValidateException("参数不合法，相关数据不存在");
		db.setContainers(this.containerDao.selectByMclusterId(db
				.getMclusterId()));
		return db;
	}

	@Override
	public List<DbModel> selectDbByMclusterId(Long mclusterId) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("mclusterId", mclusterId);
		List<DbModel> dbs = this.dbDao.selectByMap(map);
		return dbs;
	}

	@Override
	public Map<String, Object> getGbaConfig(Long dbId) {
		if (dbId == null)
			throw new ValidateException("参数不合法");

		DbModel db = this.selectById(dbId);
		if (db == null)
			throw new ValidateException("参数不合法，相关数据不存在");

		Map<String, Object> glbParams = new HashMap<String, Object>();

		List<ContainerModel> containers = this.containerService
				.selectByMclusterId(db.getMclusterId());
		MclusterModel mcluster = this.mclusterService.selectById(db
				.getMclusterId());
		List<String> urlPorts = new ArrayList<String>();
		for (ContainerModel container : containers) {
			if ("mclusternode".equals(container.getType()) || "mclusteraddnode".equals(container.getType())) {
				urlPorts.add(container.getIpAddr() + ":3306");
			}
		}

		glbParams.put("User", "monitor");
		glbParams.put("Pass", mcluster.getSstPwd());
		glbParams.put("Addr", "127.0.0.1");
		glbParams.put("Port", "3306");
		glbParams.put("Backend", urlPorts);

		return glbParams;
	}

	@Override
	public Integer selectCountByStatus(Integer _parameter) {
		return this.dbDao.selectCountByStatus(_parameter);
	}

	@Override
	public List<DbModel> selectPendingAuditDb() {
		return this.dbDao.selectPendingAuditDb();
	}

	@Override
	public Page getPagePendingAuditDb(Page page,
			Map<String, Object> params) {
		QueryParam param = new QueryParam();
		param.setPage(page);
		param.setParams(params);
		page.setData(this.dbDao.getPagePendingAuditDb(param));
		page.setTotalRecords(this.dbDao.getPagePendingAuditDbCount(param));
		return page;
	}
}
