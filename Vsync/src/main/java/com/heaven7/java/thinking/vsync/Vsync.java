package com.heaven7.java.thinking.vsync;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 1, sdk发送帧播放到多少的命令。.上层平滑.
 * 2， 混音。
 */
public final class Vsync implements Runnable {

    public interface LogCallback {
        void logWarn(RunningInfo info, String msg);
        void logRender(Worker worker, RunningInfo info);
    }

    private final Worker[] workers;
    private final RenderDelegate[] hs;
    private final int fps;
    private final LogCallback mLog;

    private final RunningInfo mRunInfo = new RunningInfo();
    private final RunningInfo mTempInfo = new RunningInfo();
    private final AtomicBoolean mCanceled = new AtomicBoolean(true);
    private Thread mThread;

    public Vsync(int fps, Worker[] workers, RenderDelegate[] hs, LogCallback callback) {
        if(workers.length != hs.length){
            throw new IllegalArgumentException();
        }
        this.fps = fps;
        this.workers = workers;
        this.hs = hs;
        this.mLog = callback != null ? callback : EMPTY;

        this.mThread = new Thread(this);
    }

    @Override
    public void run() {
        long startTime = getCurrentTime();
        mRunInfo.renderStartTime = startTime;
        mRunInfo.firstStartTime = startTime;
        mRunInfo.renderIndex = 0;
        final long delay = 1000 / fps; //ms
        final int size = hs.length;

        long cost;
        long st;
        try {
            while (mCanceled.get()) {
                for (int i = 0; i < size; i++) {
                    st = getCurrentTime();
                    hs[i].render(workers[i], mTempInfo);
                    mTempInfo.renderCostTime = getCurrentTime() - st;
                    mLog.logRender(workers[i], mTempInfo);
                }
                cost = getCurrentTime() - startTime;
                if (cost >= delay) {
                    mLog.logWarn(mTempInfo, "render cost to much time. ");
                } else {
                    Thread.sleep(delay - cost);
                }
                startTime += delay;
                mRunInfo.renderIndex ++;
                mTempInfo.renderIndex = mRunInfo.renderIndex;
                mTempInfo.renderStartTime = mRunInfo.renderStartTime = startTime;
                mTempInfo.lastVsyncCost = mRunInfo.lastVsyncCost = cost;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void cancel(){
        if(mCanceled.compareAndSet(false, true)){
            mThread.interrupt();
            mThread = null;
            for (int i = 0, size = hs.length; i < size; i++) {
                hs[i].onCancel();
                workers[i].onCancel();
            }
        }
    }

    public void start() {
        if(mCanceled.compareAndSet(true, false)){
            for (int i = 0, size = hs.length; i < size; i++) {
                hs[i].onStart();
                workers[i].onStart();
            }
            if(mThread == null){
                mThread = new Thread(this);
            }
            mThread.start();
        }
    }

    protected long getCurrentTime() {
        return System.currentTimeMillis();
    }

    private static class EmptyLogCallback implements LogCallback {
        @Override
        public void logWarn(RunningInfo info, String msg) {

        }
        @Override
        public void logRender(Worker worker, RunningInfo info) {

        }
    };
    private static final EmptyLogCallback EMPTY = new EmptyLogCallback();

}
