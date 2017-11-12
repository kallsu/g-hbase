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
import java.util.ArrayList;

/**
 * @author Giorgio Desideri - giorgio.desideri@gmail.com
 *
 */
public class ClassConfiguration implements Serializable {

  private static final long serialVersionUID = 1L;

  /** The HBtable. */
  private String table = null;

  /** The HBTable namespace. */
  private String namespace = null;

  /** The fields configuration list. */
  private final ArrayList<FieldConfiguration> fields = new ArrayList<>(0);

  /** The row key field. */
  private FieldConfiguration rowKeyField = null;

  /**
   * Instantiates a new class configuration.
   */
  public ClassConfiguration() {
    super();
  }

  /**
   * Instantiates a new class configuration.
   *
   * @param table the table
   */
  public ClassConfiguration(String table) {
    super();

    this.table = table;
  }

  /**
   * Instantiates a new class configuration.
   *
   * @param table the table
   * @param namespace the namespace
   */
  public ClassConfiguration(String table, String namespace) {
    super();

    this.table = table;
    this.namespace = namespace;
  }

  public String getTable() {
    return table;
  }

  public void setTable(String table) {
    this.table = table;
  }

  public String getNamespace() {
    return namespace;
  }

  public void setNamespace(String namespace) {
    this.namespace = namespace;
  }

  public ArrayList<FieldConfiguration> getFields() {
    return fields;
  }

  public FieldConfiguration getRowKeyField() {
    return rowKeyField;
  }

  public void setRowKeyField(FieldConfiguration rowKeyField) {
    this.rowKeyField = rowKeyField;
  }

  /**
   * Adds the field configuration.
   *
   * @param fconfig {@link FieldConfiguration}
   * 
   * @return true, if configuration is not null AND is not already inside list, otherwise false.
   */
  public boolean addFieldConfiguration(FieldConfiguration fconfig) {

    // check
    if (fconfig == null) {
      return false;
    }

    // check no uplicate
    if (!getFields().contains(fconfig)) {
      // add in list
      getFields().add(fconfig);

      return true;

    } // duplicate ---> no insert
    else {
      return false;
    }
  }

  /**
   * Gets the field configuration based on field name.
   *
   * @param fieldName the field name
   * 
   * @return {@link FieldConfiguration}
   */
  public FieldConfiguration getFieldConfiguration(String fieldName) {

    FieldConfiguration field = null;

    // iterate
    for (FieldConfiguration current : getFields()) {

      // check name
      if (current.getFieldName().equals(fieldName)) {
        // associate
        field = current;

        break;
      }
    }

    return field;
  }

  /**
   * Get field configuration by family name and column name.
   * 
   * @param family family name
   * @param column column name
   * 
   * @return {@link FieldConfiguration}
   */
  public FieldConfiguration getFieldConfiguration(String family, String column) {

    FieldConfiguration fieldConfig = null;

    // iterate
    for (FieldConfiguration current : getFields()) {

      // match exactly with all field
      if (current.getFamilyName().equals(family) && column.startsWith(current.getColumnName())) {

        fieldConfig = current;

        break;
      }
    }

    return fieldConfig;
  }

}
