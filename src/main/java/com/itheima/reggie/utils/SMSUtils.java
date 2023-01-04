package com.itheima.reggie.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Random;

/**
 * 短信发送工具类
 */
@Component
public class SMSUtils {

	public static void contextLoads(JavaMailSenderImpl mailSender, String user,String code) throws MessagingException {
		int count=1;//默认发送一次
		MimeMessage mimeMessage = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,true);
		while(count--!=0){
			System.out.println(code);
			//标题
			helper.setSubject("您的验证码为："+code);
			//内容
			helper.setText("您好！，感谢支持瑞吉外卖。您的验证码为："+"<h2>"+code+"</h2>"+"千万不能告诉别人哦！",true);
			//邮件接收者
			helper.setTo(user);
			//邮件发送者，必须和配置文件里的一样，不然授权码匹配不上
			helper.setFrom("2715309233@qq.com");
			mailSender.send(mimeMessage);
			// System.out.println("邮件发送成功！"+(count+1));
		}
	}

}
