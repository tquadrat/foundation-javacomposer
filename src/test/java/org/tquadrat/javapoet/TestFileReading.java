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
import static java.nio.charset.StandardCharsets.UTF_8;

import javax.lang.model.element.Modifier;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;
import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;

import org.junit.Rule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.rules.TemporaryFolder;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.javacomposer.JavaComposer;
import org.tquadrat.foundation.javacomposer.ParameterizedTypeName;
import com.google.common.io.ByteStreams;

@SuppressWarnings( {"static-method", "javadoc"} )
@ClassVersion( sourceVersion = "$Id: TestFileReading.java 943 2021-12-21 01:34:32Z tquadrat $" )
@DisplayName( "TestFileReading" )
public class TestFileReading
{
        /*------------*\
    ====** Attributes **=======================================================
        \*------------*/
    /**
     * Used for storing compilation output.
     */
    @Rule
    public final TemporaryFolder m_TemporaryFolder = new TemporaryFolder();

        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    @Test
    public void compileJavaFile() throws Exception
    {
        final var composer = new JavaComposer();

        m_TemporaryFolder.create();

        final var value = "Hello World!";
        final var type = composer.classBuilder( "Test" )
            .addModifiers( Modifier.PUBLIC )
            .addSuperinterface( ParameterizedTypeName.from( Callable.class, String.class ) )
            .addMethod( composer.methodBuilder( "call" )
                .returns( String.class )
                .addModifiers( Modifier.PUBLIC )
                .addStatement( "return $S", value )
                .build() )
            .build();
        final var javaFile = composer.javaFileBuilder( "foo", type )
            .build();

        final var compiler = ToolProvider.getSystemJavaCompiler();
        final var diagnosticCollector = new DiagnosticCollector<JavaFileObject>();
        @SuppressWarnings( "resource" )
        final var fileManager = compiler.getStandardFileManager( diagnosticCollector, Locale.getDefault(), UTF_8 );
        fileManager.setLocation( StandardLocation.CLASS_OUTPUT, Collections.singleton( m_TemporaryFolder.newFolder() ) );
        final var task = compiler.getTask(
            null,
            fileManager,
            diagnosticCollector,
            List.of( "-proc:none" ),
            Collections.emptySet(),
            Collections.singleton( javaFile.toJavaFileObject() )
        );

        assertThat( task.call() ).isTrue();
        assertThat( diagnosticCollector.getDiagnostics() ).isEmpty();

        final var loader = fileManager.getClassLoader( StandardLocation.CLASS_OUTPUT );
        final Callable<?> test = Class.forName( "foo.Test", true, loader )
            .asSubclass( Callable.class )
            .getDeclaredConstructor()
            .newInstance();
        assertThat( Callable.class.getMethod( "call" ).invoke( test ) ).isEqualTo( value );
    }

    @Test
    public void javaFileObjectCharacterContent() throws IOException
    {
        final var composer = new JavaComposer();

        final var type = composer.classBuilder( "Test" )
            .addJavadoc( "Pi\u00f1ata\u00a1" )
            .addMethod( composer.methodBuilder( "fooBar" )
                .build() )
            .build();
        final var javaFile = composer.javaFileBuilder( "foo", type )
            .build();
        final var javaFileObject = javaFile.toJavaFileObject();

        // We can never have encoding issues (everything is in process)
        assertThat( javaFileObject.getCharContent( true ) ).isEqualTo( javaFile.toString() );
        assertThat( javaFileObject.getCharContent( false ) ).isEqualTo( javaFile.toString() );
    }

    @Test
    public void javaFileObjectInputStreamIsUtf8() throws IOException
    {
        final var composer = new JavaComposer();

        final var javaFile = composer.javaFileBuilder( "foo", composer.classBuilder( "Test" ).build() )
            .addFileComment( "Pi\u00f1ata\u00a1" )
            .build();
        @SuppressWarnings( "resource" )
        final var bytes = ByteStreams.toByteArray( javaFile.toJavaFileObject().openInputStream() );

        /*
         * JavaPoet always uses UTF-8; same for JavaComposer.
         */
        assertThat( bytes ).isEqualTo( javaFile.toString().getBytes( UTF_8 ) );
    }

    @Test
    public void javaFileObjectKind()
    {
        final var composer = new JavaComposer();

        final var javaFile = composer.javaFileBuilder( "", composer.classBuilder( "Test" ).build() )
            .build();
        assertThat( javaFile.toJavaFileObject().getKind() ).isEqualTo( Kind.SOURCE );
    }

    @Test
    public void javaFileObjectUri()
    {
        final var composer = new JavaComposer();

        final var type = composer.classBuilder( "Test" )
            .build();
        assertThat( composer.javaFileBuilder( "", type ).build().toJavaFileObject().toUri() ).isEqualTo( URI.create( "Test.java" ) );
        assertThat( composer.javaFileBuilder( "foo", type ).build().toJavaFileObject().toUri() ).isEqualTo( URI.create( "foo/Test.java" ) );
        assertThat( composer.javaFileBuilder( "com.example", type ).build().toJavaFileObject().toUri() ).isEqualTo( URI.create( "com/example/Test.java" ) );
    }
}
//  class TestFileReading

/*
 *  End of File
 */