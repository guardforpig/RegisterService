package com.middleware.server.Service;

import com.middleware.server.ServerApplication;
import com.middleware.server.annotation.JwtHelper;
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

import static com.middleware.server.Service.RegisterService.groupMap;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@SpringBootTest(classes = ServerApplication.class)
@WebAppConfiguration        //调用Java Web组件，如自动注入ServletContext Bean等
@Transactional      //防止脏数据
@AutoConfigureMockMvc
public class RegisterServiceTest {
    private static String adminToken;

    private static JwtHelper jwtHelper = new JwtHelper();

    @Autowired
    private MockMvc mvc;

    @MockBean
    private RedisUtil redisUtil;

    @Test
    @Transactional(rollbackFor = Exception.class)
    public void test1() throws Exception {
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
        //正确的心跳
        responseString = mvc.perform(post("/beat").param("ip",ip).param("port",port).param("ServiceName",serviceName)
                        .param("GroupName",groupName).contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        System.out.println(responseString);
        expectString = "{\"errno\":200}";
        JSONAssert.assertEquals(expectString, responseString, true);
        // 服务2注册
        responseString = mvc.perform(post("/register").param("ip",ip).
                        param("port","2234").param("ServiceName",serviceName+"1")
                        .param("GroupName",groupName).
                        param("providers",serviceName).
                        contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        System.out.println(responseString);
        expectString = "{\"errno\":200,\"producers\":{\"producer1\":{\"cluster\":[{\"ip\":\"192.168.7.8\"," +
                "\"port\":\"7788\",\"alive\":true,\"providers\":null}]}}}";
        JSONAssert.assertEquals(expectString, responseString, true);
        // 服务2获取服务提供者
        responseString = mvc.perform(post("/loadProviders").param("ip",ip).
                        param("port","2234").param("ServiceName",serviceName+"1")
                        .param("GroupName",groupName).
                        param("providers",serviceName).contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        System.out.println(responseString);
        expectString = "{\"errno\":200,\"producers\":{\"producer1\":{\"cluster\":" +
                "[{\"ip\":\"192.168.7.8\",\"port\":\"7788\",\"alive\":true,\"providers\":null}]}}}";
        JSONAssert.assertEquals(expectString, responseString, true);
        // 服务2登出
        responseString = mvc.perform(post("/logout").param("ip",ip).param("port","2234").param("ServiceName",serviceName+"1")
                        .param("GroupName",groupName).param("providers",serviceName).contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        System.out.println(responseString);
        expectString = "{\"errno\":200}";
        JSONAssert.assertEquals(expectString, responseString, true);
        //错误组名
        responseString = mvc.perform(post("/beat").param("ip",ip).param("port",port).param("ServiceName",serviceName)
                        .param("GroupName","gourp2").contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        System.out.println(responseString);
        expectString = "{\"errno\":203,\"errmsg\":\"组不存在\"}";
        JSONAssert.assertEquals(expectString, responseString, true);
        //错误服务名
        responseString = mvc.perform(post("/beat").param("ip",ip).param("port",port).param("ServiceName",serviceName+"2")
                        .param("GroupName",groupName).contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        System.out.println(responseString);
        expectString = "{\"errno\":203,\"errmsg\":\"服务不存在\"}";
        JSONAssert.assertEquals(expectString, responseString, true);
        //错误ip port
        responseString = mvc.perform(post("/beat").param("ip",ip).param("port","2233").param("ServiceName",serviceName)
                        .param("GroupName",groupName).contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        System.out.println(responseString);
        expectString = "{\"errno\":203,\"errmsg\":\"实例未注册\"}";
        JSONAssert.assertEquals(expectString, responseString, true);
        //两次刷新就会将服务下线
        for(String key:groupMap.keySet()){
            GroupInfo groupInfo=groupMap.get(key);
            try {
                groupInfo.flush();
                if(groupInfo.getServiceMap().isEmpty()) groupMap.remove(key);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        for(String key:groupMap.keySet()){
            GroupInfo groupInfo=groupMap.get(key);
            try {
                groupInfo.flush();
                if(groupInfo.getServiceMap().isEmpty()){
                    groupMap.remove(key);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        responseString = mvc.perform(post("/beat").param("ip",ip).
                        param("port",port).param("ServiceName",serviceName)
                        .param("GroupName",groupName).
                        contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        System.out.println(responseString);
        expectString = "{\"errno\":203,\"errmsg\":\"组不存在\"}";
        JSONAssert.assertEquals(expectString, responseString, true);
    }
}
