package testpreference.com.testcalendar;

/**
 * Created by cyong on 11/05/16.
 */
import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract.Events;
import android.provider.CalendarContract.Reminders;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.TimeZone;

public class CalendarHelper {

    //Remember to initialize this activityObj first, by calling initActivityObj(this) from
//your activity
    private static final String TAG = "CalendarHelper";
    public static final int CALENDARHELPER_PERMISSION_REQUEST_CODE = 99;


    public static void MakeNewCalendarEntry(Activity caller,String title,String description,String location,long startTime,long endTime, boolean allDay,boolean hasAlarm, int calendarId,int selectedReminderValue) {

        ContentResolver cr = caller.getContentResolver();
        ContentValues values = new ContentValues();
        values.put(Events.DTSTART, startTime);
        values.put(Events.DTEND, endTime);
        values.put(Events.TITLE, title);
        values.put(Events.DESCRIPTION, description);
        values.put(Events.CALENDAR_ID, calendarId);
        values.put(Events.STATUS, Events.STATUS_CONFIRMED);


        if (allDay)
        {
            values.put(Events.ALL_DAY, true);
        }

        if (hasAlarm)
        {
            values.put(Events.HAS_ALARM, true);
        }

        //Get current timezone
        values.put(Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());
        Log.i(TAG, "Timezone retrieved=>"+TimeZone.getDefault().getID());
        Uri uri = cr.insert(Events.CONTENT_URI, values);
        Log.i(TAG, "Uri returned=>"+uri.toString());
        // get the event ID that is the last element in the Uri
        long eventID = Long.parseLong(uri.getLastPathSegment());

        if (hasAlarm)
        {
            ContentValues reminders = new ContentValues();
            reminders.put(Reminders.EVENT_ID, eventID);
            reminders.put(Reminders.METHOD, Reminders.METHOD_ALERT);
            reminders.put(Reminders.MINUTES, selectedReminderValue);

            Uri uri2 = cr.insert(Reminders.CONTENT_URI, reminders);
        }


    }

    public static void requestCalendarReadWritePermission(Activity caller)
    {
        List<String> permissionList = new ArrayList<String>();

        if  (ContextCompat.checkSelfPermission(caller,Manifest.permission.WRITE_CALENDAR)!=PackageManager.PERMISSION_GRANTED)
        {
            permissionList.add(Manifest.permission.WRITE_CALENDAR);

        }

        if  (ContextCompat.checkSelfPermission(caller,Manifest.permission.READ_CALENDAR)!=PackageManager.PERMISSION_GRANTED)
        {
            permissionList.add(Manifest.permission.READ_CALENDAR);

        }

        if (permissionList.size()>0)
        {
            String [] permissionArray = new String[permissionList.size()];

            for (int i=0;i<permissionList.size();i++)
            {
                permissionArray[i] = permissionList.get(i);
            }

            ActivityCompat.requestPermissions(caller,
                    permissionArray,
                    CALENDARHELPER_PERMISSION_REQUEST_CODE);
        }

    }

    public static Hashtable listCalendarId(Context c) {

        if (haveCalendarReadWritePermissions((Activity)c)) {

            String projection[] = {"_id", "calendar_displayName"};
            Uri calendars;
            calendars = Uri.parse("content://com.android.calendar/calendars");

            ContentResolver contentResolver = c.getContentResolver();
            Cursor managedCursor = contentResolver.query(calendars, projection, null, null, null);

            if (managedCursor.moveToFirst())
            {
                String calName;
                String calID;
                int cont = 0;
                int nameCol = managedCursor.getColumnIndex(projection[1]);
                int idCol = managedCursor.getColumnIndex(projection[0]);
                Hashtable<String,String> calendarIdTable = new Hashtable<>();

                do
                {
                    calName = managedCursor.getString(nameCol);
                    calID = managedCursor.getString(idCol);
                    Log.v(TAG, "CalendarName:" + calName + " ,id:" + calID);
                    calendarIdTable.put(calName,calID);
                    cont++;
                } while (managedCursor.moveToNext());
                managedCursor.close();

                return calendarIdTable;
            }

        }

        return null;

    }

    public static boolean haveCalendarReadWritePermissions(Activity caller)
    {
        int permissionCheck = ContextCompat.checkSelfPermission(caller,
                Manifest.permission.READ_CALENDAR);

        if (permissionCheck== PackageManager.PERMISSION_GRANTED)
        {
            permissionCheck = ContextCompat.checkSelfPermission(caller,
                    Manifest.permission.WRITE_CALENDAR);

            if (permissionCheck== PackageManager.PERMISSION_GRANTED)
            {
                return true;
            }
        }

        return false;
    }

}