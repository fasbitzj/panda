package com.panda.mongodb.support.dao;

public enum Formula {
    //  ==
    EQ,

    //  !=
    NE,

    //  <
    LT,

    //  >
    GT,

    //  <=
    LE,

    //  >=
    GE,

    //    like
    LIKE,

    IN,
    BETWEEN,
    IS, // is value null or not null
    OR
}
