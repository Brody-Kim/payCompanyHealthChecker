package com.finance.healthchecker.comm.db;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;


public class DBConnection {
    static private DBConnection instance;       // singleton
    static private int clients;

    private Vector drivers = new Vector();
    private PrintWriter log;
    private Hashtable pools = new Hashtable();
    public static String propertyFile;

    /**
     * Singletone | Double-Checked Locking
     **************************************************************************/
    public static DBConnection getInstance() {
//log("PGDBConnection.getInstance()");
      if (instance == null) {
        synchronized(DBConnection.class) {
            if (instance == null) {
                instance = new DBConnection();
            }
        }
      }
      clients++;
      return instance;
    }

    /**
     *
     **************************************************************************/
    private DBConnection() {
//log("PGDBConnection.PGDBConnection()");
      init();
    }

    /**
     *
     **************************************************************************/
    public void freeConnection(String name, Connection con) {
//log("PGDBConnection.freeConnection()");
      DBConnectionPool pool = (DBConnectionPool) pools.get(name);
      if (pool != null) {
          pool.freeConnection(con);
      }
    }

    /**
     *
     **************************************************************************/
    public Connection getConnection(String name) {
//log("PGDBConnection.getConnection()");
        DBConnectionPool pool = (DBConnectionPool) pools.get(name);
        if (pool != null) {
            return pool.getConnection();
        }
        return null;
    }

    /**
     *
     **************************************************************************/
    public Connection getConnection(String name, long time) {
//log("PGDBConnection.getConnection()");
        DBConnectionPool pool = (DBConnectionPool) pools.get(name);
        if (pool != null) {
            return pool.getConnection(time);
        }
        return null;
    }

    /**
     *
     **************************************************************************/
    public synchronized void release() {
        // Wait until called by the last client
        if (--clients != 0) {
            return;
        }

        Enumeration allPools = pools.elements();
        while (allPools.hasMoreElements()) {
            DBConnectionPool pool = (DBConnectionPool) allPools.nextElement();
            pool.release();
        }
        Enumeration allDrivers = drivers.elements();
        while (allDrivers.hasMoreElements()) {
            Driver driver = (Driver) allDrivers.nextElement();
            try {
                DriverManager.deregisterDriver(driver);
                //log("Deregistered JDBC driver " + driver.getClass().getName());
            }
            catch (SQLException e) {
                log(e, "Can't deregister JDBC driver: " + driver.getClass().getName());
            }
        }
    }

    /**
     *
     **************************************************************************/
    private void createPools(Properties props) {
//log("PGDBConnection.createPool()");
        Enumeration propNames = props.propertyNames();
        while (propNames.hasMoreElements()) {
            String name = (String) propNames.nextElement();
            if (name.endsWith(".url")) {
                String poolName = name.substring(0, name.lastIndexOf("."));
                String url = props.getProperty(poolName + ".url");
                log("url " + url);                
                if (url == null) {
                    log("No URL specified for " + poolName);
                    continue;
                }
                String user = props.getProperty(poolName + ".user");
                String password = props.getProperty(poolName + ".password");
                String maxconn = props.getProperty(poolName + ".maxconn", "5");

                int max;
                try {
                    max = Integer.valueOf(maxconn).intValue();
                }
                catch (NumberFormatException e) {
                    log("Invalid maxconn value " + maxconn + " for " + poolName);
                    max = 0;
                }
                DBConnectionPool pool =
                    new DBConnectionPool(poolName, url, user, password, max);
                pools.put(poolName, pool);
                //log("Initialized pool " + poolName);
            }
        }
    }

    private void createPools() {
        String poolName = "mysql";

        String url = System.getenv("CONF_CORE_DB_HOST");
        String user = System.getenv("CONF_CORE_DB_USERNAME");
        String password = System.getenv("CONF_CORE_DB_PASSWORD");
        int max = Integer.parseInt(System.getenv("CONF_CORE_DB_MAX_CONN"));

        DBConnectionPool pool = new DBConnectionPool(poolName, url, user, password, max);
        pools.put(poolName, pool);

    }

    private void init(){

        /*
        String db_resource = SystemProperty.property_file_path;
        System.out.println("prop:" + db_resource);

        //InputStream is = getClass().getResourceAsStream(db_resource);
        InputStream is;
        Properties dbProps = new Properties();
        try {

          is = new FileInputStream(db_resource);
          dbProps.load(is);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Can't read the properties file. " +
                "Make sure db.properties is in the CLASSPATH");
            return;
        }

        loadDrivers(dbProps);
        createPools(dbProps);
        */

        loadDrivers();
        createPools();
    }

    /**
     *
     **************************************************************************/
    private void loadDrivers() {

        //String driverClasses = props.getProperty("drivers");
        String driverClasses = "com.mysql.cj.jdbc.Driver";

        StringTokenizer st = new StringTokenizer(driverClasses);
        while (st.hasMoreElements()) {
            String driverClassName = st.nextToken().trim();
            try {
                Driver driver = (Driver)
                    Class.forName(driverClassName).newInstance();
                DriverManager.registerDriver(driver);
                drivers.addElement(driver);
                //log("Registered JDBC driver " + driverClassName);
            }
            catch (Exception e) {
                log("Can't register JDBC driver: " + driverClassName + ", Exception: " + e);
            }
        }
    }

    /**
     *
     **************************************************************************/
    private void log(String msg) {
        //log.println(new Date() + ": " + msg);
        System.out.println(new Date() + ": " + msg);
    }

    /**
     *
     **************************************************************************/
    private void log(Throwable e, String msg) {
        //log.println(new Date() + ": " + msg);
        System.out.println(new Date() + ": " + msg);
        e.printStackTrace(log);
    }


    class DBConnectionPool {
        private int checkedOut;
        private Vector freeConnections = new Vector();
        private int maxConn;
        private String name;
        private String password;
        private String URL;
        private String user;

        /**
         *
         **********************************************************************/
        public DBConnectionPool(String name, String URL, String user, String password,
                int maxConn) {
//log("PGDBConnection.DBConnectionPool().DBConnectionPool");
            this.name = name;
            this.URL = URL;
            this.user = user;
            this.password = password;
            this.maxConn = maxConn;
        }

        /**
         *
         **********************************************************************/
        public synchronized void freeConnection(Connection con) {
            // Put the connection at the end of the Vector
            freeConnections.addElement(con);
            checkedOut--;
            notifyAll();
        }

        /**
         *
         **********************************************************************/
        public synchronized Connection getConnection() {
//log("DBConnectionManager.DBConnectionPool.getConnection()");
            Connection con = null;
            if (freeConnections.size() > 0) {
                // Pick the first Connection in the Vector
                // to get round-robin usage
                con = (Connection) freeConnections.firstElement();
                freeConnections.removeElementAt(0);
                try {
                    if (con.isClosed()) {
                        log("Removed bad connection from[0] " + name);
                        // Try again recursively
                        con = getConnection();
                    }
                }
                catch (SQLException e) {
                    log("Removed bad connection from[1] " + name);
                    // Try again recursively
                    con = getConnection();
                }
            }
            else if (maxConn == 0 || checkedOut < maxConn) {
                con = newConnection();
            }
            if (con != null) {
                checkedOut++;
            }
            return con;
        }

        /**
         *
         **********************************************************************/
        public synchronized Connection getConnection(long timeout) {
//log("PGDBConnection.DBConnectionPool.getConnection()");
            long startTime = new Date().getTime();
            Connection con;
            while ((con = getConnection()) == null) {
                try {
                    wait(timeout);
                }
                catch (InterruptedException e) {}
                if ((new Date().getTime() - startTime) >= timeout) {
                    // Timeout has expired
                    return null;
                }
            }
            return con;
        }

        /**
         *
         **********************************************************************/
        public synchronized void release() {
            Enumeration allConnections = freeConnections.elements();
            while (allConnections.hasMoreElements()) {
                Connection con = (Connection) allConnections.nextElement();
                try {
                    con.close();
                    //log("Closed connection for pool " + name);
                }
                catch (SQLException e) {
                    log(e, "Can't close connection for pool " + name);
                }
            }
            freeConnections.removeAllElements();
        }

        /**syncronize
         *
         **********************************************************************/
        private Connection newConnection() {
//log("PGDBConnection.DBConnectionPool.newConnection()");
            Connection con = null;
            try {
                if (user == null) {
                    con = DriverManager.getConnection(URL);
                }
                else {
                    con = DriverManager.getConnection(URL, user, password);
                }
                //log("Created a new connection in pool " + name);
            }
            catch (SQLException e) {
                log(e, "Can't create a new connection for " + URL);
                return null;
            }
            return con;
        }
    }
}


