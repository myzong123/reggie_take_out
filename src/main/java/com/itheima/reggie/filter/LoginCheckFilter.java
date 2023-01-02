package com.itheima.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;
import sun.util.resources.cldr.mg.LocaleNames_mg;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * @author myz
 * @create 2022/12/28-14:10
 */
@Slf4j
@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
public class LoginCheckFilter implements Filter {
    // 路径匹配工具类
    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String uri = request.getRequestURI();
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/sendMsg",
                "/user/login"
        };
        if (check(uri, urls)) {
            filterChain.doFilter(request, response);
            return;
        }
        HttpSession session = request.getSession();
        Long employee = (Long) session.getAttribute("employee");
        User user = (User) session.getAttribute("user");
        if (employee != null) {
            BaseContext.setCurrentId(employee);
            filterChain.doFilter(request, response);
            return;
        }
        if (user != null) {
            BaseContext.setCurrentId(user.getId());
            filterChain.doFilter(request, response);
            return;
        }

        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
    }

    /**
     * 判断请求路径是否需要过滤
     *
     * @param uri
     * @param urls
     * @return
     */
    public boolean check(String uri, String[] urls) {
        for (String url : urls) {
            if (PATH_MATCHER.match(url, uri)) {
                return true;
            }
        }
        return false;
    }
}

//     /**
//      * 判断用户是否登录
//      * @param response
//      * @param request
//      * @param filterChain
//      * @param name
//      * @throws ServletException
//      * @throws IOException
//      */
//     public boolean checkName(HttpServletResponse response, HttpServletRequest request,FilterChain filterChain, String name) throws ServletException, IOException {
//         HttpSession session = request.getSession();
//         Long attribute = (Long) session.getAttribute(name);
//         if(attribute != null){
//             BaseContext.setCurrentId(attribute);
//             filterChain.doFilter(request,response);
//             return true;
//         }
//         return false;
//     }
// }
