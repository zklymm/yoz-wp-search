package io.renren;


import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

public class FreeMakerTest {
//    public static void main(String[] args) {
//        try{
//            //1.创建配置类,这个configuration是freemarker提供的,不要导错包了
//            Configuration configuration=new Configuration(Configuration.getVersion());
//            //2.设置模板所在的目录,这里定义的就是刚刚test.ftl所存放的真实目录
//            configuration.setDirectoryForTemplateLoading(new File("D:/workspace/freemarkerDemo/"));  //注意这里是文件夹路径,不是文件路径
//            //3.设置字符集
//            configuration.setDefaultEncoding("utf-8");
//            //4.加载模板
//            Template template = configuration.getTemplate("/test.ftl");
//            //5.创建数据模型,就是用来填充模板那些插值的,可以用map,也可以定义对象,一般都是map,注意的是key需要跟插值中的对应上
//            Map map=new HashMap();
//            map.put("name", "张三 ");
//            map.put("message", "欢迎来到Freemarker！ ");
//            //6.创建 Writer 对象,代表生成的源代码会存放到哪里
//            Writer out =new FileWriter(new File("d:/workspace/freemarkerDemo/test.html"));
//            //7.输出
//            template.process(map, out);
//            //8.关闭 Writer 对象,切记不要忘记关流,不然以上的数据都还是在内存中,需要refresh才可以持久化到磁盘,这是IO流的知识点.
//            out.close();
//        }catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }
}
