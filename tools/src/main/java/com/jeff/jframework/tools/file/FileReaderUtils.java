package com.jeff.jframework.tools.file;

import android.content.res.AssetManager;

import com.jeff.jframework.core.CannotCreateException;
import com.jeff.jframework.core.ContextUtils;
import com.jeff.jframework.tools.CloseUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

/**
 * 文件读取工具类
 *
 * @author Jeff
 * @describe
 * @date 2019/4/1.
 */
public final class FileReaderUtils {
    private static final String TAG = FileReaderUtils.class.getSimpleName();

    private FileReaderUtils() {
        throw new CannotCreateException(this.getClass());
    }

    /**
     * 读取assert文件
     *
     * @param fileName
     * @return
     */
    public static String readAsset(String fileName) {
        InputStream inputStream = null;
        Reader reader = null;
        BufferedReader bufReader = null;
        try {
            AssetManager assetManager = ContextUtils.getResources().getAssets();
            inputStream = assetManager.open(fileName);
            reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            bufReader = new BufferedReader(reader);
            int ch = 0;
            StringBuilder sb = new StringBuilder();
            while ((ch = reader.read()) != -1) {
                sb.append((char) ch);
            }
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            CloseUtils.closeIOQuietly(bufReader,reader,inputStream);
        }
    }

    /**
     * 读取文件内容
     *
     * @param fileName
     * @return
     */
    public static String readFile(String fileName) {
        try {
            File jsonFile = new File(fileName);
            return readInputStream(new FileInputStream(jsonFile));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 读取inputStream
     *
     * @param inputStream
     * @return
     * @throws IOException
     */
    public static String readInputStream(InputStream inputStream) throws IOException {
        Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        BufferedReader bufReader = new BufferedReader(reader);
        int ch = 0;
        StringBuilder sb = new StringBuilder();
        while ((ch = reader.read()) != -1) {
            sb.append((char) ch);
        }
        CloseUtils.closeIOQuietly(bufReader,reader,inputStream);
        return sb.toString();
    }

}
