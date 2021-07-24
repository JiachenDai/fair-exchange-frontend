package edu.ncl.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import edu.ncl.domain.ErrorMessage;
import edu.ncl.domain.Result;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class RegisterController {

    @PostMapping("/register")
    @ResponseBody
    public Result register(@RequestParam("username") String username, @RequestParam("password") String password, @RequestParam("email") String email, HttpServletRequest request){
        System.out.println("register");
        if (username == null || username.equals("") || password == null || password.equals("") || email == null || email.equals("")){
            Result result = new Result(500, new ErrorMessage("lack of information"));
            return result;
        }
        //发送http请求去ttp，然后接收响应
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        // create GET request
        HttpGet httpGet = new HttpGet("http://18.169.167.111:8080/register?" + "username=" + username + "&password=" + password + "&email=" + email);
        CloseableHttpResponse response = null;
        try {
            // execute the GET request
            response = httpClient.execute(httpGet);
            // response
            HttpEntity responseEntity = response.getEntity();
            //json解包，看是否发生错误，以及对应的页面渲染
            Result result = JSON.parseObject(EntityUtils.toString(responseEntity), Result.class);
            return result;
        } catch (Exception e) {
            System.out.println("connection failed: "+e.getMessage());
            Result result = new Result(500, "internal server error");
            return result;
        } finally {
            try {
                // release the resources
                if (httpClient != null) {
                    httpClient.close();
                }
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
