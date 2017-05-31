package com.roundel.lazysteam.net;

import com.roundel.lazysteam.LazyServer;
import com.roundel.lazysteam.util.SocketUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.Socket;
import java.util.Objects;


import static com.roundel.lazysteam.Constants.CONNECTION_TIMEOUT;
import static com.roundel.lazysteam.Constants.JSON_APP_ID;
import static com.roundel.lazysteam.Constants.JSON_COM;
import static com.roundel.lazysteam.Constants.JSON_ENCRYPTION_KEY;
import static com.roundel.lazysteam.Constants.JSON_EXT_HOST;
import static com.roundel.lazysteam.Constants.JSON_EXT_PORT;
import static com.roundel.lazysteam.Constants.JSON_SECURITY_CODE;
import static com.roundel.lazysteam.Constants.JSON_SERVER_ID;
import static com.roundel.lazysteam.Constants.RECEIVE_TIMEOUT;
import static com.roundel.lazysteam.Constants.SETUP_BEGIN;
import static com.roundel.lazysteam.Constants.SETUP_CODE_REQUEST;
import static com.roundel.lazysteam.Constants.SETUP_CODE_RESPONSE;
import static com.roundel.lazysteam.Constants.SETUP_COMPLETE;
import static com.roundel.lazysteam.Constants.SETUP_COMPLETE_CONFIRM;
import static com.roundel.lazysteam.Constants.SETUP_KEY_EXCHANGE;
import static com.roundel.lazysteam.Constants.SETUP_PORT_REQUEST;
import static com.roundel.lazysteam.Constants.SETUP_PORT_RESPONSE;

/*
 * Created by Krzysiek on 24/05/2017.
 */

/**
 * This {@link Thread} is used to exchange important information (ids, external port and ip, etc.)
 * with a local {@link LazyServer}. The connection is not encrypted so it should not be used outside
 * a local network. The thread will throw an {@link IllegalArgumentException} if the provided server
 * is not local. You must implement the {@link SetupProgress SetupProgress} interface to receive
 * callbacks related to the process
 */
public class ServerSetupThread extends Thread
{

    private LazyServer target;
    private SetupProgress listener;

    public ServerSetupThread(LazyServer target, SetupProgress listener)
    {
        this.target = target;
        this.listener = listener;
    }

    public static byte[] hexStringToByteArray(String s)
    {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for(int i = 0; i < len; i += 2)
        {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    @Override
    public void run()
    {
        if(SocketUtils.isLocalAddress(target.getInetAddress()))
            throw new IllegalArgumentException("Provided LazyServer must be a server on a local network");

        listener.onStart(target);

        try
        {
            Socket socket = new Socket();
            socket.connect(target.getInetSocketAddress(), CONNECTION_TIMEOUT);
            socket.setSoTimeout(RECEIVE_TIMEOUT);

            listener.onConnect(target);

            JSONObject json = new JSONObject();

            json.put(JSON_COM, SETUP_BEGIN);
            SocketUtils.sendJSON(socket, json);

            json = SocketUtils.receiveJSON(socket);
            do
            {
                if(!Objects.equals(SETUP_CODE_REQUEST, json.getString(JSON_COM)))
                {
                    throw new IllegalStateException("The server didn't send a proper response: " + json.toString());
                }
                int code = listener.onCodeRequest(target);

                json = new JSONObject();
                json.put(JSON_COM, SETUP_CODE_RESPONSE);
                json.put(JSON_SECURITY_CODE, code);
                SocketUtils.sendJSON(socket, json);
                json = SocketUtils.receiveJSON(socket);
            }
            while(Objects.equals(SETUP_CODE_REQUEST, json.getString(JSON_COM)));

            if(!Objects.equals(SETUP_KEY_EXCHANGE, json.getString(JSON_COM)))
            {
                throw new IllegalStateException("The server didn't send a proper response: " + json.toString());
            }
            byte[] key = hexStringToByteArray(json.getString(JSON_ENCRYPTION_KEY));
            int id = json.getInt(JSON_SERVER_ID);

            json = new JSONObject();
            json.put(JSON_COM, SETUP_PORT_REQUEST);
            json.put(JSON_APP_ID, 57);
            SocketUtils.sendJSON(socket, json);

            json = SocketUtils.receiveJSON(socket);
            if(!Objects.equals(SETUP_PORT_RESPONSE, json.getString(JSON_COM)))
            {
                throw new IllegalStateException("The server didn't send a proper response: " + json.toString());
            }
            String host = json.getString(JSON_EXT_HOST);
            int port = json.getInt(JSON_EXT_PORT);

            json = new JSONObject();
            json.put(JSON_COM, SETUP_COMPLETE);
            SocketUtils.sendJSON(socket, json);

            json = SocketUtils.receiveJSON(socket);
            if(!Objects.equals(SETUP_COMPLETE_CONFIRM, json.getString(JSON_COM)))
            {
                throw new IllegalStateException("The server didn't send a proper response: " + json.toString());
            }
            listener.onSuccess(new LazyServer(host, target.getName(), port, key, id));

            socket.close();
        }
        catch(IOException | IllegalStateException e)
        {
            listener.onFailure(e);
            e.printStackTrace();
        }
        catch(JSONException e)
        {
            listener.onFailure(e);
            e.printStackTrace();
        }
    }

    public interface SetupProgress
    {
        void onStart(LazyServer server);

        /**
         * @param server a server that the connection has been established with
         */
        void onConnect(LazyServer server);

        /**
         * @param server a server that requested the auth code
         *
         * @return a code that the user submitted
         */
        int onCodeRequest(LazyServer server);

        /**
         * @param external a new {@link LazyServer} instance that is an external representation of
         *                 the local server provided in the constructor
         */
        void onSuccess(LazyServer external);

        void onFailure(Exception e);
    }
}
