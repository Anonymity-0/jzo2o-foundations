package com.jzo2o.foundations.service;

import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.jzo2o.foundations.mapper.ServeMapper;
import com.jzo2o.foundations.model.dto.response.ServeResDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author agq
 * @date 2025-03-09
 * @description 服务Mapper测试类
 * @version 1.0
 */
//@SpringBootTest注解,用于测试类,表示该类是一个SpringBoot测试类
@SpringBootTest
//@slf4j注解,用于日志打印
@Slf4j
public class ServeMapperTest {

    //面试题：resource和autowired的区别
    //答：@Autowired是spring提供的注解，@Resource是J2EE提供的注解
    //Autowired是按照类型注入，Resource是按照名称注入
    //autowire也可以按照名称注入，需要@Qualifier注解
    //@Resource注解,用于依赖注入,表示该属性是一个资源
    @Resource
    private ServeMapper serveMapper;


    //@Test注解,用于测试方法
    @Test
    public void testQueryServeListByRegionId() {
        List<ServeResDTO> serveResDTOList = serveMapper.queryServeListByRegionId(1686303222843662337L);
        Assert.notEmpty(serveResDTOList, "查询结果为空");
    }
}
