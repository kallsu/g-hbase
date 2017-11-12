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

import java.io.Serializable;
import java.util.List;

import net.sf.gee.hbase.annotation.HBaseColumn;
import net.sf.gee.hbase.annotation.RowKey;
import net.sf.gee.hbase.annotation.Table;

/**
 * @author Giorgio Desideri - giorgio.desideri@gmail.com
 *
 */
@Table(table = "pojo_hb_table")
public class Pojo implements Serializable {

  private static final long serialVersionUID = 1L;

  @RowKey
  private String code = null;

  @HBaseColumn(familyName = "test", name = "attr1", insertable = true, updatable = true)
  private String attr1 = null;

  @HBaseColumn(familyName = "test", name = "attr2", mapper = Pojo2Mapper.class)
  private Pojo2 attr2 = null;

  @HBaseColumn(familyName = "test", name = "attr3")
  private List<String> attr3 = null;

  @HBaseColumn(familyName = "test", name = "attr4", mapper = Pojo2Mapper.class)
  private List<Pojo2> attr4 = null;

  /**
   * 
   */
  public Pojo() {
    super();
  }

  public String getAttr1() {
    return attr1;
  }

  public void setAttr1(String attr1) {
    this.attr1 = attr1;
  }

  public Pojo2 getAttr2() {
    return attr2;
  }

  public void setAttr2(Pojo2 attr2) {
    this.attr2 = attr2;
  }

  public List<String> getAttr3() {
    return attr3;
  }

  public void setAttr3(List<String> attr3) {
    this.attr3 = attr3;
  }

  public List<Pojo2> getAttr4() {
    return attr4;
  }

  public void setAttr4(List<Pojo2> attr4) {
    this.attr4 = attr4;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((code == null) ? 0 : code.hashCode());
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
    Pojo other = (Pojo) obj;
    if (code == null) {
      if (other.code != null)
        return false;
    }
    else if (!code.equals(other.code))
      return false;
    return true;
  }

}
