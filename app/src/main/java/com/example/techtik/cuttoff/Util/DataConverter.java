package com.example.techtik.cuttoff.Util;

import com.example.techtik.cuttoff.Models.Contact;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import androidx.room.TypeConverter;

public class DataConverter {

    @TypeConverter
    public String fromContacts(ArrayList<String> phones){
        if(phones==null)
            return (null);
//        Convert the object to json to save it
        Gson  gson=new Gson();
        Type type=new TypeToken<ArrayList<String>>(){}.getType();
        String json=gson.toJson(phones,type);
        return json;
    }

    @TypeConverter
    public ArrayList<String> toContacts(String phones){
        if(phones==null)
            return (null);
//        Convert the object back from json
        Gson  gson=new Gson();
        Type type=new TypeToken<ArrayList<String>>(){}.getType();
        ArrayList<String> phone=gson.fromJson(phones,type);
        return phone;
    }

}
