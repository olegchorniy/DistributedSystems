package kpi.ipt.labs.distributed.rabbitmq.common;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileCounter {

    private final Path counterPath;

    public FileCounter(Path counterPath) {
        this.counterPath = counterPath;
    }

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
