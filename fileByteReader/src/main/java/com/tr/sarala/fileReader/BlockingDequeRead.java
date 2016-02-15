package com.tr.sarala.fileReader;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by S on 2/15/16.
 * This Recorder and Printer uses a BlockingDeque to recordbytes
 * Albeit the records will be at a O(1) order , there might be a performance hit when using the BlockingDeque
 */
public class BlockingDequeRead implements LastBytesRead {
    private static BlockingDeque<Byte> queue;
    private static List<Byte> byteList = new ArrayList<Byte>();
    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public BlockingDequeRead(BlockingDeque<Byte> queue) {
        this.queue = queue;
    }

    @Override
    /**
     * Check if the Queue limit has reached
     * If not, simply insert the latest byte into the Queue
     * Otherwise, remove the oldest entry in the queue and insert the latest byte
     */
    public void recordByte(byte b) {
        if (queue.remainingCapacity() > 0) {
            System.out.println("Recording byte : " + b);
            queue.offer(b);
        } else {
            System.out.println("Freeing bytes " + queue.removeFirst());
            try {
                System.out.println("Recording byte after freeing space " + queue.removeFirst());
                queue.put(b);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    /**
     * Drain the elements of the current queue to a byteArray and return a String
     *
     */
    public String printLastBytes() throws IOException {
        //Copying the queue contents to a ArrayListof Bytes , make sure we acquire a write lock
        lock.writeLock().lock();

        queue.drainTo(byteList);
        Object[] byteArray = byteList.toArray();

        lock.writeLock().unlock();

        //Being reading from the copies ArrayList, use a read lock
        lock.readLock().lock();

        byte[] byteString = new byte[byteArray.length];

        for (int i = 0; i < byteArray.length; i++) {
            byteString[i] = ((Byte) byteArray[i]).byteValue();
        }

        lock.readLock().unlock();


        return new String(byteString, Charset.defaultCharset());
    }

}
