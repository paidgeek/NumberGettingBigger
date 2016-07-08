package com.moybl.topnumber;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.WakefulBroadcastReceiver;

public class AlarmReceiver extends WakefulBroadcastReceiver {

	public static final String KEY_REQUEST_CODE = "request_code";
	public static final int SETUP_NOTIFICATION = 1;
	public static final int SCHEDULE_SETUP = 2;

	@Override
	public void onReceive(Context context, Intent intent) {
		switch (intent.getIntExtra(KEY_REQUEST_CODE, 0)) {
			case SCHEDULE_SETUP:
				scheduleSetup(context);
				break;
			case SETUP_NOTIFICATION:
				setupNotification(context);
				break;
		}
	}

	private void scheduleSetup(Context context) {
		Intent service = new Intent(context, AlarmReceiver.class);
		service.putExtra(AlarmReceiver.KEY_REQUEST_CODE, AlarmReceiver.SETUP_NOTIFICATION);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, AlarmReceiver.SETUP_NOTIFICATION, service, PendingIntent.FLAG_UPDATE_CURRENT);

		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 1000, pendingIntent);

		startWakefulService(context, service);
	}

	private void setupNotification(Context context) {
		Uri sound = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.notification);

		NotificationCompat.Builder mBuilder =
				new NotificationCompat.Builder(context)
						.setSmallIcon(R.drawable.logo)
						.setContentTitle(context.getResources()
								.getString(R.string.app_name))
						.setContentText(context.getResources()
								.getString(R.string.notification_text))
						.setAutoCancel(true)
						.setSound(sound)
						.setVibrate(new long[]{0, 100, 200, 300});
		Intent service = new Intent(context, LogInActivity.class);
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
		stackBuilder.addParentStack(LogInActivity.class);
		stackBuilder.addNextIntent(service);
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(resultPendingIntent);
		NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(1, mBuilder.build());

		startWakefulService(context, service);
	}

}
