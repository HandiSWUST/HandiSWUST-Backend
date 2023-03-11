package org.shirakawatyu.handixikebackend;

import cn.hutool.core.date.DateUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

@SpringBootTest
class HandiXikeBackendApplicationTests {

    @Test
    public void test() throws NoSuchAlgorithmException {
        System.out.println(new RuntimeException().getMessage());
    }
}
