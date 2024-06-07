package com.middleware.server.console;
import com.alibaba.fastjson.JSON;
import com.middleware.server.ServerApplication;
import com.middleware.server.annotation.JwtHelper;
import com.middleware.server.common.TokenJwt;
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

import java.util.ArrayList;
import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@SpringBootTest(classes = ServerApplication.class)
@WebAppConfiguration        //调用Java Web组件，如自动注入ServletContext Bean等
@Transactional      //防止脏数据
@AutoConfigureMockMvc
public class AdminControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private RedisUtil redisUtil;

    private static String adminToken = new TokenJwt().creatToken(1L, 6*5*10000);
    @Test
    @Transactional(rollbackFor = Exception.class)
    public void test1() throws Exception {
        int page=1,pageSize=10;
        String ip="192.168.7.8";
        String port="7788";
        String serviceName="producer1";
        String groupName="group1";
        String providers="";
        String responseString=null;
        String expectString=null;
        //查看所有用户
        responseString = mvc.perform(get("/admin/user").header("authorization", adminToken).param("page", String.valueOf(page))
                        .param("pageSize", String.valueOf(pageSize)).contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        System.out.println(responseString);
        expectString = "{\"errno\":0,\"data\":{\"total\":2,\"pages\":1,\"pageSize\":10,\"page\":1,\"list\":[{\"userid\":1,\"state\":1,\"userName\":\"admin\",\"rolelist\":[{\"rolename\":\"admin\",\"description\":\"管理员\"}]},{\"userid\":2,\"state\":1,\"userName\":\"user1\",\"rolelist\":[]}]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectString, responseString, true);
        NewUserBody newUser=new NewUserBody();
        newUser.setUsername("user2");
        ArrayList<String> roleList=new ArrayList<String>(); roleList.add("admin");
        newUser.setRoleNameList(roleList);
        //2
        responseString = mvc.perform(post("/admin/user").header("authorization", adminToken).content(JSON.toJSON(newUser).toString()).contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        System.out.println(responseString);
        expectString = "{\"code\":\"OK\",\"errmsg\":\"成功\",\"data\":null}";
        JSONAssert.assertEquals(expectString, responseString, true);
        //查看某用户
        responseString = mvc.perform(get("/admin/user/2").header("authorization", adminToken).contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        System.out.println(responseString);
        expectString = "{\"errno\":0,\"data\":{\"userid\":2,\"userName\":\"user1\",\"state\":1,\"roles\":[],\"lastlogin\":null},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectString, responseString, true);
        //逻辑删除某用户
        responseString = mvc.perform(delete("/admin/user/2").header("authorization", adminToken).contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        System.out.println(responseString);
        expectString = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectString, responseString, true);
        //更改用户状态
        responseString = mvc.perform(put("/admin/user/2/state").header("authorization", adminToken).contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        System.out.println(responseString);
        expectString = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectString, responseString, true);
        //初始化用户密码
        responseString = mvc.perform(put("/admin/user/2/password").header("authorization", adminToken).contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        System.out.println(responseString);
        expectString = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectString, responseString, true);
        //查看所有角色
        responseString = mvc.perform(get("/admin/roles").header("authorization", adminToken).param("page", String.valueOf(page))
                        .param("pageSize", String.valueOf(pageSize)).contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        System.out.println(responseString);
        expectString = responseString;
        JSONAssert.assertEquals(expectString, responseString, true);
        //更改用角色描述
        responseString = mvc.perform(put("/admin/roles/1").header("authorization", adminToken).param("description","这是管理员").contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        System.out.println(responseString);
        expectString = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectString, responseString, true);
        //批量修改用户角色
        RoleIdListBody roleIdListBody=new RoleIdListBody();
        roleIdListBody.setRoleIdList(Arrays.asList(2L,3L));
        responseString = mvc.perform(put("/admin/user/2/roles").header("authorization", adminToken).content(JSON.toJSON(roleIdListBody).toString()).contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        System.out.println(responseString);
        expectString = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectString, responseString, true);
        //批量修改角色权限
        PrivilegeIdListBody privilegeList=new PrivilegeIdListBody();
        privilegeList.setPrivilegeIdList(Arrays.asList(2L,3L));
        responseString = mvc.perform(put("/admin/roles/1/privileges").header("authorization", adminToken).content(JSON.toJSON(privilegeList).toString()).contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        System.out.println(responseString);
        expectString = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectString, responseString, true);
        //查看所有权限
        responseString = mvc.perform(get("/admin/privileges").header("authorization", adminToken).contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        System.out.println(responseString);
        expectString = "{\"errno\":0,\"data\":[{\"name\":\"逻辑删除某用户\",\"children\":[],\"actions\":[]},{\"name\":\"更改用户状态\",\"children\":[],\"actions\":[]},{\"name\":\"初始化用户密码\",\"children\":[],\"actions\":[]},{\"name\":\"批量修改员工角色\",\"children\":[],\"actions\":[]},{\"name\":\"查看所有角色\",\"children\":[],\"actions\":[]},{\"name\":\"修改角色描述\",\"children\":[],\"actions\":[]},{\"name\":\"批量修改角色权限\",\"children\":[],\"actions\":[]},{\"name\":\"查看所有权限\",\"children\":[],\"actions\":[]}],\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectString, responseString, true);
    }
}
