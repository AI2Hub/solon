package org.noear.solon.extend.schedule;

import org.noear.solon.core.AppContext;
import org.noear.solon.core.LifecycleIndex;
import org.noear.solon.core.Plugin;

/**
 * solon.extend.schedule 相对于 cron4j-solon-plugin 的区别：
 *
 * getInterval 和 getThreads 可动态控制；例，夜间或流量小时弹性变小数值.
 *
 * */
public class XPluginImp implements Plugin {
    @Override
    public void start(AppContext context) {
        context.lifecycle(LifecycleIndex.PLUGIN_BEAN_USES, () -> {
            context.beanForeach((v) -> {
                if (v.raw() instanceof IJob) {
                    JobManager.register(new JobEntity(v.name(), v.raw()));
                }
            });
        });

        context.lifecycle(Integer.MAX_VALUE, () -> {
            JobManager.run(JobRunner.global);
        });
    }
}