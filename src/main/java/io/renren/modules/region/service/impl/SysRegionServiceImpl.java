package io.renren.modules.region.service.impl;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import io.renren.modules.region.utils.GovRegionSpiderPipeline;
import io.renren.modules.region.utils.GovRegionSpiderScheduler;
import io.renren.modules.region.utils.GovRegionSpiderUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.common.utils.PageUtils;
import io.renren.common.utils.Query;

import io.renren.modules.region.dao.SysRegionDao;
import io.renren.modules.region.entity.SysRegionEntity;
import io.renren.modules.region.service.SysRegionService;
import redis.clients.jedis.JedisPool;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.monitor.SpiderMonitor;
import us.codecraft.webmagic.scheduler.DuplicateRemovedScheduler;
import us.codecraft.webmagic.scheduler.RedisPriorityScheduler;


@Service("sysRegionService")
public class SysRegionServiceImpl extends ServiceImpl<SysRegionDao, SysRegionEntity> implements SysRegionService {

    @Autowired
    private GovRegionSpiderUtils govRegionSpiderUtils;
    @Autowired
    private GovRegionSpiderPipeline govRegionSpiderPipeline;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SysRegionEntity> page = this.page(
                new Query<SysRegionEntity>().getPage(params),
                new QueryWrapper<SysRegionEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void updateRegion() {
        Spider spider = Spider.create(govRegionSpiderUtils)
                //从"https://github.com/code4craft"开始抓
                .addUrl("http://www.stats.gov.cn/tjsj/tjbz/tjyqhdmhcxhfdm/2020/")
//                .addUrl("https://github.com/code4craft")
                //.addUrl("http://www.stats.gov.cn/tjsj/tjbz/tjyqhdmhcxhfdm/2019/45/09/450981.html")
                .addPipeline(govRegionSpiderPipeline)
                //开启5个线程抓取
                .thread(20);
                //启动爬虫
        spider.setExitWhenComplete(true);
        spider.setScheduler(new GovRegionSpiderScheduler("127.0.0.1"));
        spider.run();
    }

    @Override
    public void updateData() {
        QueryWrapper<SysRegionEntity> wrapper = new QueryWrapper<>();
        wrapper.lambda().lt(true,SysRegionEntity::getRegionType,4);
        List<SysRegionEntity> sysRegionEntities = baseMapper.selectList(wrapper);

        DateTime date = DateUtil.date();
        List<Map<String,Object>> list = new ArrayList<>();
        List<Map<String,Object>> list2 = new ArrayList<>();
        List<String> sql = new ArrayList<>();
        for(SysRegionEntity entity:sysRegionEntities){
            Map<String,Object> params = new HashMap<>();
            params.put("id",entity.getRegionCode().substring(0,6));
//            params.put("pid",entity.getParentCode().substring(5));
//            params.put("name",entity.getRegionName());
            List<Map<String, Object>> result = baseMapper.getOldRegion(params);
            if(CollUtil.isNotEmpty(result)){
                continue;
            }else {
                params.remove("id");
                params.put("pid",entity.getParentCode() == null? "0":entity.getParentCode()
                        .substring(0,6));
                params.put("name",entity.getRegionName());
                result = baseMapper.getOldRegion(params);
                if(CollUtil.isNotEmpty(result)){
                    String updateStr = "UPDATE `sys_region` SET region_id_new = '"+entity.getRegionCode().substring(0,6)+"' " +
                            "WHERE `name` = '"+entity.getRegionName()+"' and pid = '"+entity.getParentCode().substring(0,6)+"';";
                    list2.add(params);
                    sql.add(updateStr);
                    continue;
                }
                String insertStr = "INSERT INTO `sys_region`(`id`, `pid`, `name`," +
                        " `tree_level`, `leaf`, `sort`, `creator`, `create_date`," +
                        " `updater`, `update_date`) VALUES ('"+ entity.getRegionCode().substring(0,6) +"'," +
                        " '"+(entity.getParentCode() == null? "0" :entity.getParentCode().substring(0,6))+"', '"+entity.getRegionName()+"'," +
                        " "+entity.getRegionType()+", "+(entity.getRegionType().compareTo(3) == 0?1:0)+"," +
                        " '"+ entity.getRegionCode().substring(0,6) +"', 1067246875800000001, '"+date+"', 1067246875800000001, '"+date+"');";
                list.add(params);
                sql.add(insertStr);
            }
        }
        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        System.out.println(list);
        System.out.println(list2);
        System.out.println(list.size());
        System.out.println(list2.size());
        sql.stream().forEach(e -> System.out.println(e));
        System.out.println(sql.size());
        System.out.println(date);
    }
}