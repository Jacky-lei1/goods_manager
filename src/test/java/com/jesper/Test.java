package com.jesper;

import com.jesper.email.MailService;
import com.jesper.mapper.UserMapper;
import com.jesper.model.User;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author lvyelanshan
 * @create 2020-01-05 19:11
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class Test {

    @Autowired(required = false)
    private UserMapper userMapper;

    @Autowired
    private MailService mailService;

    @org.junit.Test
    public void activeTest(){
        int i = userMapper.active("admin5");
        System.out.println(i);
    }

    @org.junit.Test
    public void TestSelect(){
        User user = userMapper.selectUserById(15);
        System.out.println("查询到的用户是："+user);

    }

    /**
     * 测试发送邮件
     */
    @org.junit.Test
    public void TestSendMail(){
        mailService.sendSimpleMail("1049649419@qq.com","测试主题","测试内容");
    }
}
