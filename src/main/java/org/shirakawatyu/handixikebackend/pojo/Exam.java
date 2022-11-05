package org.shirakawatyu.handixikebackend.pojo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;

@Data
@AllArgsConstructor
public class Exam {
    private String id;
    private String name;
    private String week;
    private String dateOrder;
    private String date;
    private String timeSpan;
    private String location;
    private String seat;
    private String certainLocation;


}
