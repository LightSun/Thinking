package com.heaven7.java.thinking.vsync;

public class VideoRender implements RenderDelegate {

    @Override
    public void onCancel() {
        System.out.println("VideoRender: onCancel");
    }

    @Override
    public void onStart() {
        System.out.println("VideoRender: onStart");
    }
    @Override
    public void render(Worker worker, RunningInfo info) {
        System.out.println("VideoRender: render done. index = " + info.renderIndex);
    }
}
