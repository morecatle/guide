package com.kakao.guide;

import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.mannan.translateapi.Language;
import com.mannan.translateapi.TranslateAPI;

public class TestActivity extends AppCompatActivity {
    EditText editTest;
    Button test;
    TextView textView, text_input, text_output;
    Boolean translate = false; // 0: input, 1: output;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_layout);

        editTest = (EditText)findViewById(R.id.editText);
        test = (Button)findViewById(R.id.btn_test);
        textView = (TextView)findViewById(R.id.textView);
        text_input = (TextView)findViewById(R.id.text_input);
        text_output = (TextView)findViewById(R.id.text_output);


        registerForContextMenu(text_input);
        registerForContextMenu(text_output);
        text_input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                translate = false;
                v.showContextMenu();
            }
        });
        text_output.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                translate = true;
                v.showContextMenu();
            }
        });


        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final TranslateAPI translateAPI = new TranslateAPI(
                        getEnglish(text_input.getText().toString()),
                        getEnglish(text_output.getText().toString()),
                        editTest.getText().toString()
                );

                translateAPI.setTranslateListener(new TranslateAPI.TranslateListener() {
                    @Override
                    public void onSuccess(String s) {
                        textView.setText(s);
                    }

                    @Override
                    public void onFailure(String s) {
                        System.out.print("번역 오류 발생: "+s);
                    }
                });
            }
        });


//        editTest.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                /////////////////
//                final TranslateAPI translateAPI = new TranslateAPI(
//                        Language.KOREAN,
//                        Language.ENGLISH,
//                        editTest.getText().toString()
//                );
//
//                translateAPI.setTranslateListener(new TranslateAPI.TranslateListener() {
//                    @Override
//                    public void onSuccess(String s) {
//                        textView.setText(s);
//                    }
//
//                    @Override
//                    public void onFailure(String s) {
//
//                    }
//                });
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater menuInflater = getMenuInflater();
        menu.setHeaderTitle("언어 선택");
        menuInflater.inflate(R.menu.translate_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.KOREAN:
                if(translate) {
                    text_output.setText("한국어");
                } else {
                    text_input.setText("한국어");
                }
                break;
            case R.id.ENGLISH:
                if(translate) {
                    text_output.setText("영어");
                } else {
                    text_input.setText("영어");
                }
                break;
            case R.id.CHINESE_SIMPLIFIED:
                if(translate) {
                    text_output.setText("중국어");
                } else {
                    text_input.setText("중국어");
                }
                break;
            case R.id.CHINESE_TRADITIONAL:
                if(translate) {
                    text_output.setText("라틴어");
                } else {
                    text_input.setText("라틴어");
                }
                break;
            case R.id.JAPANESE:
                if(translate) {
                    text_output.setText("일본어");
                } else {
                    text_input.setText("일본어");
                }
                break;
            case R.id.RUSSIAN:
                if(translate) {
                    text_output.setText("러시아어");
                } else {
                    text_input.setText("러시아어");
                }
                break;
        }
        return super.onContextItemSelected(item);
    }

    private String getEnglish(String place) {
        String temp = place;

        if(temp.equals("한국어")) {
            return Language.KOREAN;
        }
        else if(temp.equals("영어")) {
            return Language.ENGLISH;
        }
        else if(temp.equals("중국어")) {
            return Language.CHINESE;
        }
        else if(temp.equals("라틴어")) {
            return Language.LATIN;
        }
        else if(temp.equals("일본어")) {
            return Language.JAPANESE;
        }
        else if(temp.equals("러시아어")) {
            return Language.RUSSIAN;
        }
        else
            return null;
    }
}
