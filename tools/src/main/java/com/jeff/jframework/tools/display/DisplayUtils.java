package com.jeff.jframework.tools.display;

import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import com.jeff.jframework.core.CannotCreateException;
import com.jeff.jframework.core.ContextUtils;
import com.jeff.jframework.core.Preconditions;

/**
 * 屏幕显示相关工具类
 * <p>
 *
 * @author Jeff
 * @date 2020/8/7
 *
 * <a href="mailto:feijeff0486@gmail.com">Contact me</a>
 * <a href="https://github.com/feijeff0486">Follow me</a>
 */
public final class DisplayUtils {
    private static final String TAG = "DisplayUtils";
    /**
     * drawable-ldpi、mipmap-ldpi (dpi=120, density=0.75)
     */
    private static final float MDPI_MIN = 120f;
    /**
     * drawable-mdpi、mipmap-mdpi (dpi=160, density=1)
     */
    private static final float MDPI_MAX = 160f;
    /**
     * drawable-hdpi、mipmap-hdpi (dpi=240, density=1.5)
     */
    private static final float HDPI_MAX = 240f;
    /**
     * drawable-xhdpi、mipmap-xhdpi (dpi=320, density=2)
     */
    private static final float XHDPI_MAX = 320f;
    /**
     * drawable-xxhdpi、mipmap-xxhdpi (dpi=480, density=3)
     */
    private static final float XXHDPI_MAX = 480f;
    /**
     * drawable-xxxhdpi、mipmap-xxxhdpi (dpi=640, density=4)
     */
    private static final float XXXHDPI_MAX = 640f;

    private static boolean sHasInit;
    private static int sWidthPixels;
    private static int sHeightPixels;

    private DisplayUtils(){
        throw new CannotCreateException(this.getClass());
    }

    /**
     * 在必要时初始化
     * @param context
     */
    public synchronized static void initIfNeed(Context context){
        if (sHasInit)return;
        Preconditions.checkArgument(context!=null,"The Param context of DisplayUtils#init(Context) is null!");
        WindowManager windowManager= (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Point point=new Point();
        // 这里推荐使用getRealSize()的方式获取屏幕的宽高，它可以真实的拿到当前屏幕的尺寸。
        // 其它 Api 在部分智能电视和盒子上，拿到的尺寸会小，因为没有计算 StatusBar 或者 NavigationBar的高度，这些都是经验之谈。
        windowManager.getDefaultDisplay().getRealSize(point);
        sWidthPixels=point.x;
        sHeightPixels=point.y;
        sHasInit=true;
    }

    /**
     * 获取屏幕的宽
     * @param context
     * @return 屏幕的宽 单位px
     */
    public static int getScreenWidth(Context context){
        initIfNeed(context);
        return sWidthPixels;
    }

    /**
     * 获取屏幕的高
     * @param context
     * @return 屏幕的高 单位px
     */
    public static int getScreenHeight(Context context){
        initIfNeed(context);
        return sHeightPixels;
    }


    public static DisplayMetrics getDisplayMetrics() {
        return ContextUtils.getResources().getDisplayMetrics();
    }

    /**
     * 该方法获取的可能并不是准确的值
     * @deprecated
     *
     * 使用该方法代替
     * @see #getScreenWidth(Context)
     * @return
     */
    public static int getScreenWidth() {
        return getDisplayMetrics().widthPixels;
    }

    /**
     * 该方法获取的可能并不是准确的值
     * @deprecated
     *
     * 使用该方法代替
     * @see #getScreenHeight(Context)
     * @return
     */
    public static int getScreenHeight() {
        return getDisplayMetrics().heightPixels;
    }

    public static float getSystemDensity() {
        return getDisplayMetrics().density;
    }

    public static float getSystemDensityDpi() {
        return getDisplayMetrics().densityDpi;
    }

    public static float getSystemScaledDensity() {
        return getDisplayMetrics().scaledDensity;
    }

    /**
     * 优先找dpi最接近的，没有就选在drawable/mipmap
     * @return
     */
    static String getBelongDpi(){
        float dpi= getSystemDensityDpi();
        String dir="";
        if (dpi> XXHDPI_MAX &&dpi<= XXXHDPI_MAX){
            dir= "xxxhdpi(480dp~640dp)";
        }else if(dpi> XHDPI_MAX){
            dir= "xxhdpi(320dp~480dp)";
        }else if(dpi> HDPI_MAX){
            dir= "xhdpi(240dp~320dp)";
        }else if (dpi> MDPI_MAX){
            dir= "hdpi(160dp~240dp)";
        }else if (dpi>= MDPI_MIN){
            dir= "mdpi(120dp~160dp)";
        }else {
            return String.format("Device dpi(%s) is beyond 120dp~640dp, please add you resource to nearly dir.",dpi);
        }
        return String.format("Device dpi is %s, please add you resource to %s",dpi,dir);
    }

    /**
     * 打印设备的分辨率信息
     */
    public static void printDensityInfo(){
        String builder = "设备分辨率信息：" +
                String.format("\nmodel= %s", Build.MODEL) +
                String.format("\nSDK版本= %s", Build.VERSION.SDK_INT) +
                String.format("\n系统版本= Android%s", Build.VERSION.RELEASE) +
                String.format("\n屏幕宽度= %spx", getScreenWidth(ContextUtils.getContext())) +
                String.format("\n屏幕高度= %spx",  getScreenHeight(ContextUtils.getContext())) +
                String.format("\nstatusBarHeight= %spx",  BarUtils.getStatusBarHeight()) +
                String.format("\nactionBarHeight= %spx",  BarUtils.getActionBarHeight()) +
                String.format("\n屏幕密度= %s",  getSystemDensity()) +
                String.format("\n屏幕密度DPI= %s\t",  getSystemDensityDpi()) +
                getBelongDpi()+
                String.format("\n屏幕文字密度= %s",  getSystemScaledDensity());
        Log.d(TAG, "printDensityInfo: "+ builder);
    }
}
