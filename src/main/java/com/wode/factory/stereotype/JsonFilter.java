package com.wode.factory.stereotype;

import java.lang.annotation.*;

/**
 * Created with IntelliJ IDEA.
 * User: wode
 * Date: 14-6-12
 * Time: 下午6:57
 * To change this template use File | Settings | File Templates.
 */
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface JsonFilter {
      public OutPutEnum value() default OutPutEnum.ALL_NO_WRITE;
}
