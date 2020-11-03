package io.renren.modules.region.service.impl;

import io.renren.modules.region.utils.RegionUtils;
import org.springframework.stereotype.Service;

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


@Service("sysRegionService")
public class SysRegionServiceImpl extends ServiceImpl<SysRegionDao, SysRegionEntity> implements SysRegionService {

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
        List<SysRegionEntity> list = RegionUtils.readSheng();
        System.out.println(list.toString());
    }

}