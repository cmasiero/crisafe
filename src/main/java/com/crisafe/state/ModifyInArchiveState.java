package com.crisafe.state;

import com.crisafe.ArchiveRecord;
import com.crisafe.command.ModifyInArchiveCommand;
import com.crisafe.pattern.Command;
import com.crisafe.pattern.Context;
import com.crisafe.pattern.State;
import com.crisafe.service.OutputService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class ModifyInArchiveState implements State {

    private final OutputService output = OutputService.getInstance();
    private final Command command = new ModifyInArchiveCommand();

    @Override
    public String display() {
        output.printBold("=== Modify in Archive, '*' for all. Return to go back. ===");
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
            output.print(String.format("%d) Company: %s | User: %s | Note: %s",
                    i + 1, r.company(), r.user(), r.note()));
        }
        output.print("Return) Back");

        String selection = output.readLine("Select index to modify: ");
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

        ArchiveRecord old = results.get(index - 1);
        output.printBold("Leave blank to keep current value.");

        String company = output.readLine("Company (" + old.company() + "): ");
        String user    = output.readLine("User (" + old.user() + "): ");
        String pass    = output.readPassword("Password (Return to keep): ");
        String note    = output.readLine("Note (" + old.note() + "): ");

        context.setAttribute("modify.old.company", old.company());
        context.setAttribute("modify.old.user",    old.user());
        context.setAttribute("modify.old.pass",    old.pass());
        context.setAttribute("modify.old.note",    old.note());
        context.setAttribute("modify.new.company", company.isEmpty() ? old.company() : company);
        context.setAttribute("modify.new.user",    user.isEmpty()    ? old.user()    : user);
        context.setAttribute("modify.new.pass",    pass.isEmpty()    ? old.pass()    : pass);
        context.setAttribute("modify.new.note",    note.isEmpty()    ? old.note()    : note);

        command.execute(context);
    }

    private boolean contains(String field, String search) {
        return field != null && field.toLowerCase().contains(search);
    }

}
