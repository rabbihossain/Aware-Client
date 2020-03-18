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
    String dateval;
    String timeval;


    public Applog() {
    }

    public Applog(String phoneid, String packagen, String permission, Date timestamp, String gps, String synced) {
        this.phoneid = phoneid;
        this.packagen = packagen;
        this.permission = permission;
        this.timestamp = timestamp;
        this.gps = gps;
        this.synced = synced;
        this.dateval = timestamp.getDate()+"/"+(timestamp.getMonth() + 1)+"/"+(1900 + timestamp.getYear());
        this.timeval = timestamp.getHours()+":"+timestamp.getMinutes();
    }

}


