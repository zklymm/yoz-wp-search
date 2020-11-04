package io.renren.modules.region.utils;

import io.renren.modules.region.entity.SysRegionEntity;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.ArrayList;
import java.util.List;

@Component
public class GovRegionSpiderUtils implements PageProcessor {
    // 部分一：抓取网站的相关配置，包括编码、抓取间隔、重试次数等
    private Site site = Site.me().setRetryTimes(3).setSleepTime(1000);

    @Override
    // process是定制爬虫逻辑的核心接口，在这里编写抽取逻辑
    public void process(Page page) {
        // 部分二：定义如何抽取页面信息，并保存下来
//        page.putField("author", page.getUrl().regex("https://github\\.com/(\\w+)/.*").toString());
        List<String> provNameList = page.getHtml().xpath("//tr[@class='provincetr']/td/a/text()").all();
        List<String> provCodeList = page.getHtml().xpath("//tr[@class='provincetr']/td/a/@href").all();
        List<String> cityNameList = page.getHtml().xpath("//tr[@class='citytr']/td[2]/a/text()").all();
        List<String> cityCodeList = page.getHtml().xpath("//tr[@class='citytr']/td[1]/a/text()").all();
        List<String> countyNameList = page.getHtml().xpath("//tr[@class='countytr']/td[2]/a/text()").all();
        List<String> countyCodeList = page.getHtml().xpath("//tr[@class='countytr']/td[1]/a/text()").all();
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
                entity.setRegionId(provCodeList.get(i).substring(0,provCodeList.get(i).indexOf("."))+"0000000000");
                entity.setRegionName(provNameList.get(i));
                entity.setRegionType(1);
                list.add(entity);
            }
        }else if(cityNameList.size()>0 && cityCodeList.size()>0){
            if(cityNameList.size() != cityCodeList.size()){
                return;
            }
            for(int i=0;i<cityNameList.size();i++){
                SysRegionEntity entity = new SysRegionEntity();
                entity.setRegionId(cityCodeList.get(i));
                entity.setRegionName(cityNameList.get(i));
                entity.setPid(cityCodeList.get(i).substring(0,2)+"0000000000");
                entity.setRegionType(2);
                list.add(entity);
            }
        }else if(countyNameList.size()>0 && countyCodeList.size()>0){
            if(countyNameList.size() != countyCodeList.size()){
                return;
            }
            for(int i=0;i<countyNameList.size();i++){
                SysRegionEntity entity = new SysRegionEntity();
                entity.setRegionId(countyCodeList.get(i));
                entity.setRegionName(countyNameList.get(i));
                entity.setRegionType(3);
                entity.setPid(countyCodeList.get(i).substring(0,4)+"00000000");
                list.add(entity);
            }
        }else if(townNameList.size()>0 && townCodeList.size()>0){
            if(townNameList.size() != townCodeList.size()){
                return;
            }
            for(int i=0;i<townNameList.size();i++){
                SysRegionEntity entity = new SysRegionEntity();
                entity.setRegionId(townCodeList.get(i));
                entity.setRegionName(townNameList.get(i));
                entity.setRegionType(4);
                entity.setPid(townCodeList.get(i).substring(0,6)+"000000");
                list.add(entity);
            }
        }else if(villageNameList.size()>0 && villageTypeList.size()>0 && villageCodeList.size()>0){
            if(villageNameList.size() != villageTypeList.size() || villageTypeList.size() != villageCodeList.size()){
                return;
            }
            for(int i=0;i<cityNameList.size();i++){
                SysRegionEntity entity = new SysRegionEntity();
                entity.setRegionId(villageCodeList.get(i));
                entity.setRegionName(villageNameList.get(i));
                entity.setCountyType(villageTypeList.get(i));
                entity.setRegionType(5);
                entity.setPid(villageCodeList.get(i).substring(0,9)+"000");
                list.add(entity);
            }
        }
        System.out.println(list);


        page.putField("list",list);
        // 部分三：从页面发现后续的url地址来抓取
        page.addTargetRequests(page.getHtml().xpath("//tr[@class='provincetr']/td/a/@href|//tr[@class='citytr']/td[1]/a/@href|//tr[@class='countytr']/td[1]/a/@href|//tr[@class='towntr']/td[1]/a/@href").all());
    }

    @Override
    public Site getSite() {
        return site.setCharset("gbk").setRetryTimes(3).setSleepTime(3000).setTimeOut(10000)
                .setUserAgent("\"Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.111 Safari/537.36\"");
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
