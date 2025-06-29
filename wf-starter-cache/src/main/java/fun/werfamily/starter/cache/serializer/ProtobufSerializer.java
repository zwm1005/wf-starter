//package fun.werfamily.starter.cache.serializer;
//
//import com.dyuproject.protostuff.LinkedBuffer;
//import com.dyuproject.protostuff.ProtobufIOUtil;
//import com.dyuproject.protostuff.runtime.RuntimeSchema;
//import org.springframework.data.redis.serializer.SerializationException;
//
///**
// * Description: protobuf协议序列化
// *
// * @Author : Mr.WenMing
// * @create 2022/2/24 14:57
// */
//public class ProtobufSerializer<T> {
//
//    public byte[] serialize(T t, Class<T> clazz) throws SerializationException {
//        return ProtobufIOUtil.toByteArray(t, RuntimeSchema.createFrom(clazz),
//                LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
//    }
//
//    public T deserialize(byte[] bytes, Class<T> clazz) throws SerializationException {
//        RuntimeSchema<T> runtimeSchema = RuntimeSchema.createFrom(clazz);
//        T t = runtimeSchema.newMessage();
//        ProtobufIOUtil.mergeFrom(bytes, t, runtimeSchema);
//        return t;
//    }
//
//    public ProtobufSerializer() {
//    }
//}
