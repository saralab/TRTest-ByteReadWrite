package com.tr.sarala.fileReader.executor;

import com.tr.sarala.fileReader.BufferBytesRead;
import com.tr.sarala.fileReader.LastBytesRead;

import java.io.FileInputStream;
import java.nio.channels.FileChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by S on 2/10/16.
 * This Executor needs some more work
 * Ideally the reader and printer would be  Runnables that will run indefnitely or have a set TimeOut
 *
 */
public class ByteExecutor {

    public static void main(String[] args) {
        final LastBytesRead byteReader = new BufferBytesRead(10);
        ExecutorService executor = Executors.newFixedThreadPool(2);

        try {
            executor.execute(new Runnable() {
                public void run() {
                    try {
                        FileInputStream inputFile = new FileInputStream("/Users/S/TR/batman.txt");
                        FileChannel fileChannel = inputFile.getChannel();
                        int fileIndex;

                        while ((fileIndex = inputFile.read()) != -1) {
                            byteReader.recordByte((byte) inputFile.read());
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });

            executor.execute(new Runnable() {
                public void run() {
                    try {
                        byteReader.printLastBytes();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {

                    }
                }
            });
        }
        finally {
            executor.shutdown();
        }

    }
}
