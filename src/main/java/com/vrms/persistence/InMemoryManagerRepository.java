package com.vrms.persistence;

import java.util.ArrayList;
import java.util.List;

import com.vrms.domain.Manager;

public class InMemoryManagerRepository implements ManagerRepository {

    private List<Manager> managers;

    public InMemoryManagerRepository() {
        managers = new ArrayList<>();
        managers.add(new Manager("admin", "1234"));
    }

    public Manager findByUsername(String username) {
        for (Manager manager : managers) {
            if (manager.getUsername().equals(username)) {
                return manager;
            }
        }

        return null;
    }
}