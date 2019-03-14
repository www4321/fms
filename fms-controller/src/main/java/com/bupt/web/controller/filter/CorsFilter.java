package com.bupt.web.controller.filter;


import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CorsFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain filterChain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) res;

        HttpServletRequest reqs = (HttpServletRequest) req;

        // Origin ->> http://localhost:8080，允许此URL进行资源访问
        // https://developer.mozilla.org/zh-CN/docs/Web/HTTP/Headers/Access-Control-Allow-Origin?utm_source=mozilla&utm_medium=devtools-netmonitor&utm_campaign=default
        response.setHeader("Access-Control-Allow-Origin",reqs.getHeader("Origin")); // 这是必须的，下面都是可选项
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        response.setHeader("Access-Control-Max-Age", "3600");
        // application/x-www-form-urlencoded, multipart/form-data 或 text/plain
        response.setHeader("Access-Control-Allow-Headers", "Content-Type,x-requested-with");
        filterChain.doFilter(req, res);
    }

    @Override
    public void destroy() {

    }
}
