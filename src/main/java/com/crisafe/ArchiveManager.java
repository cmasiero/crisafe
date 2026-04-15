package com.crisafe;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class ArchiveManager {

    private final Gson gson = new Gson();
    private List<ArchiveRecord> records;

    public void load(String json) {
        records = gson.fromJson(json, new TypeToken<List<ArchiveRecord>>() {}.getType());
    }

}
