package net.senink.seninkapp.telink.model;

public class TelinkOperation {
    public static final int REFRESH_GROUP_DATA = 0X01;
    public static final int DEVICE_BIND_OR_UNBIND_GROUP_SUCCEED = 0X02;
    public static final int DEVICE_BIND_OR_UNBIND_GROUP_FAIL = 0X03;
    public static final int RECONNECT_TELINK_DEVICES = 0X04;
    public static final int DEVICE_FOUND = 0X05;

    private int opr;

    public TelinkOperation(int opr) {
        this.opr = opr;
    }

    public int getOpr() {
        return opr;
    }

}
