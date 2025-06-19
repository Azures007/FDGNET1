package org.thingsboard.server.service.rpc;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.thingsboard.server.dao.dto.LcToStartDTO;
import org.thingsboard.server.dao.vo.LcToStartVo;

//@FeignClient(name = "lc-client" ,url = "http://192.168.175.21:8083")
@FeignClient(name = "lc", url = "${feign.lc-Ip}:${feign.lc-port}/${feign.midMoApi_url}")
public interface LcERPCallBack {

    @PostMapping("/toStart")
    LcToStartVo toStart(@RequestBody LcToStartDTO lcToStartDTO);

    //安全认证
    //添加Taken
    //@RequestMapping("/find")
    //String find(@RequestHeader(name = "Token",required = true) String Token);

}
