package com.fwtai.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @作者 田应平
 * @版本 v1.0
 * @创建时间 2020-05-07 12:23
 * @QQ号码 444141300
 * @Email service@dwlai.com
 * @官网 http://www.fwtai.com
*/
@Data//生成get和set方法
@NoArgsConstructor//无参的构造方法
@AllArgsConstructor//全参的构造方法
@Accessors(chain = true)//链式调用
public class User{

    private Integer id;

    private String username;

    private String password;
}