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
package net.sf.jguiraffe.gui.platform.javafx

import org.easymock.IAnswer
import org.easymock.EasyMock

/**
 * An implementation of the ''IAnswer'' interface which stores a value passed
 * as argument to the associated method call.
 *
 * Sometimes it is necessary to expect a method invocation with an argument
 * object which is not yet known. But the object is to be stored so that it
 * can be further used or evaluated in the unit test. In such a case, a
 * developer usually has to create an ''IAnswer'' implementation which fetches
 * an argument from ''EasyMock'' 's current arguments. This class does exactly
 * this in a generic way. It can be configured with the value to return and
 * with the number of argument to obtain.
 *
 * @param argIdx the index of the argument to fetch
 * @param retVal the value to return
 * @tparam A the return type of the answer
 * @tparam V the type of the value
 */
class FetchAnswer[A, V](argIdx: Int = 0, retVal: A = null) extends IAnswer[A] {
  /** Stores the value obtained from the arguments. */
  private var argValue: Option[V] = None

  /**
   * @inheritdoc This implementation obtains the argument value from the
   * current method arguments.
   */
  override def answer(): A = {
    val argVal = EasyMock.getCurrentArguments()(argIdx).asInstanceOf[V]
    argValue = Some(argVal)
    retVal
  }

  /**
   * Returns the value obtained from the current parameters. This method will
   * fail if this answer was not invoked.
   * @return the value fetched by this answer
   */
  def value = argValue.get
}

/**
 * The companion object of ''FetchAnswer''.
 */
object FetchAnswer {
  /**
   * Converts the given answer to an ''Option''. This can be convenient if it
   * is not sure that the answer gets executed.
   * @param a the answer to be converted
   * @return the associated option object
   */
  implicit def convertToOption[V](a: FetchAnswer[_, V]): Option[V] =
    a.argValue
}
