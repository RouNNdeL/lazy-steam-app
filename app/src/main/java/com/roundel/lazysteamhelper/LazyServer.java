package com.roundel.lazysteamhelper;

import java.net.InetSocketAddress;

/**
 * Created by Krzysiek on 16/05/2017.
 */

public class LazyServer
{
    private String host;
    private String name;
    private int port;
    private byte[] key;
    private boolean local;

    //External server
    public LazyServer(String host, String name, int port, byte[] key)
    {
        this(host, name, port);
        this.key = key;
        this.local = false;
    }

    /**
     * Local server, used for setup
     *
     * @param host hostname or ip address used to connect
     * @param name name visible to the user
     * @param port port used for connection
     */
    public LazyServer(String host, String name, int port)
    {
        this.port = port;
        this.host = host;
        this.name = name;
        this.local = true;
        this.key = new byte[0];
    }

    public int getPort()
    {
        return port;
    }

    public String getHost()
    {
        return host;
    }

    public String getName()
    {
        return name;
    }

    public byte[] getKey()
    {
        return key;
    }

    public void setKey(byte[] key)
    {
        this.key = key;
        this.local = key == new byte[0];
    }

    public boolean isLocal()
    {
        return local;
    }

    public InetSocketAddress getInetSocketAddress()
    {
        return new InetSocketAddress(getHost(), getPort());
    }
}
