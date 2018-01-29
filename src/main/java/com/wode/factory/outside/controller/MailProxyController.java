package com.wode.factory.outside.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.wode.common.util.ActResult;
import com.wode.factory.stereotype.NoCheckAsync;
import com.wode.factory.util.JavaMailUtil;

@Controller
@RequestMapping("mailProxy")
@ResponseBody
public class MailProxyController extends BaseController {

	// private static Logger logger=
	// LoggerFactory.getLogger(UserProxyController.class);
	@Autowired
	@Qualifier("mailUtil")
	private JavaMailUtil mailUtil;

	@RequestMapping("sendMail")
	@NoCheckAsync
	public ActResult<String> sendMail(String toEmail, String subject, String text, String cc, String bcc,
			String comeFrom,boolean isAsync, String notifyUrl) {
		ActResult<String> act = new ActResult<String>();
		if(isAsync) {
			JSONObject paramas = new JSONObject();
			//String from= org.apache.commons.codec.binary.Base64.encodeBase64String(body.toJSONString().getBytes());
			paramas.put("toEmail",toEmail);
			paramas.put("subject",subject);
			paramas.put("text",org.apache.commons.codec.binary.Base64.encodeBase64String(text.getBytes()));
			paramas.put("cc",cc);
			paramas.put("bcc",bcc);
			paramas.put("comeFrom",comeFrom);
			
			Long cid = this.saveCommand("mailProxy", "sendMail", notifyUrl, paramas);
			act.setSuccess(true);
			act.setData(cid+"");
		} else {
			act.setSuccess(mailUtil.sendSenderSimpleMessageMail(toEmail, subject, text));
		}
		return act;

	}
}
