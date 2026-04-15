package com.crisafe;

public record ArchiveRecord(
        String company,
        String user,
        String pass,
        String note
) {
    public String toJson() {
        return String.format("{\"company\":\"%s\",\"user\":\"%s\",\"pass\":\"%s\",\"note\":\"%s\"}",
                company, user, pass, note);
    }
}
