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

import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;
import static org.apiguardian.api.API.Status.DEPRECATED;
import static org.tquadrat.foundation.javacomposer.Primitives.BOOLEAN;
import static org.tquadrat.foundation.javacomposer.Primitives.INT;
import static org.tquadrat.foundation.javacomposer.internal.Util.checkArgument;
import static org.tquadrat.foundation.lang.Objects.isNull;
import static org.tquadrat.foundation.lang.Objects.nonNull;
import static org.tquadrat.foundation.lang.Objects.requireNonNullArgument;
import static org.tquadrat.foundation.lang.Objects.requireNotEmptyArgument;

import javax.lang.model.SourceVersion;
import java.lang.reflect.Type;
import java.util.function.Supplier;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.annotation.UtilityClass;
import org.tquadrat.foundation.exception.PrivateConstructorForStaticClassCalledError;
import org.tquadrat.foundation.lang.Lazy;

/**
 *  This class provides some helper functions for the creation of Java code.
 *
 *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: Shortcuts.java 860 2021-01-27 23:08:28Z tquadrat $
 *  @since 0.0.5
 *
 *  @UMLGraph.link
 *
 *  @deprecated The methods from this class were moved to
 *      {@link JavaComposer}.
 */
@SuppressWarnings( "removal" )
@Deprecated( since = "0.2.0", forRemoval = true )
@UtilityClass
@ClassVersion( sourceVersion = "$Id: Shortcuts.java 860 2021-01-27 23:08:28Z tquadrat $" )
@API( status = DEPRECATED, since = "0.0.5" )
public final class Shortcuts
{
        /*-----------*\
    ====** Constants **========================================================
        \*-----------*/
    /**
     *  The Javadoc tag for inherited documentation: {@value}.
     *
     *  @deprecated Use
     *      {@link JavaComposer#JAVADOC_TAG_INHERITDOC}
     *      instead.
     */
    @Deprecated( since = "0.2.0", forRemoval = true )
    @API( status = DEPRECATED, since = "0.0.5" )
    public static final String JAVADOC_TAG_INHERITDOC = JavaComposer.JAVADOC_TAG_INHERITDOC;

    /**
     *  The Javadoc tag for a constant value: {@value}.
     *
     *  @deprecated Use
     *      {@link JavaComposer#JAVADOC_TAG_INHERITDOC}
     *      instead.
     */
    @Deprecated( since = "0.2.0", forRemoval = true )
    @API( status = DEPRECATED, since = "0.0.5" )
    public static final String JAVADOC_TAG_VALUE = JavaComposer.JAVADOC_TAG_VALUE;

        /*------------------------*\
    ====** Static Initialisations **===========================================
        \*------------------------*/
    /**
     *  The {@code @ClassVersion} annotation.
     *
     *  @deprecated The methods from this class were moved to
     *      {@link JavaComposer}.
     */
    @Deprecated( since = "0.2.0", forRemoval = true )
    @API( status = DEPRECATED, since = "0.0.5" )
    private static final Lazy<AnnotationSpec> m_Annotation_ClassVersion;

    /**
     *  The {@code @UtilityClass} annotation.
     *
     *  @deprecated The methods from this class were moved to
     *      {@link JavaComposer}.
     */
    @Deprecated( since = "0.2.0", forRemoval = true )
    @API( status = DEPRECATED, since = "0.1.0" )
    private static final Lazy<AnnotationSpec> m_Annotation_UtilityClass;

    /**
     *  The Javadoc comment for an overriding method.
     *
     *  @deprecated The methods from this class were moved to
     *      {@link JavaComposer}.
     */
    @Deprecated( since = "0.2.0", forRemoval = true )
    @API( status = DEPRECATED, since = "0.0.5" )
    private static final Lazy<CodeBlock> m_JavaDoc_InheritDoc;

    /**
     *  A predefined {@code equals()} method.
     *
     *  @deprecated The methods from this class were moved to
     *      {@link JavaComposer}.
     */
    @Deprecated( since = "0.2.0", forRemoval = true )
    @API( status = DEPRECATED, since = "0.0.5" )
    private static final Lazy<MethodSpec> m_Method_Equals;

    /**
     *  A predefined {@code hashCode()} method.
     *
     *  @deprecated The methods from this class were moved to
     *      {@link JavaComposer}.
     */
    @Deprecated( since = "0.2.0", forRemoval = true )
    @API( status = DEPRECATED, since = "0.0.5" )
    private static final Lazy<MethodSpec> m_Method_HashCode;

    /**
     *  A predefined {@code toString()} method.
     *
     *  @deprecated The methods from this class were moved to
     *      {@link JavaComposer}.
     */
    @Deprecated( since = "0.2.0", forRemoval = true )
    @API( status = DEPRECATED, since = "0.0.5" )
    private static final Lazy<MethodSpec> m_Method_ToString;

    static
    {
        //---* The inherited doc comment *-------------------------------------
        final Supplier<CodeBlock> codeBlockSupplier = () ->
        {
            final var retValue = CodeBlock.builder()
                .add( "$L\n", JAVADOC_TAG_INHERITDOC )
                .build();

            //---* Done *------------------------------------------------------
            return retValue;
        };
        m_JavaDoc_InheritDoc = Lazy.use( codeBlockSupplier );

        //---* The @ClassVersion annotation *----------------------------------
        final Supplier<AnnotationSpec> annotationSpecSupplierClassVersion = () ->
        {
            final var retValue = AnnotationSpec.builder( ClassVersion.class )
                .forceInline( true )
                .addMember( "sourceVersion", "$S", "Generated with JavaComposer" )
                .addMember( "isGenerated", "$L", "true" )
                .build();

            //---* Done *------------------------------------------------------
            return retValue;
        };
        m_Annotation_ClassVersion = Lazy.use( annotationSpecSupplierClassVersion );

        //---* The @ClassVersion annotation *----------------------------------
        final Supplier<AnnotationSpec> annotationSpecSupplierUtilityClass = () ->
        {
            final var retValue = AnnotationSpec.builder( UtilityClass.class )
                .forceInline( true )
                .build();

            //---* Done *------------------------------------------------------
            return retValue;
        };
        m_Annotation_UtilityClass = Lazy.use( annotationSpecSupplierUtilityClass );

        //---* The equals() method *-------------------------------------------
        Supplier<MethodSpec> methodSpecSupplier = () ->
        {
            final var retValue = MethodSpec.methodBuilder( "equals" )
                .addJavadoc( m_JavaDoc_InheritDoc.get() )
                .addAnnotation( Override.class )
                .addModifiers( PUBLIC )
                .addParameter( Object.class, "o", FINAL )
                .returns( BOOLEAN )
                .build();

            //---* Done *------------------------------------------------------
            return retValue;
        };
        m_Method_Equals = Lazy.use( methodSpecSupplier );

        //---* The hashCode() method *-----------------------------------------
        methodSpecSupplier = () ->
        {
            final var retValue = MethodSpec.methodBuilder( "hashCode" )
                .addJavadoc( m_JavaDoc_InheritDoc.get() )
                .addAnnotation( Override.class )
                .addModifiers( PUBLIC )
                .returns( INT )
                .build();

            //---* Done *------------------------------------------------------
            return retValue;
        };
        m_Method_HashCode = Lazy.use( methodSpecSupplier );

        //---* The toString() method *-----------------------------------------
        methodSpecSupplier = () ->
        {
            final var retValue = MethodSpec.methodBuilder( "toString" )
                .addJavadoc( m_JavaDoc_InheritDoc.get() )
                .addAnnotation( Override.class )
                .addModifiers( PUBLIC )
                .returns( String.class )
                .build();

            //---* Done *------------------------------------------------------
            return retValue;
        };
        m_Method_ToString = Lazy.use( methodSpecSupplier );
    }

        /*--------------*\
    ====** Constructors **=====================================================
        \*--------------*/
    /**
     *  No instance allowed for this class.
     */
    private Shortcuts() { throw new PrivateConstructorForStaticClassCalledError( Shortcuts.class ); }

        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  Creates a comment for constant.
     *
     *  @param  comment The already existing comment; can be {@code null}.
     *  @return The comment for the new constant.
     *
     *  @deprecated The methods from this class were moved to
     *      {@link JavaComposer}.
     */
    @Deprecated( since = "0.2.0", forRemoval = true )
    @API( status = DEPRECATED, since = "0.0.5" )
    private static final CodeBlock createComment4Constant( final CodeBlock comment )
    {
        var retValue = comment;
        if( isNull( comment ) )
        {
            retValue = CodeBlock.of( "Constant value: $L.\n", JAVADOC_TAG_VALUE );
        }
        else
        {
            if( !comment.toString().contains( JAVADOC_TAG_VALUE ) )
            {
                retValue = comment.toBuilder()
                    .add( """
                          
                          <p>The value is: $L.</p>
                          """, JAVADOC_TAG_VALUE )
                    .build();
            }
        }

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  createComment4Constant()

    /**
     *  <p>{@summary Creates a String constant.}</p>
     *  <p>A constant is an initialised {@code public static final} field.</p>
     *
     *  @param  name    The name of the constant.
     *  @param  value   The value of the constant.
     *  @param  comment The description for the constant.
     *  @return The field spec for the new constant.
     *
     *  @deprecated The methods from this class were moved to
     *      {@link JavaComposer}.
     */
    @Deprecated( since = "0.2.0", forRemoval = true )
    @API( status = DEPRECATED, since = "0.0.5" )
    public static final FieldSpec createConstant( final CharSequence name, final String value, final CodeBlock comment )
    {
        final var retValue = createConstant( name, String.class, CodeBlock.of( "$S", requireNonNullArgument( value, "value" ) ), createComment4Constant( comment ) );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  createConstant()

    /**
     *  <p>{@summary Creates a numerical constant.}</p>
     *  <p>A constant is an initialised {@code public static final} field.</p>
     *
     *  @param  name    The name of the constant.
     *  @param  type    The type of the constant.
     *  @param  value   The value of the constant.
     *  @param  comment The description for the constant.
     *  @return The field spec for the new constant.
     *
     *  @deprecated The methods from this class were moved to
     *      {@link JavaComposer}.
     */
    @Deprecated( since = "0.2.0", forRemoval = true )
    @API( status = DEPRECATED, since = "0.0.5" )
    @SuppressWarnings( "SameParameterValue" )
    private static final FieldSpec createConstant( final CharSequence name, final TypeName type, final Number value, final CodeBlock comment )
    {
        final var retValue = createConstant( name, type, CodeBlock.of( "$L", requireNonNullArgument( value, "value" ) ), createComment4Constant( comment ) );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  createConstant()

    /**
     *  <p>{@summary Creates an integer constant.}</p>
     *  <p>A constant is an initialised {@code public static final} field.</p>
     *
     *  @param  name    The name of the constant.
     *  @param  value   The value of the constant.
     *  @param  comment The description for the constant.
     *  @return The field spec for the new constant.
     *
     *  @deprecated The methods from this class were moved to
     *      {@link JavaComposer}.
     */
    @Deprecated( since = "0.2.0", forRemoval = true )
    @API( status = DEPRECATED, since = "0.0.5" )
    public static final FieldSpec createConstant( final CharSequence name, final int value, final CodeBlock comment )
    {
        final var retValue = createConstant( name, INT, Integer.valueOf( value ), comment );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  createConstant()

    /**
     *  <p>{@summary Creates a constant.}</p>
     *  <p>A constant is an initialised {@code public static final} field.</p>
     *
     *  @param  name    The name of the constant.
     *  @param  type    The type of the constant.
     *  @param  value   The value of the constant.
     *  @param  comment The description for the constant.
     *  @return The field spec for the new constant.
     *
     *  @deprecated The methods from this class were moved to
     *      {@link JavaComposer}.
     */
    @Deprecated( since = "0.2.0", forRemoval = true )
    @API( status = DEPRECATED, since = "0.0.5" )
    public static final FieldSpec createConstant( final CharSequence name, final Type type, final CodeBlock value, final CodeBlock comment )
    {
        return createConstant( name, TypeName.get( requireNonNullArgument( type, "type" ) ), value, comment );
    }   //  createConstant()

    /**
     *  Creates a constant.<br>
     *  <br>A constant is an initialised {@code public final static} field.
     *
     *  @param  name    The name of the constant.
     *  @param  type    The type of the constant.
     *  @param  value   The value of the constant.
     *  @param  comment The description for the constant.
     *  @return The field spec for the new constant.
     *
     *  @deprecated The methods from this class were moved to
     *      {@link JavaComposer}.
     */
    @Deprecated( since = "0.2.0", forRemoval = true )
    @API( status = DEPRECATED, since = "0.0.5" )
    public static final FieldSpec createConstant( final CharSequence name, final TypeName type, final CodeBlock value, final CodeBlock comment )
    {
        final var builder = FieldSpec.builder( type, name, PUBLIC, FINAL, STATIC )
            .initializer( requireNonNullArgument( value, "value" ) );

        if( nonNull( comment ) && !comment.isEmpty() ) builder.addJavadoc( comment );

        final var retValue = builder.build();

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  createConstant()

    /**
     *  Creates a return statement with a comment, using {@code retValue} as
     *  the name for the return variable.
     *
     *  @return The return statement.
     *
     *  @deprecated The methods from this class were moved to
     *      {@link JavaComposer}.
     */
    @Deprecated( since = "0.2.0", forRemoval = true )
    @API( status = DEPRECATED, since = "0.0.5" )
    public static final CodeBlock createReturnStatement() { return createReturnStatement( "retValue" ); }

    /**
     *  Creates a return statement with a comment.
     *
     *  @param  name    The name of the variable that is returned.
     *  @return The return statement.
     *
     *  @deprecated The methods from this class were moved to
     *      {@link JavaComposer}.
     */
    @Deprecated( since = "0.2.0", forRemoval = true )
    @API( status = DEPRECATED, since = "0.0.5" )
    public static final CodeBlock createReturnStatement( final String name )
    {
        final var retValue = CodeBlock.builder()
            .add( """
                  
                  //---* Done *----------------------------------------------------------
                  """ )
            .addStatement( "return $L", requireNotEmptyArgument( name, "name" ) )
            .build();

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  createReturnStatement()

    /**
     *  Returns a {@code @ClassVersion} annotation.
     *
     *  @return The annotation.
     *
     *  @deprecated The methods from this class were moved to
     *      {@link JavaComposer}.
     */
    @Deprecated( since = "0.2.0", forRemoval = true )
    @API( status = DEPRECATED, since = "0.0.5" )
    public static final AnnotationSpec getClassVersionAnnotation() { return m_Annotation_ClassVersion.get(); }

    /**
     *  <p>{@summary Returns a builder for an implementation of the method
     *  {@link Object#equals(Object)}
     *  that just needs the method body for completion.}</p>
     *  <p>The argument has the name &quot;{@code o}&quot;.</p>
     *
     *  @return The method builder.
     *
     *  @deprecated The methods from this class were moved to
     *      {@link JavaComposer}.
     */
    @Deprecated( since = "0.2.0", forRemoval = true )
    @API( status = DEPRECATED, since = "0.0.5" )
    public static final MethodSpec.Builder getEqualsBuilder()
    {
        final var retValue = m_Method_Equals.get().toBuilder();

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  getEqualsBuilder()

    /**
     *  <p>{@summary Returns a
     *  {@link MethodSpec}
     *  instance for an implementation of the method
     *  {@link Object#equals(Object)}.}</p>
     *  <p>The argument has the name &quot;{@code o}&quot;.</p>
     *
     *  @param  body    The method body.
     *  @return The method specification.
     *
     *  @deprecated The methods from this class were moved to
     *      {@link JavaComposer}.
     */
    @Deprecated( since = "0.2.0", forRemoval = true )
    @API( status = DEPRECATED, since = "0.0.5" )
    public static final MethodSpec getEqualsMethod( final CodeBlock body )
    {
        final var retValue = getEqualsBuilder()
            .addCode( requireNonNullArgument( body, "body" ) )
            .build();

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  getEqualsMethod()

    /**
     *  Returns a builder for an implementation of the method
     *  {@link Object#hashCode()}
     *  that just needs the method body for completion.
     *
     *  @return The method builder.
     *
     *  @deprecated The methods from this class were moved to
     *      {@link JavaComposer}.
     */
    @Deprecated( since = "0.2.0", forRemoval = true )
    @API( status = DEPRECATED, since = "0.0.5" )
    public static final MethodSpec.Builder getHashCodeBuilder()
    {
        final var retValue = m_Method_HashCode.get().toBuilder();

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  getHashCodeBuilder()

    /**
     *  Returns a
     *  {@link MethodSpec}
     *  instance for an implementation of the method
     *  {@link Object#hashCode()}.
     *
     *  @param  body    The method body.
     *  @return The method specification.
     *
     *  @deprecated The methods from this class were moved to
     *      {@link JavaComposer}.
     */
    @Deprecated( since = "0.2.0", forRemoval = true )
    @API( status = DEPRECATED, since = "0.0.5" )
    public static final MethodSpec getHashCodeMethod( final CodeBlock body )
    {
        final var retValue = getHashCodeBuilder()
            .addCode( requireNonNullArgument( body, "body" ) )
            .build();

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  getHashCodeMethod()

    /**
     *  Returns the a code block with a comment for overriding methods:
     *  <pre><code>  &#47;**
     *   * {&#64;inheritDoc}
     *   *&#47;</code></pre>
     *
     *  @return The comment.
     *
     *  @deprecated The methods from this class were moved to
     *      {@link JavaComposer}.
     */
    @Deprecated( since = "0.2.0", forRemoval = true )
    @API( status = DEPRECATED, since = "0.0.5" )
    public static final CodeBlock getInheritDocComment() { return m_JavaDoc_InheritDoc.get(); }

    /**
     *  <p>{@summary Returns a builder for a static class.}</p>
     *  <p>A <i>static class</i> is a {@code final} class with a
     *  {@code private} constructor that has only {@code static} members; no
     *  instances are allowed for such a class, so none of the {@code static}
     *  methods are factories for that class.</p>
     *  <p>This would be the skeleton for the new static class:</p>
     *  <pre><code>  &lt;package <i>what.ever.package.was.chosen</i>&gt;;
     *
     *  import org.tquadrat.foundation.annotation.ClassVersion;
     *  import org.tquadrat.foundation.annotation.UtilityClass;
     *  import org.tquadrat.foundation.exception.PrivateConstructorForStaticClassCalledError;
     *
     *  &#64;UtilityClass
     *  &#64;ClassVersion(sourceVersion = "Generated with JavaComposer", isGenerated = true)
     *  public final class StaticClass {
     *    &#47;**
     *     * No instance allowed for this class!
     *     *&#47;
     *    private StaticClass() {
     *      throw new PrivateConstructorForStaticClassCalledError( StaticClass.class );
     *    }
     *  }</code></pre>
     *
     *  @param  className   The name of the new class.
     *  @return The builder.
     *
     *  @deprecated The methods from this class were moved to
     *      {@link JavaComposer}.
     */
    @Deprecated( since = "0.2.0", forRemoval = true )
    @API( status = DEPRECATED, since = "0.0.5" )
    public static final TypeSpec.Builder getStaticClassBuilder( final CharSequence className )
    {
        checkArgument( SourceVersion.isName( requireNotEmptyArgument( className, "className" ) ), "not a valid name: %s", className );
        final var constructor = MethodSpec.constructorBuilder()
            .addModifiers( PRIVATE )
            .addJavadoc( "No instance allowed for this class!\n" )
            .addStatement( "throw new $T( $L.class )", PrivateConstructorForStaticClassCalledError.class, className )
            .build();
        final var retValue = TypeSpec.classBuilder( className )
            .addModifiers( PUBLIC, FINAL )
            .addAnnotation( getUtilityClassAnnotation() )
            .addAnnotation( getClassVersionAnnotation() )
            .addMethod( constructor );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  getStaticClassBuilder()

    /**
     *  Returns a builder for an implementation of the method
     *  {@link Object#toString()}
     *  that just needs the method body for completion.
     *
     *  @return The method builder.
     *
     *  @deprecated The methods from this class were moved to
     *      {@link JavaComposer}.
     */
    @Deprecated( since = "0.2.0", forRemoval = true )
    @API( status = DEPRECATED, since = "0.0.5" )
    public static final MethodSpec.Builder getToStringBuilder()
    {
        final var retValue = m_Method_ToString.get().toBuilder();

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  getToStringBuilder()

    /**
     *  Returns a
     *  {@link MethodSpec}
     *  instance for an implementation of the method
     *  {@link Object#toString()}.
     *
     *  @param  body    The method body.
     *  @return The method specification.
     *
     *  @deprecated The methods from this class were moved to
     *      {@link JavaComposer}.
     */
    @Deprecated( since = "0.2.0", forRemoval = true )
    @API( status = DEPRECATED, since = "0.0.5" )
    public static final MethodSpec getToStringMethod( final CodeBlock body )
    {
        final var retValue = getToStringBuilder()
            .addCode( requireNonNullArgument( body, "body" ) )
            .build();

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  getToStringMethod()

    /**
     *  Returns a {@code @UtilityClass} annotation.
     *
     *  @return The annotation.
     *
     * @since 0.1.0
     *
     *  @deprecated The methods from this class were moved to
     *      {@link JavaComposer}.
     */
    @Deprecated( since = "0.2.0", forRemoval = true )
    @API( status = DEPRECATED, since = "0.1.0" )
    public static final AnnotationSpec getUtilityClassAnnotation() { return m_Annotation_UtilityClass.get(); }
}
//  class Shortcuts

/*
 *  End of File
 */