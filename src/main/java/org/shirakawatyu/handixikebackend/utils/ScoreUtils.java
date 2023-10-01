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
    public static void requiredScoreFilter(Queue<String> scores, LinkedHashMap<String, ArrayList<Score>> target) {
        String next;
        if (!scores.isEmpty() && scores.peek().contains("学年")) {
            next = scores.poll();
        } else {
            return;
        }
        while (!scores.isEmpty()) {
            String term = scores.poll();
            while ("春".equals(term) || "秋".equals(term)) {
                ArrayList<Score> scoreList = new ArrayList<>();
                String title1, course;
                if ("秋".equals(term)) {
                    title1 = next.split(" ")[0] + "-1";
                } else {
                    title1 = next.split(" ")[0] + "-2";
                }
                if (!scores.isEmpty()) {
                    course = scores.poll();
                } else {
                    break;
                }
                while (!course.contains("平均学分绩点")) {
                    if (!scores.isEmpty()) {
                        course = scores.poll();
                    } else {
                        break;
                    }
                    String[] s = course.split(" ");
                    if (s.length == 6 || s.length == 5) {
                        scoreList.add(new Score(s[0], s[2], s[3], s[4]));
                    } else if (s.length == 7){
                        scoreList.add(new Score(s[0], s[2], s[3], s[5]));
                    }
                }
                String[] s = course.split(" ");
                scoreList.add(new Score("【" + s[0], s[1], s[2], s[3]));
                target.put(title1, scoreList);
                if (!scores.isEmpty() && ("春".equals(scores.peek()) || "秋".equals(scores.peek()))) {
                    term = scores.poll();
                } else {
                    break;
                }
            }
            if (!scores.isEmpty() && scores.peek().contains("学年")) {
                next = scores.poll();
            } else {
                break;
            }
        }
    }

    /**
     * 选修课及体育课成绩过滤器
     * @param scores 成绩字符串队列
     * @param target 要放入成绩的map
     * @author ShirakawaTyu
     */
    public static void optionalScoreFilter(Queue<String> scores, LinkedHashMap<String, ArrayList<Score>> target) {
        String next;
        if (!scores.isEmpty() && "学期 课程 课程号 学分 正考 补考 绩点".equals(scores.peek())) {
            scores.poll();
        } else {
            return;
        }
        while (!scores.isEmpty()) {
            while (!scores.isEmpty()) {
                next = scores.poll();
                if (next.contains("已获得学分")) {
                    break;
                }
                String[] s = next.split(" ");
                ArrayList<Score> scoreList = target.computeIfAbsent(s[0], k -> new ArrayList<>());
                if (s.length == 6) {
                    ArrayUtils.addSecondLast(scoreList, new Score(s[1], s[3], "其他", s[4]));
                } else if (s.length == 7) {
                    ArrayUtils.addSecondLast(scoreList, new Score(s[1], s[3], "其他", s[5]));
                }
            }
            if (!scores.isEmpty() && "学期 课程 课程号 学分 正考 补考 绩点".equals(scores.peek())) {
                scores.poll();
            } else {
                break;
            }
        }
    }

    /**
     * CET成绩过滤器
     * @param scores 成绩字符串队列
     * @param target 要放入成绩的map
     * @author ShirakawaTyu
     */
    public static void cetScoreFilter(Queue<String> scores, LinkedHashMap<String, ArrayList<Score>> target) {
        if (!scores.isEmpty() && "准考证号 考试场次 语言级别 总分 听力 阅读 写作 综合".equals(scores.peek())) {
            scores.poll();
        } else {
            return;
        }
        ArrayList<Score> objects = new ArrayList<>();
        while (!scores.isEmpty() && (scores.peek().contains("CET") || scores.peek().contains("CJT"))) {
            String[] s = scores.poll().split(" ");
            objects.add(new Score(s[4], "0", "其他", s[5]));
        }
        target.put("外语等级考试", objects);
    }
}
