package com.heaven7.java.thinking.vsync;

public final class RunningInfo {
    public int renderIndex;
    public long firstStartTime;

    public long renderStartTime;
    public long renderCostTime;
    public long lastVsyncCost;

    public void copyTo(RunningInfo info) {
        info.renderIndex = this.renderIndex;
        info.firstStartTime = this.firstStartTime;
        info.renderStartTime = this.renderStartTime;
        info.renderCostTime = this.renderCostTime;
    }

    public RunningInfo copy() {
        RunningInfo info = new RunningInfo();
        copyTo(info);
        return info;
    }
}