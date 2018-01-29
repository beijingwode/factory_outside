package com.wode.factory.util.weixin;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;

import com.alibaba.fastjson.JSONObject;
import com.wode.common.redis.RedisUtil;
import com.wode.factory.util.Constant;

/**
 * 常量
 * @author herosky
 *
 */
public class WXConstants {
	/**
	 * APPID
	 */
	public static String APPID = Constant.WX_OPEN_APP_ID;
	/**
	 * SECRET
	 */
	public static String SECRET = Constant.WX_OPEN_SECRET;
	/**
	 * 获取ACCESS_TOKEN接口
	 */
	public static String GET_ACCESSTOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";
	/**
	 * 获取ACCESS_TOKEN接口
	 */
	public static String GET_TICKET_URL = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=ACCESS_TOKEN&type=";
	/**
	 * 删除菜单接口
	 */
	public static String DELETE_MENU_URL = "https://api.weixin.qq.com/cgi-bin/menu/delete?access_token=ACCESS_TOKEN";
	/**
	 * 创建菜单接口
	 */
	public static String CREATE_MENU_URL = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=ACCESS_TOKEN";
	/**
	 * 创建个性化菜单
	 */
	public static String CREATE_SPECIAL_MENU_URL = "https://api.weixin.qq.com/cgi-bin/menu/addconditional?access_token=ACCESS_TOKEN";
	/**
	 * 发送模板消息
	 */
	public static String SEND_TEMPLATE_URL = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=ACCESS_TOKEN";
	/**
	 * 批量设置用户标签
	 */
	public static String MEMBERS_BATCHTAGGING = "https://api.weixin.qq.com/cgi-bin/tags/members/batchtagging?access_token=ACCESS_TOKEN";
	/**
	 * 批量设置用户标签
	 */
	public static String SEND_KF_URL = "https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=ACCESS_TOKEN";
	/**
	 * 生成带参数的二维码
	 */
	public static String CREATE_QR_MORE_URL = "https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=ACCESS_TOKEN";
	/**
	 * 获取用户基本情报
	 */
	public static String GET_USER_BASE_INFO_URL = "https://api.weixin.qq.com/cgi-bin/user/info?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN";
	/**	
	 * 微信用户标签 shop
	 */
	public static String TAG_ID_SHOP = "100";
	
	
	/**
	 * ACCESS_TOKEN有效时间(单位：ms)
	 */
	public static int EFFECTIVE_TIME = 700000;
	/**
	 * conf.properties文件路径
	 */
	public static String CONF_PROPERTIES_PATH = "src/conf/config.properties";
	/**
	 * 微信接入token ，用于验证微信接口
	 */
	public static String TOKEN = "weixin";
	
	/**
	 * 微信菜单（普通员工）
	 * @param type
	 * @param baseUrl
	 * @return
	 */
	public static JSONObject makeMenuJson(String type, String baseUrl) {
		//菜单
		JSONObject menu = new JSONObject();
		List<JSONObject> button = new ArrayList<JSONObject>();
		/////////////////////////////////////我的福利///////////////////////////////////////////
		//我的福利
		JSONObject menu_1 = new JSONObject();
		menu_1.put("name", "我的福利");
		List<JSONObject> sub_button1 = new ArrayList<JSONObject>();
		// 搜索商品
		JSONObject menu_1_1 = new JSONObject();
		menu_1_1.put("name", "搜索商品");
		menu_1_1.put("type", "view");
		menu_1_1.put("url", baseUrl+"pSearch/page?wxOpen=1");
		sub_button1.add(menu_1_1);
		// 购物车
		JSONObject menu_1_2 = new JSONObject();
		menu_1_2.put("name", "购物车");
		menu_1_2.put("type", "view");
		menu_1_2.put("url", baseUrl+"cart/page?wxOpen=1");
		sub_button1.add(menu_1_2);
		menu_1.put("sub_button", sub_button1);
		button.add(menu_1);
		/////////////////////////////////////我的福利///////////////////////////////////////////

		/////////////////////////////////////福利内购///////////////////////////////////////////
		// 内购商城
		JSONObject menu_2 = new JSONObject();
		menu_2.put("name", "内购商城");
		menu_2.put("type", "view");
		menu_2.put("url", baseUrl+"index_m.htm?wxOpen=1");
		button.add(menu_2);
		/////////////////////////////////////福利内购///////////////////////////////////////////

		/////////////////////////////////////更多服务///////////////////////////////////////////
		//更多服务
		JSONObject menu_3 = new JSONObject();
		menu_3.put("name", "更多");
		List<JSONObject> sub_button3 = new ArrayList<JSONObject>();
		// 个人中心
		JSONObject menu_3_1 = new JSONObject();
		menu_3_1.put("name", "个人中心");
		menu_3_1.put("type", "view");
		menu_3_1.put("url", baseUrl+"user/page?wxOpen=1");
		sub_button3.add(menu_3_1);
		// 待支付订单
		JSONObject menu_3_2 = new JSONObject();
		menu_3_2.put("name", "待支付订单");
		menu_3_2.put("type", "view");
		menu_3_2.put("url", baseUrl+"order/page?wxOpen=1&status=0&pageId=1");
		sub_button3.add(menu_3_2);
		// 待收货订单
		JSONObject menu_3_3 = new JSONObject();
		menu_3_3.put("name", "待收货订单");
		menu_3_3.put("type", "view");
		menu_3_3.put("url", baseUrl+"order/page?wxOpen=1&status=2&pageId=3");
		sub_button3.add(menu_3_3);
		// 客服
		JSONObject menu_3_4 = new JSONObject();
		String kf="test";
		if("online".equals(type)) {
			kf="客服";
		} else if(baseUrl.contains("mdev")) {
			kf="dev";
		}
		menu_3_4.put("name", kf);
		menu_3_4.put("type", "click");
		menu_3_4.put("key", "service");
		sub_button3.add(menu_3_4);
		// 下载APP
		JSONObject menu_4_4 = new JSONObject();
		menu_4_4.put("name", "下载APP");
		menu_4_4.put("type", "view");
		menu_4_4.put("url", "http://wd-w.com/app.htm?d=1");
		sub_button3.add(menu_4_4);
				
		menu_3.put("sub_button", sub_button3);
		button.add(menu_3);
		/////////////////////////////////////更多服务///////////////////////////////////////////
		menu.put("button", button);
		return menu;
	}

	/**
	 * 微信菜单（商家）
	 * @param tagId
	 * @param baseUrl
	 * @return
	 */
	public static JSONObject makeSpecialMenuJson(String tagId, String baseUrl) {
		String shopBaseUrl = "http://supplier.wd-w.com/";
		//菜单
		JSONObject menu = new JSONObject();
		List<JSONObject> button = new ArrayList<JSONObject>();
		/////////////////////////////////////我的福利///////////////////////////////////////////
		//我的福利
		JSONObject menu_1 = new JSONObject();
		menu_1.put("name", "我的福利");
		List<JSONObject> sub_button1 = new ArrayList<JSONObject>();
		// 搜索商品
		JSONObject menu_1_1 = new JSONObject();
		menu_1_1.put("name", "搜索商品");
		menu_1_1.put("type", "view");
		menu_1_1.put("url", baseUrl+"pSearch/page?wxOpen=1");
		sub_button1.add(menu_1_1);
		// 购物车
		JSONObject menu_1_2 = new JSONObject();
		menu_1_2.put("name", "购物车");
		menu_1_2.put("type", "view");
		menu_1_2.put("url", baseUrl+"cart/page?wxOpen=1");
		sub_button1.add(menu_1_2);
		// 内购商城
		JSONObject menu_1_3 = new JSONObject();
		menu_1_3.put("name", "内购商城");
		menu_1_3.put("type", "view");
		menu_1_3.put("url", baseUrl+"index_m.htm?wxOpen=1");
		sub_button1.add(menu_1_3);
		
		menu_1.put("sub_button", sub_button1);
		button.add(menu_1);
		/////////////////////////////////////我的福利///////////////////////////////////////////

		/////////////////////////////////////店铺管理///////////////////////////////////////////
		//福利内购
		JSONObject menu_2 = new JSONObject();
		menu_2.put("name", "店铺管理");
		List<JSONObject> sub_button2 = new ArrayList<JSONObject>();
		// 店铺管理
		JSONObject menu_2_1 = new JSONObject();
		menu_2_1.put("name", "交易管理");
		menu_2_1.put("type", "view");
		menu_2_1.put("url", shopBaseUrl+"app/suborder/page");
		sub_button2.add(menu_2_1);
		// 商品管理
		JSONObject menu_2_2 = new JSONObject();
		menu_2_2.put("name", "商品管理");
		menu_2_2.put("type", "view");
		menu_2_2.put("url", shopBaseUrl+"app/product/page");
		sub_button2.add(menu_2_2);

		menu_2.put("sub_button", sub_button2);
		button.add(menu_2);
		/////////////////////////////////////福利内购///////////////////////////////////////////

		/////////////////////////////////////更多服务///////////////////////////////////////////
		//更多服务
		JSONObject menu_3 = new JSONObject();
		menu_3.put("name", "更多");
		List<JSONObject> sub_button3 = new ArrayList<JSONObject>();
		// 个人中心
		JSONObject menu_3_1 = new JSONObject();
		menu_3_1.put("name", "个人中心");
		menu_3_1.put("type", "view");
		menu_3_1.put("url", baseUrl+"user/page?wxOpen=1");
		sub_button3.add(menu_3_1);
		// 店铺中心
		JSONObject menu_3_2 = new JSONObject();
		menu_3_2.put("name", "店铺中心");
		menu_3_2.put("type", "view");
		menu_3_2.put("url",  shopBaseUrl+"app/shop/page");
		sub_button3.add(menu_3_2);
		// 客服
		JSONObject menu_3_4 = new JSONObject();
		menu_3_4.put("name", "客服");
		menu_3_4.put("type", "click");
		menu_3_4.put("key", "service");
		sub_button3.add(menu_3_4);
		// 下载APP
		JSONObject menu_4_4 = new JSONObject();
		menu_4_4.put("name", "我的福利APP");
		menu_4_4.put("type", "view");
		menu_4_4.put("url", "http://wd-w.com/app.htm?d=1");
		sub_button3.add(menu_4_4);
				
		menu_3.put("sub_button", sub_button3);
		button.add(menu_3);
		/////////////////////////////////////更多服务///////////////////////////////////////////

		/////////////////////////////////////个性化菜单///////////////////////////////////////////
		JSONObject matchrule =new JSONObject();
		matchrule.put("tag_id", tagId);	//用户标签
		/////////////////////////////////////个性化菜单///////////////////////////////////////////
		
		menu.put("button", button);
		menu.put("matchrule", matchrule);
		return menu;
	}
	
	public static void wxConfigJson(String url, JSONObject data,RedisUtil redisUtil) {
		url=url.replace("____", "&").replace("****", "=");
		SortedMap<String, String> params = new TreeMap<>();
		//noncestr
		Random random = new Random();
		String nonceStr= MD5.getMessageDigest(String.valueOf(random.nextInt(10000))
				.getBytes());
		params.put("noncestr", nonceStr);
		data.put("nonceStr", nonceStr);

		//jsapi_ticket
		String jsapi_ticket = CoreService.getTicket(redisUtil, "jsapi");
		params.put("jsapi_ticket", jsapi_ticket);
		data.put("jsapi_ticket", jsapi_ticket);
		
		//timestamp
		String timestamp =Calendar.getInstance().getTimeInMillis()/1000+"";
		params.put("timestamp", timestamp);
		data.put("timestamp", timestamp);
		
		//url
		params.put("url", url);
		data.put("url", url);
		
		//signature
		data.put("signature", SignUtil.getSignWithOutKey(params));
		
		//appId
		data.put("appId", WXConstants.APPID);
	}
}
