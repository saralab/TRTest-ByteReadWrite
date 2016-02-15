package com.tr.sarala.fileReader;


import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by S on 2/10/16.
 */
public class BufferBytesRead implements LastBytesRead {
    //For large files, using a Queue might be a better option here
    static ByteBuffer buffer;

    private static String defaultCharSet = "UTF-8";
    private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    public BufferBytesRead(int bufferSize) {
        initBuffer(bufferSize);
    }

    private void initBuffer(int bufferSize) {
        //Setting the default charset to UTF-8 for now
        Charset charset = Charset.forName(defaultCharSet);
        CharsetDecoder bufferDecoder = charset.newDecoder();
        CharsetEncoder bufferEncoder = charset.newEncoder();

        buffer = ByteBuffer.allocate(bufferSize);
    }

    @Override
    /**
     * recordByte simply puts the input byte to teh Buffer
     * If the buffer has no more capacity, it shifts the buffer left by a position
     * No write locks are being used to make sure there aren't any performance bottlenecks
     * Only Read locks are needed for now to prevent any dirty reads
     */
    public void recordByte(byte byteToWrite) {
        //If the buffer is full, shift it a byte to the left
        if (buffer.remaining() <= 0) {
            leftShiftBuffer(1);
        }
        if (buffer.hasRemaining()) {
            buffer.put(byteToWrite);
            int pos = buffer.position();
            int rem = buffer.remaining();
        }
    }

    @Override
    /**
     * This method acquires a readlock on the buffer and reads the latest bytes from the buffer and then flips the buffer to get ready for any next reads
     *
     */
    public String printLastBytes() {
        byte[] latestBytes = new byte[1024];
        String readBytesAsString;

        //Make sure that the reads are synchronized! Without this Dirty reads might be a possibility
        readWriteLock.readLock().lock();

        try {
            int remaining = buffer.remaining();

            while (remaining > 0) {
                while (buffer.hasRemaining()) {
                    latestBytes[remaining] = buffer.get(); // read 1 byte at a time
                }
                --remaining;
            }

            readBytesAsString = new String(latestBytes, Charset.forName(defaultCharSet));
            //Unsure if a clear is needed here or a flip will suffice?
            //Keeping it to flip so the position is reset to 1, if the buffer needs to be cleared after every read, use a clear
            //buffer.clear();
            buffer.flip();

        } finally {
            //Release lock and make sure, it's writeable again
            readWriteLock.readLock().unlock();
        }
        return readBytesAsString;
    }

    /**
     * This method shifts the buffer contents to the left but a given bytes
     *
     * @param bytesToShift
     */
    void leftShiftBuffer(int bytesToShift) {

        int index = 0;
        for (int i = bytesToShift; i < buffer.limit(); i++) {
            buffer.put(index++, buffer.get(i));
            buffer.put(i, (byte) 0);
        }
        buffer.position(buffer.position() - 1);
    }
}
