package love.wangqi.p1;

import java.io.Serializable;

/**
 * @Description:
 * @Author: wangqi
 * @Version:
 * @Date: 2019-08-01 09:42
 */
public class RunningData implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long cid;
    private String name;

    public Long getCid() {
        return cid;
    }

    public void setCid(Long cid) {
        this.cid = cid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
