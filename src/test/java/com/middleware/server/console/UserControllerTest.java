package com.middleware.server.console;
import com.alibaba.fastjson.JSON;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@SpringBootTest(classes = ServerApplication.class)
@WebAppConfiguration        //调用Java Web组件，如自动注入ServletContext Bean等
@Transactional      //防止脏数据
@AutoConfigureMockMvc
public class UserControllerTest {
    private static String adminToken = new TokenJwt().creatToken(1L, 6*5*10000);

    private static JwtHelper jwtHelper = new JwtHelper();

    @Autowired
    private MockMvc mvc;

    @MockBean
    private RedisUtil redisUtil;

    @Test
    @Transactional(rollbackFor = Exception.class)
    public void test1() throws Exception {
        String responseString=null;
        String expectString=null;
        //登录
        LoginVO loginVO=new LoginVO("admin","123456");
        responseString = mvc.perform(post("/login").content(JSON.toJSON(loginVO).toString()).contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        System.out.println(responseString);
        expectString = responseString;
        //JSONAssert.assertEquals(expectString, responseString, true);
        //增加用户角色
        responseString = mvc.perform(post("/users/1/beusers/2/roles/1").header("authorization", adminToken).contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        System.out.println(responseString);
        expectString = responseString;
        JSONAssert.assertEquals(expectString, responseString, true);
        //查看用户角色
        responseString = mvc.perform(get("/users/1/target/2").header("authorization", adminToken).contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        System.out.println(responseString);
        expectString = responseString;
        JSONAssert.assertEquals(expectString, responseString, true);
        //删除用户角色
        responseString = mvc.perform(delete("/user_id/2/role_id/1").header("authorization", adminToken).contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        System.out.println(responseString);
        expectString = "{\"errno\":0,\"data\":1,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectString, responseString, true);
        //登出
        responseString = mvc.perform(get("/userLogout").header("authorization", adminToken).contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        System.out.println(responseString);
        expectString = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectString, responseString, true);
    }
}
