package testpreference.com.testcalendar;

import android.Manifest;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private Spinner calendarIdSpinner;
    private Hashtable<String,String> calendarIdTable;
    private Button newEventButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        calendarIdSpinner = (Spinner) findViewById(R.id.calendarid_spinner);
        newEventButton = (Button) findViewById(R.id.newevent_button);

        newEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CalendarHelper.haveCalendarReadWritePermissions(MainActivity.this))
                {
                    addNewEvent();
                }
                else
                {
                    CalendarHelper.requestCalendarReadWritePermission(MainActivity.this);
                }
            }
        });


        calendarIdSpinner.setOnItemSelectedListener(new CustomOnItemSelectedListener());

        if (CalendarHelper.haveCalendarReadWritePermissions(this))
        {
            //Load calendars
            calendarIdTable = CalendarHelper.listCalendarId(this);

            updateCalendarIdSpinner();

        }


    }

    private void updateCalendarIdSpinner()
    {
        if (calendarIdTable==null)
        {
            return;
        }

        List<String> list = new ArrayList<String>();

        Enumeration e = calendarIdTable.keys();
        while (e.hasMoreElements()) {
            String key = (String) e.nextElement();
            list.add(key);
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        calendarIdSpinner.setAdapter(dataAdapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode==CalendarHelper.CALENDARHELPER_PERMISSION_REQUEST_CODE)
        {
            if (CalendarHelper.haveCalendarReadWritePermissions(this))
            {
                Toast.makeText(this, (String)"Have Calendar Read/Write Permission.",
                        Toast.LENGTH_LONG).show();

            }

        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void addNewEvent()
    {
        if (calendarIdTable==null)
        {
            Toast.makeText(this, (String)"No calendars found. Please ensure at least one google account has been added.",
                    Toast.LENGTH_LONG).show();
            //Load calendars
            calendarIdTable = CalendarHelper.listCalendarId(this);

            updateCalendarIdSpinner();

            return;
        }


        final long oneHour = 1000 * 60 * 60;
        final long tenMinutes = 1000 * 60 * 10;

        long oneHourFromNow = (new Date()).getTime() + oneHour;
        long tenMinutesFromNow = (new Date()).getTime() + tenMinutes;


        String calendarString = calendarIdSpinner.getSelectedItem().toString();

        int calendar_id = Integer.parseInt(calendarIdTable.get(calendarString));

        CalendarHelper.MakeNewCalendarEntry(this, "Test", "Add event", "Somewhere",tenMinutesFromNow,tenMinutesFromNow+oneHour,false,true,calendar_id,3);

    }

}
