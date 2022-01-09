package com.nobody.nobodyplace.utils;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class HttpUtil {

    private static final String USER_AGENT = "Mozilla/5.0"; // TODO ?


    public static void httpsGetData(final String input, final HttpsRequestCallback cb) {

//        final String domain = "qidian.qq.com";
//        final String requestUrl = urlBuilder.build().toString();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String result = null;
                try {
//                    URL url = new URL(requestUrl);
//                    HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
//                    conn.setRequestMethod("GET");
//                    conn.setConnectTimeout(30 * 1000);
//                    conn.setReadTimeout(30 * 1000);
//                    conn.connect();
//                    if (conn.getResponseCode() == HttpsURLConnection.HTTP_OK) {
//                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//                        StringBuffer sb = new StringBuffer();
//                        String line = null;
//                        while ((line = br.readLine()) != null) {
//                            sb.append(line);
//                        }
//                        result = sb.toString();
//                    } else {
//                        // TODO LOG
//                    }

                    String url = "http://suggestqueries.google.com/complete/search?output=toolbar&hl=zh&q=" + input;

                    URL obj = new URL(url);
                    HttpURLConnection con = (HttpURLConnection) obj.openConnection();

                    //默认值我GET
                    con.setRequestMethod("GET");

                    //添加请求头
                    con.setRequestProperty("User-Agent", USER_AGENT);

                    int responseCode = con.getResponseCode();
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(con.getInputStream()));
                    String inputLine;
                    StringBuilder sb = new StringBuilder();

                    while ((inputLine = in.readLine()) != null) {
                        sb.append(inputLine);
                    }
                    in.close();

                    //打印结果
                    System.out.println(sb.toString());
                    result = sb.toString();
                } catch (MalformedURLException mue) {
//                    QidianLog.d(TAG, QidianLog.USR, "http request, url invalid, url = " + requestUrl);
                } catch (IOException ioe) {
//                    QidianLog.d(TAG, QidianLog.USR, "http request, ioexception");
                } catch (Exception ioe) {
//                    QidianLog.d(TAG, QidianLog.USR, "http request, exception");
                } finally {
                    if (cb != null) {
                        cb.onFinished(result);
                    }
                }
            }
        });
    }

//    public void httpsPostData(final Uri.Builder urlBuilder, final String params, final AsyncRequestCallback cb)
//    {
//        final String domain = "qidian.qq.com";
//        final String csrf = String.valueOf((long) (System.currentTimeMillis() + Math.random() * System.currentTimeMillis()));
//        final String requestUrl = urlBuilder.build().toString();
//
//        ThreadManager.post(new Runnable() {
//            @Override
//            public void run()
//            {
//                try {
//                    JSONObject args = new JSONObject(params);
//                    args.put(CMD_PARAM_CRSF,csrf);
//                    URL url = new URL(requestUrl);
//                    HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
//                    conn.setRequestProperty("Content-Type","application/json; charset=UTF-8");
//                    conn.setRequestProperty("Accept", "application/json");
//                    conn.setRequestMethod("POST");
//                    conn.setConnectTimeout(30 * 1000);
//                    conn.setReadTimeout(30 * 1000);
//                    conn.setDoInput(true); //允許輸入流，即允許下載
//                    conn.setDoOutput(true); //允許輸出流，即允許上傳
//                    conn.setUseCaches(false); //設置是否使用緩存
//                    TicketManager tm = (TicketManager) appManager.this.app.getManager(AppRuntime.TICKET_MANAGER);
//                    String uin = appManager.this.app.getCurrentAccountUin();
//                    String pskey = tm.getPskey(uin, domain);
//                    String cookie = CMD_PARAM_CRSF + "=" + csrf +"; p_skey=" + pskey +"; uin=o" + uin + "; p_uin=o" + uin;
//                    conn.setRequestProperty("Cookie", cookie);
//                    OutputStream os = conn.getOutputStream();
//                    DataOutputStream writer = new DataOutputStream(os);
//                    writer.writeBytes(args.toString());
//                    writer.flush();
//                    writer.close();
//                    os.close();
//
//                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//                    StringBuffer sb = new StringBuffer();
//                    String line = null;
//                    while ((line = br.readLine()) != null) {
//                        sb.append(line);
//                    }
//
//                    QidianLog.d(TAG, QidianLog.USR, "http request succeeded,adb url = " + requestUrl);
//                    if (cb != null) {
//                        cb.onFinished(sb.toString());
//                    }
//                }
//                catch (JSONException jep) {
//                    QidianLog.d(TAG, QidianLog.USR, "params is not a json string, url = " + requestUrl);
//                }
//                catch (MalformedURLException meo) {
//                    QidianLog.d(TAG, QidianLog.USR, "http request, url invalid, url = " + requestUrl);
//                }
//                catch (ProtocolException peo) {
//                    QidianLog.d(TAG, QidianLog.USR, "http request, protocol invalid, url = " + requestUrl);
//                }
//                catch (IOException ioe) {
//                    QidianLog.d(TAG, QidianLog.USR, "http request, ioexception");
//                }
//
//            }
//        }, ThreadPriority.NORMAL, null, false);
//    }

    public interface HttpsRequestCallback {
        void onFinished(String res);
    }
}
