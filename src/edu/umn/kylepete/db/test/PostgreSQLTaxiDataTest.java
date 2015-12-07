package edu.umn.kylepete.db.test;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.umn.kylepete.TaxiSystemProperties;
import edu.umn.kylepete.db.PostgreSQLTaxiData;
import edu.umn.kylepete.env.OSRM;
import edu.umn.kylepete.env.Request;

public class PostgreSQLTaxiDataTest {

	private static TaxiSystemProperties props;

	@BeforeClass
	public static void setUpClass() throws IOException, ParseException {
		props = new TaxiSystemProperties("taxisystem.properties");
		OSRM.hostname = props.getOsrmHost();
		OSRM.port = props.getOsrmPort();
	}

	@Test
	public void testGetNextAndPaging() throws ParseException {
		int testPageSize = 5;
		int testResultSize = 3 * testPageSize; // this should get 3 pages

		// first get the results at 100 percent
		List<Request> db1Requests = new ArrayList<Request>();
		PostgreSQLTaxiData db1 = new PostgreSQLTaxiData(props.getDbUrl(), props.getDbUser(), props.getDbPassword(), new Timestamp(props.getTimeStart().getTime()), 100, testPageSize);
		for (int i = 0; i < testResultSize; i++) {
			Request r = db1.getNextRequest();
			Assert.assertNotNull(r);
			db1Requests.add(r);
		}

		// next get the results at 10 percent
		List<Request> db2Requests = new ArrayList<Request>();
		PostgreSQLTaxiData db2 = new PostgreSQLTaxiData(props.getDbUrl(), props.getDbUser(), props.getDbPassword(), new Timestamp(props.getTimeStart().getTime()), 10, testPageSize);
		for (int i = 0; i < testResultSize; i++) {
			Request r = db2.getNextRequest();
			Assert.assertNotNull(r);
			db2Requests.add(r);
		}

		Assert.assertTrue(db1Requests.get(0).getSubmitTime().compareTo(props.getTimeStart()) >= 0);
		Assert.assertTrue(db2Requests.get(0).getSubmitTime().compareTo(props.getTimeStart()) >= 0);

		for (int i = 1; i < testResultSize; i++) {
			Assert.assertTrue(db1Requests.get(i - 1).getSubmitTime().compareTo(db1Requests.get(i).getSubmitTime()) <= 0);
			Assert.assertTrue(db2Requests.get(i - 1).getSubmitTime().compareTo(db2Requests.get(i).getSubmitTime()) <= 0);
		}

		Assert.assertEquals(db1Requests.get(9), db2Requests.get(0));
	}

	@Test
	public void testStartDate() throws ParseException {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date start = dateFormat.parse("2013-1-30 01:02:03");
		PostgreSQLTaxiData db = new PostgreSQLTaxiData(props.getDbUrl(), props.getDbUser(), props.getDbPassword(), new Timestamp(start.getTime()), 10, 2);
		Request r = db.getNextRequest();
		Assert.assertNotNull(r);
		Assert.assertTrue(r.getSubmitTime().compareTo(start) >= 0);
	}

	@Test
	public void testNoResults() throws ParseException {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date start = dateFormat.parse("2020-1-30 01:02:03");
		PostgreSQLTaxiData db = new PostgreSQLTaxiData(props.getDbUrl(), props.getDbUser(), props.getDbPassword(), new Timestamp(start.getTime()), 10, 2);
		Request r = db.getNextRequest();
		Assert.assertNull(r);
		r = db.getNextRequest();
		Assert.assertNull(r);
	}
}
