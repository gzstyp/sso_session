package com.fwtai.tool;

import com.fwtai.pojo.User;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @作者 田应平
 * @版本 v1.0
 * @创建时间 2020-05-07 14:45
 * @QQ号码 444141300
 * @Email service@dwlai.com
 * @官网 http://www.fwtai.com
*/
@Component
public class LoginCacheUser{

    //存入token和对应的User
    public static Map<String,User> loginUser = new HashMap<>();
}