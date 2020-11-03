package com.telink.sig.mesh.model;

public class CommandModel {
    public static final int Command_ON = 0;
    public static final int Command_OFF = 1;
    public static final int Custom_Command = 2;
    private int commandMode;
    private byte[] customCommand;
}
