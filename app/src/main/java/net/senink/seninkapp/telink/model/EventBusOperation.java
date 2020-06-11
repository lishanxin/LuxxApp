package net.senink.seninkapp.telink.model;

public class EventBusOperation {
    public static final int REFRESH_GROUP_DATA = 0X01;


    private int opr;

    public EventBusOperation(int opr) {
        this.opr = opr;
    }

    public int getOpr() {
        return opr;
    }

}
