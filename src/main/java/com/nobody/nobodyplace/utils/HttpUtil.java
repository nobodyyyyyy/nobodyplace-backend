package com.nobody.nobodyplace.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.*;

public class HttpUtil {

    // 为了不在 ShadowSocks 中选择全局代理也能访问外网……
    private static final String LOCAL_HOST = "127.0.0.1";
    private static final int PROXY_PORT = 1087;

    private static final Logger Nlog = LoggerFactory.getLogger(HttpUtil.class);

    /**
     * http 通用 get 方法
     * @param url 请求 url
     * @param needProxy 是否需要代理
     * @param timeout 超时时间
     * @param args 其他传参
     * @return 回包，包括状态码和拉回的内容
     */
    public static HttpResponse get(URL url, boolean needProxy, int timeout, String... args) {
        // FIXME 子线程需要吗？
        int responseCode = -1;
        String data;
        try {
            
            HttpURLConnection con;
            if (needProxy) {
                Proxy proxy = new Proxy(java.net.Proxy.Type.HTTP, new InetSocketAddress(LOCAL_HOST, PROXY_PORT));
                con = (HttpURLConnection) url.openConnection(proxy);
            } else {
                con = (HttpURLConnection) url.openConnection();
            }
//            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
//            con.setRequestProperty("Accept", "application/json");
            con.setRequestMethod("GET");
            con.setConnectTimeout(timeout);

            responseCode = con.getResponseCode();
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = in.readLine()) != null) {
                sb.append(line);
            }
            in.close();
            data = sb.toString();
        } catch (Exception e) {
            data = "get... Unable to get data, err: " + e.toString();
            Nlog.info(data);
        }
        return new HttpResponse(responseCode, data);
    }

    public static final class HttpResponse {
        public int code;
        public String data;
        public HttpResponse(int code, String data) {
            this.code = code;
            this.data = data;
        }
    }
}
