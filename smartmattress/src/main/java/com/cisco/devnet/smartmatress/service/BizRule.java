package com.cisco.devnet.smartmatress.service;

import java.util.Queue;

import com.cisco.devnet.smartmatress.model.CalibratedSensorData;
import com.cisco.devnet.smartmatress.model.SensorData;

public class BizRule {

	private static int movements = 0;

	public static void checkNotificationRule(SensorData data, CalibratedSensorData calibratedData, int sensitivity,
			Queue<String> notificationQueue) {

		long port0 = data.getPort0();
		long port1 = data.getPort1();

		if (port0 == 0 && port1 == 0) {
			notificationQueue.add("Vacated!");
			System.out.println(notificationQueue.peek());
			movements = 0;
		} else if (hasMoved(port0, calibratedData.getPort0(), sensitivity)
				|| hasMoved(port1, calibratedData.getPort1(), sensitivity)) {
			if (sensitivity < ++movements) {
				notificationQueue.add("Movement Detected!");
				System.out.println(notificationQueue.peek());
				movements = 0;
			}
		}
	}

	public static boolean hasMoved(long a, long b, int sensitivity) {

		long difference = Math.abs(a - b);

		return difference > 1000 || difference / 100 > sensitivity;
	}

}
