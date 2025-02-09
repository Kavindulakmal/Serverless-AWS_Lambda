package com.example.demo;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LambdaHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static ConfigurableApplicationContext context;
    private UserService userService;
    private ObjectMapper objectMapper = new ObjectMapper();

    public LambdaHandler() {
        if (context == null) {
            context = SpringApplication.run(DemoApplication.class);
        }
        userService = context.getBean(UserService.class);
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        response.setHeaders(headers);

        try {
            String httpMethod = input.getHttpMethod();
            String path = input.getPath();
            String body = input.getBody();

            switch (httpMethod) {
                case "GET":
                    if (path.endsWith("/users")) {
                        // Get all users
                        List<User> users = userService.getAllUsers();
                        response.setStatusCode(200);
                        response.setBody(objectMapper.writeValueAsString(users));
                    } else if (path.matches("/users/\\d+")) {
                        // Get user by ID
                        Long id = Long.parseLong(path.substring(path.lastIndexOf("/") + 1));
                        User user = userService.getUserById(id);
                        if (user != null) {
                            response.setStatusCode(200);
                            response.setBody(objectMapper.writeValueAsString(user));
                        } else {
                            response.setStatusCode(404);
                            response.setBody("User not found");
                        }
                    }
                    break;

                case "POST":
                    if (path.endsWith("/users")) {
                        // Create a new user
                        User newUser = objectMapper.readValue(body, User.class);
                        User createdUser = userService.createUser(newUser);
                        response.setStatusCode(201);
                        response.setBody(objectMapper.writeValueAsString(createdUser));
                    }
                    break;

                case "PUT":
                    if (path.matches("/users/\\d+")) {
                        // Update user by ID
                        Long id = Long.parseLong(path.substring(path.lastIndexOf("/") + 1));
                        User updatedUser = objectMapper.readValue(body, User.class);
                        User result = userService.updateUser(id, updatedUser);
                        response.setStatusCode(200);
                        response.setBody(objectMapper.writeValueAsString(result));
                    }
                    break;

                case "DELETE":
                    if (path.matches("/users/\\d+")) {
                        // Delete user by ID
                        Long id = Long.parseLong(path.substring(path.lastIndexOf("/") + 1));
                        userService.deleteUser(id);
                        response.setStatusCode(204);
                        response.setBody("User deleted successfully");
                    }
                    break;

                default:
                    response.setStatusCode(400);
                    response.setBody("Unsupported HTTP method");
                    break;
            }
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setBody("Internal Server Error: " + e.getMessage());
        }

        return response;
    }
}
