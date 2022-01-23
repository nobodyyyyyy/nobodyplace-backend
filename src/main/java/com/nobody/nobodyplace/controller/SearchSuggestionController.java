package com.nobody.nobodyplace.controller;

import com.google.gson.Gson;
import com.nobody.nobodyplace.gson.BingSuggestionResponse;
import com.nobody.nobodyplace.response.Result;
import com.nobody.nobodyplace.response.SearchSuggestions;
import com.nobody.nobodyplace.utils.HttpUtil;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
public class SearchSuggestionController {

    private static final Logger Nlog = LoggerFactory.getLogger(SearchSuggestionController.class);

    private static final String GOOGLE_SUGGESTIONS_API_PREFIX = "http://suggestqueries.google.com/complete/search?output=toolbar&hl=zh&q=";
    private static final String BAIDU_SUGGESTIONS_API_PREFIX = "http://suggestion.baidu.com/su?wd=";
    private static final String BING_SUGGESTIONS_API_PREFIX = "http://api.bing.com/qsonhs.aspx?type=cb&q=";

    private static final String SUGGESTION_ENGINE_BAIDU = "suggestion_engine_baidu";
    private static final String SUGGESTION_ENGINE_GOOGLE = "suggestion_engine_google";
    private static final String SUGGESTION_ENGINE_BING = "suggestion_engine_bing";

    public SearchSuggestionController() {

    }

    @CrossOrigin
    @ResponseBody
    @GetMapping(value = NobodyPlaceAPI.WEB_SEARCH_SUGGESTIONS)
    public Result getSearchSuggestions(String engine, long seq, String input) {
        switch (engine) {
            case SUGGESTION_ENGINE_BING:
                return getSuggestionsByEngineBing(seq, input);
            case SUGGESTION_ENGINE_GOOGLE:
                return getSuggestionsByEngineGoogle(seq, input);
            case SUGGESTION_ENGINE_BAIDU:
                return getSuggestionsByEngineBaidu(seq, input);
            default:
                Nlog.info("engine '" + engine + "' unsupported");
                break;
        }
        return new Result(400);
    }

    private Result getSuggestionsByEngineBing(long seq, String input) {
        try {
            long beginTime = System.currentTimeMillis();
            String url = BING_SUGGESTIONS_API_PREFIX + input;

            URL obj = new URL(BING_SUGGESTIONS_API_PREFIX + URLEncoder.encode(input, StandardCharsets.UTF_8));
            HttpUtil.HttpResponse response = HttpUtil.get(obj, false, 1000);

            if (response.code != 200) {
                return new Result(400);
            }
            List<String> suggestions = new ArrayList<>();
            BingSuggestionResponse suggestionResponse = new Gson().fromJson(response.data, BingSuggestionResponse.class);
            for (BingSuggestionResponse.ResultNode node : suggestionResponse.AS.Results) {
                for (BingSuggestionResponse.Suggestion suggestion : node.Suggests) {
                    if (!suggestion.Txt.equals(input)) {
                        suggestions.add(suggestion.Txt);
                    }
                }
            }
            Nlog.info("Successfully handled input = '" + input + "' used " + (System.currentTimeMillis() - beginTime) + "ms");
            return generateSuccessResult(seq, input, suggestions);
        } catch (Exception e) {
            Nlog.info("Handling input = '" + input + "' get Exception: " + e.toString());
            return new Result(400);
        }
    }

    @Deprecated
    // FIXME 乱码
    private Result getSuggestionsByEngineBaidu(long seq, String input) {
        try {
            long beginTime = System.currentTimeMillis();

            URL obj = new URL(BAIDU_SUGGESTIONS_API_PREFIX + URLEncoder.encode(input, StandardCharsets.UTF_8));
            HttpUtil.HttpResponse response = HttpUtil.get(obj, false, 1000);

            if (response.code != 200) {
                // FIXME 代码相同部分合并合并
                return new Result(400);
            }
            String data = response.data;

            // 转码
            // FIXME 乱码～～～～

            List<String> suggestions = new ArrayList<>();
            Pattern pattern = Pattern.compile("\\[(.*?)]");
            Matcher matcher = pattern.matcher(data);
            if (matcher.find()) {
                data = matcher.group(1);
            }
            pattern = Pattern.compile("\"(.*?)\"");
            matcher = pattern.matcher(data);
            while (matcher.find()) {
                suggestions.add(matcher.group(1));
            }
            Nlog.info("Successfully handled input = '" + input + "' used " + (System.currentTimeMillis() - beginTime) + "ms");
            return generateSuccessResult(seq, input, suggestions);
        } catch (Exception e) {
            Nlog.info("Handling input = '" + input + "' get Exception: " + e.toString());
            return new Result(400);
        }
    }

    private Result getSuggestionsByEngineGoogle(long seq, String input) {
        try {
            long beginTime = System.currentTimeMillis();

            URL obj = new URL(GOOGLE_SUGGESTIONS_API_PREFIX + URLEncoder.encode(input, StandardCharsets.UTF_8));
            HttpUtil.HttpResponse response = HttpUtil.get(obj, true,1000);
            if (response.code != 200) {
                return new Result(400);
            }

            // 解析并存储
            String xmlStr = response.data;
            List<String> suggestions = new ArrayList<>();
            Element rootElement = DocumentHelper.parseText(xmlStr).getRootElement();
            List<Element> suggestionElements = rootElement.elements("CompleteSuggestion");
            for (Element e : suggestionElements) {
                suggestions.add(e.element("suggestion").attributeValue("data"));
            }
            Nlog.info("Successfully handled input = '" + input + "' used " + (System.currentTimeMillis() - beginTime) + "ms");
            return generateSuccessResult(seq, input, suggestions);

        } catch (Exception e) {
            Nlog.info("Handling input = '" + input + "' get Exception: " + e.toString());
            return new Result(400);
        }
    }

    private Result generateSuccessResult(long seq, String input, List<String> suggestions) {
        Result result = new Result(200);
        result.data = new SearchSuggestions();
        ((SearchSuggestions) result.data).seq = seq;
        ((SearchSuggestions) result.data).input = input;
        ((SearchSuggestions) result.data).suggestions = suggestions;
        return result;
    }
}
