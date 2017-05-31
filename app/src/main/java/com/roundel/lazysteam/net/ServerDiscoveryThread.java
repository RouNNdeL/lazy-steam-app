package com.roundel.lazysteam.net;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.roundel.lazysteam.LazyServer;
import com.roundel.lazysteam.util.LogHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.Objects;


import static com.roundel.lazysteam.Constants.DISCOVERY_REQUEST;
import static com.roundel.lazysteam.Constants.DISCOVERY_RESPONSE;
import static com.roundel.lazysteam.Constants.JSON_COM;
import static com.roundel.lazysteam.Constants.JSON_HOSTNAME;
import static com.roundel.lazysteam.Constants.JSON_TCP_PORT;

/**
 * Created by Krzysiek on 16/05/2017.
 */

/**
 * This thread is used to send a UDP broadcast and detect servers running on the local network, to
 * receive status updates such as {@link ServerDiscoveryListener#onServerFound(LazyServer)
 * onServerFound} implement the {@link ServerDiscoveryListener ServerDiscoveryListener} and set it
 * using {@link #setServerDiscoveryListener(ServerDiscoveryListener) setServerDiscoveryListener}
 * before starting the thread.
 */
public class ServerDiscoveryThread extends Thread
{
    private static final String TAG = ServerDiscoveryThread.class.getSimpleName();

    private DatagramSocket socket;
    private ServerDiscoveryListener listener;

    private int discoveryTimeout = 500;

    public ServerDiscoveryThread()
    {
    }

    @Override
    public void run()
    {
        // Find the server using UDP broadcast
        try
        {
            //Open a random port to send the package
            socket = new DatagramSocket();
            socket.setBroadcast(true);

            listener.onSocketOpened();

            JSONObject json = new JSONObject();
            json.put(JSON_COM, DISCOVERY_REQUEST);
            byte[] sendData = json.toString().getBytes(Charset.defaultCharset());

            //Try the 255.255.255.255 first
            try
            {
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName("255.255.255.255"), 8888);
                socket.send(sendPacket);
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }

            // Broadcast the message over all the network interfaces
            Enumeration interfaces = NetworkInterface.getNetworkInterfaces();
            while(interfaces.hasMoreElements())
            {
                NetworkInterface networkInterface = (NetworkInterface) interfaces.nextElement();

                if(networkInterface.isLoopback() || !networkInterface.isUp())
                {
                    continue; // Don't want to broadcast to the loopback interface
                }

                for(InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses())
                {
                    InetAddress broadcast = interfaceAddress.getBroadcast();
                    if(broadcast == null)
                    {
                        continue;
                    }

                    // Send the broadcast package!
                    try
                    {
                        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, broadcast, 8888);
                        socket.send(sendPacket);
                    }
                    catch(Exception e)
                    {
                    }
                }
            }

            //Close the socket after discoveryTimeout millis
            new Handler(Looper.getMainLooper()).postDelayed(() -> socket.close(), discoveryTimeout);

            //Wait for a response
            while(true)
            {
                byte[] recvBuf = new byte[15000];
                DatagramPacket receivePacket = new DatagramPacket(recvBuf, recvBuf.length);
                socket.receive(receivePacket);

                //We have a response
                LogHelper.i(TAG, "Broadcast response from " + receivePacket.getAddress().getHostName() + ": " +
                        receivePacket.getAddress().getHostAddress() + ":" +
                        receivePacket.getPort());

                //Check if the message is correct
                String message = new String(receivePacket.getData(), Charset.defaultCharset()).trim();

                LogHelper.i(TAG, "Message: " + message);
                try
                {
                    JSONObject response = new JSONObject(message);
                    if(Objects.equals(response.getString(JSON_COM), DISCOVERY_RESPONSE))
                    {
                        String hostName = response.getString(JSON_HOSTNAME);
                        final String hostAddress = receivePacket.getAddress().getHostAddress();
                        final int communicationPort = response.getInt(JSON_TCP_PORT);

                        Log.i(TAG, "New server \"" + hostName + "\"at:" + hostAddress + ":" + communicationPort);

                        listener.onServerFound(
                                new LazyServer(
                                        hostAddress,
                                        hostName,
                                        communicationPort
                                ));
                    }
                }
                catch(JSONException e)
                {
                    LogHelper.e(TAG, e.toString());
                    e.printStackTrace();
                }
            }
        }
        catch(SocketException e)
        {
            if(socket.isClosed())
                listener.onSocketClosed();
        }
        catch(IOException e)
        {
            if(socket.isClosed())
                listener.onSocketClosed();
            e.printStackTrace();
            LogHelper.e(TAG, e.toString());
        }
        catch(JSONException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * @param listener a {@link ServerDiscoveryListener} that notifies about the process
     */
    public void setServerDiscoveryListener(ServerDiscoveryListener listener)
    {
        this.listener = listener;
    }

    public void setDiscoveryTimeout(int discoveryTimeout)
    {
        this.discoveryTimeout = discoveryTimeout;
    }

    public interface ServerDiscoveryListener
    {
        void onServerFound(LazyServer server);

        void onSocketClosed();

        void onSocketOpened();
    }
}