package creepersan.com.cfilemanager.util;

import android.content.Context;

/** 尺寸转换器
 * Created by CreeperSan on 2017/11/23.
 * GL（arui319）
 * http://blog.csdn.net/arui319
 * 本文可以转载，但是请保留以上作者信息。谢谢。
 */

public class DimenUtil {


    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int px2dp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

}
