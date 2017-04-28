package com.yourong.web.controller;

import com.yourong.web.BaseWebControllerTest;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.junit.Test;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;

/**
 * Created by peng.yong on 2015/12/9.
 */
public class LoginControllerTest extends BaseWebControllerTest {


    //@Test
    public void test() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/security/lostpwdsubmit")).andDo(MockMvcResultHandlers.print()).andReturn();
    }



    //@Test
    public void testMemberCheck() throws Exception {
        //设置当前用户到session
        setLoginedSession(13800138000L);
        this.mockMvc.perform(get("/member/check").session(session)).andDo(MockMvcResultHandlers.print()).andReturn();
    }

    @Test
    public void testBeanDynamicMethod() throws  Exception{
        //Object byMobile = MethodUtils.invokeExactMethod(memberService, "selectByMobile", 13800138000L);
        System.out.println("-----------------------------");
        //System.out.println(byMobile);
    }

    @Test
    public void testToUnderwriteRepay() throws Exception {
        //设置当前用户到session
        setLoginedSession(13625252525L);
        this.mockMvc.perform(post("/myBorrow/toUnderwriteRepay").param("projectId", "989800470").session(session)).andDo(MockMvcResultHandlers.print()).andReturn();
    }



}
