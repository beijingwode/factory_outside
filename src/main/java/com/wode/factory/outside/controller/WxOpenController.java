package com.wode.factory.outside.controller;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wode.common.redis.RedisUtil;
import com.wode.common.util.ActResult;
import com.wode.common.util.HttpClientUtil;
import com.wode.common.util.StringUtils;
import com.wode.factory.stereotype.NoCheckAsync;
import com.wode.factory.util.Constant;
import com.wode.factory.util.weixin.CoreService;
import com.wode.factory.util.weixin.SignUtil;
import com.wode.factory.util.weixin.TempletMessageUtil;
import com.wode.factory.util.weixin.WXConstants;

@Controller
@RequestMapping("wxOpen")
@ResponseBody
@SuppressWarnings("unchecked")
public class WxOpenController extends BaseController {

	private static Logger logger = LoggerFactory.getLogger(WxOpenController.class);
	private static Long default_expire_seconds = (long) (15*24*60*60);	//15 天
	
	@Qualifier("redis")
    @Autowired
    private RedisUtil redisUtil;
	
	@RequestMapping("templateMsgSend")
	@NoCheckAsync
	public ActResult<String> templateMsgSend(String openId,String type,Long userId,boolean isAsync, String notifyUrl,HttpServletRequest request) throws UnsupportedEncodingException {
		//获取token
		String token = CoreService.getAccessToken(redisUtil);
		
		JSONObject body = TempletMessageUtil.makeBody(request, openId, type,userId);
		
		if(body!=null) { 
			if(isAsync) {
				JSONObject paramas = new JSONObject();
				//String from= org.apache.commons.codec.binary.Base64.encodeBase64String(body.toJSONString().getBytes());
				paramas.put("body",org.apache.commons.codec.binary.Base64.encodeBase64String(body.toJSONString().getBytes()));
				
				Long cid = this.saveCommand("wxOpen", "templateMsgSend", notifyUrl, paramas);
				return ActResult.success(cid+"");
			} else {
				//发送消息
				String rtn;
				try {
					rtn = HttpClientUtil.sendJsonDataByPost(WXConstants.SEND_TEMPLATE_URL.replace("ACCESS_TOKEN", token), body.toJSONString(), null);
					if(rtn.contains("access_token is invalid or not latest hint")) {
						// 如果失败 就删除缓存token 再此获取1此
						redisUtil.del("WX_ACCESS_TOKEN");
						token = CoreService.getAccessToken(redisUtil);
						rtn = HttpClientUtil.sendJsonDataByPost(WXConstants.SEND_TEMPLATE_URL.replace("ACCESS_TOKEN", token), body.toJSONString(), null);
					}
					return ActResult.success(rtn);
				} catch (UnsupportedEncodingException e) {
					return ActResult.fail("退送内容中有非法字符");
				}
			}
			
		} else {
			return ActResult.fail("模板id有误， 暂时不能发送此类消息，请联系客服");
		}
	}

	@RequestMapping("checkSignature")
	@NoCheckAsync
	public ActResult<String> checkSignature(String signature, String timestamp,String nonce) {

        if (SignUtil.checkSignature(signature, timestamp, nonce)) {  
    		return ActResult.success("");
        } else {  
    		return ActResult.fail("error");
        }  
	}


	@RequestMapping("processRequest")
	@NoCheckAsync
	public ActResult<List<String>> processRequest(String jsonMap) {
		Map<String,String> map = null;
		if(StringUtils.isEmpty(jsonMap)){
			return ActResult.fail("参数错误");
		} else {
			map = JSON.parseObject(jsonMap, Map.class);
		}
		List<String> subscribes = new ArrayList<String>();
		
		try {
			ActResult<List<String>> act = ActResult.successSetMsg(CoreService.processRequest(map, subscribes, redisUtil));
			act.setData(subscribes);
			return act;
		} catch (Exception e) {
			return ActResult.fail("");
		}
	}

	@RequestMapping("wxConfig")
	@NoCheckAsync
	public ActResult<JSONObject> wxConfig(String url) {
		JSONObject data = new JSONObject();
		WXConstants.wxConfigJson(url, data, redisUtil);
		return ActResult.success(data);
	}
	
	@RequestMapping("getOpenId")
	@NoCheckAsync
	public ActResult<String> getOpenId(String code) {
		Map<String, Object> paramMap=new HashMap<String, Object>();
		String rtn = HttpClientUtil.sendHttpRequest("get","https://api.weixin.qq.com/sns/oauth2/access_token?appid="+WXConstants.APPID+"&secret="+WXConstants.SECRET+"&code="+code+"&grant_type=authorization_code ", paramMap);
		return ActResult.success(rtn);
	}

	
	@RequestMapping("getUserInfo")
	@NoCheckAsync
	public ActResult<JSONObject> getUserInfo(String openId) {
		Map<String, Object> paramMap=new HashMap<String, Object>();
		String token = CoreService.getAccessToken(redisUtil);
		try {
			String rep = HttpClientUtil.sendHttpRequest("get",WXConstants.GET_USER_BASE_INFO_URL.replace("ACCESS_TOKEN", token).replace("OPENID", openId),paramMap);
			if(rep.contains("access_token is invalid or not latest hint")) {
				// 如果失败 就删除缓存token 再此获取1此
				redisUtil.del("WX_ACCESS_TOKEN");
				token = CoreService.getAccessToken(redisUtil);
				rep = HttpClientUtil.sendHttpRequest("get",WXConstants.GET_USER_BASE_INFO_URL.replace("ACCESS_TOKEN", token).replace("OPENID", openId),paramMap);
			}
			JSONObject jo = JSONObject.parseObject(rep);
			if(jo.containsKey("errcode") && !StringUtils.isEmpty(jo.getString("errcode"))) {
				return ActResult.fail("获取用户信息失败");
			}
			return ActResult.success(jo);
		} catch (Exception e) {
			return ActResult.fail("获取用户信息失败");
		}
	}
	@RequestMapping("createQRTemp")
	@NoCheckAsync
	public ActResult<String> createQRTemp(String shareId,Long expireSeconds) {
		if(expireSeconds==null) expireSeconds=default_expire_seconds;
		
		JSONObject jo = new JSONObject();
		jo.put("expire_seconds", expireSeconds);		//有效期15天
		jo.put("action_name", "QR_STR_SCENE");
		JSONObject scene = new JSONObject();
		scene.put("scene_str", shareId);
		JSONObject action_info = new JSONObject();
		action_info.put("scene", scene);
		jo.put("action_info", action_info);
		
		String token = CoreService.getAccessToken(redisUtil);
		try {
			String rep = HttpClientUtil.sendJsonDataByPost(WXConstants.CREATE_QR_MORE_URL.replace("ACCESS_TOKEN", token), jo.toJSONString(), null);
			if(rep.contains("access_token is invalid or not latest hint")) {
				// 如果失败 就删除缓存token 再此获取1此
				redisUtil.del("WX_ACCESS_TOKEN");
				token = CoreService.getAccessToken(redisUtil);
				rep = HttpClientUtil.sendJsonDataByPost(WXConstants.CREATE_QR_MORE_URL.replace("ACCESS_TOKEN", token), jo.toJSONString(), null);
			}
			return ActResult.success(rep);
		} catch (Exception e) {
			return ActResult.fail("生成QR 失败");
		}
	}
	
	@RequestMapping("setTag")
	public ActResult<String> setTag(String openIds,String tagId) {
		String token = CoreService.getAccessToken(redisUtil);
		try {
			HttpClientUtil.sendJsonDataByPost(WXConstants.MEMBERS_BATCHTAGGING.replace("ACCESS_TOKEN", token), "{\"openid_list\":[\""+openIds+"\"],\"tagid\":"+tagId+"}", null);
			return ActResult.success("");
		} catch (UnsupportedEncodingException e) {
			return ActResult.fail("内容中有非法字符");
		}
	}
	
	@RequestMapping("updateMemu")
	@NoCheckAsync
	public ActResult<String> updateMemu(String type) {
		
		String baseUrl = Constant.FACTORY_API_URL;
		//获取token
		String token = CoreService.getAccessToken(redisUtil);
		
		//删除菜单
		Map<String, Object> paramMap=new HashMap<String, Object>();
		HttpClientUtil.sendHttpsRequest("get",WXConstants.DELETE_MENU_URL.replace("ACCESS_TOKEN", token), paramMap);
		
		JSONObject menu = WXConstants.makeMenuJson(type, baseUrl);
		
		//创建菜单
		String rtn;
		try {
			rtn = HttpClientUtil.sendJsonDataByPost(WXConstants.CREATE_MENU_URL.replace("ACCESS_TOKEN", token), menu.toJSONString(), null);

			//创建菜单
			rtn =HttpClientUtil.sendJsonDataByPost(WXConstants.CREATE_SPECIAL_MENU_URL.replace("ACCESS_TOKEN", token), WXConstants.makeSpecialMenuJson(WXConstants.TAG_ID_SHOP, baseUrl).toJSONString(), null);
			
			return ActResult.success(rtn);
		} catch (UnsupportedEncodingException e) {
			return ActResult.fail("内容中有非法字符");
		}
	}
	
}
