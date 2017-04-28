package com.yourong.web.controller;

import com.yourong.web.BaseWebControllerTest;
import org.junit.Test;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

/**
 * Created by peng.yong on 2016/1/19.
 */
public class LandingControllerTest  extends BaseWebControllerTest {
    @Test
    public void testlandingquintupleGiftgainlist() throws Exception {
        //设置当前用户到session
        setLoginedSession(13800138000L);
        this.mockMvc.perform(post("/landing/quintupleGift/gain/list").session(session)).andDo(MockMvcResultHandlers.print()).andReturn();
    }
}
