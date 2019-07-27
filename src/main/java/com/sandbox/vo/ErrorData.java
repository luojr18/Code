package com.sandbox.vo;

import lombok.Data;

import java.util.List;
@Data
public class ErrorData {
    private String traceId;
    private String className;
    private String methodName;
    private String param;
    private List<String> paramType;
    private String returnType;
    private Object returnData;
    private String exceptionType;
    private Object exceptionMessage;
}
