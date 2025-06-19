package org.thingsboard.server.controller;

import io.swagger.annotations.Api;
import org.hibernate.SQLQuery;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.internal.NativeQueryImpl;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.TSysClass;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.dao.message.MessageService;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;
import java.util.Map;

/**
 * 测试专用
 */
@RestController
@Api(value = "测试专用",tags = "测试专用")
@RequestMapping("/test")
public class TestController extends BaseController {

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    MessageService messageService;

    @PersistenceContext
    private EntityManager entityManager;


    @Transactional
    @GetMapping("/test")
    public String test() throws ThingsboardException {
//        String sql="select * from mid_mo mm ";
//        Query query = entityManager.createNativeQuery(sql);
//        query.unwrap(NativeQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
//        List<Map> mapList= query.getResultList();
//        System.out.println(mapList);
//        messageService.test();
        roleService.test();
        return null;
//        SecurityUser currentUser = getCurrentUser();
//        String x = (String) redisTemplate.opsForValue().get(GlobalConstant.LOGIO_REDIS_KEY + "1bf77d60-c443-11ec-856f-97210fa076c0");
//        System.out.println(x);
//        roleService.test();
    }

    @GetMapping("/testClass")
    @ResponseBody
    public TSysClass testClass(@RequestParam Integer classId){
        return tSysClassService.getAndCheckByScheduling(classId);
    }
}
