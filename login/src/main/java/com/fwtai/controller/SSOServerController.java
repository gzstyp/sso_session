package com.fwtai.controller;

import com.fwtai.pojo.ClientInfoVo;
import com.fwtai.tool.MockDatabaseUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by wolfcode-lanxw
 */
@Controller
public class SSOServerController {

    //第一步
    @RequestMapping("/checkLogin")
    public String checkLogin(String redirectUrl, HttpSession session,Model model){
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

    /**
     * 登陆功能
    */
    @RequestMapping("/login")
    public String login(String username,String password,String redirectUrl,HttpSession session,Model model){
        if("zhangsan".equals(username)&&"666".equals(password)){
            //账号密码匹配
            //1.创建令牌信息
            String token = UUID.randomUUID().toString();
            //2.创建全局的会话,把令牌信息放入会话中.
            session.setAttribute("token",token);
            //3.需要把令牌信息放到数据库中.
            MockDatabaseUtil.T_TOKEN.add(token);
            //4.重定向到redirectUrl,把令牌信息带上.  http://www.crm.com:8088/main?token=
            model.addAttribute("token",token);
            return "redirect:"+redirectUrl;
        }
        //如果账号密码有误,重新回到登录页面,还需要把redirectUrl放入request域中.
        model.addAttribute("redirectUrl",redirectUrl);
        return "login";
    }
    /**
     * 校验token是否由统一认证中心产生的
     *
     */
    @RequestMapping("/verify")
    @ResponseBody
    public String verifyToken(String token,String clientUrl,String jsessionid){
        if(MockDatabaseUtil.T_TOKEN.contains(token)){
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
        return "false";
    }

    @RequestMapping("/logOut")
    public String logOut(final HttpSession session){
        //销毁全局会话
        session.invalidate();
        return "logOut";
    }
}
