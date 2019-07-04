/***********************************************************************************************************************
 Copyright (c) Damak Mahdi.
 Github.com/damakmahdi
 damakmahdi2012@gmail.com
 linkedin.com/in/mahdi-damak-400a3b14a/
 **********************************************************************************************************************/

package com.mahdi.WebServices;

import com.google.gson.Gson;
import com.mahdi.ElasticQueries.RetrieveMeasures;
import com.mahdi.Listeners.MailTest;
import com.mahdi.Servlets.RetrieveServlet;

import javax.mail.MessagingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/*
Peaktech Data sending web service
 */
public class PeaktechData extends RetrieveServlet {
    RetrieveMeasures r ;
    public Gson g;
    public long borneInf;
    public long borneSup;
    @Override
    public void init() throws ServletException {
        super.init();
        this.r=new RetrieveMeasures();
        this.g=new Gson();
        borneInf= System.currentTimeMillis()-5000;
        borneSup= System.currentTimeMillis();

    }

    /*
GET request returns getPeakList which is a list that contains the Peaktech logs
 */
    /*
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException
    {
        r= new RetrieveMeasures();
        this.r.retrieveData("peaktechpower",r.peakList);
        res.setContentType("application/json");

        this.doWrite(r.getPeakList(),res.getWriter()); }

     */
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException
    {

        this.r.perfectRetrieve("peaktechpower",r.peakList,this.borneInf,this.borneSup);
        res.setContentType("application/json");
        if(r.getPeakList().size()>0){
            this.doWrite(r.getPeakList(), res.getWriter());
       this.doWrite(r.stats, res.getWriter());
            this.borneInf= System.currentTimeMillis()-5000;
            this.borneSup= System.currentTimeMillis();
        } else {
            try {
                MailTest.generateAndSendEmail();
                //System.exit(0);

            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }


    }
}