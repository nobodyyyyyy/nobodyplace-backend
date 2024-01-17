package com.nobody.nobodyplace.oldpojo.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = CommonStorage.TABLE_NAME)
public class CommonStorage {

    public static final String TABLE_NAME = "info_common_storage";

    @Id
    private String key;

    private String value;

    public CommonStorage() {

    }

    public CommonStorage(String k, String v) {
        this.key = k;
        this.value = v;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key == null ? null : key.trim();
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value == null ? null : value.trim();
    }
}
