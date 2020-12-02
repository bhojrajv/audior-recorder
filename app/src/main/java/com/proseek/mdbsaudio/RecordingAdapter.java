package com.proseek.mdbsaudio;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class RecordingAdapter extends RecyclerView.Adapter<RecordingAdapter.MyViewholder>
 {
     private final Handler mHandler;
     private final Thread mUiThread;
     private String upLoadServerUri;
    private int  position2;
    private String flNm=null;
   private ClickonItem clickonItem;
   private Renamefile renamefile;
   private Context context;
   private ArrayList<Recording> arrayList;
     EditText editText;
     private int serverResponseCode=0;
     private ProgressDialog dialog;
     private Toast messageText;
     File[]files;
     public RecordingAdapter(RecordlistActivity recordlistActivity, ArrayList<Recording> arrayList,File[]files, ClickonItem clickonItem,
                             RecordlistActivity renamefile) {
        context=recordlistActivity;
        this.arrayList=arrayList;
        this.clickonItem=clickonItem;
        this.renamefile=renamefile;
        this.files=files;

         mHandler = new Handler();
         mUiThread = new Thread();
     }

    @NonNull
    @Override
    public MyViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view=layoutInflater.inflate(R.layout.list_item,parent,false);
        upLoadServerUri = "http://md-bs.com/upload_to_server.php";
        dialog=new ProgressDialog(context);
        return new MyViewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewholder holder, final int position) {
    holder.recrNm.setText(arrayList.get(position).getFileName());
    holder.chronometer.setText(arrayList.get(position).getRemaintime());
    holder.datetm.setText(Timeago.getTimeago(files[position].lastModified()));
    holder.delete.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            removeItem(position);
        }
    });
     holder.popMenu.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             PopupMenu popupMenu=new PopupMenu(context,v);
             MenuInflater menuItem= popupMenu.getMenuInflater();
             menuItem.inflate(R.menu.popu_menu,popupMenu.getMenu());
             popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                 @Override
                 public boolean onMenuItemClick(MenuItem item) {
                     switch (item.getItemId())
                     {
                         case R.id.upload:
                             //upload code
                             dialog = ProgressDialog.show(context, "",
                                     "Uploading file...", true);
                             messageText.makeText(context,"uploading started.....",Toast.LENGTH_LONG);
                             new Thread(new Runnable() {
                                 public void run() {
                                     uploadFile(arrayList.get(position).getUri());
                                 }
                             }).start();

                             return true;
                         case R.id.share:
                             //sharecode
                             Toast.makeText(context, "Hello", Toast.LENGTH_SHORT).show();
                             shareitem(position2);
                             return  true;
                         case R.id.reNm:
                             renamefile.onclickItem(position);
                             notifyDataSetChanged();
                             TextView textView=new TextView(context);
                             return true;
                         default:
                             return true;
                     }
                 }
             });
             popupMenu.show();
         }
     });
        position2=position;
    }

    private void shareitem(int position) {
        File path=context.getExternalCacheDir();
        String fnuri=path.getAbsolutePath()+"/Mdbs/Audios/";
        File file=new File(fnuri);
        File[]dr=file.listFiles();
        Uri uri=Uri.parse(dr[position].getAbsolutePath());
        Intent intent=new Intent(Intent.ACTION_SEND);
        intent.setType("audio/*");
        intent.putExtra(Intent.EXTRA_STREAM,uri);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            context.startActivity(Intent.createChooser(intent,"Share Sound file"));
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }


    public class MyViewholder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imgplay;
        ImageButton popMenu;
        Chronometer chronometer;
        TextView recrNm,datetm;
        ImageButton delete;
        public MyViewholder(@NonNull final View itemView) {
            super(itemView);
            imgplay=itemView.findViewById(R.id.imageViewPlay);
            chronometer=itemView.findViewById(R.id.chrono2);
            recrNm=itemView.findViewById(R.id.textViewRecordingname);
            delete=itemView.findViewById(R.id.deleteItem);
            popMenu=itemView.findViewById(R.id.popmenu);
            datetm=itemView.findViewById(R.id.dataTime);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {

                clickonItem.itemCliclListner(arrayList.get(getAdapterPosition()),getAdapterPosition());


        }

    }

    private void removeItem(int adapterPosition) {
         arrayList.remove(adapterPosition);
        notifyItemRemoved(adapterPosition);
      notifyItemRangeChanged(adapterPosition,arrayList.size());
      File root= context.getExternalCacheDir();
       String path=root.getAbsolutePath()+"/Mdbs/Audios/";
       File file2=new File(path);
       File[]dr=file2.listFiles();
       String flNm=dr[adapterPosition].getName();
       String uri=  root.getAbsolutePath()+"/Mdbs/Audios/"+flNm;
        File file=new File(uri);
        file.delete();

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

             new runOnUithread(new Runnable() {
                 @Override
                 public void run() {
                     messageText.makeText(context,"please select audio file..",Toast.LENGTH_LONG).show();
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

//                 Log.i("uploadFile", "HTTP Response is : "
//                         + serverResponseMessage + ": " + serverResponseCode);

                 if (serverResponseCode == 200) {
                     new runOnUithread(new Runnable() {
                         @Override
                         public void run() {
//                             String msg = "File Upload Completed.\n\n See uploaded :\n"
//                                     + "in our server ";
//                             messageText.makeText(context,msg,Toast.LENGTH_LONG).show();
                             Toast.makeText(context, "File Uploading Completed", Toast.LENGTH_SHORT).show();
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

                         Toast.makeText(context,"MalformedURLException Exception : check script url.",Toast.LENGTH_LONG).show();
//                        Toast.makeText(MainActivity.this,
//                                "MalformedURLException", Toast.LENGTH_SHORT)
//                                .show();


             } catch (Exception e) {

                 dialog.dismiss();
                 e.printStackTrace();

//                runOnUiThread(new Runnable() {
//                    public void run() {
//                        messageText.setText("Got Exception : see logcat ");
//                        Toast.makeText(MainActivity.this,
//                                "Got Exception : see logcat ",
//                                Toast.LENGTH_SHORT).show();
//                    }
//                });
                 Log.e("Upload file to server Exception",
                         "Exception : " + e.getMessage(), e);
             }
             dialog.dismiss();
             return serverResponseCode;
         }
     }

     private class runOnUithread {

         public runOnUithread(Runnable runnable) {
             if (Thread.currentThread() != mUiThread) {
                 mHandler.post(runnable);
             } else {
                 runnable.run();
             }
         }
     }
 }
interface ClickonItem {
    void itemCliclListner(Recording recording,int position);
}
interface Renamefile{
    void onclickItem(int postion);
}
