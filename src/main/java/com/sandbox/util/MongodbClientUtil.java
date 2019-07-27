package com.sandbox.util;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.sandbox.vo.Record;
import org.bson.Document;

public class MongodbClientUtil {
    public static MongoClient getMongoClient(){
        MongoClient client = new MongoClient(new MongoClientURI("mongodb://192.168.128.129:27017"));
        return client;
    }
}
