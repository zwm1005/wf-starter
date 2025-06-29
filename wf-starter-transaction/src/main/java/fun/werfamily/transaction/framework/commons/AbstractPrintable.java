package fun.werfamily.transaction.framework.commons;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;

/**
 * @AuthorMr.WenMing
 * @date 2022年03月12日
 */
public abstract class AbstractPrintable implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 6066611493519757245L;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}
