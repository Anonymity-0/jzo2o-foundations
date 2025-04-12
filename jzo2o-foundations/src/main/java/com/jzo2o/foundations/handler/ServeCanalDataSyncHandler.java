package com.jzo2o.foundations.handler;
import com.jzo2o.canal.listeners.AbstractCanalRabbitMqMsgListener;
import com.jzo2o.es.core.ElasticSearchTemplate;
import com.jzo2o.es.core.impl.ElasticSearchTemplateImpl;
import com.jzo2o.foundations.constants.IndexConstants;
import com.jzo2o.foundations.model.domain.ServeSync;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
@Slf4j
public class ServeCanalDataSyncHandler extends AbstractCanalRabbitMqMsgListener<ServeSync> {


    //这个ElasticSearchTemplate
    @Resource
    private ElasticSearchTemplate elasticSearchTemplate;


    //监听mq
    // 这些注解的详细意思是
    // @RabbitListener：监听队列，bindings：绑定队列的交换机和路由键
    // @QueueBinding：value：队列，exchange：交换机，key：路由键
    // @Queue：队列名称，durable：是否持久化，autoDelete：是否自动删除
    // @Exchange：交换机名称，type：交换机类型
    // concurrency：消费线程数,设置为1，表示单线程消费，防止消息重复消费

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "canal-mq-jzo2o-foundations",arguments = {@Argument(name = "x-single-active-consumer",value = "true")}),
            exchange = @Exchange(name = "exchange.canal-jzo2o", type = ExchangeTypes.TOPIC),
            key = "canal-mq-jzo2o-foundations"
    ),concurrency = "1")
    public void onMessage(Message message) throws Exception{
        //调用抽象类中的parseMsg方法，解析消息
        parseMsg(message);
    }



    /**
     * 向es中保存数据，解析到binlog中的新增，更新消息执行此方法
     *
     * @param data
     */
    @Override
    public void batchSave(List<ServeSync> data) {

        //向es中添加索引
        Boolean serve_aggregation = elasticSearchTemplate.opsForDoc().batchInsert(IndexConstants.SERVE, data);
        // 如果执行失败，抛出异常，给mq发送回滚消息nack
        if(!serve_aggregation){
            try {
                Thread.sleep(1000);
            }catch (InterruptedException e){
                throw new RuntimeException(e);
            }
            throw new RuntimeException("同步失败");
        }

    }

    /**
     * 解析到binlog中的delete消息向es中文档进行删除
     *
     * @param ids
     */
    @Override
    public void batchDelete(List ids) {
        Boolean serveAggregation = elasticSearchTemplate.opsForDoc().batchDelete(IndexConstants.SERVE, ids);
        if(!serveAggregation){
            try {
                Thread.sleep(1000);
            }catch (InterruptedException e){
                throw new RuntimeException(e);
            }
            throw new RuntimeException("同步失败");
        }

    }
}
