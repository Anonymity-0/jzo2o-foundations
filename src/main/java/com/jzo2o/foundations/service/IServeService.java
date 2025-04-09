package com.jzo2o.foundations.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jzo2o.common.model.PageResult;
import com.jzo2o.foundations.model.domain.Serve;
import com.jzo2o.foundations.model.dto.request.ServePageQueryReqDTO;
import com.jzo2o.foundations.model.dto.request.ServeUpsertReqDTO;
import com.jzo2o.foundations.model.dto.response.ServeResDTO;

import java.math.BigDecimal;
import java.util.List;

public interface IServeService extends IService<Serve> {

    PageResult<ServeResDTO> page(ServePageQueryReqDTO servePageQueryReqDTO);

    /**
     * 批量添加
     * @param serveUpsertReqDTOList
     */
    void batchAdd(List<ServeUpsertReqDTO> serveUpsertReqDTOList);

    /**
     * 修改区域服务价格
     * @param id
     * @param price
     * @return
     */
    Serve update(Long id, BigDecimal price);

    /**
     * 上架
     * @param id
     * @return
     */
    Serve onSale(Long id);

    /**
     * 下架
     * @param id
     * @return
     */
    Serve offSale(Long id);

    /**
     * 删除区域服务
     * @param id
     * @return
     */
    void delete(Long id);

    /**
     * 设置区域服务热门
     * @param id
     * @return
     */
    Serve onHot(Long id);

    /**
     * 取消区域服务热门
     * @param id
     * @return
     */
    Serve offHot(Long id);


    /**
     * 根据区域id和状态查询服务数量
     * @param regionId
     * @param status
     * @return
     */
    Integer queryServeCountByRegionIdAndStatus(Long regionId, Integer status);
}

