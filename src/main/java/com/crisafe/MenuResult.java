package com.crisafe;

import java.util.Optional;

public record MenuResult(Operation operation, Archive archive, Optional<String> filter) {
}
