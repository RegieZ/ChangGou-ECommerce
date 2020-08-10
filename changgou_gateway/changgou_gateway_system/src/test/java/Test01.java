import org.junit.Test;
import org.springframework.security.crypto.bcrypt.BCrypt;

public class Test01 {

    @Test
    public void test01() {
        //加密
        String hashPw = BCrypt.hashpw("123456", BCrypt.gensalt());
        System.out.println("加密后：" + hashPw);
        //验证
        boolean checkPw = BCrypt.checkpw("123456", hashPw);
        System.out.println("密码校验是否成功：" + checkPw);
    }
}
