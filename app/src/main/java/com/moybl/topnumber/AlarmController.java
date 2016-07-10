package com.moybl.topnumber;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.moybl.topnumber.backend.TopNumberClient;

import java.util.List;

public class AlarmController extends WakefulBroadcastReceiver {

	public static final String KEY_REQUEST_CODE = "request_code";
	public static final String KEY_PLAYER_ID = "player_id";
	public static final int SETUP_NOTIFICATION = 1;
	public static final int SCHEDULE_SETUP = 2;

	@Override
	public void onReceive(Context context, Intent intent) {
		switch (intent.getIntExtra(KEY_REQUEST_CODE, 0)) {
			case SCHEDULE_SETUP:
				scheduleSetup(context, intent);
				break;
			case SETUP_NOTIFICATION:
				setupNotification(context, intent);
				break;
		}
	}

	private void scheduleSetup(Context context, Intent intent) {
		//String playerId = intent.getStringExtra(KEY_PLAYER_ID);
		//long notifyTime = calculateNotifyTime(context, playerId);

		// rate * seconds = lastCost - number
		// seconds = (lastCost - number) / rate

		Intent service = new Intent(context, AlarmController.class);
		service.putExtra(AlarmController.KEY_REQUEST_CODE, AlarmController.SETUP_NOTIFICATION);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, AlarmController.SETUP_NOTIFICATION, service, 0);

		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 10000, pendingIntent);

		startWakefulService(context, service);
	}

	private long calculateNotifyTime(Context context, String playerId){
		Prefs.load(context, playerId);
		NumberData numberData = NumberData.getInstance();
		numberData.load();

		List<Source> sources = numberData.getSources();
		double rate = numberData.getRate();
		double lastCost = 0.0;

		for (int i = 0; i < sources.size(); i++) {
			Source s = sources.get(i);

			if (!s.isUnlocked()) {
				lastCost = s.getCost();
				break;
			}
		}

		return System.currentTimeMillis() + (long) Math.ceil(((lastCost - numberData.getNumber()) / rate) * 1000.0);
	}

	private void setupNotification(Context context, Intent intent) {
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

	public static void cancelNotificationSetup(Context context) {
		PendingIntent pendingIntent = createScheduleSetupIntent(context);

		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(pendingIntent);
	}

	public static void scheduleNotificationSetup(Context context) {
		PendingIntent pendingIntent = createScheduleSetupIntent(context);

		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 3000, pendingIntent);
	}

	private static PendingIntent createScheduleSetupIntent(Context context) {
		Intent intent = new Intent(context, AlarmController.class);
		intent.putExtra(KEY_PLAYER_ID, TopNumberClient.getInstance()
				.getPlayer()
				.getId());
		intent.putExtra(KEY_REQUEST_CODE, SCHEDULE_SETUP);

		return PendingIntent.getBroadcast(context, SCHEDULE_SETUP, intent, 0);
	}

}
