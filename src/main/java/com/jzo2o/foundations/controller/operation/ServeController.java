package com.jzo2o.foundations.controller.operation;

import com.jzo2o.common.model.PageResult;
import com.jzo2o.foundations.model.domain.Serve;
import com.jzo2o.foundations.model.dto.request.ServePageQueryReqDTO;
import com.jzo2o.foundations.model.dto.response.ServeResDTO;
import com.jzo2o.foundations.service.IServeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 区域表 前端控制器
 * </p>
 * @since 2025/3/9
 */

//设置为RestController，设置id为operationServeController。
@RestController("operationServeController")
//设置请求路径为/operation/serve
@RequestMapping("/operation/serve")
//设置Api标签为运营端 - 区域服务管理相关接口
@Api(tags = "运营端 - 区域服务管理相关接口")
public class ServeController {

    @Resource
    private IServeService serveService;




    //GET/foundations/operation/serve/page
    @GetMapping("/page")
    //设置Api文档为服务分页查询
    @ApiOperation("服务分页查询")
    //返回ServeResDTO列表
    public PageResult<ServeResDTO> page(ServePageQueryReqDTO servePageQueryReqDTO) {
        return serveService.page(servePageQueryReqDTO);
    }
}
