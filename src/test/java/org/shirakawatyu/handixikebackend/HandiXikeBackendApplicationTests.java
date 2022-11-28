package org.shirakawatyu.handixikebackend;

import cn.hutool.http.HttpGlobalConfig;
import cn.hutool.http.HttpUtil;
import org.junit.jupiter.api.Test;
import org.shirakawatyu.handixikebackend.common.Const;
import org.shirakawatyu.handixikebackend.utils.Requests;
import org.shirakawatyu.handixikebackend.utils.SignUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import javax.annotation.Resource;
import java.net.*;
import java.util.*;

@SpringBootTest
class HandiXikeBackendApplicationTests {

    @Test
    void contextLoads() throws URISyntaxException {
        System.out.println(SignUtil.getSign("ShirakawaTyu"));
    }

}
