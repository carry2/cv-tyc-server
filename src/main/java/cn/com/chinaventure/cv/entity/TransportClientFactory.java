package cn.com.chinaventure.cv.entity;

import java.net.InetAddress;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jfinal.kit.PropKit;

/**
 * Created by YZP on 2018/5/15.
 * 用来生成Elasticsearch的Client
 * 
 * Updated by Mark.zhang 2018/5/17
 * 修改为单例，减少关闭连接带来的时间耗损
 *
 */
public  class TransportClientFactory {
	
	private static final Logger log = LoggerFactory.getLogger(TransportClientFactory.class);
	
	private TransportClientFactory(){}
	
    private static volatile TransportClient transportClient;

    private static TransportClient initClient(){

        Settings esSettings = Settings.builder()
                // 设置ES实例的名称
                .put("cluster.name",PropKit.get("es.lawsuit.clustername"))
                // 自动嗅探整个集群的状态，把集群中其他ES节点的ip添加到本地的客户端列表中
                .put("client.transport.sniff", true)
                .build();
        // 初始化client较老版本发生了变化，此方法有几个重载方法，初始化插件等。
        try {
        	transportClient = new PreBuiltTransportClient(esSettings);
            // 此步骤添加IP，至少一个，其实一个就够了，因为添加了自动嗅探配置
        	//5.6.8写法
        	transportClient.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName( PropKit.get("es.lawsuit.server.node0.ip")), PropKit.getInt("es.lawsuit.server.port")))
            .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName( PropKit.get("es.lawsuit.server.node1.ip")), PropKit.getInt("es.lawsuit.server.port")))
                    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName( PropKit.get("es.lawsuit.server.node2.ip")), PropKit.getInt("es.lawsuit.server.port")));
        	// 6.*写法
//          transportClient.addTransportAddress(new TransportAddress(new InetSocketAddress(servIP,port)));
            log.info("elasticsearch transportClient 连接成功");
        } catch (Exception e) {
            e.printStackTrace();
            log.info("elasticsearch transportClient 连接失败{}", e.getMessage());

        }

        return transportClient;
    }
    
    private synchronized static TransportClient initInstance(){
    	if(transportClient == null){
    		transportClient = initClient();
    	}
    	return transportClient;
    }
    
    
    /**
     * 获取法律信息的es地址
     * @return
     */
    public static TransportClient getLawsuitInstance(){
    	if(transportClient == null){
    		transportClient = initInstance();
    	}
    	return transportClient;
    }

}
