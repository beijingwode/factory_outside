package com.wode.factory.util;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.wode.common.util.TimeUtil;

@Service("mailUtil")
public class JavaMailUtil {



    @Autowired
	@Qualifier("mailSender")
	private JavaMailSenderImpl mailSender;
    
    @Autowired(required=false)
    @Qualifier("gunMailSender")
    private JavaMailSenderImpl gunMailSender;
    
    /*@Autowired
    @Qualifier("tmpl")
    private Map<String,String> tmpl;*/
    
	/**
	 * 读取配置文件对象
	 */
	private static ReloadableResourceBundleMessageSource messageSource;
	
	@Autowired
	public void setMessageSource(
			ReloadableResourceBundleMessageSource messageSource) {
		JavaMailUtil.messageSource = messageSource;
	}
	
    /*public Map<String, String> getTmpl() {
		return tmpl;
	}

	public void setTmpl(Map<String, String> tmpl) {
		System.out.println(1);
		this.tmpl = tmpl;
	}*/

	public JavaMailSenderImpl getMailSender() {
        return mailSender;
    }

    public void setMailSender(JavaMailSenderImpl mailSender) {
        this.mailSender = mailSender;
    }
    
    public JavaMailSenderImpl getGunMailSender() {
		return gunMailSender;
	}

	public void setGunMailSender(JavaMailSenderImpl gunMailSender) {
		this.gunMailSender = gunMailSender;
	}

	public void setMailFrom(String name){
        mailSender.setUsername(name);
    }
    public  void setMailPass(String pass){
        mailSender.setPassword(pass);
    }
    
	/**
	 * 普通邮件模板
	 */
	private SimpleMailMessage smm = new SimpleMailMessage();
	/**
	 * 日志记录对象
	 */
	private static Logger logger = LoggerFactory.getLogger(JavaMailUtil.class);
	
	
	public boolean sendSenderSimpleMessageMail(String toEmail, String subject, String text) {
		MimeMessage msg = null;
		msg = mailSender.createMimeMessage();
		MimeMessageHelper helper = null;
		try {
			helper = new MimeMessageHelper(msg, true,"UTF-8");
			//设定邮件发送参数
			helper.setFrom("\"我的科技\"<"+mailSender.getUsername()+">");
			helper.setTo(new InternetAddress().parse(toEmail));
			helper.setSubject(subject);
			helper.setText(text,true);
			//msg.setContent(text, "text/html;charset=UTF-8");
			mailSender.send(msg);
			return true;
		}catch (Exception e) {
			logger.error(TimeUtil.getCurrentTime() + "发送邮件失败，邮箱账号：" + mailSender.getUsername() + "		，\n异常：" + e);
			return false;
		}
	}
	
	/**
	 * 发送复杂邮件(先使用gun代理发送邮件，如果发送失败则使用qq邮件服务器发送邮件)
	 * @param toEmail
	 * @param subject
	 * @param text
	 * @return boolean
	 */
	public boolean sendMimeMessageHelperMail(String toEmail, String subject, String text) {
		//使用JavaMail的MimeMessage，支付更加复杂的邮件格式和内容
		MimeMessage msg = null;
		//创建MimeMessageHelper对象，处理MimeMessage的辅助类
		MimeMessageHelper helper = null;
		try{
			msg = mailSender.createMimeMessage();
			helper = new MimeMessageHelper(msg, true,"UTF-8");
			//使用辅助类MimeMessage设定参数
			
			helper.setTo(toEmail);
			helper.setSubject(subject);
			helper.setText(text, true);
				helper.setFrom(messageSource.getMessage("mail.username", null, null, null).trim(), "我的科技");
				final String apiUser = "wode_mailcloud1_trigger";
				final String apiKey = "s1Va7UvIV0zHS1YQ";
				Session session = Session.getInstance(mailSender.getJavaMailProperties(), new Authenticator() {
		            @Override
		            protected PasswordAuthentication getPasswordAuthentication() {
		                return new PasswordAuthentication(apiUser, apiKey);
		            }
		        });  
				mailSender.setSession(session);
			//helper.setFrom("\"我的科技\"<"+messageSource.getMessage("mail.username", null, null, null).trim()+">");
	//			//加载文件资源，作为附件
	//			ClassPathResource file = new ClassPathResource("Chrysanthemum.jpg");
	//			//加入附件
	//			helper.addAttachment("attachment.jpg", file);
				// 发送邮件
				mailSender.send(msg);
			return true;
		} catch(Exception e) {
			e.printStackTrace();
			logger.error(TimeUtil.getCurrentTime() + "gunMail发送邮件失败，邮箱账号：" + mailSender.getUsername() + "		,\n异常：" + e);
			//如果使用gun代理发送邮件失败，则使用qq邮件服务器发送邮件
			try {
				mailSender.send(msg);
				return true;
			} catch (MailException e1) {
				e1.printStackTrace();
				logger.error(TimeUtil.getCurrentTime() + "qqMail发送邮件失败，邮箱账号：" + mailSender.getUsername() + "	,\n异常：" + e);
				return false;
			}
		}
		
		
	}

	/**
	 * 发送附带附件的邮件针对（qq邮箱不显示图片进行测试）
	 * 
	 * @param toEmail
	 * @param subject
	 * @param text
	 * @return boolean
	 */
	public boolean sendMimeMessageAttachmentMail(String toEmail, String subject, String text) {
		//使用JavaMail的MimeMessage，支付更加复杂的邮件格式和内容
		MimeMessage msg = null;
		//创建MimeMessageHelper对象，处理MimeMessage的辅助类
		MimeMessageHelper helper = null;
		try{
			msg = mailSender.createMimeMessage();
			helper = new MimeMessageHelper(msg, true,"UTF-8");
			//使用辅助类MimeMessage设定参数
			helper.setFrom("\"我的科技\"<"+mailSender.getUsername()+">");
			helper.setTo(toEmail);
			helper.setSubject(subject);
			helper.setText(text, true);
//			//加载文件资源，作为附件
//			ClassPathResource file = new ClassPathResource("Chrysanthemum.jpg");
//			System.out.println("\n	+++++++++++	filename	" + file.getFilename() + "	path	" + file.getPath());
//			//加入附件
//			helper.addAttachment("attachment.jpg", file);
			// 发送邮件
			
			//第二个参数true，表示text的内容为html
			//注意<img/>标签，src='cid:file'，'cid'是contentId的缩写，'file'是一个标记，需要在后面的代码中调用MimeMessageHelper的addInline方法替代成文件
			FileSystemResource file = new FileSystemResource("E:\\image\\newName.jpg");
			helper.addInline("file", file);
			mailSender.send(msg);
			return true;
		} catch(Exception e) {
			logger.error(TimeUtil.getCurrentTime() + "发送邮件失败，邮箱账号：" + mailSender.getUsername() + "		，\n异常：" + e);
			return false;
		}
		
		
	}

    /**
     * 发送复杂邮件
     * @param toEmail
     * @param subject
     * @param text
     * @return boolean
     */
    @Deprecated
    public boolean sendMimeMessageHelperMail(String from ,String toEmail, String subject, String text) {
        //使用JavaMail的MimeMessage，支付更加复杂的邮件格式和内容
        MimeMessage msg = null;
        //创建MimeMessageHelper对象，处理MimeMessage的辅助类
        MimeMessageHelper helper = null;
        try{
            msg = mailSender.createMimeMessage();
            helper = new MimeMessageHelper(msg, true,"UTF-8");
            //使用辅助类MimeMessage设定参数
            helper.setFrom("\"我的科技\"<"+from+">");
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(text, true);
//			//加载文件资源，作为附件
//			ClassPathResource file = new ClassPathResource("Chrysanthemum.jpg");
//			//加入附件
//			helper.addAttachment("attachment.jpg", file);
            // 发送邮件
            mailSender.send(msg);
            return true;
        } catch(Exception e) {
            logger.error(TimeUtil.getCurrentTime() + "发送邮件失败，邮箱账号：" + mailSender.getUsername() + "		，\n异常：" + e);
            return false;
        }


    }
	
}
