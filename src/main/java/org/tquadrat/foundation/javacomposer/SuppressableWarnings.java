/*
 * ============================================================================
 * Copyright © 2002-2021 by Thomas Thrien.
 * All Rights Reserved.
 * ============================================================================
 *
 * Licensed to the public under the agreements of the GNU Lesser General Public
 * License, version 3.0 (the "License"). You may obtain a copy of the License at
 *
 *      http://www.gnu.org/licenses/lgpl.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package org.tquadrat.foundation.javacomposer;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;
import static org.apiguardian.api.API.Status.STABLE;
import static org.tquadrat.foundation.lang.CommonConstants.EMPTY_STRING;
import static org.tquadrat.foundation.lang.Objects.requireNonNullArgument;
import static org.tquadrat.foundation.util.StringUtils.format;

import java.util.Optional;
import java.util.StringJoiner;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;

/**
 *  <p>{@summary Warnings that can be suppressed with the
 *  {@link SuppressWarnings &#64;SuppressWarnings}
 *  annotation.}</p>
 *  <p>There are different – partially overlapping – sets of these warnings
 *  and the arguments to the annotation; one is defined by Java directly,
 *  another one is defined by Eclipse, a third one by IntelliJ&nbsp;IDEA (see
 *  {@href https://gist.github.com/vegaasen/157fbc6dce8545b7f12c}
 *  for a – not exhausting – list). Other IDEs and/or Java distributions may
 *  add more.</p>
 *
 *  @see <a href="http://help.eclipse.org/photon/index.jsp?topic=%2Forg.eclipse.jdt.doc.user%2Ftasks%2Ftask-suppress_warnings.htm&cp=1_4_8_4">Eclipse documentation</a>
 *
 *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: SuppressableWarnings.java 947 2021-12-23 21:44:25Z tquadrat $
 *  @since 0.0.5
 *
 *  @UMLGraph.link
 */
@ClassVersion( sourceVersion = "$Id: SuppressableWarnings.java 947 2021-12-23 21:44:25Z tquadrat $" )
@API( status = STABLE, since = "0.0.5" )
public enum SuppressableWarnings
{
        /*------------------*\
    ====** Enum Declaration **=================================================
        \*------------------*/
    /**
     *  All warnings.
     */
    ALL( "all" ),

    /**
     *  An abstract class is not implemented in the current project scope.
     *  Common for libraries.
     */
    ABSTRACT_CLASS_NEVER_IMPLEMENTED( "AbstractClassNeverImplemented" ),

    /**
     *  A class was qualified as abstract without having any abstract methods.
     *  Happens sometimes for adapter classes.
     */
    ABSTRACT_CLASS_WITHOUT_ABSTRACT_METHODS( "AbstractClassWithoutAbstractMethods" ),

    /**
     *  A method accesses a non-public (private) field of another object.
     */
    ACCESSING_NON_PUBLIC_FIELD_OF_ANOTHER_OBJECT( "AccessingNonPublicFieldOfAnotherObject" ),

    /**
     *  A type variable bound is more restrictive than necessary; sometimes,
     *  this is wrong, in particular when the bound type is an {@code enum}.
     */
    BOUNDED_WILDCARD( "BoundedWildcard" ),

    /**
     *  Boxing/unboxing is used implicitly.
     */
    BOXING( "boxing" ),

    /**
     *  <p>{@summary Some possible cases in a {@code switch} statement have
     *  been omitted purposely.}</p>
     *  <p>This suppression is specific to Eclipse and works only as a comment,
     *  like this:</p>
     *  <pre><code>  …
     *  //$CASES-OMITTED$
     *  switch( … )
     *  {
     *      case …
     *      default …
     *  }</code></pre>
     *  <p>It cannot be used with
     *  {@link #createSuppressWarningsAnnotation(JavaComposer,SuppressableWarnings...)}
     *  nor with
     *  {@link #createSuppressWarningsCommentForIDEA(JavaComposer,SuppressableWarnings...)}.</p>
     */
    CASES_OMITTED( "$CASES-OMITTED$" ),

    /**
     *  An unnecessary cast is used.
     */
    CAST( "cast" ),

    /**
     *  An object is cast to a concrete class, instead to one of the interfaces
     *  implemented by that class.
     */
    CAST_TO_CONCRETE_CLASS( "CastToConcreteClass" ),

    /**
     *  <p>{@summary A class or interface references one of its own
     *  subclasses/implementations.} This happens for example in factory
     *  methods in the interface or base class.</p>
     *  <p>Although potentially dangerous, it can be done safely if interface
     *  or baseclass are sealed.</p>
     */
    CLASS_REFERENCES_SUBCLASS( "ClassReferencesSubclass" ),

    /**
     *  The current class has too many fields according to the configured
     *  metric value.
     */
    CLASS_WITH_TOO_MANY_FIELDS( "ClassWithTooManyFields" ),

    /**
     *  The current class or interface has too many methods according to the
     *  configured metric value.
     */
    CLASS_WITH_TOO_MANY_METHODS( "ClassWithTooManyMethods" ),

    /**
     *  A field or parameter is declared with a concrete collection class (like
     *  {@link java.util.HashSet HashSet})
     *  instead of the respective Interface
     *  ({@link java.util.Set Set}
     *  in this case).
     */
    COLLECTION_DECLARED_AS_CONCRETE_CLASS( "CollectionDeclaredAsConcreteClass" ),

    /**
     *  A necessary
     *  {@link Deprecated &#64;Deprecated}
     *  annotation is missing.
     *
     *  @note   To suppress this warning makes only sense on the class level
     *      when there are lots of deprecated methods in it, missing the
     *      {@code @Deprecated} annotation.
     */
    DEPRECATION_ANNOTATION( "dep-ann" ),

    /**
     *  A deprecated element is used.
     */
    DEPRECATION( "deprecation" ),

    /**
     *  A (private) attribute can be moved to be a local variable.
     */
    FIELD_CAN_BE_LOCAL( "FieldCanBeLocal" ),

    /**
     *  A private attribute of a class is never changed.
     */
    FIELD_MAY_BE_FINAL( "FieldMayBeFinal" ),

    /**
     *  <p>{@summary A component in a {@code for} loop was omitted.}</p>
     *  <p>The usual {@code for} loop looks like this:</p>
     *  <pre><code>  …
     *  for( var i = 0; i &lt; max; ++i )
     *  {
     *      …
     *  }
     *  …</code></pre>
     *  <p>But when using an instance of
     *  {@link java.util.Iterator},
     *  a common pattern looks like below:</p>
     *  <pre><code>  …
     *  Collection&lt;T&gt; c = …
     *  for( var i = c.iterator(); i.hasNext(); )
     *  {
     *     var t = i.next();
     *     …
     *  }
     *  …</code></pre>
     *  <p>The update for {@code i} is missing the header for the loop, and the
     *  compiler warn about that (or the IDE, whoever  …).</p>
     */
    FOR_LOOP_WITH_MISSING_COMPONENT( "ForLoopWithMissingComponent" ),

    /**
     *  A switch statement is incomplete.
     */
    INCOMPLETE_SWITCH( "incomplete-switch" ),

    /**
     *  The particular class is an inner class or interface of an interface.
     */
    INNER_CLASS_OF_INTERFACE( "InnerClassOfInterface" ),

    /**
     *  The attribute's type is of a concrete class instead of the interface
     *  that is implemented by that class.
     */
    INSTANCE_VARIABLE_OF_CONCRETE_CLASS( "InstanceVariableOfConcreteClass" ),

    /**
     *  An interface with just one non-default, non-static method syntactically
     *  can be used as a functional interface, but it does not make sense for
     *  all of these interfaces semantically.
     */
    INTERFACE_MAY_BE_ANNOTATED_FUNCTIONAL( "InterfaceMayBeAnnotatedFunctional" ),

    /**
     *  A required Javadoc comment is missing or the Javadoc is incomplete or
     *  invalid.
     */
    JAVADOC( "javadoc" ),

    /**
     *  A &quot;Magic&quot; character is used, instead of a constant or an
     *  enum.
     */
    MAGIC_CHARACTER( "MagicCharacter" ),

    /**
     *  A &quot;Magic&quot; number is used, instead of a constant.
     */
    MAGIC_NUMBER( "MagicNumber" ),

    /**
     *  The implementation of a  method (like
     *  {@link Object#clone()}
     *  for example) does not call the implementation of it in the super class,
     *  although this is expected.
     */
    METHOD_DOESNT_CALL_SUPER_METHOD( "MethodDoesntCallSuperMethod" ),

    /**
     *  JUnit assert statements, like {@code assertEquals()} do expect their
     *  arguments in a specific order, usually is the expected value the first
     *  argument, and the result from the test run is the second argument.
     */
    MISORDERED_ASSERT_EQUALS_ARGUMENTS( "MisorderedAssertEqualsArguments" ),

    /**
     *  Usually, a method that does nothing in meant to be overridden in an
     *  implementing class. If such a method is in an abstract class, it
     *  should be abstract, too. But for adapter classes, it is intended that
     *  the method is no-op and not abstract.
     */
    NOOP_METHOD_IN_ABSTRACT_CLASS( "NoopMethodInAbstractClass" ),

    /**
     *  <p>{@summary There is a call to
     *  {@link Optional#get()}
     *  without a previous check whether the {@code Optional} is not empty by
     *  calling
     *  {@link Optional#isPresent()}
     *  or
     *  {@link Optional#isEmpty()}.}</p>
     *  <p>This can be valid if it is well known otherwise that the
     *  {@code Optional} is not empty.</p>
     */
    OPTIONAL_GET_WITHOUT_IS_PRESENT( "OptionalGetWithoutIsPresent" ),

    /**
     *  The current class is too complex (cyclomatic complexity), according to
     *  the configured metric.
     */
    OVERLY_COMPLEX_CLASS( "OverlyComplexClass" ),

    /**
     *  The current class is too closely coupled to other classes, according to
     *  the configured metric.
     */
    OVERLY_COUPLED_CLASS("OverlyCoupledClass" ),

    /**
     *  A preview feature was used.
     */
    PREVIEW( "preview" ),

    /**
     *  A class implements one or more interfaces, but some of its public
     *  methods are not exposed via any of those interfaces. This happens is a
     *  common pattern with modularisation (Jigsaw), where internal classes are
     *  public to the whole module, but invisible to the users of the module.
     */
    PUBLIC_METHOD_NOT_EXPOSED_IN_INTERFACE( "PublicMethodNotExposedInInterface" ),

    /**
     *  The default (no-argument) constructor does nothing, and therefore it is
     *  not necessary to write it down explicitly in the code.
     */
    REDUNDANT_NO_ARG_CONSTRUCTOR( "RedundantNoArgConstructor" ),

    /**
     *  The variable's explicit type can be replaced by {@code var}.
     */
    REDUNDANT_EXPLICIT_VARIABLE_TYPE( "RedundantExplicitVariableType" ),

    /**
     *  An
     *  {@link AutoCloseable}
     *  was not closed.
     */
    RESOURCE( "resource" ),

    /**
     *  A method is called with always the same argument. Usually this means
     *  that the argument is obsolete.
     */
    SAME_PARAMETER_VALUE( "SameParameterValue" ),

    /**
     *  The {@code SerialVersionUID} for a serialisable class is missing.
     */
    SERIAL( "serial" ),

    /**
     *  The {@code SerialVersionUID} for a serialisable class is missing.
     */
    SIMPLIFY_STREAM_API_CALL_CHAIN( "SimplifyStreamApiCallChains" ),

    /**
     *  A static method is one class is exclusively called by one single other
     *  class. Usually this means that this method should be moved into the
     *  calling class.
     */
    STATIC_METHOD_ONLY_USED_IN_ONE_CLASS( "StaticMethodOnlyUsedInOneClass" ),

    /**
     *  The type of the argument to the {@code toArray()} method is unexpected.
     */
    SUSPICIOUS_TO_ARRAY_CALL( "SuspiciousToArrayCall" ),

    /**
     *  A thrown exception is caught within the same method.
     */
    THROW_CAUGHT_LOCALLY( "ThrowCaughtLocally" ),

    /**
     *  {@summary A more general type could be used} (e.g.
     *  {@link CharSequence}
     *  instead of
     *  {@link String}
     *  or
     *  {@link java.util.Collection}
     *  instead of
     *  {@link java.util.List}).
     */
    TYPE_MAY_BE_WEAKENED( "TypeMayBeWeakened" ),

    /**
     *  The type for the argument looks not correct.
     */
    UNLIKELY_ARG_TYPE( "unlikely-arg-type" ),

    /**
     *  The method or statement is performing an unchecked conversion for a
     *  generic data type.
     */
    UNCHECKED( "unchecked" ),

    /**
     *  An element is not used. This could be a parameter, a field, a private
     *  method …
     */
    UNUSED( "unused" ),

    /**
     *  No caller of the method ever regards its return value.
     */
    UNUSED_RETURN_VALUE( "UnusedReturnValue" ),

    /**
     *  <p>{@summary A concrete class is used for an attribute, a return value
     *  or an argument.} For a better decoupling, the use of interfaces only
     *  is preferred.
     */
    USE_OF_CONCRETE_CLASS( "UseOfConcreteClass" );

        /*-----------*\
    ====** Constants **========================================================
        \*-----------*/
    /**
     *  An empty array of {@code SuppressableWarnings} objects.
     *
     *  @deprecated Got obsolete.
     */
    @Deprecated( since = "0.2.0", forRemoval = true )
    public static final SuppressableWarnings [] EMPTY_SuppressableWarnings_ARRAY = new SuppressableWarnings [0];

        /*------------*\
    ====** Attributes **=======================================================
        \*------------*/
    /**
     *  The text for the suppressable warning as added to the annotation.
     */
    private final String m_Text;

        /*--------------*\
    ====** Constructors **=====================================================
        \*--------------*/
    /**
     *  Creates a new {@code SuppressableWarnings} instance.
     *
     *  @param  text    The text for the suppressable warning as added to the
     *      annotation.
     */
    private SuppressableWarnings( final String text )
    {
        m_Text = text.intern();
    }   //  SuppressableWarnings()

        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  Creates an
     *  {@link AnnotationSpec}
     *  instance for the
     *  {@link SuppressWarnings &#64;SuppressWarnings}
     *  annotation.
     *
     *  @param  factory The factory for the JavaComposer artefacts.
     *  @param  suppress    The tokens for the warnings to suppress.
     *  @return The annotation specification.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    public static final AnnotationSpec createSuppressWarningsAnnotation( final JavaComposer factory, final SuppressableWarnings... suppress )
    {
        final var argument = requireNonNullArgument( suppress, "suppress" ).length == 1
            ? format( "\"%s\"", suppress [0] )
            : stream( suppress )
                .map( SuppressableWarnings::toString )
                .sorted()
                .collect( joining( "\", \"", "{\"", "\"}" ) );
        final var retValue = requireNonNullArgument( factory, "factory" ).annotationBuilder( SuppressWarnings.class )
            .forceInline( true )
            .addMember( "value", argument )
            .build();

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  createSuppressWarningsAnnotation()

    /**
     *  Creates a comment (for IntelliJ IDEA) that suppresses the given
     *  warnings.
     *
     *  @param  factory The factory for the JavaComposer artefacts.
     *  @param  suppress    The tokens for the warnings to suppress.
     *  @return The
     *      {@link CodeBlock}
     *      with the comment.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    public static final CodeBlock createSuppressWarningsCommentForIDEA( final JavaComposer factory, final SuppressableWarnings... suppress )
    {
        requireNonNullArgument( factory, "factory" );

        final var joiner = new StringJoiner( " ", "//noinspection ", EMPTY_STRING ).setEmptyValue( EMPTY_STRING );
        for( final var s : requireNonNullArgument( suppress, "suppress" ) )
        {
            joiner.add( s.toString() );
        }
        final var retValue = joiner.length() > 0 ? factory.codeBlockOf( joiner.toString() ) : factory.emptyCodeBlock();

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  createSuppressWarningsCommentForIDEA()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final String toString() { return m_Text; }
}
//  class SuppressableWarnings

/*
 *  End of File
 */