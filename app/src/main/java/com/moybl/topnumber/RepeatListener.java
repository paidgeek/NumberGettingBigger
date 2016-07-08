package com.moybl.topnumber;

import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;

// Credit: http://stackoverflow.com/questions/4284224/android-hold-button-to-repeat-action

/**
 * A class, that can be used as a TouchListener on any view (e.g. a Button). It cyclically runs a
 * clickListener, emulating keyboard-like behaviour. First click is fired immediately, next one
 * after the initialInterval, and subsequent ones after the normalInterval.
 *
 * <p>Interval is scheduled after the onClick completes, so it has to run fast. If it runs slow, it
 * does not generate skipped onClicks. Can be rewritten to achieve this.
 */
public class RepeatListener implements OnTouchListener {

	public interface OnRepeatListener {
		void onFirstClick(View v);

		void onRepeatClick(View v);
	}

	private Handler handler = new Handler();

	private int initialInterval;
	private final int normalInterval;
	private final OnRepeatListener repeatListener;

	private Runnable handlerRunnable = new Runnable() {
		@Override
		public void run() {
			handler.postDelayed(this, normalInterval);
			repeatListener.onRepeatClick(downView);
		}
	};

	private View downView;

	/**
	 * @param initialInterval The interval after first click event
	 * @param normalInterval  The interval after second and subsequent click events
	 * @param repeatListener  The OnRepeatListener, that will be called periodically
	 */
	public RepeatListener(int initialInterval, int normalInterval,
								 OnRepeatListener repeatListener) {
		if (repeatListener == null)
			throw new IllegalArgumentException("null runnable");
		if (initialInterval < 0 || normalInterval < 0)
			throw new IllegalArgumentException("negative interval");

		this.initialInterval = initialInterval;
		this.normalInterval = normalInterval;
		this.repeatListener = repeatListener;
	}

	public boolean onTouch(View view, MotionEvent motionEvent) {
		switch (motionEvent.getAction()) {
			case MotionEvent.ACTION_DOWN:
				handler.removeCallbacks(handlerRunnable);
				handler.postDelayed(handlerRunnable, initialInterval);
				downView = view;
				downView.setPressed(true);
				repeatListener.onFirstClick(view);
				return true;
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_CANCEL:
				cancel();
				return true;
		}

		return false;
	}

	public void cancel() {
		handler.removeCallbacks(handlerRunnable);

		if (downView != null) {
			downView.setPressed(false);
			downView = null;
		}
	}

}