package me.task.api;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TaskApiSampleTest {
    @Test
    void helloReturnsExpected() {
        assertEquals("Hello from TaskAPI", TaskApiSample.hello());
    }
}

