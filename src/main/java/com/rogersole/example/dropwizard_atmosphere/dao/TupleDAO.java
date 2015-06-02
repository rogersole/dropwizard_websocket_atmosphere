package com.rogersole.example.dropwizard_atmosphere.dao;

import io.dropwizard.hibernate.AbstractDAO;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;

import com.rogersole.example.dropwizard_atmosphere.core.Tuple;

/**
 * Data Access Object to manage Tuple objects and interact with database.
 * 
 * @author rogersole
 *
 */
public class TupleDAO extends AbstractDAO<Tuple> {

    public TupleDAO(SessionFactory factory) {
        super(factory);
    }

    public Tuple create(Tuple tuple) throws HibernateException {
        return persist(tuple);
    }

    public List<Tuple> findAll() throws HibernateException {
        return list(namedQuery("findAllTuples"));
    }
}
