package com.microsoft.sample;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;

public class CosmosHelper {
	
	
	
	private Configurations config = new Configurations();
	
	public JsonNode getMetaData() {
		JsonNode jsonNode = null;
		
		try {
			String date = getDateTime().toLowerCase();
			System.out.println(date);
			String token = getToken(date);
			System.out.println(token);
			String url = config.getProperty("host") + "/addresses/?$resolveFor=dbs&$filter=protocol%20eq%20rntbd";
			
			System.out.println(url);
		
			HttpResponse<JsonNode> response = Unirest.get(url)
												.header("accept", "application//json")
												.header("Content-Type", "application//json")
												.header("x-ms-date", date)
												.header("x-ms-version", "2018-12-31")
												.header("Authorization", token)
												.asJson();
			jsonNode = response.getBody();
		}
		catch(Exception exp) {
			System.out.println(exp.getMessage());
		}
		return jsonNode;
	}
	
	private String getToken(String date) {
		String authToken = "";
		String masterToken = "master";
		String tokenVersion = "1.0";
		//hard coding  to GET
		String verb = "GET";
		String resourceType = "dbs";
		String resourceLink = "";
		String algo = "HMACSHA256";
		try {
			String key = config.getProperty("key");
			System.out.println(key);
			Mac macSha256 = Mac.getInstance(algo);
			SecretKeySpec secretKey = new SecretKeySpec(Base64.decode(key), algo);
			macSha256.init(secretKey);
			String data = verb.toLowerCase() + "\n"
						  	+ resourceType.toLowerCase() + "\n"
						  	+ resourceLink + "\n"
						  	+ date.toLowerCase() + "\n"
						  	+ "" + "\n";
			System.out.println(data);
			String signature = Base64.encode(macSha256.doFinal(data.getBytes("UTF-8")));
			System.out.println("Signature: " + signature);
			String unencodedToken = "type="+ masterToken + "&ver=" + tokenVersion + "&sig=" + signature;
			authToken = URLEncoder.encode(unencodedToken, "UTF-8");
		}
		catch(Exception exp) {
			System.out.println(exp.getMessage());
		}
		return authToken;
	}
	
	private String getDateTime() {
		Instant instant = Instant.now();
		return DateTimeFormatter.RFC_1123_DATE_TIME
		        .withZone(ZoneOffset.UTC)
		        .format(instant);
	}

}
