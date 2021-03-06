package io.renren.modules.region.service.impl;


import io.renren.modules.region.utils.GovRegionSpiderPipeline;
import io.renren.modules.region.utils.GovRegionSpiderUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.common.utils.PageUtils;
import io.renren.common.utils.Query;

import io.renren.modules.region.dao.SysRegionDao;
import io.renren.modules.region.entity.SysRegionEntity;
import io.renren.modules.region.service.SysRegionService;
import us.codecraft.webmagic.Spider;


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
        Spider.create(govRegionSpiderUtils)
                //从"https://github.com/code4craft"开始抓
                .addUrl("http://www.stats.gov.cn/tjsj/tjbz/tjyqhdmhcxhfdm/2019/")
//                .addUrl("https://github.com/code4craft")
                //.addUrl("http://www.stats.gov.cn/tjsj/tjbz/tjyqhdmhcxhfdm/2019/45/09/450981.html")
                .addPipeline(govRegionSpiderPipeline)
                //开启5个线程抓取
                .thread(5)
                //启动爬虫
                .run();
    }

}