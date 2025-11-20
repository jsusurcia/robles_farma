package com.example.robles_farma.response;
import java.util.List;

public class ItemListResponse<T> {
    private String status;
    private String message;
    private List<T> data;

    public String getStatus() { return status; }
    public String getMessage() { return message; }
    public List<T> getData() { return data; }
}