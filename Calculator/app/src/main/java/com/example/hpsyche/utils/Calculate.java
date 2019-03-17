package com.example.hpsyche.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 计算器工具类，负责计算结果
 */
public class Calculate {
    private char add = '+';
    private char del = '-';
    private char mul = '*';
    private char div = '/';

    public String calculate(String s){
        //存储数据的list列表
        List<String> math = new ArrayList<String>();
        //存储运算符号的list列表
        List<String> flag = new ArrayList<String>();
        //存储  “在flag列表中*或/下标”  的list列表，以便后续的优先级运算
        List<Integer> mulDiv = new ArrayList<Integer>();
        for (int i = 0; i < s.length(); i++) {
            char temp = s.charAt(i);
            //如果不为+-*/任意一个，则在sbMath中追加
            if(temp!= add && temp!= del && temp!=mul && temp!=div){
                math.add(String.valueOf(temp));
            }
            else{
                //添加符号
                flag.add(String.valueOf(temp));
                //如果符号属于*或/，则添加至mulDiv中
                if(temp == mul || temp == div){
                    mulDiv.add(flag.size()-1);
                }
            }
        }
        //确保运算先后顺序
        while(math.size() != 1){
            boolean needReIndex = false;
            //确保*/先运算
            while(mulDiv.size() != 0){
                int index = mulDiv.get(0);
                if(needReIndex){
                    index = index -1;
                }
                //计算并移除数字和符号
                Map<String, List<String>> map = this.loopProcess(index, math, flag);
                math = map.get("math");
                flag = map.get("flag");
                //移除mulDiv中的*或/号
                mulDiv = this.removeList(Integer.class, mulDiv, 0);
                needReIndex = true;
            }
            //+-运算
            while(flag.size() != 0){
                Map<String, List<String>> map = this.loopProcess(0, math, flag);
                math = map.get("math");
                flag = map.get("flag");
            }
        }
        return math.get(0);
    }

    private Map<String, List<String>> loopProcess(int index, List<String> math, List<String> flag){
        Map<String, List<String>> map = new HashMap<String, List<String>>();
        //获取*或/
        char ch = flag.get(index).charAt(0);
        //进行+、-、*、/运算
        String result = this.getResult(math.get(index).trim(), math.get(index+1).trim(), ch);
        //在math中移除符号前后的数
        math = this.removeList(String.class, math, index);
        math = this.removeList(String.class, math, index);
        //添加入计算的结果
        math.add(index, result);
        //移除计算的符号
        flag = this.removeList(String.class, flag, index);
        map.put("math", math);
        map.put("flag", flag);
        return map;
    }

    private <T> List<T> removeList(Class<T> clazz, List<T> list, int index){
        List<T> listTemp = new ArrayList<T>();
        for (int i = 0; i < list.size(); i++) {
            if(i != index){
                listTemp.add(list.get(i));
            }
        }
        return listTemp;
    }

    //+-/*运算
    private String getResult(String b, String e, char flag){
        boolean isLong = false;
        if(!b.contains(".") && !e.contains(".")){
            isLong = true;
        }
        //如果不是浮点数
        if(isLong){
            if(flag == add){
                return String.valueOf(Long.valueOf(b)+Long.valueOf(e));
            }else if(flag == del){
                return String.valueOf(Long.valueOf(b)-Long.valueOf(e));
            }else if(flag == mul){
                return String.valueOf(Long.valueOf(b)*Long.valueOf(e));
            }else if(flag == div){
                return String.valueOf((double)Long.valueOf(b)/Long.valueOf(e));
            }else{
                throw new RuntimeException("error: "+ b + flag + e);
            }
        }
        //浮点数
        else{
            if(flag == add){
                return String.valueOf(Double.valueOf(b)+Double.valueOf(e));
            }else if(flag == del){
                return String.valueOf(Double.valueOf(b)-Double.valueOf(e));
            }else if(flag == mul){
                return String.valueOf(Double.valueOf(b)*Double.valueOf(e));
            }else if(flag == div){
                return String.valueOf((double)Double.valueOf(b)/Double.valueOf(e));
            }else{
                throw new RuntimeException("error: "+ b + flag + e);
            }
        }

    }
}
