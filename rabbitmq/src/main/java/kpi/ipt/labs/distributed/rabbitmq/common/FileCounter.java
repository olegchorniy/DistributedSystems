package kpi.ipt.labs.distributed.rabbitmq.common;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileCounter implements MonotonicCounter {

    private final Path counterPath;

    public FileCounter(Path counterPath) {
        if (!Files.exists(counterPath)) {
            throw new IllegalStateException(counterPath + " doesn't exist.");
        }

        if (!Files.isRegularFile(counterPath)) {
            throw new IllegalStateException(counterPath + " is not a file.");
        }

        this.counterPath = counterPath;
    }

    @Override
    public int getAndIncrement() {
        try {
            int oldValue = read();
            write(oldValue + 1);

            return oldValue;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private int read() throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(counterPath)) {
            return Integer.parseInt(reader.readLine());
        }
    }

    private void write(int newValue) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(counterPath)) {
            writer.append(Integer.toString(newValue));
        }
    }
}
