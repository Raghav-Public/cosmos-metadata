package com.microsoft.sample;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.ObjectMapper;
import kong.unirest.Unirest;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;

public class CosmosHelper {
	
	private String host;
	private String key;
	//private String db;
	//private String col;
	
	public CosmosHelper(String host, String key, String dbRid, String colRid) {
		this.host = host;
		this.key = key;
		//this.db = dbRid;
		//this.col = colRid;
	}
	
	//private Configurations config = new Configurations();
	
	public JSONObject getAccountDetails() {
		String date = getServerTime();
		String token = generate("GET","","",key,"master","1.0",date);
		JSONObject accountDetails = null;
		JsonNode jsonNode = get(this.host, token, date);
		if(jsonNode != null) {
			accountDetails = jsonNode.getObject();
		}
		return accountDetails;
	}
	
	public JsonNode getAllDatabases() {
		String resourceType = "dbs";
		String resourceId = "";
		String resolvefor = "dbs/";
		String date = getServerTime();
		String token = generate("GET", resourceType, resourceId, key,"master","1.0",date);
		String url = this.host + resolvefor;
		return get(url, token, date);
	}
	
	public JsonNode getAllCollections(Database db) {
		String resourceType = "colls";
		String resourceId = db.getRid();
		String resolvefor = "dbs/"+URLEncoder.encode(db.getRid())+"/colls";
		String date = getServerTime();
		String token = generate("GET", resourceType, resourceId, key,"master","1.0",date);
		String url = this.host + resolvefor;
		return get(url, token, date);
	}
	
	public JsonNode getMetaData(String regionHost, String db, String col) {
		String resourceType = "docs";
		String resourceId = col;
		String resolvefor = "dbs/"+URLEncoder.encode(db)+"/colls/"+URLEncoder.encode(col)+"/docs";
		String date = getServerTime();
		String token = generate("GET", resourceType, resourceId, key,"master","1.0",date);
		String url = regionHost + "/addresses/?$resolveFor="+ resolvefor +"&$filter=protocol%20eq%20rntbd";
		return get(url, token, date);
	}
	
	private JsonNode get(String url, String token, String date) {
		JsonNode jsonNode = null;
		System.out.println(url);
		try {
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
	
	
	public static String generate(String verb, String resourceType, String resourceId, String key, String keyType, String tokenVersion, String date)
    {
         String authorization = null;
         System.out.println(date);
         String payload=verb.toLowerCase()+"\n"
         +resourceType.toLowerCase()+"\n"
         +resourceId.toLowerCase()+"\n"
         +date.toLowerCase()+"\n"
         +""+"\n";
        System.out.println(payload); 
        Mac sha256_HMAC;
        try {
            sha256_HMAC = Mac.getInstance("HmacSHA256");
         
         SecretKeySpec secret_key = new SecretKeySpec(Base64.decode(key), "HmacSHA256");
         sha256_HMAC.init(secret_key);
         String signature = Base64.encode(sha256_HMAC.doFinal(payload.getBytes("UTF-8")));
         authorization=URLEncoder.encode("type="+keyType+"&ver="+tokenVersion+"&sig="+signature, "utf-8");
         System.out.println(authorization);
        }catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (IllegalStateException e) {
            e.printStackTrace();
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return authorization;
    }	
	private static String getServerTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return dateFormat.format(calendar.getTime());
    }
	
	public List<Database> castDatabases(JSONArray jdbs) {
		List<Database> dbs = new ArrayList<Database>();
		
		for(int i=0;i<jdbs.length();i++) {
			JSONObject jdb = jdbs.getJSONObject(i);
			dbs.add(new Database(jdb.getString("id"), jdb.getString("_rid")));
		}
		return dbs;
	}
	public List<Collection> castCollections(JSONArray jcolls) {
		List<Collection> colls = new ArrayList<Collection>();
		
		for(int i=0;i<jcolls.length();i++) {
			JSONObject jcoll = jcolls.getJSONObject(i);
			colls.add(new Collection(jcoll.getString("id"), jcoll.getString("_rid")));
		}
		return colls;
	}
	
}
