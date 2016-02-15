package com.tr.sarala.fileReader;

import java.io.IOException;

/**
 *
 *
 */
public interface LastBytesRead {

    public void recordByte(byte b);

    public String printLastBytes() throws IOException;
}
