package com.serverless.handler;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.serverless.dto.ItemDTO;
import com.serverless.message.ApiGatewayResponse;
import com.serverless.message.ResponseIn;
import com.serverless.message.ResponseOut;
import com.serverless.config.DataBaseTables;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class GetItemsHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

    private final Logger logger = LogManager.getLogger(GetItemsHandler.class);

    int statusCode = 200;
    ResponseOut responseOut;
    ResponseIn responseIn;

    @Override
    public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
        logger.info("received: {}", input);

        try {
            responseOut = getItens(input);
        }
        catch (Exception ex){
            this.statusCode = 500;
            logger.error("Não foi possível recuperar itens : " + ex.getMessage());
            responseIn = new ResponseIn("Não foi possível recuperar itens.", input);
        }

        return ApiGatewayResponse.builder()
                .setStatusCode(this.statusCode)
                .setObjectBody(responseOut == null ? responseIn : responseOut)
                .build();
    }

    public ResponseOut getItens(Map<String, Object> event) throws ParseException, JsonProcessingException {


        AmazonDynamoDB dynamoDBBuilder = AmazonDynamoDBClientBuilder.defaultClient();
        DynamoDB dynamoDbClient = new DynamoDB(dynamoDBBuilder);

        Table table = dynamoDbClient.getTable(DataBaseTables.ITEM_TABLE.toString());

        ItemCollection<ScanOutcome> items = table.scan();
        Iterator<Item> iterator = items.iterator();

        ObjectMapper objectMapper = new ObjectMapper();
        List<JSONObject> responseBody = new ArrayList<>();
        while (iterator.hasNext()) {
            Map<String, Object> mappedItem = objectMapper.readValue(iterator.next().toJSON(), new TypeReference<Map<String, Object>>(){});
            responseBody.add(new JSONObject(mappedItem));
        }

        return new ResponseOut("Itens recuperados com sucesso!", responseBody);
    }


}
