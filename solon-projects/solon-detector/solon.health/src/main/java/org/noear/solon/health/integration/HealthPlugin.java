package org.noear.solon.health.integration;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.health.HealthChecker;
import org.noear.solon.health.HealthHandler;
import org.noear.solon.health.HealthIndicator;

/**
 * @author iYarnFog
 * @since 1.5
 */
public class HealthPlugin implements Plugin {
    @Override
    public void start(AppContext context) {
        //
        // HealthHandler 独立出来，便于其它检测路径的复用
        //
        Solon.app().get(HealthHandler.HANDLER_PATH, HealthHandler.getInstance());
        Solon.app().head(HealthHandler.HANDLER_PATH, HealthHandler.getInstance());

        //添加 HealthIndicator 自动注册
        context.subWrapsOfType(HealthIndicator.class, bw -> {
            if (Utils.isEmpty(bw.name())) {
                //使用类名作指标名
                HealthChecker.addIndicator(bw.clz().getSimpleName(), bw.get());
            } else {
                HealthChecker.addIndicator(bw.name(), bw.get());
            }
        });
    }
}