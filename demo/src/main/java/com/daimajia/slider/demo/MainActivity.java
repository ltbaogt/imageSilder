package com.daimajia.slider.demo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.facebook.drawee.backends.pipeline.Fresco;

import java.util.HashMap;


public class MainActivity extends ActionBarActivity implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener {

    private SliderLayout mDemoSlider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Fresco.initialize(this);
        mDemoSlider = (SliderLayout) findViewById(R.id.slider);

        HashMap<String, String> url_maps = new HashMap<String, String>();
        url_maps.put("Hannibal1", "http://dev.pam.cdn.propzy.vn/images/pam_listing_IMG-930795250_56509d432e2e3fe4c784e2061a0648357b5931771030c06d08b24f5e59e7ee11.jpg");
        url_maps.put("Hannibal2", "http://dev.pam.cdn.propzy.vn/images/pam_listing_IMG-1417145317_9fe43375343564a3ed59afa51cac284b0db5d81948a79cf39d308d9c4bf57786.jpg");
        url_maps.put("Hannibal3", "http://dev.pam.cdn.propzy.vn/images/pam_listing_listing_510556c36e2366349521ac18025311c75bd2224431b6e7763db297cadf58a8af.png");
        url_maps.put("Hannibal4", "http://dev.pam.cdn.propzy.vn/images/pam_listing_IMG1107776057_ecee64384be7c566e6dcd0a3fd18577fbacc6156fb3fa8343cc0e78612603a70.jpg");
        url_maps.put("Hannibal5", "http://dev.pam.cdn.propzy.vn/images/pam_listing_IMG-1013159107_e7d10b63273af89dff1f5719ef8769de32cb39167ada2cb77915e84c9ef7adde.jpg");
        url_maps.put("Hannibal6", "http://dev.pam.cdn.propzy.vn/images/pam_listing_IMG-1013159107_81e43f2e2b7705fe1e2bdbfe0e6564c7f922260296794ce1dc8b7ff091a621e3.jpg");
        url_maps.put("Big Bang Theory", "http://dev.pam.cdn.propzy.vn/images/pam_listing_listing_275275fd497e9bdbd93718cb511b2a323ff535a25690d852001738e96249fae0.png");
        url_maps.put("House of Cards", "http://dev.pam.cdn.propzy.vn/images/pam_listing_IMG-831845034_c853ebfd9976a65c22a87a7e6d8ec6a123b5fd61c3a3b0fd8ef0f2c9939d0c4a.jpg");
        url_maps.put("Game of Thrones", "http://dev.pam.cdn.propzy.vn/images/pam_listing_IMG1088398402_40c8410ba067662b356e57aab24140c7881a5bcad5e186c116dcd6ee04eab635.jpg");

//        HashMap<String,Integer> file_maps = new HashMap<String, Integer>();
//        file_maps.put("Hannibal",R.drawable.hannibal);
//        file_maps.put("Big Bang Theory",R.drawable.bigbang);
//        file_maps.put("House of Cards",R.drawable.house);
//        file_maps.put("Game of Thrones", R.drawable.game_of_thrones);

        for (String name : url_maps.keySet()) {
            TextSliderView textSliderView = new TextSliderView(this, -1, -1);
            // initialize a SliderLayout
            textSliderView
                    .description(name)
                    .image(url_maps.get(name))
                    .setScaleType(BaseSliderView.ScaleType.Fit)
                    .setOnSliderClickListener(this);

            //add your extra information
            textSliderView.bundle(new Bundle());
            textSliderView.getBundle()
                    .putString("extra", name);

            mDemoSlider.addSlider(textSliderView);
        }
        mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);
        mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        mDemoSlider.setCustomAnimation(new DescriptionAnimation());
        mDemoSlider.setDuration(4000);
        mDemoSlider.addOnPageChangeListener(this);
        mDemoSlider.stopAutoCycle();
        ListView l = (ListView) findViewById(R.id.transformers);
        l.setAdapter(new TransformerAdapter(this));
        l.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mDemoSlider.setPresetTransformer(((TextView) view).getText().toString());
                Toast.makeText(MainActivity.this, ((TextView) view).getText().toString(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    protected void onStop() {
        // To prevent a memory leak on rotation, make sure to call stopAutoCycle() on the slider before activity or fragment is destroyed
        mDemoSlider.stopAutoCycle();
        super.onStop();
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {
        Toast.makeText(this, slider.getBundle().get("extra") + "", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_custom_indicator:
                mDemoSlider.setCustomIndicator((PagerIndicator) findViewById(R.id.custom_indicator));
                break;
            case R.id.action_custom_child_animation:
                mDemoSlider.setCustomAnimation(new ChildAnimationExample());
                break;
            case R.id.action_restore_default:
                mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
                mDemoSlider.setCustomAnimation(new DescriptionAnimation());
                break;
            case R.id.action_github:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/daimajia/AndroidImageSlider"));
                startActivity(browserIntent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        Log.d("Slider Demo", "Page Changed: " + position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }
}
