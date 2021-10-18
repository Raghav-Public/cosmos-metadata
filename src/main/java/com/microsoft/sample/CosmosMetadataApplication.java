package com.microsoft.sample;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import kong.unirest.JsonNode;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;

public class CosmosMetadataApplication {

	public static void main(String[] args) {
		Configurations config = new Configurations();
		String host = "";
		String key = "";
		String databaseRid = "";
		String collectionRid = "";
		
		
		try {
			host = config.getProperty("host");
			key = config.getProperty("key");
			databaseRid = config.getProperty("dbRid");
			collectionRid = config.getProperty("colRid");
			
			JSONObject accountDetails = null;
			JsonNode databaseResponse = null;
			
			JSONArray readRegions = null;
			JSONArray writeRegions = null;
			List<JsonNode> allEndpoints = new ArrayList<JsonNode>();
		
			CosmosHelper cosmosHelper = new CosmosHelper(host, key, databaseRid, collectionRid);
			accountDetails = cosmosHelper.getAccountDetails();
			
			System.out.print(System.lineSeparator());
			System.out.println("############Account Details##########");
			System.out.println(accountDetails.toString());
			System.out.println("############End Account Details##########");
			System.out.print(System.lineSeparator());
			
			if(accountDetails != null) {
				readRegions = accountDetails.getJSONArray("readableLocations");
				writeRegions = accountDetails.getJSONArray("writableLocations");
			}
			
			
			databaseResponse = cosmosHelper.getAllDatabases();
			System.out.println(databaseResponse.toPrettyString());
			JSONObject databaseJsonObject =  databaseResponse.getObject();
			
			if(databaseJsonObject != null) {
				List<Database> dbs = cosmosHelper.castDatabases(databaseJsonObject.getJSONArray("Databases"));
				for(int i=0;i<dbs.size();i++) {
					JsonNode collectionResponse = cosmosHelper.getAllCollections(dbs.get(i));
					JSONObject collectionJsonObject = collectionResponse.getObject();
					if(collectionJsonObject != null) {
						List<Collection> colls = cosmosHelper.castCollections(collectionJsonObject.getJSONArray("DocumentCollections"));
						for(int j =0;j<colls.size();j++) {
							for(int k=0;k<readRegions.length();k++) {
								JSONObject readRegion = readRegions.getJSONObject(k);
								JsonNode responseData = cosmosHelper.getMetaData(readRegion.getString("databaseAccountEndpoint"),dbs.get(i).getRid(), colls.get(j).getRid());
								allEndpoints.add(responseData);
							}
						}
					}
				}
			}
			for(JsonNode endpoint: allEndpoints) {
				System.out.println(endpoint.toPrettyString());
			}	
		}
		catch(Exception exp) {
			System.out.println(exp.getMessage());
		}
		
		
		
	}
	
	

}
