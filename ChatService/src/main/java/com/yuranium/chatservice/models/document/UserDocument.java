package com.yuranium.chatservice.models.document;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "users")
public class UserDocument
{
    @Id
    private Long id;

    @Indexed(unique = true)
    @Field(name = "username", targetType = FieldType.STRING)
    private String username;

    @Field(name = "avatarData")
    private byte[] binaryData;
}