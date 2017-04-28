package com.yourong.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Date;

import org.junit.Test;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import com.yourong.common.util.DateUtils;

public class ProjectPackageTest extends BaseWebControllerTest{

	@Test
    public void Demo()throws Exception{
        mockMvc.perform(post("/project/queryProjectPackageList")
                .header("Accept-Version", "2.1.0")
                .header("Encrypt-Version", "2.1.0")
                .param("status", "2").param("requestTime", DateUtils.getDateStrFromDate(new Date()))
                .param("sign", "e8qdwl9caset5zugii2r7q0k8ikopxor")
                  )
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print()).andReturn();
    }
}
