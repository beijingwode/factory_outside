package com.wode.factory.util.weixin;

import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSONObject;
import com.wode.factory.util.Constant;

public class TempletMessageUtil {
	

    public static final String API_BASE_URL = Constant.FACTORY_API_URL;  
	/** 
     * 模板类型  新订单通知
     */  
    public static final String TEMPLATE_TYPE_NEW_ORDERS = "new_order";  
	/** 
     * 模板类型  返现通知
     */  
    public static final String TEMPLATE_TYPE_COMMENT_PRIZE = "comment_prize";  
	/** 
     * 模板类型  商品已发出通知
     */  
    public static final String TEMPLATE_TYPE_ORDER_SEND = "order_send";  
	/** 
     * 模板类型  到账提醒
     */  
    public static final String TEMPLATE_TYPE_BALANCE = "balace";  
	/** 
     * 模板类型  返利提醒
     */  
    public static final String TEMPLATE_TYPE_TRIAL_COMMENT = "trial_comment";
	/** 
     * 模板类型  专享商品上架通知
     */  
    public static final String TEMPLATE_TYPE_SPECIAL_SALE = "special_sale";    
	/** 
     * 模板类型  专享券通知
     */  
    public static final String TEMPLATE_TYPE_SPECIAL_SALE_TICKET = "special_sale_ticket";   
	/** 
     * 模板类型  完善资料通知
     */  
    public static final String TEMPLATE_TYPE_ADDITIONAL_INFO = "additional_info";   
	/** 
     * 模板类型 预订成功通知
     */  
    public static final String TEMPLATE_TYPE_RESERVATIONL_SUCCESS = "reservation_success";  
	/** 
     * 模板类型 预订失败通知
     */  
    public static final String TEMPLATE_TYPE_RESERVATIONL_FAILURE = "reservation_failure";  
	/** 
     * 模板类型 团购结算提醒
     */  
    public static final String TEMPLATE_TYPE_GROUP_BUY_CLOSE = "group_buy_close"; 
	/** 
     * 模板类型 中奖结果通知
     */  
    public static final String TEMPLATE_TYPE_WIN_PRIZE = "win_prize";  
	/** 
     * 模板id 新订单通知
     */  
    public static final String TEMPLATE_ID_NEW_ORDERS = "RK8SLtgtVs8a2sYoJhBU9sw5a0-1LJAKAwUPshtyGdI";  
	/** 
     * 模板id 返现到账通知
     */  
    public static final String TEMPLATE_COMMENT_PRIZE = "EjHdK9JNFtaQBDY0XamJ1iFh5ZO6To7rQ4S17v-AQfY";  
	/** 
     * 模板id 商品已发出通知
     */  
    public static final String TEMPLATE_ORDER_SEND = "Nd-7ScghNM2pgkszmyHhdcOzJJD2tsx7zMyLohadbUw";  
	/** 
     * 模板id 到账提醒
     */  
    public static final String TEMPLATE_BALANCE = "3z8FyHzG2wEMossXH96sDyGvC74PkLVaPjYBflBc_6o";  
	/** 
     * 模板id 返利提醒
     */  
    public static final String TEMPLATE_TRIAL_COMMENT = "jLnB0Y-Xefw4TtuGCvUd59iyGuPaNcHCMfYzKY0eVZg";  
	/** 
     * 模板id 专享商品上架通知
     */  
    public static final String TEMPLATE_SPECIAL_SALE = "oDq0CDwEQdIcKF8b7qHqJZzUrRJcY5YR7Zd1ThNTiYM";  
	/** 
     * 模板id 专享商品上架通知
     */  
    public static final String TEMPLATE_SPECIAL_SALE_TICKET = "gHyRquPRqOFOoH3upW5x0Wyl5TaJtPlHlQ79LFBdJEs";  
	/** 
     * 模板id 完善资料通知
     */  
    public static final String TEMPLATE_ADDITIONAL_INFO = "rb2pxnIZTiNV11_FX09fGJ1I8pWYGxkp-kgWryhaQsk";  
	/** 
     * 模板id 预订成功通知
     */  
    public static final String TEMPLATE_RESERVATIONL_SUCCESS = "UmCgScVlek7Ha_KwgY5ABLHiaXszB850l--QTgzlFiU";  
	/** 
     * 模板id 预订失败通知
     */  
    public static final String TEMPLATE_RESERVATIONL_FAILURE = "68kumC3yl6N3GiNTjoeH13C1Z5gfB3me2p0TzGREnqY";  
	/** 
     * 模板id 团购结算提醒
     */  
    public static final String TEMPLATE_GROUP_BUY_CLOSE = "LUS7HLo620U8JmsEfr2YiJw0MdtibuOH-XBLFdY5z-Y";  
	/** 
     * 模板id 中奖结果通知
     */  
    public static final String TEMPLATE_WIN_PRIZE = "ByS63CXsItqAxc4qt0fxq27LCJuD5F5bY9kWnA0Kvxg";  
     
    /** 
     * 返回消息类型：音乐 
     */  
    public static final String COLOR_TITLE = "#000000";  
    /** 
     * 返回消息类型：音乐 
     */  
    public static final String COLOR_DATA = "#000000";  

    /** 
     * 返回消息类型：音乐 
     */  
    public static final String COLOR_DATA_SPECIAL = "#FF4343";  

    /** 
     * 返回消息类型：音乐 
     */  
    public static final String COLOR_DATA_INFO = "#352DA7";  
  
    /**
     * 初始化共通 消息体
     * @param touser		toUser OpenId
     * @param template_id	模板id
     * @param url			点击后URL
     * @return
     */
    private static JSONObject initBody(String touser,String template_id,String url) {

		JSONObject body = new JSONObject();
		// touser
		body.put("touser", touser);
		// 模板id
		body.put("template_id", template_id);
		// 点击后URL
		body.put("url", url);
		return body;
    }
  
    /**
     * 初始化数据体
     * @param first		标题
     * @param remark	备注
     * @return
     */
    private static JSONObject initData(String first,String remark) {

		JSONObject data = new JSONObject();
		JSONObject firstO = new JSONObject();
		JSONObject remarkO = new JSONObject();
		// first
		firstO.put("value", first);
		firstO.put("color", COLOR_TITLE);
		// 模板id
		remarkO.put("value", remark);
		remarkO.put("color", COLOR_TITLE);
		
		// 组成消息体
		data.put("first", firstO);
		data.put("remark", remarkO);
		return data;
    }
    
    private static JSONObject initItem(String value) {
    	return initItem(value,COLOR_DATA);
    }

    private static JSONObject initItem(String value,String color) {

		JSONObject item = new JSONObject();
		item.put("value", value);
		item.put("color", color);
		
		return item;
    }
    /**
     * 新订单通知
     * @param openId	
     * @param shopName	
     * @param productName
     * @param createDate
     * @param amount
     * @param subOrderId
     * @return
     */
    private static JSONObject getNewOrderMsg(String openId,String shopName,String productName,String createDate,String amount,String subOrderId) {  

    	//初始化共通部分
		JSONObject body= initBody(openId, TEMPLATE_ID_NEW_ORDERS, "http://supplier.wd-w.com/app/suborder/orderDetail?status=1&flag=1&subOrderId="+subOrderId);

    	//初始化数据体
		JSONObject data= initData("您的店铺有新的订单生成。\n", "\n感谢您的使用,祝您生意兴隆。");
		
		//店铺名称：{{keyword1.DATA}}
		data.put("keyword1", initItem(shopName));
		//商品名称：{{keyword2.DATA}}
		data.put("keyword2", initItem(productName,COLOR_DATA_SPECIAL));
		//下单时间：{{keyword3.DATA}}
		data.put("keyword3", initItem(createDate));
		//下单金额：{{keyword4.DATA}}
		data.put("keyword4", initItem(amount));
		//付款状态：{{keyword5.DATA}}
		data.put("keyword5", initItem("已付款"));
		
		body.put("data", data);
		return body;
    }  

    /**
     * 试用奖励通知
     * @param openId
     * @param orderId
     * @param amount
     * @param userId
     * @return
     */
    private static JSONObject getCommentPrizeMsg(String openId,String orderId,String amount,Long userId) {  

    	//初始化共通部分
		JSONObject body= initBody(openId, TEMPLATE_COMMENT_PRIZE, API_BASE_URL+"bargainFlow?wxOpen=1&empId="+userId+"&cId=0");

    	//初始化数据体
		JSONObject data= initData("感谢您的积极参与，试用商品返现金额已到达您的现金券账户\n", "\n点击查看现金券账户及消费券明细。");
		
		//订单：{{order.DATA}}
		data.put("order", initItem(orderId));
		//返现金额：{{money.DATA}}
		data.put("money", initItem(amount,COLOR_DATA_INFO));
		
		body.put("data", data);
		return body;
    }  

    /**
     * 订单已发出通知
     * @param openId
     * @param subOrderId
     * @param productName
     * @param num
     * @param expressName
     * @param expressNo
     * @return
     */
    private static JSONObject getOrderSendMsg(String openId,String subOrderId,String productName,String num,String expressName,String expressNo) {  

    	//初始化共通部分
		JSONObject body= initBody(openId, TEMPLATE_ORDER_SEND, API_BASE_URL+"orderM?subOrderId="+subOrderId);

    	//初始化数据体
		JSONObject data= initData("亲，宝贝已经启程了，好想快点来到你身边\n", "\n商品信息："+productName+"\n商品数量：共"+num+"件\n\n点击查看订单详情。\n收到宝贝后，确认收货可以获得内购券，\n试用商品还可获得现金券奖励。");
		
		//快递公司：{{delivername.DATA}}
		data.put("delivername", initItem(expressName));
		//快递单号：{{ordername.DATA}}
		data.put("ordername", initItem(expressNo));
		
		body.put("data", data);
		return body;
    }

    /**
     * 到账提醒
     * @param openId
     * @param subOrderId
     * @param productName
     * @param num
     * @param expressName
     * @param expressNo
     * @return
     */
    public static JSONObject getBalaceMsg(String openId,String amount,String date,String note,Long userId,String cId) {

    	//初始化共通部分
    	String url = API_BASE_URL+"bargainFlow?wxOpen=1&empId="+userId+"&cId="+cId;
		String remark = "\n点击查看钱包>>";
		String first = "亲，您有一笔资金到账，请即时查收\n";
		if("0".equals(cId)) {
			first = "亲，您有一笔现金券到账，请即时查收\n";
		} else if("1".equals(cId)) {
			first = "亲，您有一笔内购券到账，请即时查收\n";
		} else if("3".equals(cId)) {
			first = "亲，您已收到一笔换领币。\n";
			remark = "换领活动已开启，大牌商品限时免费领~\r\n" + 
					"立即抢购>>";
			url = API_BASE_URL+"huanling.html";
		}
		JSONObject body= initBody(openId, TEMPLATE_BALANCE, url);
    	//初始化数据体
		JSONObject data= initData(first, remark);
		
		//到账金额：{{keyword1.DATA}}
		data.put("keyword1", initItem(amount,COLOR_DATA_INFO));
		//到账时间：{{keyword2.DATA}}
		data.put("keyword2", initItem(date));
		//到账详情：{{keyword3.DATA}}
		data.put("keyword3", initItem(note));
		
		body.put("data", data);
		return body;
    }

    /**
     * 返利提醒
     * @param openId
     * @param subOrderId
     * @param productName
     * @param num
     * @param expressName
     * @param expressNo
     * @return
     */
    private static JSONObject getTrialCommentMsg(String openId,String subOrderId,String productName,String amount,String prize,Long userId) {  

    	//初始化共通部分
		JSONObject body= initBody(openId, TEMPLATE_TRIAL_COMMENT, API_BASE_URL+"order/comments/getSubOrderInfo?subOrderId="+subOrderId + "&uid="+userId);

    	//初始化数据体
		JSONObject data= initData("您有试用商品需要评价，评价后可立即获得返现\n", "\n感谢你的使用，祝您购物愉快");
		
		//购买商品：{{keyword1.DATA}}
		data.put("keyword1", initItem(productName));
		//返       利：{{keyword2.DATA}}
		data.put("keyword2", initItem(prize,COLOR_DATA_SPECIAL));
		//成交金额：{{keyword3.DATA}}
		data.put("keyword3", initItem(amount));
		//到账日期：{{keyword4.DATA}}
		data.put("keyword4", initItem("评价后立即返现"));
		
		body.put("data", data);
		return body;
    }


    /**
     * 专享商品上架通知
     * @param openId
     * @param productId
     * @param productName
     * @param amount
     * @param date
     * @param comName
     * @return
     */
    private static JSONObject getSpecialSaleMsg(String openId,String productId,String productName,String amount,String date,String comName) {  

    	//初始化共通部分
		JSONObject body= initBody(openId, TEMPLATE_SPECIAL_SALE, API_BASE_URL+"productm?productId="+productId);

    	//初始化数据体
		JSONObject data= initData(comName+"为企业员工超低价福利商品", "\n点击查看商品详情");
		
		//订阅商品：{{keyword1.DATA}}
		data.put("keyword1", initItem(productName));
		//商品价格：{{keyword2.DATA}}
		data.put("keyword2", initItem(amount,COLOR_DATA_SPECIAL));
		//订阅时间：{{keyword3.DATA}}
		data.put("keyword3", initItem(date));
		
		body.put("data", data);
		return body;
    }

    private static JSONObject getSpecialSaleTicketMsg(Long userId,String openId,String specialSaleId,String first,String remark,String amount,String date,String detail) {  

    	String url=API_BASE_URL;
//    	if(specialSaleId.equals(-1L)) {
    		//换领券
    		url =url+ "bargainFlow?wxOpen=1&empId="+userId+"&cId=3";
//    	} else {
//    		//专享券
//    		url =url+ "supplierTicket/page?key="+specialSaleId;
//    	}
    	//初始化共通部分
		JSONObject body= initBody(openId, TEMPLATE_SPECIAL_SALE_TICKET,url);
    	//初始化数据体
		JSONObject data= initData(first, "\n"+remark);
		//储值：{{keyword1.DATA}}
		data.put("keyword1", initItem(amount,COLOR_DATA_SPECIAL));
		//详情：{{keyword2.DATA}}
		data.put("keyword2", initItem(detail));
		//时间：{{keyword3.DATA}}
		data.put("keyword3", initItem(date));
		
		body.put("data", data);
		return body;
    }

    private static JSONObject getAdditionalInfoMsg(String openId,String first,String comName,String info,String note) {  

    	//初始化共通部分
		JSONObject body= initBody(openId, TEMPLATE_ADDITIONAL_INFO, API_BASE_URL+"user/personal?pageId=4");

    	//初始化数据体
		JSONObject data= initData(first, "\n谢谢您的配合");
		//机构名称：{{keyword1.DATA}}
		data.put("keyword1", initItem(comName));
		//需完善的资料：{{keyword2.DATA}}
		data.put("keyword2", initItem(info));
		//备注：{{keyword3.DATA}}
		data.put("keyword3", initItem(note));
		
		body.put("data", data);
		return body;
    }
    
    /**
     * 预订成功通知
     * @param openId
     * @param first
     * @param remark
     * @param subOrderId
     * @param exchangeStatus
     * @return
     */
    private static JSONObject getReservationSuccessMsg(Long userId,String openId,String first,String remark,String subOrderId,String exchangeStatus) {  

    	//初始化共通部分
		JSONObject body= initBody(openId, TEMPLATE_RESERVATIONL_SUCCESS, API_BASE_URL+"exchangeOrder/myhl.user?uid="+userId);

    	//初始化数据体
		JSONObject data= initData(first, remark);
		//订单编号：{{keyword1.DATA}}
		data.put("keyword1", initItem(subOrderId));
		//订单状态：{{keyword2.DATA}}
		data.put("keyword2", initItem(exchangeStatus));
		
		body.put("data", data);
		return body;
    }
    
    /**
     * 预订成功通知
     * @param openId
     * @param first
     * @param remark
     * @param subOrderId
     * @param exchangeStatus
     * @return
     */
    private static JSONObject getReservationFailureMsg(Long userId,String openId,String first,String remark,String subOrderId,String exchangeStatus) {  

    	//初始化共通部分
		JSONObject body= initBody(openId, TEMPLATE_RESERVATIONL_FAILURE, API_BASE_URL+"exchangeOrder/myhl.user?uid="+userId);

    	//初始化数据体
		JSONObject data= initData(first, remark);
		//订单编号：{{keyword1.DATA}}
		data.put("keyword1", initItem(subOrderId));
		//订单状态：{{keyword2.DATA}}
		data.put("keyword2", initItem(exchangeStatus));
		
		body.put("data", data);
		return body;
    }
        
    /**
     * 团购结算提醒
     * @param userId
     * @param openId
     * @param first
     * @param remark
     * @param subOrderId
     * @param exchangeStatus
     * @return
     */
    private static JSONObject getGroupBuyCloseMsg(String openId,String first,String remark,String groupName,String time,String total,String refund,String fee) {  

    	//初始化共通部分
		JSONObject body= initBody(openId, TEMPLATE_GROUP_BUY_CLOSE, "http://wd-w.com/app.htm?d=1");

    	//初始化数据体
		JSONObject data= initData(first, remark);
		//团购名称：{{keyword1.DATA}}
		data.put("keyword1", initItem(groupName));
		//团购时间：{{keyword2.DATA}}
		data.put("keyword2", initItem(time));
		//订单总额：{{keyword3.DATA}}
		data.put("keyword3", initItem(total));
		//退款总额：{{keyword4.DATA}}
		data.put("keyword4", initItem(refund));
		//手续费：{{keyword5.DATA}}
		data.put("keyword5", initItem(fee));
		
		body.put("data", data);
		return body;
    }
    

    /**
     * 中奖结果通知
     * @param openId
     * @param first
     * @param remark
     * @param eventTitle
     * @param prizeName
     * @param date
     * @param url
     * @return
     */
    private static JSONObject getWinPrizeMsg(String openId,String first,String remark,String eventTitle,String prizeName,String date,String url) {  

    	//初始化共通部分
		JSONObject body= initBody(openId, TEMPLATE_WIN_PRIZE, url);

    	//初始化数据体
		JSONObject data= initData(first, remark);
		//活动：{{keyword1.DATA}}
		data.put("keyword1", initItem(eventTitle));
		//奖品：{{keyword2.DATA}}
		data.put("keyword2", initItem(prizeName));
		//时间：{{keyword3.DATA}}
		data.put("keyword3", initItem(date));
		
		body.put("data", data);
		return body;
    }
    /**
     * 根据类型创建 模板消息
     * @param request
     * @param openId
     * @param type
     * @param userId
     * @return
     */
    public static JSONObject makeBody(HttpServletRequest request,String openId,String type,Long userId) {
    	if(TEMPLATE_TYPE_NEW_ORDERS.equals(type)) {
    		return getNewOrderMsg(openId, request.getParameter("shopName"), request.getParameter("productName"), request.getParameter("createDate"), request.getParameter("amount"), request.getParameter("subOrderId"));
    	} else if(TEMPLATE_TYPE_COMMENT_PRIZE.equals(type)) {
    		return getCommentPrizeMsg(openId, request.getParameter("subOrderId"), request.getParameter("amount"), userId);
    	} else if(TEMPLATE_TYPE_ORDER_SEND.equals(type)) {
    		return getOrderSendMsg(openId, request.getParameter("subOrderId"), request.getParameter("productName"), request.getParameter("num"), request.getParameter("expressName"), request.getParameter("expressNo"));
    	} else if(TEMPLATE_TYPE_BALANCE.equals(type)) {
    		return getBalaceMsg(openId, request.getParameter("amount"), request.getParameter("date"), request.getParameter("note"), userId, request.getParameter("cId"));
    	} else if(TEMPLATE_TYPE_TRIAL_COMMENT.equals(type)) {
    		return getTrialCommentMsg(openId, request.getParameter("subOrderId"), request.getParameter("productName"), request.getParameter("amount"),request.getParameter("prize"), userId);
    	} else if(TEMPLATE_TYPE_SPECIAL_SALE.equals(type)) {
    		return getSpecialSaleMsg(openId, request.getParameter("productId"), request.getParameter("productName"), request.getParameter("amount"),request.getParameter("date"), request.getParameter("comName"));
    	} else if(TEMPLATE_TYPE_ADDITIONAL_INFO.equals(type)) {
    		return getAdditionalInfoMsg(openId, request.getParameter("first"), request.getParameter("comName"), request.getParameter("info"),request.getParameter("note"));
    	} else if(TEMPLATE_TYPE_SPECIAL_SALE_TICKET.equals(type)) {
    		return getSpecialSaleTicketMsg(userId,openId, request.getParameter("specialSaleId"), request.getParameter("first"), request.getParameter("remark"), request.getParameter("amount"),request.getParameter("date"), request.getParameter("detail"));
    	} else if(TEMPLATE_TYPE_RESERVATIONL_SUCCESS.equals(type)) {
    		return getReservationSuccessMsg(userId,openId,request.getParameter("first"), request.getParameter("remark"), request.getParameter("subOrderId"),request.getParameter("exchangeStatus"));
    	} else if(TEMPLATE_TYPE_RESERVATIONL_FAILURE.equals(type)) {
    		return getReservationFailureMsg(userId,openId,request.getParameter("first"), request.getParameter("remark"), request.getParameter("subOrderId"),request.getParameter("exchangeStatus"));
    	} else if(TEMPLATE_TYPE_GROUP_BUY_CLOSE.equals(type)) {
    		return getGroupBuyCloseMsg(openId,request.getParameter("first"), request.getParameter("remark"),request.getParameter("groupName"),request.getParameter("time"),request.getParameter("total"),request.getParameter("refund"),request.getParameter("fee"));
    	} else if(TEMPLATE_TYPE_WIN_PRIZE.equals(type)) {
    		return getWinPrizeMsg(openId,request.getParameter("first"), request.getParameter("remark"),request.getParameter("eventTitle"),request.getParameter("prizeName"),request.getParameter("date"),request.getParameter("url"));
    	} else {
    		return null;
    	}
    }
}
