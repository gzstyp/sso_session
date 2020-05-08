package com.fwtai.controller;

import com.fwtai.pojo.User;
import com.fwtai.tool.LoginCacheUser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 登录认证
 * @作者 田应平
 * @版本 v1.0
 * @创建时间 2020-05-07 12:29
 * @QQ号码 444141300
 * @Email service@dwlai.com
 * @官网 http://www.fwtai.com
*/
@Controller
public class LoginController{

    private static Set<User> dbUsers = new HashSet<>(4);
    static {
        dbUsers.add(new User(1,"zhangsan","123456"));
        dbUsers.add(new User(2,"admin","123456"));
        dbUsers.add(new User(3,"user","123456"));
        dbUsers.add(new User(4,"typ","123456"));
    }

    @PostMapping("/user/auth")
    public String login(final User user,final HttpSession session,final HttpServletResponse response){
        final String target = (String)session.getAttribute("target");
        //模拟数据库登录认证
        final Optional<User> optional = dbUsers.stream().filter(dbUser -> dbUser.getUsername().equals(user.getUsername()) && dbUser.getPassword().equals(user.getPassword())).findFirst();
        //判断用户是否存在
        if(optional.isPresent()){
            final String token = getToken();
            //保存用户登录信息
            LoginCacheUser.loginUser.put(token,optional.get());
            //重定向到 target 地址,响应时写入cookie,todo Cookie具有不可跨域名性
            final Cookie cookie = new Cookie("token",token);// todo 实际项目中同时采用session和cookie一起使用
            cookie.setDomain("codeshop.com");//这个很重要!!!
            cookie.setPath("/");//这个很重要!!!
            cookie.setMaxAge(-1);//该Cookie失效的时间,单位秒;如果为正数,则该Cookie在maxAge秒之后失效;如果为负数,该Cookie为临时Cookie,关闭浏览器即失效,浏览器也不会以任何形式保存该Cookie.如果为0;表示删除该Cookie,默认为–1
            response.addCookie(cookie);
            return "redirect:"+target;
        }else {
            //登录失败处理
            session.setAttribute("msg","账号或密码错误");
            return "login";
        }
    }

    @GetMapping("/user/logout")
    public String logout(@RequestParam(required = false,defaultValue = "") String target,final HttpServletRequest request,@CookieValue(required = false,value = "token") final Cookie cookie){
        if(target == null || target.length() <= 0){
            target = "http://www.codeshop.com:9010/";
        }
        if(cookie !=null){
            final String value = cookie.getValue();
            //如果已登录那直接重定向原url即可
            if(!StringUtils.isEmpty(value)){
                LoginCacheUser.loginUser.remove(value);
                final Cookie[] cookies = request.getCookies();
                if(cookies != null){
                    for(int i = 0; i < cookies.length; i++){
                        cookies[i].setValue(null);
                        cookies[i].setMaxAge(0);
                    }
                }
                return "redirect:"+target;
            }
        }
        return "redirect:"+target;
    }

    // 验证token是否有效
    @GetMapping("/user/info")
    @ResponseBody
    public Map<String,Object> getUserInfo(final String token){
        final Map<String,Object> map = new HashMap<>();//防止空指针
        if(!StringUtils.isEmpty(token)){
            final User user = LoginCacheUser.loginUser.get(token);
            if(user != null){
                map.put("user",user);
                return map;
            }
        }
        return map;
    }

    private String getToken(){
        final ThreadLocalRandom random = ThreadLocalRandom.current();
        return new UUID(random.nextInt(),random.nextInt()).toString().replaceAll("-","");
    }
}