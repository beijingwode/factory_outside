package com.wode.factory.outside.controller;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.domain.AlipayTradeRefundModel;
import com.alipay.api.domain.AlipayTradeWapPayModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.wode.common.util.ActResult;
import com.wode.common.util.StringUtils;
import com.wode.factory.alipay.config.AlipayConfig;
import com.wode.factory.alipay.util.AlipayCore;
import com.wode.factory.alipay.util.AlipayNotify;
import com.wode.factory.alipay.util.AlipaySubmit;
import com.wode.factory.stereotype.NoCheckAsync;
import com.wode.factory.util.Constant;

@Controller
@RequestMapping("alipay")
@ResponseBody
public class AlipayController extends BaseController {

	private static Logger logger = LoggerFactory.getLogger(AlipayController.class);

	private AlipayClient alipayClient = new DefaultAlipayClient(Constant.ALIPAY_SERVERURL, Constant.ALIPAY_APP_ID,
			Constant.ALIPAY_PRIVATE_KEY, "json", "UTF-8", Constant.ALIPAY_PULIC_KEY, Constant.ALIPAY_SIGNTYPE);

	@RequestMapping("createDirectPay")
	@NoCheckAsync
	public ActResult<String> createDirectPay(String outTradeNo, String subject, BigDecimal money, String body,
			String showUrl, String notifyUrl, String returnUrl) {

		// 防钓鱼时间戳
		String anti_phishing_key = "";
		// 若要使用请调用类文件submit中的query_timestamp函数
		// 客户端的IP地址
		String exter_invoke_ip = "";
		// 非局域网的外网IP地址，如：221.0.0.1

		// 把请求参数打包成数组
		Map<String, String> sParaTemp = new HashMap<String, String>();
		sParaTemp.put("service", "create_direct_pay_by_user");
		sParaTemp.put("partner", AlipayConfig.partner);
		sParaTemp.put("seller_email", AlipayConfig.seller_email);
		sParaTemp.put("_input_charset", AlipayConfig.input_charset);
		sParaTemp.put("payment_type", AlipayConfig.payment_type);
		sParaTemp.put("notify_url", notifyUrl);
		sParaTemp.put("return_url", returnUrl);
		sParaTemp.put("out_trade_no", outTradeNo);
		sParaTemp.put("subject", subject);
		sParaTemp.put("total_fee", "" + money);
		sParaTemp.put("body", body);
		sParaTemp.put("show_url", showUrl);
		sParaTemp.put("anti_phishing_key", anti_phishing_key);
		sParaTemp.put("exter_invoke_ip", exter_invoke_ip);
		// 建立请求
		String result = AlipaySubmit.buildRequest(sParaTemp, "get", "确认");
		return ActResult.success(result);
	}

	@RequestMapping("oldAppPay")
	@NoCheckAsync
	public ActResult<String> oldAppPay(String outTradeNo,BigDecimal money,String notifyUrl) {
		
		String info = AlipayCore.makeAppOrder(outTradeNo, money.doubleValue(),notifyUrl);
		String sign = AlipayCore.appSign(info);
		sign = URLEncoder.encode(sign);
		info += "&sign=\"" + sign + "\"&sign_type=\"RSA\"";
		return ActResult.success(info);
	}
	
	@RequestMapping("webPay")
	@NoCheckAsync
	public ActResult<String> webPay(String outTradeNo, String subject, BigDecimal money, String body, String showUrl,
			String notifyUrl, String returnUrl) {

	    AlipayTradeWapPayRequest alipay_request= new AlipayTradeWapPayRequest(); // 创建API对应的request
	 // 封装请求支付信息
	    AlipayTradeWapPayModel model=new AlipayTradeWapPayModel();
	    model.setOutTradeNo(outTradeNo);
	    model.setSubject(subject);
	    model.setTotalAmount(money+"");
	    model.setBody(body);
	    model.setTimeoutExpress("20m");
	    model.setProductCode("");
	    alipay_request.setBizModel(model);
	    // 设置异步通知地址
	    alipay_request.setNotifyUrl(notifyUrl);
	    // 设置同步地址
	    alipay_request.setReturnUrl(returnUrl);   
	    
		
		try {
			// 这里和普通的接口调用不同，使用的是sdkExecute
			String form = alipayClient.pageExecute(alipay_request).getBody();
			return ActResult.success(form);
		} catch (AlipayApiException e) {
			e.printStackTrace();
			return ActResult.fail(e.getErrMsg());
		}
		
		
	}

	@RequestMapping("appPay")
	@NoCheckAsync
	public ActResult<String> appPay(String outTradeNo, String subject, BigDecimal money, String body, String showUrl,
			String notifyUrl) {
		// 实例化具体API对应的request类,类名称和接口名称对应,当前调用接口名称：alipay.trade.app.pay
		AlipayTradeAppPayRequest request = new AlipayTradeAppPayRequest();
		// SDK已经封装掉了公共参数，这里只需要传入业务参数。以下方法为sdk的model入参方式(model和biz_content同时存在的情况下取biz_content)。
		AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
		model.setBody(body);
		model.setSubject(subject);
		model.setOutTradeNo(outTradeNo);
		model.setTimeoutExpress("30m");
		model.setTotalAmount(money + "");
		model.setProductCode("");
		request.setBizModel(model);
		request.setNotifyUrl(notifyUrl);

		try {
			// 这里和普通的接口调用不同，使用的是sdkExecute
			AlipayTradeAppPayResponse response = alipayClient.sdkExecute(request);
			return ActResult.success(response.getBody());
		} catch (AlipayApiException e) {
			e.printStackTrace();
			return ActResult.fail(e.getErrMsg());
		}
	}

	@RequestMapping("refund")
	@NoCheckAsync
	public ActResult<String> refund(String outTradeNo, String tradeNo, String outRequestNo, BigDecimal refundAmount,String notifyUrl) {
		AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();//创建API对应的request类
		
		AlipayTradeRefundModel model=new AlipayTradeRefundModel();
		model.setOutTradeNo(outTradeNo);
		model.setTradeNo(tradeNo);
		model.setRefundAmount(refundAmount + "");
		model.setRefundReason("");
		model.setOutRequestNo(outRequestNo);
		request.setBizModel(model);
		request.setNotifyUrl(notifyUrl);

		try {
			AlipayTradeRefundResponse response = alipayClient.execute(request);//通过alipayClient调用API，获得对应的response类
			return ActResult.success(response.getBody());
		} catch (AlipayApiException e) {
			e.printStackTrace();
			return ActResult.fail(e.getErrMsg());
		}
	}

	@RequestMapping("verify")
	@NoCheckAsync
	public ActResult<String> verify(String params) {
		Map<String, String> map = null;
		if (!StringUtils.isEmpty(params)) {
			map = JSON.parseObject(params, Map.class);
		} else {
			return ActResult.fail("参数错误，参数为空");
		}

		boolean verify_result = AlipayNotify.verify(map);
		if (verify_result) {
			return ActResult.success("验证成功");
		} else {
			return ActResult.fail("验证失败");
		}
	}
	


	@RequestMapping("rsaCheckV1")
	@NoCheckAsync
	public ActResult<String> rsaCheckV1(String jsonParams) {

		Map<String, String> params = null;
		if (!StringUtils.isEmpty(jsonParams)) {
			params = JSON.parseObject(jsonParams, Map.class);
		} else {
			return ActResult.fail("参数错误，参数为空");
		}
		
		//切记alipaypublickey是支付宝的公钥，请去open.alipay.com对应应用下查看。
		//boolean AlipaySignature.rsaCheckV1(Map<String, String> params, String publicKey, String charset, String sign_type)
		boolean verify_result;
		try {
			verify_result = AlipaySignature.rsaCheckV1(params, Constant.ALIPAY_PULIC_KEY, "UTF-8", Constant.ALIPAY_SIGNTYPE);
			if (verify_result) {
				return ActResult.success("验证成功");
			} else {
				return ActResult.fail("验证失败");
			}
		} catch (AlipayApiException e) {
			return ActResult.fail(e.getErrMsg());
		}
	}
}
