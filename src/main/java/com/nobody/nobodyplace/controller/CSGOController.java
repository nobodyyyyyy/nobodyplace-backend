package com.nobody.nobodyplace.controller;

import com.google.gson.Gson;
import com.nobody.nobodyplace.gson.csgo.MarketItemInfo;
import com.nobody.nobodyplace.service.CsgoService;
import com.nobody.nobodyplace.utils.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.util.HashMap;

@Controller
public class CSGOController {
    private static final Logger Nlog = LoggerFactory.getLogger(CSGOController.class);

    final CsgoService service;

    private static String cookie;

    private static final String API_GET_ITEM_ID = "https://buff.163.com/api/market/search/suggest?game=csgo";
    private static final String API_GET_ITEM_PAST_MONTH_TRANSACTION = "https://buff.163.com/api/market/goods/price_history/buff?game=csgo";
    private static final String API_GET_ITEM_RECENT_TRANSACTION = "https://buff.163.com/api/market/goods/bill_order?game=csgo";

    public CSGOController(CsgoService service) {
        this.service = service;
    }

    private void getCurrentCookie() {

    }

    private void getCurrentMarketInfo(int itemId) {

    }

    private void getHistoryMarketInfo(int itemId) {

    }

    /**
     * 格式化用户输入，以正确格式去查询 buff 商品 id
     * @param itemType 商品类型
     * @param skinName 皮肤名称
     * @param wear 磨损
     * @return 格式化查询文本
     */
    private String formatUserInput(String itemType, String skinName, String wear) {
        return "";
    }

    /**
     * 使用格式化后的用户输入文本查商品 id
     * @param input formatUserInput 返回值
     * @return item 对应的 buff id
     */
    private String getItemId(String input) {
        String requestUrl = API_GET_ITEM_ID + input;
        return null;
    }

    public static void main(String[] args) {
        try {
            String url = "https://buff.163.com/api/market/goods/bill_order?game=csgo";
            url = HttpUtil.setUrlParams(url, new HashMap<>(){{put("goods_id", "33812");}});

            String cookie = "_ntes_nnid=11be1748e8c987fa96480cf8855018cb,1631112620339; _ntes_nuid=11be1748e8c987fa96480cf8855018cb; NTES_CMT_USER_INFO=310069990%7C%E6%9C%89%E6%80%81%E5%BA%A6%E7%BD%91%E5%8F%8B0iuQHC%7Chttp%3A%2F%2Fcms-bucket.nosdn.127.net%2F2018%2F08%2F13%2F078ea9f65d954410b62a52ac773875a1.jpeg%7Cfalse%7Cemh1eXVudGlhbm5uQDE2My5jb20%3D; UM_distinctid=17c0de301d6379-08d10bb65c12c-113f6757-13c680-17c0de301d71239; Device-Id=04owwRB4uVfnTUBoVm5i; _ga=GA1.2.804444908.1632670698; NTES_P_UTID=XgghiBlhCVdvfx2GztXzbGu58bGKcj7U|1640926744; nts_mail_user=zhuyuntiannn@163.com:-1:1; usertrack=ezq0J2He1f23iz2WAxzZAg==; game=csgo; Locale-Supported=zh-Hans; _gid=GA1.2.584549684.1642950402; NTES_YD_SESS=yol_z55ViOV_mqMMjQnbk6Tm9Tgq6CTSkxKnjGcajp8M3iTX3xgZlsrJ7Nz0CLI5o6rHShY4eedglP8eawGmzy36s1jblK_.hkFiT1AQplYP49pxX24kzbq0qvtOEyGduVHGVO4lCMAyKoZ4_NtYQAYk4nyNJWbqCYn.p9hfi.a.r2beuMFkizA_kq28AocWmH1zPLPMnxQ.4VPpXA_wUx0CV9QzS23ETtx3PJi2nRrV9; S_INFO=1642954096|0|0&60##|13807886099; P_INFO=13807886099|1642954096|1|netease_buff|00&99|null&null&null#gux&450100#10#0|&0||13807886099; remember_me=U1101328814|wAI5M7D0ddxJJ1gKcSaB6Os2XugthMzt; session=1-4_-d6JSUNkaCJLz5BVCgpi55G6BFYmy91UdLADy3JP6d2037018358; steam_info_to_bind=; _gat_gtag_UA_109989484_1=1; csrf_token=ImNiNDVkYmFjMWNmNGFhMTQ4MGQyNTc0MWUyYzRjZTdjNDllMzgxNmUi.FNBYYQ.jUoHT5BSb1tisR890IK2ruZiNMs";
            URL obj = new URL(url);

            HttpUtil.HttpResponse response = HttpUtil.get(obj, false, 1000, cookie);

            if (response.code != 200) {
                return;
            }
            MarketItemInfo infos = new Gson().fromJson(response.data, MarketItemInfo.class);
            int i = 1 + 1;



        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
