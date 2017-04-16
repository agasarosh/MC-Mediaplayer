package com.example.saroshaga.mediaplayer;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.StringRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private String[] song_list;
    private String[] artist_list;
    private String[] paths;
    private MediaPlayer player;
    private Handler h;
    int curr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(MainActivity.this,                                //Permissions problem
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);                //workaround
        player = new MediaPlayer();
        h = new Handler();
        ListView v1 = (ListView) findViewById(R.id.ListView);

        song_list = getMusic();

        ArrayAdapter<String> a = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, song_list);
        v1.setAdapter(a);

        v1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    curr=position;
                    playSong(paths[position]);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        });

    }

    @Override                       //Permission problem workaround
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission granted and now can

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(MainActivity.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            // add other cases for more permissions
        }
    }

    public void pause(View v)
    {
        Button b=(Button)findViewById(R.id.toggleButton);
        if(player.isPlaying())
        {
            player.pause();
            b.setText(getString(R.string.play));

        }
        else
        {
            player.start();
            b.setText(getString(R.string.pause));
        }
    }

    public void back(View v) throws IOException
    {
        if(curr-1!=0)
        {
            playSong(paths[--curr]);

        }
    }

    public void forward(View v) throws IOException
    {
        if(curr+1!=paths.length)
        {
            playSong(paths[++curr]);
        }
    }


    public String[] getMusic()
    {
        Cursor c=getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,new String[]{MediaStore.Audio.Media.DISPLAY_NAME,MediaStore.Audio.Media.ARTIST,MediaStore.Audio.Media.DATA},null,null,null);
        int count = c.getCount();

        String[] song=new String[count];
        paths=new String[count];
        artist_list= new String[count];
        int i=0;
        if(c.moveToFirst())
        {
            do {


                song[i] = c.getString(0);
                artist_list[i] = c.getString(1);
                paths[i] = c.getString(2);
                i++;
            }while(c.moveToNext());
        }

        c.close();
        return song;



    }

    private void playSong(String path) throws IllegalArgumentException,
            IllegalStateException, IOException {

        Log.d("ringtone", "playSong :: " + path);

        player.reset();
        player.setDataSource(path);

        player.prepare();
        player.start();


    }


}
