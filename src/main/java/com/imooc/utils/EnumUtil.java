package com.imooc.utils;

import com.imooc.enums.CodeEnum;

/**
 * 通过code值获取枚举类
 * Created by 廖师兄
 * 2017-07-16 18:36
 */
public class EnumUtil {

    public static <T extends CodeEnum> T getByCode(Integer code, Class<T> enumClass) {
        for (T each : enumClass.getEnumConstants()) {
            if (code.equals(each.getCode())) {
                return each;
            }
        }
        return null;
    }
}
