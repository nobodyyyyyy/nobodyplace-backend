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
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Controller
public class SearchSuggestionController {

    private static final String USER_AGENT = "Mozilla/5.0"; // TODO ?
    private static final Logger Nlog = LoggerFactory.getLogger(SearchSuggestionController.class);

    private static final String GOOGLE_SUGGESTIONS_API_PREFIX = "http://suggestqueries.google.com/complete/search?output=toolbar&hl=zh&q=";

    public SearchSuggestionController() {

    }

    @CrossOrigin
    @ResponseBody
    @GetMapping(value = NobodyPlaceAPI.WEB_SEARCH)
    public Result getSearchSuggestions(String input) {

        new Thread(new Runnable() {
            @Override
            public void run() {

                // TODO Auto-generated method stub

            }

        }).start();
        try {
            Nlog.info(">> Before handling input = " + input);
            long beginTime = System.currentTimeMillis();

            String url = GOOGLE_SUGGESTIONS_API_PREFIX + input;
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", USER_AGENT); // TODO

            int responseCode = con.getResponseCode();
            if (responseCode != 200) {
                return new Result(400);
            }
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String flow;
            StringBuilder sb = new StringBuilder();
            while ((flow = in.readLine()) != null) {
                sb.append(flow);
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

            Nlog.info("handled input = " + input + " used " + (System.currentTimeMillis() - beginTime) + "ms");

            return result;

        } catch (Exception e) {
            return new Result(400);
        }
    }
}
