package edu.ncl.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import edu.ncl.domain.Result;
import edu.ncl.domain.User;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Controller
public class LoginController {

    @GetMapping("/login")
    @ResponseBody
    public Object login(@RequestParam("username") String username, @RequestParam("password") String password, HttpServletRequest request){
        System.out.println("login");
        if (username == null || username.equals("") || password == null || password.equals("")){
            return "error";
        }
        //发送http请求去ttp，然后接收响应
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        // create GET request
        HttpGet httpGet = new HttpGet("http://18.169.167.111:8080/login?username=" + username + "&password=" + password);
        CloseableHttpResponse response = null;
        try {
            // execute the GET request
            response = httpClient.execute(httpGet);
            // response
            HttpEntity responseEntity = response.getEntity();
            //json解包，看是否发生错误，以及对应的页面渲染
            Result result = JSON.parseObject(EntityUtils.toString(responseEntity), Result.class);
            JSONObject jsonObject = (JSONObject) result.getData();
            if (jsonObject.containsKey("username")){
                String user = jsonObject.getString("username");
                request.getSession().setAttribute("name", user);
                request.getSession().setAttribute("id", jsonObject.getInteger("id"));
                request.getSession().setAttribute("RSAPrivateKey", jsonObject.getString("privateKey"));
                System.out.println(request.getSession().getAttribute("name"));
            }
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

    @GetMapping("/logout")
    @ResponseBody
    public Result login(HttpServletRequest request){
        System.out.println("logout");
        Result result = new Result();
        //直接删除session
        HttpSession session = request.getSession();
        session.removeAttribute("name");
        session.removeAttribute("id");
        session.removeAttribute("RSAPrivateKey");
        result.setCode(200);
        return result;
    }

}
