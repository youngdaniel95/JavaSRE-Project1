package com.example.p1.service;

import com.example.p1.exceptions.EtAuthException;
import com.example.p1.models.Employee;

public interface EmployeeService {

    //validate existing user
    Employee validateEmployee (String email, String password) throws EtAuthException;

    //register new user
    Employee registerUser (String firstName, String lastName, String email, String password) throws EtAuthException;
}
