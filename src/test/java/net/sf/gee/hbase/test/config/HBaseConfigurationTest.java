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
package net.sf.gee.hbase.test.config;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import net.sf.gee.common.util.string.StringUtil;
import net.sf.gee.hbase.config.ClassConfiguration;
import net.sf.gee.hbase.config.FieldConfiguration;
import net.sf.gee.hbase.config.HBaseConfiguration;
import net.sf.gee.hbase.test.bean.Pojo;

/**
 * @author Giorgio Desideri - giorgio.desideri@gmail.com
 *
 */
public class HBaseConfigurationTest {

  @Test
  public void testFile() {

    try {
      HBaseConfiguration config =
          HBaseConfiguration.newInstance().addConfigFile("ghbase_mapping.xml").build();

      ClassConfiguration cc = config.getConfiguration(Pojo.class.getName());

      Assert.assertNotNull(cc);
      Assert.assertNotNull(cc.getRowKeyField());
      Assert.assertFalse(cc.getFields().isEmpty());

      for (FieldConfiguration current : cc.getFields()) {
        Assert.assertFalse(StringUtil.isEmpty(current.getFamilyName()));
        Assert.assertFalse(StringUtil.isEmpty(current.getColumnName()));
        Assert.assertNotNull(current.getMapper());
      }

    }
    catch (IOException e) {
      e.printStackTrace();

      Assert.fail();
    }
  }

  @Test
  public void testDefaultFile() {

    try {
      HBaseConfiguration config = HBaseConfiguration.newInstance().build();

      ClassConfiguration cc = config.getConfiguration(Pojo.class.getName());

      Assert.assertNotNull(cc);
      Assert.assertNotNull(cc.getRowKeyField());
      Assert.assertFalse(cc.getFields().isEmpty());

      for (FieldConfiguration current : cc.getFields()) {
        Assert.assertFalse(StringUtil.isEmpty(current.getFamilyName()));
        Assert.assertFalse(StringUtil.isEmpty(current.getColumnName()));
        Assert.assertNotNull(current.getMapper());
      }
    }
    catch (IOException e) {
      e.printStackTrace();

      Assert.fail();
    }
  }

}
