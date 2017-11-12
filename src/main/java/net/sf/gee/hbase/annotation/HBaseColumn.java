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
package net.sf.gee.hbase.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.sf.gee.hbase.basic.HBaseMapper;
import net.sf.gee.hbase.basic.NoneMapper;

/**
 * @author Giorgio Desideri - giorgio.desideri@gmail.com
 *
 */
@Target(value = {ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface HBaseColumn {

  /**
   * Column name.
   *
   * @return the column name
   */
  String name();

  /**
   * Column Family name.
   *
   * @return the column family name
   */
  String familyName();

  /**
   * Column insertable.
   * 
   * @return default is <code>true</code>
   */
  boolean insertable() default true;

  /**
   * Column updatable.
   * 
   * @return default is <code>true</code>
   */
  boolean updatable() default true;

  /**
   * Class of Mapper to retrieve column name and column value.
   * 
   * @return {@linkplain java.lang.Class} of {@link HBaseMapper}
   */
  @SuppressWarnings("rawtypes")
  Class<? extends HBaseMapper> mapper() default NoneMapper.class;
}
