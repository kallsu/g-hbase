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
package net.sf.gee.hbase.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.management.OperationsException;

import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;

import net.sf.gee.common.util.reflection.ReflectionUtil;
import net.sf.gee.hbase.basic.HBaseMapper;
import net.sf.gee.hbase.basic.NoneMapper;
import net.sf.gee.hbase.config.ClassConfiguration;
import net.sf.gee.hbase.config.FieldConfiguration;
import net.sf.gee.hbase.core.HBaseConnectionFactory;
import net.sf.gee.logger.factory.GLogFactory;
import net.sf.gee.logger.log.SimpleGLogger;

/**
 * @author Giorgio Desideri - giorgio.desideri@gmail.com
 *
 */
public final class GHBaseObjectBuilder<T> implements Serializable {

  private static final long serialVersionUID = 1L;

  private final static SimpleGLogger LOGGER =
      GLogFactory.getInstance().getLogger(SimpleGLogger.class, GHBaseObjectBuilder.class);

  private Class<T> objectClass = null;

  private T object = null;

  private ClassConfiguration classConfig = null;

  /**
   * Creaete a new instance of GHBaseObjectBuilder using the class of <T> type.
   * 
   * @param objectClass {@linkplain java.lang.Class} of <T>
   */
  public GHBaseObjectBuilder(Class<T> objectClass) {
    super();

    // object class
    this.objectClass = objectClass;

    // get class configuration
    this.classConfig =
        HBaseConnectionFactory.getInstance().getClassConfiguration(objectClass.getName());
  }

  /**
   * Create a new instance of GHBaseObjectBuilder with class and own row key value
   * 
   * @param objectClass
   * @param rowKeyValue
   */
  public GHBaseObjectBuilder(Class<T> objectClass, byte[] rowKeyValue) {
    this(objectClass);

    // build object
    buildObject();

    // set row key value
    ReflectionUtil.setValue(object, classConfig.getRowKeyField().getFieldName(),
        toObject(String.class, rowKeyValue));
  }

  /**
   * Creaete a new instance of GHBaseObjectBuilder using the object of <T> type.
   * 
   * @param object not null input object
   */
  @SuppressWarnings("unchecked")
  public GHBaseObjectBuilder(T object) {
    super();

    this.object = object;
    this.objectClass = (Class<T>) object.getClass();

    // get class configuration
    this.classConfig =
        HBaseConnectionFactory.getInstance().getClassConfiguration(objectClass.getName());
  }

  /**
   * Build object if it is null.
   * 
   * @param params constructor parameters
   */
  private void buildObject(Object... params) {

    // constructor with object
    if (object == null && params != null && params.length > 0) {

      Class<?>[] constructTypes = new Class<?>[params.length];

      for (int i = 0; i < params.length; i++) {
        constructTypes[i] = constructTypes[i].getClass();
      }

      // init object
      try {
        object = objectClass.getDeclaredConstructor(constructTypes).newInstance(params);
      }
      catch (InstantiationException | IllegalAccessException | IllegalArgumentException
          | InvocationTargetException | NoSuchMethodException | SecurityException e) {
        LOGGER.logError(e);
      }

    } // single constructor
    else if (object == null) {
      try {
        // init object
        object = objectClass.getDeclaredConstructor().newInstance();
      }
      catch (InstantiationException | IllegalAccessException | IllegalArgumentException
          | InvocationTargetException | NoSuchMethodException | SecurityException e) {
        LOGGER.logError(e);
      }
    }
  }

  /**
   * Init collection field.
   * 
   * @param fieldConfig field configuration{@link FieldConfiguration}
   * 
   * @return {@linkplain java.util.Collection<Object>}
   * 
   * @throws {@linkplain java.lang.Exception}
   */
  @SuppressWarnings("unchecked")
  private Collection<Object> buildCollectionField(FieldConfiguration fieldConfig) throws Exception {

    // get field
    final Field field = objectClass.getDeclaredField(fieldConfig.getFieldName());
    field.setAccessible(true);

    // cast to collection
    Collection<Object> collection = (Collection<Object>) field.get(object);

    // list
    if (collection == null && List.class.isAssignableFrom(fieldConfig.getFieldType())) {

      // user arraylist
      collection = new ArrayList<>(0);

    } // set
    else if (collection == null && Set.class.isAssignableFrom(fieldConfig.getFieldType())) {

      // use hashset
      collection = new HashSet<>(0);
    }

    // set value
    field.set(object, collection);

    return collection;
  }

  /**
   * Convert the value in the proper object.
   * 
   * @param valueClass return class type {@linkplain java.lang.Class}
   * @param value byte[] value
   * 
   * @return object value casted to valueClass.
   */
  private <K> K toObject(Class<K> valueClass, byte[] value) {

    if (value == null) {
      return null;
    }

    // string
    if (String.class.getName().equals(valueClass.getName())) {
      return valueClass.cast(Bytes.toString(value));

    } // date
    else if (Date.class.getName().equals(valueClass.getName())) {
      long time = Bytes.toLong(value);

      return valueClass.cast(new Date(time));

    } // boolean
    else if (Boolean.class.getName().equals(valueClass.getName())) {

      return valueClass.cast(Bytes.toBoolean(value));

    } // integer
    else if (Integer.class.getName().equals(valueClass.getName())) {

      return valueClass.cast(Bytes.toInt(value));

    } // long
    else if (Long.class.getName().equals(valueClass.getName())) {

      return valueClass.cast(Bytes.toLong(value));

    } // double
    else if (Double.class.getName().equals(valueClass.getName())) {

      return valueClass.cast(Bytes.toDouble(value));

    } // bigdecimal
    else if (BigDecimal.class.getName().equals(valueClass.getName())) {

      return valueClass.cast(Bytes.toBigDecimal(value));

    } // byte array
    else if (valueClass == byte[].class) {

      return valueClass.cast(value);

    } // try to serializable
    else {

      // open streams
      try (ByteArrayInputStream bin = new ByteArrayInputStream(value);
          ObjectInputStream in = new ObjectInputStream(bin);) {

        // write object
        return valueClass.cast(in.readObject());
      }
      catch (Exception e) {
        LOGGER.logError(e.getMessage(), e);
      }
    }

    return null;
  }

  /**
   * Convert input in byte array ready to use for HBase
   * 
   * @param input {@linkplain java.lang.Object}
   * 
   * @return byte[]
   */
  private byte[] toByteArray(Object input) {

    if (input == null) {
      return null;
    }

    // prepare buffer
    byte[] value = null;

    // string
    if (String.class.getName().equals(input.getClass().getName())) {
      value = Bytes.toBytes((String) input);

    } // date
    else if (Date.class.getName().equals(input.getClass().getName())) {
      value = Bytes.toBytes(((Date) input).getTime());

    } // boolean
    else if (Boolean.class.getName().equals(input.getClass().getName())
        || boolean.class.equals(input.getClass())) {
      value = Bytes.toBytes(((Boolean) input).booleanValue());

    } // integer
    else if (Integer.class.getName().equals(input.getClass().getName())
        || int.class.equals(input.getClass())) {
      value = Bytes.toBytes(((Integer) input).intValue());

    } // long
    else if (Long.class.getName().equals(input.getClass().getName())
        || long.class.equals(input.getClass())) {
      value = Bytes.toBytes(((Long) input).longValue());

    } // double
    else if (Double.class.getName().equals(input.getClass().getName())
        || double.class.equals(input.getClass())) {
      value = Bytes.toBytes(((Double) input).doubleValue());

    } // bigdecimal
    else if (BigDecimal.class.getName().equals(input.getClass().getName())) {
      value = Bytes.toBytes((BigDecimal) input);

    } // biginteger
    else if (BigInteger.class.getName().equals(input.getClass().getName())) {
      value = Bytes.toBytes(((BigInteger) input).doubleValue());

    } // byte array
    else if (input.getClass() == byte[].class) {
      value = (byte[]) input;

    } // try to serializable
    else {

      // open streams
      try (ByteArrayOutputStream bout = new ByteArrayOutputStream();
          ObjectOutputStream out = new ObjectOutputStream(bout);) {

        // write object
        out.writeObject(input);

        // get byte array
        value = bout.toByteArray();
      }
      catch (Exception e) {
        LOGGER.logError(e.getMessage(), e);
      }
    }

    return value;
  }

  /**
   * Set value inside object to return.
   * 
   * @param family family name of cell
   * @param column column name of cell
   * @param value value of cell
   * 
   * @throws {@link OperationsException}
   */
  @SuppressWarnings({"rawtypes", "unchecked"})
  public void setValue(final String family, final String column, final byte[] value)
      throws OperationsException {

    final FieldConfiguration fieldConfig = classConfig.getFieldConfiguration(family, column);

    // check configuration
    if (fieldConfig == null) {
      throw new OperationsException(String
          .format("No field configuration found for Family [%s], Column [%s]", family, column));
    }

    // build object of the builder
    buildObject();

    try {

      // No collection and NoneMapper
      if (!fieldConfig.isCollection() && NoneMapper.class.equals(fieldConfig.getMapper())) {

        // set field value
        ReflectionUtil.setValue(object, fieldConfig.getFieldName(),
            toObject(fieldConfig.getFieldType(), value));

      } // No collection and user mapper
      else if (!fieldConfig.isCollection()) {

        // call mapper
        final HBaseMapper mapper = fieldConfig.getMapper().getDeclaredConstructor().newInstance();

        // set field value
        ReflectionUtil.setValue(object, fieldConfig.getFieldName(),
            mapper.toObject(column, value, fieldConfig.getFieldType()));

      } // Collection and NoneMapper
      else if (fieldConfig.isCollection() && NoneMapper.class.equals(fieldConfig.getMapper())) {

        // init collection
        Collection<Object> collection = buildCollectionField(fieldConfig);

        // get class of inner type of collection
        Class<?> innerTypeClass = Class.forName(fieldConfig.getFieldInnerType());

        byte[] columnAsValue =
            Bytes.toBytes(column.substring(fieldConfig.getColumnName().length()));

        // add to collection
        collection.add(toObject(innerTypeClass, columnAsValue));

      } // Collection and custom mapper
      else if (fieldConfig.isCollection()) {

        // init collection
        Collection<Object> collection = buildCollectionField(fieldConfig);

        // get class of inner type of collection
        Class<?> innerTypeClass = Class.forName(fieldConfig.getFieldInnerType());

        // call mapper
        final HBaseMapper mapper = fieldConfig.getMapper().getDeclaredConstructor().newInstance();

        // add to collection
        collection.add(mapper.toObject(column, value, innerTypeClass));
      }

    }
    catch (Exception e) {
      LOGGER.logError(e.getMessage(), e);

      throw new OperationsException(e.getMessage());
    }
  }

  /**
   * Create the put array to store object inside HBASE row.
   * 
   * @param isSaveMode flag to check if save or update mode.
   * 
   * @return {@linkplain java.util.List< org.apache.hadoop.hbase.client.Put>}
   * 
   * @throws {@link OperationsException}
   */
  @SuppressWarnings({"rawtypes", "unchecked"})
  public List<Put> preparePuts(boolean isSaveMode) throws OperationsException {

    // result
    final List<Put> puts = new ArrayList<>(0);

    try {
      // get row key field
      FieldConfiguration rowFieldConfig = classConfig.getRowKeyField();

      // get row class
      Class<?> rowClass = rowFieldConfig.getFieldType();

      // get row as byte array
      byte[] row = toByteArray(
          rowClass.cast(ReflectionUtil.getValue(rowClass, object, rowFieldConfig.getFieldName())));

      // iterate
      for (FieldConfiguration current : classConfig.getFields()) {

        // create put
        Put put = new Put(row);

        // check
        if (current.isInsertable() == isSaveMode) {

          // add to list
          puts.add(put);

        }
        // update case
        else if (current.isUpdatable() == isSaveMode) {

          // add to list
          puts.add(put);

        } // safe net
        else {
          continue;
        }

        // get row class
        Class<?> fieldClass = current.getFieldType();

        // no collection and not use mapper
        if (!current.isCollection() && NoneMapper.class.equals(current.getMapper())) {

          // get value
          byte[] value = toByteArray(
              fieldClass.cast(ReflectionUtil.getValue(fieldClass, object, current.getFieldName())));

          // put
          put.addColumn(Bytes.toBytes(current.getFamilyName()),
              Bytes.toBytes(current.getColumnName()), value);

        } // no collection and use mapper
        else if (!current.isCollection()) {

          // call mapper
          HBaseMapper mapper = current.getMapper().getDeclaredConstructor().newInstance();

          // put
          put.addColumn(Bytes.toBytes(current.getFamilyName()),
              // column name
              Bytes.toBytes(mapper.getColumnName(current.getColumnName(),
                  ReflectionUtil.getValue(fieldClass, object, current.getFieldName()))),

              // column value
              mapper.getColumnValue(
                  ReflectionUtil.getValue(fieldClass, object, current.getFieldName())));

        } // collection and not use mapper
        else if (current.isCollection() && NoneMapper.class.equals(current.getMapper())) {

          // cast to collection
          Collection<Object> collection = (Collection<Object>) ReflectionUtil.getValue(fieldClass,
              object, current.getFieldName());

          // iterator
          Iterator<Object> it = collection.iterator();

          // iterate on collection
          while (it.hasNext()) {

            // current value
            Object valueObj = it.next();

            // buffer
            StringBuilder buffer = new StringBuilder();

            // add column name
            buffer.append(current.getColumnName());

            // add field value
            buffer.append(valueObj);

            // put
            put.addColumn(Bytes.toBytes(current.getFamilyName()), Bytes.toBytes(buffer.toString()),
                new byte[] {});
          }

        } // collection and use mapper
        else if (current.isCollection()) {

          // cast to collection
          Collection<Object> collection = (Collection<Object>) ReflectionUtil.getValue(fieldClass,
              object, current.getFieldName());

          // iterator
          Iterator<Object> it = collection.iterator();

          // mapper
          HBaseMapper mapper = current.getMapper().getDeclaredConstructor().newInstance();

          // iterate on collection
          while (it.hasNext()) {

            // current value
            Object valueObj = it.next();

            // buffer
            StringBuilder buffer = new StringBuilder();

            // add column name
            buffer.append(current.getColumnName());

            // add field value
            buffer.append(mapper.getColumnName(current.getColumnName(), valueObj));

            // put
            put.addColumn(Bytes.toBytes(current.getFamilyName()), Bytes.toBytes(buffer.toString()),
                mapper.getColumnValue(valueObj));
          }

        }
      }
    }
    catch (Exception e) {
      LOGGER.logError(e.getMessage(), e);

      throw new OperationsException(e.getMessage());
    }

    return puts;
  }

  public Class<T> getObjectClass() {
    return objectClass;
  }

  public T getObject() {
    return object;
  }

  public ClassConfiguration getClassConfig() {
    return classConfig;
  }

}
