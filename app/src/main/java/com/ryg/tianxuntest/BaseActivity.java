package com.ryg.tianxuntest;

import android.app.Instrumentation;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ryg.tianxuntest.view.NetProgressDialog;

/**
 * Created by renyiguang on 2015/7/11.
 */
public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener {

    private final String NAME = BaseActivity.class.getName();
    public Context context;
    public LayoutInflater inflater;

    private RelativeLayout toolbar;
    public RelativeLayout mainContent;

    private View mainView;

    protected RelativeLayout leftView;
    protected RelativeLayout rightView;
    protected RelativeLayout centerView;

    private LinearLayout title_bar_left_back;
    private TextView title_bar_left_textview;
    private TextView title_bar_center_textview;
    private TextView title_bar_right_textview;

    protected InputMethodManager imm;
    protected NetProgressDialog netProgressDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        context = this;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        toolbar = (RelativeLayout)findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);


        leftView = (RelativeLayout)toolbar.findViewById(R.id.title_bar_left);
        rightView = (RelativeLayout) toolbar.findViewById(R.id.title_bar_right);
        centerView =  (RelativeLayout) toolbar.findViewById( R.id.title_bar_center);

        title_bar_left_back = (LinearLayout)leftView.findViewById(R.id.title_bar_left_back);
        title_bar_left_textview = (TextView)title_bar_left_back.findViewById(R.id.title_bar_left_tv);
        title_bar_center_textview = (TextView)centerView.findViewById(R.id.title_bar_center_tv);
        title_bar_right_textview = (TextView)rightView.findViewById(R.id.title_bar_right_tv);



        mainContent = (RelativeLayout)findViewById(R.id.main_content);
        mainView = inflater.inflate(getContentView(),null);
        mainContent.addView(mainView,new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT));

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        netProgressDialog = new NetProgressDialog(context,R.string.please_waiting);

        initView();
        setView();
    }



    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.title_bar_left){
            new Thread(){
                @Override
                public void run() {
                    try{
                        Instrumentation inst = new Instrumentation();
                        inst.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
                    }catch (Exception e){}
                }
            }.start();
        }
    }


    public void hideSoftInput(EditText view){
        imm.hideSoftInputFromWindow(view.getWindowToken(),0);}

    public void showSoftInput(EditText view){
        view.setFocusable(true);
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        imm.showSoftInput(view, 0);
    }

    public void toggleSoftInput(){
        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);
    }


    /**
     * hide title
     */
    protected void hideTitle( ) {
        toolbar.setVisibility(View.GONE);
    }


    /**
     * set left view
     *
     * @param view
     */
    protected void setTitleLeftView( View view ) {
        if( leftView != null ) {
            leftView.removeAllViews( );
            leftView.addView( view );
        }
    }
    /**
     * set center view
     *
     * @param view
     */
    protected void setTitleCenterView( View view ) {
        if( centerView != null ) {
            centerView.removeAllViews( );
            centerView.addView( view );
        }
    }

    /**
     * set right view
     *
     * @param view
     */
    protected void setTitleRightView( View view ) {
        if( rightView != null )
            rightView.removeAllViews();

        rightView.addView( view,new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.MATCH_PARENT));

    }


    public void setLeftBack(){

        title_bar_left_back.setVisibility(View.VISIBLE);
        leftView.setOnClickListener(this);
    }

    public void setLeftBack(int id){
        setLeftBack();
        title_bar_left_textview.setText(getResources().getString(id));
    }


    public void setCenterTextView (int id){

        setCenterTextView(getResources().getString(id));

    }

    public void setCenterTextView (String title){
        if( centerView != null ) {
            centerView.removeAllViews( );
            centerView.addView( title_bar_center_textview );
        }
        title_bar_center_textview.setText(title);

    }

    public void setRightTextView (int id){
        if( rightView != null ) {
            rightView.removeAllViews( );
            rightView.addView( title_bar_right_textview );
        }

        title_bar_right_textview.setText(getResources().getString(id));

    }

    protected abstract int getContentView();
    public abstract void initView();
    public abstract  void setView();


    public void showToast(String str){
        Toast.makeText(context,str,Toast.LENGTH_LONG).show();
    }

    public void showToast(int id){
        showToast(getResources().getString(id));
    }

    public void showLog(String str){
        Log.i(NAME,str);
    }


}
