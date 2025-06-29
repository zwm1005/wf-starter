package fun.werfamily.framework.excel.executor;

import java.util.List;

/**
 * Description:
 * <p>
 *
 * @Author Mr.WenMing
 * Created on 2022/7/11.
 */
public interface IExportSourceHanlder<T> {

    /**
     * 获取数据
     *
     * @return
     */
    public List getSourceData(String dataParam);

    /**
     * 获取排序规则
     *
     * @return
     */
    public String getOrderBy();

    /**
     * 获取数据类型
     *
     * @return
     */
    Class getSourceDataClass();

    /**
     * 组装数据
     *
     * @param data
     * @return
     */
    List<T> buildSourceData(List data, String dataParam);
}
