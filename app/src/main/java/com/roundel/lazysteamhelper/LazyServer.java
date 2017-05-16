package com.roundel.lazysteamhelper;

/**
 * Created by Krzysiek on 16/05/2017.
 */

public class LazyServer
{
    private int port;
    private String host;
    private String name;

    public LazyServer(String host, String name, int port)
    {
        this.port = port;
        this.host = host;
        this.name = name;
    }

    @Override
    public String toString()
    {
        return "LazyServer{" +
                "port=" + port +
                ", host='" + host + '\'' +
                ", name='" + name + '\'' +
                '}';
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
}
