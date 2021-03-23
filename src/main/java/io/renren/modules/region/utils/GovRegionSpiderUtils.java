package io.renren.modules.region.utils;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.renren.modules.region.entity.SysRegionEntity;
import io.renren.modules.region.service.SysRegionService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.*;

@Component
public class GovRegionSpiderUtils implements PageProcessor {
    // 部分一：抓取网站的相关配置，包括编码、抓取间隔、重试次数等
    private Site site = Site.me().setCharset("gbk").setRetryTimes(3).setSleepTime(10000).setTimeOut(10000).setUserAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/534.50 (KHTML, like Gecko) Version/5.1 Safari/534.50");
    @Autowired
    private SysRegionService sysRegionService;

    private String gaoUrl = "https://restapi.amap.com/v3/geocode/geo";
    private String gaoKey = "4138c1cea719cc344b6c876825079684";
    @Override
    // process是定制爬虫逻辑的核心接口，在这里编写抽取逻辑
    public void process(Page page) {
        // 部分二：定义如何抽取页面信息，并保存下来
//        page.putField("author", page.getUrl().regex("https://github\\.com/(\\w+)/.*").toString());
        List<String> provNameList = page.getHtml().xpath("//tr[@class='provincetr']/td/a/text()").all();
        List<String> provCodeList = page.getHtml().xpath("//tr[@class='provincetr']/td/a/@href").all();
        List<String> cityNameList = page.getHtml().xpath("//tr[@class='citytr']/td[2]/a/text()").all();
        List<String> cityCodeList = page.getHtml().xpath("//tr[@class='citytr']/td[1]/a/text()").all();
        List<String> countyNameList = page.getHtml().xpath("//tr[@class='countytr']/td[2]/a/text()|//tr[@class='countytr']/td[2]/text()").all();
        countyNameList.removeAll(Collections.singleton(""));
        List<String> countyCodeList = page.getHtml().xpath("//tr[@class='countytr']/td[1]/a/text()|//tr[@class='countytr']/td[1]/text()").all();
        countyCodeList.removeAll(Collections.singleton(""));
        List<String> townNameList = page.getHtml().xpath("//tr[@class='towntr']/td[2]/a/text()").all();
        List<String> townCodeList = page.getHtml().xpath("//tr[@class='towntr']/td[1]/a/text()").all();
        List<String> villageNameList = page.getHtml().xpath("//tr[@class='villagetr']/td[3]/text()").all();
        List<String> villageTypeList = page.getHtml().xpath("//tr[@class='villagetr']/td[2]/text()").all();
        List<String> villageCodeList = page.getHtml().xpath("//tr[@class='villagetr']/td[1]/text()").all();
        List<SysRegionEntity> list = new ArrayList<>();
        if(provNameList.size()>0 && provCodeList.size()>0){
            if(provNameList.size() != provCodeList.size()){
                return;
            }
            for(int i=0;i<provNameList.size();i++){
                SysRegionEntity entity = new SysRegionEntity();
                entity.setRegionCode(provCodeList.get(i).substring(0,provCodeList.get(i).indexOf("."))+"0000000000");
                entity.setRegionName(provNameList.get(i));
                entity.setRegionType(1);
                entity.setProviceName(provNameList.get(i));
//                String[] itude = getItude(provNameList.get(i));
//                entity.setLongitude(itude[0]);
//                entity.setLatitude(itude[1]);
                list.add(entity);
            }
        }else if(cityNameList.size()>0 && cityCodeList.size()>0){
            if(cityNameList.size() != cityCodeList.size()){
                return;
            }
            for(int i=0;i<cityNameList.size();i++){
                String proviceCode = cityCodeList.get(i).substring(0,2)+"0000000000";
                SysRegionEntity entity = new SysRegionEntity();
                entity.setRegionCode(cityCodeList.get(i));
                entity.setRegionName(cityNameList.get(i));
                entity.setParentCode(proviceCode);
                entity.setRegionType(2);
                entity.setProviceName(getName(proviceCode));
                entity.setCityName(cityNameList.get(i));
                entity.setParentPath(proviceCode);
//                String[] itude = getItude(getName(proviceCode)+cityNameList.get(i));
//                entity.setLongitude(itude[0]);
//                entity.setLatitude(itude[1]);
                list.add(entity);
            }
        }else if(countyNameList.size()>0 && countyCodeList.size()>0){
            if(countyNameList.size() != countyCodeList.size()){
                return;
            }
            for(int i=0;i<countyNameList.size();i++){
                String proviceCode = countyCodeList.get(i).substring(0,2)+"0000000000";
                String cityCode = countyCodeList.get(i).substring(0,4)+"00000000";
                SysRegionEntity entity = new SysRegionEntity();
                entity.setRegionCode(countyCodeList.get(i));
                entity.setRegionName(countyNameList.get(i));
                entity.setRegionType(3);
                entity.setParentCode(cityCode);
                entity.setProviceName(getName(proviceCode));
                entity.setCityName(getName(cityCode));
                entity.setCountyName(countyNameList.get(i));
                entity.setParentPath(proviceCode+","+cityCode);
//                String[] itude = getItude(getName(proviceCode)+getName(cityCode)+countyNameList.get(i));
//                entity.setLongitude(itude[0]);
//                entity.setLatitude(itude[1]);
                list.add(entity);
            }
        }else if(townNameList.size()>0 && townCodeList.size()>0){
            if(townNameList.size() != townCodeList.size()){
                return;
            }
            for(int i=0;i<townNameList.size();i++){
                String proviceCode = townCodeList.get(i).substring(0,2)+"0000000000";
                String cityCode = townCodeList.get(i).substring(0,4)+"00000000";
                String countyCode = townCodeList.get(i).substring(0,6)+"000000";
                SysRegionEntity entity = new SysRegionEntity();
                entity.setRegionCode(townCodeList.get(i));
                entity.setRegionName(townNameList.get(i));
                entity.setRegionType(4);
                entity.setParentCode(countyCode);
                entity.setProviceName(getName(proviceCode));
                entity.setCityName(getName(cityCode));
                entity.setCountyName(getName(countyCode));
                entity.setTownName(townNameList.get(i));
                entity.setParentPath(proviceCode+","+cityCode+","+countyCode);
//                String[] itude = getItude(getName(proviceCode)+getName(cityCode)+getName(countyCode)+townNameList.get(i));
//                entity.setLongitude(itude[0]);
//                entity.setLatitude(itude[1]);
                list.add(entity);
            }
        }else if(villageNameList.size()>0 && villageTypeList.size()>0 && villageCodeList.size()>0){
            if(villageNameList.size() != villageTypeList.size() || villageTypeList.size() != villageCodeList.size()){
                return;
            }
            for(int i=0;i<villageNameList.size();i++){
                String proviceCode = villageCodeList.get(i).substring(0,2)+"0000000000";
                String cityCode = villageCodeList.get(i).substring(0,4)+"00000000";
                String countyCode = villageCodeList.get(i).substring(0,6)+"000000";
                String townCode = villageCodeList.get(i).substring(0,9)+"000";
                SysRegionEntity entity = new SysRegionEntity();
                entity.setRegionCode(villageCodeList.get(i));
                entity.setRegionName(villageNameList.get(i));
                entity.setCountyType(villageTypeList.get(i));
                entity.setRegionType(5);
                entity.setParentCode(townCode);
                entity.setProviceName(getName(proviceCode));
                entity.setCityName(getName(cityCode));
                entity.setCountyName(getName(countyCode));
                entity.setTownName(getName(townCode));
                entity.setVillageName(villageNameList.get(i));
                entity.setParentPath(proviceCode+","+cityCode+","+countyCode+","+townCode);
//                String[] itude = getItude(getName(proviceCode)+getName(cityCode)+getName(countyCode)+getName(townCode)+villageNameList.get(i));
//                entity.setLongitude(itude[0]);
//                entity.setLatitude(itude[1]);
                list.add(entity);
            }
        }
        System.out.println(list);


        page.putField("list",list);
        // 部分三：从页面发现后续的url地址来抓取
        page.addTargetRequests(page.getHtml().xpath("//tr[@class='provincetr']/td/a/@href|//tr[@class='citytr']/td[1]/a/@href|//tr[@class='countytr']/td[1]/a/@href|//tr[@class='towntr']/td[1]/a/@href").all());
    }

    private String getName(String regionCode){
        QueryWrapper<SysRegionEntity> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(StringUtils.isNotBlank(regionCode),SysRegionEntity::getRegionCode,regionCode);
        wrapper.lambda().select(SysRegionEntity::getRegionName);
        SysRegionEntity entity = sysRegionService.getOne(wrapper);

        return entity.getRegionName();
    }

    private String[] getItude(String address){
        Map<String,Object> param = new HashMap<>();
        param.put("address",address);
        param.put("key",gaoKey);
        String result = HttpUtil.get(gaoUrl, param);
        JSONObject jsonObject = JSONUtil.parseObj(result);
        JSONArray geocodes = (JSONArray)jsonObject.get("geocodes");
        Iterator<Object> iterator = geocodes.stream().iterator();
        Map map = (Map) iterator.next();
        String[] str = map.get("location").toString().split(",");
        System.out.println(str[1]);
        return str;

    }

    @Override
    public Site getSite() {

        return site.setCharset("gbk").setRetryTimes(3).setSleepTime(10000).setTimeOut(10000);
    }

//    public static void start() {
//
//        Spider.create(new GovRegionSpiderUtils())
//                //从"https://github.com/code4craft"开始抓
//                .addUrl("http://www.stats.gov.cn/tjsj/tjbz/tjyqhdmhcxhfdm/2019/")
////                .addUrl("https://github.com/code4craft")
//                //.addUrl("http://www.stats.gov.cn/tjsj/tjbz/tjyqhdmhcxhfdm/2019/45/09/450981.html")
//                .addPipeline(new GovRegionSpiderPipeline())
//                //开启5个线程抓取
//                .thread(5)
//                //启动爬虫
//                .run();
//    }
}
