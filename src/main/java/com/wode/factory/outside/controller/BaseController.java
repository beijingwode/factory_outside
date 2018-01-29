package com.wode.factory.outside.controller;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;
import com.wode.factory.outside.model.OutsideCmd;
import com.wode.factory.outside.service.OutsideCmdService;

public class BaseController {

	//private static Logger logger= LoggerFactory.getLogger(UserProxyController.class);
	@Autowired
	private OutsideCmdService outsideCmdService;
	
	protected Long saveCommand(String serviceName, String methodName, String notifyUrl,JSONObject paramas) {
		OutsideCmd cmd = new OutsideCmd();
		cmd.setExecStatus("0");
		cmd.setExecResult("0");
		cmd.setServiceName(serviceName);
		cmd.setMethodName(methodName);
		cmd.setParamJson(paramas.toJSONString());
		cmd.setNotifyUrl(notifyUrl);
		outsideCmdService.saveOrUpdate(cmd);
		return cmd.getId();
    }
	
	protected boolean isAsync(String isAsync) {
		return "1".equals(isAsync) || "true".equals(isAsync);
	}
}
