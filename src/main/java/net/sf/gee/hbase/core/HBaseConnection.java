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

import java.io.Closeable;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;

import net.sf.gee.common.util.string.StringUtil;
import net.sf.gee.hbase.config.ClassConfiguration;
import net.sf.gee.hbase.filters.HBaseColumnParam;
import net.sf.gee.hbase.filters.HBaseParams;
import net.sf.gee.hbase.util.GHBaseObjectBuilder;
import net.sf.gee.logger.factory.GLogFactory;
import net.sf.gee.logger.log.SimpleGLogger;

/**
 * @author Giorgio Desideri - giorgio.desideri@gmail.com
 *
 */
public class HBaseConnection implements Serializable, Closeable {

  private static final long serialVersionUID = 1L;

  private final static SimpleGLogger LOGGER =
      GLogFactory.getInstance().getLogger(SimpleGLogger.class, HBaseConnection.class);

  private Connection connection = null;

  /**
   * Constructor with connection
   * 
   * @param connection {@linkplain org.apache.hadoop.hbase.client.Connection}
   */
  public HBaseConnection(Connection connection) {
    super();

    this.connection = connection;
  }

  /**
   * Close connection.
   * 
   * @throws IOException
   */
  @Override
  public void close() throws IOException {
    connection.close();
  }

  /**
   * Gets the table.
   *
   * @param hBaseEntity class object of entity {@linkplain java.lang.Class<?>}
   * 
   * @return the table object {@linkplain org.apache.hadoop.hbase.client.Table}, otherwise
   *         <code>null</code>
   * 
   * @throws IOException
   */
  protected Table getTable(Class<?> hBaseEntity) throws IOException {

    // get class configuration
    final ClassConfiguration cc =
        HBaseConnectionFactory.getInstance().getClassConfiguration(hBaseEntity.getName());

    // check namespace
    if (StringUtil.isEmpty(cc.getNamespace())) {

      // get table by name
      return connection.getTable(TableName.valueOf(cc.getTable()));

    }
    else {

      // get table by namespace + name
      return connection.getTable(TableName.valueOf(cc.getNamespace() + ":" + cc.getTable()));
    }
  }

  /**
   * Close table.
   *
   * @param table the table object {@linkplain org.apache.hadoop.hbase.client.Table}
   * 
   * @throws IOException {@link java.io.IOException}
   */
  protected void closeTable(Table table) throws IOException {

    // close table
    table.close();
  }

  /**
   * Load by row key.
   * 
   * @param clazz {@linkplain java.lang.Class<K extends java.io.Serializable>}
   * @param rowKey row key {@linkplain java.lang.String}
   * @param columns optional array of columns to retrieve {@link HBaseColumnParam}
   * 
   * @return <T extends java.io.Serializable>
   * 
   * @throws IOException
   */
  public <T extends Serializable> T load(Class<T> clazz, String rowKey, HBaseColumnParam... columns)
      throws IOException {

    // check array
    if (columns != null && columns.length > 0) {
      return load(clazz, rowKey, Arrays.asList(columns));

    } // load all
    else {
      return load(clazz, rowKey, new ArrayList<>(0));
    }
  }

  /**
   * @param clazz {@linkplain java.lang.Class} of <K extends java.io.Serializable>
   * @param rowKey row key {@linkplain java.lang.String}
   * @param columns {@linkplain java.util.List} of columns to retrieve {@link HBaseColumnParam}
   * 
   * @return <T extends java.io.Serializable>
   * 
   * @throws IOException
   */
  public <T extends Serializable> T load(Class<T> clazz, String rowKey,
      List<HBaseColumnParam> columns) throws IOException {

    // get table
    try (Table table = getTable(clazz);) {

      // get by rowId
      Get get = new Get(Bytes.toBytes(rowKey));

      // check columns
      if (columns != null && !columns.isEmpty()) {

        // iterate
        for (HBaseColumnParam current : columns) {

          // check
          if (StringUtil.isEmpty(current.getColumnName())) {

            // add only family
            get.addFamily(Bytes.toBytes(current.getFamilyName()));
          }
          else {
            // add column
            get.addColumn(Bytes.toBytes(current.getFamilyName()),
                Bytes.toBytes(current.getColumnName()));
          }
        }
      }

      // get result
      Result result = table.get(get);

      // check
      if (result.isEmpty()) {
        return null;
      }

      // init GHbase Bean
      final GHBaseObjectBuilder<T> gHBean =
          new GHBaseObjectBuilder<T>(clazz, Bytes.toBytes(rowKey));

      // iterate
      for (Cell cell : result.listCells()) {

        // family
        String family = StringUtil.toString(CellUtil.cloneFamily(cell));

        // column
        String column = StringUtil.toString(CellUtil.cloneQualifier(cell));

        // value
        byte[] value = CellUtil.cloneValue(cell);

        // set value
        gHBean.setValue(family, column, value);
      }

      return gHBean.getObject();

    }
    catch (Exception e) {
      LOGGER.logError(e.getMessage(), e);

      throw new IOException(e.getMessage());
    }
  }

  /**
   * Save HBase Entity
   * 
   * @param hBaseEntity <T>
   * 
   * @throws IOException
   */
  public <T extends Serializable> void save(T hBaseEntity) throws IOException {

    try (Table table = getTable(hBaseEntity.getClass());) {

      final GHBaseObjectBuilder<T> gHBean = new GHBaseObjectBuilder<T>(hBaseEntity);

      // put operation list
      final List<Put> puts = gHBean.preparePuts(true);

      // save or update inside table
      table.put(puts);
    }
    catch (Exception e) {
      LOGGER.logError(e.getMessage(), e);

      throw new IOException(e.getMessage());
    }
  }

  /**
   * Update HBase Entity
   * 
   * @param hBaseEntity <K>
   * 
   * @throws IOException
   */
  public <T extends Serializable> void update(T hBaseEntity) throws IOException {

    try (Table table = getTable(hBaseEntity.getClass());) {

      final GHBaseObjectBuilder<T> gHBean = new GHBaseObjectBuilder<T>(hBaseEntity);

      // put operation list
      final List<Put> puts = gHBean.preparePuts(false);

      // save or update inside table
      table.put(puts);
    }
    catch (Exception e) {
      LOGGER.logError(e.getMessage(), e);

      throw new IOException(e.getMessage());
    }
  }

  /**
   * Delete entire row.
   * 
   * @param clazz {@linkplain java.lang.Class<K>}
   * @param rowKey row key code
   * 
   * @throws IOException
   */
  public <K extends Serializable> void delete(Class<K> clazz, String rowKey) throws IOException {

    try (Table table = getTable(clazz);) {

      // get by rowId
      Delete delete = new Delete(Bytes.toBytes(rowKey));

      // delete
      table.delete(delete);
    }
    catch (Exception e) {
      LOGGER.logError(e.getMessage(), e);

      throw new IOException(e.getMessage());
    }
  }

  /**
   * Delete enitre column family values.
   * 
   * @param clazz {@linkplain java.lang.Class<K>}
   * @param rowKey row key code
   * @param family family name
   * 
   * @throws IOException
   */
  public <K extends Serializable> void deleteFamily(Class<K> clazz, String rowKey, String family)
      throws IOException {

    try (Table table = getTable(clazz);) {

      // get by rowId
      Delete delete = new Delete(Bytes.toBytes(rowKey));

      // add family name
      delete.addFamily(Bytes.toBytes(family));

      // delete
      table.delete(delete);

    }
    catch (Exception e) {
      LOGGER.logError(e.getMessage(), e);

      throw new IOException(e.getMessage());
    }
  }

  /**
   * Get rows by filters.
   * 
   * @param <K>
   * @param hBaseEntityClass {@linkplain java.lang.Class} class of entity
   * @param params {@link HBaseParams}
   * 
   * @return {@linkplain java.util.List<K>}
   * 
   * @throws IOException
   */
  @SuppressWarnings("unchecked")
  public <T extends Serializable> List<T> scan(Class<T> hBaseEntityClass, HBaseParams params)
      throws IOException {

    final List<T> results = new ArrayList<>(0);

    // get table
    Table table = getTable(hBaseEntityClass);

    // get scan
    Scan scan = new Scan();

    // only last version
    scan.setMaxVersions(1);

    // add columns
    if (!params.isColumnsEmpty()) {
      for (HBaseColumnParam current : params.getColumns()) {

        // add column to scan
        scan.addColumn(Bytes.toBytes(current.getFamilyName()),
            Bytes.toBytes(current.getColumnName()));
      }
    }

    // add filters
    if (!params.isFiltersEmpty()) {
      scan.setFilter(params.getFilters());
    }

    // prepare all columns
    List<HBaseColumnParam> allCols = params.getAllColumns();

    // get result
    try (ResultScanner scanner = table.getScanner(scan);) {

      // Reading values from scan result
      for (Result result = scanner.next(); result != null; result = scanner.next()) {

        // check if result is empty
        if (result.isEmpty()) {
          continue;
        }

        // retrieve row key
        String rowKey = Bytes.toString(result.getRow());

        // retrieve entity
        T entity = load(hBaseEntityClass, rowKey, allCols);

        // add to results
        results.add(entity);
      }

      // sorting
      if (params.getComparator() != null) {
        // sort
        results.sort((Comparator<T>) params.getComparator());
      }

      return results;
    }
    catch (Exception e) {
      LOGGER.logError(e);

      throw new IOException(e.getMessage());
    }
    finally {
      // close table
      table.close();
    }
  }

}
