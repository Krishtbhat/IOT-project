package code;


import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import javax.comm.CommPortIdentifier;
import javax.comm.PortInUseException;
import javax.comm.SerialPort;
import javax.comm.UnsupportedCommOperationException;

import java.io.*;

public class SimpleRead {
	private static final  char[]COMMAND = {'*', 'R', 'D', 'Y', '*'};
	private static final int WIDTH = 320; //640;
    private static final int HEIGHT = 240; //480;
    	
    private static CommPortIdentifier portId;
    InputStream inputStream;
    SerialPort serialPort;

    public static void main(String[] args) {
    	 Enumeration portList = CommPortIdentifier.getPortIdentifiers();

        while (portList.hasMoreElements()) {
        	portId = (CommPortIdentifier) portList.nextElement();
            if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
            	System.out.println("Port name: " + portId.getName());
                if (portId.getName().equals("COM5")) {
                	SimpleRead reader = new SimpleRead();
                }
            }
        }
    }

    public SimpleRead() {
       	int[][]rgb = new int[HEIGHT][WIDTH];
       	int[][]rgb2 = new int[WIDTH][HEIGHT];
    	
    	try {
            serialPort = (SerialPort) portId.open("SimpleReadApp", 1000);
            inputStream = serialPort.getInputStream();

            serialPort.setSerialPortParams(1000000,
                SerialPort.DATABITS_8,
                SerialPort.STOPBITS_1,
                SerialPort.PARITY_NONE);

			int counter = 0;
			int imageCount = 0;

        	while(imageCount < 5) {
        		System.out.println("Looking for image");
        	
        		while(!isImageStart(inputStream, 0)){};
        	
	        	System.out.println("Found image: " + counter);
	        	
	        	for (int y = 0; y < HEIGHT; y++) {
	        		for (int x = 0; x < WIDTH; x++) {
		       			int temp = read(inputStream);
		    			rgb[y][x] = ((temp&0xFF) << 16) | ((temp&0xFF) << 8) | (temp&0xFF);
	        		}
	        	}
	        	
	        	for (int y = 0; y < HEIGHT; y++) {
		        	for (int x = 0; x < WIDTH; x++) {
		        		rgb2[x][y]=rgb[y][x];
		        	}	        		
	        	}
	        	
		        BMP bmp = new BMP();
	      		bmp.saveBMP("D:/BMSCE/sem5/IOT-project/images/" + (counter++) + ".bmp", rgb2);
	      		
				System.out.println("Saved image: " + counter);
				imageCount += 1;  
        	}
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    private int read(InputStream inputStream) throws IOException {
    	int temp = (char) inputStream.read();
		if (temp == -1) {
			throw new  IllegalStateException("Exit");
		}
		return temp;
    }
    	
    private boolean isImageStart(InputStream inputStream, int index) throws IOException {
    	if (index < COMMAND.length) {
    		if (COMMAND[index] == read(inputStream)) {
    			return isImageStart(inputStream, ++index);
    		} else {
    			return false;
    		}
    	}
    	return true;
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