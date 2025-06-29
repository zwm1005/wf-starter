package fun.werfamily.framework.excel.util;

import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * Description: 对象属性复制创建
 *
 * Author: Mr.WenMing
 * Created on 2019/6/25.
 */
public class ExportBeanConvertor {

    public interface BeanHandler<T> {
        T handler(T t);
    }

    public static <T> T convertor(Object obj, Class<T> cls) {
        if(obj==null){
            return null;
        }
        try {
            T t = cls.newInstance();
            BeanUtils.copyProperties(obj, t);
            return t;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> List<T> convertorToList(Object objList, Class<T> cls) {
        return convertorToList(objList, cls, null);
    }

    public static <T> List<T> convertorToList(Object objList, Class<T> cls, BeanHandler beanHandler) {
        if(objList==null){
            return null;
        }
        List<T> list = new ArrayList<>();
        List listTemp=(List)objList;
        listTemp.forEach(item -> {
            T t = convertor(item, cls);
            if(beanHandler != null) {
                t = (T) beanHandler.handler(t);
            }
            list.add(t);
        });
        return list;
    }

    public static <T> List<T> convertorToNotNullList(Object objList, Class<T> cls, BeanHandler beanHandler) {
        if(objList==null){
            return null;
        }
        List<T> list = new ArrayList<>();
        List listTemp=(List)objList;
        listTemp.forEach(item -> {
            T t = convertor(item, cls);
            if(beanHandler != null) {
                t = (T) beanHandler.handler(t);
            }
            if(t!=null) {
                list.add(t);
            }
        });
        return list;
    }

}
