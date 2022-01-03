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

package org.tquadrat.javapoet;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;

import org.junit.Rule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.javacomposer.JavaComposer;
import org.tquadrat.foundation.javacomposer.Layout;
import org.tquadrat.javapoet.helper.DummyFiler;
import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;

@SuppressWarnings( {"javadoc", "UseOfObsoleteDateTimeApi"} )
@ClassVersion( sourceVersion = "$Id: TestFileWriting.java 937 2021-12-14 21:59:00Z tquadrat $" )
@DisplayName( "TestFileWriting" )
public class TestFileWriting
{
        /*------------*\
    ====** Attributes **=======================================================
        \*------------*/
    /**
     *  Used for testing annotation processor Filer behaviour.
     */
    @SuppressWarnings( "InstanceVariableOfConcreteClass" )
    private final DummyFiler m_Filer;

    /**
     *  Used for testing java.nio.file Path behaviour.
     */
    private final FileSystem m_FileSystem;

    private final Path m_FileSystemRoot;

    // Used for testing java.io File behavior.
    @Rule
    public final TemporaryFolder m_TempFolder = new TemporaryFolder();

        /*--------------*\
    ====** Constructors **=====================================================
        \*--------------*/
    /**
     *  Creates a new {@code TestFileWriting_Square} instance.
     */
    public TestFileWriting()
    {
        m_FileSystem = Jimfs.newFileSystem( Configuration.unix() );
        m_FileSystemRoot = m_FileSystem.getRootDirectories().iterator().next();
        m_Filer = new DummyFiler( m_FileSystem, m_FileSystemRoot );
    }   //  TestFileWriting_Square()

        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    @Test
    public void fileDefaultPackage() throws IOException
    {
        m_TempFolder.create();

        final var composer = new JavaComposer();
        final var type = composer.classBuilder( "Test" )
            .build();
        composer.javaFileBuilder( "", type )
            .build()
            .writeTo( m_TempFolder.getRoot() );

        final var testFile = new File( m_TempFolder.getRoot(), "Test.java" );
        assertThat( testFile.exists() ).isTrue();
    }

    /**
     * This test confirms that JavaPoet ignores the host charset and always uses
     * UTF-8. The host
     * charset is customized with {@code -Dfile.encoding=ISO-8859-1}.
     */
    @Test
    public void fileIsUtf8() throws IOException
    {
        final var composer = new JavaComposer();
        final var javaFile = composer.javaFileBuilder( "foo", composer.classBuilder( "Taco" ).build() )
            .addFileComment( "Pi\u00f1ata\u00a1" )
            .build();
        javaFile.writeTo( m_FileSystemRoot );

        final var fooPath = m_FileSystemRoot.resolve( m_FileSystem.getPath( "foo", "Taco.java" ) );
        assertThat( Files.readString( fooPath ) ).isEqualTo(
            """
            /*
             * Pi\u00f1ata\u00a1
             */
             
            package foo;

            class Taco {
            }
            """ );
    }

    @Test
    public void fileNestedClasses() throws IOException
    {
        m_TempFolder.create();

        final var composer = new JavaComposer();

        final var type = composer.classBuilder( "Test" )
            .build();
        composer.javaFileBuilder( "foo", type )
            .build()
            .writeTo( m_TempFolder.getRoot() );
        composer.javaFileBuilder( "foo.bar", type )
            .build()
            .writeTo( m_TempFolder.getRoot() );
        composer.javaFileBuilder( "foo.bar.baz", type )
            .build()
            .writeTo( m_TempFolder.getRoot() );

        final var fooDir = new File( m_TempFolder.getRoot(), "foo" );
        final var fooFile = new File( fooDir, "Test.java" );
        final var barDir = new File( fooDir, "bar" );
        final var barFile = new File( barDir, "Test.java" );
        final var bazDir = new File( barDir, "baz" );
        final var bazFile = new File( bazDir, "Test.java" );
        assertThat( fooFile.exists() ).isTrue();
        assertThat( barFile.exists() ).isTrue();
        assertThat( bazFile.exists() ).isTrue();
    }

    @Test
    public void fileNotDirectory() throws IOException
    {
        m_TempFolder.create();

        final var composer = new JavaComposer();

        final var type = composer.classBuilder( "Test" )
            .build();
        final var javaFile = composer.javaFileBuilder( "example", type )
            .build();
        final var file = new File( m_TempFolder.newFolder( "foo" ), "bar" );
        //noinspection ResultOfMethodCallIgnored
        file.createNewFile();
        try
        {
            javaFile.writeTo( file );
            fail();
        }
        catch( final IllegalArgumentException e )
        {
            assertThat( e.getMessage() ).isEqualTo( "path " + file.getPath() + " exists but is not a directory." );
        }
    }

    @Test
    public void filerClassesWithTabIndent() throws IOException
    {
        final var composer = new JavaComposer( Layout.LAYOUT_JAVAPOET_WITH_TAB );

        final var test = composer.classBuilder( "Test" )
            .addField( Date.class, "madeFreshDate" )
            .addMethod( composer.methodBuilder( "main" )
                .addModifiers( Modifier.PUBLIC, Modifier.STATIC )
                .addParameter( String [].class, "args" )
                .addCode( "$T.out.println($S);\n", System.class, "Hello World!" )
                .build() )
            .build();
        composer.javaFileBuilder( "foo", test )
            .build()
            .writeTo( m_Filer );

        final var fooPath = m_FileSystemRoot.resolve( m_FileSystem.getPath( "foo", "Test.java" ) );
        assertThat( Files.exists( fooPath ) ).isTrue();
        final var source = new String( Files.readAllBytes( fooPath ) );

        assertThat( source ).isEqualTo(
            """
            package foo;

            import java.lang.String;
            import java.lang.System;
            import java.util.Date;

            class Test {
            \tDate madeFreshDate;

            \tpublic static void main(String[] args) {
            \t\tSystem.out.println("Hello World!");
            \t}
            }
            """ );
    }

    @Test
    public void filerDefaultPackage() throws IOException
    {
        final var composer = new JavaComposer();
        final var type = composer.classBuilder( "Test" )
            .build();
        composer.javaFileBuilder( "", type )
            .build()
            .writeTo( m_Filer );

        final var testPath = m_FileSystemRoot.resolve( "Test.java" );
        assertThat( Files.exists( testPath ) ).isTrue();
    }

    @Test
    public void filerNestedClasses() throws IOException
    {
        final var composer = new JavaComposer();

        final var type = composer.classBuilder( "Test" )
            .build();
        composer.javaFileBuilder( "foo", type )
            .build()
            .writeTo( m_Filer );
        composer.javaFileBuilder( "foo.bar", type )
            .build()
            .writeTo( m_Filer );
        composer.javaFileBuilder( "foo.bar.baz", type )
            .build()
            .writeTo( m_Filer );

        final var fooPath = m_FileSystemRoot.resolve( m_FileSystem.getPath( "foo", "Test.java" ) );
        final var barPath = m_FileSystemRoot.resolve( m_FileSystem.getPath( "foo", "bar", "Test.java" ) );
        final var bazPath = m_FileSystemRoot.resolve( m_FileSystem.getPath( "foo", "bar", "baz", "Test.java" ) );
        assertThat( Files.exists( fooPath ) ).isTrue();
        assertThat( Files.exists( barPath ) ).isTrue();
        assertThat( Files.exists( bazPath ) ).isTrue();
    }

    @Test
    public void filerPassesOriginatingElements() throws IOException
    {
        final var composer = new JavaComposer();

        final var element1_1 = Mockito.mock( Element.class );
        final var test1 = composer.classBuilder( "Test1" )
            .addOriginatingElement( element1_1 )
            .build();

        final var element2_1 = Mockito.mock( Element.class );
        final var element2_2 = Mockito.mock( Element.class );

        final var test2 = composer.classBuilder( "Test2" )
            .addOriginatingElement( element2_1 )
            .addOriginatingElement( element2_2 )
            .build();

        composer.javaFileBuilder( "example", test1 )
            .build()
            .writeTo( m_Filer );
        composer.javaFileBuilder( "example", test2 )
            .build()
            .writeTo( m_Filer );

        final var testPath1 = m_FileSystemRoot.resolve( m_FileSystem.getPath( "example", "Test1.java" ) );
        assertThat( m_Filer.getOriginatingElements( testPath1 ) ).containsExactly( element1_1 );
        final var testPath2 = m_FileSystemRoot.resolve( m_FileSystem.getPath( "example", "Test2.java" ) );
        assertThat( m_Filer.getOriginatingElements( testPath2 ) ).containsExactly( element2_1, element2_2 );
    }

    @Test
    public void pathDefaultPackage() throws IOException
    {
        final var composer = new JavaComposer();

        final var type = composer.classBuilder( "Test" )
            .build();
        composer.javaFileBuilder( "", type )
            .build()
            .writeTo( m_FileSystemRoot );

        final var testPath = m_FileSystemRoot.resolve( "Test.java" );
        assertThat( Files.exists( testPath ) ).isTrue();
    }

    @Test
    public void pathNestedClasses() throws IOException
    {
        final var composer = new JavaComposer();

        final var type = composer.classBuilder( "Test" )
            .build();
        composer.javaFileBuilder( "foo", type )
            .build()
            .writeTo( m_FileSystemRoot );
        composer.javaFileBuilder( "foo.bar", type )
            .build()
            .writeTo( m_FileSystemRoot );
        composer.javaFileBuilder( "foo.bar.baz", type )
            .build()
            .writeTo( m_FileSystemRoot );

        final var fooPath = m_FileSystemRoot.resolve( m_FileSystem.getPath( "foo", "Test.java" ) );
        final var barPath = m_FileSystemRoot.resolve( m_FileSystem.getPath( "foo", "bar", "Test.java" ) );
        final var bazPath = m_FileSystemRoot.resolve( m_FileSystem.getPath( "foo", "bar", "baz", "Test.java" ) );
        assertThat( Files.exists( fooPath ) ).isTrue();
        assertThat( Files.exists( barPath ) ).isTrue();
        assertThat( Files.exists( bazPath ) ).isTrue();
    }

    @Test
    public void pathNotDirectory() throws IOException
    {
        final var composer = new JavaComposer();

        final var type = composer.classBuilder( "Test" ).build();
        final var javaFile = composer.javaFileBuilder( "example", type ).build();
        final var path = m_FileSystem.getPath( "/foo/bar" );
        Files.createDirectories( path.getParent() );
        Files.createFile( path );
        try
        {
            javaFile.writeTo( path );
            fail();
        }
        catch( final IllegalArgumentException e )
        {
            assertThat( e.getMessage() ).isEqualTo( "path /foo/bar exists but is not a directory." );
        }
    }
}
//  class TestFileWriting

/*
 *  End of File
 */