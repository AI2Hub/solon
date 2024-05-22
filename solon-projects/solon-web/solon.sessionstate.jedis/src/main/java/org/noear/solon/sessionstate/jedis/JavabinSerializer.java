package org.noear.solon.sessionstate.jedis;


import org.noear.solon.core.serialize.Serializer;
import org.noear.redisx.utils.SerializationUtil;

import java.util.Base64;

/**
 * @author noear
 * @since 1.5
 */
public class JavabinSerializer implements Serializer<String> {
    public static final JavabinSerializer instance = new JavabinSerializer();

    @Override
    public String name() {
        return "java-bin";
    }

    @Override
    public String serialize(Object fromObj)  {
        if(fromObj == null){
            return null;
        }

        byte[] tmp = SerializationUtil.serialize(fromObj);
        return Base64.getEncoder().encodeToString(tmp);
    }

    @Override
    public Object deserialize(String dta, Class<?> toClz) {
        if(dta == null){
            return null;
        }

        byte[] bytes = Base64.getDecoder().decode(dta);
        return SerializationUtil.deserialize(bytes);
    }
}