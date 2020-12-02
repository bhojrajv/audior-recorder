package com.proseek.mdbsaudio;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

@SuppressWarnings("unchecked")
public class RecordlistActivity extends AppCompatActivity implements View.OnClickListener,ClickonItem,
Renamefile{
    private String startduration;
    private String totalduration;
    Toolbar toolbar;
    private int postion=0;
    protected ImageButton prevbtn,nextbtn,playbtn;
    private SeekBar seekBar;
    private Chronometer startTime,endtime;
  private RecyclerView recyclerView;
  private ArrayList<Recording>arrayList;
 public RecordingAdapter recordingAdapter;
  private  MediaPlayer mediaPlayer;
    private String recordNm;
    private String elapsetimer;
    private String remaintimer;
    private boolean isPlaying;
    private long offset;
    private int lastposition;
  Handler handler=new Handler();
  File[]files2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recordlist);
        recyclerView=findViewById(R.id.recordRec);
        LinearLayoutManager  linearLayoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        arrayList=new ArrayList();
        fetchRecordings();
        initailize();
        mediaPlayer=new MediaPlayer();

    }

    private void initailize() {
        prevbtn=findViewById(R.id.previous);
        nextbtn=findViewById(R.id.btnnext);
        playbtn=findViewById(R.id.playPause);
        seekBar=findViewById(R.id.bottomseek);
        startTime=findViewById(R.id.startTm);
        endtime=findViewById(R.id.endTm);
         toolbar=findViewById(R.id.mdbstool);
        nextbtn.setOnClickListener(this);
        prevbtn.setOnClickListener(this);
        playbtn.setOnClickListener(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(RecordlistActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private void fetchRecordings() {

        File root = this.getExternalCacheDir();

        String path = root.getAbsolutePath() + "/Mdbs/Audios";
        File directory = new File(path);
        File[] files = directory.listFiles();
        if( files!=null ){

            for (int i = 0; i < files.length; i++) {


                String fileName = files[i].getName();
                recordNm=files[0].getAbsolutePath();

                String recordingUri = root.getAbsolutePath() + "/Mdbs/Audios/" + fileName;
                try {
                    mediaPlayer=new MediaPlayer();
                    mediaPlayer.setDataSource(recordingUri);
                    mediaPlayer.prepare();
                    elapsetimer=showTimer(mediaPlayer.getCurrentPosition());
                    remaintimer=showTimer(mediaPlayer.getDuration());

                } catch (IOException e) {
                    e.printStackTrace();
                }

                Recording recording = new Recording(recordingUri,fileName,false,elapsetimer,remaintimer);
                arrayList.add(recording);
                //recordingAdapter.notifyDataSetChanged();
               // recordingAdapter.notifyDataSetChanged();
                files2=directory.listFiles();
            }

//            textViewNoRecordings.setVisibility(View.GONE);
//            recyclerViewRecordings.setVisibility(View.VISIBLE);
            setAdaptertoRecyclerView();
            recordingAdapter.notifyDataSetChanged();

//        }else{
//            textViewNoRecordings.setVisibility(View.VISIBLE);
//            recyclerViewRecordings.setVisibility(View.GONE);
//        }

    }
}

    private String showTimer(long duration) {
            int hours,min,sec;
            String finaltime="" ;
            String secondtimer="";
            hours=(int)(duration/(1000*60*60));
            min=(int)(duration%(1000*60*60)/(1000*60));
            sec=(int)(duration%(1000*60*60)%(1000*60)/(1000));
            if(hours>0)
            {
                finaltime=hours+":";
            }
            if (sec<10)
            {
                secondtimer="0"+sec;
            }
            else {
                secondtimer=""+sec;
            }
            finaltime=finaltime+min+":"+secondtimer;
            return finaltime;

    }

    private void setAdaptertoRecyclerView() {
        recordingAdapter=new RecordingAdapter(RecordlistActivity.this,arrayList,files2,this,this);
        recordingAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(recordingAdapter);

    }

    @Override
    public void onClick(View v) {
        if(v==nextbtn)
        {
            File root=this.getExternalCacheDir();
            String path=root. getAbsolutePath()+"/Mdbs/Audios";
            File file=new File(path);
            File[]files=file.listFiles();
            if (files.length<=0)
            {
                Toast.makeText(this, "Please start audio recording", Toast.LENGTH_SHORT).show();
            }
            else {
                playbtn.setImageResource(R.drawable.whitepause);
                startTime.setBase(SystemClock.elapsedRealtime());
                startTime.start();
                if(postion<(files.length-1))
                {
                    postion=postion+1;
                    String filepath= files[postion].getAbsolutePath();
                    recordNm=filepath;
                    String name=files[postion].getName();
                    // mediaPlayer=new MediaPlayer();
                    mediaPlayer.reset();
                    if (!isPlaying || mediaPlayer!=null)
                    {
                        isPlaying=true;
                        mediaPlayer=MediaPlayer.create(RecordlistActivity.this, Uri.parse(files[postion].getAbsolutePath()));
                        // mPlayer.prepare();
                        mediaPlayer.start();
                        nextprev(mediaPlayer);
                        mediaPlayer.seekTo(  mediaPlayer.getCurrentPosition());
                        seekBar.setProgress(  mediaPlayer.getCurrentPosition());
                        seekBar.setMax(  mediaPlayer.getDuration());
                        //seeknextprev(mPlayer);
                        totalduration=showTimer(  mediaPlayer.getDuration());
                        endtime.setText(totalduration);
//
                        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mediaPlayer) {
                                isPlaying=false;
                                startTime.setBase(SystemClock.elapsedRealtime());
                                startTime.stop();
                                startTime.setText(totalduration);
                                playbtn.setImageResource(R.drawable.whiteplay);
                            }
                        });
                    }

                    else  {
                        isPlaying=false;

                    }

                    int index=0;
                    Toast.makeText(RecordlistActivity.this, "Record Name"+files[index].getName(), Toast.LENGTH_SHORT).show();

//
                }
                else {
                    postion=0;
//                           try {
                    if(!isPlaying || mediaPlayer!=null)
                    {isPlaying=true;
                        mediaPlayer=MediaPlayer.create(RecordlistActivity.this,Uri.parse(files[postion].getAbsolutePath()));
                        // mPlayer.prepare();
                        mediaPlayer.start();
                        nextprev(mediaPlayer);
                        mediaPlayer.seekTo(mediaPlayer.getCurrentPosition());
                        seekBar.setProgress(mediaPlayer.getCurrentPosition());

                        seekBar.setMax(mediaPlayer.getDuration());
                        totalduration=showTimer(mediaPlayer.getDuration());
                        endtime.setText(totalduration);
//
                        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mediaPlayer) {
                                isPlaying=false;
                                startTime.setBase(SystemClock.elapsedRealtime());
                                startTime.stop();
                                startTime.setText(totalduration);

                                playbtn.setImageResource(R.drawable.whiteplay);


                            }
                        });
                    }
                    else {
                        isPlaying=false;
//
                    }


                }

            }
        }
        else  if(v==prevbtn)
        {

            File root = this.getExternalCacheDir();

            String path = root.getAbsolutePath() + "/Mdbs/Audios";
            File file = new File(path);
            File[] files = file.listFiles();
            if (files.length <=0) {
                Toast.makeText(this, "Please start audio recording", Toast.LENGTH_SHORT).show();
            }
            else {
                startTime.setBase(SystemClock.elapsedRealtime());
                startTime.start();
                playbtn.setImageResource(R.drawable.whitepause);
                if (postion > 0) {
                    postion = postion - 1;
                    String filepath = files[postion].getAbsolutePath();
                    recordNm = filepath;
                    String name = files[postion].getName();
                    // mediaPlayer = new MediaPlayer();
                    mediaPlayer.reset();
                    if (!isPlaying || mediaPlayer != null) {
                        isPlaying = true;
                        mediaPlayer = MediaPlayer.create(RecordlistActivity.this, Uri.parse(files[postion].getAbsolutePath()));
                        // mPlayer.prepare();
                        mediaPlayer.start();
                        nextprev(mediaPlayer);
                        mediaPlayer.seekTo(mediaPlayer.getCurrentPosition());
                        seekBar.setProgress(mediaPlayer.getCurrentPosition());
                        seekBar.setMax(mediaPlayer.getDuration());
                        //seeknextprev(mPlayer);
                        totalduration = showTimer(mediaPlayer.getDuration());
                        endtime.setText(totalduration);
//                                     chronometer.start();
//                                    offes2=SystemClock.elapsedRealtime()-chronometer.getBase();
//                                         chronometer2.start();
//                                         offes2=SystemClock.elapsedRealtime()-chronometer.getBase();
                        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mediaPlayer) {
                                isPlaying = false;
                                startTime.setBase(SystemClock.elapsedRealtime());
                                startTime.stop();
                                startTime.setText(totalduration);
                                playbtn.setImageResource(R.drawable.whiteplay);


                            }
                        });
                    } else {
                        isPlaying= false;
                    }
                    Toast.makeText(this, "File:" + name, Toast.LENGTH_SHORT).show();
                } else {
                    postion = files.length - 1;
                    String filepath = files[postion].getAbsolutePath();
                    recordNm = filepath;
                    String name = files[postion].getName();
                    if (!isPlaying || mediaPlayer != null) {
                        isPlaying= true;
                        mediaPlayer = MediaPlayer.create(RecordlistActivity.this, Uri.parse(files[postion].getAbsolutePath()));
                        // mPlayer.prepare();
                        mediaPlayer.start();
                        nextprev(mediaPlayer);
                        mediaPlayer.seekTo(mediaPlayer.getCurrentPosition());
                        seekBar.setProgress(mediaPlayer.getCurrentPosition());
//                                  chronometer.start();
//                                  offes2=SystemClock.elapsedRealtime()-chronometer.getBase();
                        //seeknextprev(mPlayer);
                        seekBar.setMax(mediaPlayer.getDuration());
                        totalduration = showTimer(mediaPlayer.getDuration());
                        endtime.setText(totalduration);
//                                         chronometer2.start();
//                                         offes2=SystemClock.elapsedRealtime()-chronometer.getBase();
                        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mediaPlayer) {
                                isPlaying = false;
                                startTime.setBase(SystemClock.elapsedRealtime());
                                startTime.stop();
                                startTime.setText(totalduration);

                                playbtn.setImageResource(R.drawable.whiteplay);
                            }
                        });
                    } else {
                        isPlaying = false;
                    }
                }
            }
        }
        else if(v==playbtn)
        {
            if(!isPlaying)
            {
                isPlaying=true;

                 if(recordNm!=null)
                 {
                     startTime.setBase(SystemClock.elapsedRealtime()-offset);
                     startTime.start();
                     startPlaying(recordNm);
                 }
                 else {
                     Toast.makeText(this, "Please first record audio....", Toast.LENGTH_SHORT).show();
                 }

            }
            else {
                isPlaying=false;
                offset=SystemClock.elapsedRealtime()-startTime.getBase();
                startTime.stop();
                stopPlaying();
            }
        }
    }

    private void stopPlaying() {
        mediaPlayer.stop();
//      mediaPlayer.release();
//      mediaPlayer=null;
        playbtn.setImageResource(R.drawable.whiteplay);
    }

    private void startPlaying(String recordNm2) {
        recordNm=recordNm2;
        playbtn.setImageResource(R.drawable.whitepause);
        playbtn.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        mediaPlayer=new MediaPlayer();
        try {
            mediaPlayer.setDataSource(recordNm);
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //             mPlayer.setDataSource(name);
        // mPlayer.prepare();
        mediaPlayer.start();
        mediaPlayer.seekTo(lastposition);
        seekBar.setProgress(lastposition);
        seekBar.setMax(mediaPlayer.getDuration());
        updateSeekbar();
        endtime.setText(showTimer(mediaPlayer.getDuration()));
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                isPlaying=false;
                //  mPlayer.stop();
                startTime.setBase(SystemClock.elapsedRealtime());
                startTime.stop();
                if (mediaPlayer!=null)
                {
                    startTime.setText(showTimer(mediaPlayer.getDuration()));
                }
                playbtn.setImageResource(R.drawable.whiteplay);
            }
        });
    }

    private void nextprev(MediaPlayer mediaPlayer2) {
        mediaPlayer=mediaPlayer2;
        playbtn.setImageResource(R.drawable.whitepause);
        updateSeekbar();
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean userFrom) {
                if(mediaPlayer!=null && userFrom)
                {
                    mediaPlayer.seekTo(progress);

                }
                startTime.setBase(SystemClock.elapsedRealtime()-mediaPlayer.getCurrentPosition());
                lastposition=progress;

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private  void updateSeekbar()
    {
        if(mediaPlayer!=null)
        {
            int currentpostion=mediaPlayer.getCurrentPosition();
            seekBar.setProgress(currentpostion);
            // mediaPlayer.seekTo(currentpostion);
            seekBar.setMax(mediaPlayer.getDuration());
            lastposition=currentpostion;
        }
        handler.postDelayed(runnable,100);
    }
    Runnable runnable=new Runnable() {
        @Override
        public void run() {
            updateSeekbar();
        }
    };

    @Override
    public void itemCliclListner(Recording recording, int position) {
        recordNm=recording.getUri();
        if(!isPlaying)
        {
            isPlaying=true;
            playbtn.setImageResource(R.drawable.whitepause);
//            mediaPlayer=MediaPlayer.create(RecordingListActivity.this, Uri.parse(recordNm));
            mediaPlayer=new MediaPlayer();
            mediaPlayer.reset();
            try {
                mediaPlayer.setDataSource(recordNm);
                mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mediaPlayer.start();
            mediaPlayer.seekTo(mediaPlayer.getCurrentPosition());
            seekBar.setProgress(mediaPlayer.getCurrentPosition());
            seekBar.setMax(mediaPlayer.getDuration());
            nextprev(mediaPlayer);
            endtime.setText(showTimer(mediaPlayer.getDuration()));
            startTime.setText(showTimer(mediaPlayer.getCurrentPosition()));
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    isPlaying=false;
                    //  mPlayer.stop();
                    startTime.setBase(SystemClock.elapsedRealtime());
                    startTime.stop();
                    if (mediaPlayer!=null)
                    {
                        startTime.setText(showTimer(mediaPlayer.getDuration()));
                    }
                    playbtn.setImageResource(R.drawable.whiteplay);
                }
            });
        }
        else {
            isPlaying=false;
            offset=SystemClock.elapsedRealtime()-startTime.getBase();
            startTime.stop();
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer=null;
            playbtn.setImageResource(R.drawable.whiteplay);
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        if(mediaPlayer!=null)
        {
            mediaPlayer.release();
            mediaPlayer=null;
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onclickItem(int postion) {
        final EditText editText=new EditText(RecordlistActivity.this);
        LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        editText.setLayoutParams(layoutParams);

        // editText.setHint("Edit File Name");
        File path=this.getExternalCacheDir();
        final String filnm=path.getAbsolutePath()+"/Mdbs/Audios/";
        final File file=new File(filnm);
        File[]files=file.listFiles();
        if(files!=null)
        {
            String flNm = files[postion].getName();
            AlertDialog.Builder builder= new AlertDialog.Builder(this);
            builder.setTitle("Rename the file Name");
            builder.setView(editText);
            //editText.setInputType(InputType.TYPE_CLASS_TEXT);
            editText.setText(arrayList.get(postion).getFileName());
            //notifyDataSetChanged();
            builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String newNm=editText.getText().toString().trim();
                    File from=new File(filnm,arrayList.get(postion).getFileName());
                    File to=new File(filnm,newNm+".m4a");
                    if(from.exists())
                    {
                        from.renameTo(to);
                        recordingAdapter.notifyDataSetChanged();
                        recordingAdapter.notifyItemInserted(postion);
                       // recordingAdapter.notifyItemInserted(postion);
                        //recordingAdapter.notifyItemRangeInserted(postion,arrayList.size());
                    }
                    else {
                        Toast.makeText(RecordlistActivity.this, "something went wrong", Toast.LENGTH_SHORT).show();
                    }

                }
            });
            builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.show();
        }
    }
}

