/*
 * ============================================================================
 *  Copyright © 2002-2024 by Thomas Thrien.
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

import static java.lang.String.format;
import static java.util.Arrays.sort;
import static java.util.Arrays.stream;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.joining;
import static javax.lang.model.element.Modifier.ABSTRACT;
import static javax.lang.model.element.Modifier.DEFAULT;
import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;
import static org.apiguardian.api.API.Status.STABLE;
import static org.tquadrat.foundation.javacomposer.Layout.LAYOUT_DEFAULT;
import static org.tquadrat.foundation.javacomposer.MethodSpec.CONSTRUCTOR;
import static org.tquadrat.foundation.javacomposer.Primitives.BOOLEAN;
import static org.tquadrat.foundation.javacomposer.Primitives.INT;
import static org.tquadrat.foundation.lang.CommonConstants.EMPTY_STRING;
import static org.tquadrat.foundation.lang.Objects.deepEquals;
import static org.tquadrat.foundation.lang.Objects.hash;
import static org.tquadrat.foundation.lang.Objects.isNull;
import static org.tquadrat.foundation.lang.Objects.nonNull;
import static org.tquadrat.foundation.lang.Objects.require;
import static org.tquadrat.foundation.lang.Objects.requireNonNullArgument;
import static org.tquadrat.foundation.lang.Objects.requireNotEmptyArgument;
import static org.tquadrat.foundation.lang.Objects.requireValidNonNullArgument;
import static org.tquadrat.foundation.util.JavaUtils.translateModifiers;

import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.util.Types;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.annotation.UtilityClass;
import org.tquadrat.foundation.exception.PrivateConstructorForStaticClassCalledError;
import org.tquadrat.foundation.exception.ValidationException;
import org.tquadrat.foundation.javacomposer.internal.AnnotationSpecImpl;
import org.tquadrat.foundation.javacomposer.internal.AnnotationSpecImpl.BuilderImpl;
import org.tquadrat.foundation.javacomposer.internal.AnnotationTypeSpecImpl;
import org.tquadrat.foundation.javacomposer.internal.AnnotationValueVisitor;
import org.tquadrat.foundation.javacomposer.internal.ClassNameImpl;
import org.tquadrat.foundation.javacomposer.internal.ClassSpecImpl;
import org.tquadrat.foundation.javacomposer.internal.CodeBlockImpl;
import org.tquadrat.foundation.javacomposer.internal.CodeProcessorImpl;
import org.tquadrat.foundation.javacomposer.internal.EnumSpecImpl;
import org.tquadrat.foundation.javacomposer.internal.FieldSpecImpl;
import org.tquadrat.foundation.javacomposer.internal.InterfaceSpecImpl;
import org.tquadrat.foundation.javacomposer.internal.JavaFileImpl;
import org.tquadrat.foundation.javacomposer.internal.LambdaSpecImpl;
import org.tquadrat.foundation.javacomposer.internal.MethodSpecImpl;
import org.tquadrat.foundation.javacomposer.internal.ParameterSpecImpl;
import org.tquadrat.foundation.javacomposer.internal.RecordSpecImpl;
import org.tquadrat.foundation.javacomposer.internal.TypeNameImpl;
import org.tquadrat.foundation.javacomposer.internal.TypeSpecImpl;
import org.tquadrat.foundation.lang.Lazy;
import org.tquadrat.foundation.lang.Objects;
import org.tquadrat.foundation.util.JavaUtils;

/**
 *  <p>{@summary The factory for the various JavaComposer artefacts.}</p>
 *  <p>Instances of this class do not maintain a state, therefore they are
 *  thread-safe without any synchronisation.</p>
 *
 *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: JavaComposer.java 1105 2024-02-28 12:58:46Z tquadrat $
 *  @since 0.2.0
 *
 *  @UMLGraph.link
 */
@SuppressWarnings( {"OverlyCoupledClass", "ClassWithTooManyMethods", "OverlyComplexClass", "ClassWithTooManyFields"} )
@ClassVersion( sourceVersion = "$Id: JavaComposer.java 1105 2024-02-28 12:58:46Z tquadrat $" )
@API( status = STABLE, since = "0.2.0" )
public final class JavaComposer
{
        /*-----------*\
    ====** Constants **========================================================
        \*-----------*/
    /**
     *  The Javadoc tag for inherited documentation: {@value}.
     */
    public static final String JAVADOC_TAG_INHERITDOC = "{@inheritDoc}";

    /**
     *  The Javadoc tag for a constant value: {@value}.
     */
    public static final String JAVADOC_TAG_VALUE = "{@value}";

    /**
     *  Message: {@value}.
     */
    public static final String MSG_CannotOverrideFinalClass = "Cannot override method on final class '%s'";

    /**
     *  Message: {@value}.
     */
    public static final String MSG_CannotOverrideMethod = "Cannot override method with modifier '%s'";

        /*------------*\
    ====** Attributes **=======================================================
        \*------------*/
    /**
     *  If set to {@code true}, some debug information will be added to the
     *  output.
     */
    private final boolean m_AddDebugOutput;

    /**
     *  The {@code @ClassVersion} annotation.
     */
    @SuppressWarnings( "FieldNamingConvention" )
    private final Lazy<AnnotationSpec> m_Annotation_ClassVersion;

    /**
     *  The {@code @UtilityClass} annotation.
     */
    @SuppressWarnings( "FieldNamingConvention" )
    private final Lazy<AnnotationSpec> m_Annotation_UtilityClass;

    /**
     *  The code processor associated with this composer instance.
     */
    private final CodeProcessor m_CodeProcessor;

    /**
     *  The Javadoc comment for an overriding method.
     */
    @SuppressWarnings( "FieldNamingConvention" )
    private final Lazy<CodeBlock> m_JavaDoc_InheritDoc;

    /**
     *  The layout that is used to format the output.
     */
    private final Layout m_Layout;

    /**
     *  The maximum number of fields for a class; if this number is exceeded,
     *  an annotation
     *  {@link SuppressWarnings &#64;SuppressWarnings}
     *  with
     *  {@link SuppressableWarnings#CLASS_WITH_TOO_MANY_FIELDS}
     *  will be added to the class.
     */
    @SuppressWarnings( "MagicNumber" )
    private int m_MaxFields = 16;

    /**
     *  The maximum number of methods for a class; if this number is exceeded,
     *  an annotation
     *  {@link SuppressWarnings &#64;SuppressWarnings}
     *  with
     *  {@link SuppressableWarnings#CLASS_WITH_TOO_MANY_METHODS}
     *  will be added to the class.
     */
    @SuppressWarnings( "MagicNumber" )
    private int m_MaxMethods = 20;

    /**
     *  A predefined {@code equals()} method.
     */
    @SuppressWarnings( "FieldNamingConvention" )
    private final Lazy<MethodSpec> m_Method_Equals;

    /**
     *  A predefined {@code hashCode()} method.
     */
    @SuppressWarnings( "FieldNamingConvention" )
    private final Lazy<MethodSpec> m_Method_HashCode;

    /**
     *  A predefined {@code toString()} method.
     */
    @SuppressWarnings( "FieldNamingConvention" )
    private final Lazy<MethodSpec> m_Method_ToString;

        /*--------------*\
    ====** Constructors **=====================================================
        \*--------------*/
    /**
     *  Creates a new instance of {@code JavaComposer} that uses the default
     *  {@link Layout#LAYOUT_DEFAULT Layout}.
     */
    public JavaComposer()
    {
        this( LAYOUT_DEFAULT, false );
    }   //  JavaComposer()

    /**
     *  Creates a new instance of {@code JavaComposer}.
     *
     *  @param  layout  The layout that is used to format the output.
     */
    public JavaComposer( final Layout layout )
    {
        this( layout, false );
    }   //  JavaComposer()

    /**
     *  Creates a new instance of {@code JavaComposer} that uses the default
     *  {@link Layout#LAYOUT_DEFAULT Layout} and adds some debug information
     *  to the output.
     *
     *  @param  addDebugOutput  {@code true} if debug information should be
     *      added,  {@code false} if not.
     */
    public JavaComposer( final boolean addDebugOutput )
    {
        this( LAYOUT_DEFAULT, addDebugOutput );
    }   //  JavaComposer()

    /**
     *  Creates a new instance of {@code JavaComposer}.
     *
     *  @param  layout  The layout that is used to format the output.
     *  @param  addDebugOutput  {@code true} if debug information should be
     *      added,  {@code false} if not.
     */
    public JavaComposer( final Layout layout, final boolean addDebugOutput )
    {
        m_Layout = requireNonNullArgument( layout, "layout" );
        m_AddDebugOutput = addDebugOutput;

        //---* The inherited doc comment *-------------------------------------
        final var codeBlockSupplier = (Supplier<CodeBlock>) () ->
        {
            final var retValue = new CodeBlockImpl.BuilderImpl( this )
                .addWithoutDebugInfo( "$L\n", JAVADOC_TAG_INHERITDOC )
                .build();

            //---* Done *------------------------------------------------------
            return retValue;
        };
        m_JavaDoc_InheritDoc = Lazy.use( codeBlockSupplier );
        final var inheritDocComment = codeBlockSupplier.get();

        //---* The @ClassVersion annotation *----------------------------------
        final var annotationSpecSupplierClassVersion = (Supplier<AnnotationSpec>) () ->
        {
            final var retValue = annotationBuilder( ClassVersion.class )
                .forceInline( true )
                .addMember( "sourceVersion", "$S", "Generated with JavaComposer" )
                .addMember( "isGenerated", "$L", "true" )
                .build();

            //---* Done *------------------------------------------------------
            return retValue;
        };
        m_Annotation_ClassVersion = Lazy.use( annotationSpecSupplierClassVersion );

        //---* The @UtilityClass annotation *----------------------------------
        final var annotationSpecSupplierUtilityClass = (Supplier<AnnotationSpec>) () ->
        {
            final var retValue = annotationBuilder( UtilityClass.class )
                .forceInline( true )
                .build();

            //---* Done *------------------------------------------------------
            return retValue;
        };
        m_Annotation_UtilityClass = Lazy.use( annotationSpecSupplierUtilityClass );

        //---* The equals() method *-------------------------------------------
        var methodSpecSupplier = (Supplier<MethodSpec>) () ->
        {
            final var retValue = methodBuilder( "equals" )
                .addJavadoc( inheritDocComment )
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
            final var retValue = methodBuilder( "hashCode" )
                .addJavadoc( inheritDocComment )
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
            final var retValue = methodBuilder( "toString" )
                .addJavadoc( inheritDocComment )
                .addAnnotation( Override.class )
                .addModifiers( PUBLIC )
                .returns( String.class )
                .build();

            //---* Done *------------------------------------------------------
            return retValue;
        };
        m_Method_ToString = Lazy.use( methodSpecSupplier );

        //---* The code processor *--------------------------------------------
        //noinspection ThisEscapedInObjectConstruction
        m_CodeProcessor = new CodeProcessorImpl( this );
    }   //  JavaComposer()

        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  Returns the flag that controls whether the output should be enhanced
     *  with some debug information.
     *
     *  @return {@code true} if the debug information should be added to the
     *      output, {@code false} otherwise.
     */
    @SuppressWarnings( "BooleanMethodNameMustStartWithQuestion" )
    public final boolean addDebugOutput() { return m_AddDebugOutput; }

    /**
     *  <p>{@summary Creates a builder for an instance of an implementation for
     *  {@link AnnotationSpec}
     *  from the given
     *  {@link ClassName}
     *  instance.}</p>
     *  <p>This builder generates the code for the <i>use</i> of an annotation,
     *  while the builders returned from
     *  {@link #annotationTypeBuilder(CharSequence)}
     *  and
     *  {@link #annotationTypeBuilder(ClassName)}
     *  will generate <i>new</i> annotation types.</p>
     *
     *  @param  type    The class name.
     *  @return The new builder.
     */
    public final AnnotationSpec.Builder annotationBuilder( final ClassName type )
    {
        final var retValue = new BuilderImpl( this, requireNonNullArgument( type, "type" ) );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  annotationBuilder()

    /**
     *  <p>{@summary Creates a builder for an instance of an implementation for
     *  {@link AnnotationSpec}
     *  from the given
     *  {@link Class}
     *  instance.}</p>
     *  <p>This builder generates the code for the <i>use</i> of an annotation,
     *  while the builders returned from
     *  {@link #annotationTypeBuilder(CharSequence)}
     *  and
     *  {@link #annotationTypeBuilder(ClassName)}
     *  will generate <i>new</i> annotation types.</p>
     *
     *  @param  type    The class.
     *  @return The new builder.
     */
    public final AnnotationSpec.Builder annotationBuilder( final Class<?> type )
    {
        final var retValue = annotationBuilder( ClassName.from( type ) );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  annotationBuilder()

    /**
     *  <p>{@summary Creates a builder for an annotation.}</p>
     *  <p>This method creates a builder for a new annotation, while
     *  {@link #annotationBuilder(ClassName)}
     *  and
     *  {@link #annotationBuilder(Class)}
     *  will create the builder for the <i>use</i> of an annotation.</p>
     *
     *  @param  className   The name of the annotation.
     *  @return The builder.
     */
    public final TypeSpec.Builder annotationTypeBuilder( final ClassName className )
    {
        final var retValue = annotationTypeBuilder( requireNonNullArgument( className, "className" ).simpleName() );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  annotationTypeBuilder()

    /**
     *  <p>{@summary Creates a builder for an annotation.}</p>
     *  <p>This method creates a builder for a new annotation, while
     *  {@link #annotationBuilder(ClassName)}
     *  and
     *  {@link #annotationBuilder(Class)}
     *  will create the builder for the <i>use</i> of an annotation.</p>
     *
     *  @param  name   The name of the annotation.
     *  @return The builder.
     */
    public final TypeSpec.Builder annotationTypeBuilder( final CharSequence name )
    {
        final var retValue = new AnnotationTypeSpecImpl.BuilderImpl( this, requireNonNullArgument( name, "name" ) );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  annotationTypeBuilder()

    /**
     *  Creates a builder for an anonymous class.
     *
     *  @param  typeArguments   The type arguments.
     *  @return The builder.
     */
    public final TypeSpec.Builder anonymousClassBuilder( final CodeBlock typeArguments )
    {
        final var retValue = new ClassSpecImpl.BuilderImpl( this, (CodeBlockImpl) requireNonNullArgument( typeArguments, "typeArguments" ) );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  anonymousClassBuilder()

    /**
     *  <p>{@summary Creates a builder for an anonymous class.} This method is
     *  the shortcut for</p>
     *  <pre><code>  CodeBlock codeBlock = codeBlockBuilder()
     *      .add( format, args )
     *      .build();
     *  TypeSpec.Builder builder = anonymousClassBuilder( codeBlock ); </code></pre>
     *
     *  @param  format  The format for the code; may be empty.
     *  @param  args    The arguments for the code.
     *  @return The builder.
     *
     *  @see #codeBlockBuilder()
     *  @see #anonymousClassBuilder(CodeBlock)
     */
    public final TypeSpec.Builder anonymousClassBuilder( final String format, final Object... args )
    {
        final var retValue = anonymousClassBuilder( codeBlockBuilder()
            .add( format, args )
            .build() );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  anonymousClassBuilder()

    /**
     *  Creates a builder for a regular class.
     *
     *  @param  className   The name of the class.
     *  @return The builder.
     */
    public final TypeSpec.Builder classBuilder( final ClassName className )
    {
        final var retValue = classBuilder( requireNonNullArgument( className, "className" ).simpleName() );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  classBuilder()

    /**
     *  Creates a builder for a regular class.
     *
     *  @param  name    The name of the class.
     *  @return The builder.
     */
    public final TypeSpec.Builder classBuilder( final CharSequence name )
    {
        final var retValue = new ClassSpecImpl.BuilderImpl( this, requireNotEmptyArgument( name, "name" ), null );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  classBuilder()

    /**
     *  Creates a builder for an instance of
     *  {@link CodeBlock}.
     *
     *  @return The new builder.
     */
    public final CodeBlock.Builder codeBlockBuilder()
    {
        return new CodeBlockImpl.BuilderImpl( this );
    }   //  codeBlockBuilder()

    /**
     *  Creates a new
     *  {@link CodeBlock}
     *  instance from the given format and arguments.
     *
     *  @note   No debug info will be added.
     *
     *  @param  format  The format.
     *  @param  args    The arguments.
     *  @return The new code block.
     */
    public final CodeBlock codeBlockOf( final String format, final Object... args )
    {
        final var retValue = ((CodeBlockImpl.BuilderImpl) codeBlockBuilder())
            .add( format, args )
            .build();

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  codeBlockOf()

    /**
     *  Creates a builder that builds an instance of {@code MethodSpec} for a
     *  constructor.
     *
     *  @return The builder.
     */
    public final MethodSpec.Builder constructorBuilder()
    {
        final var retValue = new MethodSpecImpl.BuilderImpl( this, CONSTRUCTOR );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  constructorBuilder()

    /**
     *  Creates an instance of
     *  {@link AnnotationSpec}
     *  from the given
     *  {@link Annotation}
     *  instance.
     *
     *  @param  annotation  The annotation.
     *  @return The new instance of {@code AnnotationSpec}.
     */
    public final AnnotationSpec createAnnotation( final Annotation annotation )
    {
        final var retValue = createAnnotation( annotation, false );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  createAnnotation()

    /**
     *  Creates an instance of
     *  {@link AnnotationSpec}
     *  from the given
     *  {@link Annotation}
     *  instance.
     *
     *  @param  annotation  The annotation.
     *  @param  includeDefaultValues    {@code true} to include the
     *      annotation's default values, {@code false} to ignore them.
     *  @return The new instance of {@code AnnotationSpec}.
     */
    public final AnnotationSpec createAnnotation( final Annotation annotation, final boolean includeDefaultValues )
    {
        final var builder = (AnnotationSpecImpl.BuilderImpl) annotationBuilder( ClassName.from( requireNonNullArgument( annotation, "annotation" ).annotationType() ) );
        try
        {
            final var methods = annotation.annotationType().getDeclaredMethods();
            sort( methods, comparing( Method::getName ) );
            MethodsLoop: for( final var method : methods )
            {
                final var value = method.invoke( annotation );
                if( !includeDefaultValues )
                {
                    if( deepEquals( value, method.getDefaultValue() ) ) continue MethodsLoop;
                }
                if( value.getClass().isArray() )
                {
                    for( var i = 0; i < Array.getLength( value ); ++i )
                    {
                        builder.addMemberForValue( method.getName(), Array.get( value, i ) );
                    }
                    continue MethodsLoop;
                }
                if( value instanceof final Annotation annotationValue )
                {
                    builder.addMember( method.getName(), "$L", createAnnotation( annotationValue, false ) );
                    continue MethodsLoop;
                }
                builder.addMemberForValue( method.getName(), value );
            }   //  MethodsLoop:
        }
        catch( final SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e )
        {
            throw new JavaComposerException( "Reflecting " + annotation + " failed!", e );
        }
        final var retValue = builder.build();

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  createAnnotation()

    /**
     *  Creates an instance of
     *  {@link AnnotationSpec}
     *  from the given
     *  {@link AnnotationMirror}
     *  instance.
     *
     *  @param  annotation  The annotation mirror.
     *  @return The new instance of {@code AnnotationSpec}.
     */
    public final AnnotationSpec createAnnotation( final AnnotationMirror annotation )
    {
        final var element = (TypeElement) requireNonNullArgument( annotation, "annotation" ).getAnnotationType().asElement();
        final var builder = (AnnotationSpecImpl.BuilderImpl) annotationBuilder( ClassName.from( element ) );
        final var visitor = new AnnotationValueVisitor( builder );
        for( final var executableElement : annotation.getElementValues().keySet() )
        {
            final var name = executableElement.getSimpleName().toString();
            @SuppressWarnings( "unlikely-arg-type" )
            final var value = annotation.getElementValues().get( executableElement );
            value.accept( visitor, name );
        }
        final var retValue = builder.build();

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  createAnnotation()

    /**
     *  Returns a {@code @ClassVersion} annotation with a default text for
     *  {@link ClassVersion#sourceVersion()}.
     *
     *  @return The annotation.
     *
     *  @see    ClassVersion
     */
    public final AnnotationSpec createClassVersionAnnotation() { return m_Annotation_ClassVersion.get(); }

    /**
     *  Returns a {@code @ClassVersion} annotation with the text for
     *  {@link ClassVersion#sourceVersion()}.
     *
     *  @param  sourceVersion   The text.
     *  @return The annotation.
     *
     *  @see    ClassVersion
     */
    public final AnnotationSpec createClassVersionAnnotation( final String sourceVersion )
    {
        final var retValue = annotationBuilder( ClassVersion.class )
            .forceInline( true )
            .addMember( "sourceVersion", "$S", requireNotEmptyArgument( sourceVersion, "sourceVersion" ) )
            .addMember( "isGenerated", "$L", "true" )
            .build();

        //---* Done *------------------------------------------------------
        return retValue;
    }   //  createClassVersionAnnotation()

    /**
     *  Creates the comment for a constant.
     *
     *  @param  comment The already existing comment; can be {@code null}.
     *  @return The comment for the new constant.
     */
    private final CodeBlock createComment4Constant( final CodeBlock comment )
    {
        var retValue = comment;
        if( isNull( retValue ) )
        {
            retValue = codeBlockOf(
                """
                Constant value: $L.
                """, JAVADOC_TAG_VALUE );
        }
        else
        {
            if( !comment.toString().contains( JAVADOC_TAG_VALUE ) )
            {
                retValue = retValue.join( "\n", codeBlockOf(
                    """
                    <p>The value is: $L.</p>
                    """, JAVADOC_TAG_VALUE ) );
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
     */
    public final FieldSpec createConstant( final CharSequence name, final String value, final CodeBlock comment )
    {
        final var retValue = createConstant( name, String.class, codeBlockOf( "$S", requireNonNullArgument( value, "value" ) ), createComment4Constant( comment ) );

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
     */
    @SuppressWarnings( "SameParameterValue" )
    private final FieldSpec createConstant( final CharSequence name, final TypeName type, final Number value, final CodeBlock comment )
    {
        final var retValue = createConstant( name, type, codeBlockOf( "$L", requireNonNullArgument( value, "value" ) ), createComment4Constant( comment ) );

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
     */
    public final FieldSpec createConstant( final CharSequence name, final int value, final CodeBlock comment )
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
     */
    public final FieldSpec createConstant( final CharSequence name, final Type type, final CodeBlock value, final CodeBlock comment )
    {
        return createConstant( name, TypeName.from( requireNonNullArgument( type, "type" ) ), value, comment );
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
     */
    public final FieldSpec createConstant( final CharSequence name, final TypeName type, final CodeBlock value, final CodeBlock comment )
    {
        final var builder = fieldBuilder( type, name, PUBLIC, FINAL, STATIC )
            .initializer( requireNonNullArgument( value, "value" ) );

        if( nonNull( comment ) && !comment.isEmpty() ) builder.addJavadoc( comment );

        final var retValue = builder.build();

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  createConstant()

    /**
     *  <p>{@summary Returns a builder for an implementation of the method
     *  {@link Object#equals(Object)}
     *  that just needs the method body for completion.}</p>
     *  <p>The argument has the name &quot;{@code o}&quot;.</p>
     *
     *  @return The method builder.
     */
    public final MethodSpec.Builder createEqualsBuilder()
    {
        final var retValue = m_Method_Equals.get().toBuilder();

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  createEqualsBuilder()

    /**
     *  <p>{@summary Returns a
     *  {@link MethodSpec}
     *  instance for an implementation of the method
     *  {@link Object#equals(Object)}.}</p>
     *  <p>The argument has the name &quot;{@code o}&quot;.</p>
     *
     *  @param  body    The method body.
     *  @return The method specification.
     */
    public final MethodSpec createEqualsMethod( final CodeBlock body )
    {
        final var retValue = createEqualsBuilder()
            .addCode( requireNonNullArgument( body, "body" ) )
            .build();

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  createEqualsMethod()

    /**
     *  Returns a builder for an implementation of the method
     *  {@link Object#hashCode()}
     *  that just needs the method body for completion.
     *
     *  @return The method builder.
     */
    public final MethodSpec.Builder createHashCodeBuilder()
    {
        final var retValue = m_Method_HashCode.get().toBuilder();

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  createHashCodeBuilder()

    /**
     *  Returns a
     *  {@link MethodSpec}
     *  instance for an implementation of the method
     *  {@link Object#hashCode()}.
     *
     *  @param  body    The method body.
     *  @return The method specification.
     */
    public final MethodSpec createHashCodeMethod( final CodeBlock body )
    {
        final var retValue = createHashCodeBuilder()
            .addCode( requireNonNullArgument( body, "body" ) )
            .build();

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  createHashCodeMethod()

    /**
     *  <p>{@summary Returns a
     *  {@link MethodSpec}
     *  instance for an implementation of the method
     *  {@link Object#hashCode()}.}</p>
     *  <p>The created method uses
     *  {@link org.tquadrat.foundation.lang.Objects#hash(Object...)}
     *  for the implementation.</p>
     *
     *  @param  fields  The fields that are used for the calculation of the
     *      hash code value.
     *  @return The method specification.
     */
    public final MethodSpec createHashCodeMethod( final FieldSpec... fields )
    {
        final var retValue = createHashCodeMethod( List.of( requireNonNullArgument( fields, "fields" ) ) );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  createHashCodeMethod()

    /**
     *  <p>{@summary Returns a
     *  {@link MethodSpec}
     *  instance for an implementation of the method
     *  {@link Object#hashCode()}.}</p>
     *  <p>The created method uses
     *  {@link org.tquadrat.foundation.lang.Objects#hash(Object...)}
     *  for the implementation.</p>
     *
     *  @param  fields  The fields that are used for the calculation of the
     *      hash code value.
     *  @return The method specification.
     */
    public final MethodSpec createHashCodeMethod( final Collection<? extends FieldSpec> fields )
    {
        final var fieldList = requireNotEmptyArgument( fields, "fields" ).stream()
            .map( FieldSpec::name )
            .collect( joining( ", ", "return hash( ", " )" ) );
        final var body = codeBlockBuilder()
            .addStaticImport( Objects.class, "hash" )
            .addStatement( fieldList )
            .build();
        final var retValue = createHashCodeBuilder()
            .addCode( body )
            .build();

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  createHashCodeMethod()

    /**
     *  Returns a code block with a comment for overriding methods:
     *  <pre><code>  &#47;**
     *   * {&#64;inheritDoc}
     *   *&#47;</code></pre>
     *
     *  @return The comment.
     */
    public final CodeBlock createInheritDocComment() { return m_JavaDoc_InheritDoc.get(); }

    /**
     *  <p>{@summary Creates a
     *  {@link MethodSpec}
     *  for the given
     *  {@link ExecutableElement}.}</p>
     *  <p>This method copies visibility modifiers, type parameters, return
     *  type, name, parameters, and {@code throws} declarations, but not the
     *  body (if any).</p>
     *
     *  @note   The annotations will not be copied and must be added separately.
     *
     *  @param  method  The method to override.
     *  @return The builder.
     */
    public final MethodSpec createMethod( final ExecutableElement method )
    {
        final var methodName = requireNonNullArgument( method, "method" ).getSimpleName();
        final var builder = methodBuilder( methodName )
            .addModifiers( method.getModifiers() )
            .returns( TypeName.from( method.getReturnType() ) )
            .addParameters( parametersOf( method ) )
            .varargs( method.isVarArgs() );

        for( final var typeParameterElement : method.getTypeParameters() )
        {
            final var typeVariable = (TypeVariable) typeParameterElement.asType();
            builder.addTypeVariable( TypeVariableName.from( typeVariable ) );
        }

        for( final var thrownType : method.getThrownTypes() )
        {
            builder.addException( TypeName.from( thrownType ) );
        }

        //---* Create the return value *---------------------------------------
        final var retValue = builder.build();

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  createMethod()

    /**
     *  Creates an instance of
     *  {@link ParameterSpec}
     *  from the given
     *  {@link VariableElement}
     *  instance.
     *
     *  @param  element The variable element.
     *  @return The parameter spec.
     */
    public final ParameterSpec createParameter( final VariableElement element )
    {
        final var type = TypeName.from( requireNonNullArgument( element, "element" ).asType() );
        final var name = element.getSimpleName().toString();
        final var modifiers = element.getModifiers();
        final var builder = parameterBuilder( type, name )
            .addModifiers( modifiers );
        for( final var mirror : element.getAnnotationMirrors() )
        {
            builder.addAnnotation( createAnnotation( mirror ) );
        }
        final var retValue = builder.build();

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  createParameter()

    /**
     *  Creates an instance of
     *  {@link ParameterSpec}
     *  from the given
     *  {@link Parameter}
     *  instance.
     *
     *  @param  parameter The variable element.
     *  @return The parameter spec.
     */
    public final ParameterSpec createParameter( final Parameter parameter )
    {
        final var type = TypeName.from( requireNonNullArgument( parameter, "parameter" ).getType() );
        final var name = parameter.getName();
        final var modifiers = translateModifiers( parameter.getModifiers() );
        final var builder = parameterBuilder( type, name )
            .addModifiers( modifiers );
        for( final var annotation : parameter.getAnnotations() )
        {
            builder.addAnnotation( createAnnotation( annotation ) );
        }
        final var retValue = builder.build();

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  createParameter()

    /**
     *  Creates a return statement with a comment, using {@code retValue} as
     *  the name for the return variable.
     *
     *  @return The return statement.
     */
    public final CodeBlock createReturnStatement() { return createReturnStatement( "retValue" ); }

    /**
     *  Creates a return statement with a comment.
     *
     *  @param  name    The name of the variable that is returned.
     *  @return The return statement.
     */
    public final CodeBlock createReturnStatement( final CharSequence name )
    {
        final var retValue = new CodeBlockImpl.BuilderImpl( this )
            .addWithoutDebugInfo(
                  """

                  //---* Done *----------------------------------------------------------
                  """ )
            .addStatement( "return $L", requireNotEmptyArgument( name, "name" ) )
            .build();

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  createReturnStatement()

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
     *  @see    ClassVersion
     *  @see    PrivateConstructorForStaticClassCalledError
     *  @see    UtilityClass
     */
    public final TypeSpec.Builder createStaticClassBuilder( final CharSequence className )
    {
        requireValidNonNullArgument( className, "className", v -> SourceVersion.isName( requireNotEmptyArgument( v, "className" ) ), $ -> "not a valid name: %s".formatted( className ) );
        final var constructor = constructorBuilder()
            .addModifiers( PRIVATE )
            .addJavadoc(
                """
                No instance allowed for this class!
                """ )
            .addStatement( "throw new $T( $L.class )", PrivateConstructorForStaticClassCalledError.class, className )
            .build();
        final var retValue = classBuilder( className )
            .addModifiers( PUBLIC, FINAL )
            .addAnnotation( createUtilityClassAnnotation() )
            .addAnnotation( createClassVersionAnnotation() )
            .addMethod( constructor );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  createStaticClassBuilder()

    /**
     *  <p>{@summary Returns a
     *  {@link SuppressWarnings &#64;SuppressWarnings}
     *  annotation with the given values.} No annotation will be created if
     *  the given collection is empty.</p>
     *
     *  @param  warnings    The warnings to suppress.
     *  @return An instance of
     *      {@link Optional}
     *      that holds the new annotation.
     */
    public final Optional<AnnotationSpec> createSuppressWarningsAnnotation( final Collection<SuppressableWarnings> warnings )
    {
        return createSuppressWarningsAnnotation( requireNonNullArgument( warnings, "warnings" ).toArray( SuppressableWarnings []::new ) );
    }   //  createSuppressWarningsAnnotation()

    /**
     *  <p>{@summary Returns a
     *  {@link SuppressWarnings &#64;SuppressWarnings}
     *  annotation with the given values.} No annotation will be created if
     *  the given collection is empty.</p>
     *
     *  @param  warnings    The warnings to suppress.
     *  @return An instance of
     *      {@link Optional}
     *      that holds the new annotation.
     */
    public final Optional<AnnotationSpec> createSuppressWarningsAnnotation( final SuppressableWarnings... warnings )
    {
        final Optional<AnnotationSpec> retValue = warnings.length > 0
            ? Optional.of( SuppressableWarnings.createSuppressWarningsAnnotation( this, warnings ) )
            : Optional.empty();

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  createSuppressWarningsAnnotation()

    /**
     *  Returns a builder for an implementation of the method
     *  {@link Object#toString()}
     *  that just needs the method body for completion.
     *
     *  @return The method builder.
     */
    public final MethodSpec.Builder createToStringBuilder()
    {
        final var retValue = m_Method_ToString.get().toBuilder();

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  createToStringBuilder()

    /**
     *  Returns a
     *  {@link MethodSpec}
     *  instance for an implementation of the method
     *  {@link Object#toString()}.
     *
     *  @param  body    The method body.
     *  @return The method specification.
     */
    public final MethodSpec createToStringMethod( final CodeBlock body )
    {
        final var retValue = createToStringBuilder()
            .addCode( requireNonNullArgument( body, "body" ) )
            .build();

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  createToStringMethod()

    /**
     *  Returns a {@code @UtilityClass} annotation.
     *
     *  @return The annotation.
     *
     *  @see    UtilityClass
     */
    public final AnnotationSpec createUtilityClassAnnotation() { return m_Annotation_UtilityClass.get(); }

    /**
     *  Returns an empty
     *  {@link CodeBlock}.
     *
     *  @return An empty code block.
     */
    public final CodeBlock emptyCodeBlock() { return codeBlockBuilder().build(); }

    /**
     *  Creates a builder for an {@code enum} type.
     *
     *  @param  className   The name of the class.
     *  @return The builder.
     */
    public final TypeSpec.Builder enumBuilder( final ClassName className )
    {
        final var retValue = enumBuilder( requireNonNullArgument( className, "className" ).simpleName() );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  enumBuilder()

    /**
     *  Creates a builder for an {@code enum} type.
     *
     *  @param  name   The name of the class.
     *  @return The builder.
     */
    public final TypeSpec.Builder enumBuilder( final CharSequence name )
    {
        final var retValue = new EnumSpecImpl.BuilderImpl( this, requireNonNullArgument( name, "name" ) );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  enumBuilder()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final boolean equals( final Object o )
    {
        var retValue = this == o;
        if( !retValue && o instanceof final JavaComposer other )
        {
            retValue = (m_Layout == other.m_Layout) && (m_AddDebugOutput == other.m_AddDebugOutput);
        }

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  equals()

    /**
     *  Creates a builder for an instance of
     *  {@link FieldSpec}
     *  from the given type, name and modifiers.
     *
     *  @param  type    The type of the {@code FieldSpec} to build.
     *  @param  name    The name for the new field.
     *  @param  modifiers   The modifiers.
     *  @return The new builder.
     */
    public final FieldSpec.Builder fieldBuilder( final Type type, final CharSequence name, final Modifier... modifiers )
    {
        final var retValue = fieldBuilder( TypeNameImpl.from( requireNonNullArgument( type, "type" ) ), name, modifiers );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  fieldBuilder()

    /**
     *  Creates a builder for an instance of
     *  {@link FieldSpec}
     *  from the given type, name and modifiers.
     *
     *  @param  type    The type of the {@code FieldSpec} to build.
     *  @param  name    The name for the new field.
     *  @param  modifiers   The modifiers.
     *  @return The new builder.
     */
    public final FieldSpec.Builder fieldBuilder( final TypeName type, final CharSequence name, final Modifier... modifiers )
    {
        final var retValue = new FieldSpecImpl.BuilderImpl( this, (TypeNameImpl) requireNonNullArgument( type, "type" ), require( name, $ -> "not a valid name: %s".formatted( name ), JavaUtils::isValidName ) ).addModifiers( requireNonNullArgument( modifiers, "modifiers" ) );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  fieldBuilder()

    /**
     *  Creates a builder for an instance of
     *  {@link FieldSpec}
     *  from the given type, name and modifiers.
     *
     *  @param  type    The type of the {@code FieldSpec} to build.
     *  @param  name    The name for the new field.
     *  @param  modifiers   The modifiers.
     *  @return The new builder.
     */
    public final FieldSpec.Builder fieldBuilder( final TypeSpec type, final CharSequence name, final Modifier... modifiers )
    {
        final var typeName = ClassNameImpl.from( EMPTY_STRING, requireNonNullArgument( type, "type" ).name().orElseThrow( () -> new ValidationException( "Anonymous class cannot be used as type for a field" ) ) );
        final var retValue = fieldBuilder( typeName, name, modifiers );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  fieldBuilder()

    /**
     *  Provides access to the code processor for this composer instance.
     *
     *  @return The code processor.
     */
    public final CodeProcessor getCodeProcessor() { return m_CodeProcessor; }

    /**
     *  Returns the layout that is used to format the output.
     *
     *  @return The layout.
     */
    public final Layout getLayout() { return m_Layout; }

    /**
     *  <p>{@summary Returns the maximum number of fields for a class.} If this
     *  number is exceeded, an annotation
     *  {@link SuppressWarnings &#64;SuppressWarnings}
     *  with
     *  {@link SuppressableWarnings#CLASS_WITH_TOO_MANY_FIELDS}
     *  will be added to the class.</p>
     *
     *  @return The maximum number of fields.
     */
    public final int getMaxFields() { return m_MaxFields; }

    /**
     *  <p>{@summary Returns the maximum number of methods for a class.} If
     *  this number is exceeded, an annotation
     *  {@link SuppressWarnings &#64;SuppressWarnings}
     *  with
     *  {@link SuppressableWarnings#CLASS_WITH_TOO_MANY_METHODS}
     *  will be added to the class.</p>
     *
     *  @return The maximum number of fields.
     */
    public final int getMaxMethods() { return m_MaxMethods; }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final int hashCode() { return hash( m_Layout, Boolean.valueOf( m_AddDebugOutput ) ) ; }

    /**
     *  Creates a builder for an interface.
     *
     *  @param  className   The name of the class.
     *  @return The builder.
     */
    public final TypeSpec.Builder interfaceBuilder( final ClassName className )
    {
        final var retValue = interfaceBuilder( requireNonNullArgument( className, "className" ).simpleName() );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  interfaceBuilder()

    /**
     *  Creates a builder for an interface.
     *
     *  @param  name   The name of the class.
     *  @return The builder.
     */
    public final TypeSpec.Builder interfaceBuilder( final CharSequence name )
    {
        final var retValue = new InterfaceSpecImpl.BuilderImpl( new JavaComposer(), requireNotEmptyArgument( name, "name" ) );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  interfaceBuilder()

    /**
     *  Creates a builder for a new instance of
     *  {@link JavaFile}
     *  from the given package name and class definition.
     *
     *  @param  packageName The package name; may be empty for the default
     *      package.
     *  @param  typeSpec    The class definition.
     *  @return The builder.
     */
    public final JavaFile.Builder javaFileBuilder( final CharSequence packageName, final TypeSpec typeSpec )
    {
        final var retValue = new JavaFileImpl.BuilderImpl( this, packageName, (TypeSpecImpl) typeSpec );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  javaFileBuilder()

    /**
     *  Creates a builder for an instance of
     *  {@link LambdaSpec}.
     *
     *  @return The new builder.
     */
    public final LambdaSpec.Builder lambdaBuilder()
    {
        final var retValue = new LambdaSpecImpl.BuilderImpl( this );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  lambdaBuilder()

    /**
     *  Returns a builder for a regular method.
     *
     *  @param  name    The name for the method.
     *  @return The builder.
     */
    public final MethodSpec.Builder methodBuilder( final CharSequence name )
    {
        final var retValue = new MethodSpecImpl.BuilderImpl( new JavaComposer(), name );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  methodBuilder()

    /**
     *  <p>{@summary Returns a new method builder for a method that overrides
     *  the given method.}</p>
     *  <p>This new builder will copy visibility modifiers, type parameters,
     *  return type, name, parameters, and {@code throws} declarations. An
     *  {@link Override}
     *  annotation will be added.</p>
     *
     *  @note In JavaPoet&nbsp;1.2 through 1.7 this method retained annotations
     *      from the method and parameters of the overridden method. Since
     *      JavaPoet&nbsp;1.8 and in JavaComposer annotations must be added
     *      separately.
     *
     *  @param  method  The method to override.
     *  @return The builder.
     */
    public final MethodSpec.Builder overridingMethodBuilder( final ExecutableElement method )
    {
        final var enclosingClass = requireNonNullArgument( method, "method" ).getEnclosingElement();
        if( enclosingClass.getModifiers().contains( FINAL ) )
        {
            throw new IllegalArgumentException( format( MSG_CannotOverrideFinalClass, enclosingClass.toString() ) );
        }

        var modifiers = method.getModifiers();
        if( modifiers.contains( PRIVATE ) || modifiers.contains( FINAL ) || modifiers.contains( STATIC ) )
        {
            throw new IllegalArgumentException( format( MSG_CannotOverrideMethod, modifiers.stream().map( Enum::name ).collect( joining( "', '" ) ) ) );
        }

        final var methodName = method.getSimpleName().toString();
        final var retValue = methodBuilder( methodName );

        retValue.addAnnotation( Override.class );

        modifiers = new LinkedHashSet<>( modifiers );
        modifiers.remove( ABSTRACT );
        modifiers.remove( DEFAULT );
        retValue.addModifiers( modifiers );

        for( final var typeParameterElement : method.getTypeParameters() )
        {
            final var typeVariable = (TypeVariable) typeParameterElement.asType();
            retValue.addTypeVariable( TypeVariableName.from( typeVariable ) );
        }

        retValue.returns( TypeName.from( method.getReturnType() ) );

        retValue.addParameters( parametersOf( method ) );
        retValue.varargs( method.isVarArgs() );

        for( final var thrownType : method.getThrownTypes() )
        {
            retValue.addException( TypeName.from( thrownType ) );
        }

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  overridingMethodBuilder()

    /**
     *  <p>{@summary Returns a new method builder that overrides the given
     *  method as a member of of the given enclosing class.} This will resolve
     *  type parameters: for example overriding
     *  {@link Comparable#compareTo}
     *  in a type that implements {@code Comparable<Movie>}, the {@code T}
     *  parameter will be resolved to {@code Movie}.</p>
     *  <p>This will copy its visibility modifiers, type parameters, return
     *  type, name, parameters, and {@code throws} declarations. An
     *  {@link Override}
     *  annotation will be added.</p>
     *
     *  @note In JavaPoet&nbsp;1.2 through 1.7 this method retained annotations
     *      from the method and parameters of the overridden method. Since
     *      JavaPoet&nbsp;1.8 and in JavaComposer annotations must be added
     *      separately.
     *
     *  @param  method  The method to override.
     *  @param  enclosing   The enclosing class for the method.
     *  @param  typeUtils   An implementation of some utility methods for
     *      operating on types, as provided by the processing environment of an
     *      annotation processor.
     *  @return The builder.
     */
    public final MethodSpec.Builder overridingMethodBuilder( final ExecutableElement method, final DeclaredType enclosing, final Types typeUtils )
    {
        final var executableType = (ExecutableType) requireNonNullArgument( typeUtils, "types" ).asMemberOf( requireNonNullArgument( enclosing, "enclosing" ), requireNonNullArgument( method, "method" ) );
        final var resolvedParameterTypes = executableType.getParameterTypes();
        final var resolvedThrownTypes = executableType.getThrownTypes();
        final var resolvedReturnType = executableType.getReturnType();

        final var retValue = (MethodSpecImpl.BuilderImpl) overridingMethodBuilder( method );
        retValue.returns( TypeName.from( resolvedReturnType ) );
        final var parameters = retValue.parameters();
        for( int i = 0, size = parameters.size(); i < size; ++i )
        {
            final var parameter = parameters.get( i );
            final var type = TypeName.from( resolvedParameterTypes.get( i ) );
            parameters.set( i, parameter.toBuilder( type, parameter.name(), true ).build() );
        }
        retValue.exceptions().clear();
        for( final var resolvedThrownType : resolvedThrownTypes )
        {
            retValue.addException( TypeName.from( resolvedThrownType ) );
        }

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  overridingMethodBuilder()

    /**
     *  <p>{@summary Returns a new method builder for a method that overrides
     *  the given method.}</p>
     *  <p>This new builder will copy visibility modifiers, type parameters,
     *  return type, name, parameters, and {@code throws} declarations. An
     *  {@link Override}
     *  annotation will be added, but any other annotation will be omitted;
     *  this is consistent with the behaviour of
     *  {@link #overridingMethodBuilder(ExecutableElement)}
     *  and
     *  {@link #overridingMethodBuilder(ExecutableElement, DeclaredType, Types)}.</p>
     *
     *  @param  method  The method to override.
     *  @return The builder.
     */
    public final MethodSpec.Builder overridingMethodBuilder( final Method method )
    {
        final var enclosingClass = requireNonNullArgument( method, "method" ).getDeclaringClass();
        if( translateModifiers( enclosingClass.getModifiers() ).contains( FINAL ) )
        {
            throw new IllegalArgumentException( format( MSG_CannotOverrideFinalClass, enclosingClass.getName() ) );
        }

        var modifiers = translateModifiers( method.getModifiers() );
        if( modifiers.contains( PRIVATE ) || modifiers.contains( FINAL ) || modifiers.contains( STATIC ) )
        {
            throw new IllegalArgumentException( format( MSG_CannotOverrideMethod, modifiers.stream().map( Enum::name ).collect( joining( "', '" ) ) ) );
        }
        final var methodName = method.getName();
        final var retValue = methodBuilder( methodName );

        retValue.addAnnotation( Override.class );

        modifiers = new LinkedHashSet<>( modifiers );
        modifiers.remove( ABSTRACT );
        modifiers.remove( DEFAULT );
        retValue.addModifiers( modifiers );

        for( final var typeParameterVariable : method.getTypeParameters() )
        {
            retValue.addTypeVariable( TypeVariableName.from( typeParameterVariable ) );
        }

        retValue.returns( TypeName.from( method.getReturnType() ) );

        retValue.addParameters( parametersOf( method ) );
        retValue.varargs( method.isVarArgs() );

        for( final var thrownType : method.getExceptionTypes() )
        {
            retValue.addException( TypeName.from( thrownType ) );
        }

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  overridingMethodBuilder()

    /**
     *  Creates a builder for a new
     *  {@link ParameterSpec}
     *  instance.
     *
     *  @param  type    The type of the new parameter.
     *  @param  name    The name of the new parameter.
     *  @param  modifiers   The modifiers for the new parameter.
     *  @return The builder.
     */
    public final ParameterSpec.Builder parameterBuilder( final Type type, final CharSequence name, final Modifier... modifiers )
    {
        final var retValue = parameterBuilder( TypeNameImpl.from( requireNonNullArgument( type, "type" ) ), name, modifiers );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  parameterBuilder()

    /**
     *  Creates a builder for a new
     *  {@link ParameterSpec}
     *  instance.
     *
     *  @param  type    The type of the new parameter.
     *  @param  name    The name of the new parameter.
     *  @param  modifiers   The modifiers for the new parameter.
     *  @return The builder.
     */
    public final ParameterSpec.Builder parameterBuilder( final TypeName type, final CharSequence name, final Modifier... modifiers )
    {
        final var retValue = new ParameterSpecImpl.BuilderImpl( this, type, name ).addModifiers( modifiers );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  parameterBuilder()

    /**
     *  Creates a new
     *  {@link ParameterSpec}
     *  instance for the given arguments.
     *
     *  @param  type    The type of the new parameter.
     *  @param  name    The name of the new parameter.
     *  @param  modifiers   The modifiers for the new parameter.
     *  @return The parameter specification.
     */
    public final ParameterSpec parameterOf( final Type type, final CharSequence name, final Modifier... modifiers )
    {
        final var retValue = parameterOf( TypeNameImpl.from( requireNonNullArgument( type, "type" ) ), name, modifiers );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  parameterOf()

    /**
     *  Creates a new
     *  {@link ParameterSpec}
     *  instance for the given arguments.
     *
     *  @param  type    The type of the new parameter.
     *  @param  name    The name of the new parameter.
     *  @param  modifiers   The modifiers for the new parameter.
     *  @return The parameter specification.
     */
    public final ParameterSpec parameterOf( final TypeName type, final CharSequence name, final Modifier... modifiers )
    {
        final var retValue = parameterBuilder( type, name, modifiers )
            .build();

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  parameterOf()

    /**
     *  Retrieves th    e parameters from the given method.
     *
     *  @param  method  The method.
     *  @return The parameters of the given method; the returned list can be
     *      empty, but it will not be {@code null}.
     */
    public final List<ParameterSpec> parametersOf( final ExecutableElement method )
    {
        final var retValue = requireNonNullArgument( method, "method" ).getParameters()
            .stream()
            .map( this::createParameter )
            .toList();

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  parametersOf()

    /**
     *  Retrieves the parameters from the given method.
     *
     *  @param  method  The method.
     *  @return The parameters of the given method; the returned list can be
     *      empty, but it will not be {@code null}.
     */
    public final List<ParameterSpec> parametersOf( final Method method )
    {
        final var retValue = stream( requireNonNullArgument( method, "method" ).getParameters() )
            .map( this::createParameter )
            .toList();

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  parametersOf()

    /**
     *  Creates a builder for a record.
     *
     *  @param  className   The name of the record type.
     *  @return The builder.
     */
    public final TypeSpec.Builder recordBuilder( final ClassName className )
    {
        final var retValue = recordBuilder( requireNonNullArgument( className, "className" ).simpleName() );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  recordBuilder()

    /**
     *  Creates a builder for a record.
     *
     *  @param  name    The name of the record type.
     *  @return The builder.
     */
    public final TypeSpec.Builder recordBuilder( final CharSequence name )
    {
        final var retValue = new RecordSpecImpl.BuilderImpl( this, requireNotEmptyArgument( name, "name" ) );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  recordBuilder()

    /**
     *  <p>{@summary Sets the maximum number of fields for a class.} If this
     *  number is exceeded, an annotation
     *  {@link SuppressWarnings &#64;SuppressWarnings}
     *  with
     *  {@link SuppressableWarnings#CLASS_WITH_TOO_MANY_FIELDS}
     *  will be added to the class.</p>
     *  <p>Setting the value to 0 or a negative value will disable this
     *  feature.</p>
     *
     *  @param  value   The value.
     */
    public final void setMaxFields( final int value ) { m_MaxFields = value; }

    /**
     *  <p>{@summary Sets the maximum number of methods for a class.} If this
     *  number is exceeded, an annotation
     *  {@link SuppressWarnings &#64;SuppressWarnings}
     *  with
     *  {@link SuppressableWarnings#CLASS_WITH_TOO_MANY_METHODS}
     *  will be added to the class.</p>
     *  <p>Setting the value to 0 or a negative value will disable this
     *  feature.</p>
     *
     *  @param  value   The value.
     */
    public final void setMaxMethods( final int value ) { m_MaxMethods = value; }

    /**
     *  Creates a new
     *  {@link CodeBlock}
     *  instance from the given format and arguments, using
     *  {@link CodeBlock.Builder#addStatement(String, Object...)}.
     *
     *  @note   No debug info will be added.
     *
     *  @param  format  The format.
     *  @param  args    The arguments.
     *  @return The new code block.
     */
    public final CodeBlock statementOf( final String format, final Object... args )
    {
        final var retValue = ((CodeBlockImpl.BuilderImpl) codeBlockBuilder())
            .addStatement( format, args )
            .build();

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  codeBlockOf()
}
//  class JavaComposer

/*
 *  End of File
 */