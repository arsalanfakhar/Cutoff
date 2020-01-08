package com.example.techtik.cuttoff.Util;

import com.example.techtik.cuttoff.Models.Contact;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import androidx.room.TypeConverter;

public class DataConverter {

    @TypeConverter
    public String fromContacts(Contact contact){
        if(contact==null)
            return (null);
//        Convert the object to json to save it
        Gson  gson=new Gson();
        Type type=new TypeToken<Contact>(){}.getType();
        String json=gson.toJson(contact,type);
        return json;
    }

    @TypeConverter
    public Contact toContacts(String contactStr){
        if(contactStr==null)
            return (null);
//        Convert the object back from json
        Gson  gson=new Gson();
        Type type=new TypeToken<Contact>(){}.getType();
        Contact contact=gson.fromJson(contactStr,type);
        return contact;
    }

}
