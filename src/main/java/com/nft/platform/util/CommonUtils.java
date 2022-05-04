package com.nft.platform.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class CommonUtils {

    public int toPrimitive(Integer value) {
        return value == null ? 0 : value;
    }
}
