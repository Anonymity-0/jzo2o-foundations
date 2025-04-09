package com.jzo2o.foundations.service.impl;

import com.jzo2o.common.utils.CollUtils;
import com.jzo2o.common.utils.ObjectUtils;
import com.jzo2o.foundations.constants.RedisConstants;
import com.jzo2o.foundations.enums.FoundationStatusEnum;
import com.jzo2o.foundations.mapper.ServeMapper;
import com.jzo2o.foundations.model.domain.Region;
import com.jzo2o.foundations.model.domain.Serve;
import com.jzo2o.foundations.model.dto.response.ServeCategoryResDTO;
import com.jzo2o.foundations.model.dto.response.ServeSimpleResDTO;
import com.jzo2o.foundations.service.HomeService;
import com.jzo2o.foundations.service.IRegionService;
import com.jzo2o.foundations.service.IServeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Mr.M
 * @version 1.0
 * @description 门户查询接口
 * @date 2023/12/10 16:00
 */
@Service
@Slf4j
public class HomeServiceImpl implements HomeService {
    @Resource
    private ServeMapper serveMapper;

    @Resource
    private IRegionService regionService;


    @Caching(
            cacheable = {
                    //result为null时,属于缓存穿透情况，缓存时间30分钟
                    @Cacheable(value = RedisConstants.CacheName.SERVE_ICON, key = "#regionId", unless = "#result.size() != 0", cacheManager = RedisConstants.CacheManager.THIRTY_MINUTES),
                    //result不为null时,永久缓存
                    @Cacheable(value = RedisConstants.CacheName.SERVE_ICON, key = "#regionId", unless = "#result.size() == 0", cacheManager = RedisConstants.CacheManager.FOREVER)
            }
    )
    @Override
    public List<ServeCategoryResDTO> queryServeIconCategoryByRegionIdCache(Long regionId) {

        //查询区域
        Region region = regionService.getById(regionId);
        //如果区域没有启动，直接返回空
        if(ObjectUtils.isNull(region) || region.getActiveStatus()!= FoundationStatusEnum.ENABLE.getStatus()){
            return Collections.emptyList();
        }
        //查询首页服务列表
        List<ServeCategoryResDTO> list = serveMapper.findServeIconCategoryByRegionId(regionId);
        if(CollUtils.isEmpty(list)){
            return Collections.emptyList();
        }

        //对查询到的数据进行处理
        int endIndex = list.size() >= 2 ? 2 : list.size();
        //最多包括两个服务类型
        List<ServeCategoryResDTO> serveCategoryResDTOS = new ArrayList<>(list.subList(0, endIndex));
        serveCategoryResDTOS.forEach(item->{
            List<ServeSimpleResDTO> serveResDTOList = item.getServeResDTOList();//服务项
            int endIndex2 = serveResDTOList.size() >= 4 ? 4 : serveResDTOList.size();
            //取出最多4个服务项
            List<ServeSimpleResDTO> serveSimpleResDTOS = new ArrayList<>(serveResDTOList.subList(0, endIndex2));
            item.setServeResDTOList(serveSimpleResDTOS);
        });


        return serveCategoryResDTOS;
    }
}
