package com.jzo2o.foundations.service.impl;

import com.jzo2o.common.utils.ObjectUtils;
import com.jzo2o.foundations.constants.RedisConstants;
import com.jzo2o.foundations.enums.FoundationStatusEnum;
import com.jzo2o.foundations.mapper.ServeMapper;
import com.jzo2o.foundations.model.domain.Region;
import com.jzo2o.foundations.model.dto.response.ServeCategoryResDTO;
import com.jzo2o.foundations.model.dto.response.ServeSimpleResDTO;
import com.jzo2o.foundations.service.IHomeService;
import com.jzo2o.foundations.service.IRegionService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class IHomeServiceImpl implements IHomeService {
    @Resource
    ServeMapper serveMapper;
    @Resource
    private IRegionService regionService;

    @Caching(cacheable = {
            //result.size()==0时缓存时间短一些
            @Cacheable(value = RedisConstants.CacheName.SERVE_ICON, key = "#regionId", cacheManager = RedisConstants.CacheManager.ONE_DAY,unless = "#result.size()==0"),
            //result.size()!=0时缓存时间不过期
            @Cacheable(value = RedisConstants.CacheName.SERVE_ICON, key = "#regionId", cacheManager = RedisConstants.CacheManager.THIRTY_MINUTES,unless = "#result.size()!=0")
    })
    @Override
    public List<ServeCategoryResDTO> queryServeIconCategoryByRegionIdCache(Long regionId) {

        //查询区域
        Region region = regionService.getById(regionId);

        // 如果区域没有启用，直接返回空列表
        if(ObjectUtils.isEmpty(region)||region.getActiveStatus()!= FoundationStatusEnum.ENABLE.getStatus()) {
            return Collections.emptyList();
        }

        List<ServeCategoryResDTO> list = serveMapper.findServeIconCategoryByRegionId(regionId);
        if(ObjectUtils.isEmpty(list)){
            return Collections.emptyList();
        }

        // 对查询到的数据进行处理
        // 最多取出前两个服务类型
        int endIndex = list.size() >=2 ? 2:list.size();
        List<ServeCategoryResDTO> resDTOList = new ArrayList<>(list.subList(0, endIndex));
        resDTOList.forEach(item -> {
            List<ServeSimpleResDTO> serveResDTOList = item.getServeResDTOList();
            // 取出前4个服务项
            int endIndex1 = serveResDTOList.size() >= 4 ? 4 : serveResDTOList.size();
            List<ServeSimpleResDTO> serveSimpleResDTOS = new ArrayList<>(serveResDTOList.subList(0, endIndex1));
            item.setServeResDTOList(serveSimpleResDTOS);
        });


        return resDTOList;

    }
}
