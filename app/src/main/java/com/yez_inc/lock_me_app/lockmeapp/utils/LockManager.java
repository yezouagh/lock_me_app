package com.yez_inc.lock_me_app.lockmeapp.utils;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;

public class LockManager {
    private static SharedPreferences preferences;
    private final String  isSet_Tag ="isSet_Tag", background_Tag="background", background_isColor_Tag="background_isColor",
            lock_type_Tag="lock_type", key_email_Tag="key_email", answer1_Tag="answer1" ,answer2_Tag="answer2",
            key_question2_Tag="key_question2", key_question1_Tag="key_question1",show_notifications_Tag="show_notifications",
            sleep_on_tap_Tag="sleep_on_tap",Lock_enabled_Tag="Lock_enabled",Widgets_Tag="Widgets",
            fast_unLock_Tag="fast_unLock",Skip_gestures_Tag="Skip_gestures", vibrate_Tag="vibrate",
            display_Name_Tag="display_Name",Theme_Color_Tag="Theme_Color", Lock_Tag="Lock", first_Time_Tag="first_Time";
    public LockManager(Context context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }
    public void setNewLock(String Lock, int LockType)
    {
        try
        {
            SharedPreferences.Editor editor= preferences.edit();
            editor.putString(Lock_Tag, Lock);
            editor.putInt(lock_type_Tag, LockType);
            editor.apply();
        } catch(Exception e) {e.printStackTrace();}
    }
    public void setLockType(int LockType)
    {
        try
        {
            SharedPreferences.Editor editor= preferences.edit();
            editor.putInt(lock_type_Tag, LockType);
            editor.apply();
        } catch(Exception e) {e.printStackTrace();}
    }
    public String getLock()
    {
        try
        {
            return preferences.getString(Lock_Tag,"");
        } catch(Exception e) {e.printStackTrace();}
        return "";
    }
    public int getLockType()
    {
        try
        {
            return preferences.getInt(lock_type_Tag, 0);
        } catch(Exception e) {e.printStackTrace();}
        return 0;
    }
    public String getLockBackground()
    {
        try
        {
            return preferences.getString(background_Tag,"");
        } catch(Exception e) {e.printStackTrace();}
        return "";
    }
    public void setLockBackground(String Background)
    {
        try
        {
            preferences.edit().putString(background_Tag, Background).apply();
        } catch(Exception e) {e.printStackTrace();}
    }
    public int getTheme_Color()
    {
        try
        {
            return preferences.getInt(Theme_Color_Tag, Color.WHITE);
        } catch(Exception e) {e.printStackTrace();}
        return Color.WHITE;
    }
    public void setTheme_Color(int Theme_Color)
    {
        try
        {
            preferences.edit().putInt(Theme_Color_Tag, Theme_Color).apply();
        } catch(Exception e) {e.printStackTrace();}
    }
    public String getDisplay_Name()
    {
        try
        {
            return preferences.getString(display_Name_Tag,"");
        } catch(Exception e) {e.printStackTrace();}
        return "";
    }
    public void setDisplay_Name(String Display_Name)
    {
        try
        {
            preferences.edit().putString(display_Name_Tag, Display_Name).apply();
        } catch(Exception e) {e.printStackTrace();}
    }
    public String getWidgets()
    {
        try
        {
            return preferences.getString(Widgets_Tag,"01");
        } catch(Exception e) {e.printStackTrace();}
        return "01";
    }
    public void setWidgets(String Widgets)
    {
        try
        {
            preferences.edit().putString(Widgets_Tag, Widgets).apply();
        } catch(Exception e) {e.printStackTrace();}
    }
    public String getEmail()
    {
        try
        {
            return preferences.getString(key_email_Tag,"");
        } catch(Exception e) {e.printStackTrace();}
        return "";
    }
    public void setEmail(String Email)
    {
        try
        {
            preferences.edit().putString(key_email_Tag, Email).apply();
        } catch(Exception e) {e.printStackTrace();}
    }
    public int getQuestion1()
    {
        try
        {
            return preferences.getInt(key_question1_Tag,0);
        } catch(Exception e) {e.printStackTrace();}
        return 0;
    }
    public void setQuestion1(int Question1)
    {
        try
        {
            preferences.edit().putInt(key_question1_Tag, Question1).apply();
        } catch(Exception e) {e.printStackTrace();}
    }
    public int getQuestion2()
    {
        try
        {
            return preferences.getInt(key_question2_Tag,0);
        } catch(Exception e) {e.printStackTrace();}
        return 0;
    }
    public void setQuestion2(int Question2)
    {
        try
        {
            preferences.edit().putInt(key_question2_Tag, Question2).apply();
        } catch(Exception e) {e.printStackTrace();}
    }
    public String getAnswer1()
    {
        try
        {
            return preferences.getString(answer1_Tag,"");
        } catch(Exception e) {e.printStackTrace();}
        return "";
    }
    public void setAnswer1(String Answer1)
    {
        try
        {
            preferences.edit().putString(answer1_Tag, Answer1).apply();
        } catch(Exception e) {e.printStackTrace();}
    }
    public String getAnswer2()
    {
        try
        {
            return preferences.getString(answer2_Tag,"");
        } catch(Exception e) {e.printStackTrace();}
        return "";
    }
    public void setAnswer2(String Answer2)
    {
        try
        {
            preferences.edit().putString(answer2_Tag, Answer2).apply();
        } catch(Exception e) {e.printStackTrace();}
    }
    public boolean isLockRight(String Lock)
    {
        try
        {
            return  Lock.equals(preferences.getString(Lock_Tag, ""));
        } catch(Exception e) {e.printStackTrace();}
        return false;
    }
    public boolean isBackground_Color()
    {
        try
        {
            return  preferences.getBoolean(background_isColor_Tag, false);
        } catch(Exception e) {e.printStackTrace();}
        return false;
    }
    public void setIsBackground_Color(boolean IsBackground_Color)
    {
        try
        {
            preferences.edit().putBoolean(background_isColor_Tag, IsBackground_Color).apply();
        } catch(Exception e) {e.printStackTrace();}
    }
    public boolean isLock_enabled()
    {
        try
        {
            return  preferences.getBoolean(Lock_enabled_Tag, true);
        } catch(Exception e) {e.printStackTrace();}
        return true;
    }
    public void setIsLock_enabled(boolean Lock_enabled)
    {
        try
        {
            preferences.edit().putBoolean(Lock_enabled_Tag, Lock_enabled).apply();
        } catch(Exception e) {e.printStackTrace();}
    }
    public boolean isFast_unLock()
    {
        try
        {
            return  preferences.getBoolean(fast_unLock_Tag, false);
        } catch(Exception e) {e.printStackTrace();}
        return false;
    }
    public void setIsFast_unLock(boolean Fast_unLock)
    {
        try
        {
            preferences.edit().putBoolean(fast_unLock_Tag, Fast_unLock).apply();
        } catch(Exception e) {e.printStackTrace();}
    }
    public boolean isShow_notifications()
    {
        try
        {
            return  preferences.getBoolean(show_notifications_Tag, false);
        } catch(Exception e) {e.printStackTrace();}
        return false;
    }
    public void setShow_notifications(boolean show_notifications)
    {
        try
        {
            preferences.edit().putBoolean(show_notifications_Tag, show_notifications).apply();
        } catch(Exception e) {e.printStackTrace();}
    }
    public boolean isSleep_on_tap()
    {
        try
        {
            return  preferences.getBoolean(sleep_on_tap_Tag, false);
        } catch(Exception e) {e.printStackTrace();}
        return false;
    }
    public void setIsSleep_on_tap(boolean Sleep_on_tap)
    {
        try
        {
            preferences.edit().putBoolean(sleep_on_tap_Tag, Sleep_on_tap).apply();
        } catch(Exception e) {e.printStackTrace();}
    }
    public boolean isSkip_gestures()
    {
        try
        {
            return  preferences.getBoolean(Skip_gestures_Tag, false);
        } catch(Exception e) {e.printStackTrace();}
        return false;
    }
    public void setIsSkip_gestures(boolean Skip_gestures)
    {
        try
        {
            preferences.edit().putBoolean(Skip_gestures_Tag, Skip_gestures).apply();
        } catch(Exception e) {e.printStackTrace();}
    }
    public boolean isFirst_Time()
    {
        try
        {
            return  preferences.getBoolean(first_Time_Tag, true);
        } catch(Exception e) {e.printStackTrace();}
        return true;
    }
    public void setIsFirst_Time(boolean First_Time)
    {
        try
        {
            preferences.edit().putBoolean(first_Time_Tag, First_Time).apply();
        } catch(Exception e) {e.printStackTrace();}
    }
    public boolean isSet()
    {
        try
        {
            return  preferences.getBoolean(isSet_Tag, false);
        } catch(Exception e) {e.printStackTrace();}
        return false;
    }
    public void setIsSet(boolean IsSet)
    {
        try
        {
            preferences.edit().putBoolean(isSet_Tag, IsSet).apply();
        } catch(Exception e) {e.printStackTrace();}
    }
    public boolean isVibrate()
    {
        try
        {
            return  preferences.getBoolean(vibrate_Tag, false);
        } catch(Exception e) {e.printStackTrace();}
        return false;
    }
    public void setVibrate(boolean IsVibrate)
    {
        try
        {
            preferences.edit().putBoolean(vibrate_Tag, IsVibrate).apply();
        } catch(Exception e) {e.printStackTrace();}
    }
}
