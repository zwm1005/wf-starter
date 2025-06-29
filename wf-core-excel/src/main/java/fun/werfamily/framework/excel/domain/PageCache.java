package fun.werfamily.framework.excel.domain;

import lombok.Data;

/**
 * Description:
 * <p>
 *
 * @Author Mr.WenMing
 * Created on 2022/7/12.
 */
@Data
public class PageCache {

    private Integer endPageIndex;

    private Integer exportCount;

    public PageCache(Integer endPageIndex, Integer exportCount) {
        this.endPageIndex = endPageIndex;
        this.exportCount = exportCount;
    }
}
