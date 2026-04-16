package com.crisafe;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class ArchiveManager {

    private final Gson gson;
    private List<ArchiveRecord> records;

    public ArchiveManager(String json) {
        gson = new Gson();
        records = gson.fromJson(json, new TypeToken<List<ArchiveRecord>>() {}.getType());
    }

}
