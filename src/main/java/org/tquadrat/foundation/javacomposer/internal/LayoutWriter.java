/*
 * ============================================================================
 *  Copyright © 2002-2021 by Thomas Thrien.
 *  All Rights Reserved.
 * ============================================================================
 *  Licensed to the public under the agreements of the GNU Lesser General Public
 *  License, version 3.0 (the "License"). You may obtain a copy of the License at
 *
 *       http://www.gnu.org/licenses/lgpl.html
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *  WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *  License for the specific language governing permissions and limitations
 *  under the License.
 */

package org.tquadrat.foundation.javacomposer.internal;

import static org.apiguardian.api.API.Status.INTERNAL;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;

/**
 *  This is the definition for
 *  {@link org.tquadrat.foundation.javacomposer.Layout}.
 *  It provides the base implementations for the various layout emit methods.
 *
 *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: LayoutWriter.java 920 2021-05-23 14:27:24Z tquadrat $
 *
 *  @UMLGraph.link
 *  @since 0.2.0
 */
@ClassVersion( sourceVersion = "$Id: LayoutWriter.java 920 2021-05-23 14:27:24Z tquadrat $" )
@API( status = INTERNAL, since = "0.2.0" )
public interface LayoutWriter
{
        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     * Returns the indentation String for this layout.
     *
     * @return The indentation.
     */
    public String indent();

    /**
     *  Returns the tabulator size this layout was configured with.
     *
     * @return  The tabulator size.
     */
    public int tabsize();
}
//  interface LayoutWriter

/*
 *  End of File
 */