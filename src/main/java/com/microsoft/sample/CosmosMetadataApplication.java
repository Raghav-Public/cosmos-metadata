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
		
		String host = "";
		String key = "";
		JSONObject accountDetails = null;
		JSONArray readRegions = null;
		JSONArray writeRegions = null;
		List<JsonNode> allEndpoints = new ArrayList<JsonNode>();
		
		if(args.length < 2) {
			System.out.println("Please provide host and key as arguments");
		}
		else  {
			host = args[0];
			key = args[1];
			
			CosmosHelper cosmosHelper = new CosmosHelper(host, key);
			accountDetails = cosmosHelper.getAccountDetails("");
			System.out.print(System.lineSeparator());
			System.out.println("############Account Details##########");
			System.out.println(accountDetails.toString());
			System.out.println("############End Account Details##########");
			System.out.print(System.lineSeparator());
			
			if(accountDetails != null) {
				readRegions = accountDetails.getJSONArray("readableLocations");
				writeRegions = accountDetails.getJSONArray("writableLocations");
				System.out.print(System.lineSeparator());
				System.out.println("############Read Regions##########");
				System.out.println("Read Regions:");
				System.out.println(readRegions);
				System.out.println("############END Read Regions##########");
				System.out.print(System.lineSeparator());
				System.out.println("############Write Regions##########");
				System.out.println("Write Regions:");
				System.out.println(writeRegions);
				System.out.println("############End Write Regions##########");
				System.out.print(System.lineSeparator());
				for(int i=0;i<writeRegions.length();i++) {
					readRegions.put(writeRegions.get(i));
				}
				if(readRegions != null) {
					for(int i=0;i<readRegions.length();i++) {
						JSONObject readRegion = readRegions.getJSONObject(i);
						JsonNode responseData = cosmosHelper.getMetaData(readRegion.getString("databaseAccountEndpoint"), "dbs");
						System.out.println(responseData.toPrettyString());
						allEndpoints.add(responseData);
					}
				}
			}
			for(JsonNode endpoint: allEndpoints) {
				System.out.println(endpoint.toPrettyString());
			}
			
			/* */
		}
	}

}
