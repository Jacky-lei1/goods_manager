package com.jesper.sms.impl;

import com.jesper.sms.SendCode;
import com.jesper.sms.util.SmsSingleSender;
import com.jesper.sms.util.SmsSingleSenderResult;
import org.springframework.stereotype.Service;

/**
 * @author lvyelanshan
 * @create 2020-01-05 21:26
 */
@Service
public class SendCodeImpl implements SendCode {
    @Override
    public void sendCode(String telephone, String code) {
        try {
            //请根据实际 accesskey 和 secretkey 进行开发，以下只作为演示 sdk 使用
            String accesskey = "5de9f11befb9a335db39c3f6";
            String secretkey = "deb187193d6d4bf0aeee6eb650fbaf42";

            //初始化单发
            SmsSingleSender singleSender = new SmsSingleSender(accesskey, secretkey);
            SmsSingleSenderResult singleSenderResult;

            //"【绿野蓝杉】爱您的雷爸爸为您准备了一份惊喜！请登陆http://lvyelanshan.ngrok2.xiaomiqiu.cn/查看，您的专属验证码为："+random

            //普通单发,注意前面必须为【】符号包含，置于头或者尾部。
            singleSenderResult = singleSender.send(0, "86", telephone, "【绿野科技】尊敬的绿野会员：您的验证码为："+code+"，工作人员不会索取，请勿泄漏！", "", "");
            System.out.println(singleSenderResult);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
