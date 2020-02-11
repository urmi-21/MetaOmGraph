package edu.iastate.metnet.metaomgraph.utils;

import java.io.IOException;

public class ByteArrayDataSource implements javax.activation.DataSource {
    private byte[] data;
    private String type;

    public ByteArrayDataSource(java.io.InputStream is, String type) {
        this.type = type;
        try {
            java.io.ByteArrayOutputStream os = new java.io.ByteArrayOutputStream();

            int ch;
            while ((ch = is.read()) != -1) {
                os.write(ch);
            }
            data = os.toByteArray();
        } catch (IOException localIOException) {
        }
    }


    public ByteArrayDataSource(byte[] data, String type) {
        this.data = data;
        this.type = type;
    }


    public ByteArrayDataSource(String data, String type) {
        try {
            this.data = data.getBytes("iso-8859-1");
        } catch (java.io.UnsupportedEncodingException localUnsupportedEncodingException) {
        }
        this.type = type;
    }

    @Override
	public java.io.InputStream getInputStream() throws IOException {
        if (data == null)
            throw new IOException("no data");
        return new java.io.ByteArrayInputStream(data);
    }

    @Override
	public java.io.OutputStream getOutputStream() throws IOException {
        throw new IOException("cannot do this");
    }

    @Override
	public String getContentType() {
        return type;
    }

    @Override
	public String getName() {
        return "dummy";
    }
}
