package org.shirakawatyu.handixikebackend.utils;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.cookie.CookieStore;
import org.apache.hc.client5.http.cookie.StandardCookieSpec;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.DefaultRedirectStrategy;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.apache.hc.client5.http.ssl.TrustAllStrategy;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.apache.hc.core5.util.Timeout;
import org.shirakawatyu.handixikebackend.exception.RequestException;
import org.springframework.util.MultiValueMap;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

/**
 * 封装的发送请求
 *
 * @author ShirakawaTyu
 * @since 2022/10/1 17:45
 */
@UtilityClass
public class Requests {
    private static final String USER_AGENT = "\n" +
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36 Edg/122.0.0.0";

    private static final RequestConfig requestConfig = RequestConfig.custom()
            .setConnectionRequestTimeout(Timeout.ofMilliseconds(5000))
            .setResponseTimeout(Timeout.ofMilliseconds(5000))
            .setCircularRedirectsAllowed(true)
            .setMaxRedirects(32)
            .setRedirectsEnabled(true)
            .setCookieSpec(StandardCookieSpec.RELAXED)
            .build();

    public static String getForString(String url, String referer, CookieStore cookieStore) {
        return getByHttpClient(url, referer, cookieStore, classicHttpResponse -> {
            HttpEntity entity = classicHttpResponse.getEntity();
            String encoding = entity.getContentEncoding();
            if (encoding == null) {
                encoding = "UTF-8";
            }
            return new String(entity.getContent().readAllBytes(), encoding);
        });
    }

    public static byte[] getForBytes(String url, String referer, CookieStore cookieStore) {
        return getByHttpClient(url, referer, cookieStore, classicHttpResponse -> {
            HttpEntity entity = classicHttpResponse.getEntity();
            return entity.getContent().readAllBytes();
        });
    }

    public static String postForString(String url, MultiValueMap<String, String> data, CookieStore cookieStore) {
        return postByHttpClient(url, cookieStore, data, classicHttpResponse -> {
            HttpEntity entity = classicHttpResponse.getEntity();
            String encoding = entity.getContentEncoding();
            if (encoding == null) {
                encoding = "UTF-8";
            }
            return new String(entity.getContent().readAllBytes(), encoding);
        });
    }

    @SneakyThrows
    public static <T> T getByHttpClient(String url, String referer, CookieStore cookieStore, HttpClientResponseHandler<T> handler) {
        HttpGet httpGet = new HttpGet(url);
        if (!"".equals(referer)) {
            httpGet.addHeader("referer", referer);
        }
        httpGet.addHeader("Content-Type", "application/json");
        httpGet.addHeader("User-Agent", USER_AGENT);
        try (CloseableHttpClient client = HttpClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .setConnectionManager(getManager())
                .setRedirectStrategy(new DefaultRedirectStrategy())
                .setDefaultCookieStore(cookieStore)
                .disableDefaultUserAgent()
                .build()) {
            return client.execute(httpGet, handler);
        } catch (IOException e) {
            throw new RequestException(e);
        }
    }

    public static <T> T postByHttpClient(String url, CookieStore cookieStore, MultiValueMap<String, String> data, HttpClientResponseHandler<T> handler) {
        HttpPost httpPost = new HttpPost(url);
        httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded");
        httpPost.addHeader("User-Agent", USER_AGENT);
        ArrayList<NameValuePair> params = new ArrayList<>();
        data.forEach((key, value) -> params.add(new BasicNameValuePair(key, value.getFirst())));
        httpPost.setEntity(new UrlEncodedFormEntity(params));
        try (CloseableHttpClient client = HttpClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .setConnectionManager(getManager())
                .setRedirectStrategy(new DefaultRedirectStrategy())
                .setDefaultCookieStore(cookieStore)
                .disableDefaultUserAgent()
                .build()) {
            return client.execute(httpPost, handler);
        } catch (IOException e) {
            throw new RequestException(e);
        }
    }


    @SneakyThrows
    private PoolingHttpClientConnectionManager getManager() {
        return PoolingHttpClientConnectionManagerBuilder.create()
                .setSSLSocketFactory(SSLConnectionSocketFactoryBuilder.create()
                        .setSslContext(SSLContextBuilder.create()
                                .loadTrustMaterial(TrustAllStrategy.INSTANCE)
                                .build())
                        .setHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                        .build())
                .build();
    }
}
