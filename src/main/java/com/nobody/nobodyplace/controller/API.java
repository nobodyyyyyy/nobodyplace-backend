package com.nobody.nobodyplace.controller;

import com.nobody.nobodyplace.oldpojo.entity.csgo.CsgoItem;
import com.nobody.nobodyplace.oldpojo.entity.csgo.CsgoUserTransaction;
import com.nobody.nobodyplace.response.old.ResultPast;
import com.nobody.nobodyplace.response.old.csgo.IncomeStatusData;

public class API {

    // 本项目时间戳全部传「秒」级别

    public static final String LOGIN = "/api/login";
    public static final String WEB_SEARCH_SUGGESTIONS = "/api/get_search_suggestions";

    // count down related
    private static final String COUNTDOWN = "/api/countdown";
    public static final String GET_COUNTDOWNS = COUNTDOWN + "/get_all";
    public static final String ADD_COUNTDOWN = COUNTDOWN + "/add";
    public static final String UPDATE_COUNTDOWN = COUNTDOWN + "/update";
    public static final String DELETE_COUNTDOWN = COUNTDOWN + "/delete";

    // websocket related
    public static final String WEB_SOCKET_END_POINT = "/notification";

    // csgo related
    private static final String CSGO = "/api/csgo";

    /**
     * post
     * 入参: cookie
     * 出参: {@link ResultPast 没有 data}
     */
    public static final String UPDATE_BUFF_COOKIE = CSGO + "/update_buff_cookie";

    /**
     * post
     * 入参: cookie
     * 出参: {@link ResultPast 没有 data}
     */
    public static final String UPDATE_YOYO_COOKIE = CSGO + "/update_yoyo_cookie";

    /**
     * get
     * 获取用户（我）所持有的物品列表
     * 入参: none
     * 出参: infos: [{@link CsgoItem}, ...]
     *
     */
    public static final String GET_USER_PROPERTY = CSGO + "/get_user_property";

    /**
     * post
     *（单个）获取物品 id 对应的最近成交记录
     * 入参: id
     * 出参: [{price: 成交金额, wear: 磨损, fade: 渐变百分比（若有）, time: 成交时间, }, ...]
     */
    public static final String BATCH_GET_ITEM_RECENT_PRICE = CSGO + "/batch_get_item_recent_price";

    /**
     * post
     * （批量）获取物品 id 对应的历史售价
     * 入参: [{id: id, from: 开始时间戳, to: 结束时间戳}, ...]
     * 出参: prices: [{id: id, price: [[time0, price0], [time1, price1], ...]}, ...]
     */
    public static final String BATCH_GET_ITEM_HISTORY_PRICE = CSGO + "/batch_get_item_history_price";

    /**
     * post
     * （单个）获取物品 id 对应的当前在售
     * 入参: id
     * 出参: {id: id, info: [{price: price, wear: wear, fade: fade_percent, added_time: 上架时间, stickers: 贴纸列表], ...}
     */
    public static final String GET_ITEM_CURRENT_PRICE = CSGO + "/get_item_current_price";

    /**
     * post
     * 查看用户交易记录
     * 入参: {from: 开始时间戳（秒）, to: 结束, fetch: 是否强制走悠悠api更新，否则是单纯的读 db (0/1)}
     * 出参: records: [
     *  {
            {@link CsgoUserTransaction}
     *  },
     *  ...
     * ]
     */
    public static final String GET_USER_TRANSACTION = CSGO + "/get_user_transaction";

    /**
     * post
     * 获取累计收入状态
     * 入参: {from: 开始时间戳, to: 结束时间戳, type: 0租赁,1卖出,2潜在}
     * 出参: {@link IncomeStatusData.LeaseStatus}
     */
    public static final String GET_INCOME_STATUS = CSGO + "/get_income_status";

    // 上述都不知道啥时候写的了，

    // 新加的在下面 2024/1/4
    public static final String GET_ITEM_INFOS = CSGO + "/item_infos";

    public static final String ADD_USER_ITEM = CSGO + "/add_user_item";

    public static final String GET_USER_ITEMS = CSGO + "/get_user_items";

    public static final String GET_USER_ITEMS_WITH_UPDATE_PRICE = CSGO + "/get_user_items_with_update_price";

    public static final String DELETE_USER_ITEM = CSGO + "/del_user_item";

    // 真实交易相关
    public static final String GET_ITEM_PRICE = CSGO + "/get_item_price";

    public static final String GET_INVENTORY_STATUS = CSGO + "/inventory_status";

    public static final String GET_RANKING = CSGO + "/ranking";
}
