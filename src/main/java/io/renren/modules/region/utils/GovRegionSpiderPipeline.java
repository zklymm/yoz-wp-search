package io.renren.modules.region.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.renren.modules.region.entity.SysRegionEntity;
import io.renren.modules.region.service.SysRegionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.util.List;
import java.util.Map;

/**
 * @author zk
 */
@Component
public class GovRegionSpiderPipeline implements Pipeline {
    @Autowired
    private SysRegionService sysRegionService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public void process(ResultItems resultItems, Task task) {
        for (Map.Entry<String, Object> entry : resultItems.getAll().entrySet()) {
            List<SysRegionEntity> list = (List) entry.getValue();

            for(SysRegionEntity sysRegionEntity:list){
                String s = JSONUtil.toJsonStr(sysRegionEntity);
                redisTemplate.opsForList().rightPushAll("regionList",s);
                Long region = redisTemplate.opsForValue().increment("region");
                System.out.println(region);
//                redisTemplate.opsForList().rightPush("regionList",sysRegionEntity);
                QueryWrapper<SysRegionEntity> wrapper = new QueryWrapper<>();
                wrapper.lambda().eq(SysRegionEntity::getRegionCode,sysRegionEntity.getRegionCode());
                List<SysRegionEntity> list1 = sysRegionService.list(wrapper);
                if(CollUtil.isNotEmpty(list1)){
                    sysRegionService.update(sysRegionEntity,wrapper);
                }else {
                    sysRegionService.save(sysRegionEntity);
                }
            }
        }
    }
}
