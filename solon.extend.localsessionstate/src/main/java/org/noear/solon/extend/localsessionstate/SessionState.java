package org.noear.solon.extend.localsessionstate;

import org.noear.solon.XUtil;
import org.noear.solon.core.Aop;
import org.noear.solon.core.XContext;
import org.noear.solon.core.XSessionState;
import org.noear.solon.extend.localsessionstate.util.EncryptUtil;
import org.noear.solon.extend.localsessionstate.util.IDUtil;
import org.noear.solon.extend.localsessionstate.util.ScheduledStore;


public class SessionState implements XSessionState {
    public final static String SESSIONID_KEY = "SOLONID";
    public final static String SESSIONID_MD5(){return SESSIONID_KEY+"2";}
    public final static String SESSIONID_encrypt = "&L8e!@T0";

    private final ScheduledStore _store;
    public SessionState() {
        if (XServerProp.session_timeout > 0) {
            _expiry = XServerProp.session_timeout;
        }

        if (XServerProp.session_state_domain != null) {
            _domain = XServerProp.session_state_domain;
        }

        _store = new ScheduledStore(_expiry);

    }

    //
    // cookies control
    //
    private int _expiry =  60 * 60 * 2;
    private String _domain=null;

    public  String cookieGet(String key){
        return XContext.current().cookie(key);
    }
    public  void   cookieSet(String key, String val) {
        if (XUtil.isEmpty(_domain)) {
            _domain = XContext.current().uri().getHost();
        }

        XContext.current().cookieSet(key, val, _domain, _expiry);
    }

    protected void updateSessionID() {
        String skey = cookieGet(SESSIONID_KEY);

        if (XUtil.isEmpty(skey) == false) {
            cookieSet(SESSIONID_KEY, skey);
            cookieSet(SESSIONID_MD5(), EncryptUtil.md5(skey + SESSIONID_encrypt));

            _store.delay(sessionId());
        }
    }

    //
    // session control
    //


    @Override
    public boolean replaceable() {
        return false;
    }

    private String _sessionId;

    @Override
    public String sessionId() {
        if(_sessionId != null){
            return _sessionId;
        }

        String skey = cookieGet(SESSIONID_KEY);
        String smd5 = cookieGet(SESSIONID_MD5());

        if(_sessionId == null) {
            if (XUtil.isEmpty(skey) == false && XUtil.isEmpty(smd5) == false) {
                if (EncryptUtil.md5(skey + SESSIONID_encrypt).equals(smd5)) {
                    _sessionId = skey;
                }
            }
        }

        if(_sessionId == null) {
            skey = IDUtil.guid();
            cookieSet(SESSIONID_KEY, skey);
            cookieSet(SESSIONID_MD5(), EncryptUtil.md5(skey + SESSIONID_encrypt));
            _sessionId = skey;
        }

        return _sessionId;
    }

    @Override
    public Object sessionGet(String key) {
        return _store.get(sessionId(),key);
    }

    @Override
    public void sessionSet(String key, Object val) {
        _store.put(sessionId(),key,val);
    }
}
