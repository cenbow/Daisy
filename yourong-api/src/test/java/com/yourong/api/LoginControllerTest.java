package com.yourong.api;

import com.yourong.api.dto.LoginDto;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;

/**
 * 登陆测试
 * Created by peng.yong on 2015/12/14.
 */
public class LoginControllerTest extends BaseWebControllerTest {
    @Test
    public void testLogin()throws Exception{
        mockMvc.perform(post("/logining")
                .header("Accept-Version", "1.0.0")
                .header("Encrypt-Version", "1.0.0")
                .param("username", "别忘我是谁")
                .param("password", "1qaz2wsx")
                .param("loginSource", "1")
                .param("equipment", "1111")
                .param("device", "1111")
                .param("position", "123,67")
                .param("channelId", "1234")
                .param("sign", "121231231"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andDo(MockMvcResultHandlers.print()).andReturn();
    }

    @Test
    public void dynamicInvokeTest()throws Exception{
        mockMvc.perform(post("/activity/dynamicInvoke")
                        .header("Accept-Version", "1.0.0")
                        .header("Encrypt-Version", "1.0.0")
                        .param("method", "anniversaryGetRed")
                .param("args_2_string_mobile", "13564622214")
                .param("args_1_string_param", "bTywmPEIC+7DYf4OKwwXGHwtaCIxxKskkgdc8P9IZk9YMvxqmJZdIOLcZSg2mz4ng1Fzzz+JN0Ych7FXqteYiw==")

                .param("equipment", "1111")
                .param("device", "1111")
                .param("token", "ndSkQdIUUcW8y5FwJi6pBjAVDW/xEvRgpEQlWDnpEocrz1SncZoMSkHrOeGRaXOfIaxSPC+00Q4kGRwXf8+wMA==")
                .param("position", "123,67")
                .param("channelId", "1234")
                .param("sign", "121231231"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andDo(MockMvcResultHandlers.print()).andReturn();
    }
    @Test
    public void dynamicInvokeTest1()throws Exception{
        mockMvc.perform(post("/activity/dynamicInvoke")
                .header("Accept-Version", "1.0.0")
                .header("Encrypt-Version", "1.0.0")
                .param("method", "anniversaryGetRed1")
                .param("args_2_string_mobile", "13564622214")
                .param("args_1_long_param", "123123")
                .param("args_3_string_mobile", "xddddd")
                .param("equipment", "1111")
                .param("device", "1111")
                .param("token", "ndSkQdIUUcW8y5FwJi6pBjAVDW/xEvRgpEQlWDnpEocrz1SncZoMSkHrOeGRaXOfIaxSPC+00Q4kGRwXf8+wMA==")
                .param("position", "123,67")
                .param("channelId", "1234")
                .param("sign", "121231231"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andDo(MockMvcResultHandlers.print()).andReturn();
    }
    @Test
    public void dynamicInvokeTest2()throws Exception{
        mockMvc.perform(post("/activity/dynamicInvoke")
                .header("Accept-Version", "1.0.0")
                .header("Encrypt-Version", "1.0.0")
                .param("method", "anniversaryGetRed2")
                .param("args_2_string_mobile", "13564622214")
                .param("args_1_string_param", "bTywmPEIC+7DYf4OKwwXGHwtaCIxxKskkgdc8P9IZk9YMvxqmJZdIOLcZSg2mz4ng1Fzzz+JN0Ych7FXqteYiw==")
                .param("args_3_long_param", "11111")
                .param("equipment", "1111")
                .param("device", "1111")
                .param("token", "ndSkQdIUUcW8y5FwJi6pBjAVDW/xEvRgpEQlWDnpEocrz1SncZoMSkHrOeGRaXOfIaxSPC+00Q4kGRwXf8+wMA==")
                .param("position", "123,67")
                .param("channelId", "1234")
                .param("sign", "121231231"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andDo(MockMvcResultHandlers.print()).andReturn();
    }
    @Test
    public void dynamicInvokeTest3()throws Exception{
        mockMvc.perform(post("/activity/dynamicInvoke")
                .header("Accept-Version", "1.0.0")
                .header("Encrypt-Version", "1.0.0")
                .param("method", "anniversaryGetRed3")
                .param("args_2_long_mobile", "13564622214")
                .param("args_1_string_param", "bTywmPEIC+7DYf4OKwwXGHwtaCIxxKskkgdc8P9IZk9YMvxqmJZdIOLcZSg2mz4ng1Fzzz+JN0Ych7FXqteYiw==")
                .param("args_3_string_param", "bTywmPEIC+7DYf4OKwwXGHwtaCIxxKskkgdc8P9IZk9YMvxqmJZdIOLcZSg2mz4ng1Fzzz+JN0Ych7FXqteYiw==")
                .param("equipment", "1111")
                .param("device", "1111")
                .param("token", "ndSkQdIUUcW8y5FwJi6pBjAVDW/xEvRgpEQlWDnpEocrz1SncZoMSkHrOeGRaXOfIaxSPC+00Q4kGRwXf8+wMA==")
                .param("position", "123,67")
                .param("channelId", "1234")
                .param("sign", "121231231"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andDo(MockMvcResultHandlers.print()).andReturn();
    }
}
