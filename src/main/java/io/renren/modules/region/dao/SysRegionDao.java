package io.renren.modules.region.dao;

import io.renren.modules.region.entity.SysRegionEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 行政区表
 * 
 * @author zk
 * @email zk@gmail.com
 * @date 2020-11-03 16:42:54
 */
@Mapper
public interface SysRegionDao extends BaseMapper<SysRegionEntity> {
	
}
