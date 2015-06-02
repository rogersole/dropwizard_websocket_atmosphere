package com.rogersole.example.dropwizard_atmosphere.core;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * This class maps the 'nr_user' database table.
 * 
 * @author rogersole
 *
 */
@Entity
@Table(name = "nr_user")
public class User implements Serializable {

    private static final long serialVersionUID = -2578610272438227660L;

    @Id
    @Column(name = "id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long              id;

    @Column(name = "name", nullable = false)
    private String            name;

    @Column(name = "password", nullable = false)
    private String            password;

    public User() {}

    public User(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
