package com.fwtai.config;

import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;

/**
 * 自定义拦截器,Spring boot拦截器,其作用是进入到控制器Controller的请求之前进行调用[认证 implements HandlerInterceptor 和菜单权限拦截 extends HandlerInterceptorAdapter应该分开,各执其职]
 * @作者 田应平
 * @版本 v1.0
 * @创建时间 2017-11-26 13:58
 * @QQ号码 444141300
 * @官网 http://www.fwtai.com
*/
public class AuthInterceptor implements HandlerInterceptor{

    /**只有返回true才会继续向下执行，返回false取消当前请求*/
    @Override
    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler) throws Exception {
        if(handler instanceof ResourceHttpRequestHandler){return true;}//静态资源配置
        final HttpSession session = request.getSession();
        //1.判断是否有局部的会话
        Boolean isLogin = (Boolean) session.getAttribute("isLogin");
        if(isLogin!=null && isLogin){
            //有局部会话,直接放行
            return true;
        }
        //判断地址栏中是否有携带token参数.
        final String token = request.getParameter("token");
        if(!StringUtils.isEmpty(token)){
            //token信息不为null,说明地址中包含了token,拥有令牌.
            //验证token信息是否由认证中心产生的.
            final String httpURL = "http://www.sso.com:8443/verify";
            final HashMap<String,String> params = new HashMap<String,String>();
            params.put("token", token);
            params.put("clientUrl","http://www.crm.com:8088");
            params.put("jsessionid", session.getId());
            try {
                //请求验证token信息是否由认证中心产生的
                final String isVerify = requestPost(httpURL,params);
                if("true".equals(isVerify)){
                    //如果返回的字符串是true,说明这个token是由统一认证中心产生的,创建局部的会话.
                    session.setAttribute("isLogin", true);
                    //放行该次的请求
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //没有局部会话,重定向到统一认证中心,检查是否有其他的系统已经登录过.
        // http://www.sso.com:8443/checkLogin?redirectUrl=http://www.crm.com:8088
        redirectToSSOURL(request,response);
        return false;
    }

    public final String requestPost(final String uri,final HashMap<String,String> params){
        try {
            final URL url = new URL(uri);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            final StringBuffer sb = new StringBuffer();
            if(params !=null && params.size() > 0){
                for(final String key : params.keySet()){
                    sb.append(key + "=" + params.get(key) + "&");
                }
            }
            conn.getOutputStream().write(String.valueOf(sb).getBytes(Charset.forName("UTF-8")));
            conn.connect();
            final InputStream stream = conn.getInputStream();
            return StreamUtils.copyToString(stream,Charset.forName("UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 根据request获取跳转到统一认证中心的地址 http://www.sso.com:8443//checkLogin?redirectUrl=http://www.crm.com:8088/main
     * 通过Response跳转到指定的地址
     * @param request
     * @param response
     */
    public final void redirectToSSOURL(final HttpServletRequest request,final HttpServletResponse response) throws IOException{
        final String redirectUrl = getRedirectUrl(request);
        final StringBuilder url = new StringBuilder(50)
            .append("http://www.sso.com:8443")
            .append("/checkLogin?redirectUrl=")
            .append(redirectUrl);
        response.sendRedirect(url.toString());
    }

    public final String getRedirectUrl(final HttpServletRequest request){
        //获取请求URL
        return "http://www.crm.com:8088"+request.getServletPath();
    }
}