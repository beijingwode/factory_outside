package com.wode.factory.interceptor;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wode.common.util.ActResult;
import com.wode.common.util.StringUtils;
import com.wode.factory.outside.model.OutsideCmd;
import com.wode.factory.outside.service.OutsideCmdService;
import com.wode.factory.stereotype.NoCheckAsync;
import com.wode.factory.util.IPUtils;

public class IPAuthInterceptor implements HandlerInterceptor{

	@Autowired
	private OutsideCmdService outsideCmdService;
	
	private static Set<String> allowIPs=new HashSet<String>();
	
	private static Set<String> allowNet=new HashSet<String>();
	
	public void afterCompletion(HttpServletRequest arg0,
			HttpServletResponse arg1, Object arg2, Exception arg3)
			throws Exception {
	}

	/**
	 * 控制器执行完，生成视图之前可以执行
	 */
	public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1,
			Object arg2, ModelAndView arg3) throws Exception {
		
	}

	/**
	 * Action处理之前执行  
	 */
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
			Object handler) throws Exception {
		
		String ip=IPUtils.getClientAddress(request);
		boolean flag=allowIPs.contains(ip);
		if(!flag){
			for(String net:allowNet){
				Pattern netpattern=java.util.regex.Pattern.compile(net);
				java.util.regex.Matcher match=netpattern.matcher(ip);
				if(match.matches()){
					flag=true;
					break;
				}
			}
		}
		
		if(!flag){
			System.out.println("非法的来源:"+ip);
//			   System.out.println("x-forwarded-for:"+arg0.getHeader("x-forwarded-for"));
//			   System.out.println("Proxy-Client-IP:"+arg0.getHeader("Proxy-Client-IP"));
//			   System.out.println("WL-Proxy-Client-IP:"+arg0.getHeader("WL-Proxy-Client-IP"));
//			   System.out.println("RemoteAddr:"+arg0.getRemoteAddr());
			ActResult ret=new ActResult();
			ret.setSuccess(false);
			ret.setMsg("非法的来源:"+ip);
			String json=JSON.toJSONString(ret);
			writeCustomJSON(json,response);
			return false;
		}
		
		
		return doAsync(request,response,handler,ip);
		
	}

	/**
	 * 检查异步操作
	 * @param request
	 * @param response
	 * @param handler
	 * @return
	 */
	private boolean doAsync(HttpServletRequest request, HttpServletResponse response,
			Object handler,String ip)
	{
		//注解 排除方法  NoCheckdoAsync
		if (!(handler instanceof HandlerMethod))
			return true;
		HandlerMethod handlerMethod = (HandlerMethod) handler;
		Method method = handlerMethod.getMethod();
	
		NoCheckAsync nocheck = method.getAnnotation(NoCheckAsync.class);
		if (nocheck != null) {
			return true;
		}
		
		boolean isAsync = StringUtils.toBoolean(request.getParameter("isAsync"));
		if(!isAsync) return true;
		
        Enumeration<String> enu = request.getParameterNames();
		JSONObject params = new JSONObject();
        while(enu.hasMoreElements()){
            String paraName = enu.nextElement();
            if (!"_dc".equals(paraName) && !"node".equals(paraName) && !"isAsync".equals(paraName) && !"notifyUrl".equals(paraName)){//_dc的参数,isAsync不要
            	params.put(paraName, request.getParameter(paraName));
            }
        }
        String path = request.getServletPath();
        if(path.startsWith("/")) {
        	path=path.substring(1);
        }
        int p = path.indexOf("/");
        String service = path;
        String methodName = "";
        if(p!=-1) {
        	service=service.substring(0,p);
        	methodName = path.substring(p+1);
        }
        
        if("smsProxy".equals(service)) {
        	params.put("ip", ip);
        }
        Long uuid = this.saveCommand(service, methodName, request.getParameter("notifyUrl"), params);
		writeCustomJSON(JSON.toJSONString(ActResult.success(uuid)),response);
        return false;
		
	}
	
	/**
	 * 保存异步处理
	 * @param serviceName
	 * @param methodName
	 * @param notifyUrl
	 * @param paramas
	 * @return
	 */
	private Long saveCommand(String serviceName, String methodName, String notifyUrl,JSONObject paramas) {
		OutsideCmd cmd = new OutsideCmd();
		cmd.setExecStatus("0");
		cmd.setExecResult("0");
		cmd.setServiceName(serviceName);
		cmd.setMethodName(methodName);
		cmd.setParamJson(paramas.toJSONString());
		cmd.setNotifyUrl(notifyUrl);
		outsideCmdService.saveOrUpdate(cmd);
		return cmd.getId();
    }
	
	/**
	 * 输出json
	 * @param data
	 * json字符
	 * @param response
	 */
	protected void  writeCustomJSON(String data,HttpServletResponse response){
		response.setContentType("application/json;charset=UTF-8");
        PrintWriter pw;
		try {
			pw = response.getWriter();
			pw.print(data);
		    pw.flush();
		    pw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}   
	}

	
	public static void setAllowIPs(Set<String> allowIPs) {
		IPAuthInterceptor.allowIPs = allowIPs;
	}

	public static void setAllowNet(Set<String> allowNet) {
		IPAuthInterceptor.allowNet = allowNet;
	}
}
