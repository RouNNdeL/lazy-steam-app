package com.roundel.lazysteam.net;

/**
 * Created by Krzysiek on 24/05/2017.
 */

public class ServerSetupThread extends Thread
{
    private static final String COM_PREFIX = "LAZY_STEAM_SETUP_";

    private static final String SETUP_BEGIN = COM_PREFIX + "BEGIN";
    private static final String CODE_REQUEST = COM_PREFIX + "CODE_REQUEST";
    private static final String CODE_RESPONSE = COM_PREFIX + "CODE_RESPONSE";
    private static final String KEY_EXCHANGE = COM_PREFIX + "KEY_EXCHANGE";
    private static final String PORT_REQUEST = COM_PREFIX + "PORT_REQUEST";
    private static final String PORT_RESPONSE = COM_PREFIX + "PORT_RESPONSE";
    private static final String COMPLETE = COM_PREFIX + "COMPLETE";
    private static final String COMPLETE_CONFIRM = COM_PREFIX + "COMPLETE_CONFIRM";

    private static final String JSON_COM = "com";                               //string (one of the above)
    private static final String JSON_TCP_PORT = "communication_port";           //int
    private static final String JSON_HOSTNAME = "server_hostname";              //string
    private static final String JSON_SECURITY_CODE = "security_code";           //int (4 digits)
    private static final String JSON_ENCRYPTION_KEY = "encryption_key";         //string (32bytes as hex)
    private static final String JSON_EXT_PORT = "external_port";                //int
    private static final String JSON_EXT_HOST = "external_host";                //string

    @Override
    public void run()
    {

    }
}
