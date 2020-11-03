package io.renren.modules.region.utils;

import io.renren.common.utils.IPUtils;
import io.renren.modules.region.entity.SysRegionEntity;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class RegionUtils {
    public static final String baseUrl = "http://www.stats.gov.cn/tjsj/tjbz/tjyqhdmhcxhfdm/2019/";
    //设置utf-8发现有部分字符有乱码
    public static final String CHARSET = "GBK";

    private static Logger logger = LoggerFactory.getLogger(IPUtils.class);
    public static List<SysRegionEntity> list = new ArrayList<>();
    /**
     * 读省的信息
     * @throws Exception
     */
    public static List<SysRegionEntity> readSheng() {
        String url = baseUrl + "index.html";
        //如果需要设置代理
        //initProxy("10.10.13.200", "80");
        String str = getContent(url).toUpperCase();
        String[] arrs = str.split("<A");

        for (String s : arrs) {
            if (s.indexOf("HREF") != -1 && s.indexOf(".HTML") != -1) {

                String a = s.substring(7, s.indexOf("'>"));
                String name = s.substring(s.indexOf("'>")+2, s.indexOf("<BR/>"));
                String code = a.substring(0,a.indexOf('.'))+"0000000000";
                if(!"北京市".equals(name)){//这行代码代表只抓取云南省
                    continue;
                }

                System.out.println("省级爬取:"+code+ "      "+name);
                SysRegionEntity entity = new SysRegionEntity();
                entity.setRegionId(code);
                entity.setRegionName(name);
                entity.setRegionType(1);
                list.add(entity);
                readShi(a,code);
            }
        }
        return list;
    }

    /**
     * 读市的数据
     * @param url
     * @throws Exception
     */
    public static void readShi(String url,String pid) {
        String content = getContent(baseUrl+url).toUpperCase();
        String[] citys = content.split("CITYTR");
        //'><TD><A HREF='11/1101.HTML'>110100000000</A></TD><TD><A HREF='11/1101.HTML'>市辖区</A></TD></td><TR CLASS='
        for(int c=1,len=citys.length; c<len; c++){
            String[] strs = citys[c].split("<A HREF='");
            String cityUrl = null;
            String cityCode = null;
            for(int si = 1; si<3; si++){
                if(si==1){//取链接和编码
                    cityUrl = strs[si].substring(0, strs[si].indexOf("'>"));
                    cityCode = strs[si].substring(strs[si].indexOf("'>")+2, strs[si].indexOf("</A>"));

                }else{

                    System.out.println("市级爬取:"+cityCode+"   "+strs[si].substring(strs[si].indexOf("'>")+2, strs[si].indexOf("</A>")));
                    SysRegionEntity entity = new SysRegionEntity();
                    entity.setRegionId(cityCode);
                    entity.setRegionName(strs[si].substring(strs[si].indexOf("'>")+2, strs[si].indexOf("</A>")));
                    entity.setRegionType(2);
                    entity.setPid(pid);
                    list.add(entity);
                }
            }
            readXian(cityUrl.substring(0, cityUrl.indexOf("/")+1),cityUrl,cityCode);
        }
    }

    /**
     * 读县的数据
     * @param url
     * @throws Exception
     */
    public static void readXian(String prix,String url,String pid) {
        String content = getContent(baseUrl+url).toUpperCase();
        String[] citys = content.split("COUNTYTR");
        for(int i=1; i<citys.length; i++){
            String cityUrl = null;
            String cityCode = null;
            //发现石家庄有一个县居然没超链接，特殊处理
            if(citys[i].indexOf("<A HREF='")==-1){
                cityCode = citys[i].substring(6, 18);
                String cityName = citys[i].substring(citys[i].indexOf("</TD><TD>")+9,citys[i].lastIndexOf("</TD>"));
                System.out.println(cityCode +"     "+cityName);

            }else{
                String[] strs = citys[i].split("<A HREF='");
                for(int si = 1; si<3; si++){
                    if(si==1){//取链接和编码
                        cityUrl = strs[si].substring(0, strs[si].indexOf("'>"));
                        cityCode = strs[si].substring(strs[si].indexOf("'>")+2, strs[si].indexOf("</A>"));
                    }else{
                        System.out.println("县级爬取:"+cityCode+"   "+strs[si].substring(strs[si].indexOf("'>")+2, strs[si].indexOf("</A>")));
                        SysRegionEntity entity = new SysRegionEntity();
                        entity.setRegionId(cityCode);
                        entity.setRegionName(strs[si].substring(strs[si].indexOf("'>")+2, strs[si].indexOf("</A>")));
                        entity.setRegionType(3);
                        entity.setPid(pid);
                        list.add(entity);
                    }
                }
            }
            if(null!=cityUrl){
                readZhen(prix,cityUrl,cityCode);
            }
        }
    }

    /**
     * 读镇的数据
     * @param url
     * @throws Exception
     */
    public static void readZhen(String prix,String url,String pid) {
        String content = getContent(baseUrl+prix+url).toUpperCase();
        String myPrix = (prix+url).substring(0, (prix+url).lastIndexOf("/")+1);
        String[] citys = content.split("TOWNTR");
        for(int i=1; i<citys.length; i++){
            String[] strs = citys[i].split("<A HREF='");
            String cityUrl = null;
            String cityCode = null;
            for(int si = 1; si<3; si++){
                if(si==1){//取链接和编码
                    cityUrl = strs[si].substring(0, strs[si].indexOf("'>"));
                    cityCode = strs[si].substring(strs[si].indexOf("'>")+2, strs[si].indexOf("</A>"));
                }else{
                    System.out.println("镇级爬取:"+cityCode+"   "+strs[si].substring(strs[si].indexOf("'>")+2, strs[si].indexOf("</A>")));
                    SysRegionEntity entity = new SysRegionEntity();
                    entity.setRegionId(cityCode);
                    entity.setRegionName(strs[si].substring(strs[si].indexOf("'>")+2, strs[si].indexOf("</A>")));
                    entity.setRegionType(4);
                    entity.setPid(pid);
                    list.add(entity);
                }
            }
            readCun(myPrix,cityUrl,cityCode);
        }
    }

    /**
     * 读村/街道的数据
     * @param url
     * @throws Exception
     */
    public static void readCun(String prix,String url,String pid) {
        String content = getContent(baseUrl+prix+url).toUpperCase();
        String[] citys = content.split("VILLAGETR");
        for(int i=1; i<citys.length; i++){
            String[] strs = citys[i].split("<TD>");
            String cityCode = strs[1].substring(0, strs[1].indexOf("</TD>"));
            String str2 = strs[2].substring(0, strs[2].indexOf("</TD>"));
            String str3 = strs[3].substring(0, strs[3].indexOf("</TD>"));
            System.out.println("村级爬取:"+cityCode +"   "+ str2 +"   "+ str3);
            SysRegionEntity entity = new SysRegionEntity();
            entity.setRegionId(cityCode);
            entity.setRegionName(str3);
            entity.setCountyType(str2);
            entity.setRegionType(5);
            entity.setPid(pid);
            list.add(entity);
        }
    }

    //设置代理
    public static void initProxy(String host, String port) {
        System.setProperty("http.proxyType", "4");
        System.setProperty("http.proxyPort", port);
        System.setProperty("http.proxyHost", host);
        System.setProperty("http.proxySet", "true");
    }

    //获取网页的内容
    public static String getContent(String strUrl) {

        String[] strs = new String[]{
                "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.111 Safari/537.36",
                "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:6.0) Gecko/20100101 Firefox/6.0",
                "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/534.50 (KHTML, like Gecko) Version/5.1 Safari/534.50",
                "Opera/9.80 (Windows NT 6.1; U; zh-cn) Presto/2.9.168 Version/11.50",
                "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Win64; x64; Trident/5.0; .NET CLR 2.0.50727; SLCC2; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; InfoPath.3; .NET4.0C; Tablet PC 2.0; .NET4.0E)",
                "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1; WOW64; Trident/4.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; .NET4.0C; InfoPath.3)",
                "Mozilla/5.0 (Windows; U; Windows NT 6.1; ) AppleWebKit/534.12 (KHTML, like Gecko) Maxthon/3.0 Safari/534.12",
                "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.1; WOW64; Trident/5.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; InfoPath.3; .NET4.0C; .NET4.0E)",
                "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.1; WOW64; Trident/5.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; InfoPath.3; .NET4.0C; .NET4.0E; SE 2.X MetaSr 1.0)",
                "Mozilla/5.0 (Windows; U; Windows NT 6.1; en-US) AppleWebKit/534.3 (KHTML, like Gecko) Chrome/6.0.472.33 Safari/534.3 SE 2.X MetaSr 1.0",
                "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; InfoPath.3; .NET4.0C; .NET4.0E)",
                "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/13.0.782.41 Safari/535.1 QQBrowser/6.9.11079.201",
                "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.1; WOW64; Trident/5.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; InfoPath.3; .NET4.0C; .NET4.0E) QQBrowser/6.9.11079.201",
                "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0)"
        };
        int index = (int) (Math.random()*14);
        try {
            Thread.sleep(index * 1000);
            HttpClient httpClient = HttpClients.createDefault(); // 创建httpClient实例
            HttpGet httpGet = new HttpGet(strUrl); // 创建httpget实例
            httpGet.setHeader("User-Agent", strs[index]); // 设置请求头消息User-Agent
            HttpResponse response = httpClient.execute(httpGet); // 执行http get请求
            HttpEntity entity = response.getEntity(); // 获取返回实体
            return EntityUtils.toString(entity, CHARSET);
        }catch (Exception e){
            logger.error("RegionUtils ERROR ", e);
            return null;
        }
    }
}
