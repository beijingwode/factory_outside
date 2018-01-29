package com.wode.factory.pay.weixin;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.security.KeyStore;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.net.ssl.SSLContext;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.XmlFriendlyNameCoder;
import com.wode.factory.pay.weixin.exception.IllegalRequestException;

/**
 * Created by zoln on 2016/4/12.
 *
 * 创建微信请求
 */
public class RequestBuilder {

	private SortedMap<String, String> params = new TreeMap<>();
	private static Map<String,SSLConnectionSocketFactory> mapSslsf;
	
	public RequestBuilder(String type,String appId,String notifyUrl) {
		params.put(Constant.RequestParam.APP_ID, appId);
		params.put(Constant.RequestParam.MCH_ID,RequestBuilder.getMchId(appId));
		
		if("1".equals(type)) {
			// 公众号下单
			params.put(Constant.RequestParam.NOTIFY_URL, notifyUrl);
			params.put(Constant.RequestParam.TRADE_TYPE, Constant.OPEN_TRADE_TYPE);
		} else if("WEB".equals(type)) {
			// 网页下单
			params.put(Constant.RequestParam.NONCE_STR, new Double(Math.random()).toString());
			params.put(Constant.RequestParam.NOTIFY_URL, notifyUrl);
			params.put(Constant.RequestParam.DEVICE_INFO, Constant.DEVICE_INFO);
			params.put(Constant.RequestParam.TRADE_TYPE, Constant.WEB_TRADE_TYPE);
			
		} else if("REFUND".equals(type)) {
			// 退款
			params.put(Constant.RequestParam.NONCE_STR, new Double(Math.random()).toString());
			
		} else if("app".equals(type)) {
			// app下单
			params.put(Constant.RequestParam.NOTIFY_URL, notifyUrl);
			params.put(Constant.RequestParam.TRADE_TYPE, Constant.TRADE_TYPE);
		}
	}

	public void setOutRefundNo(String outRefundNo) throws IllegalRequestException {
		if(StringUtils.isNotBlank(outRefundNo)) {
			params.put(Constant.RefundRequestParam.OUT_REFUND_NO, outRefundNo);
		} else {
			throw new IllegalRequestException("out_refund_no为空");
		}
	}
	

	public void setOutTradeNo(String tradeNo) throws IllegalRequestException {
		if(StringUtils.isNotBlank(tradeNo)) {
			params.put(Constant.RequestParam.OUT_TRADE_NO, tradeNo);
		} else {
			throw new IllegalRequestException("out_trade_no为空");
		}
	}
	
	public void setProductId(String productId) throws IllegalRequestException {
		if(StringUtils.isNotBlank(productId)) {
			params.put(Constant.RequestParam.PRODUCT_ID, productId);
		} else {
			throw new IllegalRequestException("product_id内容为空");
		}
	}
	
	public void setNonceStr(String str) {
		params.put(Constant.RequestParam.NONCE_STR, str);
	}

	public void setIp(String ip) {
		params.put(Constant.RequestParam.IP,ip);
	}

	public void setDeviceInfo(String deviceInfo) {
		params.put(Constant.RequestParam.DEVICE_INFO, deviceInfo);
	}


	public void setBody(String body) throws IllegalRequestException {
		if(StringUtils.isNotBlank(body)) {
			params.put(Constant.RequestParam.BODY, body);
		} else {
			throw new IllegalRequestException("body(商品描述)内容为空.");
		}
	}
	public void setOpenId(String openId) throws IllegalRequestException {
		if(StringUtils.isNotBlank(openId)) {
			params.put(Constant.RequestParam.openid, openId);
		}
	}

	public void setTotalFee(BigDecimal totalFee) throws IllegalRequestException {
		if (totalFee == null || totalFee.doubleValue() <= 0) {
			throw new IllegalRequestException("total_fee金额错误, 需设置金额并大于0");
		} else {
			params.put(Constant.RequestParam.TOTAL_FEE, totalFee.multiply(new BigDecimal(100)).intValue()+"");
		}
	}
	
	public void setRefundFee(BigDecimal totalFee) throws IllegalRequestException {
		if (totalFee == null || totalFee.doubleValue() <= 0) {
			throw new IllegalRequestException("refund_fee金额错误, 需设置金额并大于0");
		} else {
			params.put(Constant.RefundRequestParam.REFUND_FEE, totalFee.multiply(new BigDecimal(100)).intValue()+"");
		}
	}
	public void setOpUserId(String type) throws IllegalRequestException {
		String mchId = "";
		if("1".equals(type)) {
			// 公众号下单
			mchId= Constant.OPEN_MCH_ID;
		} else if("WEB".equals(type)) {
			// 网页下单
			mchId= Constant.OPEN_MCH_ID;
		} else {
			// app下单
			mchId= Constant.MCH_ID;
		}
		params.put(Constant.RefundRequestParam.OP_USER_ID, mchId);
	}

	private void setSign() {
		params.put(Constant.RequestParam.SIGN, WxPayService.getSign(params));
	}

	public String getXmlResult() throws IllegalRequestException {
		if(StringUtils.isBlank(params.get(Constant.RequestParam.OUT_TRADE_NO).toString())) {
			throw new IllegalRequestException("out_trad_no不可以为空");
		}
		if(StringUtils.isBlank(params.get(Constant.RequestParam.BODY).toString())) {
			throw new IllegalRequestException("body(商品描述)不可以为空");
		}
		String totalFee = params.get(Constant.RequestParam.TOTAL_FEE);
		if(totalFee == null || new Double(totalFee) <= 0) {
			throw new IllegalRequestException("total_fee金额错误, 需设置金额并大于0");
		}
		setSign();
		XStream xs = new XStream(new DomDriver("UTF-8",new XmlFriendlyNameCoder("-_", "_")));
		xs.registerConverter(new MapEntryConverter());
		xs.alias("xml", TreeMap.class);
		System.out.println(xs.toXML(this.params));
		return xs.toXML(this.params);
	}


	public String getRefundXmlResult() throws IllegalRequestException {

		params.put("transaction_id", "");
		setSign();
		XStream xs = new XStream(new DomDriver("UTF-8",new XmlFriendlyNameCoder("-_", "_")));
		xs.registerConverter(new MapEntryConverter());
		xs.alias("xml", TreeMap.class);
		System.out.println(xs.toXML(this.params));
		return xs.toXML(this.params);
	}
	
	public SortedMap<String, String> getResult() {
		setSign();
		return this.params;
	}
	
	public static String getMchId(String appId) {
		if(Constant.OPEN_APP_ID.equals(appId)) {
			return Constant.OPEN_MCH_ID;
		} else {
			return Constant.MCH_ID;
		}
	}
	
	public static String getAppId(String type) {
		if("1".equals(type)) {
			// 公众号下单
			return Constant.OPEN_APP_ID;
		} else if("WEB".equals(type)) {
			// 网页下单
			return Constant.OPEN_APP_ID;			
		} else if("app".equals(type)) {
			return Constant.APP_ID;
		}
		
		return null;
	}
	
	public static String getCertFilePath(String appId) {
		if(Constant.OPEN_APP_ID.equals(appId)) {
			return Constant.OPEN_API_CRTT_P12_PATH;
		} else {
			return Constant.API_CRTT_P12_PATH;
		}
	}
	
	static synchronized SSLConnectionSocketFactory getSSLsf(String appId){
		if(mapSslsf == null) {
			mapSslsf = new HashMap<String, SSLConnectionSocketFactory>();
		}
		
		try{
			if(!mapSslsf.containsKey(appId)) {
				String mchId = RequestBuilder.getMchId(appId);
				KeyStore keyStore  = KeyStore.getInstance("PKCS12");
				 FileInputStream instream = new FileInputStream(new File(RequestBuilder.getCertFilePath(appId)));//放退款证书的路径
		        try {
		            keyStore.load(instream, mchId.toCharArray());
		        } finally { 
		            instream.close();
		        }
		        SSLContext sslcontext = SSLContexts.custom().loadKeyMaterial(keyStore, mchId.toCharArray()).build();
		        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
		                sslcontext,
		                new String[] { "TLSv1" },
		                null,
		                SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
		        
		        mapSslsf.put(appId, sslsf);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return mapSslsf.get(appId);
	}
}
