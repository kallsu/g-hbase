/**
 * ------------------------------------------------------------------------------------------------
 *
 * Copyright 2015 - Giorgio Desideri
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 * 
 **/
package net.sf.gee.hbase.core;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.ConnectionFactory;

import net.sf.gee.common.util.string.StringUtil;
import net.sf.gee.hbase.config.ClassConfiguration;
import net.sf.gee.hbase.config.HBaseConfiguration;
import net.sf.gee.logger.factory.GLogFactory;
import net.sf.gee.logger.log.SimpleGLogger;

/**
 * @author Giorgio Desideri - giorgio.desideri@gmail.com
 *
 */
/**
 * @author Giorgio Desideri - giorgio.desideri@gmail.com
 *
 */
public class HBaseConnectionFactory {

  private static final SimpleGLogger LOGGER =
      GLogFactory.getInstance().getLogger(SimpleGLogger.class, HBaseConfiguration.class);

  /** Monitor object */
  private static final Object MONITOR = new Object();

  /** Singleton instance */
  private static HBaseConnectionFactory instance = null;

  /** HBase configuration */
  private Configuration hbaseConfiguration = null;

  /** HBase Entities configuration */
  private HBaseConfiguration gHbaseConfiguration = null;

  private int openedConnection = 0;

  private int closedConnection = 0;

  /**
   * Private constructor as singleton pattern
   * 
   * @param configFilePath configuration file path
   */
  private HBaseConnectionFactory(String configFilePath) {
    super();

    init(configFilePath);
  }

  /**
   * Init singleton
   * 
   * @param configFilePath configuration file path
   */
  private void init(String configFilePath) {

    try {
      // check configuration file path
      if (StringUtil.isEmpty(configFilePath)) {

        // build with default parameters
        gHbaseConfiguration = HBaseConfiguration.newInstance().build();
      }
      else {
        // build with custom parameters
        gHbaseConfiguration =
            HBaseConfiguration.newInstance().addConfigFile(configFilePath).build();
      }

      final String host = gHbaseConfiguration.getConnectionData(HBaseConfiguration.HB_HOST);
      final String port = gHbaseConfiguration.getConnectionData(HBaseConfiguration.HB_PORT);

      // create configuration configuration
      hbaseConfiguration = org.apache.hadoop.hbase.HBaseConfiguration.create();

      // set host for quorum = master
      hbaseConfiguration.set("hbase.zookeeper.quorum", host);

      // set master
      hbaseConfiguration.set("hbase.master", host + ":" + port);

      // set port of zookeeper
      hbaseConfiguration.set("hbase.zookeeper.property.clientport", port);

    }
    catch (IOException e) {
      LOGGER.logError(e.getMessage(), e);
    }

  }

  /**
   * Get singleton instance
   * 
   * @param configFilePath configuration file path
   * 
   * @return <code>this</code> singleton instance
   */
  public static HBaseConnectionFactory getInstance(String configFilePath) {

    // check
    if (instance == null) {

      // monitor for concurrency
      synchronized (MONITOR) {

        // double check
        if (instance == null) {
          instance = new HBaseConnectionFactory(configFilePath);
        }
      }
    }

    return instance;
  }

  /**
   * Get singleton instance
   * 
   * @return <code>this</code> singleton instance
   */
  public static HBaseConnectionFactory getInstance() {

    // check
    if (instance == null) {

      // monitor for concurrency
      synchronized (MONITOR) {

        // double check
        if (instance == null) {
          instance = new HBaseConnectionFactory(null);
        }
      }
    }

    return instance;
  }

  /**
   * Open new connection.
   * 
   * @return {@link HBaseConnection}
   */
  public HBaseConnection openConnection() {

    HBaseConnection newConnection = null;

    try {
      // create new connection
      newConnection = new HBaseConnection(ConnectionFactory.createConnection(hbaseConfiguration));

      // add open connection
      openedConnection++;

      // log
      LOGGER.logInfo("Connection stats: opened [%s], closed [%s], delta [%s]", openedConnection,
          closedConnection, (openedConnection - closedConnection));

    }
    catch (Exception e) {
      LOGGER.logError(e.getMessage(), e);
    }

    return newConnection;
  }

  /**
   * Close connection.
   * 
   * @param conn {@link HBaseConnection}
   */
  public void closeConnection(HBaseConnection conn) {

    try {
      // close connection
      conn.close();

      // add open connection
      closedConnection++;

      // log
      LOGGER.logInfo("Connection stats: opened [%s], closed [%s], delta [%s]", openedConnection,
          closedConnection, (openedConnection - closedConnection));

    }
    catch (Exception e) {
      LOGGER.logError(e.getMessage(), e);
    }
  }

  /**
   * Gets class configuration.
   * 
   * @param className class name Class<?>.getName()
   * 
   * @return {@link net.sf.gee.hbase.config.ClassConfiguration}
   */
  public ClassConfiguration getClassConfiguration(String className) {

    // monitor
    synchronized (gHbaseConfiguration) {

      // return class configuration
      return gHbaseConfiguration.getConfiguration(className);
    }
  }

}
