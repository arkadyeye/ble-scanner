package example.com.bus2.service;


import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * this class intends to hold the data for each tag, and it's properties.
 * actually, it's just an array wrapper
 */
public class TagsContainer {

    //data
    private ArrayList<BleTag> data = new ArrayList<>();

    //constructor
    public TagsContainer (JSONObject json){
        try {
            JSONArray bles = json.getJSONArray("blemacs");
            for (int i=0;i<bles.length();i++){
                BleTag tmp = new BleTag();
                tmp.lat = bles.getJSONObject(i).getDouble("lat");
                tmp.lon = bles.getJSONObject(i).getDouble("lon");
                tmp.alt = bles.getJSONObject(i).getDouble("alt");

                tmp.type = bles.getJSONObject(i).getInt("type");
                tmp.alert_on_db = bles.getJSONObject(i).getInt("alert_on_db");
                tmp.alert_off_db = bles.getJSONObject(i).getInt("alert_off_db");

                tmp.mac = bles.getJSONObject(i).getString("mac");
                tmp.name = bles.getJSONObject(i).getString("name");

                data.add(tmp);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //functions

    public BleTag getBleTag(String mac){
        for (int i=0; i< data.size();i++){
            if (data.get(i).mac.equals(mac)) return data.get(i);
        }
        return null;
    }

    public int getBleTagIndex(String mac){
        for (int i=0; i< data.size();i++){
            if (data.get(i).mac.equals(mac)) return i;
        }
        return -1;
    }

    public int size(){
        return data.size();
    }

    public BleTag get(int index){
        return data.get(index);
    }
}

