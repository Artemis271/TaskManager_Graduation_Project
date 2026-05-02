package com.yuranium.chatservice.service;

import com.yuranium.chatservice.models.document.ChatDocument;
import com.yuranium.chatservice.models.document.UserDocument;
import com.yuranium.core.events.UserCreatedEvent;
import com.yuranium.core.events.UserUpdatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService
{
    private final MongoTemplate mongoTemplate;

    public List<UserDocument> searchUser(String inputUsername, Pageable pageable)
    {
        String escaped = Pattern.quote(inputUsername);
        Pattern regex = Pattern.compile(escaped, Pattern.CASE_INSENSITIVE);

        Query query = Query.query(Criteria.where("username").regex(regex))
                .with(pageable);
        return mongoTemplate.find(query, UserDocument.class);
    }

    public UserDocument createUser(UserCreatedEvent user)
    {
        return mongoTemplate.insert(UserDocument.builder()
                .id(user.id())
                .username(user.username())
                .binaryData(user.avatarData())
                .build());
    }

    public UserDocument updateUser(UserUpdatedEvent updatedUser)
    {
        UserDocument newUser = UserDocument.builder()
                .id(updatedUser.id())
                .username(updatedUser.username())
                .binaryData(updatedUser.avatarData())
                .build();
        return mongoTemplate.save(newUser);
    }

    public void deleteUser(Long id)
    {
        mongoTemplate.remove(
                Query.query(Criteria.where("_id").is(id)),
                UserDocument.class
        );
    }

    @Cacheable(value = "users", key = "#userId")
    public List<UserDocument> myTeam(Long userId, Pageable pageable)
    {
        Set<Long> userIds = mongoTemplate.find(
                Query.query(Criteria.where("userIds")
                        .elemMatch(Criteria.where("$eq").is(userId)))
                        .with(pageable),
                ChatDocument.class).stream()
                .flatMap(chat -> chat.getUserIds().stream())
                .collect(Collectors.toSet());
        if (userIds.isEmpty())
            userIds.add(userId);

        return mongoTemplate.find(Query.query(Criteria
                .where("_id").in(userIds)),
                UserDocument.class);
    }
}