package com.wode.factory.outside.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.wode.comm.sms.client.SmsClient;
import com.wode.common.util.ActResult;
import com.wode.factory.stereotype.NoCheckAsync;
import com.wode.factory.util.IPUtils;

@Controller
@RequestMapping("smsProxy")
@ResponseBody
public class SmsProxyController extends BaseController {

	static SmsClient client=new SmsClient();

	@RequestMapping("sendSms")
	public ActResult<String> sendSms(String mobile, String signature, String content, String source,HttpServletRequest request) {

		return client.send(mobile, signature, content, IPUtils.getClientAddress(request), source);
	}
	
	@RequestMapping("sendSmsTempCode")
	@NoCheckAsync
	public ActResult<String> sendSmsTempCode(String mobile, String signature, String content, String source,String jSonParams,String outId,boolean isAsync, String notifyUrl,HttpServletRequest request) {
		if(isAsync) {
			JSONObject paramas = new JSONObject();
			//String from= org.apache.commons.codec.binary.Base64.encodeBase64String(body.toJSONString().getBytes());
			paramas.put("mobile",mobile);
			paramas.put("signature",signature);
			paramas.put("content",content);
			paramas.put("ip",IPUtils.getClientAddress(request));
			paramas.put("source",source);
			paramas.put("outId",outId);
			paramas.put("jSonParams",org.apache.commons.codec.binary.Base64.encodeBase64String(jSonParams.getBytes()));
			
			Long cid = this.saveCommand("smsProxy", "sendSmsTempCode", notifyUrl, paramas);
			return ActResult.success(cid+"");
		} else {
			return client.send(mobile, signature, content, IPUtils.getClientAddress(request), source,jSonParams,outId);
		}
	}
}
