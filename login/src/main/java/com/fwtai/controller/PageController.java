package com.fwtai.controller;

import com.fwtai.pojo.User;
import com.fwtai.tool.LoginCacheUser;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;

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

    //跳转到登录页面
    @GetMapping("/login")
    public String login(@RequestParam(required = false,defaultValue = "") String target,final HttpSession session,@CookieValue(required = false,value = "token") final Cookie cookie){
        if(target == null || target.length() <= 0){
            target = "http://www.codeshop.com:9010/";
        }
        if(cookie !=null){
            final String value = cookie.getValue();
            //如果已登录那直接重定向原url即可
            if(!StringUtils.isEmpty(value)){
                final User user = LoginCacheUser.loginUser.get(value);
                if(user != null){
                    return "redirect:"+target;
                }
            }
        }
        //todo 在正式项目中这个url是要校验 target 是否有效的
        //(存入)重定向url地址,正式项目中这个url是要校验是否有效的
        session.setAttribute("target",target);//把访问登录认证的子系统的url存起来
        return "login";
    }
}