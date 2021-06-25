package com.jeff.jframework.logger;

import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Model日志工厂
 * <p>
 *
 * @author Jeff
 * @date 2020/3/31 16:22
 *
 * <a href="mailto:feijeff0486@gmail.com">Contact me</a>
 * <a href="https://github.com/feijeff0486">Follow me</a>
 */
public final class ModuleLoggerFactory {
    private static Map<String, ModuleLogger> sModuleLoggerMap = new HashMap<>();

    public static ModuleLogger create(String moduleTag) {
        if (TextUtils.isEmpty(moduleTag)) return null;
        ModuleLogger moduleLogger = sModuleLoggerMap.get(moduleTag);
        if (sModuleLoggerMap.get(moduleTag) == null) {
            moduleLogger = new ModuleLogger(moduleTag);
            sModuleLoggerMap.put(moduleTag, moduleLogger);
        }
        return moduleLogger;
    }

    public static ModuleLogger create(LogCore.Config config) {
        if (config == null) return null;
        return create(config.getGlobalTag());
    }

    public static ModuleLogger get(String moduleTag) {
        if (TextUtils.isEmpty(moduleTag)) return null;
        return create(defaultConfig().setGlobalTag(moduleTag));
    }

    public static LogCore.Config defaultConfig() {
        return new LogCore.Config().setStackOffset(1);
    }
}
