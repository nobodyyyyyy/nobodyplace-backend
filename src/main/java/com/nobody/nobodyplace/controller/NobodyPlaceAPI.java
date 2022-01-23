package com.nobody.nobodyplace.controller;

public class NobodyPlaceAPI {

    public static final String LOGIN = "/api/login";
    public static final String WEB_SEARCH_SUGGESTIONS = "/api/get_search_suggestions";
    
    // count down related
    private static final String COUNTDOWN = "/api/countdown";
    public static final String GET_COUNTDOWNS = COUNTDOWN + "/get_all";
    public static final String ADD_COUNTDOWN = COUNTDOWN + "/add";
    public static final String UPDATE_COUNTDOWN = COUNTDOWN + "/update";
    public static final String DELETE_COUNTDOWN = COUNTDOWN + "/delete";
}
