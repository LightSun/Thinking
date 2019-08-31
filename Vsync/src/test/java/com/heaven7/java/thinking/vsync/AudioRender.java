package com.heaven7.java.thinking.vsync;

public class AudioRender implements RenderDelegate {

    @Override
    public void onCancel() {
        System.out.println("AudioRender: onCancel");
    }

    @Override
    public void onStart() {
        System.out.println("AudioRender: onStart");
    }
    @Override
    public void render(Worker worker, RunningInfo info) {
        System.out.println("AudioRender: render done. index = " + info.renderIndex);
    }
}
