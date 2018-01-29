package com.wode.factory.outside.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.wode.common.util.ActResult;
import com.wode.common.util.HttpClientUtil;
import com.wode.factory.stereotype.NoCheckAsync;
import com.wode.factory.util.Constant;

@Controller
@RequestMapping("expressProxy")
@ResponseBody
public class ExpressProxyController extends BaseController {
	
	@RequestMapping("companyInfo")
	@NoCheckAsync
	public ActResult<String> companyInfo() {		
		String allCompInfoJSON = HttpClientUtil.sendHttpRequest("post", Constant.EXPRESS_API_URL+"express/companyInfo.do", new HashMap<String, Object>());
		return ActResult.success(allCompInfoJSON);
	}

	@RequestMapping(value="search")
	@NoCheckAsync
    public ActResult<JSONObject> express(String expressCom,String expressNo,String user){

		String content = "\"sname\":\"express.ExpressSearch\",\"com\":\""+expressCom+"\",\"express_no\":\""+expressNo+"\",\"user\":"+user+",\"version\":\"v2\"";
        Map<String,Object> paramPush=new HashMap<String,Object>();
		String body = HttpClientUtil.sendHttpRequest("post",Constant.EXPRESS_API_URL+"express/busJsonp.do?content={" +content+ "}&token=", paramPush);
		body=body.substring(body.indexOf("(")+1, body.length()-1);		
		return ActResult.success(JSONObject.parseObject(body));
	}
}
