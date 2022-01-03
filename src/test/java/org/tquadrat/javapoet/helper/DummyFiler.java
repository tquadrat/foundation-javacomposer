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

package org.tquadrat.javapoet.helper;

import static org.tquadrat.foundation.lang.Objects.requireNonNullArgument;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;
import javax.tools.FileObject;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.spi.FileSystemProvider;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.tquadrat.foundation.annotation.ClassVersion;

/**
 *  An implementation of
 *  {@link Filer}
 *  for the JavaPoet tests.
 *
 *  @author Square,Inc.
 *  @modified   Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: DummyFiler.java 937 2021-12-14 21:59:00Z tquadrat $
 *
 *  @UMLGraph.link
 */
@ClassVersion( sourceVersion = "$Id: DummyFiler.java 937 2021-12-14 21:59:00Z tquadrat $" )
public final class DummyFiler implements Filer
{
        /*---------------*\
    ====** Inner Classes **====================================================
        \*---------------*/
    /**
     *  An implementation of
     *  {@link SimpleJavaFileObject}
     *  for the use in conjunction with
     *  {@link DummyFiler}.
     *
     *  @author Thomas Thrien - thomas.thrien@tquadrat.org
     *  @version $Id: DummyFiler.java 937 2021-12-14 21:59:00Z tquadrat $
     *  @since 10
     *
     *  @UMLGraph.link
     */
    public class Source extends SimpleJavaFileObject
    {
            /*------------*\
        ====** Attributes **===================================================
            \*------------*/
        /**
         *  The path for the source file.
         */
        private final Path m_Path;

            /*--------------*\
        ====** Constructors **=================================================
            \*--------------*/
        /**
         *  Creates a new {@code Source} instance.
         *
         *  @param  path    The path for the source file.
         */
        protected Source( final Path path )
        {
            super( path.toUri(), Kind.SOURCE );
            m_Path = path;
        }   //  Source()

            /*---------*\
        ====** Methods **======================================================
            \*---------*/
        /**
         *  {@inheritDoc}
         */
        @Override
        public final OutputStream openOutputStream() throws IOException
        {
            final var parent = m_Path.getParent();
            if( !Files.exists( parent ) ) m_FileSystemProvider.createDirectory( parent );
            final var retValue = m_FileSystemProvider.newOutputStream( m_Path );

            //---* Done *------------------------------------------------------
            return retValue;
        }   //  openOutputStream()
    }
    //  class Source

         /*------------*\
    ====** Attributes **=======================================================
        \*------------*/
    /**
     *  The file system provider.
     */
    private final FileSystemProvider m_FileSystemProvider;

    /**
     *  The file system root.
     */
    private final Path m_FileSystemRoot;

    /**
     *  The map of the originating elements.
     */
    private final Map<Path,Set<Element>> m_OriginatingElementsMap;

    /**
     *  The folder/directory separator string for the file system.
     */
    private final String m_Separator;

        /*--------------*\
    ====** Constructors **=====================================================
        \*--------------*/
    /**
     *  Creates a new {@code DummyFiler} instance.
     *
     *  @param  fileSystem  The file system.
     *  @param  fsRoot  The file system root.
     */
    @SuppressWarnings( "resource" )
    public DummyFiler( final FileSystem fileSystem, final Path fsRoot )
    {
        m_Separator = requireNonNullArgument( fileSystem, "fileSystem" ).getSeparator();
        m_FileSystemRoot = requireNonNullArgument( fsRoot, "fsRoot" );
        m_FileSystemProvider = fileSystem.provider();
        m_OriginatingElementsMap = new LinkedHashMap<>();
    }   //  DummyFiler()

        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  {@inheritDoc}
     */
    @Override
    public final JavaFileObject createClassFile( final CharSequence name, final Element... originatingElements ) throws IOException
    {
        throw new UnsupportedOperationException( "Not implemented." );
    }   //  createClassFile()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final FileObject createResource( final JavaFileManager.Location location, final CharSequence pkg, final CharSequence relativeName, final Element... originatingElements ) throws IOException
    {
        throw new UnsupportedOperationException( "Not implemented." );
    }   //  createResource()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final JavaFileObject createSourceFile( final CharSequence name, final Element... originatingElements ) throws IOException
    {
        final var relative = name.toString().replace( ".", m_Separator ) + ".java"; // Assumes well-formed.
        final var path = m_FileSystemRoot.resolve( relative );
        m_OriginatingElementsMap.put( path, Set.copyOf( Arrays.asList( originatingElements ) ) );
        final var retValue = new Source( path );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  createSourceFile()

    /**
     *  Returns the originating elements for the given path.
     *
     *  @param  path    The path.
     *  @return The originating elements.
     */
    public final Set<Element> getOriginatingElements( final Path path )
    {
        return m_OriginatingElementsMap.get( path );
    }   //  getOriginatingElements()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final FileObject getResource( final JavaFileManager.Location location, final CharSequence pkg, final CharSequence relativeName ) throws IOException
    {
        throw new UnsupportedOperationException( "Not implemented." );
    }   //  getResource()
}
//  class DummyFiler

/*
 *  End of File
 */