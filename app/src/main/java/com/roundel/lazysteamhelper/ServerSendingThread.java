package com.roundel.lazysteamhelper;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.Objects;

/**
 * Created by Krzysiek on 16/05/2017.
 */

@SuppressWarnings("ResultOfMethodCallIgnored")
public class ServerSendingThread extends Thread
{
    private static final String TAG = ServerSendingThread.class.getSimpleName();

    private static final String JSON_CODE = "code";
    private static final String JSON_COM = "com";          //Communication stage
    private static final String JSON_USERNAME = "username";
    private static final String CONNECTION_REQUEST = "LAZY_STEAM_HELPER_CONNECTION_REQUEST";
    private static final String CONNECTION_RESPONSE = "LAZY_STEAM_HELPER_CONNECTION_RESPONSE";
    private static final String DATA = "LAZY_STEAM_HELPER_DATA";
    private static final String RECEIVE_RESPONSE = "LAZY_STEAM_HELPER_RECEIVE_RESPONSE";

    private LazyServer mTarget;
    private String mCode;
    private String mUsername;

    private int mConnectionTimeout = 5000;
    private int mReceiveTimeout = 5000;

    public ServerSendingThread(LazyServer target, String code, String username)
    {
        this.mTarget = target;
        this.mCode = code;
        this.mUsername = username;
    }

    @Override
    public void run()
    {
        try
        {
            Socket lazySocket = new Socket();
            lazySocket.connect(new InetSocketAddress(mTarget.getHost(), mTarget.getPort()), mConnectionTimeout);
            lazySocket.setSoTimeout(mReceiveTimeout);

            JSONObject json = new JSONObject();
            json.put(JSON_COM, CONNECTION_REQUEST);

            sendJSON(lazySocket, json);
            JSONObject response = receiveJSON(lazySocket);

            if(! Objects.equals(CONNECTION_RESPONSE, response.getString(JSON_COM)))
            {
                throw new IllegalStateException("Didn't receive a proper response");
            }

            json = new JSONObject();
            json.put(JSON_COM, DATA);
            json.put(JSON_CODE, mCode);
            json.put(JSON_USERNAME, mUsername);

            sendJSON(lazySocket, json);
            response = receiveJSON(lazySocket);

            if(!Objects.equals(RECEIVE_RESPONSE, response.getString(JSON_COM)))
            {
                throw new IllegalStateException("Didn't receive a proper response");
            }

        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        catch(JSONException e)
        {
            e.printStackTrace();
        }
    }

    private void sendJSON(Socket socket, JSONObject json) throws IOException
    {
        final String string = json.toString();
        LogHelper.i(TAG, "Sending: " + string);
        sendBytes(socket, string.getBytes(Charset.defaultCharset()));
    }

    /**
     * @param socket socket to send the bytes on
     * @param bytes  bytes to send
     *
     * @throws IOException
     */
    private void sendBytes(Socket socket, byte[] bytes) throws IOException
    {
        sendBytes(socket, bytes, 0, bytes.length);
    }


    /**
     * @param socket      socket to send the bytes to
     * @param myByteArray bytes to send
     * @param start       offset for the bytes
     * @param len         length of the message
     *
     * @throws IOException
     * @throws IllegalArgumentException  when length is negative
     * @throws IndexOutOfBoundsException when start is negative or exceeds the array
     */
    private void sendBytes(Socket socket, byte[] myByteArray, int start, int len) throws IOException
    {
        if(len < 0)
            throw new IllegalArgumentException("Negative length not allowed");
        if(start < 0 || start >= myByteArray.length)
            throw new IndexOutOfBoundsException("Out of bounds: " + start);
        // Other checks if needed.

        // May be better to save the streams in the support class;
        // just like the socket variable.
        OutputStream out = socket.getOutputStream();
        DataOutputStream dos = new DataOutputStream(out);

        //dos.writeInt(len);
        if(len > 0)
        {
            dos.write(myByteArray);
        }
        dos.flush();
    }

    private JSONObject receiveJSON(Socket socket) throws IOException, JSONException
    {
        final String s = new String(receiveBytes(socket)).trim();
        LogHelper.i(TAG, "Received: " + s);
        return new JSONObject(s);
    }

    private byte[] receiveBytes(Socket socket) throws IOException
    {
        InputStream inputStream = socket.getInputStream();
        DataInputStream dataInputStream = new DataInputStream(inputStream);
        final byte[] b = new byte[1024];
        dataInputStream.read(b);
        return b;
    }

    private byte[] receiveBytes(Socket socket, int bufferSize) throws IOException
    {
        InputStream inputStream = socket.getInputStream();
        DataInputStream dataInputStream = new DataInputStream(inputStream);
        final byte[] b = new byte[bufferSize];
        dataInputStream.read(b);
        return b;
    }

    /**
     * @param array array of bytes to be parsed
     *
     * @return parsed {@link JSONObject}
     * @throws JSONException
     */
    private JSONObject jsonFromByteArr(byte[] array) throws JSONException
    {
        return new JSONObject(new String(array, Charset.defaultCharset()).trim());
    }
}
