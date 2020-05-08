package com.fwtai.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * 页面跳转
 * @作者 田应平
 * @版本 v1.0
 * @创建时间 2020-05-07 12:29
 * @QQ号码 444141300
 * @Email service@dwlai.com
 * @官网 http://www.fwtai.com
*/
@Controller
public class PageController{

    @Autowired
    private RestTemplate restTemplate;

    // 登录成功后跳转到这个url,同时从认证中心那拿取已登录的身份信息存入到本子系统的 HttpSession 里
    @GetMapping({"/","/index"})
    public String index(@CookieValue(required = false,value = "token") final Cookie cookie,final HttpSession session){
        if(cookie != null){
            final String token = cookie.getValue();
            if(!StringUtils.isEmpty(token)){
                final String url = "http://login.codeshop.com:9000/user/info?token=" + token;
                final Map map = restTemplate.getForObject(url,Map.class);
                if(map != null && map.size() > 0){
                    session.setAttribute("user",map.get("user"));
                    return "index";
                }else{
                    session.removeAttribute("user");
                    return "index";
                }
            }
        }
        session.removeAttribute("user");
        return "index";
    }
}