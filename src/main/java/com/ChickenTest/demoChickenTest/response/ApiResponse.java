package com.ChickenTest.demoChickenTest.response;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ApiResponse<T> {
    private int status;
    //private boolean success;
    private String message;
    private T data;

    public ApiResponse(int status, String message, T data){
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public ApiResponse(int status, String message){
        this.status = status;
        this.message = message;
    }

    public ApiResponse(String message){
        this.message = message;
    }
    public ApiResponse(){}
}
