/* Copyright 2004, 2005, 2006 Acegi Technology Pty Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.yourong.common.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;

import com.yourong.common.util.PropertiesUtil;

import javax.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

/**
 * 保存用户请求
 *
 */
public class SavedRequest implements java.io.Serializable {
    //~ Static fields/initializers =====================================================================================

    protected static final Log logger = LogFactory.getLog(SavedRequest.class);

    //~ Instance fields ================================================================================================

    private ArrayList locales = new ArrayList();
    private Map headers = new TreeMap(String.CASE_INSENSITIVE_ORDER);
    private Map parameters = new TreeMap(String.CASE_INSENSITIVE_ORDER);
    private String contextPath;
    private String method;
    private String pathInfo;
    private String queryString;
    private String requestURI;
    private String requestURL;
    private String scheme;
    private String serverName;
    private String servletPath;
    private int serverPort;

    //~ Constructors ===================================================================================================

    public SavedRequest(HttpServletRequest request) {
        Assert.notNull(request, "Request required");

        // Headers
        Enumeration names = request.getHeaderNames();

        while (names.hasMoreElements()) {
            String name = (String) names.nextElement();
            Enumeration values = request.getHeaders(name);

            while (values.hasMoreElements()) {
                String value = (String) values.nextElement();
                this.addHeader(name, value);
            }
        }

        // Locales
        Enumeration locales = request.getLocales();

        while (locales.hasMoreElements()) {
            Locale locale = (Locale) locales.nextElement();
            this.addLocale(locale);
        }

        // Parameters
        Map parameters = request.getParameterMap();
        Iterator paramNames = parameters.keySet().iterator();

        while (paramNames.hasNext()) {
            String paramName = (String) paramNames.next();
            Object o = parameters.get(paramName);
            if (o instanceof String[]) {
                String[] paramValues = (String[]) o;
                this.addParameter(paramName, paramValues);
            } else {
                if (logger.isWarnEnabled()) {
                    logger.warn("ServletRequest.getParameterMap() returned non-String array");
                }
            }
        }

        // Primitives
        this.method = request.getMethod();
        this.pathInfo = request.getPathInfo();
        this.queryString = request.getQueryString();
        this.requestURI = request.getRequestURI();
        this.requestURL = request.getRequestURL().toString();
        this.scheme = request.getScheme();
        this.serverName = request.getServerName();
        this.contextPath = request.getContextPath();
        this.servletPath = request.getServletPath();
    }

    //~ Methods ========================================================================================================

    private void addHeader(String name, String value) {
        ArrayList values = (ArrayList) headers.get(name);

        if (values == null) {
            values = new ArrayList();
            headers.put(name, values);
        }

        values.add(value);
    }

    private void addLocale(Locale locale) {
        locales.add(locale);
    }

    private void addParameter(String name, String[] values) {
        parameters.put(name, values);
    }

    /**
     * Determines if the current request matches the <code>SavedRequest</code>. All URL arguments are
     * considered, but <em>not</em> method (POST/GET), cookies, locales, headers or parameters.
     *
     * @param request DOCUMENT ME!
     * @param portResolver DOCUMENT ME!
     * @return DOCUMENT ME!
     */
    public boolean doesRequestMatch(HttpServletRequest request) {
        Assert.notNull(request, "Request required");

        if (!propertyEquals("pathInfo", this.pathInfo, request.getPathInfo())) {
            return false;
        }

        if (!propertyEquals("queryString", this.queryString, request.getQueryString())) {
            return false;
        }

        if (!propertyEquals("requestURI", this.requestURI, request.getRequestURI())) {
            return false;
        }

//        if (!propertyEquals("serverPort", new Integer(this.serverPort), new Integer(portResolver.getServerPort(request))))
//        {
//            return false;
//        }

        if (!propertyEquals("requestURL", this.requestURL, request.getRequestURL().toString())) {
            return false;
        }

        if (!propertyEquals("scheme", this.scheme, request.getScheme())) {
            return false;
        }

        if (!propertyEquals("serverName", this.serverName, request.getServerName())) {
            return false;
        }

        if (!propertyEquals("contextPath", this.contextPath, request.getContextPath())) {
            return false;
        }

        if (!propertyEquals("servletPath", this.servletPath, request.getServletPath())) {
            return false;
        }

        return true;
    }

    public String getContextPath() {
        return contextPath;
    }

    /**
     * Indicates the URL that the user agent used for this request.
     *
     * @return the full URL of this request
     */
    public String getFullRequestUrl() {
        return UrlUtils.getFullRequestUrl(this);
    }

    public Iterator getHeaderNames() {
        return (headers.keySet().iterator());
    }

    public Iterator getHeaderValues(String name) {
        ArrayList values = (ArrayList) headers.get(name);

        if (values == null) {
            return ((new ArrayList()).iterator());
        } else {
            return (values.iterator());
        }
    }
    
    public String getHeaderValue(String name){
    	Iterator obj = getHeaderValues(name);
    	if(obj != null){
    		return obj.next().toString();
    	}
    	return null;
    }
    

    public Iterator getLocales() {
        return (locales.iterator());
    }

    public String getMethod() {
        return (this.method);
    }

    public Map getParameterMap() {
        return parameters;
    }

    public Iterator getParameterNames() {
        return (parameters.keySet().iterator());
    }

    public String[] getParameterValues(String name) {
        return ((String[]) parameters.get(name));
    }

    public String getPathInfo() {
        return pathInfo;
    }

    public String getQueryString() {
        return (this.queryString);
    }

    public String getRequestURI() {
        return (this.requestURI);
    }

    public String getRequestURL() {
        return requestURL;
    }

    /**
     * Obtains the web application-specific fragment of the URL.
     *
     * @return the URL, excluding any server name, context path or servlet path
     */
    public String getRequestUrl() {
        return UrlUtils.getRequestUrl(this);
    }

    public String getScheme() {
        return scheme;
    }

    public String getServerName() {
        return serverName;
    }

    public int getServerPort() {
        return serverPort;
    }

    public String getServletPath() {
        return servletPath;
    }

    private boolean propertyEquals(String log, Object arg1, Object arg2) {
        if ((arg1 == null) && (arg2 == null)) {
            if (logger.isDebugEnabled()) {
                logger.debug(log + ": both null (property equals)");
            }

            return true;
        }

        if (((arg1 == null) && (arg2 != null)) || ((arg1 != null) && (arg2 == null))) {
            if (logger.isDebugEnabled()) {
                logger.debug(log + ": arg1=" + arg1 + "; arg2=" + arg2 + " (property not equals)");
            }

            return false;
        }

        if (arg1.equals(arg2)) {
            if (logger.isDebugEnabled()) {
                logger.debug(log + ": arg1=" + arg1 + "; arg2=" + arg2 + " (property equals)");
            }

            return true;
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug(log + ": arg1=" + arg1 + "; arg2=" + arg2 + " (property not equals)");
            }

            return false;
        }
    }

    public String toString() {
        return "SavedRequest[" + getFullRequestUrl() + "]";
    }
    
    /**
     * 返回上一次请求的地址
     * @return
     */
    public String getWebFrontRefererURL(boolean isIgnore){
    	String url = UrlUtils.getRequestUrl(this);
    	if(isRefererParentUrl(url)||isIgnore){//特殊请求地址，返回该地址的上一次请求
    		return getHeaderValue("Referer");
    	}
    	return url;
    }
    
    /**
     * 检查是否特殊的请求
     * @param reqUrl
     * @return
     */
    private boolean isRefererParentUrl(String reqUrl){
		boolean checkFlag = false;
		String urlArray[] =PropertiesUtil.getUrlArray();
		if(urlArray != null){
			for(String _url : urlArray){
				if(reqUrl.indexOf(_url) >= 0){
					checkFlag = true;
					break;
				}
			}
		}
		return checkFlag;
	}
}
