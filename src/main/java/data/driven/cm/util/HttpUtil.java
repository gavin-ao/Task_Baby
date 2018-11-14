package data.driven.cm.util;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static data.driven.cm.util.JSONUtil.putMsg;

/**
 * HTTP工具类
 * 发送http/https协议get/post请求，发送map，json，xml，txt数据
 * @author 何晋凯
 * @date 2018/06/04
 */
public class HttpUtil {
    private static Logger logger = LoggerFactory.getLogger(HttpUtil.class);
    private static PoolingHttpClientConnectionManager connMgr;
    private static RequestConfig requestConfig;
    private static final int MAX_TIMEOUT = 5000;
    private static final String CONDING = "UTF-8";

    private HttpUtil() {

    }

    static {
        //设置连接池
        connMgr = new PoolingHttpClientConnectionManager();
        //设置连接池大小
        connMgr.setMaxTotal(100);
        connMgr.setDefaultMaxPerRoute(connMgr.getMaxTotal());

        RequestConfig.Builder configBuilder = RequestConfig.custom();
        //设置连接超时
        configBuilder.setConnectionRequestTimeout(MAX_TIMEOUT);
        //设置读取超时
        configBuilder.setConnectTimeout(MAX_TIMEOUT);
        //设置从连接池获取连接实例的超时
        configBuilder.setConnectionRequestTimeout(MAX_TIMEOUT);
        //在提交请求之前，测试连接是否可用
        configBuilder.setStaleConnectionCheckEnabled(true);
        requestConfig = configBuilder.build();
    }

    /**
     * 发送 GET 请求（HTTP），不带输入数据
     */
    public static String doGet(String url) {
        return doGet(url, new HashMap<String, Object>());
    }

    /**
     * 发送 GET 请求（HTTP），K-V形式
     */
    public static String doGet(String url, Map<String, Object> params) {
        String apiUrl = url;
        StringBuilder param = new StringBuilder();
        int i = 0;
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            String key = entry.getKey();
            if (i == 0) {
                param.append("?");
            } else {
                param.append("&");
            }
            param.append(key).append("=").append(params.get(key));
            i++;
        }
        apiUrl += param;
        String result = null;
        HttpClient httpClient = new DefaultHttpClient();
        HttpResponse response = null;
        try {
            HttpGet httpGet = new HttpGet(apiUrl);
            response = httpClient.execute(httpGet);

            //返回的结果
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream instream = entity.getContent();
                result = IOUtils.toString(instream, CONDING);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return result;
    }

    /**
     * 发送 POST 请求（HTTP），不带输入数据
     */
    public static String doPost(String apiUrl) {
        return doPost(apiUrl, new HashMap<String, Object>());
    }

    /**
     * 发送 POST 请求（HTTP），K-V形式
     *
     * @param apiUrl API接口URL
     * @param params 参数map
     */
    public static String doPost(String apiUrl, Map<String, Object> params) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        String httpStr = null;
        //POST的URL
        HttpPost httpPost = new HttpPost(apiUrl);
        CloseableHttpResponse response = null;

        try {
            httpPost.setConfig(requestConfig);
            //建立HttpPost对象
            List<NameValuePair> pairList = new ArrayList<NameValuePair>(params.size());
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                NameValuePair pair = new BasicNameValuePair(entry.getKey(), entry.getValue().toString());
                pairList.add(pair);
            }
            httpPost.setEntity(new UrlEncodedFormEntity(pairList, Charset.forName(CONDING)));
            response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            httpStr = EntityUtils.toString(entity, CONDING);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            try {
                httpClient.close();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
            if (response != null) {
                try {
                    EntityUtils.consume(response.getEntity());
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
        return httpStr;
    }

    /**
     * 发送 POST 请求（HTTP），JSON形式
     *
     * @param json json对象
     */
    public static String doPost(String apiUrl, Object json) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        String httpStr = null;
        HttpPost httpPost = new HttpPost(apiUrl);
        CloseableHttpResponse response = null;

        try {
            httpPost.setConfig(requestConfig);
            //转码
            StringEntity stringEntity = new StringEntity(json.toString(), "UTF-8");
            stringEntity.setContentEncoding(CONDING);
            stringEntity.setContentType("application/json");
            httpPost.setEntity(stringEntity);
            response = httpClient.execute(httpPost);
            HttpEntity httpEntity = response.getEntity();
            httpStr = EntityUtils.toString(httpEntity, CONDING);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            try {
                httpClient.close();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
            if (response != null) {
                try {
                    EntityUtils.consume(response.getEntity());
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
        return httpStr;
    }

    /**
     * 发送 SSL GET 请求（HTTPS）
     * @param apiUrl API接口URL
     */
    public static String doGetSSL(String apiUrl) {
        CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(createSSLConnSocketFactory()).setConnectionManager(connMgr).setDefaultRequestConfig(requestConfig).build();
        HttpGet httpGet = new HttpGet(apiUrl);
        CloseableHttpResponse response = null;
        String httpStr = null;

        try {
            httpGet.setConfig(requestConfig);
            response = httpClient.execute(httpGet);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                return null;
            }
            HttpEntity entity = response.getEntity();
            if (entity == null) {
                return null;
            }
            httpStr = EntityUtils.toString(entity, CONDING);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (response != null) {
                try {
                    EntityUtils.consume(response.getEntity());
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
        return httpStr;
    }

    /**
     * 发送 SSL POST 请求（HTTPS），K-V形式
     *
     * @param apiUrl API接口URL
     * @param params 参数map
     */
    public static String doPostSSL(String apiUrl, Map<String, Object> params) {
        CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(createSSLConnSocketFactory()).setConnectionManager(connMgr).setDefaultRequestConfig(requestConfig).build();
        HttpPost httpPost = new HttpPost(apiUrl);
        CloseableHttpResponse response = null;
        String httpStr = null;

        try {
            httpPost.setConfig(requestConfig);
            List<NameValuePair> pairList = new ArrayList<NameValuePair>(params.size());
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                NameValuePair pair = new BasicNameValuePair(entry.getKey(), entry.getValue().toString());
                pairList.add(pair);
            }
            httpPost.setEntity(new UrlEncodedFormEntity(pairList, Charset.forName(CONDING)));
            response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                return null;
            }
            HttpEntity entity = response.getEntity();
            if (entity == null) {
                return null;
            }
            httpStr = EntityUtils.toString(entity, CONDING);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (response != null) {
                try {
                    EntityUtils.consume(response.getEntity());
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
        return httpStr;
    }

    /**
     * 发送 SSL POST 请求（HTTPS），JSON形式
     *
     * @param apiUrl API接口URL
     * @param json   JSON对象
     */
    public static String doPostSSL(String apiUrl, Object json) {
        CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(createSSLConnSocketFactory()).setConnectionManager(connMgr).setDefaultRequestConfig(requestConfig).build();
        HttpPost httpPost = new HttpPost(apiUrl);
        CloseableHttpResponse response = null;
        String httpStr = null;

        try {
            httpPost.setConfig(requestConfig);
            StringEntity stringEntity = new StringEntity(json.toString(), CONDING);
            stringEntity.setContentEncoding(CONDING);
            stringEntity.setContentType("application/json");
            httpPost.setEntity(stringEntity);
            response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                return null;
            }
            HttpEntity httpEntity = response.getEntity();
            if (httpEntity == null) {
                return null;
            }
            httpStr = EntityUtils.toString(httpEntity, CONDING);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (response != null) {
                try {
                    EntityUtils.consume(response.getEntity());
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
        return httpStr;
    }

    /**
     * 发送 SSL POST 请求（HTTPS），JSON形式 - 返回附件
     * @param apiUrl API接口URL
     * @param json   JSON对象
     */
    public static JSONObject doPostSSLAttachment(String apiUrl, Object json, String fileName) {
        CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(createSSLConnSocketFactory()).setConnectionManager(connMgr).setDefaultRequestConfig(requestConfig).build();
        HttpPost httpPost = new HttpPost(apiUrl);
        CloseableHttpResponse response = null;
        try {
            httpPost.setConfig(requestConfig);
            StringEntity stringEntity = new StringEntity(json.toString(), CONDING);
            stringEntity.setContentEncoding(CONDING);
            stringEntity.setContentType("application/json");
            httpPost.setEntity(stringEntity);
            response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                return putMsg(false, "101", "调用失败");
            }
            HttpEntity httpEntity = response.getEntity();
            if (httpEntity == null) {
                return putMsg(false, "102", "返回实体为空");
            }
            byte[] bytes = FileUtil.readInputStream(httpEntity.getContent());
            String str = new String(bytes);
            if(str.indexOf("errcode") > 0){
                JSONObject result = JSONUtil.putMsg(false, "103", "调用失败");
                result.putAll(JSONObject.parseObject(str));
                return result;
            }else{
                FileUtil.createFile(fileName, bytes);
            }

        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            JSONUtil.putMsg(false, "104", "调用失败");
        } finally {
            if (response != null) {
                try {
                    EntityUtils.consume(response.getEntity());
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
        return putMsg(true, "200", "调用成功");
    }

    /**
     * 创建SSL安全连接
     */
    private static SSLConnectionSocketFactory createSSLConnSocketFactory() {
        SSLConnectionSocketFactory sslsf = null;
        try {
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
                public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    return true;
                }
            }).build();
            sslsf = new SSLConnectionSocketFactory(sslContext, new X509HostnameVerifier() {

                public void verify(String s, SSLSocket sslSocket) throws IOException {
                    //验证信息
                }

                public void verify(String s, X509Certificate x509Certificate) throws SSLException {
                    //验证信息
                }

                public void verify(String s, String[] strings, String[] strings1) throws SSLException {
                    //验证信息
                }

                public boolean verify(String arg0, SSLSession arg1) {
                    return true;
                }
            });
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return sslsf;
    }


    /**
     * 发送https请求，返回二维码图片
     * @param requestUrl 请求地址
     * @param requestMethod 请求方式（GET、POST）
     * @param data 提交的数据
     * @param savePath 图片保存路径
     * @param fileName 图片名称
     * @param fileType 图片类型
     */
    public static void httpsRequestPicture(String requestUrl, String requestMethod, String data, String savePath, String fileName, String fileType) {
        System.out.println("保存图片");
        InputStream inputStream = null;
        try {
            URL url = new URL(requestUrl);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            //设置请求方式（GET/POST）
            conn.setRequestMethod(requestMethod);
            conn.connect();
            //当data不为null时向输出流写数据
            if (null != data) {
                //getOutputStream方法隐藏了connect()方法
                OutputStream outputStream = conn.getOutputStream();
                //注意编码格式
                outputStream.write(data.getBytes("UTF-8"));
                outputStream.close();
            }
            // 从输入流读取返回内容
            inputStream = conn.getInputStream();
            logger.info("开始生成微信二维码...");
            WXUtil.inputStreamToMedia(inputStream, savePath, fileName, fileType);
            logger.info("微信二维码生成成功!!!");
            conn.disconnect();
        } catch (Exception e) {
            logger.error("发送https请求失败，失败", e);
        }finally {
            //释放资源
            try {
                if(null != inputStream) {
                    inputStream.close();
                }
            } catch (IOException e) {
                logger.error("释放资源失败，失败", e);
            }
        }
    }

}
