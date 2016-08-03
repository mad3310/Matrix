package com.letv.portal.service.cloudes;

import java.beans.PropertyEditor;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import com.letv.common.paging.impl.Page;
import com.letv.common.result.ResultObject;
import com.letv.portal.controller.cloudes.EsServerController;
import com.letv.portal.enumeration.EsStatus;
import com.letv.portal.junitBase.AbstractTest;
import com.letv.portal.model.es.EsServer;

public class EsServerServiceTest extends AbstractTest{

	@Autowired
	private EsServerController esServerController;
	
	private final static Logger logger = LoggerFactory.getLogger(
			EsServerServiceTest.class);
	
	private int totalSize = 0;
	
	@Before
    public void testEsServerListBefore() {
    	request.setAttribute("currentPage", 1);
    	request.setAttribute("recordsPerPage", 15);
    	ResultObject ro = esServerController.list(new Page(), request, new ResultObject());
    	totalSize = ((Page)ro.getData()).getTotalRecords();
    	logger.info("查询获取totalSize：{}", totalSize);
    }
    
    @Test
    public void testInsertOneEs() throws InterruptedException {
    	BindingResult br = new BindingResult() {
			@Override
			public void setNestedPath(String nestedPath) {
			}
			@Override
			public void rejectValue(String field, String errorCode, Object[] errorArgs,
					String defaultMessage) {
			}
			@Override
			public void rejectValue(String field, String errorCode,
					String defaultMessage) {
			}
			@Override
			public void rejectValue(String field, String errorCode) {
			}
			@Override
			public void reject(String errorCode, Object[] errorArgs,
					String defaultMessage) {
			}
			@Override
			public void reject(String errorCode, String defaultMessage) {
			}
			@Override
			public void reject(String errorCode) {
			}
			@Override
			public void pushNestedPath(String subPath) {
			}
			@Override
			public void popNestedPath() throws IllegalStateException {
			}
			@Override
			public boolean hasGlobalErrors() {
				return false;
			}
			@Override
			public boolean hasFieldErrors(String field) {
				return false;
			}
			@Override
			public boolean hasFieldErrors() {
				return false;
			}
			@Override
			public boolean hasErrors() {
				return false;
			}
			@Override
			public String getObjectName() {
				return null;
			}
			@Override
			public String getNestedPath() {
				return null;
			}
			@Override
			public List<ObjectError> getGlobalErrors() {
				return null;
			}
			@Override
			public int getGlobalErrorCount() {
				return 0;
			}
			@Override
			public ObjectError getGlobalError() {
				return null;
			}
			@Override
			public Object getFieldValue(String field) {
				return null;
			}
			@Override
			public Class<?> getFieldType(String field) {
				return null;
			}
			@Override
			public List<FieldError> getFieldErrors(String field) {
				return null;
			}
			@Override
			public List<FieldError> getFieldErrors() {
				return null;
			}
			@Override
			public int getFieldErrorCount(String field) {
				return 0;
			}
			@Override
			public int getFieldErrorCount() {
				return 0;
			}
			@Override
			public FieldError getFieldError(String field) {
				return null;
			}
			@Override
			public FieldError getFieldError() {
				return null;
			}
			@Override
			public int getErrorCount() {
				return 0;
			}
			@Override
			public List<ObjectError> getAllErrors() {
				return null;
			}
			@Override
			public void addAllErrors(Errors errors) {
			}
			@Override
			public String[] resolveMessageCodes(String errorCode, String field) {
				return null;
			}
			@Override
			public void recordSuppressedField(String field) {
			}
			@Override
			public Object getTarget() {
				return null;
			}
			@Override
			public String[] getSuppressedFields() {
				return null;
			}
			@Override
			public Object getRawFieldValue(String field) {
				return null;
			}
			@Override
			public PropertyEditorRegistry getPropertyEditorRegistry() {
				return null;
			}
			@Override
			public Map<String, Object> getModel() {
				return null;
			}
			@Override
			public PropertyEditor findEditor(String field, Class<?> valueType) {
				return null;
			}
			@Override
			public void addError(ObjectError error) {
			}
		};
    	EsServer server = new EsServer();
    	server.setEsName("junitTest"+System.currentTimeMillis());
    	server.setMemorySize(1073741824l);
    	server.setNodeCount(3);
    	server.setStorageSize(10737418240l);
    	server.setHclusterId(48l);
    	ResultObject result = esServerController.save(server, br, new ResultObject());
    	Assert.assertEquals(1, result.getResult());
    	EsServer esNewInfo = null;
    	int time = 0;
    	do {
    		ResultObject esInfo = esServerController.detail((Long) result.getData());
    		esNewInfo = (EsServer)esInfo.getData();
    		Thread.sleep(2000l);
    		time ++;
    		logger.info("es创建中...time:{}, status:{}", time, esNewInfo.getStatus());
		} while (time<=150 && !(esNewInfo.getStatus()==EsStatus.NORMAL ||
				esNewInfo.getStatus()==EsStatus.BUILDFAIL));
    	logger.info("创建成功或超时");
    	Assert.assertEquals(EsStatus.NORMAL, esNewInfo.getStatus());
    }
    
    @After
    public void testEsServerListAfter() {
    	request.setAttribute("currentPage", 1);
    	request.setAttribute("recordsPerPage", 15);
    	ResultObject ro = esServerController.list(new Page(), request, new ResultObject());
    	Assert.assertEquals(totalSize+1, ((Page)ro.getData()).getTotalRecords());
    }

}
