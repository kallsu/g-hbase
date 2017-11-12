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
package net.sf.gee.hbase.exception;

import net.sf.gee.common.exception.GBaseCode;

/**
 * @author Giorgio Desideri - giorgio.desideri@gmail.com
 *
 */
/**
 * @author Giorgio Desideri - giorgio.desideri@gmail.com
 *
 */
public enum GHBaseCode implements GBaseCode {

  PARSING_CLASS_ERROR(1, true),

  PARSING_FIELD_ERROR(2, true),


  ;

  /**
   * Code integer.
   */
  private int code = -1;

  /**
   * Error flag.
   */
  private boolean error = false;

  /**
   * Private constructor
   * 
   * @param code
   * @param error
   */
  private GHBaseCode(int code, boolean error) {
    this.code = code;
    this.error = error;
  }

  @Override
  public int getCode() {
    return code;
  }

  @Override
  public boolean isError() {
    return error;
  }



}
