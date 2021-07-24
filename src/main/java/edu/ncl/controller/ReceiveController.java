package edu.ncl.controller;

import com.alibaba.fastjson.JSON;
import edu.ncl.domain.Result;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Controller
public class ReceiveController {

    @RequestMapping("/listReceived")
    @ResponseBody
    public Result listReceived(HttpServletRequest request) throws IOException {
        Integer id = (Integer) request.getSession().getAttribute("id");
        //发送http请求去ttp，然后接收响应
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        // create GET request
        HttpGet httpGet = new HttpGet("http://localhost:8080/listReceived?userId=" + id);
        CloseableHttpResponse response = null;
        try {
            // execute the GET request
            response = httpClient.execute(httpGet);
            // response
            HttpEntity responseEntity = response.getEntity();
            //json解包，看是否发生错误，以及对应的页面渲染
            Result result = JSON.parseObject(EntityUtils.toString(responseEntity), Result.class);
            return result;
        }finally {
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
