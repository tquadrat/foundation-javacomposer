/*
 * ============================================================================
 * Copyright © 2015 Square, Inc.
 * Copyright for the modifications © 2018-2021 by Thomas Thrien.
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

package org.tquadrat.foundation.javacomposer;

import static org.apiguardian.api.API.Status.DEPRECATED;
import static org.apiguardian.api.API.Status.STABLE;

import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.javacomposer.internal.TypeSpecImpl;

/**
 *  The specification of a generated class, interface, annotation or enum
 *  declaration.
 *
 *  @author Square,Inc.
 *  @modified   Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: TypeSpec.java 930 2021-06-20 18:08:47Z tquadrat $
 *  @since 0.0.5
 *
 *  @UMLGraph.link
 */
@ClassVersion( sourceVersion = "$Id: TypeSpec.java 930 2021-06-20 18:08:47Z tquadrat $" )
@API( status = STABLE, since = "0.0.5" )
public sealed interface TypeSpec
    permits TypeSpecImpl
{
        /*---------------*\
    ====** Inner Classes **====================================================
        \*---------------*/
    /**
     *  The specification for a builder for an instance of an implementation
     *  for
     *  {@link TypeSpec}.
     *
     *  @author Square,Inc.
     *  @modified Thomas Thrien - thomas.thrien@tquadrat.org
     *  @version $Id: TypeSpec.java 930 2021-06-20 18:08:47Z tquadrat $
     *  @since 0.0.5
     *
     *  @UMLGraph.link
     */
    @SuppressWarnings( {"ClassWithTooManyMethods", "InnerClassOfInterface"} )
    @ClassVersion( sourceVersion = "$Id: TypeSpec.java 930 2021-06-20 18:08:47Z tquadrat $" )
    @API( status = STABLE, since = "0.0.5" )
    public static interface Builder
    {
            /*---------*\
        ====** Methods **======================================================
            \*---------*/
        /**
         *  <p>{@summary Adds an annotation for the type to build.}</p>
         *  <p>Do not use this method to add a
         *  {@link SuppressWarnings &#64;SuppressWarnings}
         *  annotation to the type; instead use
         *  {@link #addSuppressableWarning(SuppressableWarnings...)}
         *  to add the warning to suppress.</p>
         *  <p>The annotation will be generated automatically when the type is
         *  finally build.</p>
         *
         *  @note No debug information can be added when adding an annotation.
         *
         *  @param  annotationSpec  The annotation.
         *  @return This {@code Builder} instance.
         */
        public Builder addAnnotation( final AnnotationSpec annotationSpec );

        /**
         *  <p>{@summary Adds an annotation for the type to build.}</p>
         *  <p>Do not use this method to add a
         *  {@link SuppressWarnings &#64;SuppressWarnings}
         *  annotation to the type; instead use
         *  {@link #addSuppressableWarning(SuppressableWarnings...)}
         *  to add the warning to suppress.</p>
         *  <p>The annotation will be generated automatically when the type is
         *  finally build.</p>
         *
         *  @note No debug information can be added when adding an annotation.
         *
         *  @param  annotation  The annotation.
         *  @return This {@code Builder} instance.
         */
        public Builder addAnnotation( final Class<?> annotation );

        /**
         *  <p>{@summary Adds an annotation for the type to build.}</p>
         *  <p>Do not use this method to add a
         *  {@link SuppressWarnings &#64;SuppressWarnings}
         *  annotation to the type; instead use
         *  {@link #addSuppressableWarning(SuppressableWarnings...)}
         *  to add the warning to suppress.</p>
         *  <p>The annotation will be generated automatically when the type is
         *  finally build.</p>
         *
         *  @note No debug information can be added when adding an annotation.
         *
         *  @param  annotation  The annotation.
         *  @return This {@code Builder} instance.
         */
        public Builder addAnnotation( final ClassName annotation );

        /**
         *  <p>{@summary Add the given annotations for the type to build.}</p>
         *  <p>Do not use this method to add a
         *  {@link SuppressWarnings &#64;SuppressWarnings}
         *  annotation to the type; instead use
         *  {@link #addSuppressableWarning(SuppressableWarnings...)}
         *  to add the warning to suppress.</p>
         *  <p>The annotation will be generated automatically when the type is
         *  finally build.</p>
         *
         *  @note No debug information can be added when adding an annotation.
         *
         *  @param  annotationSpecs The annotations.
         *  @return This {@code Builder} instance.
         */
        public Builder addAnnotations( final Iterable<AnnotationSpec> annotationSpecs );

        /**
         *  <p>{@summary Adds an attribute to this type.}</p>
         *  <p>An attribute is basically a field with the related accessor and
         *  mutator methods.</p>
         *  <p>If the name for a non-final property will be {@code text}, and
         *  its type is {@code java.lang.String}, the generated code will look
         *  basically like this:</p>
         *  <pre><code>  …
         *  private String m_Text;
         *  …
         *  public final String text() { return m_Text; }
         *  …
         *  public final void text( final String value ) { m_Text = text; }
         *  …</code></pre>
         *  <p>If accessor or mutator needs to be more complex, the respective
         *  methods must be created manually.</p>
         *
         *  @note The name of the mutator parameter is always {@code value}; if
         *      this is also the name of the field, the generated code will not
         *      compile. In this case, the mutator has to be generated
         *      manually, too.
         *
         *  @param  fieldSpec   The field definition.
         *  @param  readOnly    {@code true} if no mutator should be created
         *      even for a non-final field, {@code false} if a mutator has to
         *      be created for a non-final field. Will ignored if the field is
         *      final.
         *  @return This {@code Builder} instance.
         *
         *  @since 0.2.0
         */
        @API( status = STABLE, since = "0.2.0" )
        public Builder addAttribute( final FieldSpec fieldSpec, boolean readOnly );

        /**
         *  Adds an {@code enum} constant for the type to build.
         *
         *  @param  name    The name for the {@code enum} constant.
         *  @return This {@code Builder} instance.
         */
        public Builder addEnumConstant( final CharSequence name );

        /**
         *  Adds an {@code enum} constant for the type to build.
         *
         *  @param  name    The name for the {@code enum} constant.
         *  @param  javaDoc The code block for the Javadoc for this
         *      {@code enum} constant.
         *  @return This {@code Builder} instance.
         */
        public Builder addEnumConstant( final CharSequence name, final CodeBlock javaDoc );

        /**
         *  Adds an {@code enum} constant for the type to build.
         *
         *  @param  name    The name for the {@code enum} constant.
         *  @param  format  The format for the Javadoc for this {@code enum}
         *      constant.
         *  @param  args    The arguments for the Javadoc for this {@code enum}
         *      constant.
         *  @return This {@code Builder} instance.
         */
        public Builder addEnumConstant( final CharSequence name, final String format, final Object... args );

        /**
         *  Adds an {@code enum} constant for the type to build.
         *
         *  @param  name    The name for the {@code enum} constant.
         *  @param  typeSpec    The type of the {@code enum} constant.
         *  @return This {@code Builder} instance.
         */
        public Builder addEnumConstant( final CharSequence name, final TypeSpec typeSpec );

        /**
         *  Adds a field to this type.
         *
         *  @param  fieldSpec   The field definition.
         *  @return This {@code Builder} instance.
         */
        public Builder addField( final FieldSpec fieldSpec );

        /**
         *  Adds a field to this type.
         *
         *  @param  type    The type of the field.
         *  @param  name    The name of the field.
         *  @param  modifiers   The modifiers.
         *  @return This {@code Builder} instance.
         */
        public Builder addField( final Type type, final CharSequence name, final Modifier... modifiers );

        /**
         *  Adds a field to this type.
         *
         *  @param  type    The type of the field.
         *  @param  name    The name of the field.
         *  @param  modifiers   The modifiers.
         *  @return This {@code Builder} instance.
         */
        public Builder addField( final TypeName type, final CharSequence name, final Modifier... modifiers );

        /**
         *  Adds fields to this type.
         *
         *  @param  fieldSpecs  The fields.
         *  @return This {@code Builder} instance.
         */
        public Builder addFields( final Iterable<? extends FieldSpec> fieldSpecs );

        /**
         *  Adds code to the initializer block.
         *
         *  @param  block   The code.
         *  @return This {@code Builder} instance.
         */
        public Builder addInitializerBlock( final CodeBlock block );

        /**
         *  Adds text to the Javadoc for the type.
         *
         *  @param  block   The comment text.
         *  @return This {@code Builder} instance.
         */
        public Builder addJavadoc( final CodeBlock block );

        /**
         *  Adds text to the Javadoc for the type.
         *
         *  @param  format  The format.
         *  @param  args    The arguments.
         *  @return This {@code Builder} instance.
         */
        public Builder addJavadoc( final String format, final Object... args );

        /**
         *  Adds a method for the type.
         *
         *  @param  methodSpec  The method.
         *  @return This {@code Builder} instance.
         */
        public Builder addMethod( final MethodSpec methodSpec );

        /**
         *  Adds methods for the type.
         *
         *  @param  methodSpecs The methods.
         *  @return This {@code Builder} instance.
         */
        @SuppressWarnings( "BoundedWildcard" )
        public Builder addMethods( final Iterable<MethodSpec> methodSpecs );

        /**
         *  Adds modifiers for the type.
         *
         *  @param  modifiers   The modifiers.
         *  @return This {@code Builder} instance.
         */
        public Builder addModifiers( final Modifier... modifiers );

        /**
         *  Adds an originating element for the type.
         *
         *  @param  originatingElement  The originating element.
         *  @return This {@code Builder} instance.
         */
        public Builder addOriginatingElement( final Element originatingElement );

        /**
         *  <p>{@summary Adds a JavaBean property to this type.}</p>
         *  <p>A property is basically a field with the related getter and
         *  setter methods.</p>
         *  <p>If the name for a non-final property will be {@code text}, and
         *  its type is {@code java.lang.String}, the generated code will look
         *  basically like this:</p>
         *  <pre><code>  …
         *  private String m_Text;
         *  …
         *  public final String getText() { return m_Text; }
         *  …
         *  public final void setText( final String value ) { m_Text = text; }
         *  …</code></pre>
         *  <p>If getter or setter needs to be more complex, the respective
         *  methods must be created manually.</p>
         *
         *  @note The name of the setter parameter is always {@code value}; if
         *      this is also the name of the field, the generated code will not
         *      compile. In this case, the setter has to be generated
         *      manually, too.
         *
         *  @param  fieldSpec   The field definition.
         *  @param  readOnly    {@code true} if no setter should be created
         *      even for a non-final field, {@code false} if a setter has to be
         *      created for a non-final field. Will ignored if the field is
         *      final.
         *  @return This {@code Builder} instance.
         */
        public Builder addProperty( final FieldSpec fieldSpec, boolean readOnly );

        /**
         *  Adds a static block for the type.
         *
         *  @param  block   The code for the static block.
         *  @return This {@code Builder} instance.
         */
        public Builder addStaticBlock( final CodeBlock block );

        /**
         *  Adds a static import.
         *
         *  @param  clazz   The class.
         *  @param  names   The names of the elements from the given class that
         *      are to be imported.
         *  @return This {@code Builder} instance.
         *
         *  @since 0.2.0
         */
        @API( status = STABLE, since = "0.2.0" )
        public Builder addStaticImport( final Class<?> clazz, final String... names );

        /**
         *  Adds a static import.
         *
         *  @param  className   The class.
         *  @param  names   The names of the elements from the given class that
         *      are to be imported.
         *  @return This {@code Builder} instance.
         *
         *  @since 0.2.0
         */
        @API( status = STABLE, since = "0.2.0" )
        public Builder addStaticImport( final ClassName className, final String... names );

        /**
         *  Adds a static import for the given {@code enum} value.
         *
         *  @param  constant    The {@code enum} value.
         *  @return This {@code Builder} instance.
         *
         *  @since 0.2.0
         */
        @API( status = STABLE, since = "0.2.0" )
        public Builder addStaticImport( final Enum<?> constant );

        /**
         *  <p>{@summary Adds a super interface for the type.} This interface
         *  is implemented by the new type if that is a {@code class}, or
         *  extended, if the new type is an {@code interface}.</p>
         *
         *  @note No debug information can be added when adding a
         *  superinterface.
         *
         *  @param  superinterface  The super interface.
         *  @return This {@code Builder} instance.
         */
        public Builder addSuperinterface( final Class<?> superinterface );

        /**
         *  <p>{@summary Adds a super interface for the type.} This interface
         *  is implemented by the new type if that is a {@code class}, or
         *  extended, if the new type is an {@code interface}.</p>
         *
         *  @note No debug information can be added when adding a
         *  superinterface.
         *
         *  @param  superinterface  The super interface.
         *  @return This {@code Builder} instance.
         */
        public Builder addSuperinterface( final Type superinterface );

        /**
         *  <p>{@summary Adds a super interface for the type.} This interface
         *  is implemented by the new type if that is a {@code class}, or
         *  extended, if the new type is an {@code interface}.</p>
         *
         *  @note No debug information can be added when adding a
         *  superinterface.
         *
         *  @param  superinterface  The super interface.
         *  @return This {@code Builder} instance.
         */
        public Builder addSuperinterface( final TypeElement superinterface );

        /**
         *  <p>{@summary Adds a super interface for the type.} This interface
         *  is implemented by the new type if that is a {@code class}, or
         *  extended, if the new type is an {@code interface}.</p>
         *
         *  @note No debug information can be added when adding a
         *  superinterface.
         *
         *  @param  superinterface  The super interface.
         *  @return This {@code Builder} instance.
         */
        public Builder addSuperinterface( final TypeName superinterface );

        /**
         *  <p>{@summary Adds super interfaces for the type.} These interfaces
         *  are implemented by the new type if that is a {@code class}, or
         *  extended, if the new type is an {@code interface}.</p>
         *
         *  @note No debug information can be added when adding a
         *  superinterface.
         *
         *  @param  superinterfaces The super interfaces.
         *  @return This {@code Builder} instance.
         */
        public Builder addSuperinterfaces( final Iterable<? extends TypeName> superinterfaces );

        /**
         *  <p>{@summary Adds a suppressable warning for the type.} These will
         *  result in adding the annotation
         *  {@link SuppressWarnings &#64;SuppressWarnings}
         *  to the class.</p>
         *
         *  @param  warnings    The warnings to add.
         *  @return This {@code Builder} instance.
         */
        public Builder addSuppressableWarning( final SuppressableWarnings... warnings );

        /**
         *  Adds an inner class for the type.
         *
         *  @param  typeSpec    The embedded type.
         *  @return This {@code Builder} instance.
         */
        public Builder addType( final TypeSpec typeSpec );

        /**
         *  Adds inner classes for the type.
         *
         *  @param  typeSpecs   The embedded types.
         *  @return This {@code Builder} instance.
         */
        public Builder addTypes( final Iterable<? extends TypeSpec> typeSpecs );

        /**
         *  Adds a type variable for the type.
         *
         *  @note No debug information can be added when adding a type
         *  variable without a JavaDoc comment.
         *
         *  @param  typeVariable    The type variable.
         *  @return This {@code Builder} instance.
         */
        public Builder addTypeVariable( final TypeVariableName typeVariable );

        /**
         *  Adds a type variable for the type together with the respective
         *  Javadoc.
         *
         *  @param  typeVariable    The type variable.
         *  @param  javadoc The Javadoc comment; this is only the text for the
         *      comment.
         *  @return This {@code Builder} instance.
         *
         *  @since  0.2.0
         */
        @API( status = STABLE, since = "0.2.0" )
        public Builder addTypeVariable( final TypeVariableName typeVariable, final CharSequence javadoc );

        /**
         *  Adds type variables for the type.
         *
         *  @note No debug information can be added when adding a type
         *  variable without a JavaDoc comment.
         *
         *  @param  typeVariables   The type variables.
         *  @return This {@code Builder} instance.
         */
        public Builder addTypeVariables( final Iterable<TypeVariableName> typeVariables );

        /**
         *  Builds a new
         *  {@link TypeSpec}
         *  instance from the added components.
         *
         *  @return The new {@code TypeSpec} instance.
         */
        public TypeSpec build();

        /**
         *  <p>{@summary Sets the superclass for the type.} This class is
         *  extended by the new type.</p>
         *
         *  @param  superclass  The superclass.
         *  @return This {@code Builder} instance.
         */
        public Builder superclass( final Type superclass );

        /**
         *  <p>{@summary Sets the superclass for the type.} This class is
         *  extended by the new type.</p>
         *
         *  @param  superclass  The superclass.
         *  @return This {@code Builder} instance.
         */
        public Builder superclass( final TypeName superclass );
    }
    //  interface Builder

        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  Creates a builder for an annotation.
     *
     *  @param  className   The name of the annotation.
     *  @return The builder.
     *
     *  @deprecated Replaced by
     *      {@link JavaComposer#annotationTypeBuilder(ClassName)}.
     */
    @SuppressWarnings( "removal" )
    @Deprecated( since = "0.2.0", forRemoval = true )
    @API( status = DEPRECATED, since = "0.0.5" )
    public static Builder annotationBuilder( final ClassName className )
    {
        final var retValue = TypeSpecImpl.annotationBuilder( className );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  annotationBuilder()

    /**
     *  Creates a builder for an annotation.
     *
     *  @param  name   The name of the annotation.
     *  @return The builder.
     *
     *  @deprecated Replaced by
     *      {@link JavaComposer#annotationTypeBuilder(CharSequence)}.
     */
    @SuppressWarnings( "removal" )
    @Deprecated( since = "0.2.0", forRemoval = true )
    @API( status = DEPRECATED, since = "0.0.5" )
    public static Builder annotationBuilder( final CharSequence name )
    {
        final var retValue = TypeSpecImpl.annotationBuilder( name );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  annotationBuilder()

    /**
     *  Creates a builder for an anonymous class.
     *
     *  @param  typeArguments   The type arguments.
     *  @return The builder.
     *
     *  @deprecated Replaced by
     *      {@link JavaComposer#anonymousClassBuilder(CodeBlock)}.
     */
    @SuppressWarnings( "removal" )
    @Deprecated( since = "0.2.0", forRemoval = true )
    @API( status = DEPRECATED, since = "0.0.5" )
    public static Builder anonymousClassBuilder( final CodeBlock typeArguments )
    {
        final var retValue = TypeSpecImpl.anonymousClassBuilder( typeArguments );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  anonymousClassBuilder()

    /**
     *  Creates a builder for an anonymous class.
     *
     *  @param  format  The format.
     *  @param  args    The arguments.
     *  @return The builder.
     *
     *  @deprecated Replaced by
     *      {@link JavaComposer#anonymousClassBuilder(String, Object...)}.
     */
    @SuppressWarnings( "removal" )
    @Deprecated( since = "0.2.0", forRemoval = true )
    @API( status = DEPRECATED, since = "0.0.5" )
    public static Builder anonymousClassBuilder( final String format, final Object... args )
    {
        final var retValue = TypeSpecImpl.anonymousClassBuilder( format, args );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  anonymousClassBuilder()

    /**
     *  Creates a builder for a regular class.
     *
     *  @param  className   The name of the class.
     *  @return The builder.
     *
     *  @deprecated Replaced by
     *      {@link JavaComposer#classBuilder(ClassName)}.
     */
    @SuppressWarnings( "removal" )
    @Deprecated( since = "0.2.0", forRemoval = true )
    @API( status = DEPRECATED, since = "0.0.5" )
    public static Builder classBuilder( final ClassName className )
    {
        final var retValue = TypeSpecImpl.classBuilder( className );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  classBuilder()

    /**
     *  Creates a builder for a regular class.
     *
     *  @param  name    The name of the class.
     *  @return The builder.
     *
     *  @deprecated Replaced by
     *      {@link JavaComposer#classBuilder(CharSequence)}.
     */
    @SuppressWarnings( {"DeprecatedIsStillUsed", "removal"} )
    @Deprecated( since = "0.2.0", forRemoval = true )
    @API( status = DEPRECATED, since = "0.0.5" )
    public static Builder classBuilder( final CharSequence name )
    {
        final var retValue = TypeSpecImpl.classBuilder( name );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  classBuilder()

    /**
     *  Creates a builder for an {@code enum} type.
     *
     *  @param  className   The name of the class.
     *  @return The builder.
     *
     *  @deprecated Replaced by
     *      {@link JavaComposer#enumBuilder(ClassName)}.
     */
    @SuppressWarnings( "removal" )
    @Deprecated( since = "0.2.0", forRemoval = true )
    @API( status = DEPRECATED, since = "0.0.5" )
    public static Builder enumBuilder( final ClassName className )
    {
        final var retValue = TypeSpecImpl.enumBuilder( className );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  enumBuilder()

    /**
     *  Creates a builder for an {@code enum} type.
     *
     *  @param  name   The name of the class.
     *  @return The builder.
     *
     *  @deprecated Replaced by
     *      {@link JavaComposer#enumBuilder(CharSequence)}.
     */
    @SuppressWarnings( "removal" )
    @Deprecated( since = "0.2.0", forRemoval = true )
    @API( status = DEPRECATED, since = "0.0.5" )
    public static Builder enumBuilder( final CharSequence name )
    {
        final var retValue = TypeSpecImpl.enumBuilder( name );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  enumBuilder()

    /**
     *  {@inheritDoc}
     */
    @Override
    public boolean equals( final Object o );

    /**
     *  {@inheritDoc}
     */
    @Override
    public int hashCode();

    /**
     *  Checks whether the given modifier was applied to this type.
     *
     *  @param  modifier    The modifier.
     *  @return {@code true} if the given modifier has been applied to this
     *      type, {@code false} otherwise.
     */
    public boolean hasModifier( final Modifier modifier );

    /**
     *  Returns the inner types for this type.
     *
     *  @return The inner types.
     */
    public List<TypeSpec> innerClasses();

    /**
     *  Creates a builder for an interface.
     *
     *  @param  className   The name of the class.
     *  @return The builder.
     *
     *  @deprecated Replaced by
     *      {@link JavaComposer#interfaceBuilder(ClassName)}.
     */
    @SuppressWarnings( "removal" )
    @Deprecated( since = "0.2.0", forRemoval = true )
    @API( status = DEPRECATED, since = "0.0.5" )
    public static Builder interfaceBuilder( final ClassName className )
    {
        final var retValue = TypeSpecImpl.interfaceBuilder( className );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  interfaceBuilder()

    /**
     *  Creates a builder for an interface.
     *
     *  @param  name   The name of the class.
     *  @return The builder.
     *
     *  @deprecated Replaced by
     *      {@link JavaComposer#interfaceBuilder(CharSequence)}.
     */
    @SuppressWarnings( "removal" )
    @Deprecated( since = "0.2.0", forRemoval = true )
    @API( status = DEPRECATED, since = "0.0.5" )
    public static Builder interfaceBuilder( final CharSequence name )
    {
        final var retValue = TypeSpecImpl.interfaceBuilder( name );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  interfaceBuilder()

    /**
     *  Returns the name of the class, interface or enum represented by this
     *  type spec instance.
     *
     *  @return An instance of
     *      {@link Optional}
     *      that holds the name.
     */
    public Optional<String> name();

    /**
     *  Returns the originating elements for this type.
     *
     *  @return The originating elements.
     */
    public List<Element> originatingElements();

    /**
     *  Returns a new builder that is initialised with this {@code TypeSpec}
     *  instance.
     *
     *  @return The new builder.
     */
    public Builder toBuilder();

    /**
     *  {@inheritDoc}
     */
    @Override
    public String toString();
}
//  interface TypeSpec

/*
 *  End of File
 */