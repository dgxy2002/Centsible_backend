package com.example.backendapi.repository;

import com.mongodb.client.result.UpdateResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import com.example.backendapi.model.Notification;
import java.time.LocalDate;

@Repository
public class NotificationRepositoryImpl implements NotificationRepositoryCustom {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void markAllAsRead(String userId, LocalDate sinceDate) {
        Query query = new Query();
        query.addCriteria(Criteria.where("userId").is(userId).and("createdAt").gt(sinceDate));

        Update update = new Update().set("read", true);
        mongoTemplate.updateMulti(query, update, Notification.class);
    }
}
