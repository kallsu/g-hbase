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
package net.sf.gee.hbase.test.core;

import java.io.IOException;
import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Test;

import net.sf.gee.common.util.string.StringUtil;
import net.sf.gee.hbase.config.ClassConfiguration;
import net.sf.gee.hbase.config.FieldConfiguration;
import net.sf.gee.hbase.core.HBaseConnection;
import net.sf.gee.hbase.core.HBaseConnectionFactory;
import net.sf.gee.hbase.filters.HBaseColumnParam;
import net.sf.gee.hbase.test.bean.Pojo;
import net.sf.gee.hbase.test.bean.Pojo2;

/**
 * @author Giorgio Desideri - giorgio.desideri@gmail.com
 *
 */
public class HBConnectionFactoryTest {

  @Test
  public void testMapping() {

    HBaseConnectionFactory hbcf = HBaseConnectionFactory.getInstance("ghbase_mapping.xml");

    ClassConfiguration cc = hbcf.getClassConfiguration(Pojo.class.getName());

    Assert.assertNotNull(cc);
    Assert.assertNotNull(cc.getRowKeyField());
    Assert.assertFalse(cc.getFields().isEmpty());

    for (FieldConfiguration current : cc.getFields()) {
      Assert.assertFalse(StringUtil.isEmpty(current.getFamilyName()));
      Assert.assertFalse(StringUtil.isEmpty(current.getColumnName()));
      Assert.assertNotNull(current.getMapper());
    }
  }

  @Test
  public void testSaveLoadScan() {

    HBaseConnectionFactory hbcf = HBaseConnectionFactory.getInstance("ghbase_mapping.xml");

    Pojo entity = new Pojo();
    entity.setCode("TEST_" + System.currentTimeMillis());
    entity.setAttr1("TEST Pojo");
    entity.setAttr2(new Pojo2("Inner", 1));
    entity.setAttr3(new ArrayList<>(0));
    entity.getAttr3().add("TEST");;

    entity.setAttr4(new ArrayList<>(0));
    entity.getAttr4().add(new Pojo2("Inner", 2));

    try (HBaseConnection connection = hbcf.openConnection();) {
      Assert.assertNotNull(connection);

      save(connection, entity);

      fullLoad(connection, entity);

    }
    catch (Exception e) {
      e.printStackTrace();

      Assert.fail(e.getMessage());
    }
  }

  protected void save(HBaseConnection connection, Pojo entity) throws IOException {
    connection.save(entity);
  }

  protected void fullLoad(HBaseConnection connection, Pojo entity) throws IOException {

    Pojo other = connection.load(Pojo.class, entity.getCode());

    Assert.assertNotNull(other);
    Assert.assertEquals(entity.getAttr1(), other.getAttr1());
    Assert.assertEquals(entity.getAttr2().getInner1(), other.getAttr2().getInner1());
    Assert.assertEquals(entity.getAttr2().getInner2(), other.getAttr2().getInner2());
    Assert.assertEquals(entity.getAttr3(), other.getAttr3());
    Assert.assertEquals(entity.getAttr4(), other.getAttr4());

  }

  protected void testPartialLoad(HBaseConnection connection, Pojo entity) throws IOException {

    Pojo other =
        connection.load(Pojo.class, entity.getCode(), new HBaseColumnParam("test", "attr1"));

    Assert.assertEquals(entity.getAttr1(), other.getAttr1());
  }

  protected void testScan(HBaseConnection connection, Pojo entity) throws IOException {}
}
