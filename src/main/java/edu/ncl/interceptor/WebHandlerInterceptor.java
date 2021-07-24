package edu.ncl.interceptor;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


public class  WebHandlerInterceptor implements HandlerInterceptor {
    //在controller调用前进行
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        HttpSession session=request.getSession();
//        Object ob=session.getAttribute("name");
//        if (ob!=null) {
//            System.out.println(session.getAttribute("name"));
//            return true;
//        }
//        response.sendRedirect("http://localhost:8081/login.html");
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView model) throws Exception{
    }
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,Exception ex) throws Exception{
    }

}
