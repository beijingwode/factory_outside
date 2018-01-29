package com.wode.factory.util.weixin;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.wode.common.redis.RedisUtil;
import com.wode.common.util.HttpClientUtil;
import com.wode.common.util.StringUtils;
import com.wode.factory.outside.model.weixin.message.Article;
import com.wode.factory.util.Constant;

public class CoreService {

    public final static String REDIS_WX_OPEN_MESSAGE_MAP = "WX_OPEN_MESSAGE_MAP";
    public final static String MSG_TYPE_TEMP = "2";
    public final static String MSG_TYPE_DEFAULT = "1";
    public final static String MY_EVENT_TYPE_COMPANY = "company";
    public final static String MY_EVENT_TYPE_SHARE = "share";
    public final static String MY_EVENT_TYPE_AUTO_BUYC = "autoBuyC";
    public final static String MY_EVENT_TYPE_AUTO_BUYU = "autoBuyU";
    public final static String MY_EVENT_TYPE_TICKETE = "ticketE";
    public final static String MY_EVENT_TYPE_TICKETL = "ticketL";
    
	static Logger logger = LoggerFactory.getLogger(CoreService.class);
	
	/**
	 * 处理微信发来的请求
	 * 
	 * @param request
	 * @return
	 */
	public static String processRequest(Map<String, String> requestMap,List<String> subscribes,RedisUtil redis) {
		String respMessage = null;
		try {
			// 默认返回的文本消息内容
			String respContent = "请求处理异常，请稍候尝试！";

			// xml请求解析
			// 调用消息工具类MessageUtil解析微信发来的xml格式的消息，解析的结果放在HashMap里；
//			Map<String, String> requestMap = MessageUtil.parseXml(request);

			// 从HashMap中取出消息中的字段；
			// 发送方帐号（open_id）
			String fromUserName = requestMap.get("FromUserName");
			// 公众帐号
			String toUserName = requestMap.get("ToUserName");
			// 消息类型
			String msgType = requestMap.get("MsgType");
			// 消息内容
			String content = requestMap.get("Content");
			// 消息内容
			String eventKey = requestMap.get("EventKey");
			if(eventKey!=null && eventKey.startsWith("qrscene_")) {
				eventKey=eventKey.substring("qrscene_".length());
			}
			// 从HashMap中取出消息中的字段；
			System.out.println(msgType);
			
			logger.info("fromUserName is:" +fromUserName+" toUserName is:" +toUserName+" msgType is:" +msgType+" EventKey is:" +eventKey);
			if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_EVENT)) {// 事件推送
				String eventType = requestMap.get("Event");// 事件类型
				logger.info("eventType is:" + eventType);

				if (eventType.equals(MessageUtil.EVENT_TYPE_SUBSCRIBE)) {// 订阅
					subscribes.add(fromUserName);
					List<Article> articleList = new ArrayList<Article>();
					Article art1 = new Article();
					
					if(eventKey.startsWith(MY_EVENT_TYPE_SHARE)) {
						// 一起购链接
						art1 = initArticle(redis, MY_EVENT_TYPE_SHARE, Constant.FACTORY_API_URL+"userShare/wxEventLink?eventKey="+eventKey+"&openId="+fromUserName);
						
					} else if(eventKey.startsWith(MY_EVENT_TYPE_COMPANY)) {
						// 商家绑定
						art1 = initArticle(redis, MY_EVENT_TYPE_COMPANY, Constant.FACTORY_API_URL+"userShare/wxEventLink?eventKey="+eventKey+"&openId="+fromUserName);

					} else if(eventKey.startsWith(MY_EVENT_TYPE_TICKETE)) {
						// 商家绑定(获得换领币)
						art1 = initArticle(redis, MY_EVENT_TYPE_TICKETE, Constant.FACTORY_API_URL+"userShare/wxEventLink?eventKey="+eventKey+"&openId="+fromUserName);

					} else if(eventKey.startsWith(MY_EVENT_TYPE_TICKETL)) {
						// 获得电子卡券
						art1 = initArticle(redis, MY_EVENT_TYPE_TICKETL, Constant.FACTORY_API_URL+"limitTicket/wxEventLink?eventKey="+eventKey+"&openId="+fromUserName);

					} else if(eventKey.startsWith(MY_EVENT_TYPE_AUTO_BUYC)) {
						// 商家绑定(自动下单)
						art1 = initArticle(redis, MY_EVENT_TYPE_AUTO_BUYC, Constant.FACTORY_API_URL+"userShare/wxEventLink?eventKey="+eventKey+"&openId="+fromUserName);

					} else if(eventKey.startsWith(MY_EVENT_TYPE_AUTO_BUYU)) {
						// 亲友邀请(自动下单)
						art1 = initArticle(redis, MY_EVENT_TYPE_AUTO_BUYU, Constant.FACTORY_API_URL+"userShare/wxEventLink?eventKey="+eventKey+"&openId="+fromUserName);
						
					} else {
						art1 = initArticle(redis,eventType,Constant.FACTORY_API_URL+"shiyongjihe.html");
					}
					articleList.add(art1);
					return MessageResponse.getNewsMessage(fromUserName, toUserName, articleList);
				} else if (eventType.equals(MessageUtil.EVENT_TYPE_UNSUBSCRIBE)) {// 取消订阅
//					UserWeixin uw = userWeixinService.getOneModelByOpenId(fromUserName);
//					if(uw!=null) {
//						uw.setLogout(1);
//						uw.setUpdateTime(new Date());
//						userWeixinService.update(uw);
//					}
					respContent = "期待您的再次访问";
					return MessageResponse.getTextMessage(fromUserName, toUserName, respContent);
				} else if (eventType.equals(MessageUtil.EVENT_TYPE_CLICK)) {// 自定义菜单点击事件
					if("service".equals(eventKey)) {
						respContent = "请问有什么可以帮到您，回复此信息，有专职客服经理为您服务。";					
					} else {
						respContent = "欢迎关注我的网，我的生活与众不同！ 点击以下链接，绑定我的福利账号，享个性化福利"+Constant.FACTORY_API_URL+"index_m.htm?wxOpen=1";
					}
					return MessageResponse.getTextMessage(fromUserName, toUserName, respContent);
					
				} else if (eventType.equals(MessageUtil.EVENT_TYPE_SCAN)) {// 事件类型 event
					Article art1 = new Article();
					if(eventKey.startsWith(MY_EVENT_TYPE_SHARE)) {
						// 一起购链接
						art1 = initArticle(redis, MY_EVENT_TYPE_SHARE, Constant.FACTORY_API_URL+"userShare/wxEventLink?eventKey="+eventKey+"&openId="+fromUserName);
						
					} else if(eventKey.startsWith(MY_EVENT_TYPE_COMPANY)) {
						// 商家绑定
						art1 = initArticle(redis, MY_EVENT_TYPE_COMPANY, Constant.FACTORY_API_URL+"userShare/wxEventLink?eventKey="+eventKey+"&openId="+fromUserName);

					} else if(eventKey.startsWith(MY_EVENT_TYPE_TICKETE)) {
						// 商家绑定(获得换领币)
						art1 = initArticle(redis, MY_EVENT_TYPE_TICKETE, Constant.FACTORY_API_URL + "userShare/wxEventLink?eventKey="+eventKey+"&openId="+fromUserName);

					} else if(eventKey.startsWith(MY_EVENT_TYPE_TICKETL)) {
						// 获得电子卡券
						art1 = initArticle(redis, MY_EVENT_TYPE_TICKETL, Constant.FACTORY_API_URL + "limitTicket/wxEventLink?eventKey="+eventKey+"&openId="+fromUserName);

					} else if(eventKey.startsWith(MY_EVENT_TYPE_AUTO_BUYC)) {
						// 商家绑定(自动下单)
						art1 = initArticle(redis, MY_EVENT_TYPE_AUTO_BUYC, Constant.FACTORY_API_URL+"userShare/wxEventLink?eventKey="+eventKey+"&openId="+fromUserName);

					} else if(eventKey.startsWith(MY_EVENT_TYPE_AUTO_BUYU)) {
						// 亲友邀请(自动下单)
						art1 = initArticle(redis, MY_EVENT_TYPE_AUTO_BUYU, Constant.FACTORY_API_URL+"userShare/wxEventLink?eventKey="+eventKey+"&openId="+fromUserName);
						
					} else {
						art1 = initArticle(redis,eventType,Constant.FACTORY_API_URL+"shiyongjihe.html");
					}
					
					List<Article> articleList = new ArrayList<Article>();
					articleList.add(art1);
					return MessageResponse.getNewsMessage(fromUserName, toUserName, articleList);
				}
			} else {
				sendToKF(msgType, content, fromUserName, redis);
				return MessageResponse.getTransmitService(fromUserName, toUserName);
			}
/*
			// 文本消息
			if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_TEXT)) {
				//微信聊天机器人测试 2015-3-31
				if(content!=null){
					respContent = TulingApiProcess.getTulingResult(content);
					if(respContent==""||null==respContent){
						MessageResponse.getTextMessage(fromUserName , toUserName , "服务号暂时无法回复，请稍后再试！");
					}
					//return FormatXmlProcess.formatXmlAnswer(toUserName, fromUserName, respContent);
					return MessageResponse.getTextMessage(fromUserName , toUserName , respContent);
				}
//				else if (content.startsWith("ZY")) {//查作业
//					String keyWord = content.replaceAll("^ZY" , "").trim();
//					if ("".equals(keyWord)) {
//						respContent = AutoReply.getXxtUsage("24");
//					} else {
//						return XxtService.getHomework("24" , fromUserName , toUserName , keyWord);
//					}
//				} else if (content.startsWith("SJX")) {//收件箱
//					String keyWord = content.replaceAll("^SJX" , "").trim();
//					if ("".equals(keyWord)) {
//						respContent = AutoReply.getXxtUsage("25");
//					} else {
//						return XxtService.getRecvBox("25" , fromUserName , toUserName , keyWord);
//					}
//				}
//				return MessageResponse.getTextMessage(fromUserName , toUserName , respContent);
			} else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_EVENT)) {// 事件推送
				String eventType = requestMap.get("Event");// 事件类型
				
				if (eventType.equals(MessageUtil.EVENT_TYPE_SUBSCRIBE)) {// 订阅
					respContent = "欢迎关注我的网，我的生活与众不同！";
					return MessageResponse.getTextMessage(fromUserName , toUserName , respContent);
				} else if (eventType.equals(MessageUtil.EVENT_TYPE_UNSUBSCRIBE)) {// 取消订阅
					// TODO 取消订阅后用户再收不到公众号发送的消息，因此不需要回复消息
				} else if (eventType.equals(MessageUtil.EVENT_TYPE_CLICK)) {// 自定义菜单点击事件
					String eventKey = requestMap.get("EventKey");// 事件KEY值，与创建自定义菜单时指定的KEY值对应
					logger.info("eventKey is:" +eventKey);
					//return MenuClickService.getClickResponse(eventKey , fromUserName , toUserName);
				}
			}
			
			
			//开启微信声音识别测试 2015-3-30
			else if(msgType.equals("voice"))
			{
				String recvMessage = requestMap.get("Recognition");
				//respContent = "收到的语音解析结果："+recvMessage;
				if(recvMessage!=null){
					respContent = TulingApiProcess.getTulingResult(recvMessage);
				}else{
					respContent = "您说的太模糊了，能不能重新说下呢？";
				}
				return MessageResponse.getTextMessage(fromUserName , toUserName , respContent); 
			}
			//拍照功能
			else if(msgType.equals("pic_sysphoto"))
			{
				
			}
			else
			{
				return MessageResponse.getTextMessage(fromUserName , toUserName , "返回为空"); 
			}
			
			*/
			
			
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		return respMessage;
	}

	// 缓存中获取配置消息
	private static Article initArticle(RedisUtil redis,String eventType,String link) {
		Article art1 = new Article();
		// 获取临时消息
		String str = redis.getMapData(CoreService.REDIS_WX_OPEN_MESSAGE_MAP, eventType+CoreService.MSG_TYPE_TEMP);
		// 临时消息为空 获取默认消息
		if(StringUtils.isEmpty(str)) {
			str = redis.getMapData(CoreService.REDIS_WX_OPEN_MESSAGE_MAP, eventType+CoreService.MSG_TYPE_DEFAULT);
		}
		// 临时消息、默认消息为空 
		if(StringUtils.isEmpty(str)) {
			if(MY_EVENT_TYPE_COMPANY.equals(eventType)) {
				// 商家绑定
				str = "{'title':'点击领取福利','picUrl':'https://mmbiz.qpic.cn/mmbiz_png/QWwoFJKCGKSRb8rQfcN0cUP6pBMn4jHbA72LhpGJPQLM9qiaWQx7s1ehpYSGiaWyBaVBic2dTssxm9e4WoDnwIUEw/0?wx_fmt=png','description':'注册成功即可获得500元内购券~','linkUrl':'"+link+"'}";

			} else if(MY_EVENT_TYPE_TICKETE.equals(eventType)) {
				// 商家绑定(获得换领币)
				str = "{'title':'点击领取福利','picUrl':'https://mmbiz.qpic.cn/mmbiz_png/QWwoFJKCGKSRb8rQfcN0cUP6pBMn4jHbA72LhpGJPQLM9qiaWQx7s1ehpYSGiaWyBaVBic2dTssxm9e4WoDnwIUEw/0?wx_fmt=png','description':'注册成功即可获得500元内购券，还有好礼相赠~','linkUrl':'"+link+"'}";

			} else if(MY_EVENT_TYPE_TICKETL.equals(eventType)) {
				// 领取电子卡券
				str = "{'title':'点击领取优惠券','picUrl':'https://mmbiz.qpic.cn/mmbiz_png/QWwoFJKCGKRhp8EB12ch9WBtJxYZqVnwmn6ZWgZZfTXb8d9pv3IclKKcG7swDFxXccV83YYibia9lupTiawA5DxNw/0?wx_fmt=png','description':'优惠券超值抵扣，多重福利惊喜不断~','linkUrl':'"+link+"'}";

			} else if(MY_EVENT_TYPE_AUTO_BUYC.equals(eventType)) {
				// 商家绑定自动下单
				str = "{'title':'点击领取福利','picUrl':'https://mmbiz.qpic.cn/mmbiz_png/QWwoFJKCGKSRb8rQfcN0cUP6pBMn4jHbA72LhpGJPQLM9qiaWQx7s1ehpYSGiaWyBaVBic2dTssxm9e4WoDnwIUEw/0?wx_fmt=png','description':'注册成功即可获得500元内购券，还有好礼相赠~','linkUrl':'"+link+"'}";

			} else if(MY_EVENT_TYPE_AUTO_BUYU.equals(eventType)) {
				// 商家绑定自动下单
				str = "{'title':'点击领取福利','picUrl':'https://mmbiz.qpic.cn/mmbiz_png/QWwoFJKCGKSRb8rQfcN0cUP6pBMn4jHbA72LhpGJPQLM9qiaWQx7s1ehpYSGiaWyBaVBic2dTssxm9e4WoDnwIUEw/0?wx_fmt=png','description':'注册成功即可获得500元内购券，还有好礼相赠~','linkUrl':'"+link+"'}";
				
			} else if(MY_EVENT_TYPE_SHARE.equals(eventType)) {
				// 一起购邀请
				str = "{'title':'您收到了一个“一起购”邀请，点我加入购物团~','picUrl':'https://mmbiz.qpic.cn/mmbiz_png/QWwoFJKCGKSNYVkgJucRlAOJAx5jgKhLwjOIsfVJxdZNRibiaQKibic3CBIgzEewsqh9MABtTdYujke8ZPyzRunHFw/0?wx_fmt=png','description':'本来就出厂价，拼团还能省更多，快来跟我一起购吧~','linkUrl':'"+link+"'}";
				
			} else {
				// 普通注册
				str = "{'title':'大牌赠品试用品免费领>>','picUrl':'https://mmbiz.qpic.cn/mmbiz_jpg/QWwoFJKCGKTO7Ids7R8Pw6aCM2hg6SwFicVyz7rlXDJmezpA8cib5D25EpcL3w5RKALj9SJM1ZSWicUhibb8Fn6hibw/0?wx_fmt=jpeg','description':'0元领取下午茶，和同事一起分享美味吧~','linkUrl':'"+link+"'}";
			}
		}
		
		JSONObject jsonMsg=JSONObject.parseObject(str);
		art1.setTitle(jsonMsg.getString("title"));
		art1.setDescription(jsonMsg.getString("description"));
		art1.setPicUrl(jsonMsg.getString("picUrl"));
		if(MY_EVENT_TYPE_COMPANY.equals(eventType)) {
			// 商家绑定
			art1.setUrl(link);

		} else if(MY_EVENT_TYPE_SHARE.equals(eventType)) {
			// 商家绑定自动下单
			art1.setUrl(link);
			
		} else if(MY_EVENT_TYPE_TICKETE.equals(eventType) || MY_EVENT_TYPE_TICKETL.equals(eventType)) {
			// 商家绑定(获得换领币),领取电子卡券
			art1.setUrl(link);
			
		} else if(MY_EVENT_TYPE_AUTO_BUYU.equals(eventType)) {
			// 商家绑定自动下单
			art1.setUrl(link);
			
		} else if(MY_EVENT_TYPE_AUTO_BUYC.equals(eventType)) {
			// 商家绑定自动下单
			art1.setUrl(link);
		} else {
			art1.setUrl(jsonMsg.getString("linkUrl"));
		}
		return art1;
	}

	/**
	 * 处理微信发来的请求
	 * 
	 * @param request
	 * @return
	 */
	public static String getAccessToken(RedisUtil redis) {

		String token = redis.getData("WX_ACCESS_TOKEN");
		if(StringUtils.isEmpty(token)) {
			Map<String, Object> paramMap=new HashMap<String, Object>();
	
			String rtn = HttpClientUtil.sendHttpsRequest("get",WXConstants.GET_ACCESSTOKEN_URL.replace("APPID", WXConstants.APPID).replace("APPSECRET", WXConstants.SECRET), paramMap);
			JSONObject json = JSONObject.parseObject(rtn);
			
			token= json.getString("access_token");
			redis.setData("WX_ACCESS_TOKEN", token, 7000);	//微信有效期7200 避免出错缓存7000秒
		}
		
		return token;
	}

	/**
	 * 处理微信发来的请求
	 * 
	 * @param request
	 * @return
	 */
	public static String getTicket(RedisUtil redis,String type) {

		String token = redis.getData("WX_TICKET_" + type.toUpperCase());
		if(StringUtils.isEmpty(token)) {
			Map<String, Object> paramMap=new HashMap<String, Object>();
	
			String rtn = HttpClientUtil.sendHttpsRequest("get",WXConstants.GET_TICKET_URL.replace("ACCESS_TOKEN", getAccessToken(redis))+type, paramMap);
			if(rtn.contains("access_token is invalid or not latest hint")) {
				// 如果失败 就删除缓存token 再此获取1此
				redis.del("WX_ACCESS_TOKEN");
				token = CoreService.getAccessToken(redis);
				rtn = HttpClientUtil.sendHttpsRequest("get",WXConstants.GET_TICKET_URL.replace("ACCESS_TOKEN", getAccessToken(redis))+type, paramMap);
			}
			JSONObject json = JSONObject.parseObject(rtn);
			
			token= json.getString("ticket");
			redis.setData("WX_TICKET_" + type.toUpperCase(), token, 7000);	//微信有效期7200 避免出错缓存7000秒
		}
		
		return token;
	}
	
	public static void sendToKF(String msgType,String content,String fromUserName,RedisUtil redis) throws UnsupportedEncodingException {
		if("oPt80swJ4rg3IhNeZ7Eyg5yFZ4HI".equals(fromUserName)) {
			String lastFrom = redis.getData("WX_LAST_CUSTOMER");
			if(!StringUtils.isEmpty(lastFrom)) {
				JSONObject json = new JSONObject();
				json.put("touser", lastFrom);
				json.put("msgtype", "text");

				JSONObject text = new JSONObject();
				json.put("text", text);
				text.put("content", content);
				
				HttpClientUtil.sendJsonDataByPost(WXConstants.SEND_KF_URL.replace("ACCESS_TOKEN", getAccessToken(redis)), json.toJSONString(), null);
			}
		} else {

			// 文本消息
			if (!msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_TEXT)) {
				content = "有客户需要客服";
			} 
			JSONObject json = new JSONObject();
			json.put("touser", "oPt80swJ4rg3IhNeZ7Eyg5yFZ4HI");
			json.put("msgtype", "text");

			JSONObject text = new JSONObject();
			json.put("text", text);
			text.put("content", content);
			
			redis.setData("WX_LAST_CUSTOMER", fromUserName, 7200);	//微信有效期7200 
			
			HttpClientUtil.sendJsonDataByPost(WXConstants.SEND_KF_URL.replace("ACCESS_TOKEN", getAccessToken(redis)), json.toJSONString(), null);
		}
		
	}
	public static void main(String ary[]) throws UnsupportedEncodingException {
		
		String str = "{'title':'大牌赠品试用品免费领>>','picUrl':'https://mmbiz.qpic.cn/mmbiz_jpg/QWwoFJKCGKTO7Ids7R8Pw6aCM2hg6SwFicVyz7rlXDJmezpA8cib5D25EpcL3w5RKALj9SJM1ZSWicUhibb8Fn6hibw/0?wx_fmt=jpeg','description':'0元领取下午茶，和同事一起分享美味吧~','linkUrl':'"+Constant.FACTORY_API_URL+"shiyongjihe.html'}";
		JSONObject jo= JSONObject.parseObject(str);
		
		String s = jo.toJSONString();
//		Map<String, Object> paramMap=new HashMap<String, Object>();
//		JSONObject json = new JSONObject();
//		json.put("touser", "oPt80swJ4rg3IhNeZ7Eyg5yFZ4HI");
//		json.put("msgtype", "text");
//
//		JSONObject text = new JSONObject();
//		json.put("text", text);
//		text.put("content", "Hello World");
//		
//		String rtn = HttpClientUtil.sendJsonDataByPost("https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=YR8kRMiJymB4iXefmgJMhZwuv9SR9_joUN7CxOB8Gzw4gDAegdcJBb6VR7WLHSH3Ka9PNvD9duwuVWJAHMRtu6t1oFy4TE0JWMRDAg-vW1DEkNZjd1p2JOadmWeBtwv3CPCgAGAGTG", json.toJSONString(), null);
//		//String rtn = HttpClientUtil.sendHttpsRequest("post","https://api.weixin.qq.com/cgi-bin/customservice/getkflist?access_token=YR8kRMiJymB4iXefmgJMhZwuv9SR9_joUN7CxOB8Gzw4gDAegdcJBb6VR7WLHSH3Ka9PNvD9duwuVWJAHMRtu6t1oFy4TE0JWMRDAg-vW1DEkNZjd1p2JOadmWeBtwv3CPCgAGAGTG", paramMap);
	}
}
