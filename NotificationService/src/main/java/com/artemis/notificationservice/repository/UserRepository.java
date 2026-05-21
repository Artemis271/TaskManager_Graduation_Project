package com.artemis.notificationservice.repository;

import com.artemis.notificationservice.document.UserDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<UserDocument, Long> {}
