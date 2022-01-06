package com.nobody.nobodyplace.interceptor;

import com.nobody.nobodyplace.entity.User;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class LoginInterceptor implements HandlerInterceptor {

    // 需要重定向的页面
    public static final String[] AUTH_REQUIRED_PAGES = new String[] {
//        "index"
    };

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        HttpSession session = httpServletRequest.getSession();
        String contextPath = session.getServletContext().getContextPath();

        String uri = httpServletRequest.getRequestURI();
        uri = StringUtils.remove(uri, contextPath + "/");
        if (needIntercept(uri)) {
            // 看是否登陆
            User user = (User) session.getAttribute("user");
            if (user == null) {
                httpServletResponse.sendRedirect("login");
                return false;
            }
        }
        return true;
    }

    private boolean needIntercept(String page) {
        for (String requiredAuthPage : AUTH_REQUIRED_PAGES) {
            if(StringUtils.startsWith(page, requiredAuthPage)) {
                return true;
            }
        }
        return false;
    }
}

