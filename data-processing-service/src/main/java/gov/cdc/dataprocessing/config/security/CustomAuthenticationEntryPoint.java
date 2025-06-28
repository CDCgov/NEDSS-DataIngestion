package gov.cdc.dataprocessing.config.security;

import com.google.gson.Gson;
import gov.cdc.dataprocessing.config.security.model.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        String expMessage=authException.getMessage();
        //The following code block is for providing the custom message for the invalid client id and client secrets.
        if(expMessage!=null && expMessage.contains("Full authentication is required to access this resource") ){
            String clientId = request.getHeader("clientid");
            String clientSecret = request.getHeader("clientsecret");
            String authorizationType= request.getHeader("authorization");
            if(authorizationType!=null && authorizationType.startsWith("Bearer")
                    && clientId!=null && clientSecret!=null){
                expMessage="Invalid client or Invalid client credentials";
            }
        }
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatusCode(HttpServletResponse.SC_UNAUTHORIZED);
        errorResponse.setMessage("Unauthorized");
        errorResponse.setDetails(expMessage);
        Gson gson = new Gson();
        String json = gson.toJson(errorResponse);
        response.getWriter().write(json);
    }
}
