package com.serverless.handler;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.serverless.dto.ItemDTO;
import com.serverless.message.ApiGatewayResponse;
import com.serverless.message.ResponseIn;
import com.serverless.config.DataBaseTables;
import com.serverless.message.ResponseOut;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GetItemHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

    private final Logger logger = LogManager.getLogger(GetItemHandler.class);

    int statusCode = 200;
    ResponseOut responseOut;
    ResponseIn responseIn;

    @Override
    public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
        logger.info("received: {}", input);

        try {
            responseOut = getItem(input);
        }
        catch (Exception ex){
            this.statusCode = 500;
            logger.error("Não foi possível recuperar o item : " + ex.getMessage());
            responseIn = new ResponseIn("Não foi possível recuperar o item.", input);
        }

        return ApiGatewayResponse.builder()
                .setStatusCode(this.statusCode)
                .setObjectBody(responseOut == null ? responseIn : responseOut)
                .build();
    }

    public ResponseOut getItem(Map<String, Object> event) throws ParseException, JsonProcessingException {

        Item dynamoDbItem;

        Map<String, Object> pathParameters = (Map<String, Object>) event.get("pathParameters");

        String id = pathParameters.get("id").toString();

        AmazonDynamoDB dynamoDBBuilder = AmazonDynamoDBClientBuilder.defaultClient();
        DynamoDB dynamoDbClient = new DynamoDB(dynamoDBBuilder);

        dynamoDbItem = dynamoDbClient.getTable(DataBaseTables.ITEM_TABLE.toString())
                      .getItem("id", id);

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> mappedItem = objectMapper.readValue(dynamoDbItem.toJSON(), new TypeReference<Map<String,Object>>(){});

        List<JSONObject> responseBody = new ArrayList<>();
        responseBody.add(new JSONObject(mappedItem));
        return new ResponseOut("Item recuperado com sucesso!", responseBody);
    }

}
