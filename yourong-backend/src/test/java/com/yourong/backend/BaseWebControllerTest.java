package com.yourong.backend;

import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring-core.xml","classpath:spring-mvc.xml","classpath:spring-mybatis.xml","classpath:timetask.xml"})
@WebAppConfiguration
public abstract class BaseWebControllerTest {
    @Autowired
	 protected WebApplicationContext wac;

	 protected MockMvc mockMvc;

	  @Before
	  public void setup() {
	    this.mockMvc = webAppContextSetup(this.wac).build();
	  }

    
  
 
   
    
    
    
}
