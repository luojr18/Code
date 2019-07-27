import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.sandbox.vo.User;

public class Test {
    public static void main(String[] args) {
        User user = new User();
        user.setName("张三");
        user.setUserId("1");
        User user1 = new User();
        user1.setName("李四");
        user1.setUserId("2");

        Object[] obj = null;
        String s = JSON.toJSONString(obj, SerializerFeature.WriteClassName);
        System.out.println(s);
    }
}
