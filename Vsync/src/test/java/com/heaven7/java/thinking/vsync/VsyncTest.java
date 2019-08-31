package com.heaven7.java.thinking.vsync;

public final class VsyncTest {

    public static void main(String[] args) {
        Worker[] workers = {
                new VideoMockWorker(),
                new AudioMockWorker(),
        };
        RenderDelegate[] renders = {
                new VideoRender(),
                new AudioRender(),
        };
        Vsync vsync = new Vsync(30, workers, renders, null);
        vsync.startIfNeed();
        //vsync.join();
    }
}
