package com.ma.torneos.service;

import com.ma.torneos.domain.User;

import java.util.List;

public interface UserDao {
    List<User> findOwners();
}
