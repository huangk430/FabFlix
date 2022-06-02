package com.github.klefstad_teaching.cs122b.movies.data;

import java.util.Locale;

public enum MovieLimit {
    LIMIT_10("LIMIT 10 "),
    LIMIT_25("LIMIT 25 "),
    LIMIT_50("LIMIT 50 "),
    LIMIT_100("LIMIT 100 ");

    private final String sql;

    MovieLimit(String sql)
    {
        this.sql = sql;
    }

    public String toSql()
    {
        return sql;
    }

    public static MovieLimit fromInt(Integer limit)
    {
        if (limit == null)
            return LIMIT_10;

        switch (limit)
        {
            case 10:
                return LIMIT_10;
            case 25:
                return LIMIT_25;
            case 50:
                return LIMIT_50;
            case 100:
                return LIMIT_100;
            default:
                throw new RuntimeException("No MovieLimit value for: " + limit);
        }
    }

}
