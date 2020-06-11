package net.senink.piservice.pis;

public class PISBaseAdd {
    private PISBase device;

    public PISBaseAdd(PISBase device) {
        this.device = device;
    }

    public PISBase getDevice() {
        return device;
    }

    public void setDevice(PISBase device) {
        this.device = device;
    }
}
