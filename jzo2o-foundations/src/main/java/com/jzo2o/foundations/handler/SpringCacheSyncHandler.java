package com.jzo2o.foundations.handler;

import com.jzo2o.api.foundations.dto.response.RegionSimpleResDTO;
import com.jzo2o.foundations.constants.RedisConstants;
import com.jzo2o.foundations.service.HomeService;
import com.jzo2o.foundations.service.IRegionService;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Mr.M
 * @version 1.0
 * @description TODO
 * @date 2023/12/10 15:01
 */
@Component
@Slf4j
public class SpringCacheSyncHandler {

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private IRegionService regionService;

    @Resource
    private HomeService homeService;

    //定时更新缓存
    @XxlJob("activeRegionCacheSync")//指定任务名称
    public void activeRegionCacheSync() {
     log.info(">>>>>>>>开始进行缓存同步，更新已启用区域");
        //删除原来的缓存
        //key
        String key = RedisConstants.CacheName.JZ_CACHE+"::ACTIVE_REGIONS";
        //删除缓存
        redisTemplate.delete(key);

       //添加新缓存,查询到所有开通的区域
        List<RegionSimpleResDTO> regionSimpleResDTOS = regionService.queryActiveRegionListCache();

        //遍历区域,对每个区域的首页服务列表进行删除缓存再添加缓存
        regionSimpleResDTOS.forEach(item->{
            //key
            String key1 = RedisConstants.CacheName.SERVE_ICON+"::"+item.getId();
            //删除缓存
            redisTemplate.delete(key1);
            //调用首页服务列表的查询方法去添加缓存
            homeService.queryServeIconCategoryByRegionIdCache(item.getId());
        });

        log.info(">>>>>>>>更新已启用区域完成");

    }


}
