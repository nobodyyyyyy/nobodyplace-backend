package com.nobody.nobodyplace.utils;

import javax.persistence.AttributeConverter;
import java.util.Date;

public class DateConverter implements AttributeConverter<Date, Long> {

    @Override
    public Long convertToDatabaseColumn(Date date) {
        return date.getTime();
    }

    @Override
    public Date convertToEntityAttribute(Long aLong) {
        return new Date(aLong);
    }
}
