package com.middleware.server.console;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.middleware.server.POJO.Member;
import com.middleware.server.annotation.Audit;
import com.middleware.server.common.Common;
import com.middleware.server.common.ResponseCode;
import com.middleware.server.common.ReturnObject;
import com.middleware.server.naming.GroupInfo;
import com.middleware.server.naming.ServiceInfo;
import com.middleware.server.util.BroadcastUdp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.middleware.server.Service.RegisterService.groupMap;

@RestController
@Slf4j
@RequestMapping(value = "/", produces = "application/json;charset=UTF-8",consumes = "application/json;charset=UTF-8")
public class ManageService {
    @GetMapping("/allgroup")
    public Object getAllGroup(@RequestParam(name="page",required = false) Integer page,
                              @RequestParam(name = "pageSize",required = false) Integer pageSize) {
        if(page==null) page=1;
        if(pageSize==null) pageSize=10;
        ReturnObject returnObject=null;
        try{
            List<String> list=new ArrayList<>(groupMap.keySet());
            PageInfo<String> groupPageInfo=new PageInfo<String>(list);
            groupPageInfo.setPages(PageInfo.of(list).getPages());
            groupPageInfo.setTotal(PageInfo.of(list).getTotal());
            groupPageInfo.setPageNum(page);
            groupPageInfo.setPageSize(pageSize);
            returnObject=new ReturnObject(list);
        }catch(Exception e){
            returnObject = new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR, "服务器错误");
        }
        return Common.decorateReturnObject(returnObject);
    }
    @GetMapping("/checkGroup")
    public Object getCertainGroup(@RequestParam(name="page",required = false) Integer page,
                                  @RequestParam(name = "pageSize",required = false) Integer pageSize,
                                  @RequestParam(name="groupName",required = false) String groupName) {
        if(page==null) page=1;
        if(pageSize==null) pageSize=10;
        ReturnObject returnObject=null;
        try{
            List<String> list=new ArrayList<>(groupMap.get(groupName).getServiceMap().keySet());
            PageInfo<String> servicePageInfo=new PageInfo<String>(list);
            servicePageInfo.setPages(PageInfo.of(list).getPages());
            servicePageInfo.setTotal(PageInfo.of(list).getTotal());
            servicePageInfo.setPageNum(page);
            servicePageInfo.setPageSize(pageSize);
            returnObject=new ReturnObject(list);
        }catch(Exception e){
            e.printStackTrace();
            returnObject = new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR, "组名不存在");
        }
        return Common.decorateReturnObject(returnObject);
    }
    @GetMapping("/checkService")
    public Object getCertainService(@RequestParam(name="page",required = false) Integer page,
                                    @RequestParam(name = "pageSize",required = false) Integer pageSize,
                                    @RequestParam(name="groupName",required = false) String groupName,
                                    @RequestParam(name="serviceName",required = false) String serviceName) {
        if(page==null) page=1;
        if(pageSize==null) pageSize=10;
        ReturnObject returnObject=null;
        try{
            List<Member> list=new ArrayList<>(groupMap.get(groupName).getService(serviceName).getCluster());
            PageInfo<Member> servicePageInfo=new PageInfo<Member>(list);
            servicePageInfo.setPages(PageInfo.of(list).getPages());
            servicePageInfo.setTotal(PageInfo.of(list).getTotal());
            servicePageInfo.setPageNum(page);
            servicePageInfo.setPageSize(pageSize);
            returnObject=new ReturnObject(list);
        }catch(Exception e){
            e.printStackTrace();
            returnObject = new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR, "该服务不存在");
        }
        return Common.decorateReturnObject(returnObject);
    }
    //传组、服务、IP、端口
    @Audit
    @PostMapping("/delete")
    public Object delete(@RequestParam(name="groupName",required = false) String groupName,
                         @RequestParam(name="serviceName",required = false) String serviceName,
                         @RequestParam(name="ip",required = false) String ip,
                         @RequestParam(name="port",required = false) String port) {
        ReturnObject returnObject=null;
        try{
            GroupInfo group=groupMap.get(groupName);
            Member member=new Member(ip,port);
            ServiceInfo service=group.getService(serviceName);
            if(service!=null)service.deleteMember(member);
            group.relieveSub(member);
            Map<String,ServiceInfo> map=new HashMap<>();
            map.put(serviceName,service);
            List<Member> subscribers=group.getConsumers(serviceName);
            if(subscribers!=null){
                new BroadcastUdp(JSON.toJSONString(map),subscribers).start();
            }
            returnObject=new ReturnObject();
        }catch(Exception e){
            e.printStackTrace();
            returnObject = new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR, "服务器内部错误/该服务提供者不存在");
        }
        return Common.decorateReturnObject(returnObject);
    }
}
