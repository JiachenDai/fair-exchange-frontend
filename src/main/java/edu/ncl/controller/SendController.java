package edu.ncl.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import edu.ncl.domain.Result;
import edu.ncl.util.RSAUtil;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Controller
public class SendController {

    @RequestMapping("/upload")
    @ResponseBody
    public Result upload(@RequestParam("receiverEmail") String receiverEmail, @RequestParam("privateKey") String privateKey, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Result res = new Result();
        MultipartFile file = ((MultipartHttpServletRequest) request).getFile("file");
        System.out.println(file.getSize());
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        // create GET request
        HttpPost httpPost = new HttpPost("http://3.10.225.160:8080/upload/"+ request.getSession().getAttribute("id") +"/" + receiverEmail);
        CloseableHttpResponse rsp = null;
        try {
            MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
            // add file
            String filesKey = "file";

            File f = new File(file.getOriginalFilename());
            f.createNewFile();
            inputStreamToFile(file.getInputStream(), f);

            multipartEntityBuilder.addBinaryBody(filesKey, f);
            UUID uuid = UUID.randomUUID();
            multipartEntityBuilder.addTextBody("uuid", uuid.toString());

            //sender id, receiver id, and uuid
            String text = request.getSession().getAttribute("id") + receiverEmail + uuid.toString();
            //do hash to the text and send the hash and the signature of hash to TTP
            String key = privateKey;
            System.out.println("key is: " + key);
            String signature = RSAUtil.getSignature(key, text);
            multipartEntityBuilder.addTextBody("signature", signature);

            HttpEntity httpEntity = multipartEntityBuilder.build();
            httpPost.setEntity(httpEntity);

            // execute the GET request
            rsp = httpClient.execute(httpPost);
            // response
            HttpEntity responseEntity = rsp.getEntity();
            Result result = JSON.parseObject(EntityUtils.toString(responseEntity), Result.class);
//            if (result.getCode().equals(200)){
//                response.sendRedirect("http://localhost:8081/index.html");
//            } else {
//                response.sendRedirect("http://localhost:8081/error.html");
//            }
            f.delete();
            return result;
        } finally {
            try {
                // release the resources
                if (httpClient != null) {
                    httpClient.close();
                }
                if (rsp != null) {
                    rsp.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @RequestMapping("/listSent")
    @ResponseBody
    public Result listSent(HttpServletRequest request) throws IOException {
        Integer id = (Integer) request.getSession().getAttribute("id");
        //发送http请求去ttp，然后接收响应
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        // create GET request
        HttpGet httpGet = new HttpGet("http://3.10.225.160:8080/listSent?userId=" + id);
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


    private static void inputStreamToFile(InputStream ins, File file){
        FileOutputStream os=null;
        try {
            os=new FileOutputStream(file);
            int bytesRead=0;
            byte[] buffer=new byte[1024];
            while ((bytesRead=ins.read(buffer))!=-1){
                os.write(buffer,0,bytesRead);
            }
        }catch (Exception e){
            throw new RuntimeException("调用inputStreamToFile产生异常："+e.getMessage());
        }finally {
            try {
                if (os!=null){
                    os.close();
                }
                if (ins!=null){
                    ins.close();
                }
            }catch (IOException e){
                throw new RuntimeException("inputStreamToFile关闭io产生异常："+e.getMessage());
            }
        }
    }


}
