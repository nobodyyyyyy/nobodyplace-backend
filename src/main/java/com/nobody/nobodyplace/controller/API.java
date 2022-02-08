package com.nobody.nobodyplace.controller;

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

    // csgo related
    private static final String CSGO = "/api/csgo";

    /**
     * post
     * 入参: cookie
     * 出参: {@link com.nobody.nobodyplace.response.Result 没有 data}
     */
    public static final String UPDATE_BUFF_COOKIE = CSGO + "/update_cookie";

    /**
     * get
     * 获取用户（我）所持有的物品列表
     * 入参: none
     * 出参: infos: [{@link com.nobody.nobodyplace.entity.csgo.CsgoItem}, ...]
     *
     */
    public static final String GET_USER_PROPERTY = CSGO + "/get_user_property";

    /**
     * post
     *（批量）获取物品 id 的详细信息
     * 入参: [id0, id1, ...]
     * 出参: [{id: id, type: 类型（如M9等）, name: 皮肤名字, wear_type: String 磨损类型, icon_url: 展示图片地址, is_stat_trak: 0/1 是否携带计数器}, {...}]
     */
    public static final String BATCH_GET_ITEM_DETAIL = CSGO + "/batch_get_item_detail";

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
     * （单个）添加交易记录，此操作会影响到 1）收入状态变化；2）交易流水变化
     * 目前回包只会提供更新总收入状态的 data，所以查看流水详情的时候要调相应接口更新
     * 入参: {id: id, time: 交易时间戳（秒），精确到天就可以了, price: 涉及金额, type: 0:租, 1:卖}
     * 出参: 当天的累计 {from: today, to: today, addUps: [{@link com.nobody.nobodyplace.entity.csgo.CsgoIncomeAddup}]}
     */
    public static final String ADD_USER_TRANSACTION = CSGO + "/add_user_transaction";

    /**
     * post
     * 查看用户交易记录
     * 入参: {from: 开始时间戳（秒）, to: 结束}
     * 出参: records: [
     *  {
            {@link com.nobody.nobodyplace.entity.csgo.CsgoUserTransaction}
     *  },
     *  ...
     * ]
     */
    public static final String GET_USER_TRANSACTION = CSGO + "/get_user_transaction";

    /**
     * post
     * 获取累计收入状态
     * 入参: {from: 开始时间戳, to: 结束时间戳, type: 0租赁,1卖出,2潜在,3总共}
     * 出参: {from: from, to: to, addUps: [{@link com.nobody.nobodyplace.entity.csgo.CsgoIncomeAddup}, ...]}
     */
    public static final String GET_INCOME_STATUS = CSGO + "/get_income_status";


}
