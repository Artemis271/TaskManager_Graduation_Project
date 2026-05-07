package com.yuranium.notificationservice.repository;

import com.yuranium.notificationservice.document.UserDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<UserDocument, Long> {}
