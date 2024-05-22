package org.noear.solon.sessionstate.jedis;

import org.noear.snack.ONode;
import org.noear.solon.core.serialize.Serializer;

/**
 * @author noear
 * @since 2.5
 */
public class JsonSerializer implements Serializer<String> {
    public static final JsonSerializer instance = new JsonSerializer();

    @Override
    public String name() {
        return "json-snack3";
    }

    @Override
    public String serialize(Object fromObj)  {
        return ONode.stringify(fromObj);
    }

    @Override
    public Object deserialize(String dta, Class<?> toClz)  {
        return ONode.deserialize(dta, toClz);
    }
}
