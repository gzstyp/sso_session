package com.fwtai.config;

import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 * @作者 田应平
 * @版本 v1.0
 * @创建时间 2020-05-08 10:06
 * @QQ号码 444141300
 * @Email service@dwlai.com
 * @官网 http://www.fwtai.com
*/
@Component
public class SessionListener implements HttpSessionListener{
    @Override
    public void sessionCreated(HttpSessionEvent se) {

    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        final HttpSession session = se.getSession();
        final String token = (String) session.getAttribute("token");
        /*//删除t_token表中的数据
        MockDatabaseUtil.T_TOKEN.remove(token);
        final List<ClientInfoVo> clientInfoVoList = MockDatabaseUtil.T_CLIENT_INFO.remove(token);
        try{
            for(ClientInfoVo vo:clientInfoVoList){
                //获取出注册的子系统,依次调用子系统的登出的方法
                HttpUtil.sendHttpRequest(vo.getClientUrl(),vo.getJsessionid());
            }
        }catch(Exception e){
            e.printStackTrace();;
        }*/
    }
}