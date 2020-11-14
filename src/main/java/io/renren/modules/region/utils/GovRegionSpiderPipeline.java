package io.renren.modules.region.utils;

import io.renren.modules.region.service.SysRegionService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Override
    public void process(ResultItems resultItems, Task task) {
        for (Map.Entry<String, Object> entry : resultItems.getAll().entrySet()) {
            sysRegionService.saveOrUpdateBatch((List)entry.getValue());
        }
    }
}
