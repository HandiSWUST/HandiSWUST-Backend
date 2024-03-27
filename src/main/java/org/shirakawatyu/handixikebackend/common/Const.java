package org.shirakawatyu.handixikebackend.common;

import lombok.experimental.UtilityClass;
import org.shirakawatyu.handixikebackend.utils.DateUtil;

@UtilityClass
public class Const {
    public static final String CURRENT_TERM = "2023-2024-2";
    public static final long CURRENT_TERM_LONG = 202320242;    // 2023-2024-1
    public static final long START_DATE = DateUtil.getDate("2024-2-26");
    public static final long END_DATE = DateUtil.getDate("2024-7-14");
    public static final String WEB_VERSION = "1.0";
}
