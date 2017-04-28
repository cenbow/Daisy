package com.yourong.web.authority;

import com.yourong.common.constant.Constant;
import com.yourong.common.util.StringUtil;
import com.yourong.core.sys.model.SysDict;
import com.yourong.web.utils.ServletUtil;
import com.yourong.web.utils.SysServiceUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 拦截所有跟新浪有关的业务操作
 * Created by py on 2015/5/21.
 */
public class ThirdPayHandleInterceptor  extends HandlerInterceptorAdapter {



    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        SysDict sysDictByKey = SysServiceUtils.getSysDictByKey(Constant.IS_REDIRECT, Constant.IS_HANDLE_SINA_PAY);
        if(sysDictByKey == null){
            return true;
        }
        String key = sysDictByKey.getValue();
        String url = sysDictByKey.getRemarks();
        if (StringUtil.isNotBlank(key) && key.equalsIgnoreCase("Y")) {
            response.sendRedirect(request.getContextPath() + url);
            return false;
        } else {
            return true;
        }
    }

}
