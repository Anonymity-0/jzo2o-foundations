package com.jzo2o.foundations.controller.operation;

import com.jzo2o.common.model.PageResult;
import com.jzo2o.foundations.model.domain.Serve;
import com.jzo2o.foundations.model.dto.request.ServePageQueryReqDTO;
import com.jzo2o.foundations.model.dto.request.ServeUpsertReqDTO;
import com.jzo2o.foundations.model.dto.response.ServeResDTO;
import com.jzo2o.foundations.service.IServeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
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


    @PostMapping("/batch")
    @ApiOperation("添加区域服务")
    //注意这里一定要有@RequestBody注解，否则会报错，因为传入的是一个json对象
    public void add(@RequestBody List<ServeUpsertReqDTO> serveUpsertReqDTOList) {
        serveService.batchAdd(serveUpsertReqDTOList);
    }

    @PutMapping("/{id}")
    @ApiOperation("修改区域服务价格")
    //设置Api文档为修改区域服务价格
    @ApiImplicitParams( {
        @ApiImplicitParam(name = "id", value = "区域服务id", required = true, dataTypeClass = Long.class),
        @ApiImplicitParam(name = "price", value = "价格", required = true, dataTypeClass = BigDecimal.class)
    })
    // @PathVariable注解，用于获取路径中的参数,将id参数传入update方法
    public void update(@PathVariable("id") Long id, @RequestParam("price") BigDecimal price) {
        serveService.update(id, price);
    }

    @PutMapping("/onSale/{id}")
    @ApiOperation("区域服务上架")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "区域服务id", required = true, dataTypeClass = Long.class)
    })
    public void onSale(@PathVariable("id") Long id) {
        serveService.onSale(id);
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除区域服务")
    @ApiImplicitParams({
                    @ApiImplicitParam(name = "id", value = "区域服务id", required = true, dataTypeClass = Long.class)
            })
    public void delete(@PathVariable("id") Long id) {
        serveService.delete(id);
    }

    @PutMapping("/offSale/{id}")
    @ApiOperation("区域服务下架")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "区域服务id", required = true, dataTypeClass = Long.class)
    })
    public void offSale(@PathVariable("id") Long id) {
        serveService.offSale(id);
    }

    @PutMapping("/onHot/{id}")
    @ApiOperation("区域服务设置为热门")
    @ApiImplicitParams(
            @ApiImplicitParam(name = "id", value = "区域服务id", required = true, dataTypeClass = Long.class)
    )
    public void onHot(@PathVariable("id") Long id) {
        serveService.onHot(id);
    }

    @PutMapping("/offHot/{id}")
    @ApiOperation("区域服务取消热门")
    @ApiImplicitParams(
            @ApiImplicitParam(name = "id", value = "区域服务id", required = true, dataTypeClass = Long.class)
    )
    public void offHot(@PathVariable("id") Long id) {
        serveService.offHot(id);
    }


}
