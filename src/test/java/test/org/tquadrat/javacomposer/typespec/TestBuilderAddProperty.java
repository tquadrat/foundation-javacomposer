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

package test.org.tquadrat.javacomposer.typespec;

import static java.lang.String.format;
import static java.lang.System.out;
import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.tquadrat.foundation.javacomposer.Primitives.INT;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.exception.NullArgumentException;
import org.tquadrat.foundation.exception.ValidationException;
import org.tquadrat.foundation.javacomposer.FieldSpec;
import org.tquadrat.foundation.javacomposer.JavaComposer;
import org.tquadrat.foundation.javacomposer.TypeSpec;
import org.tquadrat.foundation.javacomposer.TypeSpec.Builder;
import org.tquadrat.foundation.testutil.TestBaseClass;

/**
 *  Tests for the method
 *  {@link Builder#addProperty(FieldSpec, boolean)}.
 *
 *  @author Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: TestBuilderAddProperty.java 1076 2023-10-03 18:36:07Z tquadrat $
 */
@ClassVersion( sourceVersion = "$Id: TestBuilderAddProperty.java 1076 2023-10-03 18:36:07Z tquadrat $" )
@DisplayName( "TestBuilderAddProperty" )
public class TestBuilderAddProperty extends TestBaseClass
{
        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  Calls
     *  {@link Builder#addProperty(FieldSpec, boolean)}
     *  for a class.
     */
    @Test
    final void testAddPropertyForClass()
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        TypeSpec candidate;
        FieldSpec field;
        String actual, expected;

        field = composer.fieldBuilder( INT, "m_Field" )
            .build();
        final Class<? extends Throwable> expectedException = ValidationException.class;
        try
        {
            candidate = composer.classBuilder( "CandidateClass" )
                .addProperty( field, false )
                .build();
            assertNotNull( candidate );
            fail( () -> format( MSG_ExceptionNotThrown, expectedException.getName() ) );
        }
        catch( final AssertionError e ) { throw e; }
        catch( final Throwable t )
        {
            final var isExpectedException = expectedException.isInstance( t );
            if( !isExpectedException )
            {
                t.printStackTrace( out );
            }
            assertTrue( isExpectedException, () -> format( MSG_WrongExceptionThrown, expectedException.getName(), t.getClass().getName() ) );
        }

        field = composer.fieldBuilder( INT, "m_Field", PRIVATE )
            .build();
        candidate = composer.classBuilder( "CandidateClass" )
            .addProperty( field, false )
            .build();
        expected =
            """
            package org.tquadrat.test;

            class CandidateClass {
              private int m_Field;

              public final int getField() {
                return m_Field;
              }

              public final void setField(final int value) {
                m_Field = value;
              }
            }
            """;
        actual = composer.javaFileBuilder( "org.tquadrat.test", candidate )
            .build()
            .toString();
        assertEquals( expected, actual );

        field = composer.fieldBuilder( INT, "m_Field", PRIVATE )
            .build();
        candidate = composer.classBuilder( "CandidateClass" )
            .addProperty( field, true )
            .build();
        expected =
            """
            package org.tquadrat.test;

            class CandidateClass {
              private int m_Field;

              public final int getField() {
                return m_Field;
              }
            }
            """;
        actual = composer.javaFileBuilder( "org.tquadrat.test", candidate )
            .build()
            .toString();
        assertEquals( expected, actual );

        field = composer.fieldBuilder( INT, "m_Field", FINAL, PRIVATE ).build();
        candidate = composer.classBuilder( "CandidateClass" )
            .addProperty( field, false )
            .build();
        expected =
            """
            package org.tquadrat.test;

            class CandidateClass {
              private final int m_Field;

              public final int getField() {
                return m_Field;
              }
            }
            """;
        actual = composer.javaFileBuilder( "org.tquadrat.test", candidate )
            .build()
            .toString();
        assertEquals( expected, actual );

        field = composer.fieldBuilder( INT, "m_Field", FINAL, PRIVATE )
            .build();
        candidate = composer.classBuilder( "CandidateClass" )
            .addProperty( field, true )
            .build();
        expected =
            """
            package org.tquadrat.test;

            class CandidateClass {
              private final int m_Field;

              public final int getField() {
                return m_Field;
              }
            }
            """;
        actual = composer.javaFileBuilder( "org.tquadrat.test", candidate )
            .build()
            .toString();
        assertEquals( expected, actual );
    }   //  testAddPropertyForClass()

    /**
     *  Calls
     *  {@link TypeSpec.Builder#addProperty(FieldSpec, boolean)}
     *  for an interface.
     */
    @Test
    final void testAddPropertyForInterface()
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final Class<? extends Throwable> expectedException = ValidationException.class;

        final var candidate = composer.interfaceBuilder( "CandidateInterface" );

        var field = composer.fieldBuilder( INT, "m_Field" )
            .build();
        try
        {
            candidate.addProperty( field, true );
            fail( () -> format( MSG_ExceptionNotThrown, expectedException.getName() ) );
        }
        catch( final AssertionError e ) { throw e; }
        catch( final Throwable t )
        {
            final var isExpectedException = expectedException.isInstance( t );
            if( !isExpectedException )
            {
                t.printStackTrace( out );
            }
            assertTrue( isExpectedException, () -> format( MSG_WrongExceptionThrown, expectedException.getName(), t.getClass().getName() ) );
        }

        field = composer.fieldBuilder( INT, "m_Field", PRIVATE )
            .build();
        try
        {
            candidate.addProperty( field, true );
            fail( () -> format( MSG_ExceptionNotThrown, expectedException.getName() ) );
        }
        catch( final AssertionError e ) { throw e; }
        catch( final Throwable t )
        {
            final var isExpectedException = expectedException.isInstance( t );
            if( !isExpectedException )
            {
                t.printStackTrace( out );
            }
            assertTrue( isExpectedException, () -> format( MSG_WrongExceptionThrown, expectedException.getName(), t.getClass().getName() ) );
        }

        field = composer.fieldBuilder( INT, "m_Field", PUBLIC )
            .build();
        try
        {
            candidate.addProperty( field, true );
            fail( () -> format( MSG_ExceptionNotThrown, expectedException.getName() ) );
        }
        catch( final AssertionError e ) { throw e; }
        catch( final Throwable t )
        {
            final var isExpectedException = expectedException.isInstance( t );
            if( !isExpectedException )
            {
                t.printStackTrace( out );
            }
            assertTrue( isExpectedException, () -> format( MSG_WrongExceptionThrown, expectedException.getName(), t.getClass().getName() ) );
        }

        field = composer.fieldBuilder( INT, "m_Field", FINAL )
            .build();
        try
        {
            candidate.addProperty( field, true );
            fail( () -> format( MSG_ExceptionNotThrown, expectedException.getName() ) );
        }
        catch( final AssertionError e ) { throw e; }
        catch( final Throwable t )
        {
            final var isExpectedException = expectedException.isInstance( t );
            if( !isExpectedException )
            {
                t.printStackTrace( out );
            }
            assertTrue( isExpectedException, () -> format( MSG_WrongExceptionThrown, expectedException.getName(), t.getClass().getName() ) );
        }

        field = composer.fieldBuilder( INT, "m_Field", PRIVATE, FINAL )
            .build();
        try
        {
            candidate.addProperty( field, true );
            fail( () -> format( MSG_ExceptionNotThrown, expectedException.getName() ) );
        }
        catch( final AssertionError e ) { throw e; }
        catch( final Throwable t )
        {
            final var isExpectedException = expectedException.isInstance( t );
            if( !isExpectedException )
            {
                t.printStackTrace( out );
            }
            assertTrue( isExpectedException, () -> format( MSG_WrongExceptionThrown, expectedException.getName(), t.getClass().getName() ) );
        }

        field = composer.fieldBuilder( INT, "m_Field", PUBLIC, FINAL )
            .build();
        try
        {
            candidate.addProperty( field, true );
            fail( () -> format( MSG_ExceptionNotThrown, expectedException.getName() ) );
        }
        catch( final AssertionError e ) { throw e; }
        catch( final Throwable t )
        {
            final var isExpectedException = expectedException.isInstance( t );
            if( !isExpectedException )
            {
                t.printStackTrace( out );
            }
            assertTrue( isExpectedException, () -> format( MSG_WrongExceptionThrown, expectedException.getName(), t.getClass().getName() ) );
        }

        field = composer.fieldBuilder( INT, "m_Field", STATIC )
            .build();
        try
        {
            candidate.addProperty( field, true );
            fail( () -> format( MSG_ExceptionNotThrown, expectedException.getName() ) );
        }
        catch( final AssertionError e ) { throw e; }
        catch( final Throwable t )
        {
            final var isExpectedException = expectedException.isInstance( t );
            if( !isExpectedException )
            {
                t.printStackTrace( out );
            }
            assertTrue( isExpectedException, () -> format( MSG_WrongExceptionThrown, expectedException.getName(), t.getClass().getName() ) );
        }

        field = composer.fieldBuilder( INT, "m_Field", PRIVATE, STATIC )
            .build();
        try
        {
            candidate.addProperty( field, true );
            fail( () -> format( MSG_ExceptionNotThrown, expectedException.getName() ) );
        }
        catch( final AssertionError e ) { throw e; }
        catch( final Throwable t )
        {
            final var isExpectedException = expectedException.isInstance( t );
            if( !isExpectedException )
            {
                t.printStackTrace( out );
            }
            assertTrue( isExpectedException, () -> format( MSG_WrongExceptionThrown, expectedException.getName(), t.getClass().getName() ) );
        }

        field = composer.fieldBuilder( INT, "m_Field", PUBLIC, STATIC )
            .build();
        try
        {
            candidate.addProperty( field, true );
            fail( () -> format( MSG_ExceptionNotThrown, expectedException.getName() ) );
        }
        catch( final AssertionError e ) { throw e; }
        catch( final Throwable t )
        {
            final var isExpectedException = expectedException.isInstance( t );
            if( !isExpectedException )
            {
                t.printStackTrace( out );
            }
            assertTrue( isExpectedException, () -> format( MSG_WrongExceptionThrown, expectedException.getName(), t.getClass().getName() ) );
        }

        field = composer.fieldBuilder( INT, "m_Field", STATIC, FINAL )
            .build();
        try
        {
            candidate.addProperty( field, true );
            fail( () -> format( MSG_ExceptionNotThrown, expectedException.getName() ) );
        }
        catch( final AssertionError e ) { throw e; }
        catch( final Throwable t )
        {
            final var isExpectedException = expectedException.isInstance( t );
            if( !isExpectedException )
            {
                t.printStackTrace( out );
            }
            assertTrue( isExpectedException, () -> format( MSG_WrongExceptionThrown, expectedException.getName(), t.getClass().getName() ) );
        }

        field = composer.fieldBuilder( INT, "m_Field", PRIVATE, STATIC, FINAL )
            .build();
        try
        {
            candidate.addProperty( field, true );
            fail( () -> format( MSG_ExceptionNotThrown, expectedException.getName() ) );
        }
        catch( final AssertionError e ) { throw e; }
        catch( final Throwable t )
        {
            final var isExpectedException = expectedException.isInstance( t );
            if( !isExpectedException )
            {
                t.printStackTrace( out );
            }
            assertTrue( isExpectedException, () -> format( MSG_WrongExceptionThrown, expectedException.getName(), t.getClass().getName() ) );
        }

        field = composer.fieldBuilder( INT, "m_Field", PUBLIC, STATIC, FINAL )
            .build();
        try
        {
            candidate.addProperty( field, true );
            fail( () -> format( MSG_ExceptionNotThrown, expectedException.getName() ) );
        }
        catch( final AssertionError e ) { throw e; }
        catch( final Throwable t )
        {
            final var isExpectedException = expectedException.isInstance( t );
            if( !isExpectedException )
            {
                t.printStackTrace( out );
            }
            assertTrue( isExpectedException, () -> format( MSG_WrongExceptionThrown, expectedException.getName(), t.getClass().getName() ) );
        }
    }   //  testAddPropertyForInterface()

    /**
     *  Calls
     *  {@link TypeSpec.Builder#addProperty(FieldSpec, boolean)}
     *  with {@code null}.
     */
    @Test
    final void testAddPropertyWithNullArgument()
    {
        skipThreadTest();

        final var composer = new JavaComposer();

        final var candidate = composer.classBuilder( "CandidateClass" );

        final Class<? extends Throwable> expectedException = NullArgumentException.class;
        try
        {
            candidate.addProperty( null, true );
            fail( () -> format( MSG_ExceptionNotThrown, expectedException.getName() ) );
        }
        catch( final AssertionError e ) { throw e; }
        catch( final Throwable t )
        {
            final var isExpectedException = expectedException.isInstance( t );
            if( !isExpectedException )
            {
                t.printStackTrace( out );
            }
            assertTrue( isExpectedException, () -> format( MSG_WrongExceptionThrown, expectedException.getName(), t.getClass().getName() ) );
        }
    }   //  testAddPropertyWithNullArgument()
}
//  class TestBuilderAddProperty

/*
 *  End of File
 */