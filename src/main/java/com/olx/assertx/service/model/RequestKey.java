package com.olx.assertx.service.model;

public enum RequestKey {

    HOST,
    API_PATH,
    HEADERS,
    PAYLOAD,
    RESPONSE,
    QUERY_PARAMS,
    MULTIPART_SPECS,
    TYPE;

    public String toName() {
        return "REQUEST_" + this.name();
    }

}
