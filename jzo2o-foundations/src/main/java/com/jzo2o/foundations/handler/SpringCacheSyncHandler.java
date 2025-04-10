package com.jzo2o.foundations.handler;


import com.jzo2o.api.foundations.dto.response.RegionSimpleResDTO;
import com.jzo2o.foundations.constants.RedisConstants;
import com.jzo2o.foundations.service.IHomeService;
import com.jzo2o.foundations.service.IRegionService;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
@Slf4j
public class SpringCacheSyncHandler {

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private IRegionService regionService;

    @Resource
    private IHomeService homeService;
    //定时更新缓存
    @XxlJob("activeRegionCacheSync")
    public  void  activeRegionCacheSync(){
        log.info(">>>>>开始进行缓存同步，更新已开通服务区域列表缓存<<<<<");


        //删除原来的缓存
        //得到已开通服务区域列表缓存的key
        String key = RedisConstants.CacheName.JZ_CACHE + "::ACTIVE_REGIONS";
        //删除缓存
        redisTemplate.delete(key);
        log.info(">>>>>已开通服务区域列表缓存删除成功<<<<<");


        //添加新缓存
        List<RegionSimpleResDTO> regionSimpleResDTOS = regionService.queryActiveRegionList();

        //遍历区域，对每个区域都首页列表进行删除缓存再添加缓存
        regionSimpleResDTOS.forEach(item -> {
            //key
            String key1 = RedisConstants.CacheName.SERVE_ICON + "::" + item.getId();
            //删除缓存
            redisTemplate.delete(key1);
            log.info(">>>>>首页服务图标缓存删除成功<<<<<");
            //调用首页服务图标查询接口，添加缓存
            homeService.queryServeIconCategoryByRegionIdCache(item.getId());
        });


        log.info(">>>>>缓存同步完成<<<<<");
    }
}
