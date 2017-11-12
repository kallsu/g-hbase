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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.FilterList.Operator;

import net.sf.gee.common.util.string.StringUtil;

/**
 * @author Giorgio Desideri - giorgio.desideri@gmail.com
 *
 */
public class HBaseParams implements Serializable {

  private static final long serialVersionUID = 1L;

  private final List<HBaseColumnParam> columns = new ArrayList<>(0);

  private final List<HBaseColumnParam> extraColumns = new ArrayList<>(0);

  private FilterList filters = null;

  private Comparator<? extends Serializable> comparator = null;

  /**
   * Create new instance of HBaseParams
   * 
   * @param condition {@linkplain org.apache.hadoop.hbase.filter.Operator}
   */
  private HBaseParams(Operator condition) {
    super();

    filters = new FilterList(condition);
  }

  /**
   * @param condition {@linkplain org.apache.hadoop.hbase.filter.Operator}
   * 
   * @return {@link HBaseParams}
   */
  public static HBaseParams newInstance(Operator condition) {
    return new HBaseParams(condition);
  }

  /**
   * Add new column mapping
   * 
   * @param familyName
   * @param columnName
   * 
   * @return {@link HBaseParams}
   */
  public HBaseParams addColumn(String familyName, String columnName) {

    if (!StringUtil.isEmpty(columnName) && !StringUtil.isEmpty(columnName)) {
      columns.add(new HBaseColumnParam(familyName, columnName));
    }

    return this;
  }

  /**
   * Add new filter list
   * 
   * @param fs {@linkplain org.apache.hadoop.hbase.filter.FilterList}
   * 
   * @return {@link HBaseParams}
   */
  public HBaseParams addFilters(FilterList fs) {

    if (fs != null && !fs.getFilters().isEmpty()) {
      filters.addFilter(fs);
    }

    return this;
  }

  /**
   * Add new filter
   * 
   * @param filter {@linkplain org.apache.hadoop.hbase.filter.Filter}
   * 
   * @return {@link HBaseParams}
   */
  public HBaseParams addFilter(Filter filter) {

    if (filter != null) {
      filters.addFilter(filter);
    }

    return this;
  }

  /**
   * Add comparator
   * 
   * @param comparator
   * 
   * @return {@link HBaseParams}
   */
  public HBaseParams addComparator(Comparator<? extends Serializable> comparator) {

    if (comparator != null) {
      this.comparator = comparator;
    }

    return this;
  }

  /**
   * Add new extra column mapping
   * 
   * @param familyName
   * @param columnName
   * 
   * @return {@link HBaseParams}
   */
  public HBaseParams addExtraColumn(String familyName, String columnName) {

    HBaseColumnParam param = new HBaseColumnParam(familyName, columnName);

    if (!columns.contains(param)) {
      extraColumns.add(param);
    }

    return this;
  }

  public boolean isColumnsEmpty() {
    return columns.isEmpty();
  }

  public List<HBaseColumnParam> getColumns() {
    return columns;
  }

  public boolean isFiltersEmpty() {
    return filters.getFilters().isEmpty();
  }

  public FilterList getFilters() {
    return filters;
  }

  public Comparator<? extends Serializable> getComparator() {
    return comparator;
  }

  public List<HBaseColumnParam> getExtraColumns() {
    return extraColumns;
  }

  public List<HBaseColumnParam> getAllColumns() {

    final List<HBaseColumnParam> allColumns = new ArrayList<>(0);

    allColumns.addAll(columns);

    // extra columns is already checked which haven't duplicate on columns
    allColumns.addAll(extraColumns);

    return allColumns;
  }
}
