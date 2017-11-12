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
package net.sf.gee.hbase.basic;

import java.io.Serializable;

/**
 * HBMapper interface has the goal to solve the hard choice to design the column name and column
 * value.
 * 
 * @author Giorgio Desideri - giorgio.desideri@gmail.com
 *
 */
public interface HBaseMapper<T> extends Serializable {

  /**
   * Build up the object from column name and column value.
   * 
   * @param columnName
   * @param columnValue
   * @param clazz {@linkplain java.lang.Class} of <T>
   * 
   * @return <T>
   */
  public T toObject(String columnName, byte[] columnValue, Class<T> clazz);

  /**
   * Gets the column name.
   * 
   * @param object instance of object that contains the values to returns the column name
   * @param columnName
   * 
   * @return column name as {@linkplain java.lang.String}
   */
  public String getColumnName(String columnName, T object);

  /**
   * Gets the column name.
   * 
   * @param object instance of object that contains the values to returns the column value
   * 
   * @return column value as byte array
   */
  public byte[] getColumnValue(T object);
}
