package org.shirakawatyu.handixikebackend.utils;

import org.shirakawatyu.handixikebackend.pojo.Score;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Queue;

public class ScoreUtils {
    /**
     * 必修课成绩过滤器
     * @param scores 成绩字符串队列
     * @param target 要放入成绩的map
     * @author ShirakawaTyu
     */
    public static void requiredScoreFilter(Queue<String> scores, LinkedHashMap<String, ArrayList<Object>> target) {
        String next;
        if (!scores.isEmpty() && scores.peek().contains("学年"))
            next = scores.poll();
        else
            return;
        while (!scores.isEmpty()) {
            String term = scores.poll();
            while (term.equals("春") || term.equals("秋")) {
                ArrayList<Object> scoreList = new ArrayList<>();
                String title1, course;

                if (term.equals("秋")) title1 = next.split(" ")[0] + "-1";
                else title1 = next.split(" ")[0] + "-2";

                if (!scores.isEmpty()) course = scores.poll();
                else break;
                while (!course.contains("平均学分绩点")) {
                    if (!scores.isEmpty()) course = scores.poll();
                    else break;

                    String[] s = course.split(" ");
                    if (s.length == 6) {
                        scoreList.add(new Score(s[0], s[2], s[3], s[4]));
                    } else if (s.length == 7){
                        scoreList.add(new Score(s[0], s[2], s[3], s[5]));
                    }
                }
                String[] s = course.split(" ");
                scoreList.add(new Score("【" + s[0], s[1], s[2], s[3]));
                target.put(title1, scoreList);
                if (!scores.isEmpty() && (scores.peek().equals("春") || scores.peek().equals("秋")))
                    term = scores.poll();
                else
                    break;
            }
            if (!scores.isEmpty() && scores.peek().contains("学年"))
                next = scores.poll();
            else
                break;
        }
    }

    /**
     * 选修课及体育课成绩过滤器
     * @param scores 成绩字符串队列
     * @param target 要放入成绩的map
     * @author ShirakawaTyu
     */
    public static void optionalScoreFilter(Queue<String> scores, LinkedHashMap<String, ArrayList<Object>> target) {
        String next;
        if (!scores.isEmpty() && scores.peek().equals("学期 课程 课程号 学分 正考 补考 绩点"))
            scores.poll();
        else
            return;
        while (!scores.isEmpty()) {
            while (!scores.isEmpty()) {
                next = scores.poll();
                if (next.contains("已获得学分")) break;
                String[] s = next.split(" ");
                ArrayList<Object> scoreList = target.computeIfAbsent(s[0], k -> new ArrayList<>());
                if (s.length == 6) {
                    ArrayUtils.addSecondLast(scoreList, new Score(s[1], s[3], "其他", s[4]));
                } else if (s.length == 7) {
                    ArrayUtils.addSecondLast(scoreList, new Score(s[1], s[3], "其他", s[5]));
                }
            }
            if (!scores.isEmpty() && scores.peek().equals("学期 课程 课程号 学分 正考 补考 绩点"))
                scores.poll();
            else
                break;
        }
    }

    /**
     * CET成绩过滤器
     * @param scores 成绩字符串队列
     * @param target 要放入成绩的map
     * @author ShirakawaTyu
     */
    public static void cetScoreFilter(Queue<String> scores, LinkedHashMap<String, ArrayList<Object>> target) {
        if (!scores.isEmpty() && scores.peek().equals("准考证号 考试场次 语言级别 总分 听力 阅读 写作 综合"))
            scores.poll();
        else
            return;
        while (!scores.isEmpty()) {
            ArrayList<Object> objects = new ArrayList<>();
            while (!scores.isEmpty() && scores.peek().contains("CET")) {
                String[] s = scores.poll().split(" ");
                objects.add(new Score(s[4], "0", "其他", s[5]));
            }
            target.put("外语等级考试", objects);
        }
    }
}
