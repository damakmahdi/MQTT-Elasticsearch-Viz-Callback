/***********************************************************************************************************************
 Copyright (c) Damak Mahdi.
 Github.com/damakmahdi
 damakmahdi2012@gmail.com
 linkedin.com/in/mahdi-damak-400a3b14a/
 **********************************************************************************************************************/

package com.mahdi.Utils;

import org.apache.spark.SparkContext;
import org.apache.spark.sql.SQLContext;

public class JavaSQLContextSingleton {
    private static transient SQLContext instance = null;
    public static SQLContext getInstance(SparkContext sparkContext) {
        if (instance == null) {
            instance = new SQLContext(sparkContext);
        }
        return instance;
    }
}