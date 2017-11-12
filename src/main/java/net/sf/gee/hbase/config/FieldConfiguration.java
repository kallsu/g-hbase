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

import java.io.Serializable;

import net.sf.gee.hbase.basic.HBaseMapper;

/**
 * @author Giorgio Desideri - giorgio.desideri@gmail.com
 *
 */
@SuppressWarnings("rawtypes")
public class FieldConfiguration implements Serializable {

  private static final long serialVersionUID = 1L;

  private String fieldName = null;

  private Class<?> fieldType = null;

  private String columnName = null;

  private String familyName = null;

  private boolean insertable = false;

  private boolean updatable = true;

  private Class<? extends HBaseMapper> mapper = null;

  private String fieldInnerType = null;

  private boolean collection = false;

  /**
   * Instantiates a new field configuration.
   */
  public FieldConfiguration() {}

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((familyName == null) ? 0 : familyName.hashCode());
    result = prime * result + ((fieldName == null) ? 0 : fieldName.hashCode());
    result = prime * result + ((fieldType == null) ? 0 : fieldType.hashCode());
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
    FieldConfiguration other = (FieldConfiguration) obj;
    if (familyName == null) {
      if (other.familyName != null)
        return false;
    }
    else if (!familyName.equals(other.familyName))
      return false;
    if (fieldInnerType == null) {
      if (other.fieldInnerType != null)
        return false;
    }
    else if (!fieldInnerType.equals(other.fieldInnerType))
      return false;
    if (fieldName == null) {
      if (other.fieldName != null)
        return false;
    }
    else if (!fieldName.equals(other.fieldName))
      return false;
    if (fieldType == null) {
      if (other.fieldType != null)
        return false;
    }
    else if (!fieldType.equals(other.fieldType))
      return false;
    return true;
  }

  public String getFieldName() {
    return fieldName;
  }

  public void setFieldName(String fieldName) {
    this.fieldName = fieldName;
  }

  public Class<?> getFieldType() {
    return fieldType;
  }

  public void setFieldType(Class<?> fieldType) {
    this.fieldType = fieldType;
  }

  public String getFieldInnerType() {
    return fieldInnerType;
  }

  public void setFieldInnerType(String fieldInnerType) {
    this.fieldInnerType = fieldInnerType;
  }

  public String getColumnName() {
    return columnName;
  }

  public void setColumnName(String columnName) {
    this.columnName = columnName;
  }

  public String getFamilyName() {
    return familyName;
  }

  public void setFamilyName(String familyName) {
    this.familyName = familyName;
  }

  public boolean isInsertable() {
    return insertable;
  }

  public void setInsertable(boolean insertable) {
    this.insertable = insertable;
  }

  public boolean isUpdatable() {
    return updatable;
  }

  public void setUpdatable(boolean updatable) {
    this.updatable = updatable;
  }

  public Class<? extends HBaseMapper> getMapper() {
    return mapper;
  }

  public void setMapper(Class<? extends HBaseMapper> mapper) {
    this.mapper = mapper;
  }

  public boolean isCollection() {
    return collection;
  }

  public void setCollection(boolean collection) {
    this.collection = collection;
  }

}
