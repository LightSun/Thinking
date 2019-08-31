package com.heaven7.java.thinking.vsync;

public class AudioMockWorker implements Worker {

    @Override
    public void onCancel() {
        System.out.println("Audio: onCancel");
    }
    @Override
    public void onStart() {
        System.out.println("Audio: onStart");
    }
}
