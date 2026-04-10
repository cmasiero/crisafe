package com.crisafe;

import java.nio.file.Path;

public record Archive(String name, String content, String password, Path file) {
}
