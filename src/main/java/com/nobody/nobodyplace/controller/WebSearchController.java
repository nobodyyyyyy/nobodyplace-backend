package com.nobody.nobodyplace.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Controller
public class WebSearchController {

    private static final String USER_AGENT = "Mozilla/5.0"; // TODO ?


    public WebSearchController() {

    }

    @CrossOrigin
    @GetMapping(value = NobodyPlaceAPI.WEB_SEARCH)
    @ResponseBody
    public List<String> getSearchSuggestions(@RequestBody String input) {
//        HttpUtil.httpsGetData(input, new HttpUtil.HttpsRequestCallback() {
//            @Override
//            public void onFinished(String res) {
//
//            }
//        });

        System.out.println("11111");
        String result = null;
        try {

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
        } catch (Exception e) {

        } finally {

        }

        if (result == null || result.isEmpty()) {
            return new ArrayList<>();
        }

        return new ArrayList<>();



    }
}
