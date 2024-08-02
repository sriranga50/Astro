package com.pranavaeet.astro.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CurrentTimestamp;
import org.springframework.data.annotation.CreatedBy;

import java.util.List;

@Setter
@Getter
@Entity
@Table(name="role")

public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    private String roleDescription;
    @CurrentTimestamp
    @Column(updatable = false)
    @JsonIgnore
    private java.sql.Timestamp createdon;
    @CreatedBy
    @JsonIgnore
    private String createdby;
    @JsonIgnore
    private int page = 1;
    private int size = 10;

    @ManyToMany(mappedBy = "roles",fetch = FetchType.LAZY)
    private List<UserInfo> users;
    @ManyToMany(mappedBy = "roles",fetch = FetchType.LAZY)
    private List<ControllerAccess> controllers;

}
