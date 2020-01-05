package com.jesper.sms;

/**
 * @author lvyelanshan
 * @create 2020-01-05 21:24
 */
public interface SendCode {
    /**
     * 发送短信验证码
     */
    void sendCode(String telephone,String code);

}
