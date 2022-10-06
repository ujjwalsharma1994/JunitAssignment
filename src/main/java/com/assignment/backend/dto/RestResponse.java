package com.assignment.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RestResponse {

    private int statusCode;
    private String errorMessage;
    private String successMessage;
    private Object data;
}