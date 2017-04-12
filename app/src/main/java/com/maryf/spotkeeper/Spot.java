package com.maryf.spotkeeper;

import java.io.Serializable;

/**
 * Created by maryf on 4/5/2017.
 */

public class Spot implements Serializable {
    private String mName;
    private String mAddress;

    Spot(String name, String address) {
        mName = name;
        mAddress = address;
    }

    public String getName() {
        return mName;
    }

    public String getAddress() {
        return mAddress;
    }
}
