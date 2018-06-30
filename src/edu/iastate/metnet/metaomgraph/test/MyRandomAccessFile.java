package edu.iastate.metnet.metaomgraph.test;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.EOFException;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import java.io.UTFDataFormatException;
import java.util.Date;
import java.util.Random;


public class MyRandomAccessFile
        implements DataInput, DataOutput {
    public static final int READ = 1;
    public static final int WRITE = 2;
    public static final int CREATE = 4;
    protected static final int defaultBufferSize = 4096;
    protected RandomAccessFile file;
    protected long filePosition;
    protected byte[] buffer;
    protected long bufferStart;
    protected long dataEnd;
    protected int dataSize;
    protected boolean endOfFile;
    protected int mode;
    boolean bufferModified = false;


    private StringBuffer myLineBuffer = null;


    protected MyRandomAccessFile(int bufferSize) {
        bufferStart = 0L;
        dataEnd = 0L;
        dataSize = 0;
        filePosition = 0L;
        buffer = new byte[bufferSize];
        endOfFile = false;
    }


    public MyRandomAccessFile(String filename, int mode)
            throws IOException {
        this(filename, mode, 4096);
    }


    public MyRandomAccessFile(String filename, String modeString)
            throws IOException {
        this(filename, modeString, 4096);
    }


    public MyRandomAccessFile(String filename, String modeString, int bufferSize)
            throws IOException {
        this(filename, modeString.equals("rw") ? 3 : modeString.equals("r") ? 1 : 0, bufferSize);
    }


    public MyRandomAccessFile(File file, String modeString)
            throws IOException {
        this(file.getPath(), modeString);
    }


    public MyRandomAccessFile(String filename, int mode, int bufferSize)
            throws IOException {
        this.mode = mode;


        mode |= 0x1;
        if ((this.mode & 0x4) > 0) {
            this.mode |= 0x2;
        }


        File checkfile = new File(filename);
        if (((this.mode & 0x2) > 0) && (!checkfile.exists())) {
            mode |= 0x4;
        }


        if (((this.mode & 0x4) > 0) &&
                (checkfile.exists()) &&
                (!checkfile.delete())) {
            throw new IOException("Failed to delete " + filename);
        }


        if ((this.mode == 1) && (!new File(filename).exists())) {
            throw new FileNotFoundException(filename);
        }

        String modeString = (this.mode & 0x2) > 0 ? "rw" : "r";
        file = new RandomAccessFile(filename, modeString);


        bufferStart = 0L;
        dataEnd = 0L;
        dataSize = 0;
        filePosition = 0L;
        buffer = new byte[bufferSize];
        endOfFile = false;
    }


    public void close()
            throws IOException {
        if (((mode | 0x2) > 0) && (bufferModified)) {
            file.seek(bufferStart);
            file.write(buffer, 0, dataSize);
        }


        file.close();
    }


    public void seek(long pos)
            throws IOException {
        if ((pos >= bufferStart) && (pos < dataEnd)) {
            filePosition = pos;
            return;
        }


        if (bufferModified) {
            flush();
        }

        bufferStart = pos;
        filePosition = pos;

        dataSize = read_(pos, buffer, 0, buffer.length);
        if (dataSize < 0) {
            dataSize = 0;
            endOfFile = true;
        } else {
            endOfFile = false;
        }


        dataEnd = (bufferStart + dataSize);
    }


    public long getFilePointer() {
        return filePosition;
    }


    public long length()
            throws IOException {
        long fileLength = file.length();
        if (fileLength < dataEnd) {
            return dataEnd;
        }
        return fileLength;
    }


    public FileDescriptor getFD()
            throws IOException {
        return file.getFD();
    }


    public void flush()
            throws IOException {
        if (bufferModified) {
            file.seek(bufferStart);
            file.write(buffer, 0, dataSize);
            bufferModified = false;
        }
    }


    public final int read()
            throws IOException {
        if (filePosition < dataEnd) {
            return buffer[((int) (filePosition++ - bufferStart))] & 0xFF;
        }

        if (endOfFile) {
            return -1;
        }


        seek(filePosition);
        return read();
    }


    private int readBytes(byte[] b, int off, int len)
            throws IOException {
        if (endOfFile) {
            return -1;
        }


        int bytesAvailable = (int) (dataEnd - filePosition);
        if (bytesAvailable < 1) {
            seek(filePosition);
            return readBytes(b, off, len);
        }


        int copyLength = bytesAvailable >= len ? len : bytesAvailable;
        System.arraycopy(buffer, (int) (filePosition - bufferStart), b, off,
                copyLength);
        filePosition += copyLength;


        if (copyLength < len) {
            int extraCopy = len - copyLength;


            if (extraCopy > buffer.length) {
                extraCopy = read_(filePosition, b, off + copyLength, len -
                        copyLength);
            } else {
                seek(filePosition);
                if (!endOfFile) {
                    extraCopy = extraCopy > dataSize ? dataSize : extraCopy;
                    System.arraycopy(buffer, 0, b, off + copyLength, extraCopy);
                } else {
                    extraCopy = -1;
                }
            }


            if (extraCopy > 0) {
                filePosition += extraCopy;
                return copyLength + extraCopy;
            }
        }


        return copyLength;
    }


    protected int read_(long pos, byte[] b, int offset, int len)
            throws IOException {
        file.seek(pos);
        int n = file.read(b, offset, len);


        return n;
    }


    public int read(byte[] b, int off, int len)
            throws IOException {
        return readBytes(b, off, len);
    }


    public int read(byte[] b)
            throws IOException {
        return readBytes(b, 0, b.length);
    }


    public final void readFully(byte[] b)
            throws IOException {
        readFully(b, 0, b.length);
    }


    public final void readFully(byte[] b, int off, int len)
            throws IOException {
        int n = 0;
        while (n < len) {
            int count = read(b, off + n, len - n);
            if (count < 0)
                throw new EOFException();
            n += count;
        }
    }


    public int skipBytes(int n)
            throws IOException {
        seek(getFilePointer() + n);
        return n;
    }


    public final void unread() {
        filePosition -= 1L;
    }


    public final void write(int b)
            throws IOException {
        if (filePosition < dataEnd) {
            buffer[((int) (filePosition++ - bufferStart))] = ((byte) b);
            bufferModified = true;


        } else if (dataSize != buffer.length) {
            buffer[((int) (filePosition++ - bufferStart))] = ((byte) b);
            bufferModified = true;
            dataSize += 1;
            dataEnd += 1L;
        } else {
            seek(filePosition);
            write(b);
        }
    }


    public final void writeBytes(byte[] b, int off, int len)
            throws IOException {
        if (len < buffer.length) {

            int spaceInBuffer = 0;
            int copyLength = 0;
            if (filePosition >= bufferStart)
                spaceInBuffer = (int) (bufferStart + buffer.length - filePosition);
            if (spaceInBuffer > 0) {

                copyLength = spaceInBuffer > len ? len : spaceInBuffer;
                System.arraycopy(b, off, buffer,
                        (int) (filePosition - bufferStart), copyLength);
                bufferModified = true;
                long myDataEnd = filePosition + copyLength;
                dataEnd = (myDataEnd > dataEnd ? myDataEnd : dataEnd);
                dataSize = ((int) (dataEnd - bufferStart));
                filePosition += copyLength;
            }


            if (copyLength < len) {
                seek(filePosition);
                System.arraycopy(b, off + copyLength, buffer,
                        (int) (filePosition - bufferStart), len - copyLength);
                bufferModified = true;
                long myDataEnd = filePosition + (len - copyLength);
                dataEnd = (myDataEnd > dataEnd ? myDataEnd : dataEnd);
                dataSize = ((int) (dataEnd - bufferStart));
                filePosition += len - copyLength;
            }


        } else {
            if (bufferModified) {
                flush();
                bufferStart = (this.dataEnd = this.dataSize = 0);
            }
            file.write(b, off, len);
            filePosition += len;
        }
    }


    public void write(byte[] b)
            throws IOException {
        writeBytes(b, 0, b.length);
    }


    public void write(byte[] b, int off, int len)
            throws IOException {
        writeBytes(b, off, len);
    }


    public final boolean readBoolean()
            throws IOException {
        int ch = read();
        if (ch < 0)
            throw new EOFException();
        return ch != 0;
    }


    public final byte readByte()
            throws IOException {
        int ch = read();
        if (ch < 0)
            throw new EOFException();
        return (byte) ch;
    }


    public final int readUnsignedByte()
            throws IOException {
        int ch = read();
        if (ch < 0)
            throw new EOFException();
        return ch;
    }


    public final short readShort()
            throws IOException {
        int ch1 = read();
        int ch2 = read();
        if ((ch1 | ch2) < 0)
            throw new EOFException();
        return (short) ((ch1 << 8) + (ch2 << 0));
    }


    public final int readUnsignedShort()
            throws IOException {
        int ch1 = read();
        int ch2 = read();
        if ((ch1 | ch2) < 0)
            throw new EOFException();
        return (ch1 << 8) + (ch2 << 0);
    }


    public final char readChar()
            throws IOException {
        int ch1 = read();
        int ch2 = read();
        if ((ch1 | ch2) < 0)
            throw new EOFException();
        return (char) ((ch1 << 8) + (ch2 << 0));
    }


    public final int readInt()
            throws IOException {
        int ch1 = read();
        int ch2 = read();
        int ch3 = read();
        int ch4 = read();
        if ((ch1 | ch2 | ch3 | ch4) < 0)
            throw new EOFException();
        return (ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0);
    }


    public final long readLong()
            throws IOException {
        return (readInt() << 32) + (readInt() & 0xFFFFFFFF);
    }


    public final float readFloat()
            throws IOException {
        return Float.intBitsToFloat(readInt());
    }


    public final double readDouble()
            throws IOException {
        return Double.longBitsToDouble(readLong());
    }


    public final String readLine()
            throws IOException {
        if (myLineBuffer == null)
            myLineBuffer = new StringBuffer();
        myLineBuffer.setLength(0);

        int c;
        while (((c = read()) != -1) && (c != 10) && (c != 13)) {
            myLineBuffer.append((char) c);
        }
        if ((c == -1) && (myLineBuffer.length() == 0)) {
            return null;
        }
        if ((c == 13) &&
                (peek() == '\n')) {
            read();
        }

        return myLineBuffer.toString();
    }


    public final String readUTF()
            throws IOException {
        return DataInputStream.readUTF(this);
    }


    public final void writeBoolean(boolean v)
            throws IOException {
        write(v ? 1 : 0);
    }


    public final void writeByte(int v)
            throws IOException {
        write(v);
    }


    public final void writeShort(int v)
            throws IOException {
        write(v >>> 8 & 0xFF);
        write(v >>> 0 & 0xFF);
    }


    public final void writeChar(int v)
            throws IOException {
        write(v >>> 8 & 0xFF);
        write(v >>> 0 & 0xFF);
    }


    public final void writeInt(int v)
            throws IOException {
        write(v >>> 24 & 0xFF);
        write(v >>> 16 & 0xFF);
        write(v >>> 8 & 0xFF);
        write(v >>> 0 & 0xFF);
    }


    public final void writeLong(long v)
            throws IOException {
        write((int) (v >>> 56) & 0xFF);
        write((int) (v >>> 48) & 0xFF);
        write((int) (v >>> 40) & 0xFF);
        write((int) (v >>> 32) & 0xFF);
        write((int) (v >>> 24) & 0xFF);
        write((int) (v >>> 16) & 0xFF);
        write((int) (v >>> 8) & 0xFF);
        write((int) (v >>> 0) & 0xFF);
    }


    public final void writeFloat(float v)
            throws IOException {
        writeInt(Float.floatToIntBits(v));
    }


    public final void writeDouble(double v)
            throws IOException {
        writeLong(Double.doubleToLongBits(v));
    }


    public final void writeBytes(String s)
            throws IOException {
        int len = s.length();
        for (int i = 0; i < len; i++) {
            write((byte) s.charAt(i));
        }
    }


    public final void writeBytes(char[] b, int off, int len)
            throws IOException {
        for (int i = off; i < len; i++) {
            write((byte) b[i]);
        }
    }


    public final void writeChars(String s)
            throws IOException {
        int len = s.length();
        for (int i = 0; i < len; i++) {
            int v = s.charAt(i);
            write(v >>> 8 & 0xFF);
            write(v >>> 0 & 0xFF);
        }
    }


    public final void writeUTF(String str)
            throws IOException {
        int strlen = str.length();
        int utflen = 0;

        for (int i = 0; i < strlen; i++) {
            int c = str.charAt(i);
            if ((c >= 1) && (c <= 127)) {
                utflen++;
            } else if (c > 2047) {
                utflen += 3;
            } else {
                utflen += 2;
            }
        }
        if (utflen > 65535) {
            throw new UTFDataFormatException();
        }
        write(utflen >>> 8 & 0xFF);
        write(utflen >>> 0 & 0xFF);
        for (int i = 0; i < strlen; i++) {
            int c = str.charAt(i);
            if ((c >= 1) && (c <= 127)) {
                write(c);
            } else if (c > 2047) {
                write(0xE0 | c >> 12 & 0xF);
                write(0x80 | c >> 6 & 0x3F);
                write(0x80 | c >> 0 & 0x3F);
            } else {
                write(0xC0 | c >> 6 & 0x1F);
                write(0x80 | c >> 0 & 0x3F);
            }
        }
    }


    public String toString() {
        return

                "fp=" + filePosition + ", bs=" + bufferStart + ", de=" + dataEnd + ", ds=" + dataSize + ", bl=" + buffer.length + ", m=" + mode + ", bm=" + bufferModified;
    }


    public static void testBytes(String filename, int bufferSize) {
        System.out.println("\nTesting byte operations...");
        int newFileSize = (int) (bufferSize * 4.5D);


        try {
            MyRandomAccessFile outFile = new MyRandomAccessFile(filename,
                    6,
                    bufferSize);
            try {
                Random random = new Random(0L);
                byte b = 0;
                for (int i = 0; i < newFileSize; i++) {
                    b = (byte) (random.nextInt() % 256);
                    outFile.writeByte(b);
                }
            } finally {
                outFile.close();
            }


            if (new File(filename).length() == newFileSize) {
                System.out.println(". File size correct (" + newFileSize + ").");
            } else {
                System.out.println("X New file size incorrect (should be " +
                        newFileSize + ", but is " +
                        new File(filename).length() + ").");
            }

            MyRandomAccessFile inoutFile = new MyRandomAccessFile(filename,
                    3, bufferSize);

            boolean verified = true;
            int byteNo = 0;

            try {
                Random random = new Random(0L);
                byte b = 0;
                for (byteNo = 0; byteNo < newFileSize; byteNo++) {
                    b = (byte) (random.nextInt() % 256);
                    byte currentByte = inoutFile.readByte();


                    if (currentByte != b) {
                        verified = false;
                    }

                    if (currentByte >= 128) {
                        inoutFile.seek(inoutFile.getFilePointer() - 1L);
                        inoutFile.writeByte(0);
                    }
                }


                boolean foundEOF = false;
                try {
                    inoutFile.readByte();
                } catch (EOFException e) {
                    foundEOF = true;
                }
                if (foundEOF) {
                    System.err.println(". EOF found correctly");
                } else {
                    System.err.println("X No EOF found.");
                }
            } catch (EOFException e) {
                e.printStackTrace();
                System.err.println("    At byte " + byteNo);
            } finally {
                inoutFile.close();
            }


            if (verified) {
                System.out.println(". Read/Write verified");
            } else {
                System.out.println("X Read/Write verification failed");
            }

            MyRandomAccessFile inFile = new MyRandomAccessFile(filename,
                    1, bufferSize);

            verified = true;
            byteNo = 0;

            try {
                Random random = new Random(0L);
                byte b = 0;
                for (byteNo = 0; byteNo < newFileSize; byteNo++) {
                    b = (byte) (random.nextInt() % 256);
                    byte currentByte = inFile.readByte();

                    if (currentByte >= 128) {
                        currentByte = 0;
                    }

                    if (currentByte != b) {
                        verified = false;
                    }
                }
            } catch (EOFException e) {
                e.printStackTrace();
                System.err.println("    At byte " + byteNo);
            } finally {
                inFile.close();
            }

            if (verified) {
                System.out.println(". Update verified");
            } else {
                System.out.println("X Update verification failed");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void testBlocks(String filename) {
        System.err.println("\nTesting block operations...");

        int bufferSize = 10;
        byte[] data = new byte[256];
        for (int i = 0; i < data.length; i++) {
            data[i] = ((byte) (i % 256));
        }

        try {
            MyRandomAccessFile outFile = new MyRandomAccessFile(filename, 6, bufferSize);
            for (int i = 0; i < data.length; ) {
                int blockSize = i < data.length / 2 ? 3 : 13;
                blockSize = i + blockSize >= data.length ? data.length - i : blockSize;
                outFile.write(data, i, blockSize);
                i += blockSize;
            }

            outFile.close();

            if (new File(filename).length() != data.length) {
                System.out.println("X New file size incorrect (should be " + data.length + ", but is " + new File(filename).length() + ").");
            } else {
                System.out.println(". File size correct (" + data.length + ").");
            }

            MyRandomAccessFile inFile = new MyRandomAccessFile(filename, 1, bufferSize);


            boolean verified = true;
            int firstFailure = 256;
            Random random = new Random(0L);
            byte[] block = new byte[(int) (bufferSize * 0.5D)];
            for (int i = 0; i < 100; i++) {
                int index = Math.abs(random.nextInt()) % (data.length - block.length);
                inFile.seek(index);
                inFile.read(block);

                for (int j = 0; j < block.length; j++) {
                    if (block[j] != data[(index + j)]) {
                        verified = false;
                        if (index + j < firstFailure)
                            firstFailure = index + j;
                    }
                }
            }
            if (verified) {
                System.err.println(". Reading small blocks verified.");
            } else {
                System.err.println("X Reading small blocks failed (byte " + firstFailure + ").");
            }

            verified = true;
            random = new Random(0L);
            block = new byte[(int) (bufferSize * 1.5D)];
            for (int i = 0; i < 100; i++) {
                int index = Math.abs(random.nextInt()) % (data.length - block.length);
                inFile.seek(index);
                inFile.read(block);

                for (int j = 0; j < block.length; j++) {
                    if (block[j] != data[(j + index)]) verified = false;
                }
            }
            if (verified) {
                System.err.println(". Reading large blocks verified.");
            } else {
                System.err.println("X Reading large blocks failed.");
            }

            inFile.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void benchmark(String filename, int bufferSize) {
        System.out.println("\nBenchmarking...");

        long time = new Date().getTime();
        try {
            MyRandomAccessFile inFile = new MyRandomAccessFile(filename, 1, bufferSize);
            MyRandomAccessFile outFile = new MyRandomAccessFile("temp.data", 6, bufferSize);

            try {
                for (; ; ) {
                    outFile.writeByte(inFile.readByte());
                }
            } catch (EOFException localEOFException) {
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                inFile.close();
                outFile.close();
            }
            System.out.println(". RandomAccessFile elapsed time=" + (new Date().getTime() - time));


            time = new Date().getTime();
            RandomAccessFile inFile2 = new RandomAccessFile(filename, "r");
            RandomAccessFile outFile2 = new RandomAccessFile("temp.data", "rw");

            try {
                while (true) {
                    outFile2.writeByte(inFile2.readByte());
                }
            } catch (EOFException localEOFException1) {
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                inFile2.close();
                outFile2.close();
            }
        } catch (Exception e) {
            e.printStackTrace();

            System.out.println(". java.io.RandomAccessFile elapsed time=" + (
                    new Date().getTime() - time));
        }
    }


    public static void main(String[] argv) {
        int defaultPageSize = 4096;


        String filename = null;
        int bufferSize = 0;
        boolean test = true;
        boolean benchmark = true;
        if (argv.length < 1) {
            System.err.println("Usage: RandomAccessFile <filename> [buffer.length] [benchmark | test]");
            System.exit(-1);
        } else if (argv.length < 2) {
            filename = argv[0];
            bufferSize = defaultPageSize;
        } else if (argv.length < 3) {
            filename = argv[0];
            bufferSize = Integer.parseInt(argv[1]);
        } else {
            filename = argv[0];
            bufferSize = Integer.parseInt(argv[1]);
            if (argv[2].equals("benchmark")) {
                test = false;
            } else if (argv[2].equals("test")) {
                benchmark = false;
            }
        }
        System.out.println("\nRandomAccessFile\n========================");
        System.out.println("filename=" + filename + ", bufferSize=" +
                bufferSize);
        System.out.println("totalMemory=" +
                Runtime.getRuntime().totalMemory() / 1000L + "k" +
                " freeMemory=" + Runtime.getRuntime().freeMemory() / 1000L +
                "k");

        if (test) {
            testBytes("temp.data", bufferSize);
            testBlocks("temp.data");
        }
        if (benchmark) {
            benchmark(filename, bufferSize);
        }

        System.out.println("\nEND");
    }


    public final String readString(char delimiter)
            throws IOException {
        StringBuffer buf = new StringBuffer();
        int c;
        while (((c = read()) != -1) && (c != delimiter) && (c != 10) && (c != 13)) {
            buf.append((char) c);
        }
        if ((c == -1) && (buf.length() == 0)) {
            return null;
        }
        if ((c == 13) &&
                (peek() == '\n')) {
            read();
        }
        while (peek() == delimiter)
            read();
        return buf.toString();
    }


    public final String readString()
            throws IOException {
        return readString('\t');
    }


    public final boolean nextLine()
            throws IOException {
        int c;


        while (((c = read()) != -1) && (c != 10) && (c != 13)) {
        }

        if (c == -1)
            return false;
        if ((c == 13) &&
                (peek() == '\n')) {
            read();
        }
        return true;
    }


    public final char peek()
            throws IOException {
        long startPoint = getFilePointer();
        int c = read();
        seek(startPoint);
        if (c == -1) {
            return '\000';
        }
        return (char) c;
    }

    public final void insert(String s) throws IOException {
        long addHere = getFilePointer();
        byte[] overWritten = new byte[s.length()];
        int bytesRead = read(overWritten);
        seek(addHere);
        writeBytes(s);
        while (bytesRead >= 0) {
            byte[] writeUs = overWritten;
            int bytesToWrite = bytesRead;
            overWritten = new byte[writeUs.length];
            addHere = getFilePointer();
            bytesRead = read(overWritten);
            seek(addHere);
            write(writeUs, 0, bytesToWrite);
        }
    }
}
