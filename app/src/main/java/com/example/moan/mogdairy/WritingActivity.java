package com.example.moan.mogdairy;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
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
    private EditText diaryTitle;
    private EditText diaryContent;

    private CardView weatherCardView;
    private ImageView dailyPicView;
    private TextView dailyBriefView;
    private TextView dailyTemperatureView;
    private TextView dailyLocationView;
    private TextView dailyQuoteView;

    private EditText ddlMonthView;
    private EditText ddlDayView;
    private EditText ddlHourView;
    private EditText ddlMinuteView;
    private CardView deadlineCardView;

    private String priority;

    private int diaryId;

    private static boolean isWeatherInfoShowing = false;
    private static boolean isDeadlineViewShowing = false;
    private boolean visiableCard = false;

    //    private final String getWeather = "https://api.seniverse.com/v3/weather/now.json?key=nyjys9yz0fta6rqj&location=jingmen&language=zh-Hans&unit=c";
    public static final String getWeatherHead = "https://api.seniverse.com/v3/weather/now.json?" +
            "key=nyjys9yz0fta6rqj&location=";
    public static final String getWeatherTail = "&language=zh-Hans&unit=c";
    public final String getBingPic = "http://guolin.tech/api/bing_pic";

    private static int musicFlag = 1;
    private static int isPlaying = 2;
    private boolean hasClock = false;

    private AlarmManager manager;

    /*
     *
     *  default 0 -> the user never click the clock button ;
     *  change to 1 -> the user foget to start the clock ;
     *  change to 2 -> best done ;
     *
     *
     */
    private int savedClock = 0;

    private static int month;
    private static int day;
    private static int hour;
    private static int minute;

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
            if (diary.isHasClock()) {
                setUnfocusable();
                ddlMonthView.setText(diary.getMonth());
                ddlHourView.setText(diary.getHour());
                ddlDayView.setText(diary.getDay());
                ddlMinuteView.setText(diary.getMinute());
            }
        }

        // TODO: 11/5/18 let the main & music more flexible
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
            @Override
            public void onClick(View v) {
                if (!isDeadlineViewShowing) {
                    deadlineCardView.setVisibility(View.VISIBLE);
                } else {
                    deadlineCardView.setVisibility(View.GONE);
                }
                isDeadlineViewShowing = !isDeadlineViewShowing;
                savedClock = 1;
                setDeadline();
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


    private void initView() {
        diaryTitle = findViewById(R.id.writing_diary_title);
        diaryContent = findViewById(R.id.writing_diary_content);
        dailyBriefView = findViewById(R.id.daily_brief);
        dailyTemperatureView = findViewById(R.id.daily_temperature);
        dailyLocationView = findViewById(R.id.daily_location);
        dailyPicView = findViewById(R.id.daily_pic);
        dailyQuoteView = findViewById(R.id.daily_quote);
        weatherCardView = findViewById(R.id.writing_diary_cardview_daily_weather);

        ddlMonthView = findViewById(R.id.month);
        ddlDayView = findViewById(R.id.day);
        ddlHourView = findViewById(R.id.hour);
        ddlMinuteView = findViewById(R.id.minute);
        deadlineCardView = findViewById(R.id.writing_diary_cardview_choose_deadline);
    }


    private void setDeadline() {
        final Button setDdl = findViewById(R.id.set);
        setDdl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                month = Integer.parseInt(ddlMonthView.getText().toString());
                day = Integer.parseInt(ddlDayView.getText().toString());
                hour = Integer.parseInt(ddlHourView.getText().toString());
                minute = Integer.parseInt(ddlMinuteView.getText().toString());

                Calendar calendar = Calendar.getInstance();
                final int officialMonth = calendar.get(Calendar.MONTH);
                final int officialDay = calendar.get(Calendar.DAY_OF_MONTH);
                final int officialHour = calendar.get(Calendar.HOUR_OF_DAY);
                final int officialMinute = calendar.get(Calendar.MINUTE);

                switch (month) {
                    case 1:
                    case 3:
                    case 5:
                    case 7:
                    case 8:
                    case 10:
                    case 12:
                        if (day <= 31 && day >= 0 && hour <= 23 && hour >= 0 && minute <= 59 &&
                                minute >= 0) {

                        } else {
                            clearEditErea();
                        }
                        break;
                    case 4:
                    case 6:
                    case 9:
                    case 11:
                        if (day <= 30 && day >= 0 && hour <= 23 && hour >= 0 && minute <= 59 &&
                                minute >= 0) {

                        } else {
                            clearEditErea();
                        }
                        break;
                    case 2:
                        if (day <= 28 && day >= 0 && hour <= 23 && hour >= 0 && minute <= 59 &&
                                minute >= 0) {
                        } else {
                            clearEditErea();
                        }
                        break;
                }
                if (!setCorrect(month, officialMonth, day, officialDay, hour, officialHour,
                        minute, officialMinute)) {
                    clearEditErea();
                } else {
                    setUnfocusable();
                    //        updateDiaryClock(month, day, hour, minute);
                    setClock(month, day, hour, minute);
                    hasClock = true;
                    savedClock = 2;
                }
            }
        });
    }

    private boolean setCorrect(int month, int officialMonth, int day, int officialDay, int hour,
                               int officialHour, int minute, int officialMinute) {

        if (month < officialMonth) {
            return false;
        }
        if (month == officialMonth) {
            if (day < officialDay) {
                return false;
            }
            if (day == officialDay) {
                if (hour < officialHour) {
                    return false;
                }
                if (hour == officialHour) {
                    return minute >= officialMinute;
                }
            }
        }
        return true;
    }


    private void setClock(int month, int day, int hour, int minute) {
        manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent clockIntent = new Intent(WritingActivity.this, RingReceiver.class);
        PendingIntent pendingClockIntent = PendingIntent.getBroadcast(WritingActivity.this,
                0, clockIntent, 0);

        Calendar clockCalendar = Calendar.getInstance();
        clockCalendar.set(Calendar.MONTH, month - 1);
        clockCalendar.set(Calendar.DAY_OF_MONTH, day);
        clockCalendar.set(Calendar.HOUR_OF_DAY, hour);
        clockCalendar.set(Calendar.MINUTE, minute);
        manager.set(AlarmManager.RTC_WAKEUP, clockCalendar.getTimeInMillis(), pendingClockIntent);
        Toast.makeText(this, "the clock will ring at " + month + "月" + day + "日" +
                hour + "时" + minute + "分", Toast.LENGTH_SHORT).show();
    }

    private void setUnfocusable() {
        ddlMinuteView.setFocusable(false);
        ddlHourView.setFocusable(false);
        ddlDayView.setFocusable(false);
        ddlMonthView.setFocusable(false);

    }

    private void clearEditErea() {
        Toast.makeText(WritingActivity.this, "something wrong ?",
                Toast.LENGTH_SHORT).show();
        ddlMonthView.setText("");
        ddlDayView.setText("");
        ddlHourView.setText("");
        ddlMinuteView.setText("");

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
            String temp = getWeatherHead + MainActivity.defaultLocation + getWeatherTail;
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
        HttpUtil.sendOkHttpRequest(getBingPic, new Callback() {
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
            diary.setMonth(month);
            diary.setDay(day);
            diary.setHour(hour);
            diary.setMinute(minute);
            diary.setTotal(month*43200+day*1440+hour*60+minute);
        }
        diary.save();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //TODO
    }
}

