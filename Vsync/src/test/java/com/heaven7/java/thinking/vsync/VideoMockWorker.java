package com.heaven7.java.thinking.vsync;

public class VideoMockWorker implements Worker {

    @Override
    public void onCancel() {
        System.out.println("Video: onCancel");
    }
    @Override
    public void onStart() {
        System.out.println("Video: onStart");
    }
}
