package com.roundel.lazysteam.util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.Charset;

/**
 * Created by Krzysiek on 30/05/2017.
 */

public class SocketUtils
{
    private static String TAG = SocketUtils.class.getSimpleName();

    public static void sendJSON(Socket socket, JSONObject json) throws IOException
    {
        final String string = json.toString();
        LogHelper.i(TAG, "Sending: " + string);
        sendBytes(socket, string.getBytes(Charset.defaultCharset()));
    }

    /**
     * @param socket socket to send the bytes on
     * @param bytes  bytes to send
     *
     * @throws IOException if an I/O error occurs when creating the output stream or if the socket
     *                     is not connected.
     */
    public static void sendBytes(Socket socket, byte[] bytes) throws IOException
    {
        sendBytes(socket, bytes, 0, bytes.length);
    }


    /**
     * @param socket      socket to send the bytes to
     * @param myByteArray bytes to send
     * @param start       offset for the bytes
     * @param len         length of the message
     *
     * @throws IOException               if an I/O error occurs when creating the output stream or
     *                                   if the socket is not connected.
     * @throws IllegalArgumentException  when length is negative
     * @throws IndexOutOfBoundsException when start is negative or exceeds the array
     */
    public static void sendBytes(Socket socket, byte[] myByteArray, int start, int len) throws IOException
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

    public static JSONObject receiveJSON(Socket socket) throws IOException, JSONException
    {
        final String s = new String(receiveBytes(socket)).trim();
        LogHelper.i(TAG, "Received: " + s);
        return new JSONObject(s);
    }

    public static byte[] receiveBytes(Socket socket) throws IOException
    {
        return receiveBytes(socket, 1024);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static byte[] receiveBytes(Socket socket, int bufferSize) throws IOException
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
     * @throws JSONException if the parse fails or doesn't yield a JSONObject.
     */
    public static JSONObject jsonFromByteArr(byte[] array) throws JSONException
    {
        return new JSONObject(new String(array, Charset.defaultCharset()).trim());
    }

    public static boolean isLocalAddress(InetAddress addr)
    {
        if(addr == null)
            return false;
        // Check if the address is a valid special local or loop back
        if(addr.isAnyLocalAddress() || addr.isLoopbackAddress())
            return true;

        // Check if the address is defined on any interface
        try
        {
            return NetworkInterface.getByInetAddress(addr) != null;
        }
        catch(SocketException e)
        {
            return false;
        }
    }
}
