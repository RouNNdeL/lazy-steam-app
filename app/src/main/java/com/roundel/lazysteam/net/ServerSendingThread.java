package com.roundel.lazysteam.net;

import com.roundel.lazysteam.LazyServer;
import com.roundel.lazysteam.util.SocketUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.Socket;
import java.util.Objects;

/**
 * Created by Krzysiek on 16/05/2017.
 */

/**
 * This {@link Thread} is used to send a code and a username to a {@link LazyServer LazyServer}
 * specified in the {@link #ServerSendingThread(LazyServer, String, String) constructor}
 *
 * @deprecated this {@link Thread} does not encrypt the connection, thus is not suitable for WAN
 * connections, use {@link ServerDataThread} instead
 */
@SuppressWarnings("ResultOfMethodCallIgnored")
@Deprecated
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
            lazySocket.connect(mTarget.getInetSocketAddress(), mConnectionTimeout);
            lazySocket.setSoTimeout(mReceiveTimeout);

            JSONObject json = new JSONObject();
            json.put(JSON_COM, CONNECTION_REQUEST);

            SocketUtils.sendJSON(lazySocket, json);
            JSONObject response = SocketUtils.receiveJSON(lazySocket);

            if(!Objects.equals(CONNECTION_RESPONSE, response.getString(JSON_COM)))
            {
                throw new IllegalStateException("Didn't receive a proper response");
            }

            json = new JSONObject();
            json.put(JSON_COM, DATA);
            json.put(JSON_CODE, mCode);
            json.put(JSON_USERNAME, mUsername);

            SocketUtils.sendJSON(lazySocket, json);
            response = SocketUtils.receiveJSON(lazySocket);

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


}
