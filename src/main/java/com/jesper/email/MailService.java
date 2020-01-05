package com.jesper.email;

/**
 * 邮件接口
 * @author lvyelanshan
 * @create 2020-01-04 22:00
 */
public interface MailService {

    /**
     * 发送文本邮件
     * @param to 收件人
     * @param subject 主题
     * @param content 内容
     */
     void sendSimpleMail(String to,String subject,String content);

    /**
     * 发送HTML邮件
     * @param to 收件人
     * @param subject 主题
     * @param content 内容
     */
     void sendHtmlMail(String to,String subject,String content);

    /**
     * 发送带有附件的邮件
     * @param to 收件人
     * @param subject 主题
     * @param content 内容
     * @param filePath 文件路径
     */
     void sendAttachmentsMail(String to,String subject,String content,String filePath);
}
