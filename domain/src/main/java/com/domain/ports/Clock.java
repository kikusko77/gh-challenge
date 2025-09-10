package com.domain.ports;

import java.time.Instant;

public interface Clock {
    Instant now();
}
