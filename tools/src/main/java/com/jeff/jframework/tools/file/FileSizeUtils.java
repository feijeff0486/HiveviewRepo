package com.jeff.jframework.tools.file;

import android.support.annotation.IntDef;
import android.util.Log;

import com.jeff.jframework.core.CannotCreateException;

import java.io.File;
import java.io.FileInputStream;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.text.DecimalFormat;

/**
 *
 * <p>
 * @author Jeff
 * @date 2020/07/29 20:32
 *
 * <a href="mailto:feijeff0486@gmail.com">Contact me</a>
 * <a href="https://github.com/feijeff0486">Follow me</a>
 */
public final class FileSizeUtils {

    private FileSizeUtils() {
        throw new CannotCreateException(this.getClass());
    }

    @IntDef({SizeUnits.UNIT_B, SizeUnits.UNIT_KB, SizeUnits.UNIT_MB, SizeUnits.UNIT_GB})
    @Retention(RetentionPolicy.SOURCE)
    public @interface SizeUnits{
        /**获取文件大小单位为B的double值*/
        int UNIT_B = 0x1;
        /**获取文件大小单位为KB的double值*/
        int UNIT_KB = 0x2;
        /**获取文件大小单位为MB的double值*/
        int UNIT_MB = 0x3;
        /**获取文件大小单位为GB的double值*/
        int UNIT_GB = 0x4;
    }

    /**
     * 获取文件指定文件的指定单位的大小
     *
     * @param filePath 文件路径
     * @param unit 单位 {@link SizeUnits}
     * @return double值的大小
     */
    public static double getFileOrFilesSize(String filePath,@SizeUnits int unit) {
        File file = new File(filePath);
        long blockSize = 0;
        try {
            if (file.isDirectory()) {
                blockSize = getFileSizes(file);
            } else {
                blockSize = getFileSize(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("获取文件大小", "获取失败!");
        }
        return FormatFileSize(blockSize, unit);
    }

    /**
     * 调用此方法自动计算指定文件或指定文件夹的大小
     *
     * @param filePath 文件路径
     * @return 计算好的带B、KB、MB、GB的字符串
     */
    public static String getAutoFileOrFilesSize(String filePath) {
        File file = new File(filePath);
        long blockSize = 0;
        try {
            if (file.isDirectory()) {
                blockSize = getFileSizes(file);
            } else {
                blockSize = getFileSize(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("获取文件大小", "获取失败!");
        }
        return FormatFileSize(blockSize);
    }

    /**
     * 获取指定文件大小
     *
     * @param file
     * @return
     * @throws Exception
     */
    private static long getFileSize(File file) throws Exception {
        long size = 0;
        if (file.exists()) {
            FileInputStream fis = new FileInputStream(file);
            size = fis.available();
        } else {
            Log.e("获取文件大小", "文件不存在!");
        }
        return size;
    }

    /**
     * 获取指定文件夹
     *
     * @param file
     * @return
     * @throws Exception
     */
    private static long getFileSizes(File file) throws Exception {
        long size = 0;
        File[] flist = file.listFiles();
        for (int i = 0; i < flist.length; i++) {
            if (flist[i].isDirectory()) {
                size = size + getFileSizes(flist[i]);
            } else {
                size = size + getFileSize(flist[i]);
            }
        }
        return size;
    }

    /**
     * 转换文件大小
     *
     * @param size
     * @return
     */
    private static String FormatFileSize(long size) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        String wrongSize = "0B";
        if (size == 0) {
            return wrongSize;
        }
        if (size < 1024) {
            fileSizeString = df.format((double) size) + "B";
        } else if (size < 1048576) {
            fileSizeString = df.format((double) size / 1024) + "KB";
        } else if (size < 1073741824) {
            fileSizeString = df.format((double) size / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) size / 1073741824) + "GB";
        }
        return fileSizeString;
    }

    /**
     * 转换文件大小,指定转换的类型
     *
     * @param size
     * @param unit 单位 {@link SizeUnits}
     * @return
     */
    private static double FormatFileSize(long size,@SizeUnits int unit) {
        DecimalFormat df = new DecimalFormat("#.00");
        double fileSize = 0;
        switch (unit) {
            case SizeUnits.UNIT_B:
                fileSize = Double.valueOf(df.format((double) size));
                break;
            case SizeUnits.UNIT_KB:
                fileSize = Double.valueOf(df.format((double) size / 1024));
                break;
            case SizeUnits.UNIT_MB:
                fileSize = Double.valueOf(df.format((double) size / 1048576));
                break;
            case SizeUnits.UNIT_GB:
                fileSize = Double.valueOf(df.format((double) size / 1073741824));
                break;
            default:
                break;
        }
        return fileSize;
    }
}
