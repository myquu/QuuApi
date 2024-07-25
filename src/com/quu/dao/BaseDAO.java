// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   BaseDAO.java

package com.quu.dao;

import java.sql.*;
import javax.naming.*;
import javax.sql.DataSource;

public class BaseDAO
{
	protected Connection getSkyviewDBConnection()
    {
        try
        {
            Context initial = new InitialContext();
            DataSource ds = (DataSource)initial.lookup("java:jboss/datasources/MySqlSkyviewDS");
            return ds.getConnection();
        }
        catch(NamingException ex)
        {
            System.out.println("NamingException: " + ex.getMessage());
        }
        catch(SQLException ex)
        {
            System.out.println("SQLException: " + ex.getMessage());
        }
        
        return null;
    }

    protected Connection getBusinessDBConnection()
    {
        try
        {
            Context initial = new InitialContext();
            DataSource ds = (DataSource)initial.lookup("java:jboss/datasources/MySqlBusinessDS");
            return ds.getConnection();
        }
        catch(NamingException ex)
        {
            System.out.println("NamingException: " + ex.getMessage());
        }
        catch(SQLException ex)
        {
            System.out.println("SQLException: " + ex.getMessage());
        }
        
        return null;
    }
    
    protected Connection getReportsDBConnection()
    {
        try
        {
            Context initial = new InitialContext();
            DataSource ds = (DataSource)initial.lookup("java:jboss/datasources/MySqlReportsDS");
            return ds.getConnection();
        }
        catch(NamingException ex)
        {
            System.out.println("NamingException: " + ex.getMessage());
        }
        catch(SQLException ex)
        {
            System.out.println("SQLException: " + ex.getMessage());
        }
        
        return null;
    }
    
    protected void closeAll(Statement st, Connection conn)
    {
        try
        {
            if(st != null)
                st.close();
            if(conn != null)
                conn.close();
        }
        catch(SQLException ex)
        {
            System.out.println(ex.getMessage());
        }
    }
}
