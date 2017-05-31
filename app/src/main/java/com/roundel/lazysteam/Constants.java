package com.roundel.lazysteam;

/**
 * Created by Krzysiek on 30/05/2017.
 */

public class Constants
{
    public static final int CONNECTION_TIMEOUT = 5000;
    public static final int RECEIVE_TIMEOUT = 200000;
    //JSON variables and their types
    public static final String JSON_COM = "com";                               //string (one of the above)

    //<editor-fold desc="Setup">
    public static final String JSON_TCP_PORT = "communication_port";           //int
    public static final String JSON_HOSTNAME = "server_hostname";              //string
    public static final String JSON_SECURITY_CODE = "security_code";           //int (4 digits)
    public static final String JSON_TIRES = "tries";                           //int
    public static final String JSON_ENCRYPTION_KEY = "encryption_key";         //string (32bytes as hex)
    public static final String JSON_SERVER_ID = "server_id";                   //int
    public static final String JSON_APP_ID = "app_id";                         //int
    public static final String JSON_EXT_PORT = "external_port";                //int
    public static final String JSON_EXT_HOST = "external_host";                //string
    private static final String COM_PREFIX = "LAZY_STEAM_";
    //UDP coms
    public static final String DISCOVERY_REQUEST = COM_PREFIX + "DISCOVERY_REQUEST";
    public static final String DISCOVERY_RESPONSE = COM_PREFIX + "DISCOVERY_RESPONSE";
    //TCP coms
    private static final String SETUP_PREFIX = COM_PREFIX + "SETUP_";
    public static final String SETUP_BEGIN = SETUP_PREFIX + "BEGIN";
    public static final String SETUP_CODE_REQUEST = SETUP_PREFIX + "CODE_REQUEST";
    public static final String SETUP_CODE_RESPONSE = SETUP_PREFIX + "CODE_RESPONSE";
    public static final String SETUP_KEY_EXCHANGE = SETUP_PREFIX + "KEY_EXCHANGE";
    public static final String SETUP_PORT_REQUEST = SETUP_PREFIX + "PORT_REQUEST";
    public static final String SETUP_PORT_RESPONSE = SETUP_PREFIX + "PORT_RESPONSE";
    public static final String SETUP_COMPLETE = SETUP_PREFIX + "COMPLETE";
    public static final String SETUP_COMPLETE_CONFIRM = SETUP_PREFIX + "COMPLETE_CONFIRM";

    //</editor-fold>
}
