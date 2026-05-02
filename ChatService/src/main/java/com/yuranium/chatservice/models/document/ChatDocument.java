package com.yuranium.chatservice.models.document;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "chats")
public class ChatDocument
{
    @Id
    private UUID id;

    @Field(name = "title", targetType = FieldType.STRING)
    private String title;

    @CreatedDate
    @Field(name = "dateCreated", targetType = FieldType.TIMESTAMP)
    private LocalDateTime dateCreated;

    @Field(name = "userIds")
    private List<Long> userIds;

    @Indexed
    @Field(name = "ownerId", targetType = FieldType.INT64)
    private Long ownerId;
}