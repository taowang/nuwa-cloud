package com.study.service.system.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * 测试接口
 * Created by macro on 2020/6/19.
 */
@Api(description = "测试接口", tags = "UserController")
@RequestMapping(value = "/system")
@RestController
public class HelloController {

    @ApiOperation("mgr")
    @GetMapping("/mgr")
    public String hello() {
        return "Hello World.";
    }

    public static void main(String[] args) {
        duplicateCode();
        //hsCheck();
        //sort();
        //muban();

    }

    private static void muban() {
        Scanner scanner = new Scanner(System.in);
        String strUp = scanner.nextLine();
        String strDown = scanner.nextLine();
        String[] splitUp = strUp.split(" ");
        int n = Integer.parseInt(splitUp[0]);
        int m = Integer.parseInt(splitUp[1]);
        String[] splitDown = strDown.split(" ");
        Arrays.sort(splitDown);
        int sum = Integer.parseInt(splitDown[0]);
        int left = 0;
        int total = m;
        for (int i = 1; i < n; i++) {
            int curNum = Integer.parseInt(splitDown[i]);
            int preVal = Integer.parseInt(splitDown[left]);
            System.out.println("i=" + i + " curNum=" + curNum + " preVal=" + preVal);
            //差值
            int diff = curNum - preVal;
            System.out.println(" m=" + m + " diff=" + diff);
            if (m > 0 && diff >= 0) {
                if (m > diff * i) {
                    m -= diff * i;
                    //如果在循环中就可以用完木料，则直接输出
//                    if (m == 0) {
//                        //取出 preVal + m/i
//                        System.out.println(preVal + diff/i);
//                        break;
//                    }
                } else {
                    System.out.println(" i=" + i + " sum =" + sum + " total=" + total);
                    System.out.println((sum + total) / i);
                    //将m置0
                    break;
                }
            }
            sum += curNum;
            //左指针右移
            left++;
        }
        //如果指针跑完整个数组，木料都还有多的,趣最大值 + m/n, 比如 最大值是5， m还剩余7
        if (total > sum) {
            System.out.println(" sum=" + sum);
            System.out.println((sum + total) / n);
        }
    }

    private static void sort2(){
        Scanner sc = new Scanner(System.in);
        String line = sc.nextLine();
        String[] strs = line.split(" ");
        Map<String, Integer> map = new HashMap<>();
        for (String str:strs){
            char[] chars = str.toCharArray();
            Arrays.sort(chars);
            String key = String.valueOf(chars);
            int val = map.getOrDefault(key,0);
            map.put(key,val);
        }
        Set<Map.Entry<String,Integer>> entries = map.entrySet();
        List<Map.Entry<String, Integer>> list =new ArrayList<>(entries);
        list.sort((a1,a2)->{
            String key1 = a1.getKey();
            String key2 = a2.getKey();
            Integer value1 = a1.getValue();
            Integer value2 = a2.getValue();
            if (value1 != value2){
                return value2-value1;
            }else {
                if (key1.length() != key2.length()){
                    //次数相同时，按单词长度升序排列;
                    return key1.length() - key2.length();
                }else {
                    //次数和单词长度均相同时，按字典序升序排列。
                    return key1.compareTo(key2);
                }
            }
        });
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Integer> entry : list) {
            String key = entry.getKey();
            //出现的次数
            Integer n = entry.getValue();
            //这里要注意，出现多少次，就得打印多少个，因为key只保存一个
            for (int i = 0; i < n; i++) {
                sb.append(key).append(" ");
            }
        }
        sb.deleteCharAt(sb.length() - 1);
        System.out.println(sb);
    }


    private static void sort() {
        Scanner sc = new Scanner(System.in);
        String line = sc.nextLine();
        String[] strings = line.split(" ");

        Map<String, Integer> map = new HashMap<>();
        for (String str : strings) {
            char[] chars = str.toCharArray();
            Arrays.sort(chars);
            String key = String.valueOf(chars);
            int val = map.getOrDefault(key, 0);
            map.put(key, val + 1);
        }
        Set<Map.Entry<String, Integer>> entries = map.entrySet();
        List<Map.Entry<String, Integer>> list = new ArrayList<>(entries);
        list.sort((a1, a2) -> {
            String key1 = a1.getKey();
            String key2 = a2.getKey();
            Integer value1 = a1.getValue();
            Integer value2 = a2.getValue();
            if (value1 != value2) {
                //如果次数不一样，按次数降序排序
                return value2 - value1;
            } else {
                if (key1.length() != key2.length()) {
                    //次数相同时，按单词长度升序排列;
                    return key1.length() - key2.length();
                } else {
                    //次数和单词长度均相同时，按字典序升序排列。
                    return key1.compareTo(key2);
                }
            }
        });

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Integer> entry : list) {
            String key = entry.getKey();
            //出现的次数
            Integer n = entry.getValue();
            //这里要注意，出现多少次，就得打印多少个，因为key只保存一个
            for (int i = 0; i < n; i++) {
                sb.append(key).append(" ");
            }
        }
        sb.deleteCharAt(sb.length() - 1);
        System.out.println(sb);


    }

    private static void hsCheck() {

        Scanner sc = new Scanner(System.in);
        String str = sc.nextLine();
        String[] strings = str.split(" ");
        //采集人员人数
        int nurseNum = Integer.parseInt(strings[0]);
        //志愿者人数
        int volunteerNum = Integer.parseInt(strings[1]);

        String line = sc.nextLine();
        String[] effStrArr = line.split(" ");
        //每个采集员的效率
        int[] eff = new int[nurseNum + 1];
        for (int i = 0; i < nurseNum; i++) {
            eff[i] = Integer.parseInt(effStrArr[i]);
        }
        //动态规划的方程
        int[][] dp = new int[nurseNum + 1][volunteerNum + 1];


        for (int i = 1; i <= nurseNum; i++) {
            for (int j = 0; j <= volunteerNum; j++) {
                //当前人员（第i个采集人员）的效率，因为eff是从0开始，所以要减1
                int curEff = eff[i - 1];
                int M = (int) (0.1 * curEff);
                //最开始， dp[i][j] 取值为 当前采集员(第i个采集员)，没有志愿者协助
                dp[i][j] = dp[i - 1][j] + curEff - 2 * M;
                if (j >= 1) {
                    //有 1 个志愿者协助当前采集员
                    dp[i][j] = Math.max(dp[i][j], dp[i - 1][j - 1] + curEff);
                }
                if (j >= 2) {
                    //有 2 个志愿者协助当前采集员
                    dp[i][j] = Math.max(dp[i][j], dp[i - 1][j - 2] + curEff + M);
                }
                if (j >= 3) {
                    //有 3 个志愿者协助当前采集员
                    dp[i][j] = Math.max(dp[i][j], dp[i - 1][j - 3] + curEff + 2 * M);
                }
                if (j >= 4) {
                    //有 4 个志愿者协助当前采集员
                    dp[i][j] = Math.max(dp[i][j], dp[i - 1][j - 3] + curEff + 3 * M);
                }

            }
        }

//        for (int i = 0; i <= nurseNum; i++) {
//            for (int j = 0; j <= volunteerNum; j++) {
//                System.out.print(dp[i][j] + "\t");
//            }
//            System.out.println();
//        }

        System.out.println(dp[nurseNum][volunteerNum]);

    }


    private static void duplicateCode() {
        Scanner sc = new Scanner(System.in);
        String strOne = sc.nextLine();
        String strTwo = sc.nextLine();
        int lengthOne = strOne.length();
        int lengthTwo = strTwo.length();
        String moreStr = lengthOne >= lengthTwo ? strOne : strTwo;
        String lessStr = lengthOne < lengthTwo ? strOne : strTwo;
        String maxSub = null;

        int maxLen = 0;
        for (int i = 0; i < lessStr.length(); i++) {
            for (int j = i + 1; j < lessStr.length(); j++) {
                String sub = lessStr.substring(i, j);
                if (moreStr.contains(sub)) {
                    if (sub.length() > maxLen) {
                        maxSub = sub;
                        maxLen = sub.length();
                    }
                }
            }
        }
        System.out.println(maxSub);
    }




}
