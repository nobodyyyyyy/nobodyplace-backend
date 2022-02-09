package com.nobody.nobodyplace.service;

import com.nobody.nobodyplace.dao.CommonStorageDAO;
import com.nobody.nobodyplace.entity.CommonStorage;
import org.springframework.stereotype.Service;

@Service
public class CommonStorageService {
    final CommonStorageDAO commonStorageDAO;

    public static final String BUFF_COOKIE = "buff_cookie";
    public static final String YOYO_COOKIE = "yoyo_authorization";
    public static final String ADDUP_CAL_TIME = "addup_calculate_time";

    public CommonStorageService(CommonStorageDAO commonStorageDAO) {
        this.commonStorageDAO = commonStorageDAO;
    }

    public String get(String key) {
        try {
            return commonStorageDAO.getById(key).getValue();
        } catch (Exception e) {
            return "";
        }
    }

    public void set(String key, String value) {
        commonStorageDAO.save(new CommonStorage(key, value));
    }

    public String getBuffCookie() {
        return commonStorageDAO.getById("buff_cookie").getValue();
    }

    public String getYoyoCookie() {
        return commonStorageDAO.getById("yoyo_authorization").getValue();
    }
}
