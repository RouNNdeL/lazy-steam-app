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

    @Override
    public boolean equals(Object o)
    {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;

        LazyServer that = (LazyServer) o;

        if(port != that.port) return false;
        if(host != null ? !host.equals(that.host) : that.host != null) return false;
        return name.equals(that.name);

    }

    @Override
    public int hashCode()
    {
        int result = port;
        result = 31 * result + (host != null ? host.hashCode() : 0);
        result = 31 * result + name.hashCode();
        return result;
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
