package com.wode.factory.stereotype;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 当不要登录cookie解析和session解析的时候，请标记此注解
 * 此注解不能和LoginStereotype同时使用
 * @ClassName: NoCheckLogin 
 * @Description: 
 * @author mengkaixuan
 * @Date 2014年5月22日 下午6:32:37
 * @Version
 */
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NoCheckAsync {

}
