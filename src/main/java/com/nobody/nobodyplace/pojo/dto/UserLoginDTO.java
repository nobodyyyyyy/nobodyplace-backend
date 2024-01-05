package com.nobody.nobodyplace.pojo.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserLoginDTO implements Serializable {

    private String username;
    private String password;

}
