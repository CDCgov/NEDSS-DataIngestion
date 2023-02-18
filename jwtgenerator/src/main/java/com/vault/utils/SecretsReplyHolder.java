package com.vault.utils;

import  java.io.Serializable;
import  java.util.HashMap;

public class SecretsReplyHolder implements Serializable {
    public String request_id;
    public String lease_id;
    public String renewable;
    public String lease_duration;
    public SecretsDataHolder data;

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("SecretsReplyHolder [request_id =");
        builder.append(request_id);
        builder.append(", ");
        builder.append(data);
        builder.append("]");
        return builder.toString();
    }
}
