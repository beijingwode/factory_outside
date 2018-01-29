package com.wode.factory.pay.weixin;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.security.Key;
import java.security.Security;
import java.util.Calendar;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.wode.common.util.ActResult;
import com.wode.common.util.StringUtils;

/**
 * Created by zoln on 2016/4/8.
 */
@Service("wxPayService")
public class WxPayService {

	private static Logger logger = LoggerFactory.getLogger(WxPayService.class);
	public static final String CIPHER_ALGORITHM = "AES/ECB/PKCS7Padding";     
	public static boolean initialized = false;  
	/**
	 * 处理微信支付成功后返回的信息
	 * @param xmlResponse
	 * @return
	 */
	public ActResult<String> handlePayResult(String xmlResponse) {
		XStream xs = new XStream(new DomDriver());
		xs.registerConverter(new MapEntryConverter());
		xs.alias("xml", TreeMap.class);
		SortedMap<String, String> responseMap = (SortedMap<String, String>)xs.fromXML(xmlResponse);
		if(responseMap.get(Constant.PayResultResponseParam.RETURN_CODE).equals(Constant.ReturnCode.SUCCESS)) {
			String tradeNo = responseMap.get(Constant.PayResultResponseParam.OUT_TRADE_NO);
			if(responseMap.get(Constant.PayResultResponseParam.RESULT_CODE).equals(Constant.ReturnCode.SUCCESS)) {
				String sign = getSign(responseMap);
				if(sign.equals(responseMap.get(Constant.PayResultResponseParam.SIGN))) {
					ActResult<String> act = ActResult.success(tradeNo);
					act.setMsg(responseMap.get(Constant.PayResultResponseParam.TRANSACTION_ID));
					logger.info(responseMap.toString());
					return act;
				} else {
					return ActResult.fail("签名验证失败");
				}
			} else {
				logger.error(tradeNo + " --xxxxxxx-- " + responseMap.get(Constant.PayResultResponseParam.ERR_CODE_DES));
				return ActResult.fail(responseMap.get(Constant.PayResultResponseParam.ERR_CODE_DES));
			}
		} else {
			logger.error(responseMap.get(Constant.PayResultResponseParam.RETURN_MSG));
			return ActResult.fail(responseMap.get(Constant.PayResultResponseParam.RETURN_MSG));
		}
	}

	/**
	 * 处理微信退款成功后返回的信息
	 * @param xmlResponse
	 * @return
	 * @throws Exception 
	 */
	public ActResult<String> handleRefundResult(String xmlResponse) {
		XStream xs = new XStream(new DomDriver());
		xs.registerConverter(new MapEntryConverter());
		xs.alias("xml", TreeMap.class);
		SortedMap<String, String> responseMap = (SortedMap<String, String>)xs.fromXML(xmlResponse);
		if(responseMap.get(Constant.PayResultResponseParam.RETURN_CODE).equals(Constant.ReturnCode.SUCCESS)) {
			String reqInfo = responseMap.get(Constant.PayResultResponseParam.REQ_INFO);
			initialize();
			
			//（1）对加密串A做base64解码，得到加密串B
			byte[] B = org.apache.commons.codec.binary.Base64.decodeBase64(reqInfo.getBytes());
			//（2）对商户key做md5，得到32位小写key* ( key设置路径：微信商户平台(pay.weixin.qq.com)-->账户设置-->API安全-->密钥设置 )
			String key = DigestUtils.md5Hex(Constant.KEY).toLowerCase();
			//（3）用key*对加密串B做AES-256-ECB解密
			byte[] rtn;
			try {
				rtn = decrypt(B,key);
				xs.alias("root", TreeMap.class);
				responseMap = (SortedMap<String, String>)xs.fromXML(new String(rtn));
				String tradeNo = responseMap.get(Constant.PayResultResponseParam.OUT_REFUND_NO);

				ActResult<String> act = ActResult.success(tradeNo);
				act.setMsg(responseMap.get(Constant.PayResultResponseParam.TRANSACTION_ID) + "_" + responseMap.get(Constant.PayResultResponseParam.REFUND_STATUS));
				return act;
			} catch (Exception e) {
				return ActResult.fail("解密失败");
			}
		} else {
			logger.error(responseMap.get(Constant.PayResultResponseParam.RETURN_MSG));
			return ActResult.fail(responseMap.get(Constant.PayResultResponseParam.RETURN_MSG));
		}
	}
	
	/**
	 * 生成微信支付预支付订单
	 * @param request   HttpServletRequest
	 * @param payment   支付方式对象
	 * @return
	 * @throws Exception
	 */
	public Map<String, String> unifiedOrder(String ip, String body,String outTradeNo,BigDecimal totalFee,String noncestr,String wxOpen,String openId,boolean isOrder,String notifyUrl) throws Exception {

		RequestBuilder req = new RequestBuilder(wxOpen,RequestBuilder.getAppId(wxOpen),notifyUrl);
		
		req.setOutTradeNo(outTradeNo);
		req.setIp(ip);
		if(isOrder) {
			if(!StringUtils.isEmpty(body)) {
				if(body.length()>32) {
					body=body.substring(0, 32);
				}
			} 
			req.setBody(body);
		} else {
			req.setBody("我的福利现金券充值");
		}
		req.setOpenId(openId);
		req.setNonceStr(noncestr);
		req.setTotalFee(totalFee);
		HttpClient client = new HttpClient();
		client.getParams().setContentCharset("UTF-8");
		client.getParams().setHttpElementCharset("UTF-8");
		PostMethod post = new PostMethod(Constant.UNIFIED_ORDER_URL);
		StringRequestEntity se = new StringRequestEntity(req.getXmlResult(), "text/xml", "UTF-8");
		post.setRequestEntity(se);
		int code = client.executeMethod(post);
		if (code == 200) {
			String responseXml = post.getResponseBodyAsString();
			logger.debug(responseXml);
			XStream xs = new XStream(new DomDriver());
			xs.registerConverter(new MapEntryConverter());
			xs.alias("xml", TreeMap.class);
			return (Map<String, String>)xs.fromXML(responseXml);
		}
		return null;
	}
	/**
	 * 生成微信支付预支付订单
	 * @param request   HttpServletRequest
	 * @param payment   支付方式对象
	 * @return
	 * @throws Exception
	 */
	public Map<String, String> unifiedOrderWeb(String ip, String productId,String body,String outTradeNo,BigDecimal totalFee,String notifyUrl) throws Exception {
		RequestBuilder req = new RequestBuilder("WEB",RequestBuilder.getAppId("WEB"),notifyUrl);
		req.setOutTradeNo(outTradeNo);
		req.setIp(ip);
		req.setProductId(productId);
		req.setBody(body);
		req.setTotalFee(totalFee);
		HttpClient client = new HttpClient();
		client.getParams().setContentCharset("UTF-8");
		client.getParams().setHttpElementCharset("UTF-8");
		PostMethod post = new PostMethod(Constant.UNIFIED_ORDER_URL);
		StringRequestEntity se = new StringRequestEntity(req.getXmlResult(), "text/xml", "UTF-8");
		post.setRequestEntity(se);
		int code = client.executeMethod(post);
		if (code == 200) {
			String responseXml = post.getResponseBodyAsString();
			XStream xs = new XStream(new DomDriver());
			xs.registerConverter(new MapEntryConverter());
			xs.alias("xml", TreeMap.class);
			return (Map<String, String>)xs.fromXML(responseXml);
		}
		return null;
	}
	/**
	 * 生成微信支付预支付订单
	 * @param request   HttpServletRequest
	 * @param payment   支付方式对象
	 * @return
	 * @throws Exception
	 */
	public void payRefund(String outTradeNo,BigDecimal totalFee,String appId,BigDecimal refundFee,String outRefundNo) throws Exception {
		RequestBuilder req = new RequestBuilder("REFUND",appId,null);	// 微信退款时不需回调

		//公众账号ID 	appid 	是 	String(32) 	wx8888888888888888 	微信分配的公众账号ID（企业号corpid即为此appId）
		//商户号 	mch_id 	是 	String(32) 	1900000109 	微信支付分配的商户号
		//设备号 	device_info 	否 	String(32) 	013467007045764 	终端设备号
		//随机字符串 	nonce_str 	是 	String(32) 	5K8264ILTKCH16CQ2502SI8ZNMTM67VS 	随机字符串，不长于32位。推荐随机数生成算法
		//签名 	sign 	是 	String(32) 	C380BEC2BFD727A4B6845133519F3AD6 	签名，详见签名生成算法
		//签名类型 	sign_type 	否 	String(32) 	HMAC-SHA256 	签名类型，目前支持HMAC-SHA256和MD5，默认为MD5
		//微信订单号 	transaction_id 	二选一 	String(28) 	1217752501201407033233368018 	微信生成的订单号，在支付通知中有返回
		//商户订单号 	out_trade_no 	String(32) 	1217752501201407033233368018 	商户侧传给微信的订单号
		req.setOutTradeNo(outTradeNo);
		//商户退款单号 	out_refund_no 	是 	String(32) 	1217752501201407033233368018 	商户系统内部的退款单号，商户系统内部唯一，同一退款单号多次请求只退一笔
		req.setOutRefundNo(outRefundNo);
		//订单金额 	total_fee 	是 	Int 	100 	订单总金额，单位为分，只能为整数，详见支付金额
		req.setTotalFee(totalFee);
		//退款金额 	refund_fee 	是 	Int 	100 	退款总金额，订单总金额，单位为分，只能为整数，详见支付金额
		req.setRefundFee(refundFee);
		//货币种类 	refund_fee_type 	否 	String(8) 	CNY 	货币类型，符合ISO 4217标准的三位字母代码，默认人民币：CNY，其他值列表详见货币类型
		req.setOpUserId(RequestBuilder.getMchId(appId));
		//操作员 	op_user_id 	是 	String(32) 	1900000109 	操作员帐号, 默认为商户号
		        
        CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(RequestBuilder.getSSLsf(appId)).build();
        
        try {

            HttpPost httpPost = new HttpPost(Constant.PAY_REFUND_URL);//退款接口
            
            StringEntity  reqEntity  = new StringEntity(req.getRefundXmlResult());
            // 设置类型
            reqEntity.setContentType("application/x-www-form-urlencoded");
            httpPost.setEntity(reqEntity);
            CloseableHttpResponse response = httpclient.execute(httpPost);
            try {
                HttpEntity entity = response.getEntity();

                if (entity != null) {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(entity.getContent(),"UTF-8"));
                    String text;
                    while ((text = bufferedReader.readLine()) != null) {
                        System.out.println(text);
                    }
                }
                EntityUtils.consume(entity);
            } catch (Exception e) {
            	e.printStackTrace();
            } finally {
                response.close();
            }
        } catch (Exception e) {
        	e.printStackTrace();
        } finally {
            httpclient.close();
        }
	}
	
	/**
	 * 生成微信支付预支付订单
	 * @param request   HttpServletRequest
	 * @param payment   支付方式对象
	 * @return
	 * @throws Exception
	 */
	public Map<String, String> convertForApp(Map<String, String> result,String nonceStr,String out_trade_no) {

		SortedMap<String, String> params = new TreeMap<>();
		//req.appId			= json.getString("appid");
		params.put("appid", result.get(Constant.PrepayResponseParam.APP_ID));
		//req.partnerId		= json.getString("partnerid");
		params.put("partnerid", Constant.MCH_ID);
		//req.prepayId		= json.getString("prepayid");
		params.put("prepayid", result.get(Constant.PrepayResponseParam.PREPAY_ID));
		//req.packageValue	= json.getString("package");
		params.put("package", "Sign=WXPay");
		//req.nonceStr		= json.getString("noncestr");
		params.put("noncestr", nonceStr);
		//req.timeStamp		= json.getString("timestamp");
		params.put("timestamp",Calendar.getInstance().getTimeInMillis()/1000+"");
		//req.sign			= json.getString("sign");
		params.put("sign", getSign(params));// result.get(Constant.PrepayResponseParam.sign)); //

		params.put("out_trade_no", out_trade_no);// result.get(Constant.PrepayResponseParam.sign)); //
		return params;
	}
	


	/**
	 * 生成微信支付预支付订单
	 * @param request   HttpServletRequest
	 * @param payment   支付方式对象
	 * @return
	 * @throws Exception
	 */
	public Map<String, String> convertForOpen(Map<String, String> result,String nonceStr,String out_trade_no) {

		SortedMap<String, String> params = new TreeMap<>();
		//req.appId			= json.getString("appid");
		params.put("appId", result.get(Constant.PrepayResponseParam.APP_ID));
		//req.timeStamp		= json.getString("timestamp");
		params.put("timeStamp",Calendar.getInstance().getTimeInMillis()/1000+"");
		//req.nonceStr		= json.getString("noncestr");
		params.put("nonceStr", nonceStr);
		//req.packageValue	= json.getString("package");
		params.put("package", "prepay_id="+result.get(Constant.PrepayResponseParam.PREPAY_ID));
		//"signType":"MD5",         //微信签名方式：     
		params.put("signType", "MD5");
		//req.sign			= json.getString("sign");
		params.put("paySign", getSign(params));// result.get(Constant.PrepayResponseParam.sign)); //

		params.put("prepayid", result.get(Constant.PrepayResponseParam.PREPAY_ID));// result.get(Constant.PrepayResponseParam.sign)); //
		params.put("out_trade_no", out_trade_no);// result.get(Constant.PrepayResponseParam.sign)); //
		return params;
	}
	/**
	 * 获取sign签名
	 * @param map 请求参数Map
	 * @return
	 */
	public static String getSign(SortedMap<String, String> map) {
		Set<String> keys = map.keySet();
		StringBuilder sb = new StringBuilder();
		for(String key : keys) {
			if(!key.equals(Constant.PrepayResponseParam.SIGN) && !StringUtils.isEmpty(map.get(key))) {
				sb.append(key).append("=").append(map.get(key)).append("&");
			}
		}
		sb.append("key=").append(Constant.KEY);
		return DigestUtils.md5Hex(sb.toString()).toUpperCase();
	}
	/**
     * 解密
     * @param content
     *            待解密内容
     * @return
     */
    public static byte[] decrypt(byte[] data,String key) throws Exception {
        Key k = toKey(key.getBytes());
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM,"BC");
        cipher.init(Cipher.DECRYPT_MODE, k);
        return cipher.doFinal(data);
    }
	private static Key toKey(byte[] key) throws Exception {
        SecretKey secretKey = new SecretKeySpec(key, "AES");
        return secretKey;
    }
	
    public static void initialize(){  
        if (initialized) return;  
        Security.addProvider(new BouncyCastleProvider());  
        initialized = true;  
    }  
}
