package com.example.p1.service;

import com.example.p1.exceptions.EtAuthException;
import com.example.p1.models.Employee;
import com.example.p1.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.regex.Pattern;

@Service
@Transactional
public class EmployeeServiceImpl implements EmployeeService{

    @Autowired
    EmployeeRepository employeeRepository;

    @Override
    public Employee validateEmployee(String email, String password) throws EtAuthException {
        if(email != null ) email = email.toLowerCase();
        return employeeRepository.findByEmailAndPassword(email, password);
    }

    @Override
    public Employee registerUser(String firstName, String lastName, String email, String password) throws EtAuthException {
        Pattern pattern = Pattern.compile("^(.+)@(.+)$");
        if(email != null) email = email.toLowerCase();
        if(!pattern.matcher(email).matches())
            throw new EtAuthException("Invalid email format");
        Integer count = employeeRepository.getCountByEmail(email);
        if(count > 0)
            throw new EtAuthException("Email already in use");
        Integer userId = employeeRepository.create(firstName, lastName, email, password);
        return employeeRepository.findById(userId);
    }
}
