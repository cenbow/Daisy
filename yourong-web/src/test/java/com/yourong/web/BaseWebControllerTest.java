package com.yourong.web;
import com.yourong.common.constant.Constant;
import com.yourong.common.util.BeanCopyUtil;
import com.yourong.core.uc.model.Member;
import com.yourong.web.dto.MemberSessionDto;
import com.yourong.web.service.MemberService;
import com.yourong.web.service.TransactionService;
import org.junit.Before;
import org.junit.Test;  
import org.junit.runner.RunWith; 
import org.springframework.beans.factory.annotation.Autowired;  
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;  
import org.springframework.test.context.web.WebAppConfiguration;  
import org.springframework.test.web.servlet.MockMvc;  
import org.springframework.test.web.servlet.ResultHandler;  
import org.springframework.test.web.servlet.ResultMatcher;  
import org.springframework.ui.Model;  
import org.springframework.test.context.transaction.TransactionConfiguration;  
import org.springframework.transaction.annotation.Transactional;  
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;  
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;  
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;  
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;  
import org.springframework.web.bind.annotation.RequestMapping;  
import org.springframework.web.bind.annotation.RequestMethod;  
import org.springframework.web.context.WebApplicationContext;

/**
 * web 端单元测试，已经屏蔽掉了xtoken，onceToken 效验
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring-core.xml","classpath:spring-mvc.xml","classpath:spring-mybatis.xml"})
@WebAppConfiguration
public abstract class BaseWebControllerTest {
	 @Autowired
	 protected WebApplicationContext wac;
    
	 protected MockMvc mockMvc;

	@Autowired
	protected MockHttpSession session;

	@Autowired
	protected MemberService memberService;

	@Autowired
	protected TransactionService transactionService;


	@Before
	  public void setup() {
	    this.mockMvc = webAppContextSetup(this.wac).build();
	  }

	/**
	 * 设置 登陆用户的状态的
	 * @param mobile 手机号码
	 */
     public void setLoginedSession(Long mobile){
		 Member member = memberService.selectByMobile(mobile);
		 int loginSource =1;
		 String userAgent = "单元测试";
		 MemberSessionDto dto = BeanCopyUtil.map(member, MemberSessionDto.class);
		 dto.setLoginSource(loginSource);
		 dto.setUserAgent(userAgent);
		 dto.setIsMemberInvested(transactionService.hasTransactionByMember(member.getId()));
		 session.setAttribute(Constant.CURRENT_USER, dto);
	 }
    
}
