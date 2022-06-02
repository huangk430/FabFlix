package com.github.klefstad_teaching.cs122b.movies.data;

import java.util.Locale;

public enum MovieOrderBy
{
    TITLE(" ORDER BY m.title "),
    RATING(" ORDER BY m.rating "),
    YEAR(" ORDER BY m.year ");

    private final String sql;

    MovieOrderBy(String sql)
    {
        this.sql = sql;
    }

    public String toSql()
    {
        return sql;
    }

    public static MovieOrderBy fromString(String orderBy)
    {
        if (orderBy == null)
            return TITLE;

        switch (orderBy.toUpperCase(Locale.ROOT))
        {
            case "TITLE":
                return TITLE;
            case "RATING":
                return RATING;
            case "YEAR":
                return YEAR;
            default:
                throw new RuntimeException("No MovieOrderBy value for: " + orderBy);
        }
    }
}
