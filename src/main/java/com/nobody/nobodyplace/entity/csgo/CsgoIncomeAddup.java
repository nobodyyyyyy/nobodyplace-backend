package com.nobody.nobodyplace.entity.csgo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = CsgoIncomeAddup.TABLE_NAME)
@JsonIgnoreProperties({"handler","hibernateLazyInitializer"})
public class CsgoIncomeAddup {

    public final static String TABLE_NAME = "info_csgo_income_addup";

    @Id
    private Integer addupDate;

    private Double overallEarningAddup;

    private Double holdingEarningAddup;

    private Double sellingEarningAddup;

    private Double leaseEarningAddup;

    public Integer getAddupDate() {
        return addupDate;
    }

    public void setAddupDate(Integer addupDate) {
        this.addupDate = addupDate;
    }

    public Double getOverallEarningAddup() {
        return overallEarningAddup;
    }
//
    public void setOverallEarningAddup(Double overallEarningAddup) {
        this.overallEarningAddup = overallEarningAddup;
    }

    public Double getHoldingEarningAddup() {
        return holdingEarningAddup;
    }

    public void setHoldingEarningAddup(Double holdingEarningAddup) {
        this.holdingEarningAddup = holdingEarningAddup;
    }

    public Double getSellingEarningAddup() {
        return sellingEarningAddup;
    }

    public void setSellingEarningAddup(Double sellingEarningAddup) {
        this.sellingEarningAddup = sellingEarningAddup;
    }

    public Double getLeaseEarningAddup() {
        return leaseEarningAddup;
    }

    public void setLeaseEarningAddup(Double leaseEarningAddup) {
        this.leaseEarningAddup = leaseEarningAddup;
    }
}