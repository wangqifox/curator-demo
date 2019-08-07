package discovery;

/**
 * @Description:
 * @Author: wangqi
 * @Version:
 * @Date: 2019-08-01 17:45
 */
public class Node {
    private String name;
    private String id;
    private Integer state;

    public static final Integer MASTER = 1;
    public static final Integer DATA = 0;

    public Node() {
    }

    public Node(String name, String id) {
        this.name = name;
        this.id = id;
        this.state = DATA;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return JsonUtils.toJson(this);
    }

    public static Node fromString(String str) {
        return JsonUtils.parseObject(str, Node.class);
    }
}
