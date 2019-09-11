package com.zhw.filter;


import com.zhw.po.User;
import com.zhw.utils.ResultBean;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;



public class LoginFilter implements Filter {
    //不需要登录url
    private String[] nofilter;
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String url = filterConfig.getInitParameter("allowedurl");
//        System.out.println(url);
        nofilter = url.split(";");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String path = request.getServletPath();

        //只需要拦截.action请求
        if(!path.endsWith(".action")){
            filterChain.doFilter(servletRequest,servletResponse);
            return;
        }
        //不需要登录直接放行
        for(String url:nofilter){
            if(path.startsWith(url.trim())){
                filterChain.doFilter(servletRequest,servletResponse);
                return;
            }
        }
//        System.out.println("验证是否登录"+path);
        //验证是否登录
        HttpSession session = request.getSession(false);

        if(session!=null&&session.getAttribute("user")!=null){
//          System.out.println("登录"+(User)session.getAttribute("user"));
            filterChain.doFilter(servletRequest,servletResponse);
            return;
        }
        //如果是普通请求
        if(request.getHeader("X-Requested-With")==null){
            response.sendRedirect(request.getContextPath());
            return;
        }
        //ajax请求
        ResultBean rs = new ResultBean();
        rs.setCode(207);
        rs.setMesg("请先登录");
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().write(rs.toJsonString());
    }

    @Override
    public void destroy() {

    }
}
