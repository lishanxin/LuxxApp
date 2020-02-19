package net.senink.piservice.pis;

import java.io.Serializable;

/**
 * Created by wensttu on 2016/6/17.
 */
public class PICommandProperty implements Serializable {
    private static final long serialVersionUID = 1L;
    private long mExecuteInterval = 5;

    public int PICommand;				// 命令（消息）类型
    public String PICmdName;			// 命令（消息）说明
    public String PICmdTips;			// 命令（消息）详细信息
    public PISBase Parent;				// 支持该请求的PIS对象
    public PipaRequest.REQUEST_TYPE PropType;		// 请求的类型
    public boolean IsSubscribed;		// 是否已经被订阅，只有PropType为Subscribe才有效
    public boolean IsVaild;			    // 是否失效，如果为false，则表示该命令（消息）在当前版本不被支持
    public long ExecuteMinInterval;         // 执行的最小间隔时间

    public PipaRequest.REQUEST_STATUS PIStatus;	    // 状态

    public PICommandProperty(int cmd,
                      String name,
                      String tips,
                      PipaRequest.REQUEST_TYPE reqtype,
                      boolean isVaild,
                      long executeInterval) {
        mExecuteInterval = executeInterval;
        this.PICommand = cmd;
        this.PICmdName = name;
        this.PICmdTips = tips;
        this.PropType = reqtype;
        this.IsVaild = isVaild;
        if(reqtype == PipaRequest.REQUEST_TYPE.SUBSCRIBE)
            IsSubscribed = true;
        else
            IsSubscribed = false;
    }

    public PICommandProperty(int cmd,
                      String name,
                      String tips,
                      PipaRequest.REQUEST_TYPE reqtype,
                      boolean isVaild) {
        this.PICommand = cmd;
        this.PICmdName = name;
        this.PICmdTips = tips;
        this.PropType = reqtype;
        this.IsVaild = isVaild;
        if(reqtype == PipaRequest.REQUEST_TYPE.SUBSCRIBE)
            IsSubscribed = true;
        else
            IsSubscribed = false;
    }
}
