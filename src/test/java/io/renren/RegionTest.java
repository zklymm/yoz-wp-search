package io.renren;


import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class RegionTest {
    /**
     * 读省的信息
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        Stream.of("aaa","bbb","vvv").forEach(System.out::println);
        Stream.of(1111,2222,3333).forEach(System.out::println);

        //从 Map 集合中产生流数据，。然后分别调用 getKey() 和 getValue() 获取值。
        Map<String,Double> m = new HashMap<>();
        m.put("AA",1.1);
        m.put("BB",1.3);
        m.put("DDD",3.1);
        m.put("CCC",2.1);

//我们首先调用 entrySet() 产生一个对象流，每个对象都包含一个 key 键以及与其相关联的 value 值
//        将对象作为e参数传到后面，
        m.entrySet().stream().map(e -> e.getKey()+":"+
                e.getValue()).forEach(System.out::println);
    }
}