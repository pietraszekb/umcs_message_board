package com.example.maktel.messageboardumcs;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by maktel on 16.05.17.
 */

public class NewsArrayList extends ArrayList<News> implements Serializable {
    static final long serialVersionUID = 2L;

    void setOrAdd(int index, News element) {
        try {
            super.set(index, element);
        } catch (IndexOutOfBoundsException e) {
            super.add(element);
        }
    }
}