package fun.werfamily.starter.mq.rocket.trace.common;

import java.util.HashSet;
import java.util.Set;


/**
 * @Author alvin
 * @date 16-3-9
 */
public class OnsTraceTransferBean {
    private String transData;
    private Set<String> transKey = new HashSet<String>();


    public String getTransData() {
        return transData;
    }


    public void setTransData(String transData) {
        this.transData = transData;
    }


    public Set<String> getTransKey() {
        return transKey;
    }


    public void setTransKey(Set<String> transKey) {
        this.transKey = transKey;
    }
}
