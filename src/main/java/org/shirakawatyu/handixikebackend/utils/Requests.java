package org.shirakawatyu.handixikebackend.utils;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.cookie.CookieStore;
import org.apache.hc.client5.http.cookie.StandardCookieSpec;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.apache.hc.core5.util.Timeout;
import org.shirakawatyu.handixikebackend.exception.RequestException;
import org.springframework.util.MultiValueMap;

import java.io.IOException;
import java.util.ArrayList;

/**
 * 封装的发送请求
 *
 * @author ShirakawaTyu
 * @since 2022/10/1 17:45
 */
public class Requests {
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:123.0) Gecko/20100101 Firefox/123.0";

    private static final RequestConfig requestConfig = RequestConfig.custom()
            .setConnectionRequestTimeout(Timeout.ofMilliseconds(3000))
            .setResponseTimeout(Timeout.ofMilliseconds(3000))
            .setCircularRedirectsAllowed(true)
            .setCookieSpec(StandardCookieSpec.RELAXED)
            .build();

    public static String getForString(String url, String referer, CookieStore cookieStore) {
        return getByHttpClient(url, referer, cookieStore, classicHttpResponse -> {
            org.apache.hc.core5.http.HttpEntity entity = classicHttpResponse.getEntity();
            String encoding = entity.getContentEncoding();
            if (encoding == null) {
                encoding = "UTF-8";
            }
            return new String(entity.getContent().readAllBytes(), encoding);
        });
    }

    public static byte[] getForBytes(String url, String referer, CookieStore cookieStore) {
        return getByHttpClient(url, referer, cookieStore, classicHttpResponse -> {
            org.apache.hc.core5.http.HttpEntity entity = classicHttpResponse.getEntity();
            return entity.getContent().readAllBytes();
        });
    }

    public static String postForString(String url, MultiValueMap<String, String> data, CookieStore cookieStore) {
        return postByHttpClient(url, cookieStore, data, classicHttpResponse -> {
            org.apache.hc.core5.http.HttpEntity entity = classicHttpResponse.getEntity();
            String encoding = entity.getContentEncoding();
            if (encoding == null) {
                encoding = "UTF-8";
            }
            return new String(entity.getContent().readAllBytes(), encoding);
        });
    }

    public static <T> T getByHttpClient(String url, String referer, CookieStore cookieStore, HttpClientResponseHandler<T> handler) {
        HttpGet httpGet = new HttpGet(url);
        if (!"".equals(referer)) {
            httpGet.addHeader("referer", referer);
        }
        httpGet.addHeader("Content-Type", "application/json");
        httpGet.addHeader("User-Agent", USER_AGENT);
        try (CloseableHttpClient client = HttpClients.custom().setDefaultRequestConfig(requestConfig).setDefaultCookieStore(cookieStore).build()) {
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
        data.forEach((key, value) -> params.add(new BasicNameValuePair(key, value.get(0))));
        httpPost.setEntity(new UrlEncodedFormEntity(params));
        try (CloseableHttpClient client = HttpClients.custom().setDefaultRequestConfig(requestConfig).setDefaultCookieStore(cookieStore).build()) {
            return client.execute(httpPost, handler);
        } catch (IOException e) {
            throw new RequestException(e);
        }
    }
}
