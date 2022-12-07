package org.shirakawatyu.handixikebackend.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Score {
    String course;
    String credit;
    String catalog;
    String scroll;
}
