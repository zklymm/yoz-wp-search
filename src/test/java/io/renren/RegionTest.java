package io.renren;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 * 从国家统计局网站爬取2019年12位到村级别的行政区划代码
 * @author 杨志龙
 * blog:http://www.cnblogs.com/yangzhilong
 *
 */
public class RegionTest {
    public static final String baseUrl = "http://www.stats.gov.cn/tjsj/tjbz/tjyqhdmhcxhfdm/2019/";
    //设置utf-8发现有部分字符有乱码
    public static final String CHARSET = "GBK";

    public static final String[] strs = new String[]{
            "CITYTR","COUNTYTR","TOWNTR","VILLAGETR"
    };

    /**
     * 读省的信息
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        String url = baseUrl + "index.html";
        //如果需要设置代理
        //initProxy("10.10.13.200", "80");
        String str = getContent(url).toUpperCase();
        String[] arrs = str.split("<A");

        for (String s : arrs) {
            if (s.indexOf("HREF") != -1 && s.indexOf(".HTML") != -1) {

                String a = s.substring(7, s.indexOf("'>"));
                String name = s.substring(s.indexOf("'>")+2, s.indexOf("<BR/>"));
                String code = a.substring(0,a.indexOf('.'));
                if(!"云南省".equals(name)){//这行代码代表只抓取云南省
                    continue;
                }

                System.out.println("省级爬取:"+code+ "      "+name);

                read(a,code,1);
            }
        }
    }

    public static void read(String url,String pid,int n) throws Exception {
        String content = getContent(baseUrl+url).toUpperCase();
        String[] citys = content.split(strs[n-1]);
        if(n == 5){
            for(int i=1; i<citys.length; i++){
                String[] strs = citys[i].split("<TD>");
                String str1 = strs[1].substring(0, strs[1].indexOf("</TD>"));
                String str2 = strs[2].substring(0, strs[2].indexOf("</TD>"));
                String str3 = strs[3].substring(0, strs[3].indexOf("</TD>"));
                System.out.println("村级爬取:"+str1 +"   "+ str2 +"   "+ str3);
            }
            return;
        }else{
            for(int i=1; i<citys.length; i++){
                String[] strs = citys[i].split("<A HREF='");
                String cityUrl = null;
                String cityCode = null;
                if(citys[i].indexOf("<A HREF='")==-1){
                    cityCode = citys[i].substring(6, 18);
                    String cityName = citys[i].substring(citys[i].indexOf("</TD><TD>")+9,citys[i].lastIndexOf("</TD>"));
                    System.out.println(cityCode +"     "+cityName);

                }else {
                    for (int si = 1; si < 3; si++) {
                        if (si == 1) {//取链接和编码
                            cityUrl = strs[si].substring(0, strs[si].indexOf("'>"));
                            cityCode = strs[si].substring(strs[si].indexOf("'>") + 2, strs[si].indexOf("</A>"));

                        } else {

                            System.out.println("市级爬取:" + cityCode + "   " + strs[si].substring(strs[si].indexOf("'>") + 2, strs[si].indexOf("</A>")));
                        }
                    }
                }
                if(null!=cityUrl) {
                    read(cityUrl, cityCode, n++);
                }
            }
        }
    }

    /**
     * 读市的数据
     * @param url
     * @throws Exception
     */
    public static void readShi(String url) throws Exception{
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
                }
            }
            readXian(cityUrl.substring(0, cityUrl.indexOf("/")+1),cityUrl);
        }
    }

    /**
     * 读县的数据
     * @param url
     * @throws Exception
     */
    public static void readXian(String prix,String url) throws Exception{
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
                    }
                }
            }
            if(null!=cityUrl){
                readZhen(prix,cityUrl);
            }
        }
    }

    /**
     * 读镇的数据
     * @param url
     * @throws Exception
     */
    public static void readZhen(String prix,String url) throws Exception{
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
                }
            }
            readCun(myPrix,cityUrl);
        }
    }

    /**
     * 读村/街道的数据
     * @param url
     * @throws Exception
     */
    public static void readCun(String prix,String url) throws Exception{
        String content = getContent(baseUrl+prix+url).toUpperCase();
        String[] citys = content.split("VILLAGETR");
        for(int i=1; i<citys.length; i++){
            String[] strs = citys[i].split("<TD>");
            String str1 = strs[1].substring(0, strs[1].indexOf("</TD>"));
            String str2 = strs[2].substring(0, strs[2].indexOf("</TD>"));
            String str3 = strs[3].substring(0, strs[3].indexOf("</TD>"));
            System.out.println("村级爬取:"+str1 +"   "+ str2 +"   "+ str3);
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
    public static String getContent(String strUrl) throws Exception {
        HttpClient httpClient = HttpClients.createDefault(); // 创建httpClient实例
        HttpGet httpGet = new HttpGet(strUrl); // 创建httpget实例
        httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.111 Safari/537.36"); // 设置请求头消息User-Agent
        HttpResponse response = httpClient.execute(httpGet); // 执行http get请求
        HttpEntity entity = response.getEntity(); // 获取返回实体
        return EntityUtils.toString(entity, CHARSET);
    }
}