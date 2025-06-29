//package fun.werfamily.starter.cache;
//
//import fun.werfamily.starter.cache.serializer.ProtobufSerializer;
//import org.junit.Assert;
//import org.junit.Test;
//import org.redisson.spring.cache.CacheConfig;
//
//import java.io.IOException;
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Objects;
//
///**
// * Description:
// *
// * @Author : Mr.WenMing
// * @create 2022/2/24 15:16
// */
//public class SerializerTest {
//
//    @Test
//    public void serializer() {
//        ProtobufSerializer<TestBody> serializer = new ProtobufSerializer<>();
//        byte[] args = serializer.serialize(new TestBody("1", "test"), TestBody.class);
//        System.out.println(Arrays.toString(args));
//        Assert.assertEquals(Objects.requireNonNull(serializer.deserialize(args, TestBody.class)).getName(), "test");
//    }
//
//    @Test
//    public void toYAMLConfig() throws IOException {
//        Map<String, CacheConfig> configMap = new HashMap<>();
//        configMap.put("t-1", new CacheConfig(3000, 3000));
//        configMap.put("t-2", new CacheConfig(5000, 5000));
//        System.out.println(CacheConfig.toYAML(configMap));
//    }
//
//    public static class TestBody {
//        private String id;
//        private String name;
//
//        public TestBody(String id, String name) {
//            this.id = id;
//            this.name = name;
//        }
//
//        public String getId() {
//            return id;
//        }
//
//        public void setId(String id) {
//            this.id = id;
//        }
//
//        public String getName() {
//            return name;
//        }
//
//        public void setName(String name) {
//            this.name = name;
//        }
//    }
//}
