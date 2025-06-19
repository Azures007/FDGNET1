package org.thingsboard.server.common.data.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 数据字典注解
 *
 * @author Tellsea
 * @date 2020/6/23
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Dict {

    /**
     * 字典类型
     *
     * @return
     */
    String codeVale();

    /**
     * 返回属性名
     *
     * @return
     */
    String codeDsc() default "";

}
