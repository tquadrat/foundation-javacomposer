/*
 * ============================================================================
 *  Copyright © 2002-2023 by Thomas Thrien.
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

package org.tquadrat.foundation.javacomposer;

import static org.apiguardian.api.API.Status.STABLE;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.javacomposer.internal.CodeProcessorImpl;

/**
 *  An implementation of
 *  {@link StringTemplate.Processor}
 *  that creates an instance of
 *  {@link CodeBlock}
 *  from the given String template.
 *
 *  @version $Id: CodeProcessor.java 1079 2023-10-22 17:44:34Z tquadrat $
 *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
 *  @UMLGraph.link
 *  @since 0.3.0
 */
@ClassVersion( sourceVersion = "$Id: CodeProcessor.java 1079 2023-10-22 17:44:34Z tquadrat $" )
@API( status = STABLE, since = "0.3.0" )
public sealed interface CodeProcessor extends StringTemplate.Processor<CodeBlock,JavaComposerException>
    permits CodeProcessorImpl
{
        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     * {@inheritDoc}
     */
    @Override
    public CodeBlock process( final StringTemplate template );
}
//  class CodeProcessorImpl

/*
 *  End of File
 */