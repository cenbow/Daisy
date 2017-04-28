package com.yourong.web.service;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import com.yourong.common.pageable.Page;
import com.yourong.core.ic.model.biz.ProjectForFront;
import com.yourong.core.ic.model.query.ProjectQuery;
import com.yourong.web.BaseWebControllerTest;
import com.yourong.web.dto.ProjectInfoDto;
import com.yourong.web.controller.ActivityController;
import com.yourong.web.controller.ActivityController;

public class ProjectServiceTest extends BaseWebControllerTest{
	
	@Autowired
    public RequestMappingHandlerAdapter handlerAdapter;
	
	@Autowired
	private ProjectService projectService;
	
	@Autowired
    private ActivityController activityController;

	private static MockHttpServletRequest request;

    private static MockHttpServletResponse response;

	@BeforeClass
	public static void before() {
		request = new MockHttpServletRequest();
		request.setCharacterEncoding("UTF-8");
		response = new MockHttpServletResponse();
	}
    
	//controller test
	@Test
	public void testIamKingActivityController(){
	    request.setRequestURI("/iamKing");
        request.setMethod(HttpMethod.GET.name());
        ModelAndView mv = null;
        try {
            mv = handlerAdapter.handle(request, response, new HandlerMethod(activityController, "iamKingAct",HttpServletRequest.class,HttpServletResponse.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(mv);
        Assert.assertEquals(response.getStatus(), 200);
        Assert.assertNotNull(mv);
        Assert.assertEquals(mv.getViewName(), "/activity/iamKing");  
	}
	
	
	/**
	 * 详情页
	 */
	@Test
	public void testGetProjectInfoById(){
		Long id = 989800020L;
		ProjectInfoDto dto = projectService.getProjectInfoById(id);
		System.out.println(dto.getName());
		
	}
	
	/**
	 * 分页查询
	 */
	@Test
	public void testProjectPage(){
		ProjectQuery page = new ProjectQuery();
		Map<String, Object> obj = new HashMap<String, Object>();
		//Page<ProjectForFront> p = projectService.findProjectListByPage(page);
		//System.out.println("name: "+p.getData().get(1).getName());
		//System.out.println("size: "+p.getData().size());
	}
}
