package com.crisafe.command;

import com.crisafe.ArchiveRecord;
import com.crisafe.service.OutputService;
import com.crisafe.pattern.Command;
import com.crisafe.pattern.Context;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class FindInArchiveCommand implements Command {

    private final OutputService output = OutputService.getInstance();

    @Override
    public void execute(Context context) {

        String json = context.getAttribute("json");
        String search = context.getAttribute("search");

        Gson gson = new Gson();
        List<ArchiveRecord> records = gson.fromJson(json, new TypeToken<List<ArchiveRecord>>() {}.getType());

        String lowerSearch = search.toLowerCase();
        List<ArchiveRecord> results = "*".equals(search)
                ? records
                : records.stream()
                        .filter(r -> contains(r.company(), lowerSearch)
                                  || contains(r.user(), lowerSearch)
                                  || contains(r.note(), lowerSearch))
                        .toList();

        if (results.isEmpty()) {
            output.printRed("No records found for: " + search);
        } else {
            output.printGreen("Found " + results.size() + " record(s):");
            results.forEach(r -> output.print(
                "  Company: " + r.company() +
                " | User: " + r.user() +
                " | Pass: " + r.pass() +
                " | Note: " + r.note()
            ));
        }

    }

    private boolean contains(String field, String search) {
        return field != null && field.toLowerCase().contains(search);
    }

}
