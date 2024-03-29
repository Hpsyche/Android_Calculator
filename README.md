# 计算器设计思路
CSDN博客，见：https://blog.csdn.net/Hpsyche/article/details/88620022
## 计算器界面的实现

界面如下：

![](https://github.com/Hpsyche/Android_Calculator/blob/master/pict/%E8%AE%A1%E7%AE%97%E5%99%A8%E7%95%8C%E9%9D%A2.png)

过程：

- 首先通过网格布局，设计网格为6行4列；
- 第一行放置文本标签textview，用于显示数字和符号，及回响计算结果；
- 第二行用于放置两个button，一个用于清除textview，一个用于清除textview的最后一个字符；
- 后面四行用于放置0-9、及+-*/.=符号；

应注意：需要给textview和各个按钮添加id，以便在.java中实现对按钮的监听及对textview的回响显示。

## MainActivity的设计

过程：

- 获取各按钮；

- 各按钮添加监听器及监听事件；

  - 对于0-9和+-*/，可以设置同一个监听器（否则代码太过于冗余），对此需要在监听器中先判断button的来源，并对应符号进行回响；

    其关键代码：

    ```java
    class MyClick implements View.OnClickListener {
            @Override
            public void onClick(View v) {
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
    ```

  - 对于清除按钮的监听器，设置textview长度为0即可；

  - 对于撤回按钮的监听器，先判断是否已为0，为0则设置为空即可；不为0则去除最后一位；

  - 对于“=”的监听事件，是本设计的重点，也是最为复杂的；首先需要通过正则判断输入是否不符合运算规则：

    - 若不符合，回显“输入有误！”
    - 若符合，通过新建的utils工具类（下文介绍）计算出结果并回显；

## Utils.Calculate设计

此类最为关键，用于计算结果

过程：

- 1.首先定义三个列表，用于存储各信息
  - List<String> math：用于存储计算式中的数字
  - List<Stirng> flag：用于存储计算式中的符号
  - List<Integer> mulDiv：用于存储 “在flag列表中*或/的下标” 的list列表，以便后续的优先级运算
- 2.循环计算式中各字符，并将数字存储于math，将符号存储于flag，将mulDiv中；
- 3.若mulDiv不为空，则按先后计算mulDiv前后两数*或/的结果；
  - 由于mulDiv存储的为flag列表的 * 或 / 的下标，故可利用flag，找到该 * 或 / 符号，并在math列表中找到其前后的数字；
  - 判断flag及数字的类型（整型 / 浮点型），并进行 * 或 / 运算；
- 4.计算完毕后在math中remove前后两数，在flag中remove该符号，在mulDiv中remove第一位，并在math后重新添加计算结果；
- 5.循环步骤3-4至mulDiv为0，此时计算式只剩下+-，原理大体同3-4，按先后计算flag前后两数*或/的结果，计算完毕后在math中remove前后两数，在flag中remove该符号，并在math后重新添加计算结果；
- 最终即可得出运算结果。

由于此算法较为复杂，在此贴出该Calculate工具类的完整代码：

```java
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
```

# 效果演示

输入1+2*3

![](https://github.com/Hpsyche/Android_Calculator/blob/master/pict/%E8%BE%93%E5%85%A51.png)

“=”结果：

![](https://github.com/Hpsyche/Android_Calculator/blob/master/pict/%E7%BB%93%E6%9E%9C1.png)

在输入“2.0++”时（即输入不符合规则的数据），会输出：

![](https://github.com/Hpsyche/Android_Calculator/blob/master/pict/%E7%BB%93%E6%9E%9Cerror.png)

# 问题总结

## 问题

在进行本次实验时，我遇到了以下一些问题：

- 在给各按钮添加监听器时，一开始就发现了若一个个添加的代码过于冗杂，故想法是在各按钮调用同个监听器，在监听器中判断按钮的来源，但实际发现判断来源并不容易，在view.getXXX方法中都没有能找到直接buttonX，只能利用较为捷径的方法：System.out.print(view)，得：

  android.support.v7.widget.AppCompatButton{54df8a5 VFED..C.. ...P.... 462,316-693,457 #7f070025 app:id/button3}，故利用view最最后的buttonX，得触发该事件的按钮名称：

  ```java
  int index=v.toString().lastIndexOf("button");
  //view名称中有"button*"的信息，可利用*来确定事件来源者身份
  String buttonNum=v.toString().substring(index+6,index+7);
  ```

- 在“=”中正则判断时，一开始使用以下正则表示式：

  ```java
  String regexCheck = "\\d+([+*/-]\\d+)+";
  ```

  后发现不能对浮点数进行正则判断，故进行修改，由于对正则掌握得不是很好，修改过程也是历经磨难，经过网上搜索才得以解决：

  ```java
  String regexCheck = "(\\d+(\\.\\d+)?+[+\\-*/])+\\d+(\\.\\d+)?+";
  ```

- 在计算器的 + - * / 计算过程中，一开始还对 * / 进行优先级运算，后在调试时发现问题，一般摸索后，加入新的列表mulDiv来存储“ * / ”，并修改代码；

  主要代码如下：

  ```java
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
  ```

## 总结

​	此时实验乍一看并不能，不就是写个计算器嘛，在写的过程中，才发现计算器的学问真不少，我这里写的计算器还只是很简单的计算器，并没有包括括号、百分号等运算，若加入这些，更为复杂；
​	通过这次实验，除了让我学到了Android的页面设计知识以外，也让我复习了Java中List、Map等集合的操作，并且也锻炼了自己的算法能力，希望自己在以后的实验中能越做越好。

