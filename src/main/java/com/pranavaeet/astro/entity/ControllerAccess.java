package com.pranavaeet.astro.entity;
import java.util.List;

import com.google.common.base.Optional;

import java.util.ArrayList;


// import org.hibernate.mapping.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name="access")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ControllerAccess {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    private int page = 1;
	private int size = 10;
    @ManyToMany(fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    @JoinTable(name="controller_role_access",joinColumns = @JoinColumn(name="contoller_id",referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name="role_id",referencedColumnName="id")
    )
    private List<Role> roles=new ArrayList<>();

    public void addRole(Role role){
        this.roles.add(role);
    }
    public void removeRole(Role role) {
		this.roles.remove(role);
	}
   
}
