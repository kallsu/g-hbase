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

/**
 * @author Giorgio Desideri - giorgio.desideri@gmail.com
 *
 */
public final class NoneMapper implements HBaseMapper<Object> {

  private static final long serialVersionUID = 1L;

  /**
   * 
   */
  public NoneMapper() {
    super();
  }

  @Override
  public java.lang.Object toObject(String columnName, byte[] columnValue, Class<Object> clazz) {
    return new Object();
  }

  @Override
  public String getColumnName(String columnName, java.lang.Object object) {
    return "";
  }

  @Override
  public byte[] getColumnValue(java.lang.Object object) {
    return new byte[] {};
  }

}
