package com.letv.portal.service.product.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.letv.common.dao.IBaseDao;
import com.letv.portal.dao.base.IBasePriceDao;
import com.letv.portal.dao.base.IBaseStandardDao;
import com.letv.portal.dao.product.IProductDao;
import com.letv.portal.dao.product.IProductElementDao;
import com.letv.portal.dao.product.IProductRegionDao;
import com.letv.portal.model.base.BaseStandard;
import com.letv.portal.model.product.Product;
import com.letv.portal.model.product.ProductElement;
import com.letv.portal.model.product.ProductRegion;
import com.letv.portal.service.calculate.ICalculateService;
import com.letv.portal.service.impl.BaseServiceImpl;
import com.letv.portal.service.product.IProductService;

@Service("productService")
public class ProductServiceImpl extends BaseServiceImpl<Product> implements IProductService {
	
	private final static Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);
	
	public ProductServiceImpl() {
		super(Product.class);
	}

	@Autowired
	private IProductDao productDao;
	@Autowired
	private IProductElementDao productElementDao;
	@Autowired
	private IBaseStandardDao baseStandardDao;
	@Autowired
	private IBasePriceDao basePriceDao;
	@Autowired
	private IProductRegionDao productRegionDao;
	@Autowired
	private ICalculateService calculateService;

	@Override
	public IBaseDao<Product> getDao() {
		return this.productDao;
	}

	@Override
	public Map<String, Object> queryProductDetailById(Long id) {
		Map<String, Object> ret = new HashMap<String, Object>();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("productId", id);
		//获取地域信息
		getProductRegion(ret, params);
		//获取产品元素
		getProductElement(ret, params);
		return ret;
	}
	
	/**
	  * @Title: getProductElement
	  * @Description: 根据产品元素获取各元素具体规格
	  * @param ret
	  * @param params void   
	  * @throws 
	  * @author lisuxiao
	  * @date 2015年9月1日 上午9:45:50
	  */
	private void getProductElement(Map<String, Object> ret, Map<String, Object> params) {
		List<ProductElement> productElements = this.productElementDao.selectByMap(params);
		for (ProductElement productElement : productElements) {
			getChildren(ret, productElement.getBaseElementId(), 0l);
		}
	}
	
	/**
	  * @Title: getChildren
	  * @Description: 获取该规格下所有子规格
	  * @param ret
	  * @param baseStandardId
	  * @param fatherId void   
	  * @throws 
	  * @author lisuxiao
	  * @date 2015年9月1日 上午9:46:18
	  */
	@SuppressWarnings("unchecked")
	private void getChildren(Map<String, Object> ret, Long baseStandardId, Long fatherId) {
		Map<String, Object> elements = new HashMap<String, Object>();
		elements.put("baseElementId", baseStandardId);
		elements.put("fatherId", fatherId);
		
		List<BaseStandard> baseStandards = this.baseStandardDao.selectByMap(elements);
			
		for (BaseStandard map2 : baseStandards) {
			List<Map<String, Object>> arrayList = null;
			if(ret.get(map2.getStandard())==null) {
				arrayList = new ArrayList<Map<String, Object>>();
				ret.put(map2.getStandard(), arrayList);
			} else {
				arrayList = (List<Map<String, Object>>) ret.get(map2.getStandard());
			}
			Map<String, Object> baseStandardMap = new HashMap<String, Object>();
			baseStandardMap.put("text", map2.getType());
			baseStandardMap.put("value", map2.getValue());
			if(!StringUtils.isEmpty(map2.getUnit())) {
				baseStandardMap.put("unit", map2.getUnit());
			}
			arrayList.add(baseStandardMap);
			getChildren(baseStandardMap, baseStandardId, map2.getId());
		}
		
	}
	
	/**
	  * @Title: getProductRegion
	  * @Description: 获取产品的地区信息
	  * @param ret
	  * @param params void   
	  * @throws 
	  * @author lisuxiao
	  * @date 2015年8月31日 下午5:07:29
	  */
	private void getProductRegion(Map<String, Object> ret, Map<String, Object> params) {
		List<ProductRegion> regions = this.productRegionDao.selectByMap(params);
		List<Map<String, Object>> regionsList = new ArrayList<Map<String, Object>>();
		Set<Long> baseRegionIdSet = new HashSet<Long>();
		for (ProductRegion productRegion : regions) {//保存各个地区：北京、上海等
			if(!baseRegionIdSet.contains(productRegion.getBaseRegionId())) {
				baseRegionIdSet.add(productRegion.getBaseRegionId());
				Map<String, Object> regionMap = new HashMap<String, Object>();
				regionMap.put("text", productRegion.getBaseRegion().getName());
				regionMap.put("value", productRegion.getBaseRegionId());
				regionsList.add(regionMap);
			} 
		}
		for(Map<String, Object> regionMap : regionsList) {
			List<Map<String, Object>> areaList = new ArrayList<Map<String, Object>>();
			for (ProductRegion productRegion : regions) {//保存可用区到相应的地区下：酒仙桥、亦庄等
				if(productRegion.getBaseRegionId().equals(regionMap.get("value"))) {
					Map<String, Object> areaMap = new HashMap<String, Object>();
					areaMap.put("text", productRegion.getHcluster().getHclusterNameAlias());
					areaMap.put("value", productRegion.getHclusterId());
					areaList.add(areaMap);
				}
			}
			regionMap.put("area", areaList);
		}
		ret.put("region", regionsList);
	}

	@Override
	public Double queryProductPrice(Long id, Map<String, Object> map) {
		if(validateData(id, map)) {
			return calculateService.calculatePrice(id, map);
		}
		return null;
	}
	
	/**
	  * @Title: validateData
	  * @Description: 验证产品元素值是否合法
	  * @param id
	  * @param map
	  * @return boolean   
	  * @throws 
	  * @author lisuxiao
	  * @date 2015年9月1日 下午4:23:04
	  */
	@Override
	public boolean validateData(Long id, Map<String, Object> map) {
		//**********验证产品在该地域是否存在start******************
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("productId", id);
		params.put("baseRegionId", map.get("region")==null?-1:map.get("region"));
		params.put("hclusterId", map.get("area")==null?-1:map.get("area"));
		List<ProductRegion> regions = this.productRegionDao.selectByMap(params);
		if(regions==null || regions.size()==0) {
			logger.info("validateData, this product not belongs to the region! region : "+map.get("region")+", area : "+map.get("area"));
			return false;
		}
		//**********验证产品在该地域是否存在end******************
		
		params.clear();
		params.put("productId", id);
		List<ProductElement> productElements = this.productElementDao.selectByMap(params);
		Map<String, Set<String>> standards = new HashMap<String, Set<String>>();
		for (ProductElement productElement : productElements) {
			params.clear();
			params.put("baseElementId", productElement.getBaseElementId());
			List<BaseStandard> baseStandards = this.baseStandardDao.selectByMap(params);
			for (BaseStandard baseStandard : baseStandards) {
				Set<String> set = null;
				if(standards.containsKey(baseStandard.getStandard())) {
					set = standards.get(baseStandard.getStandard());
				} else {
					set = new HashSet<String>();
				}
				set.add(baseStandard.getValue());
				standards.put(baseStandard.getStandard(), set);
			}
		}
		for (String standard : standards.keySet()) {
			List<Integer> chargeType = this.basePriceDao.selectChargeTypeByStandard(standard);
			if(chargeType!=null && chargeType.size()>0 && chargeType.get(0)==0) {//基础定价
				if(map.get(standard)==null || !standards.get(standard).contains(map.get(standard))) {
					logger.info("validateData, map "+standard+" standard is :"+map.get(standard)+", standard not within array");
					return false;
				}
			} else if(chargeType!=null && chargeType.size()>0 && chargeType.get(0)==1 || chargeType!=null && chargeType.size()>0 && chargeType.get(0)==2) {//阶梯定价/线性定价
				Iterator<String> it = standards.get(standard).iterator();
				if(map.get(standard)==null || Double.parseDouble((String)map.get(standard))<0 
						|| Double.parseDouble((String)map.get(standard))>Double.parseDouble(it.next())) {
					logger.info("validateData, map "+standard+" standard is :"+map.get(standard)+", standard not within range");
					return false;
				}
			}
		}
		
		//**********验证购买产品数量和时长是否在规定范围内start******************
		Iterator<String> it = standards.get("order_num").iterator();
		if(map.get("order_num")==null || Double.parseDouble((String)map.get("order_num"))<0 
				|| Double.parseDouble((String)map.get("order_num"))>Double.parseDouble(it.next())) {
			logger.info("validateData, map order_num standard is :"+map.get("order_num")+", standard not within range");
			return false;
		}
		if(map.get("order_time")==null || Double.parseDouble((String)map.get("order_time"))<0 
				|| (Double.parseDouble((String)map.get("order_time"))>9 && Double.parseDouble((String)map.get("order_time"))!=12 && 
				Double.parseDouble((String)map.get("order_time"))!=24 && Double.parseDouble((String)map.get("order_time"))!=36)) {
			logger.info("validateData, map order_time standard is :"+map.get("order_time")+", standard not within range");
			return false;
		} else {
			if(Double.parseDouble((String)map.get("order_time"))==12) {//一年按10个月
				map.put("order_time", "10");
			} else if(Double.parseDouble((String)map.get("order_time"))==24) {//2年按20个月
				map.put("order_time", "20");
			} else if(Double.parseDouble((String)map.get("order_time"))==36) {//3年按30个月
				map.put("order_time", "30");
			}
		}
		//**********验证购买产品数量和时长是否在规定范围内end******************
		return true;
	}


}