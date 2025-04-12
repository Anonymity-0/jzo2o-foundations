package com.jzo2o.foundations.service.impl;

import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.jzo2o.common.utils.BeanUtils;
import com.jzo2o.common.utils.ObjectUtils;
import com.jzo2o.es.core.impl.ElasticSearchTemplateImpl;
import com.jzo2o.es.utils.SearchResponseUtils;
import com.jzo2o.foundations.model.domain.ServeAggregation;
import com.jzo2o.foundations.model.dto.response.ServeAggregationSimpleResDTO;
import com.jzo2o.foundations.model.dto.response.ServeSimpleResDTO;
import com.jzo2o.foundations.service.IServeAggregationService;
import org.springframework.stereotype.Service;


import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class ServeAggregationServiceImpl implements IServeAggregationService {

    @Resource
    private ElasticSearchTemplateImpl elasticSearchTemplate;


    @Override
    public List<ServeSimpleResDTO> findServeList(String cityCode, Long serveTypeId, String keyword) {
        SearchRequest.Builder builder = new SearchRequest.Builder();
        // 拼装查询条件
        builder.query(query->query.bool(bool->{
                 bool.must(must->
                        must.term(term->term.field("cityCode").value(cityCode)));

                 //todo 根据服务类型查询
                if(ObjectUtils.isNotEmpty(serveTypeId)){
                    bool.must(must->
                            must.term(term->term.field("serveTypeId").value(serveTypeId)));
                }

                //根据keyword查询
                if(ObjectUtils.isNotEmpty(keyword)){
                    bool.must(must->
                            must.multiMatch(multiMatch->
                                    multiMatch.query(keyword).fields("serveItemName","serveTypeName")));
                }

                return bool;

                }));


        //添加排序
        List< SortOptions> sortOptionsList =new ArrayList<>();
        sortOptionsList.add(SortOptions.of(sortOptions->sortOptions.field(field->field.field("serveTypeSortNum").order(SortOrder.Asc))));
        builder.sort(sortOptionsList);
        //制定要搜索的索引
        builder.index("serve_aggregation");
        //生成searchRequest
        SearchRequest searchRequest = builder.build();

        //请求es搜索
        SearchResponse<ServeAggregation> search = elasticSearchTemplate.opsForDoc().search(searchRequest, ServeAggregation.class);

        if (SearchResponseUtils.isSuccess(search)) {
            List<ServeAggregation> collect = search.hits().hits().stream().map(item -> {
                ServeAggregation source = item.source();
                return source;
            }).collect(Collectors.toList());

            //将collect转list<ServeSimpleResDTO>
            List<ServeSimpleResDTO> serveSimpleResDTOS = BeanUtils.copyToList(collect, ServeSimpleResDTO.class);

            return serveSimpleResDTOS;
        }
        // 找不到返回空
        return Collections.emptyList();
    }
}
