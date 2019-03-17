package com.example.hpsyche.calculator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.hpsyche.utils.Calculate;


import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    private StringBuilder sb=new StringBuilder();
    private Button button0;
    private Button button1;
    private Button button2;
    private Button button3;
    private Button button4;
    private Button button5;
    private Button button6;
    private Button button7;
    private Button button8;
    private Button button9;
    private Button buttonA;
    private Button buttonB;
    private Button buttonC;
    private Button buttonD;
    private Button buttonE;
    private Button buttonO;
    private Button clearOne;
    private Button clear;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //获取各按钮
        button0=findViewById(R.id.button0);
        button1=findViewById(R.id.button1);
        button2=findViewById(R.id.button2);
        button3=findViewById(R.id.button3);
        button4=findViewById(R.id.button4);
        button5=findViewById(R.id.button5);
        button6=findViewById(R.id.button6);
        button7=findViewById(R.id.button7);
        button8=findViewById(R.id.button8);
        button9=findViewById(R.id.button9);
        buttonA=findViewById(R.id.buttonA);
        buttonB=findViewById(R.id.buttonB);
        buttonC=findViewById(R.id.buttonC);
        buttonD=findViewById(R.id.buttonD);
        buttonE=findViewById(R.id.buttonE);
        buttonO=findViewById(R.id.buttonO);
        buttonE=findViewById(R.id.buttonE);
        clear=findViewById(R.id.clear);
        clearOne=findViewById(R.id.clearOne);
        textView=findViewById(R.id.textView);
        //添加监听器，其中0-9和+-*/.为同个监听器
        button0.setOnClickListener(new MyClick());
        button1.setOnClickListener(new MyClick());
        button2.setOnClickListener(new MyClick());
        button3.setOnClickListener(new MyClick());
        button4.setOnClickListener(new MyClick());
        button5.setOnClickListener(new MyClick());
        button6.setOnClickListener(new MyClick());
        button7.setOnClickListener(new MyClick());
        button8.setOnClickListener(new MyClick());
        button9.setOnClickListener(new MyClick());
        buttonA.setOnClickListener(new MyClick());
        buttonB.setOnClickListener(new MyClick());
        buttonC.setOnClickListener(new MyClick());
        buttonD.setOnClickListener(new MyClick());
        buttonO.setOnClickListener(new MyClick());
        buttonE.setOnClickListener(new Calculator());
        clearOne.setOnClickListener(new ClearOne());
        clear.setOnClickListener(new Clear());
    }

    class MyClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            System.out.println(v);
            //获得view的名称中"button"的位置
            int index=v.toString().lastIndexOf("button");
            //view名称中有"button*"的信息，可利用*来确定事件来源者身份
            String buttonNum=v.toString().substring(index+6,index+7);
            if(buttonNum.equals("A")){
                sb.append("+");
            }
            else if(buttonNum.equals("B")){
                sb.append("-");
            }
            else if(buttonNum.equals("C")){
                sb.append("*");
            }
            else if(buttonNum.equals("D")){
                sb.append("/");
            }
            else if(buttonNum.equals("O")){
                sb.append(".");
            }
            //如果不属于+-*/. ,则直接添加即可
            else {
                sb.append(buttonNum);
            }
            textView.setText(sb);
        }
    }

    /**
     * 清除按钮的监听器
     */
    class Clear implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            sb.setLength(0);
            textView.setText(sb);
        }
    }

    /**
     * 撤回按钮的监听器
     */
    class ClearOne implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if(sb.length()==0){
                textView.setText("");
            }
            else {
                sb.delete(sb.length() - 1, sb.length());
                textView.setText(sb);
            }
        }
    }

    /**
     * “=”按钮的监听器，此算法最为复杂，故封装了个类与utils.Calculate中
     */
    class Calculator implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            //获取当前计算器公式
            String formula=sb.toString();
            //这是原先的正则表达式判断，后来发现不能对小数点进行正则判断
            // String regexCheck = "\\d+([+*/-]\\d+)+";// 是否是合法的表达式
            //后面修改的正则判断，能够是否是合法的表达式
            String regexCheck = "(\\d+(\\.\\d+)?+[+\\-*/])+\\d+(\\.\\d+)?+";
            //不匹配则计算器输出错误
            if (!Pattern.matches(regexCheck,formula)){
                sb.setLength(0);
                textView.setText("输入有误！");
            }
            //计算器算法！！！
            else {
                Calculate calculate=new Calculate();
                String result=calculate.calculate(formula);
                //结果显示
                textView.setText(result);
                //sb串需要赋值为result
                sb=new StringBuilder(result);
            }
        }
    }


}

