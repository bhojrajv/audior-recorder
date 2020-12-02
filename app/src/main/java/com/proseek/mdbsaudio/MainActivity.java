package com.proseek.mdbsaudio;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest.permission;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener
{
    ArrayList<Integer>carouselimg;
    //private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    BottomNavigationView bottomNavigationView;
    private Button uploadbutton;
  private  static final int Recording_code=200;
 private MediaRecorder mediaRecorder;
  private MediaPlayer mediaPlayer;
  private Chronometer chronometer;
  private SeekBar seekBar;
  File root=null;
  String filenm;
  private ImageButton recordbtn,stopbtn,pausebtn,resumbtn,playbtn,nextbtn,prebtn;
    private boolean Recording;
    private int lastProgress;
    private String fileName2;
    private long offset;
    private boolean isplaying;
    Toolbar toolbar;
    private ProgressDialog dialog;
    private Toast messageText;
    private String upLoadServerUri=null;
    private int serverResponseCode = 0;
    TextView recordingNm;
    private int currentIndex=0;
    CarouselView carouselView;
    // Requesting permission to RECORD_AUDIO
      private BroadcastReceiver Myrceiver=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //chronometer.setBase(SystemClock.elapsedRealtime());
        initializeView();

       // playbtn.setOnClickListener(this);
         //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
         //getSupportActionBar().setTitle("MdbsRecorder");
        upLoadServerUri = "http://md-bs.com/upload_to_server.php";
        dialog=new ProgressDialog(MainActivity.this);
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
            {
                getPermission();
            }
            else {
                getPermission2();
            }


        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.Hm:
                        Intent home=new Intent(MainActivity.this,MainActivity.class);
                        startActivity(home);
                        return true;
                    case R.id.testmonial:
                        Intent test=new Intent(MainActivity.this,TestMonial.class);
                        test.putExtra("Urltest","https://mymdbs.com/#testimonial");
                        startActivity(test);
                        return true;
                    case R.id.about:
                        Intent about=new Intent(MainActivity.this,TestMonial.class);
                         about.putExtra("Urltest","https://mymdbs.com/#aboutus");
                        startActivity(about);
                        return true;
                    case R.id.contact:
                        Intent cont=new Intent(MainActivity.this,TestMonial.class);
                        cont.putExtra("Urltest","https://mymdbs.com/#footerwrap");
                        startActivity(cont);
                        return true;
                    case R.id.user:
                        Intent prof=new Intent(MainActivity.this,Userprofile.class);
                        startActivity(prof);
                    default: return  false;
                }

            }
        });
    }

    private void getPermission2() {
        if(ContextCompat.checkSelfPermission(MainActivity.this,
                permission.RECORD_AUDIO)!= PackageManager.PERMISSION_GRANTED
                ||ContextCompat.checkSelfPermission(MainActivity.this,
                permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED
                ||ContextCompat.checkSelfPermission(MainActivity.this,
                permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED )
        {


               ActivityCompat.requestPermissions(MainActivity.this,
                        new String[] {permission.RECORD_AUDIO,permission.READ_EXTERNAL_STORAGE,
                                permission.WRITE_EXTERNAL_STORAGE},Recording_code  );



        }


    }

    private void initializeView() {
        carouselView=findViewById(R.id.coursel);
        bottomNavigationView=findViewById(R.id.bottomNav);
        recordbtn=findViewById(R.id.recordbtn);
        stopbtn=findViewById(R.id.stopBtn);
        pausebtn=findViewById(R.id.pauseBtn);
       resumbtn=findViewById(R.id.resumBtn);
       chronometer=findViewById(R.id.chrono);
       toolbar=findViewById(R.id.mainTool);
       nextbtn=findViewById(R.id.next);
       prebtn=findViewById(R.id.prev);
       recordingNm=findViewById(R.id.recordNm);
   //playbtn=findViewById(R.id.playbtn);
   uploadbutton=findViewById(R.id.uplaodbtn);
        setSupportActionBar(toolbar);
     uploadbutton.setOnClickListener(this);
     nextbtn.setOnClickListener(this);
     prebtn.setOnClickListener(this);
        recordbtn.setOnClickListener(this);
        pausebtn.setOnClickListener(this);
        resumbtn.setOnClickListener(this);
        stopbtn.setOnClickListener(this);
        Myrceiver=new Myceiver();
     fetchRecordings();
        brodCaseIntent();
   carouselimg=new ArrayList<>();
    carouselimg.add(R.drawable.carousel2);
    carouselimg.add(R.drawable.carousel1);
        carouselimg.add(R.drawable.carousel3);
        carouselimg.add(R.drawable.carousel4);
        carouselView.setPageCount(carouselimg.size());
        carouselView.setImageListener(new ImageListener() {
            @Override
            public void setImageForPosition(int position, ImageView imageView) {
                imageView.setImageResource(carouselimg.get(position));
            }
        });
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void getPermission() {
        if(ContextCompat.checkSelfPermission(MainActivity.this,
                permission.RECORD_AUDIO)!= PackageManager.PERMISSION_GRANTED
       ||ContextCompat.checkSelfPermission(MainActivity.this,
                permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED
       ||ContextCompat.checkSelfPermission(MainActivity.this,
                permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED )
        {


                requestPermissions(
                     new String[] {permission.RECORD_AUDIO,permission.READ_EXTERNAL_STORAGE,
                      permission.WRITE_EXTERNAL_STORAGE},Recording_code  );


        }

    }
    @Override
   public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
       super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==Recording_code)
        {
           if(grantResults.length==3 && grantResults[0]==PackageManager.PERMISSION_GRANTED
            &&grantResults[1]==PackageManager.PERMISSION_GRANTED
            && grantResults[2]==PackageManager.PERMISSION_GRANTED)
           {
                Toast.makeText(this, " permission is granted to existing app", Toast.LENGTH_SHORT).show();
            }
            else {
                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.JELLY_BEAN)
                finishAffinity();
                Toast.makeText(this, "Please give me access permission to existing app", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onClick(View v) {
        if(v==recordbtn)
        {
            try{
                prepareForRecord();
                startRecording();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

        }
        else if(v==stopbtn)
        {
            prepareForstop();
            stopRecording();
        }
        else if(v==pausebtn)
        {
            pauseRecording();
        }
        else if (v==resumbtn)
        {
            resumRecording();
        }

        else if(v==uploadbutton)
        {
            if (fileName2 != null) {
                if(Recording)
                {
                    Toast.makeText(this, "Please wait until Recording", Toast.LENGTH_SHORT).show();
                }
                else if(isplaying)
                {
                    Toast.makeText(this, "Please wait until Playing", Toast.LENGTH_SHORT).show();
                }
                else {
                    dialog = ProgressDialog.show(MainActivity.this, "",
                            "Uploading file...", true);
                    messageText.makeText(MainActivity.this,"uploading started.....",Toast.LENGTH_LONG);
                    new Thread(new Runnable() {
                        public void run() {
                            uploadFile(fileName2);
                        }
                    }).start();
                    // dialog.show();
                }

            }
            else {
                Toast.makeText(MainActivity.this, "Please start audio recording !!!",
                        Toast.LENGTH_LONG).show();
            }

        }
        else if(v==nextbtn)
        {
            int index = 0;
            File file = this.getExternalCacheDir();
            String path = file.getAbsolutePath() + "/Mdbs/Audios";
            File directory2 = new File(path);
            File[] recordLst = directory2.listFiles();
            if(recordLst.length<=0 || recordLst==null)
            {
                Toast.makeText(this, "Please start audio recording", Toast.LENGTH_SHORT).show();
            }
            else {
                if (currentIndex < (recordLst.length - 1)) {
                    currentIndex = currentIndex + 1;

                    recordLst[currentIndex].getAbsolutePath();
                    recordingNm.setText(recordLst[currentIndex].getName());
                    fileName2 = recordLst[currentIndex].getAbsolutePath();
                    //fileName = fileName2;
                    uploadFile(fileName2);
                    Toast.makeText(MainActivity.this, "Record Name" + recordLst[index].getName(), Toast.LENGTH_SHORT).show();

                }

                else {
                    currentIndex=currentIndex;
                    recordLst[currentIndex].getAbsolutePath();
                    recordingNm.setText(recordLst[currentIndex].getName());
                    fileName2=recordLst[currentIndex].getAbsolutePath();
                    //fileName=fileName2;
                    uploadFile(fileName2);

                }

            }

        }
        else if(v==prebtn)
        {
            File file=this.getExternalCacheDir();
            String path=file.getAbsolutePath()+"/Mdbs/Audios";
            File directory=new File(path);
            File[] list=directory.listFiles();
             if(list.length<=0|| list==null)
             {
                 Toast.makeText(this, "Please start audio recording", Toast.LENGTH_SHORT).show();
             }
             else {
                 if(currentIndex>0)
                 {
                     currentIndex=currentIndex-1;
                     //mPlayer=new MediaPlayer();
                     // mPlayer.reset();
                     recordingNm.setText(list[currentIndex].getName());
                     fileName2=list[currentIndex].getAbsolutePath();
                     // linearLayoutPlay.setVisibility(View.VISIBLE);
//                      imageViewPlay.setVisibility(View.VISIBLE);
//                      imageViewPlay.setImageResource(R.drawable.pause);
                     // imageViewPlay.setImageResource(R.drawable.pause);
                     //seekBar.setVisibility(View.VISIBLE);
                     // fileName=fileName2;
                     uploadFile(fileName2);

                 }
                 else {
                     // play last song
                     //prepFornextPrev();
                     currentIndex=list.length-1;
                     recordingNm.setText(list[currentIndex].getName());
                     fileName2=list[currentIndex].getAbsolutePath();
                     // uploadFile(fileName2);
//                    linearLayoutPlay.setVisibility(View.VISIBLE);
//                    imageViewPlay.setImageResource(R.drawable.pause);
                     //   fileName=fileName2;
//                    imageViewPlay.setVisibility(View.VISIBLE);
//                    imageViewPlay.setImageResource(R.drawable.pause);

                     uploadFile(fileName2);

                 }

             }

        }
    }

    private void brodCaseIntent() {
      registerReceiver(Myrceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @SuppressLint("LongLogTag")
    private int uploadFile(String fileName2) {

        String fileName = fileName2;

        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = new File(fileName2);

        if (!sourceFile.isFile()) {

            dialog.dismiss();

            // Log.e("uploadFile", "Source File not exist :" + fileName2);

            runOnUiThread(new Runnable() {
                public void run() {
                    messageText.makeText(MainActivity.this,"please select audio file..",Toast.LENGTH_LONG).show();
                }
            });

            return 0;

        } else {
            dialog.show();
            try {
                FileInputStream fileInputStream = new FileInputStream(
                        sourceFile);
                URL url = new URL(upLoadServerUri);
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type",
                        "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("uploaded_file", fileName);
                dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                        + fileName + "\"" + lineEnd);

                dos.writeBytes(lineEnd);

                // create a buffer of maximum size
                bytesAvailable = fileInputStream.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {

                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                }
                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Responses from the server (code and message)
                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();

//                Log.i("uploadFile", "HTTP Response is : "
//                        + serverResponseMessage + ": " + serverResponseCode);

                if (serverResponseCode == 200) {



                    runOnUiThread(new Runnable() {
                        public void run() {
                            String msg = "File Upload Completed.\n\n See uploaded :\n"
                                    + "in our server ";
                                    messageText.makeText(MainActivity.this,msg,Toast.LENGTH_LONG).show();
                            Toast.makeText(MainActivity.this,
                                    "File Upload Complete.", Toast.LENGTH_SHORT)
                                    .show();
                        }
                    });
                }

                // close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();

            } catch (MalformedURLException ex) {

                dialog.dismiss();
                ex.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(MainActivity.this,"MalformedURLException Exception : check script url.",Toast.LENGTH_LONG).show();
//                        Toast.makeText(MainActivity.this,
//                                "MalformedURLException", Toast.LENGTH_SHORT)
//                                .show();
                    }
                });

            } catch (Exception e) {

                dialog.dismiss();
                e.printStackTrace();

//                Toast.makeText(MainActivity.this,
//                        "Got Exception : see logcat ",
//                        Toast.LENGTH_SHORT).show();
                runOnUiThread(new Runnable() {
                    public void run() {
                      // messageText.setText("Got Exception : see logcat ");
                       Toast.makeText(MainActivity.this,
                                "Got Exception : see logcat server is not responding ",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
            dialog.dismiss();
            return serverResponseCode;
        }
    }

    private void resumRecording() {
        if(!Recording)
        {
            Recording=true;
            chronometer.setBase(SystemClock.elapsedRealtime()-offset);
            chronometer.start();
            offset=SystemClock.elapsedRealtime()-chronometer.getBase();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                mediaRecorder.resume();

            }
            else if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.M)
            {
                Recording=true;
                chronometer.setBase(SystemClock.elapsedRealtime()-offset);
                chronometer.start();
                offset=SystemClock.elapsedRealtime()-chronometer.getBase();
                mediaRecorder = new MediaRecorder();
                mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                /**In the lines below, we create a directory named VoiceRecorderSimplifiedCoding/Audios in the phone storage
                 * and the audios are being stored in the Audios folder **/

                root = this.getExternalCacheDir();
                File file = new File(root.getAbsolutePath() + "/Mdbs/Audios");
                if (!file.exists()) {
                    file.mkdirs();
                }

                fileName2 = root.getAbsolutePath() + "/Mdbs/Audios/" + System.currentTimeMillis() + ".m4a";
                mediaRecorder.setOutputFile(fileName2);
                //  mRecorder.setMaxDuration(10000);
                mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);


                try {
                    mediaRecorder.prepare();
                    mediaRecorder.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }


         stopbtn.setVisibility(View.VISIBLE);
        Toast.makeText(this, "audio recording is resume", Toast.LENGTH_SHORT).show();
        // mRecorder.start()
    }

    private void pauseRecording() {
        if (Recording)
        {
            Recording=false;
            chronometer.stop();
            offset=SystemClock.elapsedRealtime()-chronometer.getBase();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                mediaRecorder.pause();
            }
            else if(Build.VERSION.SDK_INT<=Build.VERSION_CODES.M)
            {
                Recording=false;
                chronometer.stop();
                offset=SystemClock.elapsedRealtime()-chronometer.getBase();
               // mediaRecorder=new MediaRecorder();
//               mediaRecorder.stop();
//               // mediaRecorder.reset();
//                mediaRecorder.release();
//                mediaRecorder=null;
                mediaRecorder.stop();
                mediaRecorder.release();
                mediaRecorder=null;
            }

        }
        stopbtn.setVisibility(View.GONE);
        Toast.makeText(this, " Recording is pause", Toast.LENGTH_SHORT).show();
    }

    private void stopRecording() {
        if(Recording)
        {
            try {
                chronometer.stop();
                offset=SystemClock.elapsedRealtime()-chronometer.getBase();
                Recording=false;
                mediaRecorder.stop();
                mediaRecorder.release();
                mediaRecorder=null;
            } catch (Exception e) {
                e.printStackTrace();
            }
            Toast.makeText(this, "Recording saved successfully.", Toast.LENGTH_SHORT).show();
        }
        Intent intent=new Intent(MainActivity.this,RecordlistActivity.class);
        startActivity(intent);
    }

    private void prepareForstop() {
        recordbtn.setVisibility(View.VISIBLE);
        resumbtn.setVisibility(View.GONE);
        pausebtn.setVisibility(View.GONE);
        stopbtn.setVisibility(View.GONE);
    }

    private void startRecording() {

            if (!Recording) {
                Recording = true;
                mediaRecorder = new MediaRecorder();
                mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                /**In the lines below, we create a directory named VoiceRecorderSimplifiedCoding/Audios in the phone storage
                 * and the audios are being stored in the Audios folder **/

                root = this.getExternalCacheDir();
                File file = new File(root.getAbsolutePath() + "/Mdbs/Audios");
                if (!file.exists()) {
                    file.mkdirs();
                }

                fileName2 = root.getAbsolutePath() + "/Mdbs/Audios/" + System.currentTimeMillis() + ".m4a";
                mediaRecorder.setOutputFile(fileName2);
                //  mRecorder.setMaxDuration(10000);
                mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);


                try {
                    mediaRecorder.prepare();
                    mediaRecorder.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                lastProgress = 0;
                stopPlaying();
                chronometer.setBase(SystemClock.elapsedRealtime());
                chronometer.start();



        }

    }

    private void stopPlaying() {

    }

    private void prepareForRecord() {
        recordbtn.setVisibility(View.GONE);
        resumbtn.setVisibility(View.VISIBLE);
        pausebtn.setVisibility(View.VISIBLE);
        stopbtn.setVisibility(View.VISIBLE);
        //resumbtn.setVisibility(View.VISIBLE);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_list,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.menuItem:
                Intent intent= new Intent(MainActivity.this,RecordlistActivity.class);
                startActivity(intent);
                break;
                default:
                    return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    private void fetchRecordings() {

        File root = this.getExternalCacheDir();
        String path = root.getAbsolutePath() + "/Mbds/Audios";
        File directory = new File(path);
        File[] files = directory.listFiles();
        if (files != null) {

            for (int i = 0; i < files.length; i++) {


                String fileName = files[i].getName();
                String recordingUri = root.getAbsolutePath() + "/Mdbs/Audios/" + fileName;
                fileName2=fileName;
                //Recording recording = new Recording(recordingUri, fileName, false);
                //recordingArraylist.add(recording);

            }
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
        if(mediaRecorder!=null)
        {
//            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder=null;
        }
        else if(mediaPlayer!=null)
        {
            mediaPlayer.release();
            mediaPlayer=null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
       // unregisterReceiver(Myrceiver);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent=new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.finish();
        startActivity(intent);
    }




   public String getConnectivityStringStatus(MainActivity mainActivity){
       String status=null;
       ConnectivityManager connectivityManager= (ConnectivityManager) mainActivity.getSystemService(MainActivity.CONNECTIVITY_SERVICE);
        NetworkInfo activnetwrk=connectivityManager.getActiveNetworkInfo();
       if(activnetwrk!=null)
       {
           if(activnetwrk.getType()==ConnectivityManager.TYPE_WIFI)
           {
               status="Wifi is enabled";
               Toast.makeText(mainActivity, status, Toast.LENGTH_SHORT).show();
               return status;
           }
           else if(activnetwrk.getType()==ConnectivityManager.TYPE_MOBILE)
           {
               status="Mobile data is enabled";
               Toast.makeText(mainActivity, status, Toast.LENGTH_SHORT).show();
               return status;
           }
       }
       else
       {
           status="no internet connection please check";
           Toast.makeText(mainActivity, status, Toast.LENGTH_SHORT).show();
          }
       return  status;
}
    }
