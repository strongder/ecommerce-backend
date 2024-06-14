package com.example.shop.exception;

import lombok.Getter;

@Getter
public enum ErrorResponse {
    CATEGORY_EXISTED(1001,"category exist"),
    CATEGORY_NOT_EXISTED(1002,"category not exist"),
    PRODUCT_EXISTED(1003,"product exist"),
    PRODUCT_NOT_EXISTED(1004,"product not exist"),
    USER_EXISTED(1005,"user exist"),
    USER_NOT_EXISTED(1006,"user not exist"),
    ADDRESS_EXISTED(1007,"address exist"),
    ADDRESS_NOT_EXISTED(1008,"address not exist"),
    ROLE_EXISTED(1009,"role exist"),
    ROLE_NOT_EXISTED(1010,"role not exist"),


    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error"),

    ;
    ErrorResponse(int code, String message) {
        this.code = code;
        this.message = message;
    }
    private int code;
    private String message;

}
