package com.wode.factory.outside.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wode.common.redis.RedisUtil;
import com.wode.common.util.ActResult;
import com.wode.factory.stereotype.NoCheckAsync;
import com.wode.factory.util.EasemobIMUtils;

@Controller
@RequestMapping("easemobIM")
@ResponseBody
public class EasemobIMController extends BaseController {

	//private static Logger logger= LoggerFactory.getLogger(UserProxyController.class);

	@Qualifier("redis")
    @Autowired
    private RedisUtil redisUtil;
	
	@RequestMapping("updUserNickName")
	public ActResult<String> updUserNickName(String username ,String nickname,String appType, String comeFrom) {		
		if(EasemobIMUtils.updUserNickName(redisUtil, username, nickname)) {
			return ActResult.success("");
		} else {
			return ActResult.fail("修改失败");
		}
	}

	@RequestMapping("addImUser")
	public ActResult<String> addImUser(String jsonImUsers,String appType, String comeFrom) {
		
		if(EasemobIMUtils.addImUser(jsonImUsers, redisUtil)) {
			return ActResult.success("");
		} else {
			return ActResult.fail("修改失败");
		}
	}

	@RequestMapping("addUserImGroup")
	public ActResult<String> addUserImGroup(String jsonImGroups,String appType, String comeFrom) {
		return ActResult.success(EasemobIMUtils.addUserImGroup(redisUtil,jsonImGroups));
	}

	@RequestMapping("updUserImGroup")
	@NoCheckAsync
	public ActResult<String> updUserImGroup(String imGroupId, String jsonImGroups,String appType, String comeFrom) {

		if(EasemobIMUtils.updUserImGroup(redisUtil,imGroupId,jsonImGroups)) {
			return ActResult.success("");
		} else {
			return ActResult.fail("修改失败");
		}
	}

	@RequestMapping("delUserImGroup")
	@NoCheckAsync
	public ActResult<String> delUserImGroup(String imGroupId,String appType, String comeFrom) {
		if(EasemobIMUtils.delUserImGroup(redisUtil,imGroupId)) {
			return ActResult.success("");
		} else {
			return ActResult.fail("修改失败");
		}
	}

	@RequestMapping("addUserImGroupMember")
	public ActResult<Integer> addUserImGroupMember(String imGroupId, String jsonMembers,String appType, String comeFrom) {
		return ActResult.success(EasemobIMUtils.addUserImGroupMember(redisUtil,imGroupId,jsonMembers));
	}

	@RequestMapping("deleteGroupMembers")
	public ActResult<String> deleteGroupMembers(String imGroupId, String jsonMembers,String appType, String comeFrom) {
		if(EasemobIMUtils.deleteGroupMembers(redisUtil, imGroupId, jsonMembers)) {
			return ActResult.success("");
		} else {
			return ActResult.fail("修改失败");
		}
	}

	@RequestMapping("groupMessages")
	public ActResult<String> groupMessages(String body,String appType, String comeFrom) {
		EasemobIMUtils.groupMessages(redisUtil, body);
		return ActResult.success("");
	}

	@RequestMapping("checkUserStatus")
	@NoCheckAsync
	public ActResult<String> checkUserStatus(String username) {
		return ActResult.success(EasemobIMUtils.checkUserStatus(redisUtil, username));
	}
}
