package com.example.moan.mogdairy;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import gson.Weather;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class WritingActivity extends BaseActivity {
    private EditText diaryTitle;
    private EditText diaryContent;

    private CardView cardView;
    private ImageView dailyPicView;
    private TextView dailyBriefView;
    private TextView dailyTemperatureView;
    private TextView dailyLocationView;
    private TextView dailyQuoteView;
    //TODO

    private int diaryId;
    private boolean visiableCard = false;
    //    private final String getWeather = "https://api.seniverse.com/v3/weather/now.json?key=nyjys9yz0fta6rqj&location=jingmen&language=zh-Hans&unit=c";
    public static final String getWeatherHead = "https://api.seniverse.com/v3/weather/now.json?" +
            "key=nyjys9yz0fta6rqj&location=";
    public static final String getWeatherTail = "&language=zh-Hans&unit=c";
    public final String getBingPic = "http://guolin.tech/api/bing_pic";
    private static int musicFlag = 1;
    private static int isPlaying = 2;
    private static boolean isWeatherInfoShowing = false;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writing);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        diaryTitle = (EditText) findViewById(R.id.writing_diary_title);
        diaryContent = (EditText) findViewById(R.id.writing_diary_content);
        dailyBriefView = (TextView) findViewById(R.id.daily_brief);
        dailyTemperatureView = (TextView) findViewById(R.id.daily_temperature);
        dailyLocationView = (TextView) findViewById(R.id.daily_location);
        dailyPicView = (ImageView) findViewById(R.id.daily_pic);
        dailyQuoteView = (TextView) findViewById(R.id.daily_quote);
        cardView = (CardView) findViewById(R.id.card_view);

        //get data
        final Intent intent = getIntent();
        final int where = intent.getIntExtra("where", 0);
        if (where == DiaryAdapter.WHERE) {
            Diary diary = (Diary) intent.getSerializableExtra("diaryGotten");
            diaryTitle.setText(diary.getTitle());
            diaryContent.setText(diary.getContent());
            diaryId = diary.getId();
        }


        //TODO
        //let the main & music more flexible
        final FloatingActionMenu main = (FloatingActionMenu) findViewById(R.id.floating_main);
        final FloatingActionMenu music = (FloatingActionMenu) findViewById(R.id.floating_music);
        final FloatingActionButton save = (FloatingActionButton) findViewById(R.id.floating_save);
        final FloatingActionButton back = (FloatingActionButton) findViewById(R.id.floating_back);
        final FloatingActionButton clock = (FloatingActionButton) findViewById(R.id.floating_clock);
        final FloatingActionButton cloud = (FloatingActionButton) findViewById(R.id.floating_cloud);
        final FloatingActionButton musicStart = (FloatingActionButton) findViewById(R.id.floating_start);
        final FloatingActionButton musicStop = (FloatingActionButton) findViewById(R.id.floating_stop);


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
                        updateDiary();
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
                setClock();
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

    private void getCloudInfo() {
        //get localposition automaticlly
        //this demo can only get limited cloud info
        //because the city is setted manually by the user(default wuhan
        //and if user print in an invalid city the user cannot get a weather forecast
        if (visiableCard) {
            cardView.setVisibility(View.GONE);
        } else {
            cardView.setVisibility(View.VISIBLE);
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
                    Log.d("moanbigking", "??????");
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

    private void setClock() {
        final AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        final Calendar calendar = Calendar.getInstance();

        int mHour = calendar.get(Calendar.HOUR_OF_DAY);
        int mMinute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(WritingActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Calendar anotherCanlendar = Calendar.getInstance();
                anotherCanlendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                anotherCanlendar.set(Calendar.MINUTE, minute);

                Intent intent = new Intent(WritingActivity.this, RingReceiver.class);
                intent.putExtra("clock_title", diaryTitle.getText().toString());
                intent.putExtra("clock_content", diaryContent.getText().toString());

                PendingIntent pendingIntent = PendingIntent.getBroadcast(WritingActivity.this, 0, intent, 0);
                alarmManager.set(AlarmManager.RTC_WAKEUP, anotherCanlendar.getTimeInMillis(), pendingIntent);
            }
        }, mHour, mMinute, true);
        timePickerDialog.show();
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

    private void updateDiary() {
        String diarytitle = diaryTitle.getText().toString();
        String diarycontent = diaryContent.getText().toString();
        Diary diary = new Diary();
        diary.setTitle(diarytitle);
        diary.setContent(diarycontent);
        Date date = new Date();
        diary.setDate(date);
        diary.update(diaryId);
    }

    private void saveDiary() {
        String diarytitle = diaryTitle.getText().toString();
        String diarycontent = diaryContent.getText().toString();
        Diary diary = new Diary();
        diary.setTitle(diarytitle);
        diary.setContent(diarycontent);

        Date date = new Date();
        diary.setDate(date);

        diary.save();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //TODO
    }
}

