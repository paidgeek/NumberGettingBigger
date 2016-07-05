package com.moybl.numbergettingbigger;

public class FixedTimestepLoop implements Runnable {

	public interface OnLoopListener {
		void onLoop();
	}

	private Thread mThread;
	private OnLoopListener mOnLoopListener;
	private boolean mIsRunning;
	private int mTargetUPS;

	public FixedTimestepLoop(OnLoopListener onLoopListener, int targetUPS) {
		mOnLoopListener = onLoopListener;
		mTargetUPS = targetUPS;
	}

	public void start() {
		mThread = new Thread(this, "FixedTimestepLoop");
		mThread.start();
	}

	public void stop() {
		mIsRunning = false;
		try {
			mThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		long lastTime = System.nanoTime();
		double delta = 0;
		double ns = 1000000000.0 / mTargetUPS;
		long timer = System.currentTimeMillis();
		int frames = 0;
		int updates = 0;

		while (mIsRunning) {
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;

			if (delta >= 1) {
				mOnLoopListener.onLoop();

				updates++;
				delta--;
			}

			/*
			frames++;
			if (System.currentTimeMillis() - timer > 1000) {
				timer += 1000;
				System.out.println(updates + "ups, " + frames + " fps");
				updates = 0;
				frames = 0;
			}*/
		}
	}

}
