package io.renren;

import io.renren.modules.region.entity.SysRegionEntity;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PipelineTest implements Pipeline {
    private List<SysRegionEntity> list = new ArrayList<>();
    @Override
    public void process(ResultItems resultItems, Task task) {
        SysRegionEntity entity = new SysRegionEntity();
        System.out.println("get page: " + resultItems.getRequest().getUrl());
        for (Map.Entry<String, Object> entry : resultItems.getAll().entrySet()) {
            System.out.println(entry.getKey() + ":\t" + entry.getValue());
        }
    }
}
