package com.ryg;


import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class GreenDaoGenerator {


    private static final String OUTPUT_PATH = ".\\app\\src\\main\\java-gen";

    public static void main(String args[]) throws Exception {

        Schema schema = new Schema(3, "greendao");
        initWifiLog(schema);

        new DaoGenerator().generateAll(schema, OUTPUT_PATH);

    }


    private static void initWifiLog(Schema schema){
        Entity wifiLog = schema.addEntity("WifiLog");
        wifiLog.addIdProperty().autoincrement();
        wifiLog.addStringProperty("name");
        wifiLog.addLongProperty("time").notNull().unique();
        wifiLog.addStringProperty("BSSID");
    }


}
