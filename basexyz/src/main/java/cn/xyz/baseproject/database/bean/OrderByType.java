package cn.xyz.baseproject.database.bean;

/**
 * 排序类型
 * Created by zhangzheng on 2016/8/8.
 */
public class  OrderByType {
    private boolean isAscending;
    private String orderBycolumnName;

    public OrderByType(boolean isAscending, String orderBycolumnName) {
        this.isAscending = isAscending;
        this.orderBycolumnName = orderBycolumnName;
    }

    public OrderByType() {}

    public boolean isAscending() {
        return isAscending;
    }

    public void setAscending(boolean ascending) {
        isAscending = ascending;
    }

    public String getOrderBycolumnName() {
        return orderBycolumnName;
    }

    public void setOrderBycolumnName(String orderBycolumnName) {
        this.orderBycolumnName = orderBycolumnName;
    }
}
