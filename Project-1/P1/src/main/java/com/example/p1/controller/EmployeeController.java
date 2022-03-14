package com.example.p1.controller;

import com.example.p1.models.Employee;
import com.example.p1.service.Constants;
import com.example.p1.service.EmployeeService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class EmployeeController {

    @Autowired
    EmployeeService employeeService;

    //login
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> loginUser(@RequestBody Map<String, Object> userMap) {
        String email = (String) userMap.get("email");
        String password = (String) userMap.get("password");
        Employee employee = employeeService.validateEmployee(email, password);
        return new ResponseEntity<>(generateJWTToken(employee), HttpStatus.OK);
    }

    //register
    @PostMapping("register")
    public ResponseEntity<Map<String, String>> registerEmployee(@RequestBody Map<String, Object> userMap) {
        String firstName = (String) userMap.get("firstName");
        String lastName = (String) userMap.get("lastName");
        String email = (String) userMap.get("email");
        String password = (String) userMap.get("password");
        Employee employee = employeeService.registerUser(firstName, lastName, email, password);
        return new ResponseEntity<>(generateJWTToken(employee), HttpStatus.OK);
    }

    private Map<String, String> generateJWTToken(Employee employee) {
        long timestamp = System.currentTimeMillis();
        String token = Jwts.builder().signWith(SignatureAlgorithm.HS256, Constants.API_SECRET_KEY)
                .setIssuedAt(new Date(timestamp))
                .setExpiration(new Date(timestamp + Constants.TOKEN_VALIDITY))
                .claim("userId", employee.getUserId())
                .claim("email", employee.getEmail())
                .claim("firstName", employee.getFirstName())
                .claim("lastName", employee.getLastName())
                .compact();
        Map<String, String> map = new HashMap<>();
        map.put("token", token);
        return map;
    }
}
