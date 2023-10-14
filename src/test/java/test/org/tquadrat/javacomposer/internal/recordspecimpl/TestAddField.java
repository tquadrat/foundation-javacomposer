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

package test.org.tquadrat.javacomposer.internal.recordspecimpl;

import static java.lang.String.format;
import static java.lang.System.out;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.tquadrat.foundation.lang.CommonConstants.EMPTY_STRING;

import javax.lang.model.element.Modifier;
import java.lang.reflect.Type;
import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Stream;
import java.util.stream.Stream.Builder;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.exception.EmptyArgumentException;
import org.tquadrat.foundation.exception.NullArgumentException;
import org.tquadrat.foundation.exception.ValidationException;
import org.tquadrat.foundation.javacomposer.FieldSpec;
import org.tquadrat.foundation.javacomposer.JavaComposer;
import org.tquadrat.foundation.javacomposer.TypeName;
import org.tquadrat.foundation.javacomposer.internal.RecordSpecImpl;
import org.tquadrat.foundation.javacomposer.internal.RecordSpecImpl.BuilderImpl;
import org.tquadrat.foundation.testutil.TestBaseClass;

/**
 *  Some tests for the methods
 *  {@link org.tquadrat.foundation.javacomposer.internal.RecordSpecImpl.BuilderImpl#addField(FieldSpec)},
 *  {@link org.tquadrat.foundation.javacomposer.internal.RecordSpecImpl.BuilderImpĺ#addField(Type, CharSequence, Modifier...)},
 *  and
 *  {@link org.tquadrat.foundation.javacomposer.internal.RecordSpecImpl.BuilderImpl#addField(TypeName, CharSequence, Modifier...)}.
 *
 *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
 */
@ClassVersion( sourceVersion = "$Id: TestAddField.java 1076 2023-10-03 18:36:07Z tquadrat $" )
@DisplayName( "test.org.tquadrat.javacomposer.internal.recordspecimpl.TestAddField" )
public class TestAddField extends TestBaseClass
{
        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  Generates modifier sets that are not allowed for record fields.#
     *
     *  @return A stream of modifier sets.
     */
    private static Stream<Set<Modifier>> invalidModifiersProvider()
    {
        Builder<Set<Modifier>> builder = Stream.<Set<Modifier>>builder()
            .add( EnumSet.noneOf( Modifier.class ) ) // no modifier at all
            .add( EnumSet.of( Modifier.PRIVATE, Modifier.FINAL ) ) // FINAL is not allowed
            .add( EnumSet.of( Modifier.FINAL ) ) // FINAL is not allowed
            .add( EnumSet.of( Modifier.PUBLIC ) ) // PUBLIC is not allowed
            .add( EnumSet.of( Modifier.PUBLIC, Modifier.FINAL ) ) // PUBLIC is not allowed
            .add( EnumSet.of( Modifier.PROTECTED ) ) // PROTECTED is not allowed
            .add( EnumSet.of( Modifier.PROTECTED, Modifier.FINAL ) ) // PROTECTED is not allowed
            .add( EnumSet.of( Modifier.STATIC, Modifier.PROTECTED ) ); // PROTECTED is not allowed, even not for STATIC

        final var retValue = builder.build();
        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  invalidModifiersProvider()

    /**
     *  Some tests for the methods
     *  {@link org.tquadrat.foundation.javacomposer.internal.RecordSpecImpl.BuilderImpl#addField(FieldSpec)},
     *  {@link org.tquadrat.foundation.javacomposer.internal.RecordSpecImpl.BuilderImpĺ#addField(Type, CharSequence, Modifier...)}
     *  and
     *  {@link org.tquadrat.foundation.javacomposer.internal.RecordSpecImpl.BuilderImpl#addField(TypeName, CharSequence, Modifier...)}.
     *
     *  @throws Exception   Something went unexpectedly wrong.
     */
    @Test
    final void testAddField() throws Exception
    {
        skipThreadTest();
    }   //  testAddField()

    /**
     *  Some tests for the methods
     *  {@link org.tquadrat.foundation.javacomposer.internal.RecordSpecImpl.BuilderImpĺ#addField(Type, CharSequence, Modifier...)}
     *  and
     *  {@link org.tquadrat.foundation.javacomposer.internal.RecordSpecImpl.BuilderImpl#addField(TypeName, CharSequence, Modifier...)}.
     *
     *  @throws Exception   Something went unexpectedly wrong.
     */
    @Test
    final void testAddFieldWithEmptyArgument() throws Exception
    {
        skipThreadTest();

        final var composer = new JavaComposer();
        final var candidate = composer.recordBuilder( "Record" );

        final var type = String.class;
        final var typeName = TypeName.from( type );

        final Class<? extends Throwable> expectedException = EmptyArgumentException.class;
        try
        {
            candidate.addField( type, EMPTY_STRING );
            fail( () -> format( MSG_ExceptionNotThrown, expectedException.getName() ) );
        }
        catch( final AssertionError e )
        {
            throw e;
        }
        catch( final Throwable t )
        {
            final var isExpectedException = expectedException.isInstance( t );
            if( !isExpectedException )
            { t.printStackTrace( out ); }
            assertTrue( isExpectedException, () -> format( MSG_WrongExceptionThrown, expectedException.getName(), t.getClass().getName() ) );
        }
        try
        {
            candidate.addField( typeName, EMPTY_STRING );
            fail( () -> format( MSG_ExceptionNotThrown, expectedException.getName() ) );
        }
        catch( final AssertionError e )
        {
            throw e;
        }
        catch( final Throwable t )
        {
            final var isExpectedException = expectedException.isInstance( t );
            if( !isExpectedException )
            { t.printStackTrace( out ); }
            assertTrue( isExpectedException, () -> format( MSG_WrongExceptionThrown, expectedException.getName(), t.getClass().getName() ) );
        }
    }   //  testAddFieldWithEmptyArgument()

    /**
     *  <p>{@summary Some tests for the methods
     *  {@link BuilderImpl#addField(FieldSpec)},
     *  {@link RecordSpecImpl.BuilderImpl#addField(Type, CharSequence, Modifier...)}
     *  and
     *  {@link BuilderImpl#addField(TypeName, CharSequence, Modifier...)}.}</p>
     *  <p>Internal, {@code addField(Type, CharSequence, Modifier...)} and
     *  {@code addField(TypeName, CharSequence, Modifier...)} do call
     *  {@code addField(FieldSpec)}, so we will test only for the latter.</p>
     *  <p>A valid non-static field for a record is private and final, although
     *  the modifier {@code final} is not allowed. Static fields are either
     *  public, package-private or private, and they can be final.</p>
     *
     *  @param  modifierSet The modifiers for the test.
     *  @throws Exception   Something went unexpectedly wrong.
     */
    @ParameterizedTest
    @MethodSource( "invalidModifiersProvider" )
    final void testAddFieldWithInvalidArgument( final Set<Modifier> modifierSet ) throws Exception
    {
        skipThreadTest();

        final var composer = new JavaComposer();
        final var candidate = composer.recordBuilder( "Record" );

        final var type = String.class;
        final var name = "field";

        final Class<? extends Throwable> expectedException = ValidationException.class;

        final var modifiers = modifierSet.toArray( (Modifier []::new) );
        final var fieldSpec = composer.fieldBuilder( type, name, modifiers )
            .build();
        try
        {
            candidate.addField( fieldSpec );
            fail( () -> format( MSG_ExceptionNotThrown, expectedException.getName() ) );
        }
        catch( final AssertionError e )
        {
            throw e;
        }
        catch( final Throwable t )
        {
            final var isExpectedException = expectedException.isInstance( t );
            if( !isExpectedException )
            { t.printStackTrace( out ); }
            assertTrue( isExpectedException, () -> format( MSG_WrongExceptionThrown, expectedException.getName(), t.getClass().getName() ) );
        }
    }   //  testAddFieldWithInvalidArgument()

    /**
     *  Some tests for the methods
     *  {@link org.tquadrat.foundation.javacomposer.internal.RecordSpecImpl.BuilderImpl#addField(FieldSpec)},
     *  {@link org.tquadrat.foundation.javacomposer.internal.RecordSpecImpl.BuilderImpĺ#addField(Type, CharSequence, Modifier...)}
     *  and
     *  {@link org.tquadrat.foundation.javacomposer.internal.RecordSpecImpl.BuilderImpl#addField(TypeName, CharSequence, Modifier...)}.
     *
     *  @throws Exception   Something went unexpectedly wrong.
     */
    @Test
    final void testAddFieldWithNullArgument() throws Exception
    {
        skipThreadTest();

        final var composer = new JavaComposer();
        final var candidate = composer.recordBuilder( "Record" );

        Type type;
        TypeName typeName;
        CharSequence name;
        Modifier [] modifiers;

        final Class<? extends Throwable> expectedException = NullArgumentException.class;

        final FieldSpec fieldSpec = null;
        try
        {
            candidate.addField( fieldSpec );
            fail( () -> format( MSG_ExceptionNotThrown, expectedException.getName() ) );
        }
        catch( final AssertionError e )
        {
            throw e;
        }
        catch( final Throwable t )
        {
            final var isExpectedException = expectedException.isInstance( t );
            if( !isExpectedException )
            { t.printStackTrace( out ); }
            assertTrue( isExpectedException, () -> format( MSG_WrongExceptionThrown, expectedException.getName(), t.getClass().getName() ) );
        }

        type = null;
        typeName = null;
        name = "field";
        modifiers = Modifier.values();
        try
        {
            candidate.addField( type, name, modifiers );
            fail( () -> format( MSG_ExceptionNotThrown, expectedException.getName() ) );
        }
        catch( final AssertionError e )
        {
            throw e;
        }
        catch( final Throwable t )
        {
            final var isExpectedException = expectedException.isInstance( t );
            if( !isExpectedException )
            { t.printStackTrace( out ); }
            assertTrue( isExpectedException, () -> format( MSG_WrongExceptionThrown, expectedException.getName(), t.getClass().getName() ) );
        }
        try
        {
            candidate.addField( typeName, name, modifiers );
            fail( () -> format( MSG_ExceptionNotThrown, expectedException.getName() ) );
        }
        catch( final AssertionError e )
        {
            throw e;
        }
        catch( final Throwable t )
        {
            final var isExpectedException = expectedException.isInstance( t );
            if( !isExpectedException )
            { t.printStackTrace( out ); }
            assertTrue( isExpectedException, () -> format( MSG_WrongExceptionThrown, expectedException.getName(), t.getClass().getName() ) );
        }

        type = String.class;
        typeName = TypeName.from( type );
        name = null;
        modifiers = Modifier.values();
        try
        {
            candidate.addField( type, name, modifiers );
            fail( () -> format( MSG_ExceptionNotThrown, expectedException.getName() ) );
        }
        catch( final AssertionError e )
        {
            throw e;
        }
        catch( final Throwable t )
        {
            final var isExpectedException = expectedException.isInstance( t );
            if( !isExpectedException )
            { t.printStackTrace( out ); }
            assertTrue( isExpectedException, () -> format( MSG_WrongExceptionThrown, expectedException.getName(), t.getClass().getName() ) );
        }
        try
        {
            candidate.addField( typeName, name, modifiers );
            fail( () -> format( MSG_ExceptionNotThrown, expectedException.getName() ) );
        }
        catch( final AssertionError e )
        {
            throw e;
        }
        catch( final Throwable t )
        {
            final var isExpectedException = expectedException.isInstance( t );
            if( !isExpectedException )
            { t.printStackTrace( out ); }
            assertTrue( isExpectedException, () -> format( MSG_WrongExceptionThrown, expectedException.getName(), t.getClass().getName() ) );
        }

        type = String.class;
        typeName = TypeName.from( type );
        name = "field";
        modifiers = null;
        try
        {
            candidate.addField( type, name, modifiers );
            fail( () -> format( MSG_ExceptionNotThrown, expectedException.getName() ) );
        }
        catch( final AssertionError e )
        {
            throw e;
        }
        catch( final Throwable t )
        {
            final var isExpectedException = expectedException.isInstance( t );
            if( !isExpectedException )
            { t.printStackTrace( out ); }
            assertTrue( isExpectedException, () -> format( MSG_WrongExceptionThrown, expectedException.getName(), t.getClass().getName() ) );
        }
        try
        {
            candidate.addField( typeName, name, modifiers );
            fail( () -> format( MSG_ExceptionNotThrown, expectedException.getName() ) );
        }
        catch( final AssertionError e )
        {
            throw e;
        }
        catch( final Throwable t )
        {
            final var isExpectedException = expectedException.isInstance( t );
            if( !isExpectedException )
            { t.printStackTrace( out ); }
            assertTrue( isExpectedException, () -> format( MSG_WrongExceptionThrown, expectedException.getName(), t.getClass().getName() ) );
        }
    }   //  testAddFieldWithNullArgument()
}
//  class TestAddField

/*
 *  End of File
 */