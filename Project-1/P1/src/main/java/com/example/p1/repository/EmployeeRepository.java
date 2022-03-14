package com.example.p1.repository;

import com.example.p1.exceptions.EtAuthException;
import com.example.p1.models.Employee;

public interface EmployeeRepository {

    Integer create(String firstName, String lastName, String email, String password) throws EtAuthException;

    Employee findByEmailAndPassword(String email, String password) throws EtAuthException;

    Integer getCountByEmail(String email);

    Employee findById (Integer userId);
}
