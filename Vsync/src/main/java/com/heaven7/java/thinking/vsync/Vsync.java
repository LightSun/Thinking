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

        long cost, oldStartTime;
        long st;
        try {
            while (!mCanceled.get()) {
                System.out.println("start Render at time: " + startTime);
                for (int i = 0; i < size; i++) {
                    st = getCurrentTime();
                    hs[i].render(workers[i], mTempInfo);
                    mTempInfo.renderCostTime = getCurrentTime() - st;
                    mLog.logRender(workers[i], mTempInfo);
                }
                oldStartTime = startTime;
                startTime += delay;
                mRunInfo.renderIndex ++;
                mTempInfo.renderIndex = mRunInfo.renderIndex;
                mTempInfo.renderStartTime = mRunInfo.renderStartTime = startTime;
                cost = getCurrentTime() - oldStartTime;
                mTempInfo.lastVsyncCost = mRunInfo.lastVsyncCost = cost;
                if (cost >= delay) {
                    mLog.logWarn(mTempInfo, "render cost to much time. cost = " + cost + " ,expect delay = " + delay);
                } else {
                    Thread.sleep(delay - cost);
                }
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
    public boolean isCancelled(){
        return mCanceled.get();
    }
    public void startIfNeed(){
        if(isCancelled()){
            start();
        }
    }
    public void join(){
        try {
            mThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
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
