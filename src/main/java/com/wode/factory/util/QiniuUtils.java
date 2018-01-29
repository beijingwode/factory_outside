package com.wode.factory.util;

import java.io.File;
import java.io.IOException;

import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.wode.common.util.ActResult;

public class QiniuUtils {
	
	
	public static final String  ACCESS_KEY = "r3ZLb2LP6MrF6y_OnKQg42tAFM99ESO8ZsyD4WFY";
	public static final String  SECRET_KEY = "4swJ-jdVqNiofCmoluQ-mrWV9c-YMamdmofc9Dm_";
	//要上传的空间
	public static final String bucketname = "wode";
	
	static final Auth auth = Auth.create(ACCESS_KEY, SECRET_KEY);
	
	//创建上传对象
	static final UploadManager uploadManager = new UploadManager();
	  
	public static String getUpToken(){
	      return auth.uploadToken(bucketname);
	}

	public static ActResult upload(byte[] fileData,String key) {
		ActResult result = new ActResult();
        try {
        	//调用put方法上传
	        uploadManager.put(fileData, key, getUpToken());
            
        } catch (IllegalStateException e) {
        	result.setSuccess(false);
            result.setMsg(e.getMessage());
        } catch (QiniuException e1) {
        	Response r = e1.response;
            // 请求失败时打印的异常的信息
            System.out.println(r.toString());
            try {
                //响应的文本信息
              System.out.println(r.bodyString());
            } catch (QiniuException e2) {
                //ignore
            }
        } catch (IOException e1) {
        	result.setSuccess(false);
            result.setMsg(e1.getMessage());
        }        
		return result;
	}

	public static ActResult upload(File file,String key) {
		ActResult result = new ActResult();
        try {
        	//调用put方法上传
	        uploadManager.put(file, key, getUpToken());
            
        } catch (IllegalStateException e) {
        	result.setSuccess(false);
            result.setMsg(e.getMessage());
        } catch (QiniuException e1) {
        	Response r = e1.response;
            // 请求失败时打印的异常的信息
            System.out.println(r.toString());
            try {
                //响应的文本信息
              System.out.println(r.bodyString());
            } catch (QiniuException e2) {
                //ignore
            }
        } catch (IOException e1) {
        	result.setSuccess(false);
            result.setMsg(e1.getMessage());
        }        
		return result;
	}
}
