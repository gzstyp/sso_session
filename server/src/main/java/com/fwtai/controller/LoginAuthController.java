package com.fwtai.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.UUID;

/**
 * @作者 田应平
 * @版本 v1.0
 * @创建时间 2020-05-08 10:24
 * @QQ号码 444141300
 * @Email service@dwlai.com
 * @官网 http://www.fwtai.com
*/
@RestController
@RequestMapping("/auth")
public class LoginAuthController{

    @Autowired
    private HttpServletRequest request;

    //第一步
    @RequestMapping("/checkLogin")
    public String checkLogin(String redirectUrl,HttpSession session,Model model){
        //1.判断是否有全局的会话
        String token = (String) session.getAttribute("token");
        if(StringUtils.isEmpty(token)){
            //表示没有全局会话
            //跳转到统一认证中心的登陆页面.
            model.addAttribute("redirectUrl",redirectUrl);
            return "login";
        }else{
            //有全局会话
            //取出令牌信息,重定向到redirectUrl,把令牌带上  http://www.wms.com:8089/main?token
            model.addAttribute("token",token);
            return "redirect:"+redirectUrl;
        }
    }

    //第2步，第一步完成后，登录认证
    @PostMapping("/login")
    public String login(final HttpServletResponse response,String username,String password,String redirectUrl,HttpSession session,Model model){
        if("zhangsan".equals(username)&&"666".equals(password)){
            //账号密码匹配
            //1.创建令牌信息
            String token = UUID.randomUUID().toString();
            //2.创建全局的会话,把令牌信息放入会话中.
            session.setAttribute("token",token);
            //3.需要把令牌信息放到数据库中.
            //MockDatabaseUtil.T_TOKEN.add(token);
            //4.重定向到redirectUrl,把令牌信息带上.  http://www.crm.com:8088/main?token=
            model.addAttribute("token",token);
            return "redirect:"+redirectUrl;
        }
        //如果账号密码有误,重新回到登录页面,还需要把redirectUrl放入request域中.
        model.addAttribute("redirectUrl",redirectUrl);
        return "login";
    }

    //第3步,验证token
    @PostMapping("/verify")
    public String verify(final HttpServletResponse response,String token,String clientUrl,String jsessionid){
        /*if(MockDatabaseUtil.T_TOKEN.contains(token)){
            //把客户端的登出地址记录
            List<ClientInfoVo> clientInfoList = MockDatabaseUtil.T_CLIENT_INFO.get(token);
            if(clientInfoList==null){
                clientInfoList = new ArrayList<ClientInfoVo>();
                MockDatabaseUtil.T_CLIENT_INFO.put(token,clientInfoList);
            }
            ClientInfoVo vo = new ClientInfoVo();
            vo.setClientUrl(clientUrl);
            vo.setJsessionid(jsessionid);
            clientInfoList.add(vo);
            //说明令牌有效,返回true
            return "true";
        }
        return "false";*/
        return null;
    }

    //第4步，注销退出
    @GetMapping("/logout")
    public String logout(final HttpServletResponse response,final HttpSession session){
        //销毁全局会话
        session.invalidate();
        return "logOut";
    }

}