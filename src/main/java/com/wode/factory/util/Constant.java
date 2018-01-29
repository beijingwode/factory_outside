package com.wode.factory.util;

import java.util.ResourceBundle;

public class Constant {

	private static ResourceBundle res = ResourceBundle.getBundle("application");

	//快递接口Domain
	public static String EXPRESS_API_URL = res.getString("express.api.url");
	//我的网接口Domain
	public static String FACTORY_API_URL = res.getString("factory.api.url");
	//我的网接口Domain
	public static String PASSPORT_API_URL = res.getString("passport.api.url");

	// 微信公众号
	public static String WX_OPEN_APP_ID = res.getString("wxOpen.appId");
	public static String WX_OPEN_SECRET = res.getString("wxOpen.secret");
	
	//支付 
	//合作身份者ID
	public static String ALIPAY_PARTNERID = res.getString("AlipayConfig.partner");
	//收款支付宝账号
	public static String ALIPAY_SELLER_EMAIL = res.getString("AlipayConfig.seller.email");
	//商户的私钥
	public static String ALIPAY_KEY = res.getString("AlipayConfig.key");
	//调试用，创建TXT日志文件夹路径
	public static String ALIPAY_LOG_PATH = res.getString("AlipayConfig.log.path");
	//字符编码格式 目前支持 gbk 或 utf-8
	public static String ALIPAY_INPUT_CHARSET = res.getString("AlipayConfig.input.charset");
	// 签名方式 不需修改
	public static String ALIPAY_SIGN_TYPE = res.getString("AlipayConfig.sign.type");
	//页面跳转同步通知页面路径
	public static String ALIPAY_RETURN_URL = res.getString("AlipayConfig.return.url");
	//服务器异步通知页面路径
	public static String ALIPAY_NOTIFY_URL = res.getString("AlipayConfig.notify.url");
	//支付类型
	public static String ALIPAY_PAYMENT_TYPE = res.getString("AlipayConfig.payment.type");
	
	//支付宝网关（固定）
	public static String ALIPAY_SERVERURL = res.getString("AlipayConfig.serverUrl");
	//APPID即创建应用后生成
	public static String ALIPAY_APP_ID = res.getString("AlipayConfig.appId");
	//开发者应用私钥，由开发者自己生成
	public static String ALIPAY_PRIVATE_KEY = res.getString("AlipayConfig.privateKey");
	//支付宝公钥，由支付宝生成
	public static String ALIPAY_PULIC_KEY = res.getString("AlipayConfig.alipayPulicKey");
	//商户生成签名字符串所使用的签名算法类型，目前支持RSA2和RSA，推荐使用RSA2
	public static String ALIPAY_SIGNTYPE = res.getString("AlipayConfig.signType");
	//卖家ID web 用
	public static String ALIPAY_SELLER_ID = res.getString("AlipayConfig.seller_id");
}
