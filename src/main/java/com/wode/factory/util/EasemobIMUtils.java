package com.wode.factory.util;

import java.io.UnsupportedEncodingException;

import com.alibaba.fastjson.JSONObject;
import com.wode.common.redis.RedisUtil;
import com.wode.common.util.HttpClientUtil;
import com.wode.common.util.StringUtils;

public class EasemobIMUtils {

	public static final String ROOT_URI = "https://a1.easemob.com/wodewang2014/wodefuli/";

	static String url = "http://gw.api.taobao.com/router/rest";

	static String appkey = "YXA69_1zwDkjEeaLZJd5tSLVdg";
	static String secret = "YXA6f8o7ENtNTv3LlLD93-ffhs1wNgM";
	public static String APP_TYPE = "shop";

	public static String getAuthToken(RedisUtil redis) {

		String token = redis.getData("EASEMOB_ACCESS_TOKEN");
		if (StringUtils.isEmpty(token)) {
			String url = ROOT_URI + "token";
			String json = "{\"grant_type\": \"client_credentials\",\"client_id\": \"" + appkey
					+ "\",\"client_secret\": \"" + secret + "\"}";

			try {
				JSONObject jo = (JSONObject) JSONObject.parse(HttpClientUtil.sendJsonDataByPost(url, json, null));
				token = jo.getString("access_token");
				redis.setData("EASEMOB_ACCESS_TOKEN", token, 60 * 60 * 72); // 写入缓存
																			// 72小时有效
				return token;
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}

			return null;
		} else {
			return token;
		}
	}

	/**
	 * 更新用户昵称
	 * 
	 * @param redis
	 * @param username
	 * @param nickname
	 * @return
	 */
	public static boolean updUserNickName(RedisUtil redis, String username, String nickname) {

		String url = ROOT_URI + "users/" + username; // {org_name}/{app_name}/users/{username}
		JSONObject uinfos = new JSONObject();
		uinfos.put("nickname", nickname); // {“nickname” : “${昵称值}”}

		try {
			HttpClientUtil.sendJsonDataByPost(url, uinfos.toJSONString(), "Bearer " + getAuthToken(redis));
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean addImUser(String jsonImUsers, RedisUtil redis) {
		String url = ROOT_URI + "users";
		try {
			HttpClientUtil.sendJsonDataByPost(url, jsonImUsers, "Bearer " + getAuthToken(redis));
			return true;

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return false;
	}

	public static String addUserImGroup(RedisUtil redis, String jsonImGroups) {
		String url = ROOT_URI + "chatgroups";

		try {
			String json = HttpClientUtil.sendJsonDataByPost(url, jsonImGroups, "Bearer " + getAuthToken(redis));
			JSONObject js = JSONObject.parseObject(json);
			return js.getJSONObject("data").getString("groupid");

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public static boolean updUserImGroup(RedisUtil redis, String imGroupId, String jsonImGroups) {
		String url = ROOT_URI + "chatgroups/" + imGroupId;

		try {
			HttpClientUtil.sendJsonDataByPost(url, jsonImGroups, "Bearer " + getAuthToken(redis));
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean delUserImGroup(RedisUtil redis, String imGroupId) {
		String url = ROOT_URI + "chatgroups/" + imGroupId;
		try {
			HttpClientUtil.sendDeleteData(url, "Bearer " + getAuthToken(redis));
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static Integer addUserImGroupMember(RedisUtil redis, String imGroupId, String jsonMembers) {
		String url = ROOT_URI + "chatgroups/" + imGroupId + "/users";
		try {
			String json = HttpClientUtil.sendJsonDataByPost(url, jsonMembers, "Bearer " + getAuthToken(redis));
			JSONObject js = JSONObject.parseObject(json);

			return js.getJSONObject("data").getJSONArray("newmembers").size();
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	public static boolean deleteGroupMembers(RedisUtil redis, String imGroupId, String jsonMembers) {
		String url = ROOT_URI + "chatgroups/" + imGroupId + "/users/" + jsonMembers;
		try {
			HttpClientUtil.sendDeleteData(url, "Bearer " + getAuthToken(redis));
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static void groupMessages(RedisUtil redis, String body) {
		String url = ROOT_URI + "messages";

		try {
			HttpClientUtil.sendJsonDataByPost(url, body, "Bearer " + getAuthToken(redis));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 查看用户状态
	 * 
	 * @param redis
	 * @param username
	 * @return
	 */
	public static String checkUserStatus(RedisUtil redis, String username) {
		String url = ROOT_URI + "users/" + username + "/status"; // {org_name}/{app_name}/users/{username}
		try {
			String json = HttpClientUtil.sendHttpsRequest("GET", url, null, "Bearer " + getAuthToken(redis));
			JSONObject js = JSONObject.parseObject(json);
			String status = js.getJSONObject("data").getString(username).toString();
			return status;
		} catch (Exception e) {
			e.printStackTrace();
			return "false";
		}
	}
}
