package com.rogersole.example.dropwizard_atmosphere.core;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * This class maps the database 'tuple' table.
 * 
 * @author rogersole
 *
 */

@Entity
@NamedQuery(name = "findAllTuples", query = "SELECT t FROM Tuple t")
@Table(name = "tuple")
@IdClass(TupleId.class)
public class Tuple implements Serializable {
    private static final long serialVersionUID = 7309640843361945656L;

    @Id
    @Column(name = "email", nullable = false)
    private String            email;

    @Id
    @Column(name = "repository", nullable = false)
    private String            repository;

    public Tuple() {}

    public Tuple(String email, String repository) {
        this.email = email;
        this.repository = repository;
    }

    public Tuple(JsonNode root) {
        this.email = root.get("email").asText();
        this.repository = root.get("repository").asText();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRepository() {
        return repository;
    }

    public void setRepository(String repository) {
        this.repository = repository;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Tuple))
            return false;

        final Tuple that = (Tuple) o;

        return Objects.equals(this.email, that.email) && Objects.equals(this.repository, that.repository);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, repository);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Tuple [");
        sb.append("email=").append(this.email).append(", ");
        sb.append("repository=").append(this.repository).append("]");
        return sb.toString();
    }
}


class TupleId implements Serializable {
    private static final long serialVersionUID = 3818320141178522132L;
    String                    email;
    String                    repository;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof TupleId))
            return false;

        final TupleId that = (TupleId) o;

        return Objects.equals(this.email, that.email) && Objects.equals(this.repository, that.repository);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, repository);
    }
}
