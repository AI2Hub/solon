package org.noear.solon.extend.impl;

import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.runtime.NativeDetector;
import org.noear.solon.core.util.ProxyBinder;
import org.noear.solon.core.wrap.ClassWrap;
import org.noear.solon.proxy.BeanProxy;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * @author noear
 * @since 2.5
 */
public class ProxyBinderExt extends ProxyBinder {
    @Override
    public void binding(BeanWrap bw) {
        if (bw.clz().isInterface()) {
            throw new IllegalStateException("Interfaces are not supported as proxy components: " + bw.clz().getName());
        }

        int modifier = bw.clz().getModifiers();
        if (Modifier.isFinal(modifier)) {
            throw new IllegalStateException("Final classes are not supported as proxy components: " + bw.clz().getName());
        }

        if (Modifier.isAbstract(modifier)) {
            throw new IllegalStateException("Abstract classes are not supported as proxy components: " + bw.clz().getName());
        }

        if (Modifier.isPublic(modifier) == false) {
            throw new IllegalStateException("Not public classes are not supported as proxy components: " + bw.clz().getName());
        }

        if (NativeDetector.isAotRuntime()) {
            //如果是 aot 则注册函数
            ClassWrap clzWrap = ClassWrap.get(bw.clz());
            for (Method m : clzWrap.getMethods()) {
                bw.context().methodGet(m);
            }
        }

        bw.proxySet(BeanProxy.getGlobal());
    }
}