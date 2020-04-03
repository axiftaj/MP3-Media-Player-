package com.example.musicplayerapplication;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

import static android.graphics.PorterDuff.*;

public class MediaPlayerActivity extends AppCompatActivity {


    Button next, previous , pause ;
    TextView soundTextLabel ;
    SeekBar seekBar ;

    static MediaPlayer mymediaPlayer ;
    int position ;
    ArrayList<File> mySong ;
    Thread updateSeekBar ;
    String name ;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_player);

        //intilizing
        seekBar = findViewById(R.id.seekBar);
        next = findViewById(R.id.next);
        previous = findViewById(R.id.previous);
        pause = findViewById(R.id.pause);
        soundTextLabel = findViewById(R.id.songLabel);


        getSupportActionBar().setTitle("Now Playing");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        updateSeekBar = new Thread(){

            @Override
            public void run() {

                int totalDuration = mymediaPlayer.getDuration();
                int currentPostion = 0 ;

                while (currentPostion < totalDuration){

                    try {
                        sleep(500);
                        currentPostion = mymediaPlayer.getCurrentPosition();
                        seekBar.setProgress(currentPostion);

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        };

        if (mymediaPlayer != null){
            mymediaPlayer.stop();
            mymediaPlayer.release();
        }

        Intent i  = getIntent();
        Bundle bundle  = i.getExtras();

        mySong = (ArrayList) bundle.getParcelableArrayList("song");
        name = mySong.get(position).getName().toLowerCase();

        String songName = i.getStringExtra("songName");
        soundTextLabel.setText(songName);
        soundTextLabel.setSelected(true);

        position = bundle.getInt("pos" , 0);

        Uri u = Uri.parse(mySong.get(position).toString());
        mymediaPlayer = MediaPlayer.create(getApplicationContext(), u );
        mymediaPlayer.start();
        seekBar.setMax(mymediaPlayer.getDuration());



        updateSeekBar.start();

        seekBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), Mode.MULTIPLY);
        seekBar.getThumb().setColorFilter(getResources().getColor(R.color.colorPrimary) , Mode.SRC_IN);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                mymediaPlayer.seekTo(seekBar.getProgress());
            }
        });


        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seekBar.setMax(mymediaPlayer.getDuration());
                if (mymediaPlayer.isPlaying()){

                    pause.setBackgroundResource(R.drawable.icon_play);
                    mymediaPlayer.pause();
                }else {
                    pause.setBackgroundResource(R.drawable.icon_pause);
                    mymediaPlayer.start();

                }
            }
        });


        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mymediaPlayer.stop();
                mymediaPlayer.release();
                position = ((position+1)%mySong.size()) ;
                Uri u = Uri.parse(mySong.get(position).toString());
                mymediaPlayer = MediaPlayer.create(getApplicationContext(), u);
                name = mySong.get(position).getName().toString();
                soundTextLabel.setText(name);
                mymediaPlayer.start();


            }
        });

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mymediaPlayer.stop();
                mymediaPlayer.release();

                position = ((position-1)<0)?(mySong.size()-1):(position-1);
                Uri u = Uri.parse(mySong.get(position).toString());
                mymediaPlayer = MediaPlayer.create(getApplicationContext(), u);

                name = mySong.get(position).getName().toString();
                soundTextLabel.setText(name);
                mymediaPlayer.start();

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
