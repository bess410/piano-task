package com.example.demo.repository;

import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Repository
public class FakeRepository {

    private Map<Integer, Set<Integer>> fakeBase;

    public FakeRepository() {
        this.fakeBase = new HashMap<>();
        for (int i = 1; i < 6; i++) {
            fakeBase.put(i, new HashSet<>());
        }
    }

    public boolean isIntoRoom(int keyId) {
        return fakeBase.entrySet().stream()
                .flatMap(entry -> entry.getValue().stream())
                .anyMatch(item -> item.equals(keyId));
    }

    public void saveToRoom(int roomId, int keyId) {
        this.fakeBase.get(roomId).add(keyId);
    }

    public int getUserRoom(int keyId) {
        for (int i = 1; i <= this.fakeBase.size(); i++) {
            Set<Integer> integers = fakeBase.get(i);
            if (integers.contains(keyId)) {
                return i;
            }
        }
        return 0;
    }

    public void removeUserFromRoom(int roomId, int keyId) {
        this.fakeBase.get(roomId).remove(keyId);
    }
}
