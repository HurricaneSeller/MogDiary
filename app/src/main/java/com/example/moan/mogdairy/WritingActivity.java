package com.example.moan.mogdairy;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.moan.mogdairy.gson.Weather;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

// TODO: 11/6/18 cancle the clock ;
public class WritingActivity extends BaseActivity {
    private static final int SAVE_WITHOUT_SET_CLOCK = 0;
    private static final int SAVE_WITH_CLOCK = 2;
    public static final String GET_WEATHER_HEAD = "https://api.seniverse.com/v3/weather/now.json?" +
            "key=nyjys9yz0fta6rqj&location=";
    public static final String GET_WEATHER_TAIL = "&language=zh-Hans&unit=c";
    public static final String GET_BING_PIC = "http://guolin.tech/api/bing_pic";


    private EditText diaryTitle;
    private EditText diaryContent;

    private CardView weatherCardView;
    private ImageView dailyPicView;
    private TextView dailyBriefView;
    private TextView dailyTemperatureView;
    private TextView dailyLocationView;
    private TextView dailyQuoteView;

    private String priority;
    private int diaryId;

    private static boolean isWeatherInfoShowing = false;
    private static boolean visiableCard = false;

    private static int musicFlag = 1;
    private static int isPlaying = 2;
    private boolean hasClock = false;

    /**
     * default 0 -> the user never click the clock button ;
     * change to 1 -> the user foget to start the clock ;
     * change to 2 -> best done ;
     */
    private int savedClock = SAVE_WITHOUT_SET_CLOCK;


    private static int mYear;
    private static int mMonth;
    private static int mDay;
    private static int mHour;
    private static int mMinute;

    private static int system_year;
    private static int system_month;
    private static int system_day;
    private static int system_hour;
    private static int system_minute;
    private AlarmManager manager;
    private PendingIntent pendingClockIntent;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writing);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        initView();

        //get data
        final Intent intent = getIntent();
        final int where = intent.getIntExtra("where", 0);
        priority = intent.getStringExtra("priority");
        if (where == DiaryAdapter.WHERE) {
            Diary diary = (Diary) intent.getSerializableExtra("diaryGotten");
            diaryTitle.setText(diary.getTitle());
            diaryContent.setText(diary.getContent());
            diaryId = diary.getId();
        }

        final FloatingActionMenu main = findViewById(R.id.floating_main);
        final FloatingActionMenu music = findViewById(R.id.floating_music);
        final FloatingActionButton save = findViewById(R.id.floating_save);
        final FloatingActionButton back = findViewById(R.id.floating_back);
        final FloatingActionButton clock = findViewById(R.id.floating_clock);
        final FloatingActionButton cloud = findViewById(R.id.floating_cloud);
        final FloatingActionButton musicStart = findViewById(R.id.floating_start);
        final FloatingActionButton musicStop = findViewById(R.id.floating_stop);



        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (where) {
                    case MainActivity.WHERE:
                        saveDiary();
                        Toast.makeText(WritingActivity.this, "DONE successfully !",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case DiaryAdapter.WHERE:
                        updateDiaryContent();
                        Toast.makeText(WritingActivity.this, "Change saved !",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
                Intent intent = new Intent(WritingActivity.this, MainActivity.class);
                startActivity(intent);
            }

        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backWarning();
            }
        });
        clock.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                setDDL();
            }
        });

        cloud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isWeatherInfoShowing) {
                    Toast.makeText(WritingActivity.this, "please wait a few minutes",
                            Toast.LENGTH_SHORT).show();
                }
                isWeatherInfoShowing = !isWeatherInfoShowing;
                getCloudInfo();
            }
        });

        musicStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startMusicIntent = new Intent(WritingActivity.this, MusicService.class);
                startService(startMusicIntent);
                Toast.makeText(WritingActivity.this, "music start", Toast.LENGTH_SHORT).show();
                musicFlag = isPlaying;
            }
        });
        musicStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (musicFlag == isPlaying) {
                    Intent stopMusicIntent = new Intent(WritingActivity.this, MusicService.class);
                    stopService(stopMusicIntent);
                    Toast.makeText(WritingActivity.this, "music stop", Toast.LENGTH_SHORT)
                            .show();
                } else {
                    Toast.makeText(WritingActivity.this, "music is not playing yet ?",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setDDL() {
        Calendar calendar = Calendar.getInstance();
        system_year = calendar.get(Calendar.YEAR);
        system_month = calendar.get(Calendar.MONTH);
        system_day = calendar.get(Calendar.DAY_OF_MONTH);
        system_hour = calendar.get(Calendar.HOUR_OF_DAY);
        system_minute = calendar.get(Calendar.MINUTE);

        DatePickerDialog datePickerDialog = new DatePickerDialog(WritingActivity.this,
                new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, final int month, int dayOfMonth) {
                mYear = year;
                mMonth = month;
                mDay = dayOfMonth;
                TimePickerDialog timePickerDialog = new TimePickerDialog(WritingActivity.this,
                        android.app.AlertDialog.THEME_HOLO_LIGHT, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        mHour = hourOfDay;
                        mMinute = minute;
                        if (mHour < system_hour && mMinute < system_minute) {
                            Toast.makeText(WritingActivity.this, "invalid time !",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            setClock(mMonth, mDay, mHour, mMinute);
                            hasClock = true;
                        }

                    }
                }, system_hour, system_minute, true);
                timePickerDialog.show();
            }
        }, system_year, system_month, system_day);
        datePickerDialog.show();
        DatePicker datePicker = datePickerDialog.getDatePicker();
        datePicker.setMinDate(System.currentTimeMillis());
    }

    // TODO: 11/8/18
    private void changeDDL() {

    }

    private void cancleDDL() {
        manager.cancel(pendingClockIntent);
    }

    private void initView() {
        diaryTitle = findViewById(R.id.writing_diary_title);
        diaryContent = findViewById(R.id.writing_diary_content);
        dailyBriefView = findViewById(R.id.daily_brief);
        dailyTemperatureView = findViewById(R.id.daily_temperature);
        dailyLocationView = findViewById(R.id.daily_location);
        dailyPicView = findViewById(R.id.daily_pic);
        dailyQuoteView = findViewById(R.id.daily_quote);
        weatherCardView = findViewById(R.id.writing_diary_cardview_daily_weather);

    }


    private void setClock(int month, int day, int hour, int minute) {
        manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent clockIntent = new Intent(WritingActivity.this, RingReceiver.class);
        clockIntent.putExtra("clock_title", diaryTitle.getText().toString());
        Log.d("moanbigking", diaryTitle.getText().toString());
        pendingClockIntent = PendingIntent.getBroadcast(WritingActivity.this,
                0, clockIntent, 0);

        Calendar clockCalendar = Calendar.getInstance();
        clockCalendar.set(Calendar.MONTH, month);
        clockCalendar.set(Calendar.DAY_OF_MONTH, day);
        clockCalendar.set(Calendar.HOUR_OF_DAY, hour);
        clockCalendar.set(Calendar.MINUTE, minute);
        Log.d("moanbigking", month + "-" + day + "-" + hour + "-" + minute);
        manager.set(AlarmManager.RTC_WAKEUP, clockCalendar.getTimeInMillis(), pendingClockIntent);
        Toast.makeText(this, "the clock will ring at " + month + "月" + day + "日" +
                hour + "时" + minute + "分", Toast.LENGTH_SHORT).show();
    }


    private void getCloudInfo() {
        //get localposition automaticlly
        //this demo can only get limited cloud info
        //because the city is setted manually by the user(default wuhan
        //and if user print in an invalid city the user cannot get a weather forecast
        if (visiableCard) {
            weatherCardView.setVisibility(View.GONE);
        } else {
            weatherCardView.setVisibility(View.VISIBLE);
            String temp = GET_WEATHER_HEAD + MainActivity.defaultLocation + GET_WEATHER_TAIL;
            HttpUtil.sendOkHttpRequest(temp, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    Toast.makeText(WritingActivity.this, "connection failed", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responsedata = response.body().string();
                    showResponse(responsedata);
                }
            });
        }
        visiableCard = !visiableCard;
    }

    public void showResponse(final String response) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("results");
                    String weatherContent = jsonArray.getJSONObject(0).toString();
                    Weather weather = new Gson().fromJson(weatherContent, Weather.class);
                    String brief = weather.now.weatherBrief;
                    String temperature = weather.now.Temperature;
                    String location = weather.location.name;
                    dailyBriefView.setText(brief);
                    dailyTemperatureView.setText(temperature + "℃");
                    dailyLocationView.setText(location);
                    loadingPic();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void loadingPic() {
        HttpUtil.sendOkHttpRequest(GET_BING_PIC, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPic = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WritingActivity.this)
                        .edit();
                editor.putString("bing", bingPic);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WritingActivity.this).load(bingPic).into(dailyPicView);
                    }
                });
            }
        });
    }


    private void backWarning() {
        AlertDialog.Builder builder = new AlertDialog.Builder(WritingActivity.this);
        builder.setTitle("WARNING")
                .setMessage("change haven't saved ;P")
                .setCancelable(false)
                .setPositiveButton("exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(WritingActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("back", null)
                .create().show();
    }


    private void updateDiaryContent() {
        String diarytitle = diaryTitle.getText().toString();
        String diarycontent = diaryContent.getText().toString();
        Diary diary = new Diary();
        diary.setTitle(diarytitle);
        diary.setContent(diarycontent);
        diary.update(diaryId);
    }

    private void saveDiary() {
        String diarytitle = diaryTitle.getText().toString();
        String diarycontent = diaryContent.getText().toString();
        if (savedClock == 1) {
            Toast.makeText(this, "your clock haven't been saved yet", Toast.LENGTH_SHORT).show();
        }
        Diary diary = new Diary();
        diary.setTitle(diarytitle);
        diary.setContent(diarycontent);
        diary.setPriority(priority);
        diary.setHasClock(hasClock);
        Log.d("moanbigking", String.valueOf(hasClock));
        if (hasClock) {
            Log.d("moanbigking", "here");
            diary.setMonth(mMonth);
            diary.setDay(mDay);
            diary.setHour(mHour);
            diary.setMinute(mMinute);
            diary.setTotal(mMonth * 43200 + mDay * 1440 + mHour * 60 + mMinute);
        }
        diary.save();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //TODO
    }
}

