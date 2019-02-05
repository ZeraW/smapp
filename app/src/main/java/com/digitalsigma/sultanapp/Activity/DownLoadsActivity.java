package com.digitalsigma.sultanapp.Activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.digitalsigma.sultanapp.Adapter.DownLoadRecyAdapter;
import com.digitalsigma.sultanapp.Other.Constant;
import com.digitalsigma.sultanapp.R;
import com.digitalsigma.sultanapp.Service.MusicServiceSemsm;
import com.digitalsigma.sultanapp.Service.myPlayService;

import net.alhazmy13.catcho.library.Catcho;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Created by AhmedAbouElFadle on 12/4/2016.
 */
public class DownLoadsActivity extends AppCompatActivity {


    //service
    private MusicServiceSemsm musicSrv;
    private Intent playIntent;
    //binding
    private boolean musicBound=false;
    RingtoneManager mgr;
    // --Seekbar variables --
    private int seekMax;
    private int cPosItion;
    private static int songEnded = 0;
    boolean mBroadcastIsRegistered;

int delete=0;



    // Set up the notification ID
    private static final int NOTIFICATION_ID = 1;
    private boolean isPausedInCall = false;
    private PhoneStateListener phoneStateListener;
    private TelephonyManager telephonyManager;


    // --Set up constant ID for broadcast of seekbar position--
    public static final String BROADCAST_SEEKBAR = "com.smsemshehab.gmsproduction.sendseekbar";
    Intent intent;


    public static final String BROADCAST_Text = "com.smsemshehab.gmsproductiontextView";


    private Paint p = new Paint();

    private View view;

    private MediaPlayer player;
    Button btn_play;
    Button btn_next;
    Button btn_back;
    Button btn_CallTone;
    Button btn_Fav;

    TextView startTimeTxt;
    TextView songNametxt;
    TextView finalTimeTxt;
    private AlertDialog.Builder alertDialog;
    private double startTime = 0;
    private double finalTime = 0;

    private Handler myHandler = new Handler();

    Runnable runnable , playRunnabel;

    int firstPointer=0;
    int pointer=0;


        SeekBar seekBar;


    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    ArrayList<String> downloadedSongUrl=new ArrayList<String>();
    ArrayList<String> downloadedSongName=new ArrayList<String>();

    String filePath;

    Typeface t1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Catcho.Builder(this)
                .activity(ContactActivity.class)
                .build();

        setContentView(R.layout.activity_downloads);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("التحميلات");


        inti();
        setLisner();

        t1= Typeface.createFromAsset(getAssets(),"hacen.ttf");

        songNametxt.setTypeface(t1);
        startTimeTxt.setTypeface(t1);
        finalTimeTxt.setTypeface(t1);

/*

        if (MusicServiceSemsm.player.isPlaying() )
        {
            musicSrv.pausePlayer();
        }
*/


       // player = new MediaPlayer();
       // initMusicPlayer();

      //  if (MusicServiceSemsm.player != null) {
/*
            if (musicSrv.isPlayingg()) {
                btn_play.setEnabled(true);
                btn_back.setEnabled(true);
                btn_next.setEnabled(true);
                seekBar.setEnabled(true);
                btn_CallTone.setEnabled(true);
                btn_Fav.setEnabled(false);
            } else {

                btn_play.setEnabled(false);
                btn_back.setEnabled(false);
                btn_next.setEnabled(false);
                btn_Fav.setEnabled(false);

            }*/
      //  }

         initDialog();


        intent = new Intent(BROADCAST_SEEKBAR);


        registerReceiver(broadcastReceiver, new IntentFilter(
                MusicServiceSemsm.BROADCAST_ACTION));
        mBroadcastIsRegistered = true;

        /*if (MusicServiceSemsm.player!= null &&MusicServiceSemsm.player.isPlaying())
        {
            btn_play.setBackground(getResources().getDrawable(R.drawable.stop_button_light_new));

        }*/

       /* btn_Fav.setVisibility(View.GONE);
        btn_CallTone.setVisibility(View.GONE);*/



        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    int seekPos = seekBar.getProgress();
                    intent.putExtra("seekpos", seekPos);
                    sendBroadcast(intent);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });





        // mRecyclerView = (RecyclerView) findViewById(R.id.MusicListDownloaded);
        mRecyclerView.setHasFixedSize(true);
        initSwipe();


        File SDCardRoot = Environment.getExternalStorageDirectory(); // location where you want to store
        File file = new File(Environment.getExternalStorageDirectory(), SDCardRoot+"/Sultan");
      //  Toast.makeText(DownLoadsActivity.this, ""+Environment.getExternalStorageDirectory()+SDCardRoot+"MusicPro", Toast.LENGTH_SHORT).show();


        if (file.exists()){
          //  Toast.makeText(DownLoadsActivity.this, "exist", Toast.LENGTH_SHORT).show();
            File[] files = file.listFiles();

            Log.d("Files", "Size: "+ files.length);
      //  Toast.makeText(DownLoadsActivity.this, ""+files[0], Toast.LENGTH_SHORT).show();
        for (int i = 0; i < files.length; i++)
        {

            String[] items = files[i].getName().split(".mp3");

            for (String item : items) {
                downloadedSongName.add(item);
                //  Toast.makeText(DownLoadsActivity.this, "" + item, Toast.LENGTH_SHORT).show();
            }


            downloadedSongUrl.add(files[i].toString());
         //   downloadedSongName.add(files[i].getName());
          //  Toast.makeText(DownLoadsActivity.this, "url lem"+downloadedSongName.get(0), Toast.LENGTH_SHORT).show();

        }

        }else {
            Toast.makeText(DownLoadsActivity.this, "Not Exist" +Environment.getExternalStorageDirectory()+"/Sultan" , Toast.LENGTH_SHORT).show();
        }

        if (downloadedSongName.size() > 0)

        {
            findViewById(R.id.txtError).setVisibility(View.GONE);

            mLayoutManager = new LinearLayoutManager(DownLoadsActivity.this);
            mRecyclerView.setLayoutManager(mLayoutManager);

            // specify an adapter (see also next example)
            mAdapter = new DownLoadRecyAdapter(downloadedSongName, downloadedSongUrl, DownLoadsActivity.this);
            mRecyclerView.setAdapter(mAdapter);
        }else {
            findViewById(R.id.txtError).setVisibility(View.VISIBLE);
        }



        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListenerDS(DownLoadsActivity.this
                , mRecyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {

                StartAnimations();
                Constant.pointer=2;

                btn_play.setEnabled(true);
                btn_back.setEnabled(true);
                btn_next.setEnabled(true);

                btn_play.setBackground(getResources().getDrawable(R.drawable.stop_button_light_new));

                Constant.playListName=downloadedSongName;
                Constant.playListUrl=downloadedSongUrl;


           //     trackPlayingicon(position);

                Constant.pointer=1;
                Constant.postion=position;

                if (musicSrv.isPlayingg())
                {
                    musicSrv.pausePlayer();
                  //
                    // ms MusicServiceSemsm.player.pause();
                  //  musicSrv.playSong();
                   // playSong();
                    play();
                }

                else {

                  //  musicSrv.playSong();

                    play();
                    //playSong();
                }
              //  startActivity(new Intent(DownLoadsActivity.this,PlayerActivity.class));



            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));






   /*     btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (Constant.postion+1>=downloadedSongUrl.size())
                {

                    Constant.postion=0;
                   *//* player.pause();
                    playSong();*//*

                    musicSrv.pausePlayer();
                  //  MusicServiceSemsm.player.pause();
                  //  musicSrv.playSong();
play();


                }
                else {


                    Constant.postion++;
                    musicSrv.pausePlayer();
                 //
                    //   MusicServiceSemsm.player.pause();
                   // musicSrv.playSong();
play();

                }
            }
        });


        btn_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //  Toast.makeText(v.getContext(), "path "+songUrlList.get(position), Toast.LENGTH_SHORT).show();

                if (musicSrv.isPlayingg())
                {
                    btn_play.setBackground(getResources().getDrawable(R.drawable.play_button));
musicSrv.pausePlayer();
                  //  btn_play.setText("Play");
                   // MusicServiceSemsm.player.pause();
                   // musicSrv.playSong();
                    // Toast.makeText(AlbumContentActivity.this, "test", Toast.LENGTH_SHORT).show();



                }
                else {



                    btn_play.setBackground(getResources().getDrawable(R.drawable.stop_icon));

musicSrv.go();
                 //   btn_play.setText("Pause");
              //  MusicServiceSemsm.player.start();
                    //Toast.makeText(AlbumContentActivity.this, "test  pause", Toast.LENGTH_SHORT).show();


                }

            }
        });






        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                if (Constant.postion-1 < 0)
                {
                    Constant.postion=downloadedSongUrl.size()-1;
                    musicSrv.pausePlayer();
                  //  MusicServiceSemsm.player.pause();
                  //  musicSrv.playSong();
play();


                }
                else {


                    Constant.postion --;


                    musicSrv.pausePlayer();

                   // MusicServiceSemsm.player.pause();
                  //  musicSrv.playSong();
play();

                }



            }
        });*/


        intent = new Intent(BROADCAST_SEEKBAR);

        registerReceiver(broadcastReceiver, new IntentFilter(
                MusicServiceSemsm.BROADCAST_ACTION));
        mBroadcastIsRegistered = true;


        //  String path = Environment.getExternalStorageDirectory()+"/MusicPro";
//        Log.d("Files", "Path: " + path);
     /*   File directory = new File(path);
        File[] files = directory.listFiles();*/
    /*    Log.d("Files", "Size: "+ files.length);
        Toast.makeText(DownLoadsActivity.this, ""+files.length, Toast.LENGTH_SHORT).show();
        for (int i = 0; i < files.length; i++)
        {
            Log.d("Files", "FileName:" + files[i].getName());
        }*/


        btn_CallTone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  Toast.makeText(DownLoadsActivity.this, "jsdksaj", Toast.LENGTH_SHORT).show();

                settingPermission();
                 mgr = new RingtoneManager(DownLoadsActivity.this);
                mgr.setType(RingtoneManager.TYPE_RINGTONE);

                if (Constant.playListName.size()>0) {

                    alertDialog = new AlertDialog.Builder(DownLoadsActivity.this);
                    alertDialog.setTitle("عاوز اغنية '"+downloadedSongName.get(Constant.postion)+"' تبقى رنة موبايلك");
                    // view = getLayoutInflater().inflate(R.layout.dialog_layout,null);
                    // alertDialog.setView(view);
                    alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            File SDCardRoot = Environment.getExternalStorageDirectory(); // location where you want to store
                            File file = new File(Environment.getExternalStorageDirectory(), SDCardRoot+"/Sultan/"+downloadedSongName.get(Constant.postion));
                           // String path=Environment.getExternalStorageDirectory()++"/MusicPro";
                            // File file = new File(dir, "my_filename");
                           // Toast.makeText(DownLoadsActivity.this, "p"+file.toString(), Toast.LENGTH_SHORT).show();

                           // Toast.makeText(DownLoadsActivity.this, "name    "+downloadedSongUrl.get(Constant.postion), Toast.LENGTH_SHORT).show();

                            //  setRingtone(file.toString(),downloadedSongName.get(Constant.postion));
                          ringtone(downloadedSongUrl.get(Constant.postion)+"");


                        }
                    });
                    alertDialog.show();

                   /* FragmentManager fm = getFragmentManager();
                    RingFragmentDialog dialogFragment = new RingFragmentDialog();
                    dialogFragment.show(fm, "ring");*/
                } else {
                    Snackbar snackbar = Snackbar
                            .make(v, "اختر النغمه التى تريد الاستماع اليها :( !!", Snackbar.LENGTH_LONG);

                    snackbar.show();

                }
            }
        });
    }

    public void settingPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.System.canWrite(getApplicationContext())) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS, Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 200);

            }
        }
    }
    private void StartAnimations() {
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.alpha);
        anim.reset();
        LinearLayout l=(LinearLayout) findViewById(R.id.downloadPlayerSheet);
        l.clearAnimation();
        l.startAnimation(anim);
        findViewById(R.id.downloadPlayerSheet).setVisibility(View.VISIBLE);

    /*    anim = AnimationUtils.loadAnimation(this, R.anim.translate);
        anim.reset();
        //ImageView iv = (ImageView) findViewById(R.id.splash);

        .clearAnimation();
        holder.youtubeAnim.startAnimation(anim);*/





    }


    public void ringtone(String path)
    {
        // Create File object for the specified ring tone path
        File f=new File(path);




        ContentValues content = new ContentValues();
        content.put(MediaStore.MediaColumns.DATA,f.getAbsolutePath());
        content.put(MediaStore.MediaColumns.TITLE, downloadedSongName.get(Constant.postion));
        content.put(MediaStore.MediaColumns.SIZE, 215454);
        content.put(MediaStore.MediaColumns.MIME_TYPE, "audio/*");
        //  content.put(MediaStore.Audio.Media.ARTIST, "Madonna");
        content.put(MediaStore.Audio.Media.DURATION, 230);
        content.put(MediaStore.Audio.Media.IS_RINGTONE, true);
        content.put(MediaStore.Audio.Media.IS_NOTIFICATION, true);
        content.put(MediaStore.Audio.Media.IS_ALARM, true);
        content.put(MediaStore.Audio.Media.IS_MUSIC, true);


 /*       String Ringtonepath= "content://media/internal/audio/media/297";
        Uri Ringtone1 = Uri.parse(path);*/
        //Insert it into the database
        Log.i("TAG", "the absolute path of the file is :"+
                f.getAbsolutePath());
        Uri uri = MediaStore.Audio.Media.getContentUriForPath(
                f.getAbsolutePath());




        getContentResolver().delete(uri, MediaStore.MediaColumns.DATA + "=\"" + f.getAbsolutePath() + "\"",
                null);
        Uri newUri = getContentResolver().insert(uri, content);
        System.out.println("uri=="+uri);
        Log.i("TAG","the ringtone uri is :"+newUri);
        RingtoneManager.setActualDefaultRingtoneUri(
                getApplicationContext(), RingtoneManager.TYPE_RINGTONE,
                newUri);


/*
       // sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,Uri.parse("file://"+f.toString())));

        ContentResolver contentResolver=getContentResolver();

        //Cursor cursor=contentResolver.query(f.getAbsolutePath(),)

        // Insert the ring tone to the content provider
        ContentValues value=new ContentValues();
        Long current=System.currentTimeMillis();

        value.put(MediaStore.MediaColumns.DATA, f.getAbsolutePath());
        value.put(MediaStore.MediaColumns.TITLE, downloadedSongName.get(Constant.postion));
        value.put(MediaStore.MediaColumns.SIZE, f.length());
        value.put(MediaStore.Audio.Media.DATE_ADDED,(int) (current / 1000));

        value.put(MediaStore.MediaColumns.MIME_TYPE,"audio*//*");
        value.put(MediaStore.Audio.Media.ARTIST, "artist");
        value.put(MediaStore.Audio.Media.DURATION, 500);
        value.put(MediaStore.Audio.Media.IS_ALARM, false);
        value.put(MediaStore.Audio.Media.IS_MUSIC, false);
        value.put(MediaStore.Audio.Media.IS_NOTIFICATION, false);
        value.put(MediaStore.Audio.Media.IS_RINGTONE, true);
        ContentResolver cr=getContentResolver();
        Uri url= MediaStore.Audio.Media.getContentUriForPath(f.getAbsolutePath());
*//*        getContentResolver().delete(
                url,
                MediaStore.MediaColumns.DATA + "=\""
                        + f.getAbsolutePath() + "\"", null);*//*
        Uri newUri = contentResolver.insert(url, content);

        if (Uri.EMPTY.equals(newUri))
        {
            Toast.makeText(DownLoadsActivity.this, "emppppty", Toast.LENGTH_SHORT).show();
        }else {
//            String nn =newUri.toString();
          //  Toast.makeText(DownLoadsActivity.this, "not em"+ nn, Toast.LENGTH_SHORT).show();


        }

        //Uri addedUri=cr.insert(url, value);
       // Uri aa=getContentResolver().insert(url,value);
        // Set default ring tone

      //  f=addedUri;

        Log.d("uri ring tone   ",url.toString());
        Log.d("uri value   ",value.toString());
       // Log.d("uri value   ",aa.toString());


      //  Settings.System.putString(cr,Settings.System.RINGTONE,url.toString());

      //  Uri newUri=Uri.parse(path);

        RingtoneManager.setActualDefaultRingtoneUri(DownLoadsActivity.this, RingtoneManager.TYPE_RINGTONE,newUri);*/
    }

    public void setLisner()
    {

        btn_Fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Constant.playListName.size()>0){


                    // Toast.makeText(getActivity(), "pos"+searchnameResult.get(Constant.postion), Toast.LENGTH_SHORT).show();
                   // downloadong(Constant.playListUrl.get(Constant.postion),Constant.playListName.get(Constant.postion));
                } else {
                    Snackbar snackbar = Snackbar
                            .make(v, "اختر النغمه التى تريد نحميلها", Snackbar.LENGTH_LONG);

                    snackbar.show();

                }
            }
        });



    /*    btn_CallTone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //   calltoneChecker(Constant.postion);
                //   callTone(Constant.postion);
                if (Constant.playListName.size()>0) {

                   *//* FragmentManager fm = getFragmentManager();
                    RingFragmentDialog dialogFragment = new RingFragmentDialog();
                    dialogFragment.show(fm, "ring");*//*
                } else {
                    Snackbar snackbar = Snackbar
                            .make(v, "اختر النغمه التى تريد الاستماع اليها :( !!", Snackbar.LENGTH_LONG);

                    snackbar.show();

                }
            }
        });*/






        btn_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Constant.playListName.size()>0){


                    if (musicSrv.isPlayingg()) {
                        btn_play.setBackground(getResources().getDrawable(R.drawable.play_button_light_new));

                        musicSrv.pausePlayer();
                        //new myPlayService().pauseMedia();
                        // MusicServiceSemsm.player.pause();

                    } else {
                        btn_play.setBackground(getResources().getDrawable(R.drawable.stop_button_light_new));
                        musicSrv.go();
                        //  new myPlayService().playMedia();
                        // musicSrv.pausePlayer();
                        //  MusicServiceSemsm.player.start();
                    }

             /*   if (btn_play.getBackground(getResources().getDrawable(R.drawable.stop_icon)))
                {

                   // btn_play.setText("Play");

                    btn_play.setBackground(getResources().getDrawable(R.drawable.play_button));
                    // musicSrv.pausePlayer();
                    MusicServiceSemsm.player.pause();


                }
                else {

                   // btn_play.setText("Pause");
                    btn_play.setBackground(getResources().getDrawable(R.drawable.stop_icon));

                    MusicServiceSemsm.player.start();


                }
*/
                }else
                {
                    Snackbar snackbar = Snackbar
                            .make(v, "اختر النغمه التى تريد الاستماع اليها :( !!", Snackbar.LENGTH_LONG);

                    snackbar.show();

                }


            }
        });






        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Constant.playListName.size()>0){


                    if (Constant.postion > 0)
                    {

                        //  lastPosition=Constant.postion;

                        Constant.postion--;
                        //  musicSrv.pausePlayer();

                        //  play();
                        musicSrv.pausePlayer();
                        play();
                      /*  new myPlayService().pauseMedia();
                        playAudio();*/





                    }
                    else {
                        //  lastPosition=Constant.postion;
                        Constant.postion=Constant.playListUrl.size()-1;
                       /* new myPlayService().pauseMedia();
                        playAudio();*/
                    musicSrv.pausePlayer();
                    play();

                    }
                }else
                {
                    Snackbar snackbar = Snackbar
                            .make(v, "اختر النغمه التى تريد الاستماع اليها :( !!", Snackbar.LENGTH_LONG);

                    snackbar.show();

                }
            }
        });






        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Constant.playListName.size()>0){

                    if (Constant.postion<(Constant.playListName.size()-1))
                    {
                        //   lastPosition=Constant.postion;
                        Constant.postion++;
                        new myPlayService().pauseMedia();
                        /// playAudio();
                        // musicSrv.pausePlayer();

                        // player.pause();
                        play();
                        //  playSong();

                    }
                    else {


                        ///      lastPosition=searchnameResult.size()-1;
                        Constant.postion=0;
                        /*new myPlayService().pauseMedia();
                        playAudio();*/


                          musicSrv.pausePlayer();

                        // player.pause();
                        play();
                        //   playSong();


                    }
                }else
                {
                    Snackbar snackbar = Snackbar
                            .make(v, "اختر النغمه التى تريد الاستماع اليها :( !!", Snackbar.LENGTH_LONG);

                    snackbar.show();

                }
            }
        });










    }



    @Override
    public void onStart() {
        super.onStart();
        if(playIntent==null){
            playIntent = new Intent(this, MusicServiceSemsm.class);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
        }
       /* if (MusicServiceSemsm.player!= null)
        {
            if (MusicServiceSemsm.player.isPlaying())
            {
                MusicServiceSemsm.player.pause();
            }
        }*/

    }

    private ServiceConnection musicConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicServiceSemsm.MusicBinder binder = (MusicServiceSemsm.MusicBinder) service;
            //get service
            musicSrv = binder.getService();
            //pass list
            musicSrv.setList(Constant.playListUrl);
            musicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };



    public void play()
    {
        //trackPlayingicon(Constant.postion);
        musicSrv.playSong();
        songNametxt.setText(Constant.playListName.get(Constant.postion));

        /*finalTime=MusicServiceSemsm.player.getDuration();
        startTime=musicSrv.getPosn();

        finalTimeTxt.setText(String.format("%d.%d ",
                TimeUnit.MILLISECONDS.toMinutes((long) finalTime),
                TimeUnit.MILLISECONDS.toSeconds((long) finalTime) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long)
                                finalTime)))
        );

        startTimeTxt.setText(String.format("%d.%d ",
                TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long)
                                startTime)))
        );*/


        registerReceiver(broadcastReceiver, new IntentFilter(
                MusicServiceSemsm.BROADCAST_ACTION));




        mBroadcastIsRegistered = true;

    }

    // -- Broadcast Receiver to update position of seekbar from service --
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent serviceIntent) {
            updateUI(serviceIntent);
            //    updateUIText(serviceIntent);
        }
    };

    private void updateUI(Intent serviceIntent) {
        String counter = serviceIntent.getStringExtra("counter");
        String mediamax = serviceIntent.getStringExtra("mediamax");
        String strSongEnded = serviceIntent.getStringExtra("song_ended");
        String cPos=serviceIntent.getStringExtra("cPostion");
        int seekProgress = Integer.parseInt(counter);
        seekMax = Integer.parseInt(mediamax);
        songEnded = Integer.parseInt(strSongEnded);
        seekBar.setMax(seekMax);
        seekBar.setProgress(seekProgress);


        finalTime=Double.valueOf(mediamax);
        startTime=Double.valueOf(counter);

        if (Constant.playListName.size()>0) {

            songNametxt.setText(Constant.playListName.get(Constant.postion));
        }
      //  trackPlayingicon(Constant.postion);

        /*if (Constant.playListName.size() > 0)
        {
            for (int i=0;i< downloadedSongName.size() ;i++)
            {
                DownLoadRecyAdapter.ViewHolder holder =
                        (DownLoadRecyAdapter.ViewHolder) mRecyclerView.findViewHolderForAdapterPosition(i);
                holder.songNametxt.setTextColor(getResources().getColor(R.color.cardview_light_background));
                //  holder.imageViewGif.setImageDrawable(getResources().getDrawable(R.drawable.vod));
                // holder.imageViewGif.setVisibility(View.GONE);
            }
            DownLoadRecyAdapter.ViewHolder holder =
                    (DownLoadRecyAdapter.ViewHolder) mRecyclerView.findViewHolderForAdapterPosition(Constant.postion);
            holder.songNametxt
                    .setTextColor(getResources().getColor(R.color.colorAccent));


        }*/
        finalTimeTxt.setText(String.format("%d:%d ",
                TimeUnit.MILLISECONDS.toMinutes((long) finalTime),
                TimeUnit.MILLISECONDS.toSeconds((long) finalTime) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long)
                                finalTime)))
        );

        startTimeTxt.setText(String.format("%d:%d ",
                TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long)
                                startTime)))
        );


    }





    public void trackPlayingicon(int position)
    {
        if (pointer==0)
        {
            pointer=position;
            firstPointer=pointer;
            DownLoadRecyAdapter.ViewHolder holder = (DownLoadRecyAdapter.ViewHolder) mRecyclerView.findViewHolderForAdapterPosition(position);
             holder.songNametxt.setTextColor(getResources().getColor(R.color.colorAccent));
            //  holder.imageViewGif.setImageDrawable(getResources().getDrawable(R.drawable.vod));
           // holder.gif.setVisibility(View.VISIBLE);
        }
        else if (pointer == position)
        {

        }
        else {

            if (downloadedSongName.size()>0 && firstPointer>0) {

                DownLoadRecyAdapter.ViewHolder holder3 = (DownLoadRecyAdapter.ViewHolder) mRecyclerView.findViewHolderForAdapterPosition(0);
                holder3.songNametxt.setTextColor(getResources().getColor(R.color.cardview_light_background));
               // holder3.gif.setVisibility(View.GONE);

            }
            DownLoadRecyAdapter.ViewHolder holder = (DownLoadRecyAdapter.ViewHolder) mRecyclerView.findViewHolderForAdapterPosition(pointer);
            // holder.username.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            holder.songNametxt.setTextColor(getResources().getColor(R.color.cardview_light_background));

           // holder.gif.setVisibility(View.GONE);

            pointer=position;

            DownLoadRecyAdapter.ViewHolder holder2 = (DownLoadRecyAdapter.ViewHolder) mRecyclerView.findViewHolderForAdapterPosition(pointer);
            //  holder2.imageViewGif.setImageDrawable(getResources().getDrawable(R.drawable.vod));
            holder2.songNametxt.setTextColor(getResources().getColor(R.color.colorAccent));

           // holder2.gif.setVisibility(View.VISIBLE);



        }

    }


/*    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (MusicServiceSemsm.player !=null && MusicServiceSemsm.player.isPlaying() )
        {
            MusicServiceSemsm.player.pause();
        }
        finish();
    }*/

    private void initSwipe(){
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition();

                if (direction == ItemTouchHelper.LEFT){
                   // Toast.makeText(AlbumContentActivity.this, "download   "+position, Toast.LENGTH_SHORT).show();

                    alertDialog = new AlertDialog.Builder(DownLoadsActivity.this);
                    alertDialog.setTitle("هل تريد مسح هذه الاغنيه");
                    // view = getLayoutInflater().inflate(R.layout.dialog_layout,null);
                    // alertDialog.setView(view);
                    alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            File SDCardRoot = Environment.getExternalStorageDirectory(); // location where you want to store
                            File file = new File(Environment.getExternalStorageDirectory(), SDCardRoot+"/Sultan/"+downloadedSongName.get(position));
                            // File file = new File(dir, "my_filename");
                            File file1 = new File(Environment.getExternalStorageDirectory(), SDCardRoot+"/Sultan/"+downloadedSongName.get(position)+".mp3");

                            boolean deleted = file.delete();
                            boolean deleted2 = file1.delete();
                            if (deleted || deleted2)
                            {
                                mAdapter.notifyDataSetChanged();

                                delete=1;

                                startActivity(new Intent(DownLoadsActivity.this,DownLoadsActivity.class));
                                Toast.makeText(DownLoadsActivity.this, "deleted", Toast.LENGTH_SHORT).show();
                            }


                        }
                    });
                    alertDialog.show();

                    mAdapter.notifyDataSetChanged();
                } else {
                    alertDialog = new AlertDialog.Builder(DownLoadsActivity.this);
                    alertDialog.setTitle("هل تريد مسح هذه الاغنيه");
                    // view = getLayoutInflater().inflate(R.layout.dialog_layout,null);
                    // alertDialog.setView(view);
                    alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            File SDCardRoot = Environment.getExternalStorageDirectory(); // location where you want to store
                            File file = new File(Environment.getExternalStorageDirectory(), SDCardRoot+"/Sultan/"+downloadedSongName.get(position));
                            // File file = new File(dir, "my_filename");
                            File file1 = new File(Environment.getExternalStorageDirectory(), SDCardRoot+"/Sultan/"+downloadedSongName.get(position)+".mp3");

                            boolean deleted = file.delete();
                            boolean deleted2 = file1.delete();
                            if (deleted || deleted2)
                            {
                                mAdapter.notifyDataSetChanged();

                                delete=1;

                                  startActivity(new Intent(DownLoadsActivity.this,DownLoadsActivity.class));
                                Toast.makeText(DownLoadsActivity.this, "deleted", Toast.LENGTH_SHORT).show();
                            }


                        }
                    });
                    alertDialog.show();

                    mAdapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                Bitmap icon;
                if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE){

                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;

                    if(dX > 0){
                        p.setColor(Color.parseColor("#D32F2F"));
                        RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX,(float) itemView.getBottom());
                        c.drawRect(background,p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.delete_icon);
                        RectF icon_dest = new RectF((float) itemView.getLeft() + width ,(float) itemView.getTop() + width,(float) itemView.getLeft()+ 2*width,(float)itemView.getBottom() - width);
                        c.drawBitmap(icon,null,icon_dest,p);
                    } else {
                        p.setColor(Color.parseColor("#D32F2F"));
                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(),(float) itemView.getRight(), (float) itemView.getBottom());
                        c.drawRect(background,p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.delete_icon);
                        RectF icon_dest = new RectF((float) itemView.getRight() - 2*width ,(float) itemView.getTop() + width,(float) itemView.getRight() - width,(float)itemView.getBottom() - width);
                        c.drawBitmap(icon,null,icon_dest,p);
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);

    }


    public void setRingtone(String ringtoneuri,String ringtoneName) {
        //String ringtoneuri = Environment.getExternalStorageDirectory().getAbsolutePath() + "/media/ringtone";
       //// File file1 = new File(ringtoneuri);
      //  file1.mkdirs();
        File newSoundFile = new File(ringtoneuri, ringtoneName);


      //  Uri mUri = Uri.parse("android.resource://globalapps.funnyringtones/raw/sound_two.mp3");


        ContentResolver mCr = this.getContentResolver();
   /*     AssetFileDescriptor soundFile;
        try {
            soundFile = mCr.openAssetFileDescriptor(mUri, "r");
        } catch (FileNotFoundException e) {
            soundFile = null;
        }

        try {
            byte[] readData = new byte[1024];
            FileInputStream fis = soundFile.createInputStream();
            FileOutputStream fos = new FileOutputStream(newSoundFile);
            int i = fis.read(readData);

            while (i != -1) {
                fos.write(readData, 0, i);
                i = fis.read(readData);
            }

            fos.close();
        } catch (IOException io) {
        }*/

        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DATA, newSoundFile.getAbsolutePath());
        values.put(MediaStore.MediaColumns.TITLE, ringtoneName);
        values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp3");
        values.put(MediaStore.MediaColumns.SIZE, newSoundFile.length());
        values.put(MediaStore.Audio.Media.ARTIST, R.string.app_name);
        values.put(MediaStore.Audio.Media.IS_RINGTONE, true);
        values.put(MediaStore.Audio.Media.IS_NOTIFICATION, true);
        values.put(MediaStore.Audio.Media.IS_ALARM, true);
        values.put(MediaStore.Audio.Media.IS_MUSIC, false);

        Uri uri = MediaStore.Audio.Media.getContentUriForPath(newSoundFile.getAbsolutePath());
        Uri newUri = mCr.insert(uri, values);
        try {
            Uri rUri = RingtoneManager.getValidRingtoneUri(this);
            if (rUri != null)
                mgr.setStopPreviousRingtone(true);
            RingtoneManager.setActualDefaultRingtoneUri(getApplicationContext(), RingtoneManager.TYPE_RINGTONE, newUri);
            Toast.makeText(this, "New Rigntone set", Toast.LENGTH_SHORT).show();
        } catch (Throwable t) {
            Log.e("sanjay in catch", "catch exception"+t.getMessage());
        }

    }


    @Override
    public void onPause() {
        super.onPause();
        if (delete == 1)
        {
            delete=0;
            finish();
        }

        if (musicSrv != null && MusicServiceSemsm.player != null && MusicServiceSemsm.player.isPlaying()) {


            telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            //   Log.v(TAG, "Starting listener");
            phoneStateListener = new PhoneStateListener() {
                @Override
                public void onCallStateChanged(int state, String incomingNumber) {
                    // String stateString = "N/A";
                    // Log.v(TAG, "Starting CallStateChange");
                    switch (state) {
                        case TelephonyManager.CALL_STATE_OFFHOOK:
                        case TelephonyManager.CALL_STATE_RINGING:
                            if (musicSrv != null) {
                                musicSrv.pausePlayer();

                                // MusicServiceSemsm.player.pause();
                                isPausedInCall = true;
                            }

                            break;
                        case TelephonyManager.CALL_STATE_IDLE:
                            // Phone idle. Start playing.
                            if (musicSrv != null) {
                                if (isPausedInCall) {
                                    isPausedInCall = false;
                                   // musicSrv.go();

                                    //MusicServiceSemsm.player.start();
                                }

                            }
                            break;
                    }

                }
            };

            // Register the listener with the telephony manager
            telephonyManager.listen(phoneStateListener,
                    PhoneStateListener.LISTEN_CALL_STATE);

        }
    }
  /*  @Override
    public void onCompletion(MediaPlayer mp) {
        mp.reset();


        if (Constant.postion+1>=downloadedSongUrl.size())
        {
            mp.pause();
            Constant.postion=0;
            playSong();

        }
        else {
            mp.pause();
            Constant.postion++;
            playSong();

        }


    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mp.reset();

        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {


        seekBar.setMax(player.getDuration());
        playCycle();
        mp.start();


        finalTime = player.getDuration();
        startTime = player.getCurrentPosition();


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean input) {
                if (input) {
                    player.seekTo(progress);

                }


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {



            }


        });



        finalTimeTxt.setText(String.format("%d.%d ",
                TimeUnit.MILLISECONDS.toMinutes((long) finalTime),
                TimeUnit.MILLISECONDS.toSeconds((long) finalTime) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long)
                                finalTime)))
        );

        startTimeTxt.setText(String.format("%d.%d ",
                TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long)
                                startTime)))
        );

        seekBar.setProgress((int)startTime);
        myHandler.postDelayed(UpdateSongTime,100);



    }
*/
    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

 /*   @Override
    protected void onDestroy() {

        NotificationManager manager= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.cancelAll();

        stopService(playIntent);
        musicSrv=null;

        super.onDestroy();

    }*/

    @Override
    protected void onResume() {
        super.onResume();
        if (MusicServiceSemsm.player.isPlaying())
        {
            if (Constant.playListName.size()>0) {


                findViewById(R.id.downloadPlayerSheet).setVisibility(View.VISIBLE);
                btn_play.setBackground(getResources().getDrawable(R.drawable.stop_button_light_new));
                songNametxt.setText(Constant.playListName.get(Constant.postion));
            }


        }
    }

    /*public void playSong(){

       // Constant.songName=downloadedSongName.get(position);
       // Constant.songUrl=downloadedSongUrl.get(position);
        songNametxt.setText(downloadedSongName.get(Constant.postion));


        trackPlayingicon(Constant.postion);

        player.reset();

        try{
            //"http://musicpro.890m.com/musicpro/songs/Amr%20Diab%20-%2001%20-%20Waiyaah.mp3"
            player.setDataSource(downloadedSongUrl.get(Constant.postion));
        }
        catch(Exception e){
            Log.e("MUSIC SERVICE", "Error setting data source", e);
        }
        player.prepareAsync();


 *//*       finalTime=player.getDuration();
        finalTimeTxt.setText(String.format("%d.%d ",
                TimeUnit.MILLISECONDS.toMinutes((long) finalTime),
                TimeUnit.MILLISECONDS.toSeconds((long) finalTime) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long)
                                finalTime)))
        );*//*

    }*/

/*
    public void initMusicPlayer(){
        //set player properties
        player.setWakeMode(getApplicationContext(),
                PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        //set listeners
        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
    }*/

    private Runnable UpdateSongTime = new Runnable() {
        public void run() {
            startTime =player.getCurrentPosition();
            startTimeTxt.setText(String.format("%d.%d ",
                    TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                    TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                    toMinutes((long) startTime)))
            );
            seekBar.setProgress((int)startTime);

            myHandler.postDelayed(this, 100);
        }
    };

    public void playCycle()
    {
        seekBar.setProgress(player.getCurrentPosition());
        if(player.isPlaying())
        { runnable=new Runnable() {
            @Override
            public void run() {
                playCycle();
            }
        };

        }
        else{
            //  Toast.makeText(AlbumContentActivity.this, "loading", Toast.LENGTH_SHORT).show();
        }
        myHandler.postDelayed(runnable,1000);

    }


    public void inti(){

        btn_CallTone= (Button) findViewById(R.id.callToneBtn);
        btn_Fav= (Button) findViewById(R.id.favbtn);

        startTimeTxt = (TextView) findViewById(R.id.startTimetxt);
        songNametxt = (TextView) findViewById(R.id.songNametxt);
        finalTimeTxt = (TextView) findViewById(R.id.endTimetxt);


        btn_play= (Button) findViewById(R.id.btnPlayerPlay);
        btn_back= (Button) findViewById(R.id.btnPlayerBack);
        btn_next= (Button) findViewById(R.id.btnPlayerNext);

        seekBar= (SeekBar) findViewById(R.id.seekBar2);
        mRecyclerView = (RecyclerView) findViewById(R.id.MusicListDownloaded);
    }



    private void initDialog(){

       // et_country = (EditText)view.findViewById(R.id.et_country);
    }

}

class RecyclerTouchListenerDS implements RecyclerView.OnItemTouchListener {

    private GestureDetector gestureDetector;
    private DownLoadsActivity.ClickListener clickListener;

    public RecyclerTouchListenerDS(Context context, final RecyclerView recyclerView, final DownLoadsActivity.ClickListener clickListener) {
        this.clickListener = clickListener;
        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                if (child != null && clickListener != null) {
                    clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                }
            }
        });
    }



    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

        View child = rv.findChildViewUnder(e.getX(), e.getY());
        if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
            clickListener.onClick(child, rv.getChildPosition(child));
        }
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }
}

