package com.jeff.jframework.tools.file;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.DimenRes;
import android.support.annotation.RequiresApi;
import android.support.annotation.StringRes;
import android.support.v4.content.FileProvider;

import com.jeff.jframework.core.ContextUtils;

import java.io.File;

/**
 * <p>
 *
 * Android7.0 （N） 开始，将严格执行 StrictMode 模式，也就是说，将对安全做更严格的校验。
 * 而从 Android N 开始，将不允许在 App 间，使用 file:// 的方式，传递一个 File ，否则
 * 会抛出 FileUriExposedException的错误
 *
 * @since Build.VERSION_CODES.N
 *
 * 在xml/xxx.xml配置路径
 * root-path 对应DEVICE_ROOT,也就是File DEVICE_ROOT = new File("/")，即根目录，一般不需要配置。/
 * files-path对应 content.getFileDir() 获取到的目录。/data/data/<包名>/files
 * cache-path对应 content.getCacheDir() 获取到的目录。/data/data/<包名>/cache
 * external-path对应 Environment.getExternalStorageDirectory() 指向的目录。/storage/emulate/0
 * external-files-path对应 ContextCompat.getExternalFilesDirs() 获取到的目录。/storage/emulate/0/Android/data/<包名>/files
 * external-cache-path对应 ContextCompat.getExternalCacheDirs() 获取到的目录。/storage/emulate/0/Android/data/<包名>/cache
 *
 * @see FileProvider#parsePathStrategy(Context, String)
 *
 * 外部存储的公共目录
 * DIRECTORY_MUSIC：音乐类型 /storage/emulate/0/music
 * DIRECTORY_PICTURES：图片类型
 * DIRECTORY_MOVIES：电影类型
 * DIRECTORY_DCIM：照片类型,相机拍摄的照片视频都在这个目录（digital camera in memory） /storage/emulate/0/DCIM
 * DIRECTORY_DOWNLOADS：下载文件类型 /storage/emulate/0/downloads
 * DIRECTORY_DOCUMENTS：文档类型
 * DIRECTORY_RINGTONES：铃声类型
 * DIRECTORY_ALARMS：闹钟提示音类型
 * DIRECTORY_NOTIFICATIONS：通知提示音类型
 * DIRECTORY_PODCASTS：播客音频类型
 *
 * 这些可以通过Environment的getExternalStoragePublicDirectory()来获取
 * <code>
 *     public static File getExternalStoragePublicDirectory(String type);
 * </code>
 *
 * @author Jeff
 * @date 2020/6/19
 *
 * <a href="mailto:feijeff0486@gmail.com">Contact me</a>
 * <a href="https://github.com/feijeff0486">Follow me</a>
 */
public final class FileProviderWrapper {

    public static final String FILE_SEP = System.getProperty("file.separator");

    public static Uri file2Uri(File file){
        if (shouldApplyFileProvider()){
            return FileProvider.getUriForFile(ContextUtils.getContext(),ContextUtils.getContext().getApplicationInfo().packageName.replace(FILE_SEP,".").concat(".FileProvider"), file);
        }else {
            return Uri.fromFile(file);
        }
    }

    public static Intent getInstallIntent(Uri uri){
        Intent intent=new Intent();
        String type = "application/vnd.android.package-archive";
        intent.setDataAndType(uri, type);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (shouldApplyFileProvider()) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        return intent;
    }

    public static boolean shouldApplyFileProvider(){
        return Build.VERSION.SDK_INT>=Build.VERSION_CODES.N;
    }

    ////////////////////////////////////////////////////////////////////////
    //////////////////////////////获取外部储存卡资源////////////////////////////
    ////////////////////////////////////////////////////////////////////////
    /**
     * 获取外部存储卡根路径
     * 对应external-path标签下的设置
     * 路径：/storage/emulate/0
     * @return
     */
    public static String getExternalStorageDirectory(){
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    /**
     * 获取外部存储的公共目录
     * @param type
     *      DIRECTORY_MUSIC：音乐类型 /storage/emulate/0/music
     *      DIRECTORY_PICTURES：图片类型
     *      DIRECTORY_MOVIES：电影类型
     *      DIRECTORY_DCIM：照片类型,相机拍摄的照片视频都在这个目录（digital camera in memory） /storage/emulate/0/DCIM
     *      DIRECTORY_DOWNLOADS：下载文件类型 /storage/emulate/0/downloads
     *      DIRECTORY_DOCUMENTS：文档类型
     *      DIRECTORY_RINGTONES：铃声类型
     *      DIRECTORY_ALARMS：闹钟提示音类型
     *      DIRECTORY_NOTIFICATIONS：通知提示音类型
     *      DIRECTORY_PODCASTS：播客音频类型
     * @return
     */
    public static File getExternalStoragePublicDirectory(String type){
        return Environment.getExternalStoragePublicDirectory(type);
    }

    /**
     * 是否已挂载外部存储卡
     * @return
     */
    public static boolean isExternalStorageMounted(){
        return Environment.MEDIA_MOUNTED.equals(getExternalStorageState());
    }

    /**
     * 获取外部存储卡的状态
     * @return
     */
    public static String getExternalStorageState(){
        return Environment.getExternalStorageState();
    }

    ////////////////////////////////////////////////////////////////////////
    //////////////////////////////获取文件资源////////////////////////////////
    ////////////////////////////////////////////////////////////////////////
    /**
     * 获取内部文件路径
     * 对应files-path标签下的设置
     * 路径：/data/data/<包名>/files
     *
     * 当应用被移动到外部存储设备的时候，文件的绝对路径也是变化的；
     * 所以建议当数据存储到这个目录的时候，用相对路径。
     * 系统提供的访问此路径文件的方法是：
     * {@link Context#openFileOutput(String, int)}
     * 类似于SharedPreferences可通过上面方法的mode设置为可被其他应用访问
     * {@link Context#openFileInput(String)}
     * {@link Context#deleteFile(String)}
     * 当应用卸载时，系统会自动删除这个目录下的文件
     * 内部存储空间不需要申请权限
     * @return
     */
    public static File getFilesDir(){
        return ContextUtils.getContext().getFilesDir();
    }

    /**
     * 获取外部文件路径
     * 对应external-files-path标签下的设置
     * 路径：/storage/emulate/0/Android/data/<包名>/files
     * @param type
     * @return
     */
    public static File getExternalFilesDir(String type){
        return ContextUtils.getContext().getExternalFilesDir(type);
    }

    /**
     * 获取外部文件路径
     * 对应external-files-path标签下的设置
     * 路径：/storage/emulate/0/Android/data/<包名>/files
     *
     * {@link Build.VERSION_CODES#KITKAT}开始不再需要申请权限访问
     * 当应用卸载时，系统会自动删除这个目录下的文件
     * @param type
     * @return
     */
    public static File[] getExternalFilesDirs(String type){
        return ContextUtils.getContext().getExternalFilesDirs(type);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static File[] getExternalMediaDirs() {
        return ContextUtils.getContext().getExternalMediaDirs();
    }

    ////////////////////////////////////////////////////////////////////////
    //////////////////////////////获取缓存资源////////////////////////////////
    ////////////////////////////////////////////////////////////////////////

    /**
     * 获取内部缓存路径
     * 对应cache-path标签下的设置
     * 路径：/data/data/<包名>/cache
     *
     * 当安卓设备的存储空间不足（但是没有一个清晰的界限，需要手动定期清除）或卸载时，系统会自动删除这个目录下的文件
     * 内部存储空间不需要申请权限
     * @return
     */
    public static File getCacheDir(){
        return ContextUtils.getContext().getCacheDir();
    }

    /**
     * 获取外部缓存路径
     * 对应external-cache-path标签下的设置
     * 路径：/storage/emulate/0/Android/data/<包名>/cache
     *
     * {@link Build.VERSION_CODES#KITKAT}开始不再需要申请权限访问
     * 当应用卸载时，系统会自动删除这个目录下的文件
     * @return
     */
    public static File getExternalCacheDir(){
        return ContextUtils.getContext().getExternalCacheDir();
    }

    /**
     * 获取外部缓存路径
     * 对应external-cache-path标签下的设置
     * 路径：/storage/emulate/0/Android/data/<包名>/cache
     * @return
     */
    public static File[] getExternalCacheDirs(){
        return ContextUtils.getContext().getExternalCacheDirs();
    }

    ////////////////////////////////////////////////////////////////////////
    //////////////////////////////获取应用内资源///////////////////////////////
    ////////////////////////////////////////////////////////////////////////

    public static String getString(@StringRes int resId){
        return ContextUtils.getString(resId);
    }

    public static int getColor(int resId){
        return ContextUtils.getColor(resId);
    }

    public static float getDimen(@DimenRes int id){
        return ContextUtils.getDimen(id);
    }

    public static Drawable getDrawable(int resId){
        return ContextUtils.getDrawable(resId);
    }

    public static AssetManager getAssets(){
        return ContextUtils.getAssets();
    }
}
