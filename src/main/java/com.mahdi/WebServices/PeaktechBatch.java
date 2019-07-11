/***********************************************************************************************************************
 Copyright (c) Damak Mahdi.
 Github.com/damakmahdi
 damakmahdi2012@gmail.com
 linkedin.com/in/mahdi-damak-400a3b14a/
 **********************************************************************************************************************/

package com.mahdi.WebServices;

import com.google.gson.Gson;
import com.mahdi.ElasticQueries.RetrieveMeasures;
import com.mahdi.Servlets.RetrieveServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/*
Peaktech Data sending web service
 */
public class PeaktechBatch extends RetrieveServlet {
    RetrieveMeasures r ;
    public Gson g;
    public long borneInf;
    public long borneSup;
    @Override
    public void init() throws ServletException {
        super.init();
        this.r=new RetrieveMeasures();
        this.g=new Gson();


    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException
    {

        this.r.perfectRetrieve("pkkk",r.peakList,System.currentTimeMillis()-10000,System.currentTimeMillis());
        res.setContentType("application/json");
        if(r.getPeakList().size()>0){
            this.doWrite(r.getPeakList(), res.getWriter());
        } else {
            /*
            try {
              /  MailTest.generateAndSendEmail();
                //System.exit(0);

            } catch (MessagingException e) {
                e.printStackTrace();
            }

             */
        }


    }
}