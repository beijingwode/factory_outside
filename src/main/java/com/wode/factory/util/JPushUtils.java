package com.wode.factory.util;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import cn.jpush.api.common.APIConnectionException;
import cn.jpush.api.common.APIRequestException;
import cn.jpush.api.push.PushClient;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Message;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.PushPayload.Builder;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.notification.Notification;

@Service("jpush")
public class JPushUtils {
	public static final String APP_TYPE_SHOP = "shop";

	private static Logger logger= LoggerFactory.getLogger(JPushUtils.class);
	
	private String appKey;
	private String masterSecret;
	private String shopAppKey;
	private String shopMasterSecret;
	
	PushPayload payload;
	
	PushPayload pl = null;
	public String getAppKey() {
		return appKey;
	}

	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}

	public String getMasterSecret() {
		return masterSecret;
	}

	public void setMasterSecret(String masterSecret) {
		this.masterSecret = masterSecret;
	}

	public String getShopAppKey() {
		return shopAppKey;
	}

	public void setShopAppKey(String shopAppKey) {
		this.shopAppKey = shopAppKey;
	}

	public String getShopMasterSecret() {
		return shopMasterSecret;
	}

	public void setShopMasterSecret(String shopMasterSecret) {
		this.shopMasterSecret = shopMasterSecret;
	}

	/**
	 * @return 
	 * 
	 */
	public PushClient getJPushClient(String appType){
		if(APP_TYPE_SHOP.equals(appType)) {
			return new PushClient(shopMasterSecret, shopAppKey);
		} else {
			return new PushClient(masterSecret, appKey);
		}
	}
	
//	/**
//	 * @param apnsProduction
//	 * @param timeToLive
//	 */
//	public JPushUtils(boolean apnsProduction, long timeToLive) {
//		super();
//		pc = new PushClient(masterSecret, appKey, apnsProduction, timeToLive);
//	}
//
//	/**
//	 * @param maxRetryTimes
//	 * @param proxy
//	 */
//	public JPushUtils(int maxRetryTimes, HttpProxy proxy) {
//		super();
//		pc = new PushClient(masterSecret, appKey, maxRetryTimes, proxy);
//	}

	/**
	 * 功能说明：推送通知 日期: 2015年1月21日 开发者:张晨旭
	 *
	 * @param alert
	 *            推送内容
	 * @param title
	 *            推送的标题
	 * @param extras
	 *            内容(map集合)
	 * @param pushDriver
	 *            推送的设备 (android、ios)
	 * @param pushType
	 *            推送的类型(别名、标签、registrationID)
	 * @param alias
	 * @return
	 * @throws APIConnectionException
	 * @throws APIRequestException
	 */
	
	public boolean sendNotification(String appType,String alert, String title,
			Map<String, String> extras, String pushDriver, String pushType,
			String pushName) {
		/**
		 * ios alias别名 registrationID tag标签
		 * 
		 * android alias registrationID tag
		 * */
		PushClient pc=getJPushClient(appType);
		PushPayload pl = null;
		Builder bd = PushPayload.newBuilder();
		try {
			/**
			 * Android 安卓+别名推送
			 * */
			if (pushDriver.equals("android") && pushType.equals("alias")) {
				pl = bd.setPlatform(Platform.android())
						.setAudience(Audience.alias(pushName))
						.setNotification(
								Notification.android(alert, title, extras))
						.build();
				/**
				 * 安卓+标签推送
				 * */
			} else if (pushDriver.equals("android") && pushType.equals("tag")) {
				pl = bd.setPlatform(Platform.android())
						.setAudience(Audience.tag(pushName))
						.setNotification(
								Notification.android(alert, title, extras))
						.build();
				/**
				 * 安卓+registrationID推送
				 * */
			} else if (pushDriver.equals("android")
					&& pushType.equals("registrationID")) {
				pl = bd.setPlatform(Platform.android())
						.setAudience(Audience.registrationId(pushName))
						.setNotification(
								Notification.android(alert, title, extras))
						.build();
				/**
				 * IOS ios+别名推送
				 * */
			} else if (pushDriver.equals("ios") && pushType.equals("alias")) {
				pl = bd.setPlatform(Platform.ios())
						.setAudience(Audience.alias(pushName))
						.setNotification(Notification.ios(alert, extras))
						.build();
				/**
				 * ios+标签推送
				 * */
			} else if (pushDriver.equals("ios") && pushType.equals("tag")) {
				pl = bd.setPlatform(Platform.ios())
						.setAudience(Audience.tag(pushName))
						.setNotification(Notification.ios(alert, extras))
						.build();
				/**
				 * ios+registrationID推送
				 * */
			} else if (pushDriver.equals("ios")
					&& pushType.equals("registrationID")) {
				pl = bd.setPlatform(Platform.ios())
						.setAudience(Audience.registrationId(pushName))
						.setNotification(Notification.ios(alert, extras))
						.build();
			}
			PushResult pr=  pc.sendPush(pl);
			return pr.isResultOK();
		} catch (APIConnectionException e) {
			logger.error(e.getMessage());
		} catch (APIRequestException e) {
			logger.error(e.getErrorMessage());
			
		}
		return false;
	}

	/**
	 * 功能说明：推送自定义消息
	 * 
	 * 日期: 2015年1月21日 开发者:张晨旭
	 *
	 * @param msgContent
	 *            推送内容
	 * @param title
	 *            推送的标题
	 * @param pushDriver
	 *            推送的设备(android、ios)
	 * @param pushType
	 *            推送的类型(标签、别名、registrationID)
	 * @param alias
	 *            对方的 别名/标签/registrationID
	 * @return
	 */
	/**
	 * 功能说明：
	 * 日期:	2015年1月21日
	 * 开发者:张晨旭
	 *
	 * @param msgContent
	 * @param title
	 * @param pushDriver
	 * @param pushType
	 * @param alias
	 * @return
	 */
	
	public boolean sendMessage(String appType,String msgContent, String title,
			Map<String, String> extras,String pushDriver, String pushType,String statusId , String... pushName) {
		
		if(extras==null) {
			extras = new HashMap<String, String>();
		}
		/**
		 * ios alias别名 registrationID tag标签
		 * 
		 * android alias registrationID tag
		 * */
		PushClient pc=getJPushClient(appType);
		PushPayload pl = null;
		Builder bd = PushPayload.newBuilder();
		try {
			/**
			 * 安卓+别名
			 * */
			if (pushDriver.equals("android") && pushType.equals("alias")) {
				pl = bd.setPlatform(Platform.android())
						.setAudience(Audience.alias(pushName))
						.setMessage(
								Message.newBuilder().setTitle(title)
										.setMsgContent(msgContent).addExtras(extras).build())
						.build();
				/**
				 * 安卓+标签
				 * */
			} else if (pushDriver.equals("android") && pushType.equals("tag")) {
				pl = bd.setPlatform(Platform.android())
						.setAudience(Audience.tag(pushName))
						.setMessage(
								Message.newBuilder().setTitle(title)
										.setMsgContent(msgContent).addExtras(extras).build())
						.build();
				/**
				 * 安卓+registrationID
				 * */
			} else if (pushDriver.equals("android")
					&& pushType.equals("registrationID")) {
				pl = bd.setPlatform(Platform.android())
						.setAudience(Audience.registrationId(pushName))
						.setMessage(
								Message.newBuilder().setTitle(title)
										.setMsgContent(msgContent).setContentType(statusId).addExtras(extras).build())
						.build();
				/**
				 * ios+registrationID
				 * */
			} else if (pushDriver.equals("ios")
					&& pushType.equals("registrationID")) {
				pl = bd.setPlatform(Platform.ios())
						.setAudience(Audience.registrationId(pushName))
						.setMessage(
								Message.newBuilder().setTitle(title)
										.setMsgContent(msgContent).addExtras(extras).build())
						.build();
				/**
				 * ios+标签
				 * */
			} else if (pushDriver.equals("ios") && pushType.equals("tag")) {
				pl = bd.setPlatform(Platform.ios())
						.setAudience(Audience.tag(pushName))
						.setMessage(
								Message.newBuilder().setTitle(title)
										.setMsgContent(msgContent).addExtras(extras).build())
						.build();
				/**
				 * ios+别名
				 * */
			} else if (pushDriver.equals("ios") && pushType.equals("alias")) {
				pl = bd.setPlatform(Platform.ios())
						.setAudience(Audience.alias(pushName))
						.setMessage(
								Message.newBuilder().setTitle(title)
										.setMsgContent(msgContent).addExtras(extras).build())
						.build();
			}
			PushResult pr =  pc.sendPush(pl);
			return pr.isResultOK();
		} catch (APIConnectionException e) {
			e.printStackTrace();
		} catch (APIRequestException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static String formatDriver(String type) {
		if("iPhone".equals(type)) return "ios";
		return type;
	}
	/**
	 * 功能说明：推送的格式
	 * 日期:	2015年3月17日
	 * 开发者:张晨旭
	 * 版本号:1.1
	 *
	 * @param json
	 * @return
	 */
	public Map<String,String> pushFormat(String json){
		Map<String,String> map = new HashMap<String, String>();
		map.put("push", json);
		return map;
	}

}
