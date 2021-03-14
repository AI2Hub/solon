package org.noear.solon.cloud.service;

import org.noear.solon.cloud.model.Entry;

/**
 * 云端断路器服务
 *
 * @author noear
 * @since 1.3
 */
public interface CloudBreakerService {
    /**
     * 获取入口
     *
     * @param breakerName 断路器名称
     */
    Entry entry(String breakerName) throws Exception;
}
