package com.dih.keye.keyegank.model;

/**
 * Created by zsj on 2015/11/16 0016.
 */
public class PrettyGirl {

    public String _id;
    public String _ns;
    public String createdAt;
    public String desc;
    public String publishedAt;
    public String source;
    public String type;
    public String url;
    public boolean used;
    public String who;

    @Override
    public String toString() {
        return "PrettyGirl{" +
                "_id='" + _id + '\'' +
                ", _ns='" + _ns + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", desc='" + desc + '\'' +
                ", publishedAt='" + publishedAt + '\'' +
                ", source='" + source + '\'' +
                ", type='" + type + '\'' +
                ", url='" + url + '\'' +
                ", used=" + used +
                ", who='" + who + '\'' +
                '}';
    }
}