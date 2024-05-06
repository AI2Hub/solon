package webapp.demo2_mvc;

import org.noear.snack.ONode;
import org.noear.solon.Utils;
import org.noear.solon.annotation.*;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.ModelAndView;
import webapp.models.UserModel;

import java.util.List;
import java.util.Map;

/**
 * @author noear 2021/12/3 created
 */
@Mapping("/demo2/json/")
@Remoting
public class JsonController {
    @Produces("text/json")
    @Mapping("/json")
    public Object json() {
        return "{}";
    }

    @Mapping("/map")
    public Object map(Map<String, UserModel> userMap, ModelAndView mv) {
        if (userMap == null) {
            return null;
        } else {
            return userMap.get("1").getId();
        }
    }

    @Mapping("/list")
    public Object list(List<UserModel> userAry, ModelAndView mv) {
        if (userAry == null) {
            return null;
        } else {
            return userAry.get(0).getId();
        }
    }

    @Mapping("/bean")
    public Object bean(UserModel user) {
        return user;
    }

    @Mapping("/bean_map")
    public String bean_map(@Body Map<String, Object> user) {
        return ONode.stringify(user);
    }

    @Mapping("/body")
    public Integer body(@Body String body) {
        if (Utils.isEmpty(body)) {
            return 0;
        } else {
            return body.length();
        }
    }

    @Mapping("/form")
    public Integer form(String p) {
        if (Utils.isEmpty(p)) {
            return 0;
        } else {
            return p.length();
        }
    }

    @Mapping("/header")
    public Integer header(@Header("p") String p) {
        if (Utils.isEmpty(p)) {
            return 0;
        } else {
            return p.length();
        }
    }

    @Mapping("/cookie2")
    public Integer cookie2(@Cookie("p") String p) {
        if (Utils.isEmpty(p)) {
            return 0;
        } else {
            return p.length();
        }
    }

    @Mapping("/cookie")
    public Integer cookie(Context ctx) {
        String p = ctx.cookie("p");

        if (Utils.isEmpty(p)) {
            return 0;
        } else {
            return p.length();
        }
    }
}
