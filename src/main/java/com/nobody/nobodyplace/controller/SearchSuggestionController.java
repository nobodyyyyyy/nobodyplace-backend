package com.nobody.nobodyplace.controller;

import com.nobody.nobodyplace.response.Result;
import com.nobody.nobodyplace.response.SearchSuggestions;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

@Controller
public class SearchSuggestionController {

    private static final Logger Nlog = LoggerFactory.getLogger(SearchSuggestionController.class);

    private static final String GOOGLE_SUGGESTIONS_API_PREFIX = "http://suggestqueries.google.com/complete/search?output=toolbar&hl=zh&q=";

    // 为了不在 ShadowSocks 中选择全局代理也能访问外网……
    private static final String LOCAL_HOST = "127.0.0.1";
    private static final int PROXY_PORT = 1087;

    public SearchSuggestionController() {

    }

    @CrossOrigin
    @ResponseBody
    @GetMapping(value = NobodyPlaceAPI.WEB_SEARCH)
    public Result getSearchSuggestions(String input) {
        try {
            long beginTime = System.currentTimeMillis();

            String url = GOOGLE_SUGGESTIONS_API_PREFIX + input;
            URL obj = new URL(url);
            Proxy proxy = new Proxy(java.net.Proxy.Type.HTTP, new InetSocketAddress(LOCAL_HOST, PROXY_PORT));
            HttpURLConnection con = (HttpURLConnection) obj.openConnection(proxy);
            con.setRequestMethod("GET");
            con.setConnectTimeout(1000);

            int responseCode = con.getResponseCode();
            if (responseCode != 200) {
                Nlog.info("Handled input = '" + input + "' error, get responseCode = " + responseCode);
                return new Result(400);
            }
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = in.readLine()) != null) {
                sb.append(line);
            }
            in.close();

            // 解析并存储
            String xmlStr = sb.toString();
            List<String> suggestions = new ArrayList<>();
            Element rootElement = DocumentHelper.parseText(xmlStr).getRootElement();
            List<Element> suggestionElements = rootElement.elements("CompleteSuggestion");
            for (Element e : suggestionElements) {
                suggestions.add(e.element("suggestion").attributeValue("data"));
            }
            Result result = new Result(200);
            result.data = new SearchSuggestions();
            ((SearchSuggestions) result.data).input = input;
            ((SearchSuggestions) result.data).suggestions = suggestions;

            Nlog.info("Successfully handled input = '" + input + "' used " + (System.currentTimeMillis() - beginTime) + "ms");

            return result;

        } catch (Exception e) {
            Nlog.info("Handling input = '" + input + "' get Exception: " + e.toString());
            return new Result(400);
        }
    }
}
