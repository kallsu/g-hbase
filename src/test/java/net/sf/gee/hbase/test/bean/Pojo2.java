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

/**
 * @author Giorgio Desideri - giorgio.desideri@gmail.com
 *
 */
public class Pojo2 implements Serializable {

  private static final long serialVersionUID = 1L;

  private String inner1 = null;

  private Integer inner2 = null;

  /**
   * 
   */
  public Pojo2() {
    super();
  }

  /**
   * @param inner1
   * @param inner2
   */
  public Pojo2(String inner1, Integer inner2) {
    super();
    this.inner1 = inner1;
    this.inner2 = inner2;
  }

  public String getInner1() {
    return inner1;
  }

  public void setInner1(String inner1) {
    this.inner1 = inner1;
  }

  public Integer getInner2() {
    return inner2;
  }

  public void setInner2(Integer inner2) {
    this.inner2 = inner2;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((inner1 == null) ? 0 : inner1.hashCode());
    result = prime * result + ((inner2 == null) ? 0 : inner2.hashCode());
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
    Pojo2 other = (Pojo2) obj;
    if (inner1 == null) {
      if (other.inner1 != null)
        return false;
    }
    else if (!inner1.equals(other.inner1))
      return false;
    if (inner2 == null) {
      if (other.inner2 != null)
        return false;
    }
    else if (!inner2.equals(other.inner2))
      return false;
    return true;
  }

}
