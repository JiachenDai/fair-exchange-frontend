package edu.ncl.interceptor;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@ControllerAdvice
public class MyExceptionHandler {

    @ExceptionHandler(value =Exception.class)
    public void exceptionHandler(Exception e, HttpServletResponse response){
        System.out.println("server error:"+e);
        try {
            response.sendRedirect("http://localhost:8081/error.html");
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
}
