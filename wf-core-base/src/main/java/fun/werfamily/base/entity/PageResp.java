package fun.werfamily.base.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Description:
 *
 * @Author Mr.WenMing
 * Created on 2021/1/21.
 */
@Data
public class PageResp<T> implements Serializable {

    private static final long serialVersionUID = -8149476389082019530L;

    private Integer pageIndex;
    private Integer pageSize;
    private Integer total;
    private Integer pages;
    private Boolean hasNextPage;
    private List<T> list;

    public static <T> PageResp<T> success(List<T> list, Integer pageNum, Integer pageSize, Integer total) {
        PageResp<T> response = new PageResp<T>();
        response.setList(list);
        response.setPageIndex(pageNum);
        response.setPageSize(pageSize);
        response.setTotal(total);

        if (pageSize == 0) {
            response.setPages(1);
            response.setHasNextPage(false);
        } else {
            int totalPage = (total + pageSize - 1) / pageSize;
            boolean hasNext = false;
            if (pageNum < totalPage) {
                hasNext = true;
            }
            response.setHasNextPage(hasNext);
            response.setPages(totalPage);
        }
        return response;
    }


}
