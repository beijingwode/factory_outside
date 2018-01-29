package com.wode.factory.outside.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.wode.common.util.ActResult;
import com.wode.common.util.StringUtils;
import com.wode.factory.util.JPushUtils;

@Controller
@RequestMapping("jPush")
@ResponseBody
public class JPushController extends BaseController {

	//private static Logger logger= LoggerFactory.getLogger(UserProxyController.class);
	@Autowired
    private JPushUtils jPushUtils;
    	
	@RequestMapping("sendNotification")
	public ActResult<String> sendNotification(String appType,String alert, String title,
			String extras, String pushDriver, String pushType,
			String pushName) {
		
		Map<String,String> map = null;
		if(!StringUtils.isEmpty(extras)){
			map = JSON.parseObject(extras, Map.class);
		}
		if(jPushUtils.sendNotification(appType, alert, title, map, pushDriver, pushType, pushName)) {
			return ActResult.success("");
		} else {
			return ActResult.fail("发送失败");
		}
	}

	@RequestMapping("sendMessage")
	public ActResult<String> sendMessage(String appType,String msgContent, String title,
			String extras, String pushDriver, String pushType,String statusId , String pushName) {

		Map<String,String> map = null;
		if(!StringUtils.isEmpty(extras)){
			map = JSON.parseObject(extras, Map.class);
		}
		if(jPushUtils.sendMessage(appType, msgContent, title, map, pushDriver, pushType, statusId, pushName)) {
			return ActResult.success("");
		} else {
			return ActResult.fail("发送失败");
		}
	}
}
