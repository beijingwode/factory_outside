package com.wode.factory.outside.controller;

import java.math.BigDecimal;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wode.common.util.ActResult;
import com.wode.factory.pay.weixin.WxPayService;
import com.wode.factory.stereotype.NoCheckAsync;

@Controller
@RequestMapping("wxPay")
@ResponseBody
@SuppressWarnings("unchecked")
public class WxPayController extends BaseController {

	private static Logger logger = LoggerFactory.getLogger(WxPayController.class);

	@Resource(name = "wxPayService")
	private WxPayService wxPayService;
	
	@RequestMapping("handlePayResult")
	@NoCheckAsync
	public ActResult<String> handlePayResult(String xmlResponse) {
		return wxPayService.handlePayResult(xmlResponse);
	}

	@RequestMapping("handleRefundResult")
	@NoCheckAsync
	public ActResult<String> handleRefundResult(String xmlResponse) {
		return wxPayService.handleRefundResult(xmlResponse);
	}

	@RequestMapping("unifiedOrder")
	@NoCheckAsync
	public ActResult<Map<String, String>> unifiedOrder(String ip, String body,String outTradeNo,BigDecimal totalFee,String noncestr,String wxOpen,String openId,boolean isOrder,String notifyUrl) {

		Map<String, String> responseMap;
		try {
			responseMap = wxPayService.unifiedOrder(ip, body, outTradeNo, totalFee, noncestr, wxOpen, openId, isOrder,notifyUrl);
			if(responseMap == null) {
				return ActResult.fail("支付失败");
			} else {
				if("1".equals(wxOpen)) {
					return ActResult.success(wxPayService.convertForOpen(responseMap,noncestr,outTradeNo));
				} else {
					return ActResult.success(wxPayService.convertForApp(responseMap,noncestr,outTradeNo));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ActResult.fail("支付失败");
		}
	}


	@RequestMapping("unifiedOrderForWeb")
	@NoCheckAsync
	public ActResult<Map<String, String>> unifiedOrderForWeb(String ip, String productId,String body,String outTradeNo,BigDecimal totalFee,String notifyUrl) {

		try {
			if(ip.contains(",")) {
				ip = ip.substring(0, ip.indexOf(",")).trim();				
			}
			return ActResult.success(wxPayService.unifiedOrderWeb(ip, productId,body, outTradeNo, totalFee,notifyUrl));
		} catch (Exception e) {
			return ActResult.fail("支付失败");
		}
	}

	@RequestMapping("payRefund")
	@NoCheckAsync
	public ActResult<String> payRefund(String outTradeNo,BigDecimal totalFee,String appId,BigDecimal refundFee,String outRefundNo) {
		try {
			wxPayService.payRefund(outTradeNo, totalFee, appId, refundFee, outRefundNo);
			return ActResult.success("");
		} catch (Exception e) {
			e.printStackTrace();
			return ActResult.fail("支付失败");
		}
	}
	
}
