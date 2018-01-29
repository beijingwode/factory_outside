package com.wode.factory.outside.task;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wode.comm.sms.client.SmsClient;
import com.wode.comm.user.client.service.ServiceFactory;
import com.wode.comm.user.client.service.UserService;
import com.wode.common.redis.RedisUtil;
import com.wode.common.util.ActResult;
import com.wode.common.util.HttpClientUtil;
import com.wode.common.util.StringUtils;
import com.wode.factory.outside.model.OutsideCmd;
import com.wode.factory.outside.service.OutsideCmdService;
import com.wode.factory.util.Constant;
import com.wode.factory.util.EasemobIMUtils;
import com.wode.factory.util.JPushUtils;
import com.wode.factory.util.JavaMailUtil;
import com.wode.factory.util.QiniuUtils;
import com.wode.factory.util.weixin.CoreService;
import com.wode.factory.util.weixin.WXConstants;

/**
 * 功能说明: 订单相关定时任务类
 * 包括：
 * 订单未支付定时取消
 * 收货未确认定时确认
 * 退货未确认定时确认
 * 商户佣金收取跑数据
 * 日期:	2015年3月20日
 * 2015年11月2日
 * 开发者:	谷子夜
 * <p/>
 * 历史记录
 * 修改内容：
 * 修改人员：
 * 修改日期： 2015年3月20日
 */
@Component
public class CommandTask {

	@Value("#{configProperties['upload.dir']}")
	public String BASE_UPLOAD_PATH;
	
	private final Logger logger = LoggerFactory.getLogger(CommandTask.class);
	@Autowired
	private OutsideCmdService outsideCmdService;
	@Autowired
	@Qualifier("mailUtil")
	private JavaMailUtil mailUtil;
	@Qualifier("redis")
    @Autowired
    private RedisUtil redisUtil;
	@Autowired
    private JPushUtils jPushUtils;
	
	static SmsClient client=new SmsClient();
    	
	/**
	 * 活动订单支付超时定时取消2分钟执行一次
	 */
	@Scheduled(cron = "0 0/1 * * * ?")
	public void doCommand() {
		OutsideCmd q = new OutsideCmd();
		q.setExecStatus("0");
		List<OutsideCmd> cmds = outsideCmdService.select10Exec(q);
		for (OutsideCmd cmd : cmds) {
			/////////////////////////////////////////////////////////////////////////////
			// 标记执行
			cmd.setExecStatus("1");
			cmd.setExecDate(new Date());
			outsideCmdService.update(cmd);
			/////////////////////////////////////////////////////////////////////////////
		}
		
		for (OutsideCmd cmd : cmds) {
			exec(cmd);
			
			// 更新执行结果
			cmd.setExecStatus("2");
			outsideCmdService.update(cmd);
		}
	}
	
	private void exec(OutsideCmd cmd) {
		try{
			JSONObject jo = JSON.parseObject(cmd.getParamJson());
			if("upload".equals(cmd.getServiceName())) {
				// 文件上传
				Long cid = cmd.getId();
				File f=new File(BASE_UPLOAD_PATH +File.separator+ cid + jo.getString("fileType"));
				
				if(!f.exists()) {
					cmd.setExecResult("1");
					cmd.setErrMsg("参数错误");
				} else {
					ActResult act = QiniuUtils.upload(f, jo.getString("key"));
					if(act.isSuccess()) {
						cmd.setExecResult("2");
						
						f.delete();
					} else {
						cmd.setExecResult("1");
						cmd.setErrMsg(act.getMsg());
					}
				}
				
			} else if("mailProxy".equals(cmd.getServiceName())) {
				String text = jo.getString("text");
				text=new String(org.apache.commons.codec.binary.Base64.decodeBase64(text));
				if(mailUtil.sendSenderSimpleMessageMail(jo.getString("toEmail"), jo.getString("subject"),text)) {
					cmd.setExecResult("2");
				} else {
					cmd.setExecResult("1");
					cmd.setErrMsg("发送邮件失败");
				}
				
			} else if("smsProxy".equals(cmd.getServiceName())) {
				ActResult<String> act= null;
				if("sendSmsTempCode".equals(cmd.getMethodName())){
					act= client.send(jo.getString("mobile") , jo.getString("signature"), jo.getString("content"),
							jo.getString("ip"), jo.getString("source"),
							new String(org.apache.commons.codec.binary.Base64.decodeBase64(jo.getString("jSonParams"))), jo.getString("outId"));

				} else {
					act= client.send(jo.getString("mobile") , jo.getString("signature"), jo.getString("content"),jo.getString("ip"), jo.getString("source"));
				}
				if(act.isSuccess()) {
					cmd.setExecResult("2");
				} else {
					cmd.setExecResult("1");
					cmd.setErrMsg(act.getMsg());
				}
				
			} else if("wxOpen".equals(cmd.getServiceName())) {
				String token = CoreService.getAccessToken(redisUtil);
				if("setTag".equals(cmd.getMethodName())) {
					try {
						HttpClientUtil.sendJsonDataByPost(WXConstants.MEMBERS_BATCHTAGGING.replace("ACCESS_TOKEN", token), "{\"openid_list\":[\""+jo.getString("openIds")+"\"],\"tagid\":"+jo.getString("tagId")+"}", null);

						cmd.setExecResult("2");
					} catch (UnsupportedEncodingException e) {
						cmd.setExecResult("1");
						cmd.setErrMsg("内容中有非法字符");
					}

				} else if("templateMsgSend".equals(cmd.getMethodName())) {
					//发送消息
					String rtn;
					try {
						String para = jo.getString("body");
						String msg = new String(org.apache.commons.codec.binary.Base64.decodeBase64(para.getBytes()));
						rtn = HttpClientUtil.sendJsonDataByPost(WXConstants.SEND_TEMPLATE_URL.replace("ACCESS_TOKEN", token), msg, null);
						if(rtn.contains("access_token is invalid or not latest hint")) {
							// 如果失败 就删除缓存token 再此获取1此
							redisUtil.del("WX_ACCESS_TOKEN");
							token = CoreService.getAccessToken(redisUtil);
							rtn = HttpClientUtil.sendJsonDataByPost(WXConstants.SEND_TEMPLATE_URL.replace("ACCESS_TOKEN", token), msg, null);
						}
						cmd.setExecResult("2");
					} catch (UnsupportedEncodingException e) {
						cmd.setExecResult("1");
						cmd.setErrMsg("内容中有非法字符");
					}
				}
			} else if("userProxy".equals(cmd.getServiceName())) {
		    	UserService us = ServiceFactory.getService(Constant.PASSPORT_API_URL);
				if("active".equals(cmd.getMethodName())) {

			    	ActResult<String> act= us.active(jo.getLong("userId"), jo.getString("comeFrom"),jo.getString("autoLogin"));
			    	
			    	if(act.isSuccess()) {
						cmd.setExecResult("2");
			    	} else {
						cmd.setExecResult("1");
						cmd.setErrMsg(act.getMsg());
			    	}

				} else if("resetPassword".equals(cmd.getMethodName())) {

					ActResult act= us.resetPassword(jo.getLong("userId"), jo.getString("comeFrom"));
			    	
			    	if(act.isSuccess()) {
						cmd.setExecResult("2");
			    	} else {
						cmd.setExecResult("1");
						cmd.setErrMsg(act.getMsg());
			    	}
				}
				
			} else if("easemobIM".equals(cmd.getServiceName())) {
				if("updUserNickName".equals(cmd.getMethodName())) {

			    	if(EasemobIMUtils.updUserNickName(redisUtil, jo.getString("username"), jo.getString("nickname"))) {
						cmd.setExecResult("2");
					} else {
						cmd.setExecResult("1");
						cmd.setErrMsg("修改失败");
					}

				} else if("addImUser".equals(cmd.getMethodName())) {


			    	if(EasemobIMUtils.addImUser(jo.getString("jsonImUsers"),redisUtil)) {
						cmd.setExecResult("2");
					} else {
						cmd.setExecResult("1");
						cmd.setErrMsg("用户添加失败");
					}

				} else if("addUserImGroup".equals(cmd.getMethodName())) {
					EasemobIMUtils.addUserImGroup(redisUtil,jo.getString("jsonImGroups"));
					cmd.setExecResult("2");

				} else if("addUserImGroupMember".equals(cmd.getMethodName())) {
					EasemobIMUtils.addUserImGroupMember(redisUtil,jo.getString("imGroupId"),jo.getString("jsonMembers"));
					cmd.setExecResult("2");
			    	
				} else if("deleteGroupMembers".equals(cmd.getMethodName())) {

			    	if(EasemobIMUtils.deleteGroupMembers(redisUtil,jo.getString("imGroupId"),jo.getString("jsonMembers"))) {
						cmd.setExecResult("2");
					} else {
						cmd.setExecResult("1");
						cmd.setErrMsg("修改失败");
					}
				} else if("groupMessages".equals(cmd.getMethodName())) {
					EasemobIMUtils.groupMessages(redisUtil,jo.getString("body"));
					cmd.setExecResult("2");
				}
				
			} else if("jPush".equals(cmd.getServiceName())) {
				if("sendNotification".equals(cmd.getMethodName())) {
					String extras = jo.getString("extras");
					Map<String,String> map = null;
					if(!StringUtils.isEmpty(extras)){
						map = JSON.parseObject(extras, Map.class);
					}

					if(jPushUtils.sendNotification(jo.getString("appType"), jo.getString("alert"), jo.getString("title"), map, jo.getString("pushDriver"), jo.getString("pushType"), jo.getString("pushName"))) {
						cmd.setExecResult("2");
					} else {
						cmd.setExecResult("1");
						cmd.setErrMsg("发送失败");
					}

				} else if("sendMessage".equals(cmd.getMethodName())) {

					String extras = jo.getString("extras");
					Map<String,String> map = null;
					if(!StringUtils.isEmpty(extras)){
						map = JSON.parseObject(extras, Map.class);
					}

					if(jPushUtils.sendMessage(jo.getString("appType"), jo.getString("msgContent"), jo.getString("title"),map, jo.getString("pushDriver"), jo.getString("pushType"), jo.getString("statusId"), jo.getString("pushName"))) {
						cmd.setExecResult("2");
					} else {
						cmd.setExecResult("1");
						cmd.setErrMsg("发送失败");
					}
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			cmd.setExecResult("1");
			cmd.setErrMsg("系统异常");
		}
		

		if(!StringUtils.isEmpty(cmd.getNotifyUrl())) {
			// TODO 异步执行通知
		}
	}
}