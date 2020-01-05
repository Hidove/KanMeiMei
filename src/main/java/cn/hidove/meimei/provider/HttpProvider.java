package cn.hidove.meimei.provider;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Component
public class HttpProvider {

    public String get(@RequestParam("url") String url,
                      @RequestParam(value = "referer", defaultValue = "https://www.baidu.com", required = false) String referer,
                      @RequestParam(value = "charset", defaultValue = "utf-8", required = false) String charset
    ) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("referer", referer);
        httpGet.setHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 Safari/537.36");
        try {
            CloseableHttpResponse response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                return EntityUtils.toString(entity, charset);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public byte[] download(@RequestParam("url") String url,
                    @RequestParam(value = "referer", defaultValue = "https://www.baidu.com", required = false) String referer
    ) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("referer", referer);
        httpGet.setHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 Safari/537.36");
        try {
            CloseableHttpResponse response = httpClient.execute(httpGet);
            byte[] bytes = EntityUtils.toByteArray(response.getEntity());
            if (bytes != null && bytes.length != 0) {
                return bytes;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String write(
            @RequestParam("response") byte[] response,
            @RequestParam("path") String path,
            @RequestParam( "fileName") String fileName
    ) {
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        try {
            File file = new File(path, fileName);
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(response);
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return path + "/" + fileName;
    }
}
