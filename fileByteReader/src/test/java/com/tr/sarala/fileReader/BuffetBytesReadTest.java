package com.tr.sarala.fileReader;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

/**
 *
 */
public class BuffetBytesReadTest {

    BufferBytesRead testObject;


    @Before
    public void setUp() throws IOException {
        Path path = Paths.get("/Users/S/TR/inputFile.txt");
    }

    @Test
    public void printLastByte_returnsAValidString() {
        int[] input = {'F', 'L', 'A', 'S', 'H', '!'};
        testObject = new BufferBytesRead(input.toString().getBytes().length);

        testObject.buffer.clear();

        testObject.buffer.putChar('F');
        testObject.buffer.putChar('L');
        testObject.buffer.putChar('A');
        testObject.buffer.putChar('S');
        testObject.buffer.putChar('H');


        testObject.leftShiftBuffer(0);

        Assert.assertEquals(11, testObject.buffer.capacity());
        Assert.assertEquals(9, testObject.buffer.position());
        Assert.assertEquals(11, testObject.buffer.limit());

        System.out.println(Arrays.toString(testObject.buffer.array()));

        testObject.leftShiftBuffer(1);

        Assert.assertEquals(11, testObject.buffer.capacity());
        Assert.assertEquals(8, testObject.buffer.position());
        Assert.assertEquals(11, testObject.buffer.limit());


        testObject.buffer.clear();
    }

}
