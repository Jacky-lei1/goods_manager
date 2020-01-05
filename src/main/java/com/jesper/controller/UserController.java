package com.jesper.controller;

import com.jesper.email.MailService;
import com.jesper.mapper.UserMapper;
import com.jesper.model.User;
import com.jesper.sms.SendCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;

/**
 * 用户管理
 */
@Controller
public class UserController {

    @Autowired(required = false)
    private UserMapper userMapper;

    @Autowired
    private HttpSession httpSession;

    @Autowired
    private MailService mailService;

    @Autowired
    private SendCode sendCode;


    @Autowired
    private JavaMailSender mailSender; //自动注入的Bean

    @Value("${spring.mail.username}")
    private String Sender; //读取配置文件中的参数


    /**
     * 登录跳转
     *
     * @param
     * @return
     */
    @GetMapping("/")
    public String loginGet1() {
        //重定向到登陆页面
        return "redirect:/user/login";
    }


    /**
     * 登录跳转
     *
     * @param model
     * @return
     */
    @GetMapping("/user/login")
    public String loginGet(Model model) {
        return "login";
    }

    /**
     * 登录
     *
     * @param
     * @param model
     * @param
     * @return
     */
    @PostMapping("/user/login")
    public String loginPost(User user, Model model) {
        User user1 = userMapper.selectByNameAndPwd(user); //根据用户名和密码查询是否匹配
        if (user1 != null) {
            httpSession.setAttribute("user", user1); //将当前登陆的用户保存到Session中
            User name = (User) httpSession.getAttribute("user");
            return "redirect:dashboard";
        } else {
            model.addAttribute("error", "用户名或密码错误，请重新登录！");
            return "login"; //登陆失败重新跳转到登陆页面
        }
    }

    /**
     * 注册跳转
     *
     * @param model
     * @return
     */
    @GetMapping("/user/register")
    public String register(Model model) {
        return "register";
    }

    /**
     * 注册
     *
     * @param model
     * @return
     */
    @PostMapping("/user/register")
    public String registerPost(User user, Model model, HttpServletRequest request,String code) {
         String host = null;
        System.out.println("用户信息:"+user);
        try {
            //从session中取出验证码
            HttpSession session = request.getSession();
            String code1 = (String) session.getAttribute("code");
            if(!code.equals(code1)){
                return "YzmError";
            }


            userMapper.selectIsName(user); //返回值为基本数据类型，不能为空，所有这里不能书写返回值，如果查询到了则不为空，如果查询不到则为空，出现异常。
            model.addAttribute("error", "该账号已存在！");
        } catch (Exception e) {
            Date date = new Date();
            user.setAddDate(date);
            user.setUpdateDate(date);
            userMapper.insert(user); //在UserMapper.xml中控制User对象的属性插入
            //判断请求地址
            if (request.getLocalAddr().equals("0:0:0:0:0:0:0:1")){
                host="localhost";
            }else {
                host=request.getLocalAddr();
            }
            System.out.println("host地址为："+host);
            //单独的激活链接
            StringBuilder url = new StringBuilder()
                    .append("http://")
                    .append(host)
                    .append(":")
                    .append(request.getLocalPort())
                    .append("/user/active")
                    .append("?userName=")
                    .append(user.getUserName());
            System.out.println("完整的URL："+url);
            //发送邮件
            mailService.sendSimpleMail(user.getEmail(),"LY后台管理账户激活邮件","请点击此链接完成激活："+url);
            System.out.println("注册成功");
            model.addAttribute("error", "恭喜您，注册成功！请前往邮箱激活账户！");
            return "login";
        }

        return "register";
    }

    /**
     * 激活账号
     * @param userName 用户名
     * @return
     */
    @RequestMapping("/user/active")
    @ResponseBody
    public String userActive(String userName){
        System.out.println("用户激活");
        int result = userMapper.active(userName);
        if (result>0){
            return "账户激活成功，请返回重新登录！";
        }else {
            return "激活失败，请重新发送激活邮件！";
        }
    }

    /**
     * 忘记密码跳转
     *
     * @param model
     * @return
     */
    @GetMapping("/user/forget")
    public String forgetGet(Model model) {
        return "forget";
    }

    /**
     * 忘记密码
     * @param
     * @param model
     * @param
     * @return
     */
    @PostMapping("/user/forget")
    public String forgetPost(User user, Model model) {
        String password = userMapper.selectPasswordByName(user); //根据用户名查询密码
        if (password == null) {
            model.addAttribute("error", "帐号不存在或邮箱不正确！");
        } else {
//            String email = user.getEmail();
//            SimpleMailMessage message = new SimpleMailMessage();
//            message.setFrom(Sender);
//            message.setTo(email); //接收者邮箱
//            message.setSubject("LY后台信息管理系统-密码找回");
//            StringBuilder sb = new StringBuilder();
//            sb.append(user.getUserName() + "用户您好！您的注册密码是：" + password + "。感谢您使用LY信息管理系统！");
//            message.setText(sb.toString());
//            mailSender.send(message);

            mailService.sendSimpleMail(user.getEmail(),"LY后台信息管理系统-密码找回",user.getUserName()+
                    "尊敬的用户您好，您的账号密码是："+password
                    +",打死也不能告诉其他人噢！感谢您使用LY信息管理系统！");
            model.addAttribute("error", "密码已发到您的邮箱,请查收！");
        }
        return "login";

    }

    /**
     * 发送短信验证码
     * @param telephone
     * @return
     */
    @RequestMapping("/user/code/{telephone}")
    @ResponseBody
    public Boolean showcode(@PathVariable("telephone") String telephone,HttpServletRequest req){
        String code = (int)((Math.random()*9+1)*100000)+""; //随机生成6位验证码
        HttpSession session = req.getSession();
        session.setAttribute("code",code);  //将短信验证码保存到session中
        System.out.println("随机短信验证码是："+code);
        try {
            System.out.println("获得的电话号码:"+telephone);
            sendCode.sendCode(telephone,code);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @GetMapping("/user/userManage")
    public String userManageGet(Model model) {
        User user = (User) httpSession.getAttribute("user");
        User user1 = userMapper.selectByNameAndPwd(user);
        model.addAttribute("user", user1);
        return "user/userManage";
    }

    @PostMapping("/user/userManage")
    public String userManagePost(Model model, User user, HttpSession httpSession) {
        System.out.println("用户信息修改："+user);
        Date date = new Date();
        user.setUpdateDate(date);
        int i = userMapper.update(user);
        httpSession.setAttribute("user",user);
        return "redirect:userManage";
    }

}
