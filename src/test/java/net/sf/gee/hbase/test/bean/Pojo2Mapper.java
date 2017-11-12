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
package net.sf.gee.hbase.test.bean;

import org.apache.hadoop.hbase.util.Bytes;

import net.sf.gee.hbase.basic.HBaseMapper;

/**
 * @author Giorgio Desideri - giorgio.desideri@gmail.com
 *
 */
public class Pojo2Mapper implements HBaseMapper<Pojo2> {

  private static final long serialVersionUID = 1L;

  /**
   * 
   */
  public Pojo2Mapper() {
    super();
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.sf.gee.hbase.basic.HBaseMapper#toObject(java.lang.String, byte[], java.lang.Class)
   */
  @Override
  public Pojo2 toObject(String columnName, byte[] columnValue, Class<Pojo2> clazz) {
    Pojo2 obj = new Pojo2();

    int index = columnName.indexOf("_");

    if (index != -1) {
      columnName = columnName.substring(index + 1);
    }

    obj.setInner1(columnName);
    obj.setInner2(Bytes.toInt(columnValue));

    return obj;
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.sf.gee.hbase.basic.HBaseMapper#getColumnName(java.lang.Object)
   */
  @Override
  public String getColumnName(String columnName, Pojo2 object) {
    return String.format("%s_%s", columnName, object.getInner1());
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.sf.gee.hbase.basic.HBaseMapper#getColumnValue(java.lang.Object)
   */
  @Override
  public byte[] getColumnValue(Pojo2 object) {
    return Bytes.toBytes(object.getInner2());
  }

}
