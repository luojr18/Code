package com.sandbox.service;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.sandbox.util.MongodbClientUtil;
import com.sandbox.vo.ErrorData;
import com.sandbox.vo.Record;
import com.sandbox.vo.Report;
import com.sandbox.vo.User;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class MongoService {

    public void insert(User user) {
        MongoClient mongoClient = MongodbClientUtil.getMongoClient();
        MongoDatabase database = mongoClient.getDatabase("admin");
        MongoCollection<User> collection = database.getCollection("user", User.class);
        collection.insertOne(user);
    }
    public User getUser(String userId){
        MongoClient mongoClient = MongodbClientUtil.getMongoClient();
        MongoDatabase database = mongoClient.getDatabase("admin");
        MongoCollection<User> collection = database.getCollection("user", User.class);
        return collection.find(Filters.eq("userId", userId)).first();
    }
    public void insertRecord(Record record){
        MongoClient mongoClient = MongodbClientUtil.getMongoClient();
        MongoDatabase database = mongoClient.getDatabase("admin");
        MongoCollection<Record> collection = database.getCollection("record", Record.class);
        collection.insertOne(record);
    }
    public Record getRecord(String traceId){
        MongoClient mongoClient = MongodbClientUtil.getMongoClient();
        MongoDatabase database = mongoClient.getDatabase("admin");
        MongoCollection<Record> collection = database.getCollection("record", Record.class);
        return collection.find(Filters.eq("traceId", traceId)).first();
    }
    public void insertReport(Report report){
        MongoClient mongoClient = MongodbClientUtil.getMongoClient();
        MongoDatabase database = mongoClient.getDatabase("admin");
        MongoCollection<Report> collection = database.getCollection("report", Report.class);
        collection.insertOne(report);
    }
    public Report getReport(String traceId){
        MongoClient mongoClient = MongodbClientUtil.getMongoClient();
        MongoDatabase database = mongoClient.getDatabase("admin");
        MongoCollection<Report> collection = database.getCollection("report", Report.class);
        return collection.find(Filters.eq("traceId", traceId)).first();
    }
    public void insertErrorData(ErrorData errorData){
        MongoClient mongoClient = MongodbClientUtil.getMongoClient();
        MongoDatabase database = mongoClient.getDatabase("admin");
        MongoCollection<ErrorData> collection = database.getCollection("errorData", ErrorData.class);
        collection.insertOne(errorData);
    }
    public ErrorData getErrorData(String traceId){
        MongoClient mongoClient = MongodbClientUtil.getMongoClient();
        MongoDatabase database = mongoClient.getDatabase("admin");
        MongoCollection<ErrorData> collection = database.getCollection("errorData", ErrorData.class);
        return collection.find(Filters.eq("traceId", traceId)).first();
    }


}
