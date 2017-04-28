package com.yourong.common.web;
import com.google.common.collect.Lists;
import com.yourong.common.util.Identities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by py on 2015/6/8.
 */
public class TokenHelper {

    private static Logger LOG = LoggerFactory.getLogger(TokenHelper.class);

    public static String TOKEN_KEY = "onceToken";

    public static String TOKEN_SESSION_KEY = "yourong.token.onceToken";

    /**
     * 验证证token
     *
     * @param request
     * @return
     */
    public static boolean validToken(HttpServletRequest request) {
        HttpSession session = request.getSession();
        synchronized (session) {
            String token = getTokenValue(request);
            // 如果请求中没有yourong.token,则不验证
            if (token == null) {
                    LOG.error("no token found in " + request.getRequestURI());
                return false;
            }
            List<String> tokens = getTokens(session);
            if (tokens.contains(token)) {
                removeToken(session, token);
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * 把token添加到 session中
     *
     * @param session
     * @param token
     */
    public static void addToken(HttpSession session, String token) {
        @SuppressWarnings("unchecked")
        List<String> tokens = (List<String>) session.getAttribute(TOKEN_SESSION_KEY);
        if (tokens == null) {
            tokens = Lists.newArrayList();
        } else if (tokens.size() > 100) {
            tokens = tokens.subList(0, 100);
        }
        tokens.add(token);
        session.removeAttribute(TOKEN_SESSION_KEY);
        session.setAttribute(TOKEN_SESSION_KEY, tokens);
    }

    /**
     * 生成token并添加到 session中
     *
     * @param session
     * @return
     */
    public static String generateAndAddToken(HttpSession session) {
        String token = generateToken();
        addToken(session, token);
        return token;
    }

    public static String generateRequestAndAddToken(HttpServletRequest request) {
        HttpSession session = request.getSession();
        if (session==null){
            session = request.getSession(true);
        }
        String token = generateToken();
        addToken(session, token);
        return token;
    }

    /**
     * 生成token
     *
     * @return
     */
    public static String generateToken() {
        return Identities.uuid2();
    }

    /**
     * 获得请求中的token
     *
     * @param request
     * @return
     */
    private static String getTokenValue(HttpServletRequest request) {
        return request.getParameter(TOKEN_KEY);
    }

    /**
     * 移除Token
     *
     * @param token
     */
    private static void removeToken(HttpSession session, String token) {
        @SuppressWarnings("unchecked")
        List<String> tokens = (List<String>) session.getAttribute(TOKEN_SESSION_KEY);
        tokens.remove(token);
       // if (tokens != null && !tokens.isEmpty()) {
            session.removeAttribute(TOKEN_SESSION_KEY);
            session.setAttribute(TOKEN_SESSION_KEY, tokens);
       // }
    }

    private static List<String> getTokens(HttpSession session) {
        @SuppressWarnings("unchecked")
        List<String> tokens = (List<String>) session.getAttribute(TOKEN_SESSION_KEY);
        if (tokens == null) {
            tokens = Lists.newArrayList();
            session.removeAttribute(TOKEN_SESSION_KEY);
            session.setAttribute(TOKEN_SESSION_KEY, tokens);
        }
        return tokens;
    }
}
