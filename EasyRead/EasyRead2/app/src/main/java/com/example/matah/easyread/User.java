package com.example.matah.easyread;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by matah on 17/10/30.
 */

public class User {
    private String email,password;

    public User(){
    }
    public User( String email,String password){
        this.email = email;
        this.password = password;
    }
    @Exclude
    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("email",email);
        result.put("password",password);
        return result;
    }
}
