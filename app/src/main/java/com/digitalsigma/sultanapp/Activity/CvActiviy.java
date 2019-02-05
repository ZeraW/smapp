package com.digitalsigma.sultanapp.Activity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.digitalsigma.sultanapp.R;

import net.alhazmy13.catcho.library.Catcho;

/**
 * Created by user2 on 1/10/2017.
 */

public class CvActiviy extends AppCompatActivity {

    Typeface t1;

    TextView nashaaTxt,triptxt,nametxt;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Catcho.Builder(this)
                .activity(ContactActivity.class)
                .build();
        setContentView(R.layout.activity_cv_details);
        t1=Typeface.createFromAsset(getAssets(),"hacen.ttf");

        nashaaTxt= (TextView) findViewById(R.id.nashaa);
        triptxt= (TextView) findViewById(R.id.trip);
        nametxt= (TextView) findViewById(R.id.name);



        //nametxt.setText("طارق الشيخ");
        nashaaTxt.setTypeface(t1);
        triptxt.setTypeface(t1);
        nametxt.setTypeface(t1);


        nametxt.setText("محمد سلطان");

        nashaaTxt.setText("محمد سلطان ولد 15 يناير 1983 بمحافظة البحيرة وقد بدأ الغناء عام 1997 في قصور الثقافة وحفلات الأسرة فاكتسب شهرة في بلدته والمحافظات المجاورة وبدأ يذيع صيته في مجال الغناء في الأفراح الشعبية في جميع انحاء الجمهورية وكان انطلاقته في عام 2007 حين أصدر اغنية باسم (الحكاية) حيث حققت نجاحا وانتشارا كبيرا بين الجمهور الذي احب اداء وطريقة سلطان في اداء اغانية بشكل درامي");
      //  nashaaTxt.setText("طارق الشيخ طارق دلعو عندليب الشعبى وسفير الاحزان (الكروان) من مواليد حى الشرابية السن 39 رصيد الالبومات 9 مع العلم ان البوم راحو ليس من البوماته ومن كليبات طارق عشاق والكل راح وبدور عليكى وسلملى على قلبك واجرح واله الكون ويارب قوينى والحكايه واخر اعماله حتى الان 3 اغانى من مسلسل الحاره وقد اكتشفه الاستاذ المؤلف امل الطاير وطارق الشيخ مع شركه الاصدقاء وقام بعمل دويتو مع عبد الباسط حموده وهو الحب الاولانى وتعامل مع بعض المؤلفين ومنهم مصطفى كامل ومسعد الكومى و امل الطاير وهانى الصغير وغيرهم الكثير وبدا الغناء من حوالى اكثر من 20 عام");
    }


    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
