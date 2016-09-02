package com.letv.common.email.impl;

import java.io.File;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import com.letv.common.exception.CommonException;

import com.letv.common.email.ITemplateMessageSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.letv.common.email.bean.MailMessage;
import freemarker.template.Template;

@Component("defaultEmailSender")
public class DefaultEmailSender implements ITemplateMessageSender {
	
	private final static Logger logger = LoggerFactory.getLogger(DefaultEmailSender.class);
	
	@Value("${smtp.defaultEncoding}")
	private String encoding;
	
	@Value("${smtp.from}")
	private String from;
	
	@Autowired
    private TaskExecutor threadPoolTaskExecutor;
	
	@Autowired
	private FreeMarkerConfigurer freeMarkerConfigurer;
	
	@Autowired
	private JavaMailSender mailSender;

    private MimeMessage transform(MailMessage mail) throws Exception {
        MimeMessage mime = mailSender.createMimeMessage();
        MimeMessageHelper helper;
        if (mail.getAttachments() != null || mail.getInlines() != null) {
            helper = new MimeMessageHelper(mime, true, encoding);
        } else {
            helper = new MimeMessageHelper(mime, encoding);
        }
        if (mail.getFrom() != null) {
            helper.setFrom(mail.getFrom());
        } else {
            helper.setFrom(from, mail.getFrom());
        }
        if (mail.getBcc() != null) {
            helper.setBcc(mail.getBcc());
        }
        if (mail.getCc() != null) {
            String[] ccs = mail.getCc().split(";");
            helper.setCc(ccs);
        }
        if (mail.getCreateDate() != null) {
            helper.setSentDate(mail.getCreateDate());
        } else {
            helper.setSentDate(new Date());
        }
        if (mail.getEncoding() == null) {
            mail.setEncoding(encoding);
        }
        if (mail.getReplyTo() != null) {
            helper.setReplyTo(mail.getReplyTo());
        }
        if (mail.getSubject() != null) {
            helper.setSubject(mail.getSubject());
            if (mail.getSubject().endsWith(".ftl")) {
                helper.setSubject(getMailText(mail.getSubject(), mail.getModel()));
            } else {
                helper.setSubject(mail.getSubject());
            }
        }
        if (mail.getTo() != null) {
        	String[] tos = mail.getTo().split(";");
            helper.setTo(tos);
        }
        if (mail.getText() != null) {
            if (mail.getText().endsWith(".ftl")) {
                helper.setText(getMailText(mail.getText(), mail.getModel()),mail.isHtml());
            } else {
                helper.setText(mail.getText(), mail.isHtml());
            }
        }
        if (mail.getInlines() != null) {
            Map<String, File> inlines = mail.getInlines();
            Iterator<Entry<String, File>> iterator = inlines.entrySet().iterator();
            while (iterator.hasNext()) {
                Entry<String, File> entry = iterator.next();
                String contentId = entry.getKey();
                File inline = entry.getValue();
                helper.addInline(MimeUtility.encodeText(contentId), inline);
            }
        }
        if (mail.getAttachments() != null) {
            Map<String, File> attachments = mail.getAttachments();
            Iterator<Entry<String, File>> iterator = attachments.entrySet().iterator();
            while (iterator.hasNext()) {
                Entry<String, File> entry = iterator.next();
                String fileName = entry.getKey();
                File attachment = entry.getValue();
                helper.addAttachment(MimeUtility.encodeText(fileName), attachment);
            }
        }
        return mime;
    }
    
  	private String getMailText(String ftlName, Map<String, Object> model){
  		String text;
  		try {
  			//通过指定模板名获取FreeMarker模板实例
  			Template tpl = freeMarkerConfigurer.getConfiguration().getTemplate(ftlName);
  			text = FreeMarkerTemplateUtils.processTemplateIntoString(tpl,model);
  		} catch (Exception e) {
  			throw new CommonException("在发送邮件过程中，值与模版转换发生错误！",e);
  		}
  		return text;
  	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	@Override
	public void sendMessage(Object msg) {
		MailMessage[] mails = new MailMessage[1];
		mails[0] = (MailMessage) msg;
		sendMessage(mails);
	}

	@Override
	public void sendMessage(Object[] msgArray) {
		MailMessage[] mails = (MailMessage[]) msgArray;
        MimeMessage[] mimes = new MimeMessage[mails.length];
        int i = 0;
        for (MailMessage mail : mails) {
            try {
				mimes[i++] = transform(mail);
			} catch (Exception e) {
				throw new CommonException("在发送邮件过程中，值转换发生错误！",e);
			}
        }
		threadPoolTaskExecutor.execute(new SendMailThread(mailSender, mimes));
		
		logger.info("邮件已发送！");
	}
}

