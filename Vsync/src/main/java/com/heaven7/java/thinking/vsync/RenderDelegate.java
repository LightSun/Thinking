package com.heaven7.java.thinking.vsync;

public interface RenderDelegate {

    void onCancel();
    void onStart();

    void render(Worker worker, RunningInfo info);
}
