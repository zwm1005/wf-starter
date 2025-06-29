package fun.werfamily.framework.log.audit.util;

import com.alibaba.fastjson.JSON;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Description:
 * <p>
 *
 * @Author Mr.WenMing
 * Created on 2022/3/14.
 */
public class HttpUtils {

    private final static String CHARSET_DEFAULT = "UTF-8";

    public static Logger log = LoggerFactory.getLogger(HttpUtils.class);

    /**
     * post请求  编码格式默认UTF-8
     *
     * @param url     请求url
     * @return
     */
    public static String doPost(String url, Object obj, int connectTimeout, int socketTimeout) {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        CloseableHttpResponse resp = null;

        try {
            Map<String, String> params = ObjectToMapUtils.objectToMap(obj);
            HttpPost httpPost = new HttpPost(url);

            RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectTimeout(connectTimeout)
                    .setSocketTimeout(socketTimeout).build();
            httpPost.setConfig(requestConfig);
            if (params != null && params.size() > 0) {
                List<NameValuePair> list = new ArrayList<NameValuePair>();
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    list.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
                }
                httpPost.setEntity(new UrlEncodedFormEntity(list, CHARSET_DEFAULT));
            }

            resp = httpClient.execute(httpPost);
            String body = EntityUtils.toString(resp.getEntity(), CHARSET_DEFAULT);
            int statusCode = resp.getStatusLine().getStatusCode();
            return body;
        } catch (Exception e) {
            log.error("Http fail url = {} param = {} ", url, JSON.toJSONString(obj), e);
            return null;
        } finally {
            if (null != resp) {
                try {
                    resp.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * post请求  编码格式默认UTF-8
     *
     * @param url     请求url
     * @return
     */
    public static String doGet(String url, Object obj, int connectTimeout, int socketTimeout) {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        CloseableHttpResponse resp = null;

        try {
            URIBuilder uriBuilder = new URIBuilder(url);

            Map<String, String> params = ObjectToMapUtils.objectToMap(obj);

            if (params != null && params.size() > 0) {
                List<NameValuePair> list = new ArrayList<NameValuePair>();
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    uriBuilder.setParameter(entry.getKey(),entry.getValue());
                }
            }
            HttpGet httpGet = new HttpGet(uriBuilder.build());

            RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectTimeout(connectTimeout)
                    .setSocketTimeout(socketTimeout).build();
            httpGet.setConfig(requestConfig);
            resp = httpClient.execute(httpGet);
            String body = EntityUtils.toString(resp.getEntity(), CHARSET_DEFAULT);
            int statusCode = resp.getStatusLine().getStatusCode();

            if(statusCode == HttpStatus.SC_OK) {
                return body;
            }else {
                log.error("Http fail url = {} param = {} resp = {}", url, JSON.toJSONString(obj), JSON.toJSONString(resp));
                return null;
            }
        } catch (Exception e) {
            log.error("Http fail url = {} param = {} ", url, JSON.toJSONString(obj), e);
            return null;
        } finally {
            if (null != resp) {
                try {
                    resp.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
