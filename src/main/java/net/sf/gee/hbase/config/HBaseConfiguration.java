/**
 * -------------------------------------------------------------------------------------------------
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
 * or implied.
 * 
 * See the License for the specific language governing permissions and limitations under the
 * License.
 * 
 */
package net.sf.gee.hbase.config;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.HashMap;
import java.util.Properties;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import net.sf.gee.hbase.annotation.HBaseColumn;
import net.sf.gee.hbase.annotation.RowKey;
import net.sf.gee.hbase.annotation.Table;
import net.sf.gee.hbase.config.xml.Mapping;
import net.sf.gee.hbase.config.xml.Prop;
import net.sf.gee.hbase.exception.ConfigurationException;
import net.sf.gee.hbase.exception.GHBaseCode;
import net.sf.gee.logger.factory.GLogFactory;
import net.sf.gee.logger.log.SimpleGLogger;

/**
 * @author Giorgio Desideri - giorgio.desideri@gmail.com
 *
 */
public class HBaseConfiguration {

  public static final String DEFAULT_CONFIG_FILE = "g-hb-mapping.xml";

  public static final String HB_HOST = "g-hb-host";

  public static final String HB_PORT = "g-hb-port";

  public static final String HB_USERNAME = "g-hb-username";

  public static final String HB_PASSWORD = "g-hb-password";

  private static final SimpleGLogger LOGGER =
      GLogFactory.getInstance().getLogger(SimpleGLogger.class, HBaseConfiguration.class);

  /**
   * Map of inspected classes
   */
  private final HashMap<String, ClassConfiguration> classes = new HashMap<>(0);

  /**
   * Map of connection data
   */
  private final HashMap<String, String> connectionData = new HashMap<>(0);

  /**
   * Map of properties
   */
  private final HashMap<String, String> properties = new HashMap<>(0);

  /**
   * Configuration file path. Default value is {@link HBaseConfiguration.DEFAULT_CONFIG_FILE}
   */
  private String configFilepath = DEFAULT_CONFIG_FILE;


  /**
   * Private constructor
   */
  private HBaseConfiguration() {
    super();
  }

  /**
   * Create a new instance of HBConfiguration.
   *
   * @return {@link HBaseConfiguration}
   */
  public static HBaseConfiguration newInstance() {
    return new HBaseConfiguration();
  }

  /**
   * Specify file path of configuration file, otherwise system will use default.
   * 
   * @param configurationFilePath file path of configuration file.
   * 
   * @return {@link HBaseConfiguration} instance updated
   */
  public HBaseConfiguration addConfigFile(String configurationFilePath) {
    configFilepath = configurationFilePath;

    return this;
  }

  /**
   * Build and finalize the current instance of HBConfiguration.
   * 
   * @return {@link HBaseConfiguration} instance built
   * 
   * @throws IOException
   */
  public HBaseConfiguration build() throws IOException {

    // load URL of configuration file
    try (InputStream in = this.getClass().getClassLoader().getResourceAsStream(configFilepath)) {

      // read configuration file
      Unmarshaller um = JAXBContext.newInstance(Mapping.class).createUnmarshaller();

      // read file
      Mapping mapping = (Mapping) um.unmarshal(in);

      if (mapping == null) {
        throw new IOException("Unmarshalling phase return NULL object");
      }

      // inspect declared classes
      for (String current : mapping.getClazz()) {

        // inspect class
        inspectClass(current);
      }

      // connection data
      if (mapping.getHost() != null) {

        // fill connection data
        connectionData.put(HB_HOST, mapping.getHost().getHost());
        connectionData.put(HB_PORT, mapping.getHost().getPort().toString());
        connectionData.put(HB_USERNAME, mapping.getHost().getUsername());
        connectionData.put(HB_PASSWORD, mapping.getHost().getPassword());
      }

      // properties
      if (mapping.getProperties() != null) {

        // iterate
        for (Prop current : mapping.getProperties().getProp()) {
          // put in map
          properties.put(current.getName().trim(), current.getValue().trim());
        }
      }

      return this;

    }
    catch (IOException e) {
      LOGGER.logError(e.getMessage(), e);

      throw e;

    }
    catch (Exception e) {
      LOGGER.logError(e.getMessage(), e);

      throw new IOException(
          "Error during reading or parsing the configuration file or configuration object.", e);
    }
  }

  /**
   * Inspect class.
   *
   * @param fullClassName full class name as {@linkplain java.lang.String}
   * 
   * @throws {@link ConfigurationException}
   */
  private void inspectClass(String fullClassName) throws ConfigurationException {

    try {
      Class<?> clazz = Class.forName(fullClassName);

      Table tableAnnotation = clazz.getDeclaredAnnotation(Table.class);

      // no annotation found
      if (tableAnnotation == null) {

        LOGGER.logInfo("No Annotation @HBTable found for class : %s", fullClassName);
        return;
      }

      // check
      if (classes.containsKey(fullClassName)) {

        LOGGER.logDebug("Class [%s] already inspected. Return.", fullClassName);

        // skip
        return;
      }

      LOGGER.logDebug("Inspect class : [%s]", fullClassName);

      // class configuration
      final ClassConfiguration classConfig =
          new ClassConfiguration(tableAnnotation.table(), tableAnnotation.namespace());

      // fields
      Field[] fields = clazz.getDeclaredFields();

      // iterate
      for (Field current : fields) {

        // get annotations
        RowKey rowKeyAnnotation = current.getDeclaredAnnotation(RowKey.class);
        HBaseColumn columnAnnotation = current.getDeclaredAnnotation(HBaseColumn.class);

        // check row key annotation
        if (rowKeyAnnotation != null) {

          final FieldConfiguration fc = new FieldConfiguration();

          // set name
          fc.setFieldName(current.getName());

          // set type
          fc.setFieldType(current.getType());

          // set row key
          classConfig.setRowKeyField(fc);

        } // check column annotation
        else if (columnAnnotation != null) {

          LOGGER.logDebug("Inspect field : [%s]", current.getName());

          // field configuration
          final FieldConfiguration fc = inspectField(current, columnAnnotation);

          // add to class configuration
          classConfig.addFieldConfiguration(fc);
        }
      }

      // add to map
      classes.put(fullClassName, classConfig);

    }
    catch (ConfigurationException e) {
      throw e;
    }
    catch (Exception e) {
      LOGGER.logError(e.getMessage(), e);

      throw ConfigurationException.build(GHBaseCode.PARSING_CLASS_ERROR, e.getMessage(), e);
    }
  }

  /**
   * Parse single field of inspectedd class.
   * 
   * @param current {@linkplain java.lang.reflection.Field} inspected field
   * @param annotation {@link HBaseColumn} annotation
   * 
   * @return {@link FieldConfiguration} field configuration
   * 
   * @throws {@link ConfigurationException}
   */
  private FieldConfiguration inspectField(Field current, HBaseColumn annotation)
      throws ConfigurationException {

    final FieldConfiguration fc = new FieldConfiguration();

    // set name
    fc.setFieldName(current.getName());
    LOGGER.logTrace("Field name [%s]", fc.getFieldName());

    // set type
    fc.setFieldType(current.getType());
    LOGGER.logTrace("Field type [%s]", fc.getFieldType().getName());

    // set family name
    fc.setFamilyName(annotation.familyName());
    LOGGER.logTrace("Family name [%s]", fc.getFamilyName());

    // set column name
    fc.setColumnName(annotation.name());
    LOGGER.logTrace("Column name [%s]", fc.getColumnName());

    // set insertable
    fc.setInsertable(annotation.insertable());
    LOGGER.logTrace("Insertable [%s]", fc.isInsertable());

    // set updatable
    fc.setUpdatable(annotation.updatable());
    LOGGER.logTrace("Updatable [%s]", fc.isUpdatable());

    // mapper
    fc.setMapper(annotation.mapper());
    LOGGER.logTrace("Mapper Class [%s]", fc.getMapper().getName());

    // check if field is a collection
    if (Collection.class.isAssignableFrom(current.getType())) {

      fc.setCollection(true);
      LOGGER.logTrace("Field is a subclass of Collection. Inspect the inner type");

      // get inner type
      ParameterizedType collectionType = (ParameterizedType) current.getGenericType();

      // get class of inner type
      Class<?> collectionTypeClass = (Class<?>) collectionType.getActualTypeArguments()[0];

      // inner type
      fc.setFieldInnerType(collectionTypeClass.getName());
      LOGGER.logTrace("Inner type [%s]", fc.getFieldInnerType());
    }

    return fc;
  }

  /**
   * Get specific class configuration.
   * 
   * @param className full class name to inspect or retrieve inspection informations.
   * 
   * @return {@link ClassConfiguration}
   */
  public ClassConfiguration getConfiguration(String className) {
    return classes.get(className);
  }

  /**
   * Get connection data
   * 
   * @param paramName name of connection parameter
   * 
   * @return {@linkplain java.lang.String} value of connection parameter
   */
  public String getConnectionData(String paramName) {
    return connectionData.get(paramName);
  }

  /**
   * Return properties.
   * 
   * @return {@linkplain java.util.Properties}
   */
  public Properties getProperties() {

    final Properties p = new Properties();

    p.putAll(properties);

    return p;
  }

}
