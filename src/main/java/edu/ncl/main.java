package edu.ncl;

import edu.ncl.util.RSAUtil;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class main {

//    private static String RSAPrivateKey = "MIIBUwIBADANBgkqhkiG9w0BAQEFAASCAT0wggE5AgEAAkEAgEXQGHIp6M559p+Jfm7zwz3BCtif3mduEwtWja1AeBQCF2AGGKYLBF836nwhi44B4NsBUCWdTFLOh/sryfQMyQIDAQABAkBSB1MaLveqD1BjbIaKuf1cn8MxYnJp0Y7M0vVYyLBvmMEnahbHG1g37k7zrPFynQm9p6CVBDvg9pBMvpl4xlABAiEA1QRPf/yRqT3hQJrheuvp0OzGwm3lHuEM12xIPBBJrckCIQCaJ+xJwIobEd2Xtzn2Hr2QJDEveYN45upVJ/7i1ArnAQIgQAm5tKpDR/O4Yemt23xMoGhuIElVVgEmESUWWigzXjECIGX+Xh7Z7kZExslXqFjINAHHgGd5zVvT5F0dTk4Dn4cBAiABQ9iBpgoeqeAs5bjIt9HfEquW8Zit6wgLm7BlQViVHA==";
//    public static void main(String[] args) {
//        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
//        // create GET request
//        HttpPost httpPost = new HttpPost("http://localhost:8080/upload/1/2");
//        CloseableHttpResponse response = null;
//        try {
//            MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
//            // add file
//            String filesKey = "file";
//            File file1 = new File("test.txt");
//            multipartEntityBuilder.addBinaryBody(filesKey, file1);
//            UUID uuid = UUID.randomUUID();
//            multipartEntityBuilder.addTextBody("uuid", uuid.toString());
//
//            //sender id, receiver id, and uuid
//            String text = "1" + "2" + uuid.toString();
//            //do hash to the text and send the hash and the signature of hash to TTP
//
//            String signature = RSAUtil.getSignature(RSAPrivateKey, text);
//            multipartEntityBuilder.addTextBody("signature", signature);
//
//            HttpEntity httpEntity = multipartEntityBuilder.build();
//            httpPost.setEntity(httpEntity);
//
//            // execute the GET request
//            response = httpClient.execute(httpPost);
//            // response
//            HttpEntity responseEntity = response.getEntity();
//            System.out.println("response status is " + response.getStatusLine());
//            if (responseEntity != null) {
//                System.out.println("response length is :" + responseEntity.getContentLength());
//                System.out.println("response content is :" + EntityUtils.toString(responseEntity));
//            }
//        } catch (Exception e) {
//            System.out.println("connection failed: "+e.getMessage());
//        } finally {
//            try {
//                // release the resources
//                if (httpClient != null) {
//                    httpClient.close();
//                }
//                if (response != null) {
//                    response.close();
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
}
