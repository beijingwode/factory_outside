package com.wode.factory.outside.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.alibaba.fastjson.JSONObject;
import com.wode.common.util.ActResult;
import com.wode.common.util.StringUtils;
import com.wode.factory.stereotype.NoCheckAsync;
import com.wode.factory.util.QiniuUtils;

@Controller
@RequestMapping("upload")
@ResponseBody
public class UploadController extends BaseController {

	@Value("#{configProperties['upload.dir']}")
	public String BASE_UPLOAD_PATH;

	private final static String UPLOAD_TEMP="upload/temp";
	private final static String UPLOAD_REGULAR="upload/regular";
	private final static String BASEURL="img2.wd-w.com";
	
	private static Logger logger= LoggerFactory.getLogger(UploadController.class);
	
	
	@RequestMapping({ "/pic", "/file" })
	@NoCheckAsync
	public ActResult<List<String>> upload(HttpServletRequest request, Boolean isTemp, Boolean override, String folder,
			boolean isAsync, String notifyUrl) throws Exception {

		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;

		ActResult<List<String>> result = new ActResult<List<String>>();
		Map<String, MultipartFile> map = multipartRequest.getFileMap();
		List<String> flist = new ArrayList<String>();
		result.setData(flist);
		for (String fname : map.keySet()) {
			// 获得文件：
			MultipartFile file = multipartRequest.getFile(fname);

			ActResult<String> as = upload1(file, folder, isTemp, override, isAsync, notifyUrl);
			if (as.isSuccess()) {
				flist.add(as.getData());
			} else {
				result.setMsg(as.getMsg());
				result.setSuccess(false);
			}
		}

		if (flist.size() < 1) {
			result.setMsg("没有上传文件");
			result.setSuccess(false);
		}

		return result;
	}

	public ActResult<String> upload1(MultipartFile file,String folder, Boolean isTemp,Boolean override,boolean isAsync, String notifyUrl) {
		ActResult<String> result = new ActResult<String>();
		String path = "";
		if(isTemp!=null&&isTemp){
			path+=UPLOAD_TEMP;
		}else{
			path+=UPLOAD_REGULAR;
			 if(!StringUtils.isEmpty(folder)){
		        path+="/"+folder;
		     }
       
	        if(file == null||file.isEmpty()) {
	        	result.setMsg("没有上传文件");
	        	result.setSuccess(false);
	        	return result;
	        }
		}
	        
	        
        String fileName = file.getOriginalFilename();
        String fileType = "";
        if(override==null){
        	int index = -1;
            index = fileName.lastIndexOf(".");
            if(index == -1) {
            	result.setSuccess(false);
                result.setMsg("文件名有误");
            }
            fileType = fileName.substring(index);
            fileName = new Date().getTime() + fileType;        	
        }
        
    	//调用put方法上传
		String key=path + "/" + fileName;
		if(isAsync) {
			JSONObject paramas = new JSONObject();
			paramas.put("key", key);
			paramas.put("fileType", fileType);
			
			Long cid = this.saveCommand("upload", "upload", notifyUrl, paramas);
			File f=new File(BASE_UPLOAD_PATH +File.separator+ cid + fileType);
			try {
				FileCopyUtils.copy(file.getBytes(), f);
			} catch (IOException e) {
            	result.setSuccess(false);
                result.setMsg("系统异常");
                return result;
			}
		} else {
			try {
				QiniuUtils.upload(file.getBytes(), key);
			} catch (IOException e) {
				e.printStackTrace();
            	result.setSuccess(false);
                result.setMsg("文件有误");
                return result;
			}
		}
        //打印返回的信息
        //System.out.println(res.bodyString()); 
        result.setData(BASEURL+"/"+key);
		return result;
	}
	
//	@RequestMapping("/pic.json")
//	public ModelAndView upload(HttpServletRequest request,String folder,String rtnUrl) {
//		// 转型为MultipartHttpRequest：
//		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
//		String referer = request.getHeader("Referer");
//		logger.debug(referer);
//
//		ActResult result = new ActResult();
//		Map<String, MultipartFile> map = multipartRequest.getFileMap();
//		List flist = new ArrayList();
//		result.setData(flist);
//
//		int max = 1024*20;
//		for (String fname : map.keySet()) {
//			// 获得文件：
//			MultipartFile file = multipartRequest.getFile(fname);
//			//文件大小校验,flash客户端控制大小
//			if (referer != null && !referer.endsWith(".swf")) {
//				CommonsMultipartFile cf = (CommonsMultipartFile) file;
//				if (cf.getFileItem().getSize() / 1024 > max) {
//					result.setMsg("文件超过规定大小");
//					result.setSuccess(false);
//	                return makeUploadResult(rtnUrl,null,result);
//				}
//			}
//
//			ActResult as1;
//			try {
//
//				String picTYpe = getPicType(file.getInputStream());
//				if (picTYpe == null) {
//					CommonsMultipartFile cf = (CommonsMultipartFile) file;
//					cf.getFileItem().delete();
//					result.setMsg("文件格式不支持");
//					result.setSuccess(false);
//	                return makeUploadResult(rtnUrl,null,result);
//				}
//
//	            ActResult as=QiniuUtils.upload(file,folder,false,null) ;
//
//	            if(as.isSuccess()){
//	            	flist.add(as.getData());
//	            }else{
//	                return makeUploadResult(rtnUrl,null,as);
//	            }
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//
//		}
//
//        if(flist.size()<1){
//       	 	result.setMsg("没有上传文件");
//        	result.setSuccess(false);
//        }
//
//		//return result;
//        return makeUploadResult(rtnUrl,null,result);
//	}
//
//	@RequestMapping("/uedit.json")
//	public ModelAndView uedit(HttpServletRequest request,String folder,String rtnUrl,String action,String encode) {
//		if (action.startsWith("upload")) {
//
//			// 转型为MultipartHttpRequest：
//			MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
//			String referer = request.getHeader("Referer");
//			logger.debug(referer);
//
//			ActResult result = new ActResult();
//			Map<String, MultipartFile> map = multipartRequest.getFileMap();
//			List flist = new ArrayList();
//			result.setData(flist);
//
//			int max = 1024*20;
//			for (String fname : map.keySet()) {
//				// 获得文件：
//				MultipartFile file = multipartRequest.getFile(fname);
//				//文件大小校验,flash客户端控制大小
//				if (referer != null && !referer.endsWith(".swf")) {
//					CommonsMultipartFile cf = (CommonsMultipartFile) file;
//					if (cf.getFileItem().getSize() / 1024 > max) {
//						result.setMsg("文件超过规定大小");
//						result.setSuccess(false);
//		                return makeUploadResult(rtnUrl,"action="+action,result);
//					}
//				}
//
//				ActResult as1;
//				try {
//
//					String picTYpe = getPicType(file.getInputStream());
//					if (picTYpe == null) {
//						CommonsMultipartFile cf = (CommonsMultipartFile) file;
//						cf.getFileItem().delete();
//						result.setMsg("文件格式不支持");
//						result.setSuccess(false);
//		                return makeUploadResult(rtnUrl,"action="+action,result);
//					}
//
//		            ActResult as=write(file,folder,false,null) ;
//
//		            if(as.isSuccess()){
//		            	flist.add(as.getData());
//		            }else{
//		                return makeUploadResult(rtnUrl,"action="+action,as);
//		            }
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//
//			}
//
//	        if(flist.size()<1){
//	       	 	result.setMsg("没有上传文件");
//	        	result.setSuccess(false);
//	        }
//
//			//return result;
//	        return makeUploadResult(rtnUrl,"action="+action,result);
//	        
//		} else {
//			return makeUploadResult(rtnUrl,"action="+action,null);
//		}
//	}

//	private ModelAndView makeUploadResult(String rtnUrl,String urlpara,ActResult data) {
//		
//		if(!StringUtils.isEmpty(urlpara)) {
//			if(rtnUrl.contains("?")) {
//				rtnUrl = rtnUrl+"&"+urlpara;
//			} else {
//				rtnUrl = rtnUrl+"?"+urlpara;
//			}
//		}
//		if(!StringUtils.isEmpty(data)) {
//			if(rtnUrl.contains("?")) {
//				rtnUrl = rtnUrl+"&data="+JsonUtil.toJson(data);
//			} else {
//				rtnUrl = rtnUrl+"?data="+JsonUtil.toJson(data);
//			}
//		}
//		ModelAndView mv =new ModelAndView();
//		mv.setViewName("redirect:"+rtnUrl);
//		return mv;
//	}

}
