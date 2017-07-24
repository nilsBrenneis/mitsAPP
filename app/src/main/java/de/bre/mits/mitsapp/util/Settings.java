package de.bre.mits.mitsapp.util;

public class Settings {
    //REST
        //URL for data provider with rest service
    public static final String PROVIDER_URL = "https://10.0.2.2"; //"https://thinkpad-x260.fritz.box";
                                            //"https://10.0.2.2"; for use in VM without outside network connection
                                            // IP is "loopback" to host machine

        //rest resource which confirms the user entered valid credentials at login to use for other resources
    public static final String AUTH_URI = "/confirmAuth";

        //rest resource used to populate list view from class "IngredientsListActivity"
    public static final String RESOURCE_STOCK_MODIFICATION_AND_INFO = "/bestaende/";

    //rest resource used to populate list view from class "IngredientsListActivity"
    public static final String RESOURCE_CRIT_STOCK_INFO = "/kritBestaende/";

        //rest resource used to put new ingredient into data provider repository
    public static final String RESOURCE_ADD_NEW_INGREDIENT = "/neuZutat";

        //rest parameter defining request parameter for manipulating stock value at data provider. has to be frist parameter
    public static final String PARAM_MANIPULATE_STOCK = "?menge=";

        //rest parameter defining request parameter for manipulating critical stock value at data provider
    public static final String PARAM_MANIPULATE_CRIT_STOCK = "&mindestbestand=";

    //DESIGN
    public static final int ACTION_BAR_FONT_COLOR = 0xFFFFFFFF;

    //MISCELLANEOUS
        //interval in seconds in which the user can tap back on device twice to leave
    public static final int DELAY_FOR_EXIT = 2000;
}
