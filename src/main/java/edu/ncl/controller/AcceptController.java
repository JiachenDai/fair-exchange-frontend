package edu.ncl.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ResponseHeaderOverrides;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import edu.ncl.domain.ErrorMessage;
import edu.ncl.domain.Result;
import edu.ncl.util.RSAUtil;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Controller
public class AcceptController {


    @RequestMapping("/download")
    @ResponseBody
    public void accept(HttpServletRequest request, @RequestParam("bucket") String bucket, @RequestParam("key") String key, @RequestParam("region") String region, HttpServletResponse responseR) throws IOException {
        S3Object fullObject = null, objectPortion = null, headerOverrideObject = null;
        byte[] byt = new byte[1024];
        byte[] res = null;
        try {
//            AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
//                    .withRegion(region)
//                    .withCredentials(new ProfileCredentialsProvider())
//                    .build();
            final AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withRegion(Regions.EU_WEST_2).build();

            // Get an object and print its contents.
            fullObject = s3Client.getObject(new GetObjectRequest(bucket, key));
//            System.out.println("Content-Type: " + fullObject.getObjectMetadata().getContentType());
//            System.out.println("Content: ");
            //displayTextInputStream(fullObject.getObjectContent());
            S3ObjectInputStream stream = fullObject.getObjectContent();
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            int n = 0;
            while (-1 != (n = stream.read(byt))) {
                output.write(byt, 0, n);
            }
            res = output.toByteArray();
        } catch (AmazonServiceException e) {
            // The call was transmitted successfully, but Amazon S3 couldn't process
            // it, so it returned an error response.
            e.printStackTrace();
        } catch (SdkClientException e) {
            // Amazon S3 couldn't be contacted for a response, or the client
            // couldn't parse the response from Amazon S3.
            e.printStackTrace();
        } finally {
            // To ensure that the network connection doesn't remain open, close any open input streams.
            if (fullObject != null) {
                fullObject.close();
            }
            if (objectPortion != null) {
                objectPortion.close();
            }
            if (headerOverrideObject != null) {
                headerOverrideObject.close();
            }
        }
        InputStream is = new ByteArrayInputStream(res);

        String str = new String(res, "UTF-8");
        System.out.println(str);

        // 设置response参数，可以打开下载页面
        responseR.reset();
        responseR.setContentType(fullObject.getObjectMetadata().getContentType());
        ServletOutputStream out = responseR.getOutputStream();
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {
            bis = new BufferedInputStream(is);
            bos = new BufferedOutputStream(out);
            byte[] buff = new byte[2048];
            int bytesRead;
            while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
                bos.write(buff, 0, bytesRead);
            }
        } catch (final IOException e) {
            throw e;
        } finally {
            if (bis != null)
                bis.close();
            if (bos != null)
                bos.close();
        }

    }

    private static void displayTextInputStream(InputStream input) throws IOException {
        // Read the text input stream one line at a time and display each line.
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        String line = null;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
        System.out.println();
    }

    @RequestMapping("/viewSignature")
    @ResponseBody
    public Result viewSignature(HttpServletRequest request, @RequestParam("fileSequenceNumber") String fileSequenceNumber){
        Result result = new Result();
        //发送http请求去ttp，然后接收响应
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        // create GET request
        HttpGet httpGet = new HttpGet("http://18.169.167.111:8080/viewSignature?fileSequenceNumber=" + fileSequenceNumber);
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

    @RequestMapping("/writeReceipt")
    @ResponseBody
    public Result writeReceipt(HttpServletRequest request, @RequestParam("fileSequenceNumber") String fileSequenceNumber, @RequestParam("signature") String signature){
        Result result = new Result();
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();

        String receipt = RSAUtil.getSignature((String) request.getSession().getAttribute("RSAPrivateKey"), signature);

        // create GET request
        HttpGet httpGet = new HttpGet("http://18.169.167.111:8080/accept?fileSequenceNumber=" + fileSequenceNumber + "&receipt=" + receipt);
        CloseableHttpResponse response = null;
        try {
            // execute the GET request
            response = httpClient.execute(httpGet);
            // response
            HttpEntity responseEntity = response.getEntity();
            //json解包，看是否发生错误，以及对应的页面渲染
            result = JSON.parseObject(EntityUtils.toString(responseEntity), Result.class);
            if (result.getCode().equals(500)){
                return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
            result = new Result(500, new ErrorMessage("internal server error"));
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
        return result;
    }
}
