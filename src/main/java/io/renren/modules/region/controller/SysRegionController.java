package io.renren.modules.region.controller;

import java.util.Arrays;
import java.util.Map;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.renren.modules.region.entity.SysRegionEntity;
import io.renren.modules.region.service.SysRegionService;
import io.renren.common.utils.PageUtils;
import io.renren.common.utils.R;



/**
 * 行政区表
 *
 * @author zk
 * @email zk@gmail.com
 * @date 2020-11-03 16:42:54
 */
@RestController
@RequestMapping("region/sysregion")
public class SysRegionController {
    @Autowired
    private SysRegionService sysRegionService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("region:sysregion:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = sysRegionService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("region:sysregion:info")
    public R info(@PathVariable("id") Long id){
		SysRegionEntity sysRegion = sysRegionService.getById(id);

        return R.ok().put("sysRegion", sysRegion);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("region:sysregion:save")
    public R save(@RequestBody SysRegionEntity sysRegion){
		sysRegionService.save(sysRegion);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("region:sysregion:update")
    public R update(@RequestBody SysRegionEntity sysRegion){
		sysRegionService.updateById(sysRegion);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("region:sysregion:delete")
    public R delete(@RequestBody Long[] ids){
		sysRegionService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/updateRegion")
    @RequiresPermissions("region:sysregion:updateregion")
    public R updateRegion(){
        sysRegionService.updateRegion();

        return R.ok();
    }

}
