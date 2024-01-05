package com.nobody.nobodyplace.mapper;

import com.nobody.nobodyplace.pojo.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper{
    User getByUsername(@Param("username") String username);

    User getById(@Param("id") Long id);
}
