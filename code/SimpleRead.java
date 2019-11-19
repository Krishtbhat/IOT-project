package code;

import javax.comm.*;
import java.util.*;
import java.io.*;
import java.io.*;


public class SimpleRead
{
    private static final char[] COMMAND;
    private static final int WIDTH = 320;
    private static final int HEIGHT = 240;
    private static CommPortIdentifier portId;
    InputStream inputStream;
    SerialPort serialPort;
    
    public static void main(final String[] args) {
        final Enumeration portIdentifiers = CommPortIdentifier.getPortIdentifiers();
        int image_count = Integer.parseInt(args[0]);
        while (portIdentifiers.hasMoreElements()) {
            portId = (CommPortIdentifier) portIdentifiers.nextElement();
            if (SimpleRead.portId.getPortType() == 1) {
                System.out.println("Port name: " + SimpleRead.portId.getName());
                if (!SimpleRead.portId.getName().equals("COM4")) {
                    continue;
                }
                new SimpleRead(image_count);
            }
        }
    }
    
    public SimpleRead(int n) {
        final int[][] array = new int[HEIGHT][WIDTH];
        final int[][] array2 = new int[WIDTH][HEIGHT];
        try {
            this.serialPort = (SerialPort)SimpleRead.portId.open("SimpleReadApp", 1000);
            this.inputStream = this.serialPort.getInputStream();
            this.serialPort.setSerialPortParams(1000000, 8, 1, 0);

            System.out.println("Looking for image");
            while (!this.isImageStart(this.inputStream, 0)) {}
            System.out.println("Found image: " + n);
            for (int i = 0; i < HEIGHT; ++i) {
                for (int j = 0; j < WIDTH; ++j) {
                    final int read = this.read(this.inputStream);
                    array[i][j] = ((read & 0xFF) << 16 | (read & 0xFF) << 8 | (read & 0xFF));
                }
            }
            for (int k = 0; k < HEIGHT; ++k) {
                for (int l = 0; l < WIDTH; ++l) {
                    array2[l][k] = array[k][l];
                }
            }
            new BMP().saveBMP("D:/IOT-project/images/" + n + ".bmp", array2);
            System.out.println("Saved image: " + (n));

        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private int read(final InputStream inputStream) throws IOException {
        final char c = (char)inputStream.read();
        if (c == -1) {
            throw new IllegalStateException("Exit");
        }
        return c;
    }
    
    private boolean isImageStart(final InputStream inputStream, int n) throws IOException {
        return n >= SimpleRead.COMMAND.length || (SimpleRead.COMMAND[n] == this.read(inputStream) && this.isImageStart(inputStream, ++n));
    }
    
    static {
        COMMAND = new char[] { '*', 'R', 'D', 'Y', '*' };
    }
}

class BMP
{
    byte[] bytes;
    
    public int[][] readBMP(final String s) {
        final byte[] array = new byte[54];
        final int[][] array2 = null;
        int[][] array3;
        try {
            final FileInputStream fileInputStream = new FileInputStream(new File(s));
            fileInputStream.read(array, 0, array.length);
            final int n = ((array[21] & 0xFF) << 24) + ((array[20] & 0xFF) << 16) + ((array[19] & 0xFF) << 8) + (array[18] & 0xFF);
            final int n2 = ((array[25] & 0xFF) << 24) + ((array[24] & 0xFF) << 16) + ((array[23] & 0xFF) << 8) + (array[22] & 0xFF);
            array3 = new int[n2][n];
            for (int i = 0; i < n2; ++i) {
                for (int j = 0; j < n; ++j) {
                    fileInputStream.read(array, 0, 3);
                    array3[i][j] = ((array[2] & 0xFF) << 16) + ((array[1] & 0xFF) << 8) + (array[0] & 0xFF);
                }
            }
        }
        catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
        return array3;
    }
    
    public void saveBMP(final String s, final int[][] array) {
        try {
            final FileOutputStream fileOutputStream = new FileOutputStream(new File(s));
            this.bytes = new byte[54 + 3 * array.length * array[0].length];
            this.saveFileHeader();
            this.saveInfoHeader(array.length, array[0].length);
            this.saveBitmapData(array);
            fileOutputStream.write(this.bytes);
            fileOutputStream.close();
        }
        catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
    }
    
    private void saveFileHeader() {
        this.bytes[0] = 66;
        this.bytes[1] = 77;
        this.bytes[5] = (byte)this.bytes.length;
        this.bytes[4] = (byte)(this.bytes.length >> 8);
        this.bytes[3] = (byte)(this.bytes.length >> 16);
        this.bytes[2] = (byte)(this.bytes.length >> 24);
        this.bytes[10] = 54;
    }
    
    private void saveInfoHeader(final int n, final int n2) {
        this.bytes[14] = 40;
        this.bytes[18] = (byte)n2;
        this.bytes[19] = (byte)(n2 >> 8);
        this.bytes[20] = (byte)(n2 >> 16);
        this.bytes[21] = (byte)(n2 >> 24);
        this.bytes[22] = (byte)n;
        this.bytes[23] = (byte)(n >> 8);
        this.bytes[24] = (byte)(n >> 16);
        this.bytes[25] = (byte)(n >> 24);
        this.bytes[26] = 1;
        this.bytes[28] = 24;
    }
    
    private void saveBitmapData(final int[][] array) {
        for (int i = 0; i < array.length; ++i) {
            this.writeLine(i, array);
        }
    }
    
    private void writeLine(final int n, final int[][] array) {
        for (int length = array[n].length, i = 0; i < length; ++i) {
            final int n2 = array[n][i];
            final int n3 = 54 + 3 * (i + length * n);
            this.bytes[n3 + 2] = (byte)(n2 >> 16);
            this.bytes[n3 + 1] = (byte)(n2 >> 8);
            this.bytes[n3] = (byte)n2;
        }
    }
}