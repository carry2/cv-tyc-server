package cn.com.chinaventure.cv.common.thread;


import cn.com.chinaventure.cv.common.util.DbUtil;
import cn.com.chinaventure.cv.common.util.RedisUtil;
import com.jfinal.kit.PropKit;
import com.jfplugin.mail.MailKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 新创建线程，用于发送消息，并对应操作
 * @author YZP
 *
 */
public class MesThread extends  Thread{
    private static final Logger log = LoggerFactory.getLogger(Thread.class);
    private List<String> tables;
    private static final int  batchSize=100;
    private static final int  sleepMillis=100000;
    private static  int  circleSize=0;
    private static  int  maxCircleSize=10;
    private Jedis rd1 = RedisUtil.getJedis();
    @Override
    public void run() {
        while(true){
            //获取redis
            //新建数组 存放处理完的表下标
            List<Integer> tbs=new ArrayList<>();
            List<String> tbs2=new ArrayList<>();
            //开始处理表数据
            for(int i=0;i<tables.size();i++){
                System.out.println(tables.get(i).toString());
                String tableName = tables.get(i).toString();
                //定义两个key作为存储和暂存的两个list的key
                String key1 = tableName + "1";
                String key2 = tableName + "2";


                //判断当前是否有未处理数据 有则说明系统断电或者宕机了 继续处理剩余数据
                if(rd1.llen(key2)>0||rd1.llen(key1)>0){
                    try{
                        pushMessages(rd1,key1,key2,batchSize);
                    }catch (Exception e){
                        log.error("表名："+tableName+" 发送消息错误："+e.toString());
                        break;
                    }
                }
                try{  //-TODO  查询Hive数据
                DbUtil util = new DbUtil();
                List<Map<Object, Object>> list = util.query("select cv_id from  "+tableName+" limit 10");

                //将所有结果存到redis里
                for(int k=0;k<list.size();k++){
                    rd1.lpush(key1,list.get(k).get("cv_id").toString());
                }

                //从redis批量取出 存到另一个list里面  开始发送消息 递归操作 只到发完为止

                    pushMessages(rd1,key1,key2,batchSize);
                }catch (Exception e){
                    log.error("表名："+tableName+" 发送消息错误："+e.toString());
                    break;
                }

                //如果处理完毕则 从数组中去除掉
                if(rd1.llen(key2)==0 && rd1.llen(key1)==0){
                    tbs2.add(tableName);
                    tbs.add(i);
                }
            }
            //处理完一轮以后  去除掉处理完的表  判断是否全部处理完毕
            for (int tb:tbs) {
                tables.remove(tb);
            }
            if(tbs2.size()>0){
                //发送邮件 告知处理完成的表
                MailKit.send(PropKit.get("SEND_TO_MQ").toString(),null, "处理完成表",tbs2.toString());
            }

            //如果剩余表数量大于零 说明 表未生成 或者有异常发生 则线程沉睡接着处理
            if(tables.size()>0){
                try {
                    //这里做一个统计 如果处理次数超过十个轮次 则停止处理 发送报告邮件
                    circleSize++;
                    if(circleSize>maxCircleSize){
                        MailKit.send(PropKit.get("SEND_TO_MQ").toString(),null, "未处理表",tables.toString());
                        break;
                    }
                    Thread.sleep(sleepMillis);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }

            }

            //如果剩余表数量等于零 说明 处理完毕 跳出循环
            if(tables.size()==0)
            {
                MailKit.send(PropKit.get("SEND_TO_MQ").toString(),null, "处理完成","所有表处理完成");
                break;
            }

        }

    }







    /**
     *从redis批量取出 存到另一个list里面  开始发送消息 递归操作 只到发完为止
     * @param key1 存储所有数据
     * @param key2 存储批量数据 作为备灾策略 保证数据不丢失
     * @param batchSize 批量大小
     */
    public static void pushMessages(Jedis jedis, String key1, String key2, long batchSize){

        //判断key2是否为空 不为空说明断电或者宕机 未处理完 要接着处理
        if(jedis.llen(key2)>0){
            List<String> lrange = jedis.lrange(key2, 0, jedis.llen(key2));
            //将剩余的未发出列表继续发送
            System.out.println("消费消息"+lrange.toString());
            //消费完消息 将key2list 清空
            jedis.del(key2);
        }


        if(jedis.llen(key1)>0){
            List<String> Strs=new ArrayList<>();
            if(jedis.llen(key1)<batchSize){
                batchSize=jedis.llen(key1);
            }
            for(int i=0;i<batchSize;i++){
                String rpoplpush = jedis.rpoplpush(key1, key2);
                Strs.add(rpoplpush);
            }
            //TODDO  发送消息
            System.out.println("消费消息"+Strs.toString());
            //消费完消息 将key2list 清空
            jedis.del(key2);
            //接着递归 直到消费完key1list所有元素
            pushMessages(jedis,key1,key2,batchSize);
        }


    }


    public List<String> getTables() {
        return tables;
    }

    public void setTables(List<String>  tables) {
        this.tables = tables;
    }

}

