package com.letv.portal.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.letv.common.dao.IBaseDao;
import com.letv.common.dao.QueryParam;
import com.letv.common.exception.ValidateException;
import com.letv.common.paging.impl.Page;
import com.letv.portal.dao.IMclusterDao;
import com.letv.portal.enumeration.DbStatus;
import com.letv.portal.model.DbModel;
import com.letv.portal.model.MclusterModel;
import com.letv.portal.service.IBackupService;
import com.letv.portal.service.IBuildService;
import com.letv.portal.service.IContainerService;
import com.letv.portal.service.IDbService;
import com.letv.portal.service.IDbUserService;
import com.letv.portal.service.IMclusterService;

/**Program Name: MclusterServiceImpl <br>
 * Description:  <br>
 * @author name: liuhao1 <br>
 * Written Date: 2014年8月14日 <br>
 * Modified By: <br>
 * Modified Date: <br>
 */
@Service("mclusterService")
public class MclusterServiceImpl extends BaseServiceImpl<MclusterModel> implements
		IMclusterService{
	
	@Autowired
	private IMclusterDao mclusterDao;
	
	@Autowired
	private IContainerService containerService;
	@Autowired
	private IBuildService buildService;
	@Autowired
	private IDbService dbService;
	@Autowired
	private IBackupService backupService;
	@Autowired
	private IDbUserService dbUserService;
	
	public MclusterServiceImpl() {
		super(MclusterModel.class);
	}

	@Override
	public IBaseDao<MclusterModel> getDao() {
		return this.mclusterDao;
	}

	@Override
	public Page findPagebyParams(Map<String, Object> params, Page page) {
		QueryParam param = new QueryParam(params,page);
		page.setData(this.mclusterDao.selectPageByMap(param));
		page.setTotalRecords(this.mclusterDao.selectByMapCount(params));
		return page;
		
	}


	@Override
	public void audit(MclusterModel mclusterModel) {
		this.mclusterDao.updateBySelective(mclusterModel);
	}

	@Override
	public List<MclusterModel> selectByName(String mclusterName) {
		return this.mclusterDao.selectByName(mclusterName);
	}
	
	@Override
	public void delete(MclusterModel mcluster) {
		this.containerService.deleteByMclusterId(mcluster.getId());
		this.buildService.deleteByMclusterId(mcluster.getId());
		this.dbService.deleteByMclusterId(mcluster.getId());
		this.backupService.deleteByMclusterId(mcluster.getId());
		this.mclusterDao.delete(mcluster);
	}

	@Override
	public List<MclusterModel> select4Run() {
		return this.mclusterDao.select4Run();
	}

	@Override
	public boolean isExistByName(String mclusterName) {
		List<MclusterModel> mclusters = this.selectByName(mclusterName);
		return mclusters.size() == 0?true:false;
	}

	@Override
	public List<MclusterModel> selectValidMclustersByMap(Map<String, Object> params) {
		return this.mclusterDao.selectValidMclustersByMap(params);
	}

	@Override
	public List<MclusterModel> selectValidMclusters(int count) {
		if(count == 0)
			return this.mclusterDao.selectValidMclusters();
		Page page = new Page(1,count);
		QueryParam<String,Object> param = new QueryParam<String, Object>();
		param.setPage(page);
		return this.mclusterDao.selectValidMclustersByPage(param);
	}
	@Override
	public List<MclusterModel> selectValidMclusters(int count,Map<String, Object> params) {
		if(params.isEmpty())
			return selectValidMclusters(count);
		if(count == 0)
			return this.mclusterDao.selectValidMclustersByMap(params);
		QueryParam<String,Object> param = new QueryParam<String, Object>();
		param.setParams(params);
		Page page = new Page(1,count);
		param.setPage(page);
		return this.mclusterDao.selectValidMclustersByPage(param);
	}
	@Override
	public List<MclusterModel> selectValidMclusters() {
		return this.mclusterDao.selectValidMclusters();
	}
	
	@Override
	public List<MclusterModel> selectNextValidMclusterById(Long mclusterId,Long hclusterId,
			int addNewCount) {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("mclusterId", mclusterId);
		map.put("hclusterId", hclusterId);
		map.put("addNewCount", addNewCount);
		return this.mclusterDao.selectNextValidMclusterById(map);
	}

	@Override
	public Integer selectValidMclusterCount() {
		return this.mclusterDao.selectValidMclusterCount();
	}

	@Override
	public void updateUser(Long mclusterId, Long userId) {
		//更新mcluster集群表用户
		MclusterModel m = new MclusterModel();
		m.setId(mclusterId);
		m.setCreateUser(userId);
		this.mclusterDao.updateBySelective(m);
		
		//更新container表用户
		containerService.updateUserByMclusterId(mclusterId, userId);
		
		//更新DB表
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("mclusterId", mclusterId);
		List<DbModel> dbModels = dbService.selectByMap(params);
		if(null == dbModels || dbModels.size() != 1) {
			throw new ValidateException("集群主键{}，未获取到db信息");
		}
		DbModel db = new DbModel();
		db.setId(dbModels.get(0).getId());
		db.setCreateUser(userId);
		dbService.updateBySelective(db);
		
		//更新dbUser表
		dbUserService.updateUserByDbId(dbModels.get(0).getId(), userId);
		
	}

}
