package org.shirakawatyu.handixikebackend.pojo;

import lombok.Data;

import java.util.ArrayList;

@Data
public class Library {

    String bookName;
    String borrowTime ;
    String expire ;
    String location;
    public Library(){}
    public Library(Library library){

        bookName = library.getBookName();
        borrowTime = library.getBorrowTime();;
        expire = library.getExpire();
        location = library.getLocation();

    }
}
