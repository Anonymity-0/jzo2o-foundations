package com.jzo2o.foundations.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jzo2o.common.model.PageResult;
import com.jzo2o.foundations.mapper.ServeMapper;
import com.jzo2o.foundations.model.domain.Serve;
import com.jzo2o.foundations.model.dto.request.ServePageQueryReqDTO;
import com.jzo2o.foundations.model.dto.response.ServeResDTO;
import com.jzo2o.foundations.service.IServeService;
import com.jzo2o.mysql.utils.PageHelperUtils;
import org.springframework.stereotype.Service;

import java.util.List;

//@Service注解,用于标注服务类,表示该类是一个服务类
@Service
public class ServeServiceImpl extends ServiceImpl<ServeMapper, Serve> implements IServeService {

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
}
