/*
 * Copyright 2006-2016 The JGUIraffe Team.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.sf.jguiraffe.gui.platform.javafx.common

import javafx.util.Callback

/**
 * An implementation of the ''Callback'' interface which delegates to a passed
 * in function.
 *
 * JavaFX uses ''Callback'' objects in many places. In Scala code, it is more
 * convenient to deal with function objects. This class provides a way to
 * create a callback object for a function.
 *
 * Unfortunately, it does not help much to provide an '''implicit'''
 * conversion; the compiler is not able to infer the correct type parameters.
 * Therefore, it is easier to create callback objects manually; then the
 * function literals can be expressed in a shorter form.
 *
 * @tparam P the parameter type of the callback
 * @tparam R the return type of the callback
 */
class FunctionCallback[P, R] private(function: P => R) extends Callback[P, R] {
  override def call(param: P): R = function(param)
}

/**
 * Companion object for ''FunctionCallback''.
 *
 * This object provides a factory method for creating new callback instances.
 */
object FunctionCallback {
  /**
   * Factory method for creating a new ''FunctionCallback'' object.
   * @param function the function defining the callback
   * @tparam P the parameter type of the callback
   * @tparam R the return type of the callback
   * @return the newly created ''Callback'' object
   */
  def apply[P, R](function: P => R): Callback[P, R] = new FunctionCallback(function)
}
