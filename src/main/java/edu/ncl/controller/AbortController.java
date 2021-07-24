package edu.ncl.controller;

import com.alibaba.fastjson.JSON;
import edu.ncl.domain.Result;
import edu.ncl.util.RSAUtil;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
public class AbortController {

    @RequestMapping("/abort")
    public Result abort(@RequestParam("fileSequenceNumber") String fileSequenceNumber, HttpServletRequest request){
        Result result = new Result();
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        //generate signature
        String text = request.getSession().getAttribute("id") + fileSequenceNumber;
        //do hash to the text and send the hash and the signature of hash to TTP
        String key = (String) request.getSession().getAttribute("RSAPrivateKey");
        String signature = RSAUtil.getSignature((String) request.getSession().getAttribute("RSAPrivateKey"), text);
        // create GET request
        HttpGet httpGet = new HttpGet("http://localhost:8080/abort?fileSequenceNumber=" + fileSequenceNumber + "&abortSignature=" + signature);
        CloseableHttpResponse response = null;
        try {
            // execute the GET request
            response = httpClient.execute(httpGet);
            // response
            HttpEntity responseEntity = response.getEntity();
            //json解包，看是否发生错误，以及对应的页面渲染
            result = JSON.parseObject(EntityUtils.toString(responseEntity), Result.class);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            result = new Result(500, "internal server error");
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
