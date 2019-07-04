/***********************************************************************************************************************
 Copyright (c) Damak Mahdi.
 Github.com/damakmahdi
 damakmahdi2012@gmail.com
 linkedin.com/in/mahdi-damak-400a3b14a/
 **********************************************************************************************************************/

package com.mahdi.Servlets;
import com.google.gson.Gson;
import com.mahdi.ElasticQueries.RetrieveMeasures;

import javax.servlet.http.*;
import javax.servlet.*;
import java.io.*;

/*
This abstract class acts as the main servlet
 */
public abstract class RetrieveServlet extends HttpServlet{

    RetrieveMeasures r ;
    public Gson g;


    @Override
    public void init() throws ServletException {
        super.init();
        this.g=new Gson();

    }
    /*
   Transform the final result into a Json Document, to be used in every Web service.
     */
    protected final void doWrite(Object object, Writer writer) throws IOException, ServletException {
        this.g.toJson(object, writer);
    }
}


