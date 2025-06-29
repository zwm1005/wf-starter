package fun.werfamily.base.entity;

import lombok.Data;

/**
 * Description:
 *
 * @Author Mr.WenMing
 * Created on 2019/10/11.
 */
@Data
public class PageReq {

    private int pageIndex = 1;
    private int pageSize = 10;

}
