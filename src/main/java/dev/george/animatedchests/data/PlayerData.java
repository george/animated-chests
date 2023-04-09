package dev.george.animatedchests.data;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class PlayerData {

    private final Map<String, Integer> keys = new HashMap<>();

    public void addKey(String crateName, int keys) {
        this.keys.put(crateName.toLowerCase(), this.keys.getOrDefault(crateName, 0) + keys);
    }

    public int getKeyCount(String crateName) {
        return this.keys.getOrDefault(crateName.toLowerCase(), 0);
    }

    public void useKey(String crateName) {
        this.keys.put(crateName.toLowerCase(), this.keys.get(crateName.toLowerCase()) - 1);
    }
}
