package com.jeff.jframework.tools.file;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.jeff.jframework.core.CannotCreateException;
import com.jeff.jframework.core.ContextUtils;
import com.jeff.jframework.tools.CloseUtils;
import com.jeff.jframework.tools.StringUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Jeff
 * @describe
 * @date 2020/3/27.
 */
public final class FileUtils {
    private static final String TAG = "FileUtils";

    private static final String FILE_SEP = System.getProperty("file.separator");

    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();

    private FileUtils() {
        throw new CannotCreateException(this.getClass());
    }

    /**
     * Return the file by path.
     *
     * @param filePath The path of file.
     * @return the file
     */
    public static File getFileByPath(final String filePath) {
        return StringUtils.isSpace(filePath) ? null : new File(filePath);
    }

    /**
     * Return whether the file exists.
     *
     * @param file The file.
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isFileExists(final File file) {
        if (file == null) return false;
        if (file.exists()) {
            return true;
        }
        return isFileExists(file.getAbsolutePath());
    }

    /**
     * Return whether the file exists.
     *
     * @param filePath The path of file.
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isFileExists(final String filePath) {
        File file = getFileByPath(filePath);
        if (file == null) return false;
        if (file.exists()) {
            return true;
        }
        return isFileExistsApi29(filePath);
    }

    private static boolean isFileExistsApi29(String filePath) {
        if (Build.VERSION.SDK_INT >= 29) {
            try {
                Uri uri = Uri.parse(filePath);
                ContentResolver cr = ContextUtils.getContext().getContentResolver();
                AssetFileDescriptor afd = cr.openAssetFileDescriptor(uri, "r");
                if (afd == null) return false;
                try {
                    afd.close();
                } catch (IOException ignore) {
                }
            } catch (FileNotFoundException e) {
                return false;
            }
            return true;
        }
        return false;
    }

    /**
     * Create a file if it doesn't exist, otherwise do nothing.
     *
     * @param filePath The path of file.
     * @return {@code true}: exists or creates successfully<br>{@code false}: otherwise
     */
    public static boolean createOrExistsFile(final String filePath) {
        return createOrExistsFile(getFileByPath(filePath));
    }

    /**
     * Create a file if it doesn't exist, otherwise do nothing.
     *
     * @param file The file.
     * @return {@code true}: exists or creates successfully<br>{@code false}: otherwise
     */
    public static boolean createOrExistsFile(final File file) {
        if (file == null) return false;
        if (file.exists()) return file.isFile();
        if (!createOrExistsDir(file.getParentFile())) return false;
        try {
            return file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean createOrExistsDir(final String filePath) {
        return createOrExistsDir(getFileByPath(filePath));
    }

    public static boolean createOrExistsDir(final File file) {
        return file != null && (file.exists() ? file.isDirectory() : file.mkdirs());
    }

    /**
     * Create a file if it doesn't exist, otherwise delete old file before creating.
     *
     * @param filePath The path of file.
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean createFileByDeleteOldFile(final String filePath) {
        return createFileByDeleteOldFile(getFileByPath(filePath));
    }

    /**
     * Create a file if it doesn't exist, otherwise delete old file before creating.
     *
     * @param file The file.
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean createFileByDeleteOldFile(final File file) {
        if (file == null) return false;
        // file exists and unsuccessfully delete then return false
        if (file.exists() && !file.delete()) return false;
        if (!createOrExistsDir(file.getParentFile())) return false;
        try {
            return file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void deleteFile(final File... files) {
        if (files == null || files.length == 0) return;
        EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                for (File file : files) {
                    boolean delete = file.delete();
                    if (!delete) {
                        Log.e(TAG, "delete " + file + " failed!");
                    }
                }
            }
        });
    }

    /**
     * 删除指定目录下的文件
     *
     * @param filePath
     * @param deleteFolder
     */
    public static void deleteAllFilesInFolder(final String filePath, final boolean deleteFolder) {
        if (StringUtils.isEmpty(filePath)) return;
        EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    File file = new File(filePath);
                    if (file.isDirectory()) {
                        File[] files = file.listFiles();
                        for (File f : files) {
                            deleteAllFilesInFolder(f.getAbsolutePath(), true);
                        }
                    }
                    if (deleteFolder) {
                        if (!file.isDirectory()) {
                            file.delete();
                        } else {
                            if (file.listFiles().length == 0) {
                                file.delete();
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void input2File(final String input, final String filePath) {
        EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                BufferedWriter bw = null;
                try {
                    bw = new BufferedWriter(new FileWriter(filePath, true));
                    bw.write(input);
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, "write to " + filePath + " failed!");
                } finally {
                    CloseUtils.closeIO(bw);
                }
            }
        });
    }

    /****************************************************************/
    /*                          内部存储路径                          */
    /****************************************************************/

    /**
     * /data/data/{packageName}/cache/
     * 当安卓设备的存储空间不足（但是没有一个清晰的界限，需要手动定期清除）或卸载时，系统会自动删除这个目录下的文件
     * <p>
     * 内部存储空间不需要申请权限
     *
     * @return
     */
    public static String getCacheDir() {
        return ContextUtils.getContext().getCacheDir().getAbsolutePath();
    }

    /**
     * /data/data/{packageName}/files
     * 当应用被移动到外部存储设备的时候，文件的绝对路径也是变化的；
     * 所以建议当数据存储到这个目录的时候，用相对路径。
     * 系统提供的访问此路径文件的方法是：
     * {@link Context#openFileOutput(String, int)}
     * 类似于SharedPreferences可通过上面方法的mode设置为可被其他应用访问
     * {@link Context#openFileInput(String)}
     * {@link Context#deleteFile(String)}
     * 当应用卸载时，系统会自动删除这个目录下的文件
     * 内部存储空间不需要申请权限
     *
     * @return
     */
    public static String getFilesDir() {
        return ContextUtils.getContext().getFilesDir().getAbsolutePath();
    }


    /****************************************************************/
    /*                          外部存储路径                          */

    /****************************************************************/

    public static boolean isExternalStorageMounted() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    /**
     * /storage/sdcard/
     *
     * @return
     */
    public static String getExternalStorageDirectory() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    /**
     * /storage/sdcard/Android/data/{packageName}/cache/
     * {@link Build.VERSION_CODES#KITKAT}开始不再需要申请权限访问
     * 当应用卸载时，系统会自动删除这个目录下的文件
     *
     * @return
     */
    public static String getExternalCacheDir() {
        return ContextUtils.getContext().getExternalCacheDir().getAbsolutePath();
    }

    /**
     * /storage/sdcard/Android/data/{packageName}/files/
     * {@link Build.VERSION_CODES#KITKAT}开始不再需要申请权限访问
     * 当应用卸载时，系统会自动删除这个目录下的文件
     *
     * @return
     */
    public static String getExternalFilesDir(String type) {
        return ContextUtils.getContext().getExternalFilesDir(type).getAbsolutePath();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static File[] getExternalFilesDirs(String type) {
        return ContextUtils.getContext().getExternalFilesDirs(type);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static File[] getExternalMediaDirs() {
        return ContextUtils.getContext().getExternalMediaDirs();
    }


    /**
     * 外部存储空间公共目录
     *
     * @param type
     * @return
     * @see Environment#DIRECTORY_MUSIC: /storage/emulated/0/Music
     * @see Environment#DIRECTORY_MOVIES: /storage/emulated/0/Movies
     * @see Environment#DIRECTORY_PICTURES: /storage/emulated/0/Pictures
     * @see Environment#DIRECTORY_DOWNLOADS: /storage/emulated/0/Download
     */
    public static String getExternalStoragePublicDirectory(String type) {
        return Environment.getExternalStoragePublicDirectory(type).getAbsolutePath();
    }

    public static void writeFileFromBytes(final File file, final byte[] bytes) {
        EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                FileChannel fc = null;
                try {
                    fc = new FileOutputStream(file, false).getChannel();
                    fc.write(ByteBuffer.wrap(bytes));
                    fc.force(true);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    CloseUtils.closeIO(fc);
                }
            }
        });
    }

    public static byte[] readFile2Bytes(final File file) {
        FileChannel fc = null;
        try {
            fc = new RandomAccessFile(file, "r").getChannel();
            int size = (int) fc.size();
            MappedByteBuffer mbb = fc.map(FileChannel.MapMode.READ_ONLY, 0, size).load();
            byte[] data = new byte[size];
            mbb.get(data, 0, size);
            return data;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            CloseUtils.closeIO(fc);
        }
    }

    /**
     * 复制文件(可更名复制)
     *
     * @param oldPathFile 准备复制的文件源
     * @param newPathFile 拷贝到新绝对路径带文件名(注：目录路径需带文件名)
     * @return
     */
    public static void copyFile(String oldPathFile, String newPathFile) {
        copyFile(new File(oldPathFile), new File(newPathFile));
    }

    /**
     * 复制文件(可更名复制)
     *
     * @param oldfile 准备复制的文件源
     * @param newFile 拷贝到新绝对路径
     * @return
     */
    public static void copyFile(File oldfile, File newFile) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File parentFile = newFile.getParentFile();
            createOrExistsDir(parentFile);
            if (oldfile.exists()) {
                //文件存在时，读入原文件
                InputStream is = new FileInputStream(oldfile);
                FileOutputStream fos = new FileOutputStream(newFile);
                byte[] buffer = new byte[1024];
                while ((byteread = is.read(buffer)) != -1) {
                    //字节数 文件大小
                    bytesum += byteread;
                    fos.write(buffer, 0, byteread);
                }
                CloseUtils.closeIOQuietly(is);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void copyFile(byte[] bytes, String filename) {
        FileOutputStream output = null;
        try {
            output = new FileOutputStream(filename);
            output.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            CloseUtils.closeIOQuietly(output);
        }
    }

    /**
     * 打印当前应用的各个路径信息
     */
    public static void printDirsInfo() {
        try {
            StringBuilder builder = new StringBuilder();
            builder.append("\n外部存储-公共路径&信息：")
                    .append("\nEnvironment.getExternalStorageState()= ")
                    .append(Environment.getExternalStorageState())
                    .append("\nEnvironment.isExternalStorageEmulated()= ")
                    .append(Environment.isExternalStorageEmulated())
                    .append("\nEnvironment.isExternalStorageRemovable()= ")
                    .append(Environment.isExternalStorageRemovable())
                    .append("\nEnvironment.getExternalStorageDirectory()= ")
                    .append(Environment.getExternalStorageDirectory())
                    .append("\nEnvironment.getRootDirectory()= ")
                    .append(Environment.getRootDirectory())
                    .append("\nEnvironment.getDataDirectory()= ")
                    .append(Environment.getDataDirectory())
                    .append("\nEnvironment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)= ")
                    .append(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM))
                    .append("\nEnvironment.getDownloadCacheDirectory()= ")
                    .append(Environment.getDownloadCacheDirectory())
                    .append("\n内部存储-应用私有：")
                    .append("\nContext#getCacheDir()= ")
                    .append(getCacheDir())
                    .append("\nContext#getFilesDir()= ")
                    .append(getFilesDir())
                    .append("\nContext#getDatabasePath()= ")
                    .append(ContextUtils.getContext().getDatabasePath("test.db"))
                    .append("\n外部存储-应用私有：")
                    .append("\nContext#getExternalCacheDir()= ")
                    .append(getExternalCacheDir())
                    .append("\nContext#getExternalFilesDir(Environment.DIRECTORY_MUSIC)= ")
                    .append(getExternalFilesDir(Environment.DIRECTORY_MUSIC))
                    .append("\n其他路径：")
                    .append("\nContext#getPackageResourcePath()= ")
                    .append(ContextUtils.getContext().getPackageResourcePath());

            Log.d(TAG, builder.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
