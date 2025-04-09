package com.jzo2o.foundations.service;

import com.jzo2o.foundations.model.dto.response.ServeCategoryResDTO;

import java.util.List;

/**
 * @author Mr.M
 * @version 1.0
 * @description 门户查询接口
 * @date 2023/12/10 15:59
 */
public interface HomeService {

    /**
     * 根据区域id获取服务图标信息
     *
     * @param regionId 区域id
     * @return 服务图标列表
     */
    List<ServeCategoryResDTO> queryServeIconCategoryByRegionIdCache(Long regionId);
}
