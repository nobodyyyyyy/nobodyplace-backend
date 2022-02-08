package com.nobody.nobodyplace.service;

import com.nobody.nobodyplace.dao.CommonStorageDAO;
import com.nobody.nobodyplace.entity.CommonStorage;
import org.springframework.stereotype.Service;

@Service
public class CommonStorageService {
    final CommonStorageDAO commonStorageDAO;

    public CommonStorageService(CommonStorageDAO commonStorageDAO) {
        this.commonStorageDAO = commonStorageDAO;
    }

    public void updateBuffCookie(String cookie) {
        commonStorageDAO.save(new CommonStorage("buff_cookie", cookie));
    }

    public String getBuffCookie() {
        return commonStorageDAO.getById("buff_cookie").getValue();
    }

    public String getYoyoCookie() {
        return commonStorageDAO.getById("yoyo_authorization").getValue();
    }

    public void deleteBuffCookie() {
        this.commonStorageDAO.deleteById("buff_cookie");
    }
}
