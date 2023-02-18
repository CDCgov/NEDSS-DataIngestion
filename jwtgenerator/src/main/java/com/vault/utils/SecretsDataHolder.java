package com.vault.utils;

import  java.io.Serializable;
import  java.util.HashMap;

public class SecretsDataHolder implements Serializable {
    public HashMap<String, String> data;

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("DataHolder [data =");
        builder.append(data);
        builder.append("]");
        return builder.toString();
    }
}
