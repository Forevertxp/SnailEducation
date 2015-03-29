package com.snail.segmented;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RadioGroup;
import android.widget.Toast;


public class SegmentedRadioActivity extends Activity implements RadioGroup.OnCheckedChangeListener {

    SegmentedRadioGroup segmentText;
    SegmentedRadioGroup segmentImg;
    Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_segmented_radio);

        segmentText = (SegmentedRadioGroup) findViewById(R.id.segment_text);
        segmentText.setOnCheckedChangeListener(this);
        segmentImg = (SegmentedRadioGroup) findViewById(R.id.segment_img);
        segmentImg.setOnCheckedChangeListener(this);

        mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
    }


    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (group == segmentText) {
            if (checkedId == R.id.button_one) {
                mToast.setText("One");
                mToast.show();
            } else if (checkedId == R.id.button_two) {
                mToast.setText("Two");
                mToast.show();
            } else if (checkedId == R.id.button_three) {
                mToast.setText("Three");
                mToast.show();
            }
        } else if (group == segmentImg) {
            if (checkedId == R.id.button_add) {
                mToast.setText("Add");
                mToast.show();
            } else if (checkedId == R.id.button_call) {
                mToast.setText("Call");
                mToast.show();
            } else if (checkedId == R.id.button_camera) {
                mToast.setText("Camera");
                mToast.show();
            }
        }
    }
}