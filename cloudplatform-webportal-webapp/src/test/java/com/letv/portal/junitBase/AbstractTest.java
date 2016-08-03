package com.letv.portal.junitBase;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.letv.common.session.Executable;
import com.letv.common.session.Session;
import com.letv.common.session.SessionServiceImpl;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:data-applicationContext.xml"})
public abstract class AbstractTest{
	
	@Autowired(required=false)
	private SessionServiceImpl sessionService;
	
	protected MockHttpServletRequest request;
	
	@Before   
	public void setup() throws  Exception {
		Session s = new Session();
		s.setUserId(8l);
		sessionService.runWithSession(s, "Usersession changed", new Executable<Session>(){
			@Override
			public Session execute() throws Throwable {
				return null;
			}
		});
		request =  new MockHttpServletRequest();
	}
    
}