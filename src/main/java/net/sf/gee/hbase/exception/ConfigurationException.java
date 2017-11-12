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

import net.sf.gee.common.exception.GBaseException;
import net.sf.gee.common.util.string.StringUtil;

/**
 * @author Giorgio Desideri - giorgio.desideri@gmail.com
 *
 */
public class ConfigurationException extends GBaseException {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  /**
   * Constructor with signel, message and cause
   * 
   * @param signal {@link GHBaseCode}
   * @param message {@linkplain java.lang.String}
   * @param cause {@linkplain java.lang.Throwable}
   */
  public ConfigurationException(GHBaseCode signal, String message, Throwable cause) {
    super(signal, message, cause);
  }

  /**
   * Constructor.
   * 
   * @param signal {@link GHBaseCode}
   * @param message {@linkplain java.lang.String}
   */
  public ConfigurationException(GHBaseCode signal, String message) {
    super(signal, message);
  }

  /**
   * Constructor.
   * 
   * @param signal {@link GHBaseCode}
   * @param cause {@linkplain java.lang.Throwable}
   */
  public ConfigurationException(GHBaseCode signal, Throwable cause) {
    super(signal, cause);
  }

  /**
   * @param signal
   * @param message
   * @param params
   * @return
   */
  public static ConfigurationException build(GHBaseCode signal, String message, Object... params) {
    return build(signal, message, null, params);
  }

  /**
   * Configuration Exception create
   * 
   * @param signal {@link GHBaseCode} signal code
   * @param message error / warn message
   * @param cause {@linkplain java.lang.Throwable}
   * @param params array of params
   * 
   * @return {@link ConfigurationException}
   */
  public static ConfigurationException build(GHBaseCode signal, String message, Throwable cause,
      Object... params) {

    String msg = null;

    // is not empty
    if (!StringUtil.isEmpty(message)) {
      msg = message;

    } // check the cause
    else if (cause != null) {

      msg = String.format(msg, "ERROR: Caused by [%s]\n", cause.getMessage());

    } // default case
    else {
      msg = "ERROR.";
    }

    // check params
    if (params != null && params.length > 0) {
      msg = String.format(msg, params);
    }

    // define exception
    if (cause != null) {
      return new ConfigurationException(signal, msg, cause);

    }
    else {
      return new ConfigurationException(signal, msg);
    }
  }
}
