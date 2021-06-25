package com.jeff.jframework.logger;

/**
 * 日志工具类
 * @author Jeff
 * @describe
 * @date 2019/5/29.
 */
public class ModuleLogger {
    private static LogCore.Config CONFIG=new LogCore.Config();

    public ModuleLogger(String tag){
        CONFIG.setGlobalTag(tag);
    }

    public ModuleLogger(LogCore.Config config) {
        setConfig(config);
    }

    public void setConfig(LogCore.Config config) {
        if (config!=null){
            CONFIG=config;
        }
    }

    public LogCore.Config getConfig() {
        return CONFIG;
    }

    public void v(final Object... contents) {
        log(LevelType.V, CONFIG.getGlobalTag(), contents);
    }

    public void vTag(final String tag, final Object... contents) {
        log(LevelType.V, tag, contents);
    }

    public void d(final Object... contents) {
        log(LevelType.D, CONFIG.getGlobalTag(), contents);
    }

    public void dTag(final String tag, final Object... contents) {
        log(LevelType.D, tag, contents);
    }

    public void i(final Object... contents) {
        log(LevelType.I, CONFIG.getGlobalTag(), contents);
    }

    public void iTag(final String tag, final Object... contents) {
        log(LevelType.I, tag, contents);
    }

    public void w(final Object... contents) {
        log(LevelType.W, CONFIG.getGlobalTag(), contents);
    }

    public void wTag(final String tag, final Object... contents) {
        log(LevelType.W, tag, contents);
    }

    public void e(final Object... contents) {
        log(LevelType.E, CONFIG.getGlobalTag(), contents);
    }

    public void eTag(final String tag, final Object... contents) {
        log(LevelType.E, tag, contents);
    }

    public void a(final Object... contents) {
        log(LevelType.A, CONFIG.getGlobalTag(), contents);
    }

    public void aTag(final String tag, final Object... contents) {
        log(LevelType.A, tag, contents);
    }

    public void file(final Object content) {
        log(ContentType.FILE | LevelType.D, CONFIG.getGlobalTag(), content);
    }

    public void file(@LevelType final int type, final Object content) {
        log(ContentType.FILE | type, CONFIG.getGlobalTag(), content);
    }

    public void file(final String tag, final Object content) {
        log(ContentType.FILE | LevelType.D, tag, content);
    }

    public void file(@LevelType final int type, final String tag, final Object content) {
        log(ContentType.FILE | type, tag, content);
    }

    public void json(final Object content) {
        log(ContentType.JSON | LevelType.D, CONFIG.getGlobalTag(), content);
    }

    public void json(@LevelType final int type, final Object content) {
        log(ContentType.JSON | type, CONFIG.getGlobalTag(), content);
    }

    public void json(final String tag, final Object content) {
        log(ContentType.JSON | LevelType.D, tag, content);
    }

    public void json(@LevelType final int type, final String tag, final Object content) {
        log(ContentType.JSON | type, tag, content);
    }

    public void xml(final String content) {
        log(ContentType.XML | LevelType.D, CONFIG.getGlobalTag(), content);
    }

    public void xml(@LevelType final int type, final String content) {
        log(ContentType.XML | type, CONFIG.getGlobalTag(), content);
    }

    public void xml(final String tag, final String content) {
        log(ContentType.XML | LevelType.D, tag, content);
    }

    public void xml(@LevelType final int type, final String tag, final String content) {
        log(ContentType.XML | type, tag, content);
    }

    public void log(final int type, final String tag, final Object... contents) {
        if (!CONFIG.isLogSwitch()) return;
        int type_low = type & 0x0f, type_high = type & 0xf0;
        if (CONFIG.isLog2ConsoleSwitch() || CONFIG.isLog2FileSwitch() || type_high == ContentType.FILE) {
            if (type_low < CONFIG.getConsoleFilterInt() && type_low < CONFIG.getFileFilterInt()) return;
            final LogCore.TagHead tagHead = LogCore.processTagAndHead(CONFIG,tag);
            final String body = LogCore.processBody(type_high, contents);
            if (CONFIG.isLog2ConsoleSwitch() && type_high != ContentType.FILE && type_low >= CONFIG.getConsoleFilterInt()) {
                LogCore.print2Console(CONFIG,type_low, tagHead.tag, tagHead.consoleHead, body);
            }
            if ((CONFIG.isLog2FileSwitch() || type_high == ContentType.FILE) && type_low >= CONFIG.getFileFilterInt()) {
                LogCore.print2File(CONFIG,type_low, tagHead.tag, tagHead.fileHead + body);
            }
        }
    }
}
