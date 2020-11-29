package webapp.demo5_rpc;

import org.noear.nami.annotation.NamiClient;
import webapp.models.UserModel;

import java.util.List;

@NamiClient("demo:/demo5/test/")
public interface rockapi {
    Object test1(Integer a);
    Object test2(int b);
    Object test3();
    UserModel test4();
    List<UserModel> test5();
}