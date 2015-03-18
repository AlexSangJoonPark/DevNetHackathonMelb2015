package com.cisco.devnet.smartmatress.service;

import java.util.Arrays;
import java.util.List;
import java.util.Queue;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.cisco.devnet.smartmatress.dao.SmartDao;
import com.cisco.devnet.smartmatress.model.CalibratedSensorData;
import com.cisco.devnet.smartmatress.model.SensorData;
import com.cisco.devnet.smartmatress.servlet.InitServlet;

@Path("/smart")
public class SmartService {

	private SmartDao smartDao;

	@GET
	@Path("/calibrate")
	@Produces(MediaType.APPLICATION_JSON)
	public Response calibrate() {

		CalibratedSensorData calibratedData = InitServlet.calibratedData;
		JSONObject jsonObject = new JSONObject();
		if (calibratedData != null) {
			jsonObject.put("time", calibratedData.getDate().toString());
			jsonObject.put("port0", calibratedData.getPort0());
			jsonObject.put("port1", calibratedData.getPort1());
		}

		return Response.status(200).entity(jsonObject.toString()).build();

	}

	@POST
	@Path("/calibrate")
	@Produces(MediaType.APPLICATION_JSON)
	public Response calibratePost() {
		InitServlet.calibratedData = new CalibratedSensorData(InitServlet.getCurrentData());
		CalibratedSensorData calibratedData = InitServlet.calibratedData;
		JSONObject jsonObject = new JSONObject();
		if (calibratedData != null) {
			jsonObject.put("time", calibratedData.getDate().toString());
			jsonObject.put("port0", calibratedData.getPort0());
			jsonObject.put("port1", calibratedData.getPort1());
		}
		return Response.status(200).entity(jsonObject.toString()).build();

	}

	@GET
	@Path("/activate")
	@Produces(MediaType.TEXT_PLAIN)
	public Response activate() {

		return Response.status(200).entity(new Boolean(InitServlet.isActivated).toString()).build();

	}

	@POST
	@Path("/activate")
	@Produces(MediaType.TEXT_PLAIN)
	public Response activatePost() {

		InitServlet.isActivated = !InitServlet.isActivated;
		if (!InitServlet.isActivated) {
			InitServlet.notificationQueue.clear();
		}
		return Response.status(200).entity(new Boolean(InitServlet.isActivated).toString()).build();

	}

	@GET
	@Path("/data")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getData() {

		SensorData currentData = InitServlet.getCurrentData();

		JSONObject jsonObject = new JSONObject();
		if (currentData != null) {
			jsonObject.put("time", currentData.getDate().toString());
			jsonObject.put("port0", currentData.getPort0());
			jsonObject.put("port1", currentData.getPort1());
		}
		return Response.status(200).entity(jsonObject.toString()).build();

	}

	@GET
	@Path("/data_all")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getDataAll() {

		List<SensorData> sensorData = InitServlet.getSensorData();

		JSONArray jsonArray = new JSONArray();
		JSONObject jsonObject = null;
		for (int i = 0; i < sensorData.size(); i++) {
			jsonObject = new JSONObject();
			jsonObject.put("time", sensorData.get(i).getDate().toString());
			jsonObject.put("port0", sensorData.get(i).getPort0());
			jsonObject.put("port1", sensorData.get(i).getPort1());
			jsonArray.add(jsonObject);
		}
		return Response.status(200).entity(jsonArray.toString()).build();

	}
	
	@GET
	@Path("/notification")
	@Produces(MediaType.TEXT_PLAIN)
	public Response getNotification() {
		Queue queue = InitServlet.notificationQueue;
		return Response.status(200).entity(queue.poll()).build();
	}
	

	@GET
	@Path("/clear")
	@Produces(MediaType.APPLICATION_JSON)
	public Response clear() {

		InitServlet.getSensorData().clear();

		return Response.status(200).build();

	}

	@GET
	@Path("/sensitivity")
	@Produces(MediaType.TEXT_PLAIN)
	public Response getSensitivity() {

		return Response.status(200).entity(String.valueOf(InitServlet.sensitivity)).build();

	}
	
	@POST
	@Path("/sensitivity")
	@Produces(MediaType.TEXT_PLAIN)
	public Response setSensitivity(@FormParam("range") String range) {

		try {
			InitServlet.sensitivity = Integer.parseInt(range);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return Response.status(422).entity(range).build();
		}
		
		return Response.status(200).entity(String.valueOf(InitServlet.sensitivity)).build();

	}
	
//	@GET
//	@Path("/createDb")
//	@Produces(MediaType.APPLICATION_JSON)
//	public Response createDb() {
//
//		try {
//			smartDao = new SmartDao("SmartMattress");
//			smartDao.createDb();
//		} catch (Exception e) {
//			e.printStackTrace();
//			return Response.status(500).entity("DB Creation Failed.").build();
//		}
//
//		return Response.status(200).build();
//
//	}
//
//	@GET
//	@Path("/query")
//	@Produces(MediaType.APPLICATION_JSON)
//	public Response query() {
//		List<Object[]> resultList = null;
//		try {
//			if (smartDao == null) {
//				smartDao = new SmartDao("SmartMattress");
//			}
//			resultList = smartDao.query("select * from sample_table");
//		} catch (Exception e) {
//			e.printStackTrace();
//			return Response.status(500).entity("DB query Failed.").build();
//		}
//		JSONArray jsonArray = new JSONArray();
//		for (Object[] array : resultList) {
//			List<Object> asList = Arrays.asList(array);
//			jsonArray.add(asList);
//		}
//
//		return Response.status(200).entity(jsonArray.toJSONString()).build();
//
//	}
//
//	@GET
//	@Path("/closeDb")
//	@Produces(MediaType.APPLICATION_JSON)
//	public Response closeDb() {
//		try {
//			if (smartDao == null) {
//				smartDao = new SmartDao("SmartMattress");
//			}
//			smartDao.shutdown();
//		} catch (Exception e) {
//			e.printStackTrace();
//			return Response.status(500).entity("DB close Failed.").build();
//		}
//
//		return Response.status(200).build();
//
//	}

}
