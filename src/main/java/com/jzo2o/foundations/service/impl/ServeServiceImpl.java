package com.jzo2o.foundations.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jzo2o.common.expcetions.CommonException;
import com.jzo2o.common.expcetions.ForbiddenOperationException;
import com.jzo2o.common.model.PageResult;
import com.jzo2o.common.utils.BeanUtils;
import com.jzo2o.common.utils.ObjectUtils;
import com.jzo2o.foundations.enums.FoundationStatusEnum;
import com.jzo2o.foundations.mapper.RegionMapper;
import com.jzo2o.foundations.mapper.ServeItemMapper;
import com.jzo2o.foundations.mapper.ServeMapper;
import com.jzo2o.foundations.model.domain.Region;
import com.jzo2o.foundations.model.domain.Serve;
import com.jzo2o.foundations.model.domain.ServeItem;
import com.jzo2o.foundations.model.dto.request.ServePageQueryReqDTO;
import com.jzo2o.foundations.model.dto.request.ServeUpsertReqDTO;
import com.jzo2o.foundations.model.dto.response.ServeResDTO;
import com.jzo2o.foundations.service.IServeService;
import com.jzo2o.mysql.utils.PageHelperUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

//@Service注解,用于标注服务类,表示该类是一个服务类
@Service
public class ServeServiceImpl extends ServiceImpl<ServeMapper, Serve> implements IServeService {


    @Resource
    //注入ServeItemMapper,用于操作ServeItem表
    private ServeItemMapper ServeItemMapper;

    @Resource
    //注入RegionMapper,用于操作Region表
    private RegionMapper regionMapper;

    /**
     * 服务分页查询
     * @param servePageQueryReqDTO
     * @return
     */
    @Override
    public PageResult<ServeResDTO> page(ServePageQueryReqDTO servePageQueryReqDTO) {
       PageResult<ServeResDTO> serveResDTOPageResult = PageHelperUtils.selectPage(servePageQueryReqDTO,
               () -> this.baseMapper.queryServeListByRegionId(servePageQueryReqDTO.getRegionId()));
        return serveResDTOPageResult;
    }

    @Override
    public void batchAdd(List<ServeUpsertReqDTO> serveUpsertReqDTOList) {
        // 合法性校验
        for (ServeUpsertReqDTO serveUpsertReqDTO : serveUpsertReqDTOList) {
            // 1.校验serveitem是否启用，如果不启用，则不允许添加
            // 获取serveitem的id
            Long serveItemId = serveUpsertReqDTO.getServeItemId();
            // 根据serveItemId查询serveitem是否启用
            ServeItem serveItem = ServeItemMapper.selectById(serveItemId);
            // 如果serveitem为空或项目未启用，注意此处用枚举来判断
            if (ObjectUtils.isNull(serveItem)||serveItem.getActiveStatus()!= FoundationStatusEnum.ENABLE.getStatus()) {
                // 抛出异常，注意此处抛出的是ForbiddenOperationException
                throw new ForbiddenOperationException("服务项不存在或服务项未启动不允许添加");
            }
            // 2.检验添加的服务是否已经存在，同一个区域下，同一个服务项下的服务不允许重复添加
            // 根据serveItemId和regionId查询serve是否存在
            // 当前实现是和ServeMapper交互，所以可以直接用lambdaQuery,相当于new LambdaQueryWrapper<Serve>()
            // sql语句为select * from serve where serve_item_id = ？ and region_id = ？
                            //这里相当于serve_item_id = ？，Serve::getServeItemId是一个lambda表达式，表示Serve类的getServeItemId方法
            Integer count = lambdaQuery().eq(Serve::getServeItemId, serveItemId)
                            //region_id = ？,eq前面的是字段名，后面的是字段值
                            .eq(Serve::getRegionId, serveUpsertReqDTO.getRegionId())
                            //count()是查询符合条件的记录数
                            .count();
            // 如果count大于0，说明已经存在
            if (count > 0) {
                throw new ForbiddenOperationException("同一个区域下，同一个服务项下的服务不允许重复添加");
            }

            //3.组装并插入数据
            //将ServeUpsertReqDTO转换为Serve
            Serve serve = BeanUtils.toBean(serveUpsertReqDTO, Serve.class);

            // 获得区域id
            Long regionId = serve.getRegionId();
            // 根据区域id查询区域名称
            Region region = regionMapper.selectById(regionId);
            String cityCode = region.getCityCode();
            // 设置城市编码
            serve.setCityCode(cityCode);

            // 插入数据,为什么是baseMapper，因为ServeServiceImpl继承了ServiceImpl，ServiceImpl继承了IService，IService继承了BaseMapper
            baseMapper.insert(serve);
        }
    }

    @Override
    public Serve update(Long id, BigDecimal price) {
        boolean update = lambdaUpdate()
                .eq(Serve::getId, id)
                .set(Serve::getPrice, price)
                .update();
        if (!update) {
            // 不确定异常类型，抛出CommonException
            throw new CommonException("修改服务价格失败");
        }
        // 返回修改后的Serve
        Serve serve = baseMapper.selectById(id);
        return serve;
    }

    @Override
    public Serve onSale(Long id) {

        // 根据id查询Serve
        Serve serve = baseMapper.selectById(id);
        if (ObjectUtils.isNull(serve)) {
            throw new ForbiddenOperationException("区域服务信息不存在");
        }
        // 如果服务的sale_status为0或1，可以上架
        Integer saleStatus = serve.getSaleStatus();
        if (!(saleStatus==FoundationStatusEnum.INIT.getStatus()||saleStatus==FoundationStatusEnum.DISABLE.getStatus())) {
            throw new ForbiddenOperationException("区域服务的状态是草稿或者已下架状态才能上架");
        }
        // 如果服务项没启用，不能上架
        Long serveItemId = serve.getServeItemId();
        ServeItem serveItem = ServeItemMapper.selectById(serveItemId);
        Integer activeStatus = serveItem.getActiveStatus();
        if (activeStatus!=FoundationStatusEnum.ENABLE.getStatus()) {
            throw new ForbiddenOperationException("服务项未启用，不允许上架");
        }

        //更新服务的sale_status为2
        boolean update = lambdaUpdate()
                .eq(Serve::getId, id)
                .set(Serve::getSaleStatus, FoundationStatusEnum.ENABLE.getStatus())
                .update();
        if (!update) {
            throw new CommonException("服务上架失败");
        }
        return baseMapper.selectById(id);
    }

    @Override
    public Serve offSale(Long id) {
        // 根据id查询Serve
        Serve serve = baseMapper.selectById(id);
        if (ObjectUtils.isNull(serve)) {
            throw new ForbiddenOperationException("区域服务信息不存在");
        }
        // 如果服务的sale_status为上架，可以下架
        Integer saleStatus = serve.getSaleStatus();
        if (saleStatus!=FoundationStatusEnum.ENABLE.getStatus()) {
            throw new ForbiddenOperationException("区域服务的状态是已上架状态才能下架");
        }
        // 更新服务的sale_status为下架
        boolean update = lambdaUpdate()
                .eq(Serve::getId, id)
                .set(Serve::getSaleStatus, FoundationStatusEnum.DISABLE.getStatus())
                .update();
        if (!update) {
            throw new CommonException("服务下架失败");
        }
        return baseMapper.selectById(id);
    }

    @Override
    public void delete(Long id) {
        // 根据id查询Serve
        Serve serve = baseMapper.selectById(id);
        if (ObjectUtils.isNull(serve)) {
            throw new ForbiddenOperationException("区域服务信息不存在");
        }
        // 当前状态如果为草稿才能删除
        Integer saleStatus = serve.getSaleStatus();
        if(saleStatus!=FoundationStatusEnum.INIT.getStatus()){
            throw new ForbiddenOperationException("区域服务的状态是草稿才能删除");
        }
        // 删除
        baseMapper.deleteById(id);
    }

    @Override
    public Serve onHot(Long id) {
        // 根据id查询Serve
        Serve serve = baseMapper.selectById(id);
        if (ObjectUtils.isNull(serve)) {
            throw new ForbiddenOperationException("区域服务信息不存在");
        }
        // 如果当前服务为热门，不允许设置为热门
        Integer hotStatus = serve.getIsHot();
        if (hotStatus==FoundationStatusEnum.HOT.getStatus()) {
            throw new ForbiddenOperationException("区域服务已经是热门状态");
        }
        // 更新服务的hot_status为热门
        serve.setIsHot(FoundationStatusEnum.HOT.getStatus());
        serve.setHotTimeStamp(System.currentTimeMillis());
        boolean update = updateById(serve);
        if (!update) {
            throw new CommonException("服务设置热门失败");
        }
        return baseMapper.selectById(id);
    }

    @Override
    public Serve offHot(Long id) {
        // 根据id查询Serve
        Serve serve = baseMapper.selectById(id);
        if (ObjectUtils.isNull(serve)) {
            throw new ForbiddenOperationException("区域服务信息不存在");
        }
        // 如果服务的不是热门，不允许取消热门
        Integer hotStatus = serve.getIsHot();
        if (hotStatus==FoundationStatusEnum.UNHOT.getStatus()) {
            throw new ForbiddenOperationException("区域服务不是热门状态");
        }
        // 更新服务的hot_status为非热门
        serve.setIsHot(FoundationStatusEnum.UNHOT.getStatus());
        serve.setHotTimeStamp(null);
        boolean update = updateById(serve);
        if (!update) {
            throw new CommonException("服务取消热门失败");
        }

        return baseMapper.selectById(id);
    }

    @Override
    public Integer queryServeCountByRegionIdAndStatus(Long regionId, Integer status) {
        Integer count = lambdaQuery().eq(Serve::getRegionId, regionId)
                .eq(Serve::getSaleStatus, status)
                .count();
        return count;
    }


}
