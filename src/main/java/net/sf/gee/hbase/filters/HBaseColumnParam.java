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
package net.sf.gee.hbase.filters;

import java.io.Serializable;

/**
 * @author Giorgio Desideri - giorgio.desideri@gmail.com
 *
 */
public class HBaseColumnParam implements Serializable {

  private static final long serialVersionUID = 1L;

  private final String familyName;

  private final String columnName;

  /**
   * Create new instance of HBaseColumParam
   * 
   * @param familyName
   * @param columnName
   */
  public HBaseColumnParam(String familyName, String columnName) {
    super();

    this.familyName = familyName;
    this.columnName = columnName;
  }

  public String getFamilyName() {
    return familyName;
  }

  public String getColumnName() {
    return columnName;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((columnName == null) ? 0 : columnName.hashCode());
    result = prime * result + ((familyName == null) ? 0 : familyName.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    HBaseColumnParam other = (HBaseColumnParam) obj;
    if (columnName == null) {
      if (other.columnName != null)
        return false;
    }
    else if (!columnName.equals(other.columnName))
      return false;
    if (familyName == null) {
      if (other.familyName != null)
        return false;
    }
    else if (!familyName.equals(other.familyName))
      return false;
    return true;
  }

}
