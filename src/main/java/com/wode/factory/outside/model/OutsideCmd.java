/*
 * Powered By [rapid-framework]
 * Web Site: http://www.rapid-framework.org.cn
 * Google Code: http://code.google.com/p/rapid-framework/
 * Since 2008 - 2015
 */

package com.wode.factory.outside.model;


import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.nutz.dao.entity.annotation.Column;

public class OutsideCmd implements java.io.Serializable{

    /**
	 * 
	 */
	private static final long serialVersionUID = 453008719908240470L;
	//columns START
    /**
     * id       db_column: id
     * 
     * 
     * 
     */ 
    @Column("id")
    private java.lang.Long id;
    /**
     * 执行时间       db_column: exec_date
     * 
     * 
     * 
     */ 
    @Column("exec_date")
    private java.util.Date execDate;
    /**
     * 执行状态 执行状态 0:未执行/1:执行中/2:已执行       db_column: exec_status
     * 
     * 
     * 
     */ 
    @Column("exec_status")
    private java.lang.String execStatus;
    /**
     * 执行状态 0:未知/1:失败/2:成功/3:暂停       db_column: exec_result
     * 
     * 
     * 
     */ 
    @Column("exec_result")
    private java.lang.String execResult;
    /**
     * 错误信息       db_column: err_msg
     * 
     * 
     * 
     */ 
    @Column("err_msg")
    private java.lang.String errMsg;
    /**
     * 服务名称       db_column: service_name
     * 
     * 
     * 
     */ 
    @Column("service_name")
    private java.lang.String serviceName;
    /**
     * 方法名称       db_column: method_name
     * 
     * 
     * 
     */ 
    @Column("method_name")
    private java.lang.String methodName;
    /**
     * 回调地址       db_column: notify_url
     * 
     * 
     * 
     */ 
    @Column("notify_url")
    private java.lang.String notifyUrl;
    /**
     * 参数       db_column: param_json
     * 
     * 
     * 
     */ 
    @Column("param_json")
    private java.lang.String paramJson;
    /**
     * java_class       db_column: bean_class
     * 
     * 
     * 
     */ 
    @Column("bean_class")
    private java.lang.String beanClass;
    /**
     * 来源       db_column: come_form
     * 
     * 
     * 
     */ 
    @Column("come_form")
    private java.lang.String comeForm;
    /**
     * 创建时间       db_column: create_time
     * 
     * 
     * 
     */ 
    @Column("create_time")
    private java.util.Date createTime;
    /**
     * 处理优先级 0,1,2,3,4,5,       db_column: speed_type
     * 
     * 
     * 
     */ 
    @Column("speed_type")
    private java.lang.String speedType;

    //columns END

    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("Id",getId())
            .append("ExecDate",getExecDate())
            .append("ExecStatus",getExecStatus())
            .append("ExecResult",getExecResult())
            .append("ErrMsg",getErrMsg())
            .append("ServiceName",getServiceName())
            .append("MethodName",getMethodName())
            .append("NotifyUrl",getNotifyUrl())
            .append("ParamJson",getParamJson())
            .append("BeanClass",getBeanClass())
            .append("ComeForm",getComeForm())
            .append("CreateTime",getCreateTime())
            .append("SpeedType",getSpeedType())
            .toString();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getId())
            .toHashCode();
    }

	public java.lang.Long getId() {
		return id;
	}

	public void setId(java.lang.Long id) {
		this.id = id;
	}

	public java.util.Date getExecDate() {
		return execDate;
	}

	public void setExecDate(java.util.Date execDate) {
		this.execDate = execDate;
	}

	public java.lang.String getExecStatus() {
		return execStatus;
	}

	public void setExecStatus(java.lang.String execStatus) {
		this.execStatus = execStatus;
	}

	public java.lang.String getExecResult() {
		return execResult;
	}

	public void setExecResult(java.lang.String execResult) {
		this.execResult = execResult;
	}

	public java.lang.String getErrMsg() {
		return errMsg;
	}

	public void setErrMsg(java.lang.String errMsg) {
		this.errMsg = errMsg;
	}

	public java.lang.String getServiceName() {
		return serviceName;
	}

	public void setServiceName(java.lang.String serviceName) {
		this.serviceName = serviceName;
	}

	public java.lang.String getMethodName() {
		return methodName;
	}

	public void setMethodName(java.lang.String methodName) {
		this.methodName = methodName;
	}

	public java.lang.String getNotifyUrl() {
		return notifyUrl;
	}

	public void setNotifyUrl(java.lang.String notifyUrl) {
		this.notifyUrl = notifyUrl;
	}

	public java.lang.String getParamJson() {
		return paramJson;
	}

	public void setParamJson(java.lang.String paramJson) {
		this.paramJson = paramJson;
	}

	public java.lang.String getBeanClass() {
		return beanClass;
	}

	public void setBeanClass(java.lang.String beanClass) {
		this.beanClass = beanClass;
	}

	public java.lang.String getComeForm() {
		return comeForm;
	}

	public void setComeForm(java.lang.String comeForm) {
		this.comeForm = comeForm;
	}

	public java.util.Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(java.util.Date createTime) {
		this.createTime = createTime;
	}

	public java.lang.String getSpeedType() {
		return speedType;
	}

	public void setSpeedType(java.lang.String speedType) {
		this.speedType = speedType;
	}
    
}

