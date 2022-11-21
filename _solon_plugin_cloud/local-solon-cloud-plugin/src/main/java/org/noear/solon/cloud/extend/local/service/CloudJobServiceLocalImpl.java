package org.noear.solon.cloud.extend.local.service;

import org.noear.solon.cloud.CloudJobHandler;
import org.noear.solon.cloud.exception.CloudJobException;
import org.noear.solon.cloud.extend.local.impl.job.CloudJobRunnable;
import org.noear.solon.cloud.extend.local.impl.job.JobManager;
import org.noear.solon.cloud.service.CloudJobService;

import java.text.ParseException;

/**
 * @author noear
 * @since 1.10
 */
public class CloudJobServiceLocalImpl implements CloudJobService {
    @Override
    public boolean register(String name, String cron7x, String description, CloudJobHandler handler) {
        try {
            JobManager.add(name, cron7x, true, new CloudJobRunnable(handler));
            return true;
        } catch (ParseException e) {
            throw new CloudJobException(e);
        }
    }

    @Override
    public boolean isRegistered(String name) {
        return JobManager.contains(name);
    }
}