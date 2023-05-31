package org.shirakawatyu.handixikebackend.pojo;


public class GradePointAverage {
    // 平均学分绩点
    private double all;
    // 必修课平均学分绩点
    private double required;

    public GradePointAverage() {
    }

    public GradePointAverage(double all, double required) {
        this.all = all;
        this.required = required;
    }

    public double getAll() {
        return all;
    }

    public void setAll(double all) {
        this.all = all;
    }

    public double getRequired() {
        return required;
    }

    public void setRequired(double required) {
        this.required = required;
    }
}
