package net.senink.seninkapp.ui.home;

public class TelinkDataRefreshEntry {
    private boolean isGotLightStatus;

    public TelinkDataRefreshEntry() {
    }

    public TelinkDataRefreshEntry(boolean isGotLightStatus) {
        this.isGotLightStatus = isGotLightStatus;
    }

    public boolean isGotLightStatus() {
        return isGotLightStatus;
    }

    public void setGotLightStatus(boolean gotLightStatus) {
        isGotLightStatus = gotLightStatus;
    }
}
