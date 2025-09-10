package com.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Issue implements Serializable {

    private String id;
    private String description;
    private String parentId;
    private Status status;
    private Instant createdAt;
    private Instant updatedAt;
}
