package love.wangqi.discovery;

import com.fasterxml.jackson.annotation.JsonRootName;

/**
 * @Description:
 * @Author: wangqi
 * @Version:
 * @Date: 2019-08-01 16:33
 */
@JsonRootName("details")
public class InstanceDetails {
    private String description;

    public InstanceDetails() {
        this("");
    }

    public InstanceDetails(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
