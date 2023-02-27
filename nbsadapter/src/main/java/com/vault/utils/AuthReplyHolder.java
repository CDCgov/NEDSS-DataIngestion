package com.vault.utils;

import  java.io.Serializable;

public class AuthReplyHolder implements Serializable {
    public String request_id;
    public String lease_id;
    public String renewable;
    public String lease_duration;
    public String data;
    public String wrap_info;
    public String warnings;
    public AuthTokenHolder auth;

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("AuthReplyHolder [request_id =");
        builder.append(request_id);
        builder.append(", ");
        builder.append(auth);
        builder.append("]");
        return builder.toString();
    }
}
