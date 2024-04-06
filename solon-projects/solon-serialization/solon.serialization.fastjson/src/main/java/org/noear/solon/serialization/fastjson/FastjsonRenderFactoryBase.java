package org.noear.solon.serialization.fastjson;

import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializeWriter;
import org.noear.solon.core.convert.Converter;
import org.noear.solon.serialization.JsonRenderFactory;

import java.lang.reflect.Type;


/**
 * Json 渲染器工厂基类
 *
 * @author noear
 * @since 1.5
 */
public abstract class FastjsonRenderFactoryBase implements JsonRenderFactory {
    public abstract SerializeConfig config();

    public void addEncoder(Type clz, ObjectSerializer encoder) {
        config().put(clz, encoder);

        if (clz == Long.class) {
            config().put(Long.TYPE, encoder);
        } else if (clz == Integer.class) {
            config().put(Integer.TYPE, encoder);
        }
    }

    @Override
    public <T> void addConvertor(Class<T> clz, Converter<T,Object> converter) {
        addEncoder(clz, (ser, obj, fieldName, fieldType, features) -> {
            Object val = converter.convert((T) obj);

            SerializeWriter out = ser.getWriter();

            if (val == null) {
                out.writeNull();
            } else if (val instanceof String) {
                out.writeString((String) val);
            } else if (val instanceof Number) {
                if (val instanceof Integer || val instanceof Long) {
                    out.writeLong(((Number) val).longValue());
                } else {
                    out.writeDouble(((Number) val).doubleValue(), false);
                }
            } else {
                throw new IllegalArgumentException("The result type of the converter is not supported: " + val.getClass().getName());
            }
        });
    }
}
