package com.yourong.api;
import com.yourong.api.dto.ResultDTO;

import org.junit.Test;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

/**
 * Created by peng.yong on 2015/12/8.
 */
public class IndexControllerTest extends  BaseWebControllerTest {
   @Test
    public void testIndex() throws  Exception{
       ResultDTO  dto  = new ResultDTO();

        mockMvc.perform(get("/getVersion")).andExpect(status().isOk()).andDo(MockMvcResultHandlers.print()).andReturn();




    }

	@Test
    public void demo1()throws Exception{
		mockMvc.perform(get("/project/queryProjectPackageList")
                .param("status", "2")).andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print()).andReturn();
    }
	@Test
    public void demo2()throws Exception{
		mockMvc.perform(get("/project/queryProjectPackageDetailList")
                .param("projectPackageId", "21")
                .param("status", "1")).andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print()).andReturn();
    }
}
