package fun.werfamily.starter.database.mybatis.plus;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import com.baomidou.mybatisplus.extension.injector.methods.InsertBatchSomeColumn;

import java.util.List;

/**
 * @Author: Mr.WenMing
 * @since: 2021/7/26
 */
public class SqlInjector extends DefaultSqlInjector {

    private static final String CREATE_TIME = "create_time";

    private static final String UPDATE_TIME = "update_time";

    private static final String CREATED_TIME = "created_time";

    private static final String UPDATED_TIME = "updated_time";

    @Override
    public List<AbstractMethod> getMethodList(Class<?> mapperClass) {
        List<AbstractMethod> methodList = super.getMethodList(mapperClass);
        methodList.add(new InsertBatchSomeColumn(
                t -> !CREATED_TIME.equals(t.getColumn()) && !UPDATED_TIME.equals(t.getColumn()) && !CREATE_TIME.equals(t.getColumn()) && !UPDATE_TIME.equals(t.getColumn())));
        return methodList;
    }
}