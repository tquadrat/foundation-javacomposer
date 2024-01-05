/*
 * ============================================================================
 * Copyright © 2015 Square, Inc.
 * Copyright for the modifications © 2018-2024 by Thomas Thrien.
 * ============================================================================
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.tquadrat.foundation.javacomposer.internal;

import static java.lang.Character.isISOControl;
import static java.lang.String.format;
import static java.lang.Thread.currentThread;
import static org.apiguardian.api.API.Status.INTERNAL;
import static org.tquadrat.foundation.lang.Objects.checkState;
import static org.tquadrat.foundation.lang.Objects.requireNonNullArgument;

import javax.lang.model.element.Modifier;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.annotation.UtilityClass;
import org.tquadrat.foundation.exception.PrivateConstructorForStaticClassCalledError;
import org.tquadrat.foundation.exception.ValidationException;

/**
 *  Several utility functions to be used with JavaComposer.
 *
 *  @author Square,Inc.
 *  @modified Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: Util.java 1085 2024-01-05 16:23:28Z tquadrat $
 *  @since 0.0.5
 *
 *  @UMLGraph.link
 */
@SuppressWarnings( "NewClassNamingConvention" )
@UtilityClass
@ClassVersion( sourceVersion = "$Id: Util.java 1085 2024-01-05 16:23:28Z tquadrat $" )
public final class Util
{
        /*-----------*\
    ====** Constants **========================================================
        \*-----------*/
    /**
     *  The placeholder for {@code null} references.
     */
    @API( status = INTERNAL, since = "0.0.5" )
    public static final Object NULL_REFERENCE = new Object();

    /**
     *  The return value of
     *  {@link #createDebugOutput(boolean)}
     *  when no debug output is desired.
     */
    @SuppressWarnings( "OptionalUsedAsFieldOrParameterType" )
    @API( status = INTERNAL, since = "0.0.5" )
    public static final Optional<DebugOutput> NO_DEBUG_OUTPUT = Optional.empty();

        /*--------------*\
    ====** Constructors **=====================================================
        \*--------------*/
    /**
     *  No instance allowed for this class.
     */
    private Util() { throw new PrivateConstructorForStaticClassCalledError( Util.class ); }

        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  Translates the given character into a String; when that character is a
     *  special character, it will be escaped properly so that it can be used
     *  in a Java String literal.
     *
     *  @param  c   The input character.
     *  @return The target String.
     *
     *  @see <a href="https://docs.oracle.com/javase/specs/jls/se10/html/jls-3.html#jls-3.10.6">The Java Language Specification: 3.10.6. Escape Sequences for Character and String Literals </a>
     */
    @API( status = INTERNAL, since = "0.0.5" )
    public static final String characterLiteralWithoutSingleQuotes( final char c )
    {
        final var retValue = switch( c )
        {
            case '\b' -> "\\b"; // u0008: backspace (BS)
            case '\t' -> "\\t"; // u0009: horizontal tab (HT)
            case '\n' -> "\\n"; // u000a: linefeed (LF)
            case '\f' -> "\\f"; // u000c: form feed (FF)
            case '\r' -> "\\r"; // u000d: carriage return (CR)
            case '"' -> Character.toString( c ); // u0022: double quote (")
            case '\'' -> "\\'"; // u0027: single quote (')
            case '\\' -> "\\\\"; // u005c: backslash (\)
            default -> isISOControl( c ) ? format( "\\u%04x", (int) c ) : Character.toString( c );
        };

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  characterLiteralWithoutSingleQuotes()

    /**
     *  Creates the debug output.
     *
     *  @param  addDebugOutput  {@code true} if some debug output should be
     *      added to the generated code, {@code false} otherwise.
     *  @return An instance of
     *      {@link Optional}
     *      that holds the debug output; empty if the parameter
     *      {@code addDebugOutput} is {@code false}.
     *
     *  @see #NO_DEBUG_OUTPUT
     */
    @API( status = INTERNAL, since = "0.0.5" )
    public static final Optional<DebugOutput> createDebugOutput( final boolean addDebugOutput )
    {
        //---* Get the caller's caller *---------------------------------------
        final var retValue = addDebugOutput ? Optional.of( new DebugOutput( findCaller() ) ) : NO_DEBUG_OUTPUT;

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  createDebugOutput()

    /**
     *  <p>{@summary This method will find the method that makes the call into
     *  the Java Composer API and returns the appropriate stack trace
     *  element.}</p>
     *  <p>The respective method is determined by the package name of the
     *  containing class: it does <i>not</i> start with
     *  {@code org.tquadrat.foundation.javacomposer}.</p>
     *
     *  @return An instance of
     *      {@link Optional}
     *      that holds the stack trace element for the caller; will be empty if
     *      the call was internal.
     */
    @API( status = INTERNAL, since = "0.2.0" )
    private static final Optional<StackTraceElement> findCaller()
    {
        //---* Retrieve the stack *--------------------------------------------
        final var stackTraceElements = currentThread().getStackTrace();
        final var len = stackTraceElements.length;

        //---* Search the stack *----------------------------------------------
        Optional<StackTraceElement> retValue = Optional.empty();
        FindLoop: for( var i = 1; i < len; ++i )
        {
            final var className = stackTraceElements [i].getClassName();
            if( !className.startsWith( "org.tquadrat.foundation.javacomposer" ) )
            {
                retValue = Optional.of( stackTraceElements [i] );
                break FindLoop;
            }
        }   //  FindLoop:

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  findCaller()

    /**
     *  Returns the Java String literal representing {@code value}, including
     *  escaping double quotes.
     *
     *  @param  value   The input String.
     *  @param  indent  The indentation String that has to be added in case of
     *      a line break.
     *  @return The Java literal.
     */
    @API( status = INTERNAL, since = "0.0.5" )
    public static String stringLiteralWithDoubleQuotes( final String value, final String indent )
    {
        final var retValue = new StringBuilder( value.length() + 2 );
        retValue.append( '"' );
        ScanLoop: for( var i = 0; i < value.length(); ++i )
        {
            final var currentChar = value.charAt( i );

            //---* The trivial case: single quote must not be escaped *--------
            if( currentChar == '\'' )
            {
                retValue.append( "'" );
                continue ScanLoop;
            }

            //---* Another trivial case: double quotes must be escaped *-------
            if( currentChar == '"' )
            {
                retValue.append( '\\' ).append( '"' );
                continue ScanLoop;
            }

            /*
             * The default case: just let characterLiteralWithoutSingleQuotes()
             * do its work.
             */
            retValue.append( characterLiteralWithoutSingleQuotes( currentChar ) );

            //---* Do we need to append indent after linefeed? *---------------
            if( currentChar == '\n' && i + 1 < value.length() )
            {
                /*
                 * Originally, the indentation string was appended twice.
                 */
                retValue.append( "\"\n" ).append( indent ).append( "+ \"" );
            }
        }   //  ScanLoop:
        retValue.append( '"' );

        //---* Done *----------------------------------------------------------
        return retValue.toString();
    }   //  stringLiteralWithDoubleQuotes()

    /**
     *  Checks whether the given
     *  {@link Set}
     *  of
     *  {@link Modifier}
     *  instances does contain one and only one of the {@code Modifier}
     *  instances given with the second argument, {@code mutuallyExclusive}.
     *
     *  @param  modifiers   The set to check.
     *  @param  mutuallyExclusive   A list of values from which one and only
     *      one must be in the {@code modifiers} set.
     *  @throws ValidationException None or more than one {@code Modifier}
     *      instance was found.
     */
    @API( status = INTERNAL, since = "0.0.5" )
    public static final void requireExactlyOneOf( final Set<Modifier> modifiers, final Modifier... mutuallyExclusive ) throws ValidationException
    {
        requireNonNullArgument( modifiers, "modifiers" );
        final var count = (int) Arrays.stream( requireNonNullArgument( mutuallyExclusive, "mutuallyExclusive" ) )
            .filter( modifiers::contains )
            .count();
        checkState( count == 1, () -> new ValidationException( "modifiers %s must contain one of %s".formatted( modifiers, Arrays.toString( mutuallyExclusive ) ) ) );
    }   //  requireExactlyOneOf()

    /**
     *  Creates a new set with the combined contents of the given sets.
     *
     *  @param  <T> The type of the set elements.
     *  @param  firstSet    The first set.
     *  @param  secondSet   The second set.
     *  @return The combined set.
     */
    @SuppressWarnings( "TypeMayBeWeakened" )
    @API( status = INTERNAL, since = "0.0.5" )
    public static final <T> Set<T> union( final Set<? extends T> firstSet, final Set<? extends T> secondSet )
    {
        final Set<T> retValue = new LinkedHashSet<>( firstSet );
        retValue.addAll( secondSet );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  union()
}
//  class Util

/*
 *  End of File
 */