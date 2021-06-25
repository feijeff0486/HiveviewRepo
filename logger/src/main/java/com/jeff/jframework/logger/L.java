package com.jeff.jframework.logger;

import com.jeff.jframework.core.CannotCreateException;

import static com.jeff.jframework.logger.LogCore.print2Console;
import static com.jeff.jframework.logger.LogCore.print2File;
import static com.jeff.jframework.logger.LogCore.processBody;
import static com.jeff.jframework.logger.LogCore.processTagAndHead;

/**
 * 日志工具类
 * @author Jeff
 * @describe
 * @date 2019/5/29.
 */
public final class L {
    private static final LogCore.Config CONFIG=new LogCore.Config();

    private L() {
        throw new CannotCreateException(this.getClass());
    }

    public static LogCore.Config getConfig() {
        return CONFIG;
    }

    public static void v(final Object... contents) {
        log(LevelType.V, CONFIG.getGlobalTag(), contents);
    }

    public static void vTag(final String tag, final Object... contents) {
        log(LevelType.V, tag, contents);
    }

    public static void d(final Object... contents) {
        log(LevelType.D, CONFIG.getGlobalTag(), contents);
    }

    public static void dTag(final String tag, final Object... contents) {
        log(LevelType.D, tag, contents);
    }

    public static void i(final Object... contents) {
        log(LevelType.I, CONFIG.getGlobalTag(), contents);
    }

    public static void iTag(final String tag, final Object... contents) {
        log(LevelType.I, tag, contents);
    }

    public static void w(final Object... contents) {
        log(LevelType.W, CONFIG.getGlobalTag(), contents);
    }

    public static void wTag(final String tag, final Object... contents) {
        log(LevelType.W, tag, contents);
    }

    public static void e(final Object... contents) {
        log(LevelType.E, CONFIG.getGlobalTag(), contents);
    }

    public static void eTag(final String tag, final Object... contents) {
        log(LevelType.E, tag, contents);
    }

    public static void a(final Object... contents) {
        log(LevelType.A, CONFIG.getGlobalTag(), contents);
    }

    public static void aTag(final String tag, final Object... contents) {
        log(LevelType.A, tag, contents);
    }

    public static void file(final Object content) {
        log(ContentType.FILE | LevelType.D, CONFIG.getGlobalTag(), content);
    }

    public static void file(@LevelType final int type, final Object content) {
        log(ContentType.FILE | type, CONFIG.getGlobalTag(), content);
    }

    public static void file(final String tag, final Object content) {
        log(ContentType.FILE | LevelType.D, tag, content);
    }

    public static void file(@LevelType final int type, final String tag, final Object content) {
        log(ContentType.FILE | type, tag, content);
    }

    public static void json(final Object content) {
        log(ContentType.JSON | LevelType.D, CONFIG.getGlobalTag(), content);
    }

    public static void json(@LevelType final int type, final Object content) {
        log(ContentType.JSON | type, CONFIG.getGlobalTag(), content);
    }

    public static void json(final String tag, final Object content) {
        log(ContentType.JSON | LevelType.D, tag, content);
    }

    public static void json(@LevelType final int type, final String tag, final Object content) {
        log(ContentType.JSON | type, tag, content);
    }

    public static void xml(final String content) {
        log(ContentType.XML | LevelType.D, CONFIG.getGlobalTag(), content);
    }

    public static void xml(@LevelType final int type, final String content) {
        log(ContentType.XML | type, CONFIG.getGlobalTag(), content);
    }

    public static void xml(final String tag, final String content) {
        log(ContentType.XML | LevelType.D, tag, content);
    }

    public static void xml(@LevelType final int type, final String tag, final String content) {
        log(ContentType.XML | type, tag, content);
    }

    public static void log(final int type, final String tag, final Object... contents) {
        if (!CONFIG.isLogSwitch()) return;
        int type_low = type & 0x0f, type_high = type & 0xf0;
        if (CONFIG.isLog2ConsoleSwitch() || CONFIG.isLog2FileSwitch() || type_high == ContentType.FILE) {
            if (type_low < CONFIG.getConsoleFilterInt() && type_low < CONFIG.getFileFilterInt()) return;
            final LogCore.TagHead tagHead = processTagAndHead(CONFIG,tag);
            final String body = processBody(type_high, contents);
            if (CONFIG.isLog2ConsoleSwitch() && type_high != ContentType.FILE && type_low >= CONFIG.getConsoleFilterInt()) {
                print2Console(CONFIG,type_low, tagHead.tag, tagHead.consoleHead, body);
            }
            if ((CONFIG.isLog2FileSwitch() || type_high == ContentType.FILE) && type_low >= CONFIG.getFileFilterInt()) {
                print2File(CONFIG,type_low, tagHead.tag, tagHead.fileHead + body);
            }
        }
    }
}
