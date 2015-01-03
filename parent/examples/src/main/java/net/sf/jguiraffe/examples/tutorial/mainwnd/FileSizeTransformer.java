/*
 * Copyright 2006-2015 The JGUIraffe Team.
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
package net.sf.jguiraffe.examples.tutorial.mainwnd;

import java.text.NumberFormat;

import net.sf.jguiraffe.transform.Transformer;
import net.sf.jguiraffe.transform.TransformerContext;

/**
 * <p>
 * A specialized transformer for formatting file sizes.
 * </p>
 * <p>
 * This transformer obtains the size of a file in kilobytes.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: FileSizeTransformer.java 205 2012-01-29 18:29:57Z oheger $
 */
public class FileSizeTransformer implements Transformer
{
    /** Constant for the resource ID for the KB constant. */
    private static final String RES_KB = "trans_kb";

    /** The size of a kilobyte. */
    private static final long KB_SIZE = 1024;

    /**
     * Performs the transformation.
     *
     * @param o the object to transform
     * @param ctx the transformer context
     * @throws Exception if an error occurs
     */
    @Override
    public Object transform(Object o, TransformerContext ctx) throws Exception
    {
        NumberFormat fmt = NumberFormat.getIntegerInstance(ctx.getLocale());
        fmt.setGroupingUsed(true);
        long sizeKb = (((Number) o).longValue() + KB_SIZE - 1) / KB_SIZE;
        return fmt.format(sizeKb)
                + ctx.getResourceManager().getText(ctx.getLocale(), null,
                        RES_KB);
    }
}
