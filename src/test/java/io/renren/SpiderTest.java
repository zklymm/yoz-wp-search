package io.renren;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.JsonFilePipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.processor.example.GithubRepoPageProcessor;
import us.codecraft.webmagic.selector.Selectable;

public class SpiderTest implements PageProcessor {
    // 部分一：抓取网站的相关配置，包括编码、抓取间隔、重试次数等
    private Site site = Site.me().setRetryTimes(3).setSleepTime(1000);

    @Override
    // process是定制爬虫逻辑的核心接口，在这里编写抽取逻辑
    public void process(Page page) {
        // 部分二：定义如何抽取页面信息，并保存下来
//        page.putField("author", page.getUrl().regex("https://github\\.com/(\\w+)/.*").toString());
        page.putField("provName",page.getHtml().xpath("//tr[@class='provincetr']/td/a/text()").all());
        page.putField("provCode",page.getHtml().xpath("//tr[@class='provincetr']/td/a/@href").all());
        page.putField("cityName",page.getHtml().xpath("//tr[@class='citytr']/td[2]/a/text()").all());
        page.putField("cityCode",page.getHtml().xpath("//tr[@class='citytr']/td[1]/a/text()").all());
        page.putField("countyName",page.getHtml().xpath("//tr[@class='countytr']/td[2]/a/text()").all());
        page.putField("countyCode",page.getHtml().xpath("//tr[@class='countytr']/td[1]/a/text()").all());
        page.putField("townName",page.getHtml().xpath("//tr[@class='towntr']/td[2]/a/text()").all());
        page.putField("townCode",page.getHtml().xpath("//tr[@class='towntr']/td[1]/a/text()").all());
        page.putField("villageName", page.getHtml().xpath("//tr[@class='villagetr']/td[3]/text()").all());
        page.putField("villageType", page.getHtml().xpath("//tr[@class='villagetr']/td[2]/text()").all());
        page.putField("villageCode", page.getHtml().xpath("//tr[@class='villagetr']/td[1]/text()").all());
        // 部分三：从页面发现后续的url地址来抓取
        page.addTargetRequests(page.getHtml().xpath("//tr[@class='provincetr']/td/a/@href|//tr[@class='citytr']/td[1]/a/@href|//tr[@class='countytr']/td[1]/a/@href|//tr[@class='towntr']/td[1]/a/@href").all());
    }

    @Override
    public Site getSite() {
        return site.setCharset("gbk").setRetryTimes(3).setSleepTime(1000).setTimeOut(10000);
    }

    public static void main(String[] args) {

        Spider.create(new SpiderTest())
                //从"https://github.com/code4craft"开始抓
                .addUrl("http://www.stats.gov.cn/tjsj/tjbz/tjyqhdmhcxhfdm/2019/")
//                .addUrl("https://github.com/code4craft")
                //.addUrl("http://www.stats.gov.cn/tjsj/tjbz/tjyqhdmhcxhfdm/2019/45/09/450981.html")
                .addPipeline(new PipelineTest())
                //开启5个线程抓取
                .thread(5)
                //启动爬虫
                .run();
    }

}
