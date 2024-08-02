package com.pranavaeet.astro.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CurrentTimestamp;
import org.springframework.data.annotation.CreatedBy;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "userinfo")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfo {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private String username;
	private String password;
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

	@ManyToMany(fetch = FetchType.EAGER,cascade = CascadeType.ALL)
	@JoinTable(name = "user_role",joinColumns = @JoinColumn(name = "user_id",referencedColumnName = "id"),
		inverseJoinColumns = @JoinColumn(name = "role_id",referencedColumnName = "id")
	)
	private List<Role> roles = new ArrayList<>();

	public void addRole(Role role) {
		this.roles.add(role);
	}

	public void removeRole(Role role) {
		this.roles.remove(role);
	}

}
