package com.snail.education.protocol.model;

import java.io.Serializable;

public class AccessToken implements Serializable
{
    public String tokenValue;
    public Boolean isNewUser;

    public String getTokenValue()
    {
        return tokenValue;
    }

    public boolean isNewUser()
    {
        if (isNewUser != null)
        {
            return isNewUser.booleanValue();
        }
        else
        {
            return true;
        }
    }
}
