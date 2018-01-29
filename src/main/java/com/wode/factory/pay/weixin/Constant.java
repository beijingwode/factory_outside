package com.wode.factory.pay.weixin;

/**
 * Created by zoln on 2016/4/12.
 */
public class Constant {

	public static final String APP_ID = "wx1b153767a3760be4";
	public static final String OPEN_APP_ID = "wxb62e121cbeffdddf";

	public static final String MCH_ID = "1340208101";
	public static final String OPEN_MCH_ID = "1265159501";
	
	public static final String API_CRTT_P12_PATH = "/data/wx_certs/apiclient_cert_app.p12";
	public static final String OPEN_API_CRTT_P12_PATH = "/data/wx_certs/apiclient_cert_open.p12";
	//public static final String OPEN_API_CRTT_P12_PATH = "D:/wx/apiclient_cert_open.p12";
	
	//public static final String NOTIFY_URL = "http://www.wd-w.com/paymentCallBack/wePayResult";

	public static final String TRADE_TYPE = "APP";
	public static final String OPEN_TRADE_TYPE = "JSAPI";
	public static final String WEB_TRADE_TYPE = "NATIVE";
	
	public static final String DEVICE_INFO = "WEB";

	public static final String KEY = "f58c1904c5014a809fa6364feb1f25d3";

	public static final String UNIFIED_ORDER_URL = "https://api.mch.weixin.qq.com/pay/unifiedorder";
	public static final String PAY_REFUND_URL = "https://api.mch.weixin.qq.com/secapi/pay/refund";

	public static class ReturnCode {
		public static final String SUCCESS = "SUCCESS";

		public static final String FAIL = "FAIL";
	}

	private static class CommonParam {

		public static final String APP_ID = "appid";

		public static final String MCH_ID = "mch_id";

		public static final String DEVICE_INFO = "device_info";

		public static final String SIGN = "sign";

		public static final String TRADE_TYPE = "trade_type";

		public static final String NONCE_STR = "nonce_str";
	}

	private static class ResponseParam extends CommonParam{

		public static final String RETURN_CODE = "return_code";

		public static final String RETURN_MSG = "return_msg";

		public static final String RESULT_CODE = "result_code";

		public static final String ERR_CODE = "err_code";

		public static final String ERR_CODE_DES = "err_code_des";
	}

	public static class RequestParam extends CommonParam{
		public static final String BODY = "body";

		public static final String OUT_TRADE_NO = "out_trade_no";
		
		public static final String TOTAL_FEE = "total_fee";
		
		public static final String IP = "spbill_create_ip";

		public static final String NOTIFY_URL = "notify_url";
		public static final String openid = "openid";
		public static final String PRODUCT_ID = "product_id";
	}
	
	public static class RefundRequestParam extends CommonParam{
		public static final String OUT_TRADE_NO = "out_trade_no";
		public static final String OUT_REFUND_NO = "out_refund_no";
		
		public static final String TOTAL_FEE = "total_fee";
		public static final String REFUND_FEE = "refund_fee";
		public static final String OP_USER_ID = "op_user_id";

	}
	
	public static class PrepayResponseParam extends ResponseParam {
		public static final String PREPAY_ID = "prepay_id";

		public static final String CODE_URL = "code_url";
		public static final String APP_ID = "appid";
		public static final String PARTNER_ID = "partnerid";
		public static final String noncestr = "nonce_str";
		public static final String timestamp = "timestamp";
		public static final String packageValue = "package";
		public static final String sign = "sign";
		public static final String openid = "openid";
	}

	public static class PayResultResponseParam extends ResponseParam {
		public static final String OPENID = "openid";

		public static final String BANK_TYPE = "bank_type";

		public static final String TOTAL_FEE = "total_fee";

		public static final String FEE_TYPE = "fee_type";

		public static final String CASH_FEE = "cash_fee";

		public static final String TRANSACTION_ID = "transaction_id";

		public static final String OUT_TRADE_NO = "out_trade_no";

		public static final String OUT_REFUND_NO = "out_refund_no";

		public static final String TIME_END = "time_end";
		
		public static final String REQ_INFO = "req_info";	
		public static final String REFUND_STATUS = "refund_status";
		
	}
}
