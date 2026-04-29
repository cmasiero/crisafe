package com.crisafe.state;

import com.crisafe.ArchiveRecord;
import com.crisafe.command.RemoveFromArchiveCommand;
import com.crisafe.pattern.Command;
import com.crisafe.pattern.Context;
import com.crisafe.pattern.State;
import com.crisafe.service.OutputService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class RemoveFromArchiveState implements State {

    private final OutputService output = OutputService.getInstance();
    private final Command command = new RemoveFromArchiveCommand();

    @Override
    public String display() {
        output.printBold("=== Remove from Archive, '*' for all. Return to go back. ===");
        return output.readLine("Search for: ");
    }

    @Override
    public void handleInput(String input, Context context) {

        if (input == null || input.isEmpty()) {
            context.setState(new ArchiveOperationState());
            return;
        }

        String json = context.getAttribute("json");
        Gson gson = new Gson();
        List<ArchiveRecord> records = gson.fromJson(json, new TypeToken<List<ArchiveRecord>>() {}.getType());

        String lowerSearch = input.toLowerCase();
        List<ArchiveRecord> results = records.stream()
                .filter(r -> "*".equals(input)
                          || contains(r.company(), lowerSearch)
                          || contains(r.user(), lowerSearch)
                          || contains(r.note(), lowerSearch))
                .toList();

        if (results.isEmpty()) {
            output.printRed("No records found for: " + input);
            return;
        }

        output.printGreen("Found " + results.size() + " record(s):");
        for (int i = 0; i < results.size(); i++) {
            ArchiveRecord r = results.get(i);
            output.print(String.format("%d) Company: %s | User: %s | Pass: %s | Note: %s",
                    i + 1, r.company(), r.user(), r.pass(), r.note()));
        }
        output.print("Return) Back");

        String selection = output.readLine("Select index to remove: ");
        if (selection == null || selection.isEmpty()) {
            return;
        }

        int index;
        try {
            index = Integer.parseInt(selection);
        } catch (NumberFormatException e) {
            output.printRed("Invalid index");
            return;
        }

        if (index < 1 || index > results.size()) {
            output.printRed("Index out of range");
            return;
        }

        ArchiveRecord toRemove = results.get(index - 1);
        context.setAttribute("remove.company", toRemove.company());
        context.setAttribute("remove.user",    toRemove.user());
        context.setAttribute("remove.pass",    toRemove.pass());
        context.setAttribute("remove.note",    toRemove.note());
        command.execute(context);

    }

    private boolean contains(String field, String search) {
        return field != null && field.toLowerCase().contains(search);
    }

}
