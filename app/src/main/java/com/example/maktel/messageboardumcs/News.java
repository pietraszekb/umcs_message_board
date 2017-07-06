package com.example.maktel.messageboardumcs;

import android.media.tv.TvContract;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by maktel on 08.05.17.
 */

class News implements Serializable {
    static final long serialVersionUID = 1L;
    String title;
    String text;
    Date date;
    String author;

    News(String title, String text, Date date, String author) {
        this("", "", new Date(0), "", NewsType.NEWS);
    }

    News(String title, String text, Date date, String author, NewsType newsType) {
        this.title = title;
        this.text = text;
        this.date = date;
        this.author = author;

        this.newsType = newsType;
        this.logoDrawable = getLogoDrawable(author);
    }

    News() {
        this("", "", new Date(0), "", NewsType.PLACEHOLDER);
    }


    enum NewsType {PLACEHOLDER, NEWS, FACT}
    private NewsType newsType;


    private enum LogoType {UMCS, SKNI, MICROSOFT}
    private LogoType logoType;
    int logoDrawable;

    private int getLogoDrawable(String author) {
        String[] authors = {"Anonymous", "Asemblerowy Świrek", "Mark Russinovich"};
        int i;
        for (i = 0; i < authors.length; ++i) if (authors[i].equals(author)) break;

        switch (i) {
            case 0:
                this.logoType = LogoType.UMCS;
                return R.drawable.logo_umcs;
            case 1:
                this.logoType = LogoType.SKNI;
                return R.drawable.logo_umcs;
            case 2:
//                return LogoType.MICROSOFT;

            default:
                this.logoType = LogoType.UMCS;
                return R.drawable.logo_umcs;
        }
    }
}
