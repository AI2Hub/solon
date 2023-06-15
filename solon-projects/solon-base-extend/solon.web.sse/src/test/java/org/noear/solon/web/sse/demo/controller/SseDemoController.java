package org.noear.solon.web.sse.demo.controller;

import org.noear.snack.ONode;
import org.noear.solon.Utils;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Get;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.handle.Result;
import org.noear.solon.web.sse.SseEmitter;
import org.noear.solon.web.sse.SseEvent;

import java.util.HashMap;
import java.util.Map;

@Controller
public class SseDemoController {
    static Map<String, SseEmitter> emitterMap = new HashMap<>();

    @Get
    @Mapping("/sse/{id}")
    public void sse(String id) throws Throwable {
        SseEmitter emitter = new SseEmitter(1L)
                .onCompletion(() -> emitterMap.remove(id))
                .onError(e -> emitterMap.remove(id));

        emitterMap.put(id, emitter);

        emitter.start();
    }

    @Get
    @Mapping("/sse/put/{id}")
    public String ssePut(String id) {
        SseEmitter emitter = emitterMap.get(id);
        if (emitter == null) {
            return "No user: " + id;
        }

        String msg = "test msg -> " + System.currentTimeMillis();
        System.out.println(msg);
        emitter.send(msg);
        emitter.send(new SseEvent().id(Utils.guid()).name("update").data(msg));
        emitter.send(ONode.stringify(Result.succeed(msg)));

        return "Ok";
    }

    @Get
    @Mapping("/sse/del/{id}")
    public String sseDel(String id) throws Throwable {
        SseEmitter emitter = emitterMap.get(id);
        if (emitter != null) {
            emitterMap.remove(id);
            emitter.stop();
        }

        return "Ok";
    }
}