package io.a_ware.a_ware;

import com.orm.SugarRecord;

import java.util.Date;

public class Applog extends SugarRecord <Applog> {

    String phoneid;
    String packagen;
    String permission;
    Date timestamp;
    String gps;
    String synced;


    public Applog() {
    }

    public Applog(String phoneid, String packagen, String permission, Date timestamp, String gps, String synced) {
        this.phoneid = phoneid;
        this.packagen = packagen;
        this.permission = permission;
        this.timestamp = timestamp;
        this.gps = gps;
        this.synced = synced;
    }

}


