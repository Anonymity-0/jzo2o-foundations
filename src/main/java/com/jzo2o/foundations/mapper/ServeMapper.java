package com.jzo2o.foundations.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jzo2o.api.foundations.dto.response.ServeAggregationResDTO;
import com.jzo2o.foundations.model.domain.Serve;
import com.jzo2o.foundations.model.dto.response.ServeAggregationSimpleResDTO;
import com.jzo2o.foundations.model.dto.response.ServeAggregationTypeSimpleResDTO;
import com.jzo2o.foundations.model.dto.response.ServeCategoryResDTO;
import com.jzo2o.foundations.model.dto.response.ServeResDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 * 服务表 Mapper 接口
 * @author aga
 * @since 2023-07-03
 */
public interface ServeMapper extends BaseMapper<Serve> {
    /**
     * 区域服务查询
     * @param regionId 区域id
     * @return
     */

    // 根据区域id查询服务列表,返回ServeResDTO列表,参数@Param是为了在xml中引用
    List<ServeResDTO>queryServeListByRegionId(@Param("regionId") Long regionId);
}
