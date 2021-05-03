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
    private String default_data = "{\n" +
            "  \"blemacs\": [\n" +
            "\n" +
            "      {\n" +
            "          \"mac\":\"C0:F5:42:F2:F0:68\",\n" +
            "          \"lat\": 32.067821,\n" +
            "          \"lon\": 34.843545,\n" +
            "          \"alt\": 60,\n" +
            "          \"type\": 8,\n" +
            "          \"name\":\"Debug Only: Arkady home\",\n" +
            "          \"alert_on_db\": -90,\n" +
            "          \"alert_off_db\": -90\n" +
            "      },\n" +
            "      {\n" +
            "          \"mac\":\"C9:8D:E2:A8:1E:28\",\n" +
            "          \"lat\": 32.07091,\n" +
            "          \"lon\": 34.84429,\n" +
            "          \"alt\": 0,\n" +
            "          \"type\": 8,\n" +
            "          \"name\":\"507,0,SW\",\n" +
            "          \"alert_on_db\": -90,\n" +
            "          \"alert_off_db\": -90\n" +
            "      },\n" +
            "      {\n" +
            "          \"mac\":\"EA:8A:A9:D2:6B:DD\",\n" +
            "          \"lat\": 32.07029,\n" +
            "          \"lon\": 34.84385,\n" +
            "          \"alt\": -1,\n" +
            "          \"type\": 8,\n" +
            "          \"name\":\"604,-1,center\",\n" +
            "          \"alert_on_db\": -90,\n" +
            "          \"alert_off_db\": -90\n" +
            "      },\n" +
            "      {\n" +
            "          \"mac\":\"DA:10:D3:47:5B:38\",\n" +
            "          \"lat\": 32.07029,\n" +
            "          \"lon\": 34.84385,\n" +
            "          \"alt\": 0,\n" +
            "          \"type\": 8,\n" +
            "          \"name\":\"604,0,center\",\n" +
            "          \"alert_on_db\": -90,\n" +
            "          \"alert_off_db\": -90\n" +
            "      },\n" +
            "      {\n" +
            "          \"mac\":\"C7:06:A8:52:9C:3E\",\n" +
            "          \"lat\": 32.0704,\n" +
            "          \"lon\": 34.84356,\n" +
            "          \"alt\": 2,\n" +
            "          \"type\": 1,\n" +
            "          \"name\":\"605,2,N,Stairs\",\n" +
            "          \"alert_on_db\": -60,\n" +
            "          \"alert_off_db\": -80\n" +
            "      },\n" +
            "      {\n" +
            "          \"mac\":\"C9:EF:61:35:4A:E3\",\n" +
            "          \"lat\": 32.07026,\n" +
            "          \"lon\": 34.84346,\n" +
            "          \"alt\": 2,\n" +
            "          \"type\": 1,\n" +
            "          \"name\":\"605,2,S\",\n" +
            "          \"alert_on_db\": -60,\n" +
            "          \"alert_off_db\": -80\n" +
            "      },\n" +
            "      {\n" +
            "          \"mac\": \"D8:F4:59:A3:6F:5C\",\n" +
            "          \"lat\": 32.07033,\n" +
            "          \"lon\": 34.84443,\n" +
            "          \"alt\": 0,\n" +
            "          \"type\": 1,\n" +
            "          \"name\":\"505,0,SE,Entrance\",\n" +
            "          \"alert_on_db\": -60,\n" +
            "          \"alert_off_db\": -80\n" +
            "      },\n" +
            "      {\n" +
            "          \"mac\":\"EC:F1:39:B1:8E:0D\",\n" +
            "          \"lat\": 32.07068,\n" +
            "          \"lon\": 34.84388,\n" +
            "          \"alt\": 0,\n" +
            "          \"type\": 1,\n" +
            "          \"name\":\"Yard, north to 604, APM\",\n" +
            "          \"alert_on_db\": -60,\n" +
            "          \"alert_off_db\": -80\n" +
            "      },\n" +
            "      {\n" +
            "          \"mac\":\"F4:91:C5:BC:59:AA\",\n" +
            "          \"lat\": 32.07091,\n" +
            "          \"lon\": 34.84429,\n" +
            "          \"alt\": 0,\n" +
            "          \"type\": 1,\n" +
            "          \"name\":\"507,1,SW,Entrance\",\n" +
            "          \"alert_on_db\": -60,\n" +
            "          \"alert_off_db\": -80\n" +
            "      },\n" +
            "      {\n" +
            "          \"mac\": \"D6:66:3C:67:0C:A2\",\n" +
            "          \"lat\": 32.07081,\n" +
            "          \"lon\": 34.84439,\n" +
            "          \"alt\": 0,\n" +
            "          \"type\": 1,\n" +
            "          \"name\":\"507,S,Entrance\",\n" +
            "          \"alert_on_db\": -60,\n" +
            "          \"alert_off_db\": -80\n" +
            "      },\n" +
            "      {\n" +
            "          \"mac\":\"D8:2D:EE:25:23:60\",\n" +
            "          \"lat\": 32.07058,\n" +
            "          \"lon\": 34.84458,\n" +
            "          \"alt\": 0,\n" +
            "          \"type\": 1,\n" +
            "          \"name\":\"505,0,N,Entrance\",\n" +
            "          \"alert_on_db\": -60,\n" +
            "          \"alert_off_db\": -80\n" +
            "      },\n" +
            "      {\n" +
            "          \"mac\":\"F7:D2:42:AA:27:09\",\n" +
            "          \"lat\": 32.06995,\n" +
            "          \"lon\": 34.84399,\n" +
            "          \"alt\": 0,\n" +
            "          \"type\": 1,\n" +
            "          \"name\":\"Yard,604<->504, on the sign.\",\n" +
            "          \"alert_on_db\": -60,\n" +
            "          \"alert_off_db\": -80\n" +
            "      },\n" +
            "      {\n" +
            "          \"mac\":\"D7:4F:44:2D:CF:1B\",\n" +
            "          \"lat\": 32.06954,\n" +
            "          \"lon\": 34.84437,\n" +
            "          \"alt\": 0,\n" +
            "          \"type\": 1,\n" +
            "          \"name\":\"504,0,S,Near the APM\",\n" +
            "          \"alert_on_db\": -60,\n" +
            "          \"alert_off_db\": -80\n" +
            "      },\n" +
            "      {\n" +
            "          \"mac\":\"D8:47:AD:D5:C6:65\",\n" +
            "          \"lat\": 32.06952,\n" +
            "          \"lon\": 34.84447,\n" +
            "          \"alt\": 0,\n" +
            "          \"type\": 1,\n" +
            "          \"name\":\"504,0,S,Entrance\",\n" +
            "          \"alert_on_db\": -60,\n" +
            "          \"alert_off_db\": -80\n" +
            "      },\n" +
            "      {\n" +
            "          \"mac\":\"C8:BA:50:4E:36:D0\",\n" +
            "          \"lat\": 32.07004,\n" +
            "          \"lon\": 34.84426,\n" +
            "          \"alt\": 0,\n" +
            "          \"type\": 1,\n" +
            "          \"name\":\"504,0,N,Entrance\",\n" +
            "          \"alert_on_db\": -60,\n" +
            "          \"alert_off_db\": -80\n" +
            "      },\n" +
            "      {\n" +
            "          \"mac\":\"FA:90:15:D2:57:7D\",\n" +
            "          \"lat\": 32.07029,\n" +
            "          \"lon\": 34.84385,\n" +
            "          \"alt\": 1,\n" +
            "          \"type\": 1,\n" +
            "          \"name\":\"604,1,Center\",\n" +
            "          \"alert_on_db\": -60,\n" +
            "          \"alert_off_db\": -80\n" +
            "      },\n" +
            "      {\n" +
            "          \"mac\":\"F4:2D:8D:C2:88:1F\",\n" +
            "          \"lat\": 32.07029,\n" +
            "          \"lon\": 34.84385,\n" +
            "          \"alt\": 3,\n" +
            "          \"type\": 1,\n" +
            "          \"name\":\"604,3,Center\",\n" +
            "          \"alert_on_db\": -60,\n" +
            "          \"alert_off_db\": -80\n" +
            "      },\n" +
            "      {\n" +
            "          \"mac\":\"D1:D9:13:DA:1E:BA\",\n" +
            "          \"lat\": 32.07027,\n" +
            "          \"lon\": 34.8435,\n" +
            "          \"alt\": 2,\n" +
            "          \"type\": 1,\n" +
            "          \"name\":\"604,2,Center\",\n" +
            "          \"alert_on_db\": -60,\n" +
            "          \"alert_off_db\": -80\n" +
            "      },\n" +
            "      {\n" +
            "          \"mac\":\"E0:14:D0:4F:B2:81\",\n" +
            "          \"lat\": 32.07042,\n" +
            "          \"lon\": 34.84353,\n" +
            "          \"alt\": 0,\n" +
            "          \"type\": 2,\n" +
            "          \"name\":\"604,Elevator, S\",\n" +
            "          \"alert_on_db\": -60,\n" +
            "          \"alert_off_db\": -80\n" +
            "      },\n" +
            "      {\n" +
            "          \"mac\":\"E7:64:0A:E4:BE:32\",\n" +
            "          \"lat\": 32.07041,\n" +
            "          \"lon\": 34.84388,\n" +
            "          \"alt\": 0,\n" +
            "          \"type\": 2,\n" +
            "          \"name\":\"604,Elevator, N\",\n" +
            "          \"alert_on_db\": -60,\n" +
            "          \"alert_off_db\": -80\n" +
            "      },\n" +
            "      {\n" +
            "          \"mac\":\"CA:FC:B7:9C:42:C1\",\n" +
            "          \"lat\": 32.067910,\n" +
            "          \"lon\": 34.843414,\n" +
            "          \"alt\": 0,\n" +
            "          \"type\": 1,\n" +
            "          \"name\":\"604 כניסה צפון מערבית\",\n" +
            "          \"alert_on_db\": -60,\n" +
            "          \"alert_off_db\": -80\n" +
            "      },\n" +
            "      {\n" +
            "          \"mac\":\"E3:A4:53:26:18:3B\",\n" +
            "          \"lat\": 32.0704,\n" +
            "          \"lon\": 34.84371,\n" +
            "          \"alt\": 0,\n" +
            "          \"type\": 1,\n" +
            "          \"name\":\"604,0,NW,Entrance\",\n" +
            "          \"alert_on_db\": -60,\n" +
            "          \"alert_off_db\": -80\n" +
            "      },\n" +
            "      {\n" +
            "          \"mac\":\"D5:2C:78:A0:75:F4\",\n" +
            "          \"lat\": 32.07029,\n" +
            "          \"lon\": 34.8435,\n" +
            "          \"alt\": 0,\n" +
            "          \"type\": 1,\n" +
            "          \"name\":\"604,0,Center\",\n" +
            "          \"alert_on_db\": -60,\n" +
            "          \"alert_off_db\": -80\n" +
            "      },\n" +
            "      {\n" +
            "          \"mac\":\"FC:4B:DD:A9:45:91\",\n" +
            "          \"lat\": 32.0702,\n" +
            "          \"lon\": 34.84371,\n" +
            "          \"alt\": 0,\n" +
            "          \"type\": 1,\n" +
            "          \"name\":\"604,0,SW,Entrance\",\n" +
            "          \"alert_on_db\": -60,\n" +
            "          \"alert_off_db\": -80\n" +
            "      },\n" +
            "      {\n" +
            "          \"mac\":\"D7:A2:6A:0A:33:BC\",\n" +
            "          \"lat\": 32.07021,\n" +
            "          \"lon\": 34.84366,\n" +
            "          \"alt\": 0,\n" +
            "          \"type\": 0,\n" +
            "          \"name\":\"604<->605,S,on the light\",\n" +
            "          \"alert_on_db\": -60,\n" +
            "          \"alert_off_db\": -80\n" +
            "      },\n" +
            "      {\n" +
            "          \"mac\":\"CC:A6:0A:71:9F:12\",\n" +
            "          \"lat\": 32.07034,\n" +
            "          \"lon\": 34.84352,\n" +
            "          \"alt\": -1,\n" +
            "          \"type\": 1,\n" +
            "          \"name\":\"605,-1,Center\",\n" +
            "          \"alert_on_db\": -60,\n" +
            "          \"alert_off_db\": -80\n" +
            "      },\n" +
            "      {\n" +
            "          \"mac\":\"C1:99:3A:54:E1:54\",\n" +
            "          \"lat\": 32.0704,\n" +
            "          \"lon\": 34.84366,\n" +
            "          \"alt\": 0,\n" +
            "          \"type\": 1,\n" +
            "          \"name\":\"605,0,SE,Entrance\",\n" +
            "          \"alert_on_db\": -60,\n" +
            "          \"alert_off_db\": -80\n" +
            "      },\n" +
            "      {\n" +
            "          \"mac\":\"F6:3D:16:F9:24:28\",\n" +
            "          \"lat\": 32.07044,\n" +
            "          \"lon\": 34.84349,\n" +
            "          \"alt\": 0,\n" +
            "          \"type\": 1,\n" +
            "          \"name\":\"605,0,NW,Entrance\",\n" +
            "          \"alert_on_db\": -60,\n" +
            "          \"alert_off_db\": -80\n" +
            "      },\n" +
            "      {\n" +
            "          \"mac\":\"DE:7E:CD:74:C6:C0\",\n" +
            "          \"lat\": 32.07029,\n" +
            "          \"lon\": 34.84385,\n" +
            "          \"alt\": 0,\n" +
            "          \"type\": 1,\n" +
            "          \"name\":\"604,0,Center\",\n" +
            "          \"alert_on_db\": -60,\n" +
            "          \"alert_off_db\": -80\n" +
            "      },\n" +
            "      {\n" +
            "          \"mac\":\"F2:DD:08:17:6F:88\",\n" +
            "          \"lat\": 32.07024,\n" +
            "          \"lon\": 34.84352,\n" +
            "          \"alt\": 0,\n" +
            "          \"type\": 1,\n" +
            "          \"name\":\"605,0,SE,Entrance\",\n" +
            "          \"alert_on_db\": -60,\n" +
            "          \"alert_off_db\": -80\n" +
            "      },\n" +
            "      {\n" +
            "          \"mac\":\"FF:34:94:95:C8:45\",\n" +
            "          \"lat\": 32.07026,\n" +
            "          \"lon\": 34.84347,\n" +
            "          \"alt\": 1,\n" +
            "          \"type\": 1,\n" +
            "          \"name\":\"605,1,S\",\n" +
            "          \"alert_on_db\": -60,\n" +
            "          \"alert_off_db\": -80\n" +
            "      },\n" +
            "      {\n" +
            "          \"mac\":\"E6:42:4A:B6:42:9E\",\n" +
            "          \"lat\": 32.07033,\n" +
            "          \"lon\": 34.84352,\n" +
            "          \"alt\": 1,\n" +
            "          \"type\": 1,\n" +
            "          \"name\":\"605,1,Center\",\n" +
            "          \"alert_on_db\": -60,\n" +
            "          \"alert_off_db\": -80\n" +
            "      },\n" +
            "      {\n" +
            "          \"mac\":\"D5:69:71:4F:71:3E\",\n" +
            "          \"lat\": 32.07045,\n" +
            "          \"lon\": 34.84355,\n" +
            "          \"alt\": 1,\n" +
            "          \"type\": 1,\n" +
            "          \"name\":\"605,1,N,near the elevators\",\n" +
            "          \"alert_on_db\": -60,\n" +
            "          \"alert_off_db\": -80\n" +
            "      },\n" +
            "      {\n" +
            "          \"mac\":\"E5:84:F2:B3:42:92\",\n" +
            "          \"lat\": 32.07045,\n" +
            "          \"lon\": 34.84355,\n" +
            "          \"alt\": 3,\n" +
            "          \"type\": 1,\n" +
            "          \"name\":\"605,3,N,Near the Elevators\",\n" +
            "          \"alert_on_db\": -60,\n" +
            "          \"alert_off_db\": -80\n" +
            "      },\n" +
            "      {\n" +
            "          \"mac\":\"DA:95:E3:2C:83:76\",\n" +
            "          \"lat\": 32.07033,\n" +
            "          \"lon\": 34.84352,\n" +
            "          \"alt\": 3,\n" +
            "          \"type\": 1,\n" +
            "          \"name\":\"605,3,Center\",\n" +
            "          \"alert_on_db\": -60,\n" +
            "          \"alert_off_db\": -80\n" +
            "      },\n" +
            "      {\n" +
            "          \"mac\":\"C8:74:42:98:60:05\",\n" +
            "          \"lat\": 32.07025,\n" +
            "          \"lon\": 34.84344,\n" +
            "          \"alt\": 3,\n" +
            "          \"type\": 1,\n" +
            "          \"name\":\"605,3,S\",\n" +
            "          \"alert_on_db\": -60,\n" +
            "          \"alert_off_db\": -80\n" +
            "      },\n" +
            "      {\n" +
            "          \"mac\":\"CF:BD:A5:E6:FA:1D\",\n" +
            "          \"lat\": 32.07027,\n" +
            "          \"lon\": 34.8435,\n" +
            "          \"alt\": 2,\n" +
            "          \"type\": 1,\n" +
            "          \"name\":\"605,2,S,Stairs\",\n" +
            "          \"alert_on_db\": -60,\n" +
            "          \"alert_off_db\": -80\n" +
            "      },\n" +
            "      {\n" +
            "          \"mac\":\"DC:5C:F1:73:1C:65\",\n" +
            "          \"lat\": 32.07042,\n" +
            "          \"lon\": 34.84353,\n" +
            "          \"alt\": 0,\n" +
            "          \"type\": 2,\n" +
            "          \"name\":\"605,N,Elevator\",\n" +
            "          \"alert_on_db\": -60,\n" +
            "          \"alert_off_db\": -80\n" +
            "      },\n" +
            "      {\n" +
            "          \"mac\": \"F0:6B:12:38:FC:F5\",\n" +
            "          \"lat\": 32.07041,\n" +
            "          \"lon\": 34.84354,\n" +
            "          \"alt\": 0,\n" +
            "          \"type\": 2,\n" +
            "          \"name\":\"605,Elevator,S\",\n" +
            "          \"alert_on_db\": -60,\n" +
            "          \"alert_off_db\": -80\n" +
            "      },\n" +
            "      {\n" +
            "          \"mac\":\"E0:64:AC:27:CD:80\",\n" +
            "          \"lat\": 32.07045,\n" +
            "          \"lon\": 34.84355,\n" +
            "          \"alt\": 2,\n" +
            "          \"type\": 1,\n" +
            "          \"name\":\"605,2,N,Near the elevator\",\n" +
            "          \"alert_on_db\": -60,\n" +
            "          \"alert_off_db\": -80\n" +
            "      },\n" +
            "      {\n" +
            "          \"mac\": \"DD:1E:B2:F2:BD:BE\",\n" +
            "          \"lat\": 32.07031,\n" +
            "          \"lon\": 34.84352,\n" +
            "          \"alt\": 2,\n" +
            "          \"type\": 1,\n" +
            "          \"name\":\"605,2,Yuval's room\",\n" +
            "          \"alert_on_db\": -60,\n" +
            "          \"alert_off_db\": -80\n" +
            "      },\n" +
            "      {\n" +
            "          \"mac\":\"DC:6B:E6:77:B8:10\",\n" +
            "          \"lat\": 32.07033,\n" +
            "          \"lon\": 34.84352,\n" +
            "          \"alt\": 2,\n" +
            "          \"type\": 1,\n" +
            "          \"name\":\"605,2,Center\",\n" +
            "          \"alert_on_db\": -60,\n" +
            "          \"alert_off_db\": -80\n" +
            "      },\n" +
            "      {\n" +
            "          \"mac\":\"CA:3B:44:3E:B4:1A\",\n" +
            "          \"lat\": 32.07044,\n" +
            "          \"lon\": 34.84348,\n" +
            "          \"alt\": 0,\n" +
            "          \"type\": 1,\n" +
            "          \"name\":\"605,0,NW,Entrance\",\n" +
            "          \"alert_on_db\": -60,\n" +
            "          \"alert_off_db\": -80   }\n" +
            "  ]\n" +
            "}";
    //data
    private ArrayList<BleTag> data = new ArrayList<>();
    public TagsContainer (String s) {
        try {
            JSONObject json = new JSONObject(s);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    //constructor
    public TagsContainer () {
        try {
            JSONObject json = new JSONObject(default_data);
            init1(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public TagsContainer (JSONObject json){
        try {
            init1(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void init1(JSONObject json) throws JSONException {
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

