package com.wode.factory.outside.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wode.comm.user.client.service.ServiceFactory;
import com.wode.comm.user.client.service.UserService;
import com.wode.common.util.ActResult;
import com.wode.factory.stereotype.NoCheckAsync;
import com.wode.factory.util.Constant;
import com.wode.model.User;

@Controller
@RequestMapping("userProxy")
@ResponseBody
public class UserProxyController extends BaseController {

	static UserService us = ServiceFactory.getService(Constant.PASSPORT_API_URL);
	
	@RequestMapping("/active")
	public ActResult<String> active(Long userId,String comeFrom,String autoLogin) {
		return us.active(userId, comeFrom,autoLogin);
    }
	
	@RequestMapping("/resetPassword")
	public ActResult<String> resetPassword(Long userId,String comeFrom) {
		ActResult act= us.resetPassword(userId, comeFrom); 
		if(act.isSuccess()) {
			return ActResult.successSetMsg(act.getMsg());
		} else {
			return ActResult.fail(act.getMsg());
		}
    } 

	@RequestMapping("/hasLogin")
	@NoCheckAsync
	public ActResult<User> hasLogin(String ticket) {
		return us.hasLogin(ticket);
	}

	@RequestMapping("/logout")
	@NoCheckAsync
	public ActResult<String> logout(String ticket){
		us.logout(ticket);
		return ActResult.success(null);
	}

	@RequestMapping("/getUserById")
	@NoCheckAsync
	public ActResult<User> getUserById(Long userId){
		return us.getUserById(userId);
	}
	@RequestMapping("/findByPhone")
	@NoCheckAsync
	public ActResult<User> findByPhone(String phone) {
		return us.findByPhone(phone);
    }
	
	@RequestMapping("/findByEmail")
	@NoCheckAsync
	public ActResult<User> findByEmail(String email) {
		return us.findByEmail(email);
    }

	@SuppressWarnings("unchecked")
	@RequestMapping("/insert")
	@NoCheckAsync
	public ActResult<User> insert(User user) {
		return us.insert(user);
    }

	@RequestMapping("/updatePhone")
	@NoCheckAsync
	public ActResult<String> updatePhone(Long userId,String comeFrom,String phone) {
		ActResult act=us.updatePhone(phone, userId, comeFrom);
		if(act.isSuccess()) {
			return ActResult.successSetMsg(act.getMsg());
		} else {
			return ActResult.fail(act.getMsg());
		}
    }
	
	@RequestMapping("/updateEmail")
	@NoCheckAsync
	public ActResult<String> updateEmail(Long userId,String comeFrom,String email) {
		ActResult<User> act=us.updateEmail(email, userId, comeFrom);
		if(act.isSuccess()) {
			return ActResult.successSetMsg(act.getMsg());
		} else {
			return ActResult.fail(act.getMsg());
		}
    }

	@RequestMapping("/deleteUser")
	@NoCheckAsync
	public ActResult<String> deleteUser(Long userId, String comeFrom, Long operatorId) {
		ActResult<User> act=us.deleteUser(userId, comeFrom, operatorId);
		if(act.isSuccess()) {
			return ActResult.successSetMsg(act.getMsg());
		} else {
			return ActResult.fail(act.getMsg());
		}
	}
	
	@RequestMapping("/scanQrForWebLogin")
	@NoCheckAsync
	public ActResult<String> scanQrForWebLogin(String uuid) {
		us.scanQrForWebLogin(uuid);
		return ActResult.success(null);
    }

	@RequestMapping("/qrLogin")
	@NoCheckAsync
	public ActResult<Long> qrLogin(String uuid, String ticket,String loginType) {
		return us.qrLogin(uuid, ticket, loginType);
	}

	@RequestMapping("/getTmpCode")
	@NoCheckAsync
	public ActResult<String> getTmpCode(String userName) {
		return us.getTmpCode(userName);
	}
	
	@RequestMapping("/getDayRegisterNumber")
	@NoCheckAsync
	public ActResult getDayRegisterNumber(String date) {
		return us.getDayRegisterNumber(date);
	}

	@NoCheckAsync
	@RequestMapping("/getDayActiveNumber")
	public ActResult getDayActiveNumber(String date) {
		return us.getDayActiveNumber(date);
	}

	@NoCheckAsync
	@RequestMapping("/getCityRegisterCnt")
	public ActResult<List<String>> getCityRegisterCnt(String date) {
		return us.getCityRegisterCnt(date);
	}
}
