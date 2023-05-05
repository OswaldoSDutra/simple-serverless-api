package com.serverless.handler;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.spec.PutItemSpec;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.serverless.message.ApiGatewayResponse;
import com.serverless.message.ResponseIn;
import com.serverless.config.DataBaseTables;
import com.serverless.dto.ItemDTO;
import com.serverless.message.ResponseOut;
import org.apache.logging.log4j.LogManager;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import org.apache.logging.log4j.Logger;
import org.json.simple.parser.ParseException;

public class InsertItemHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {
    private final Logger logger = LogManager.getLogger(InsertItemHandler.class);

    int statusCode = 200;
    ResponseOut responseOut;
    ResponseIn responseIn;

    @Override
    public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
        logger.info("received: {}", input);

        try {
            responseOut = createItem(input);
        }
        catch (Exception ex){
            this.statusCode = 500;
            logger.error("Não foi possível criar um novo item : " + ex.getMessage());
            responseIn = new ResponseIn("Não foi possível criar um novo item.", input);
         }

        return ApiGatewayResponse.builder()
                .setStatusCode(this.statusCode)
                .setObjectBody(responseOut == null ? responseIn : responseOut)
                .build();
    }

    public ResponseOut createItem(Map<String, Object> event) throws ParseException, JsonProcessingException {

        JSONParser parser = new JSONParser();
        ObjectMapper objectMapper = new ObjectMapper();

        String bodyString = event.get("body").toString();

        JSONObject bodyJson = (JSONObject) parser.parse(bodyString);

        String createdAt = ZonedDateTime.now(ZoneId.of("America/Sao_Paulo")).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        String itemId = UUID.randomUUID().toString().replace("-", "");

        ItemDTO itemDTO = new ItemDTO(itemId, bodyJson.get("item").toString(), createdAt, false);

        AmazonDynamoDB dynamoDBBuilder = AmazonDynamoDBClientBuilder.defaultClient();
        DynamoDB dynamoDbClient = new DynamoDB(dynamoDBBuilder);

        dynamoDbClient.getTable(DataBaseTables.ITEM_TABLE.toString())
                .putItem(
                        new PutItemSpec().withItem(
                                new Item()
                                        .withString("id", itemDTO.getId())
                                        .withString("item", itemDTO.getItem())
                                        .withString("createdAt", itemDTO.getCreatedAt())
                                        .withBoolean("itemStatus", itemDTO.getItemStatus())
                        )
                );

        Map<String, Object> mappedItem = objectMapper.convertValue(itemDTO, Map.class);

        List<JSONObject> responseBody = new ArrayList<>();
        responseBody.add(new JSONObject(mappedItem));
        return new ResponseOut("Item criado com sucesso!", responseBody);
    }
}
