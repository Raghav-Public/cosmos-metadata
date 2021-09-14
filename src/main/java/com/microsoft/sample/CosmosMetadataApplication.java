package com.microsoft.sample;


import kong.unirest.JsonNode;

public class CosmosMetadataApplication {

	public static void main(String[] args) {
		
		System.out.println("hello");
		CosmosHelper cosmosHelper = new CosmosHelper();
		
		JsonNode responseData = cosmosHelper.getMetaData();
		System.out.println(responseData.toPrettyString());
	}

}
