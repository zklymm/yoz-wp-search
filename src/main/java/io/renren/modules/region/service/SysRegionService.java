package io.renren.modules.region.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.renren.common.utils.PageUtils;
import io.renren.modules.region.entity.SysRegionEntity;

import java.util.Map;

/**
 * 行政区表
 *
 * @author zk
 * @email zk@gmail.com
 * @date 2020-11-03 16:42:54
 */
public interface SysRegionService extends IService<SysRegionEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void updateRegion();
}

