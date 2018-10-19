package com.samsung.android.app.stepdiary;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.EditText;

public class StepInputPopup extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.step_input_popup);

        EditText et = (EditText) findViewById(R.id.editText1);
        et.setInputType(0x00000002);
        et.setText("");
    }

    //확인 버튼 클릭
    public void mOnClose(View v){

        EditText editText1 = (EditText) findViewById(R.id.editText1);
        String steps = editText1.getText().toString();

        //데이터 전달하기
        Intent intent = new Intent();
        intent.putExtra("result", steps);
        setResult(RESULT_OK, intent);

        //액티비티(팝업) 닫기
        finish();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥레이어 클릭시 안닫히게
//        if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
//            return false;
//        }
        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
        return;
    }
}
