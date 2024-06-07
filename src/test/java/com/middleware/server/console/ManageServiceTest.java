package com.middleware.server.console;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.middleware.server.ServerApplication;
import com.middleware.server.annotation.JwtHelper;
import com.middleware.server.common.TokenJwt;
import com.middleware.server.model.VO.LoginVO;
import com.middleware.server.naming.GroupInfo;
import com.middleware.server.util.RedisUtil;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@SpringBootTest(classes = ServerApplication.class)
@WebAppConfiguration        //调用Java Web组件，如自动注入ServletContext Bean等
@Transactional      //防止脏数据
@AutoConfigureMockMvc
public class ManageServiceTest {

    private static JwtHelper jwtHelper = new JwtHelper();
    private static String adminToken = new TokenJwt().creatToken(1L, 6*5*10000);
    @Autowired
    private MockMvc mvc;

    @MockBean
    private RedisUtil redisUtil;

    @Test
    @Transactional(rollbackFor = Exception.class)
    public void test1() throws Exception {
        redisUtil.del("u_login_"+"1");
        String ip="192.168.7.8";
        String port="7788";
        String serviceName="producer1";
        String groupName="group1";
        String providers="";
        String responseString=null;
        String expectString=null;
        // 服务1注册
        responseString = mvc.perform(post("/register").param("ip",ip).param("port",port).param("ServiceName",serviceName)
                        .param("GroupName",groupName).param("providers",providers).contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        System.out.println(responseString);
        expectString = "{\"errno\":200,\"producers\":null}";
        JSONAssert.assertEquals(expectString, responseString, true);
        //
        responseString = mvc.perform(get("/allgroup").contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        System.out.println(responseString);
        expectString = "{\"errno\":0,\"data\":[\"group1\"],\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectString, responseString, true);
        //
        responseString = mvc.perform(get("/checkGroup").param("groupName",groupName).contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        System.out.println(responseString);
        expectString = "{\"errno\":0,\"data\":[\"producer1\"],\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectString, responseString, true);
        //
        responseString = mvc.perform(get("/checkService").param("serviceName",serviceName)
                        .param("groupName",groupName).contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        System.out.println(responseString);
        expectString = "{\"errno\":0,\"data\":[{\"ip\":\"192.168.7.8\",\"port\":\"7788\",\"alive\":true,\"providers\":[\"\"]}],\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectString, responseString, true);
        //登录
//        LoginVO loginVO=new LoginVO("admin","123456");
//        responseString = mvc.perform(post("/login").content(JSON.toJSON(loginVO).toString()).contentType("application/json;charset=UTF-8"))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType("application/json;charset=UTF-8"))
//                .andReturn().getResponse().getContentAsString();
//        System.out.println(responseString);
//        expectString = responseString;
//        JSONAssert.assertEquals(expectString, responseString, true);

        //
        responseString = mvc.perform(post("/delete").header("authorization", adminToken).param("ip",ip).param("port",port).param("serviceName",serviceName)
                        .param("groupName",groupName).contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        System.out.println(responseString);
        expectString = "{\"errno\":200,\"producers\":null}";
        //JSONAssert.assertEquals(expectString, responseString, true);
    }
}
