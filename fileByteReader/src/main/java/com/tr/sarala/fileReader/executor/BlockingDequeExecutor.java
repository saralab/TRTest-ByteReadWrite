package com.tr.sarala.fileReader.executor;

import com.tr.sarala.fileReader.BlockingDequeRead;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by S on 2/15/16.
 */
public class BlockingDequeExecutor {

    public static void main(String[] args) throws IOException {
        BlockingDeque<Byte> byteQueue = new LinkedBlockingDeque<Byte>(100);
        final BlockingDequeRead reader = new BlockingDequeRead(byteQueue);
        final Random r = new Random();
        final int THREAD_POOL_SIZE = 1000;
        int BUFFER_SIZE = 1024;

        ExecutorService executor = Executors.newFixedThreadPool(BUFFER_SIZE);

        try {
            Runnable recordTask = new Runnable() {
                public void run() {
                    try {
                        reader.recordByte((byte) (r.nextInt(26) + 'a'));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };

            Runnable printTask = new Runnable() {
                public void run() {
                    try {
                        System.out.println("Printing the last recorded bytes: " + reader.printLastBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };

            for (int i = 0; i < THREAD_POOL_SIZE; i++) {
                executor.execute(new Thread(recordTask));
                executor.execute(new Thread(printTask));
            }
        } finally {
            executor.shutdown();
        }
    }
}
