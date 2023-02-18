package com.vault.utils;

import  java.io.Serializable;

public class AuthTokenHolder implements Serializable {
    public String client_token;

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("AuthTokenHolder [client_token =");
        builder.append(client_token);
        builder.append("]");
        return builder.toString();
    }
}
