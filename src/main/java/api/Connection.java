package api;

import utils.Node;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;

public class Connection {
    private final Node node;
    private final Socket socket;
    private final DataInputStream inputStream;
    private final DataOutputStream outputStream;

    public Connection(Socket socket) throws IOException {
        this.socket = socket;
        this.node = new Node(socket.getInetAddress().getHostName(), socket.getPort());
        this.inputStream = new DataInputStream(socket.getInputStream());
        this.outputStream = new DataOutputStream(socket.getOutputStream());
    }

    public byte[] receive(){
        byte[] buffer = null;
        try {
            int length = this.inputStream.readInt();
            if (length > 0) {
                buffer = new byte[length];
                this.inputStream.readFully(buffer, 0, buffer.length);
            }
        } catch (EOFException ignored) {
        } //No more content available to read
        catch (IOException exception) {
            exception.printStackTrace();
        }
        return buffer;
    }

    public boolean send(byte[] message){
        try {
            this.outputStream.writeInt(message.length);
            this.outputStream.write(message);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int getPort(){
        return this.node.getPort();
    }

    public boolean isClosed(){
        return this.socket.isClosed();
    }

    public void close() throws IOException {
        this.inputStream.close();
        this.outputStream.close();
        this.socket.close();
    }

    @Override
    public String toString() {
        return this.node.getHostName() + ":" + this.node.getPort();
    }
}
