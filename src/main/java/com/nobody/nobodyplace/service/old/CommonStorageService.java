package com.nobody.nobodyplace.service.old;

import com.nobody.nobodyplace.oldpojo.dao.CommonStorageDAO;
import com.nobody.nobodyplace.oldpojo.entity.CommonStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CommonStorageService {
    final CommonStorageDAO commonStorageDAO;

    private static final Logger Nlog = LoggerFactory.getLogger(CommonStorageService.class);

    public static final String BUFF_COOKIE = "buff_cookie";
    public static final String YOYO_COOKIE = "yoyo_authorization";

    public CommonStorageService(CommonStorageDAO commonStorageDAO) {
        this.commonStorageDAO = commonStorageDAO;
    }

    public String get(String key) {
        try {
            return commonStorageDAO.getById(key).getValue();
        } catch (Exception e) {
            Nlog.info("get... key(" + key + ") doesn't exist");
            return "";
        }
    }

    public void set(String key, String value) {
        commonStorageDAO.save(new CommonStorage(key, value));
    }

    public String getBuffCookie() {
        return commonStorageDAO.getById(BUFF_COOKIE).getValue();
    }

    public String getYoyoCookie() {
        return commonStorageDAO.getById(YOYO_COOKIE).getValue();
    }
}
