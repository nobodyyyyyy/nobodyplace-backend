package com.nobody.nobodyplace.crawler;

import com.google.gson.Gson;
import com.nobody.nobodyplace.controller.SearchSuggestionController;
import com.nobody.nobodyplace.gson.csgo.ItemInfosResponse;
import com.nobody.nobodyplace.utils.FileUtil;
import com.nobody.nobodyplace.utils.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.criteria.CriteriaBuilder;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class CSGOCrawler extends Crawler{

    protected static String TAG = "CSGOCrawler";

    private static final Logger Nlog = LoggerFactory.getLogger(CSGOCrawler.class);

    protected static int LIMIT_PER_MIN = 5;
//    private AtomicLong firstRequestTimeStamp = new AtomicLong(0);
//    private AtomicInteger minuteRequests = new AtomicInteger(0);
//
//    protected ThreadPoolExecutor pool = new ThreadPoolExecutor(3, 5, 5,
//            TimeUnit.SECONDS, new LinkedBlockingQueue<>(), Executors.defaultThreadFactory(),
//            new ThreadPoolExecutor.CallerRunsPolicy());

    private static final String API_GET_BUFF_GOODS = "https://buff.163.com/api/market/goods?game=csgo&";
    private static final String CATEGORY_KNIFE = "knife";
    private static final String CATEGORY_GLOVE = "hands";
    private static final String CATEGORY_RIFLE = "rifle";
    private static final String CATEGORY_PISTOL = "pistol";
    private String buffCookie = "";
    private static final String SORT_PRICE_DESC = "price.desc";
    private static final String SORT_PRICE_ASC = "price.asc";

    private static int PAGES = 1;


    public CSGOCrawler() {}

    public void getGoods() {
        PAGES = 1;
        String[] types = new String[]{CATEGORY_PISTOL};
        for (String type : types) {
            System.out.println("----------- NEW TYPE -------------");
            for (int i = 1; i <= PAGES; ++i) {
                try {
                    if (i % 5 == 0) {
                        Thread.sleep(5000);
                    }
                    getGoodsInfo(type, i, SORT_PRICE_DESC);
                    System.out.println("[INFO] Done. current page (total " + PAGES + "): " + i + ". current type: " + type);
                    Thread.sleep(5000);
                } catch (Exception e) {
                    System.out.println("[END] current page: " + i + ". current type: " + type);
                    System.out.println(e.toString());
                }
            }
        }
    }

    private void getGoodsInfo(String category, int page, String sort) throws MalformedURLException {
        HashMap<String, String> params = new HashMap<>();
        params.put("category_group", category);
        params.put("page_num", String.valueOf(page));
        params.put("sort_by", sort);
        params.put("use_suggestion", String.valueOf(0));
        String requestUrl = HttpUtil.setUrlParams(API_GET_BUFF_GOODS, params);
        HttpUtil.HttpResponse response = HttpUtil.get(new URL(requestUrl), false, 10000, buffCookie);
        ItemInfosResponse itemInfosResponse = new Gson().fromJson(response.data, ItemInfosResponse.class);
//        List<ItemInfosResponse.ItemInfo> infos = itemInfosResponse.getItemInfos();
        PAGES = itemInfosResponse.getPage();
        FileUtil.writeToCsv(itemInfosResponse.getLabels(), itemInfosResponse.getItemInfosStringArray(),
                "./" + category + ".csv", true);
    }

    public static void main(String[] args) throws MalformedURLException {
        CSGOCrawler crawler = new CSGOCrawler();
//        crawler.getGoodsInfo(CATEGORY_KNIFE, 1, SORT_PRICE_DESC);
        crawler.getGoods();
    }

}
