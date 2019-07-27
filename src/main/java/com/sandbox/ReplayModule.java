package com.sandbox;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.jvm.sandbox.api.Information;
import com.alibaba.jvm.sandbox.api.Module;
import com.alibaba.jvm.sandbox.api.ProcessController;
import com.alibaba.jvm.sandbox.api.annotation.Command;
import com.alibaba.jvm.sandbox.api.listener.ext.Advice;
import com.alibaba.jvm.sandbox.api.listener.ext.AdviceListener;
import com.alibaba.jvm.sandbox.api.listener.ext.EventWatchBuilder;
import com.alibaba.jvm.sandbox.api.resource.ModuleEventWatcher;
import com.sandbox.service.MongoService;
import com.sandbox.vo.ErrorData;
import com.sandbox.vo.Record;
import com.sandbox.vo.Report;
import org.kohsuke.MetaInfServices;

import javax.annotation.Resource;
import java.util.Arrays;

@MetaInfServices(Module.class)
@Information(id = "replay")
public class ReplayModule implements Module {
    @Resource
    private ModuleEventWatcher watcher;

    private static final ThreadLocal<Record> recordData = new ThreadLocal<Record>();

    @Command("start")
    public void init() {
        new EventWatchBuilder(watcher)
                .onClass("springboot.controller")
                .includeSubClasses()
                .onBehavior("addUser")
                .onWatch(new AdviceListener(){
                    @Override
                    protected void before(Advice advice) throws Throwable {
                        String className = advice.getTarget().getClass().getName();
                        String methodName = advice.getBehavior().getName();
                        Object[] parameterArray = advice.getParameterArray();
                        String param = JSON.toJSONString(parameterArray, SerializerFeature.WriteClassName);
                        int length = advice.getBehavior().getParameterTypes().length;
                        String[] paramType = null;
                        for (int i = 0; i < length; i++) {
                            paramType[i] = advice.getBehavior().getParameterTypes()[i].getName();
                        }
                        String traceId = className + "&" + methodName + "&" + param;
                        if (recordData.get()==null){
                            MongoService mongoService = new MongoService();
                            Record record = mongoService.getRecord(traceId);
                            if (record != null);
                            recordData.set(record);
                        }else{
                            Record record = recordData.get();
                            if (record.getTraceId()==null){
                                ErrorData errorData = new ErrorData();
                                errorData.setTraceId(traceId);
                                errorData.setClassName(className);
                                errorData.setMethodName(methodName);
                                errorData.setParam(param);
                                errorData.setParamType(Arrays.asList(paramType));
                                Report report = new Report();
                               report.setErrorData(errorData);
                               report.setTraceId(traceId);
                               MongoService mongoService = new MongoService();
                               mongoService.insertReport(report);
                               recordData.remove();
                                ProcessController.returnImmediately("调用链出错");
                            }else if (!className.equals(record.getClassName())){
                                ErrorData errorData = new ErrorData();
                                errorData.setClassName(className);
                                errorData.setMethodName(methodName);
                                errorData.setParam(param);
                                errorData.setParamType(Arrays.asList(paramType));
                                errorData.setExceptionMessage("");
                                Report report = new Report();
                                report.setErrorData(errorData);
                                MongoService mongoService = new MongoService();
                                mongoService.insertReport(report);
                                recordData.remove();
                                ProcessController.returnImmediately("类出错");

                            }
                            else if (!methodName.equals(record.getMethodName())){
                                ErrorData errorData = new ErrorData();
                                errorData.setClassName(className);
                                errorData.setMethodName(methodName);
                                errorData.setParam(param);
                                errorData.setParamType(Arrays.asList(paramType));
                                errorData.setExceptionMessage("");
                                Report report = new Report();
                                report.setErrorData(errorData);
                                MongoService mongoService = new MongoService();
                                mongoService.insertReport(report);
                                recordData.remove();
                                ProcessController.returnImmediately("方法出错");

                            }
                            else if (!param.equals(record.getParam())){
                                ErrorData errorData = new ErrorData();
                                errorData.setClassName(className);
                                errorData.setMethodName(methodName);
                                errorData.setParam(param);
                                errorData.setParamType(Arrays.asList(paramType));
                                errorData.setExceptionMessage("");
                                Report report = new Report();
                                report.setErrorData(errorData);
                                MongoService mongoService = new MongoService();
                                mongoService.insertReport(report);
                                recordData.remove();
                                ProcessController.returnImmediately("传参出错");

                            }
                        }
                    }

                    @Override
                    protected void afterReturning(Advice advice) throws Throwable {
                        String className = advice.getTarget().getClass().getName();
                        String methodName = advice.getBehavior().getName();
                        Object returnObj = advice.getReturnObj();
                        Record record =recordData.get();
                        if (record.getTraceId()==null){
                            ErrorData errorData = new ErrorData();
                            errorData.setTraceId(record.getTraceId());
                            errorData.setClassName(className);
                            errorData.setMethodName(methodName);
                            errorData.setReturnData(returnObj);
                            Report report = new Report();
                            report.setErrorData(errorData);
                            report.setTraceId(record.getTraceId());
                            MongoService mongoService = new MongoService();
                            mongoService.insertReport(report);
                            recordData.remove();
                            ProcessController.returnImmediately("调用链出错");
                        }else if (!className.equals(record.getClassName())){
                            ErrorData errorData = new ErrorData();
                            errorData.setClassName(className);
                            errorData.setMethodName(methodName);
                            errorData.setReturnData(returnObj);
                            Report report = new Report();
                            report.setErrorData(errorData);
                            MongoService mongoService = new MongoService();
                            mongoService.insertReport(report);
                            recordData.remove();
                            ProcessController.returnImmediately("类出错");

                        }
                        else if (!methodName.equals(record.getMethodName())){
                            ErrorData errorData = new ErrorData();
                            errorData.setClassName(className);
                            errorData.setMethodName(methodName);
                            errorData.setReturnData(returnObj);
                            Report report = new Report();
                            report.setErrorData(errorData);
                            MongoService mongoService = new MongoService();
                            mongoService.insertReport(report);
                            recordData.remove();
                            ProcessController.returnImmediately("方法出错");

                        } else if (!returnObj.equals(record.getReturnData())){
                            ErrorData errorData = new ErrorData();
                            errorData.setClassName(className);
                            errorData.setMethodName(methodName);
                            errorData.setReturnData(returnObj);
                            Report report = new Report();
                            report.setErrorData(errorData);
                            MongoService mongoService = new MongoService();
                            mongoService.insertReport(report);
                            recordData.remove();
                            ProcessController.returnImmediately("返回数据出错");
                        }
                    }

                    @Override
                    protected void afterThrowing(Advice advice) throws Throwable {
                        String className = advice.getTarget().getClass().getName();
                        String methodName = advice.getBehavior().getName();
                        Object returnObj = advice.getReturnObj();
                        Throwable throwable = advice.getThrowable();
                        Record record =recordData.get();
                        if (record.getTraceId()==null){
                            ErrorData errorData = new ErrorData();
                            errorData.setTraceId(record.getTraceId());
                            errorData.setClassName(className);
                            errorData.setMethodName(methodName);
                            errorData.setExceptionMessage(throwable);
                            Report report = new Report();
                            report.setErrorData(errorData);
                            report.setTraceId(record.getTraceId());
                            MongoService mongoService = new MongoService();
                            mongoService.insertReport(report);
                            recordData.remove();
                            ProcessController.returnImmediately("调用链出错");
                        }else if (!className.equals(record.getClassName())){
                            ErrorData errorData = new ErrorData();
                            errorData.setClassName(className);
                            errorData.setMethodName(methodName);
                            errorData.setExceptionMessage(throwable);
                            Report report = new Report();
                            report.setErrorData(errorData);
                            MongoService mongoService = new MongoService();
                            mongoService.insertReport(report);
                            recordData.remove();
                            ProcessController.returnImmediately("类出错");

                        }
                        else if (!methodName.equals(record.getMethodName())){
                            ErrorData errorData = new ErrorData();
                            errorData.setClassName(className);
                            errorData.setMethodName(methodName);
                            errorData.setExceptionMessage(throwable);
                            Report report = new Report();
                            report.setErrorData(errorData);
                            MongoService mongoService = new MongoService();
                            mongoService.insertReport(report);
                            recordData.remove();
                            ProcessController.returnImmediately("方法出错");

                        } else if (!returnObj.equals(record.getReturnData())){
                            ErrorData errorData = new ErrorData();
                            errorData.setClassName(className);
                            errorData.setMethodName(methodName);
                            errorData.setReturnData(returnObj);
                            errorData.setExceptionMessage(throwable);
                            Report report = new Report();
                            report.setErrorData(errorData);
                            MongoService mongoService = new MongoService();
                            mongoService.insertReport(report);
                            recordData.remove();
                            ProcessController.returnImmediately("返回数据出错");
                        }
                    }
                }) ;
    }
}
