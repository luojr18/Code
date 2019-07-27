package com.sandbox;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.jvm.sandbox.api.Information;
import com.alibaba.jvm.sandbox.api.Module;
import com.alibaba.jvm.sandbox.api.annotation.Command;
import com.alibaba.jvm.sandbox.api.listener.ext.Advice;
import com.alibaba.jvm.sandbox.api.listener.ext.AdviceListener;
import com.alibaba.jvm.sandbox.api.listener.ext.EventWatchBuilder;
import com.alibaba.jvm.sandbox.api.resource.ModuleEventWatcher;
import com.mongodb.MongoClient;
import com.sandbox.service.MongoService;
import com.sandbox.util.MongodbClientUtil;
import com.sandbox.vo.ErrorData;
import com.sandbox.vo.Record;
import com.sandbox.vo.Report;
import com.thoughtworks.proxy.toys.nullobject.Null;
import org.kohsuke.MetaInfServices;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.UUID;

@MetaInfServices(Module.class)
@Information(id = "record-report")
public class RecordModule implements Module {
    //录制记录数据
    private static ThreadLocal<Record> recordData = new ThreadLocal<Record>();

    @Resource
    private ModuleEventWatcher watcher;

    @Command("start")
    public void init() {
        new EventWatchBuilder(watcher)
                .onClass("springboot.controller")
                .includeSubClasses()
                .onBehavior("addUser")
                .onWatch(new AdviceListener() {
                    @Override
                    protected void before(Advice advice) throws Throwable {
                        String className = advice.getTarget().getClass().getName();
                        String methodName = advice.getBehavior().getName();
                        Object[] parameterArray = advice.getParameterArray();
                        String param = JSON.toJSONString(parameterArray, SerializerFeature.WriteClassName);
                        int length = advice.getBehavior().getParameterTypes().length;
                        String[] paramType = new String[length];
                        for (int i = 0; i < length; i++) {
                            paramType[i] = advice.getBehavior().getParameterTypes()[i].getName();
                        }
                        Record record = new Record();
                        recordData.set(record);
                        record.setClassName(className);
                        record.setMethodName(methodName);
                        record.setParam(param);
                        record.setParamType(Arrays.asList(paramType));
                        String traceId = null;
                        if (recordData.get() == null) {
                            //traceId = UUID.randomUUID().toString().replace("-","");
                            traceId = className + "&" + methodName + "&" + param;
                        }
                        record.setTraceId(traceId);
                        MongoService mongoService = new MongoService();
                        mongoService.insertRecord(record);

                    }
                    @Override
                    protected void afterReturning(Advice advice) throws Throwable {
                        String className = advice.getTarget().getClass().getName();
                        String methodName = advice.getBehavior().getName();
                        Object[] parameterArray = advice.getParameterArray();
                        String param = JSON.toJSONString(parameterArray, SerializerFeature.WriteClassName);
                        int length = advice.getBehavior().getParameterTypes().length;
                        String[] paramType = new String[length];
                        for (int i = 0; i < length; i++) {
                            paramType[i] = advice.getBehavior().getParameterTypes()[i].getName();
                        }
                        Object returnObj = advice.getReturnObj();
                        Record record = new Record();
                        record.setClassName(className);
                        record.setMethodName(methodName);
                        record.setParam(param);
                        record.setParamType(Arrays.asList(paramType));
                        record.setReturnData(returnObj);
                        record.setTraceId(recordData.get().getTraceId());
                        if (recordData.get().getTraceId().equals(className + "&" + methodName + "&" + param)) {
                            MongoService mongoService = new MongoService();
                            mongoService.insertRecord(record);
                            recordData.remove();
                        }
                    }

                    @Override
                    protected void afterThrowing(Advice advice) throws Throwable {
                        String className = advice.getTarget().getClass().getName();
                        String methodName = advice.getBehavior().getName();
                        Object[] parameterArray = advice.getParameterArray();
                        String param = JSON.toJSONString(parameterArray, SerializerFeature.WriteClassName);
                        int length = advice.getBehavior().getParameterTypes().length;
                        String[] paramType = null;
                        for (int i = 0; i < length; i++) {
                            paramType[i] = advice.getBehavior().getParameterTypes()[i].getName();
                        }
                        Throwable throwable = advice.getThrowable();
                        ErrorData errorData = new ErrorData();
                        errorData.setClassName(className);
                        errorData.setMethodName(methodName);
                        errorData.setParam(param);
                        errorData.setParamType(Arrays.asList(paramType));
                        errorData.setReturnData(throwable);
                        errorData.setExceptionMessage("fail load");
                        if (recordData.get().getTraceId().equals(className + "&" + methodName + "&" + param)) {
                            MongoService mongoService = new MongoService();
                            mongoService.insertErrorData(errorData);
                            recordData.remove();
                        }
                    }
                });
    }
}
