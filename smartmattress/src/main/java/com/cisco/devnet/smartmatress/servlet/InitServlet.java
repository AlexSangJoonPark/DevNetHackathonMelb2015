package com.cisco.devnet.smartmatress.servlet;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.cisco.devnet.smartmatress.model.CalibratedSensorData;
import com.cisco.devnet.smartmatress.model.SensorData;
import com.cisco.devnet.smartmatress.service.BizRule;
import com.cisco.devnet.smartmatress.service.SmsService;

/**
 * Servlet implementation class InitServlet
 */
public class InitServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static String dmoUrl = null;

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);

		System.out.println("initialze....");
		dmoUrl = config.getInitParameter("url");
		System.out.println(dmoUrl);
	}

	public String ReadInputStreamAsString(InputStream in) throws IOException {

		BufferedInputStream bis = new BufferedInputStream(in);
		ByteArrayOutputStream buf = new ByteArrayOutputStream();
		int result = bis.read();
		while (result != -1) {
			byte b = (byte) result;
			buf.write(b);
			result = bis.read();
		}
		return buf.toString();
	}

	public static String register_id = ""; // googleapi id to send sms msg to the device
	public static int sensitivity = 5; // 1~10
	public static CalibratedSensorData calibratedData = null;
	public static boolean isActivated = false;
	private static int maxNumber = 30;
	private static List<SensorData> sensorData = new ArrayList<SensorData>();
	public static Queue<String> notificationQueue = new ConcurrentLinkedQueue<String>();

	public static SensorData getCurrentData() {
		return sensorData.get(sensorData.size()-1);
	}

	public static List<SensorData> getSensorData() {
		return sensorData;
	}

	/**
	 * Default constructor.
	 */
	public InitServlet() {

		Runnable collectDataRunnable = new Runnable() {
			public void run() {
				System.out.println("Thread Started..");
				System.out.println(dmoUrl);

				HttpURLConnection connection = null;
				while (true) {
					try {

						URL url = new URL(dmoUrl);

						connection = (HttpURLConnection) url.openConnection();
						connection.setRequestMethod("GET");
						connection.setRequestProperty("Accept", "application/json");
						InputStream xmlResponseStream = connection.getInputStream();
						String Response = ReadInputStreamAsString(xmlResponseStream);
						JSONParser parser = new JSONParser();
						Object obj = parser.parse(Response);
						JSONObject jsonObject = (JSONObject) obj;
						obj = parser.parse(Response);
						String analog = (String) jsonObject.get("ANALOG").toString();
						obj = parser.parse(analog);
						jsonObject = (JSONObject) obj;
						String port0 = (String) jsonObject.get("Port0").toString();
						jsonObject = (JSONObject) obj;
						String port1 = (String) jsonObject.get("Port1").toString();
						System.out.println("Port0: " + port0);
						System.out.println("Port1: " + port1);

						if (sensorData.size() >= maxNumber)
							sensorData.remove(0);
						SensorData data = new SensorData(new Date(), Integer.parseInt(port0), Integer.parseInt(port1));
						sensorData.add(data);

						if (isActivated && calibratedData != null) {
							BizRule.checkNotificationRule(data, calibratedData, sensitivity, notificationQueue);
						}
						if (!"".equals(register_id) && notificationQueue.size() > 0) {
							new SmsService().sendSmsMessage(register_id, notificationQueue.poll());
						}
						
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					} catch (IOException e) {
						e.printStackTrace();
					} catch (ParseException e) {
						e.printStackTrace();
					} finally {
						if (connection != null) {
							connection.disconnect();
						}
					}
				}
			}
		};
		Thread t = new Thread(collectDataRunnable);
		t.start();

	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
