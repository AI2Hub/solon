package webapp.demoh_xsocket;


import org.noear.nami.annotation.NamiClient;

@NamiClient("localrpc:/demoe/rpc")
public interface HelloRpcService {
    String hello(String name);
}
