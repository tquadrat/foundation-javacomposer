/*
 * ============================================================================
 * Copyright © 2015 Square, Inc.
 * Copyright for the modifications © 2018-2023 by Thomas Thrien.
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

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.util.Types;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.javacomposer.internal.MethodSpecImpl;

/**
 *  The specification for a generated constructor or method declaration.
 *
 *  @author Square,Inc.
 *  @modified   Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: MethodSpec.java 1067 2023-09-28 21:09:15Z tquadrat $
 *  @since 0.0.5
 *
 *  @UMLGraph.link
 */
@ClassVersion( sourceVersion = "$Id: MethodSpec.java 1067 2023-09-28 21:09:15Z tquadrat $" )
@API( status = STABLE, since = "0.0.5" )
public sealed interface MethodSpec
    permits MethodSpecImpl
{
        /*---------------*\
    ====** Inner Classes **====================================================
        \*---------------*/
    /**
     *  The builder for an instance of
     *  {@link MethodSpec}
     *
     *  @author Square,Inc.
     *  @modified   Thomas Thrien - thomas.thrien@tquadrat.org
     *  @version $Id: MethodSpec.java 1067 2023-09-28 21:09:15Z tquadrat $
     *  @since 0.0.5
     *
     *  @UMLGraph.link
     */
    @SuppressWarnings( {"ClassWithTooManyMethods", "InnerClassOfInterface"} )
    @ClassVersion( sourceVersion = "$Id: MethodSpec.java 1067 2023-09-28 21:09:15Z tquadrat $" )
    @API( status = STABLE, since = "0.0.5" )
    public static sealed interface Builder
        permits MethodSpecImpl.BuilderImpl
    {
            /*---------*\
        ====** Methods **======================================================
            \*---------*/
        /**
         *  Adds an annotation for the method.
         *
         *  @note No debug information can be added when adding an annotation.
         *
         *  @param  annotationSpec  The annotation.
         *  @return This {@code Builder} instance.
         */
        public Builder addAnnotation( final AnnotationSpec annotationSpec );

        /**
         *  Adds an annotation for the method.
         *
         *  @note No debug information can be added when adding an annotation.
         *
         *  @param  annotation  The annotation.
         *  @return This {@code Builder} instance.
         */
        public Builder addAnnotation( final Class<?> annotation );

        /**
         *  Adds an annotation for the method.
         *
         *  @note No debug information can be added when adding an annotation.
         *
         *  @param  annotationClass The annotation.
         *  @return This {@code Builder} instance.
         */
        public Builder addAnnotation( final ClassName annotationClass );

        /**
         *  Adds annotations for the method.
         *
         *  @note No debug information can be added when adding an annotation.
         *
         *  @param  annotationSpecs The annotations.
         *  @return This {@code Builder} instance.
         */
        public Builder addAnnotations( final Iterable<? extends AnnotationSpec> annotationSpecs );

        /**
         *  Adds code for the method.
         *
         *  @param  codeBlock   The code.
         *  @return This {@code Builder} instance.
         */
        public Builder addCode( final CodeBlock codeBlock );

        /**
         *  Adds code for the method.
         *
         *  @param  format  The format.
         *  @param  args    The arguments.
         *  @return This {@code Builder} instance.
         */
        public Builder addCode( final String format, final Object... args );

        /**
         *  Adds code for the method.
         *
         *  @param  addDebugOutput  {@code true} if debug output should be
         *      added to the generated code, {@code false} if not.
         *  @param  format  The format.
         *  @param  args    The arguments.
         *  @return This {@code Builder} instance.
         *
         *  @since 0.0.6
         *
         *  @deprecated Got obsolete with the introduction of
         *      {@link JavaComposer}.
         */
        @Deprecated( since = "0.2.0", forRemoval = true )
        @API( status = DEPRECATED, since = "0.0.6" )
        public Builder addCode( final boolean addDebugOutput, final String format, final Object... args );

        /**
         *  Adds a comment for the method.
         *
         *  @note   No debug info is added when a comment is added.
         *
         *  @param  format  The format.
         *  @param  args    The arguments.
         *  @return This {@code Builder} instance.
         */
        public Builder addComment( final String format, final Object... args );

        /**
         *  Adds the declaration for an exception for the method.
         *
         *  @note   No debug info is added when an exception is added.
         *
         *  @param  exception   The exception.
         *  @return This {@code Builder} instance.
         */
        public Builder addException( final Type exception );

        /**
         *  Adds the declaration for an exception for the method.
         *
         *  @note   No debug info is added when an exception is added.
         *
         *  @param  exception   The exception.
         *  @return This {@code Builder} instance.
         */
        public Builder addException( final TypeName exception );

        /**
         *  Adds the declarations for exceptions for the method.
         *
         *  @note   No debug info is added when an exception is added.
         *
         *  @param  exceptions  The exceptions.
         *  @return This {@code Builder} instance.
         */
        @SuppressWarnings( "UnusedReturnValue" )
        public Builder addExceptions( final Iterable<? extends TypeName> exceptions );

        /**
         *  Adds a Javadoc comment for the method.
         *
         *  @note   No debug info is added when a comment is added.
         *
         *  @param  block   The Javadoc comment.
         *  @return This {@code Builder} instance.
         */
        public Builder addJavadoc( final CodeBlock block );

        /**
         *  Adds a Javadoc comment for the method.
         *
         *  @note   No debug info is added when a comment is added.
         *
         *  @param  format  The format.
         *  @param  args    The arguments.
         *  @return This {@code Builder} instance.
         */
        public Builder addJavadoc( final String format, final Object... args );

        /**
         *  Adds modifiers for the method.
         *
         *  @param  modifiers   The modifiers.
         *  @return This {@code Builder} instance.
         */
        public Builder addModifiers( final Iterable<Modifier> modifiers );

        /**
         *  Adds modifiers for the method.
         *
         *  @param  modifiers   The modifiers.
         *  @return This {@code Builder} instance.
         */
        public Builder addModifiers( final Modifier... modifiers );

        /**
         *  <p>{@summary Adds code using named arguments for the method.}</p>
         *  <p>Named arguments specify their name after the '$' followed by a
         *  colon {@code ":"} and the corresponding type character. Argument
         *  names consist of characters in {@code a-z, A-Z, 0-9, and _} and
         *  must start with a lowercase character.</p>
         *  <p>For example, to refer to the type
         *  {@link java.lang.Integer}
         *  with the argument name {@code clazz} use a format string containing
         *  {@code $clazz:T} and include the key {@code clazz} with value
         *  {@code java.lang.Integer.class} in the argument map.</p>
         *
         *  @param  format  The format.
         *  @param  args    The arguments.
         *  @return This {@code Builder} instance.
         */
        public Builder addNamedCode( final String format, final Map<String,?> args );

        /**
         *  <p>{@summary Adds code using named arguments for the method.}</p>
         *  <p>Named arguments specify their name after the '$' followed by a
         *  colon {@code ":"} and the corresponding type character. Argument
         *  names consist of characters in {@code a-z, A-Z, 0-9, and _} and
         *  must start with a lowercase character.</p>
         *  <p>For example, to refer to the type
         *  {@link java.lang.Integer}
         *  with the argument name {@code clazz} use a format string containing
         *  {@code $clazz:T} and include the key {@code clazz} with value
         *  {@code java.lang.Integer.class} in the argument map.</p>
         *
         *  @param  addDebugOutput  {@code true} if debug output should be
         *      added to the generated code, {@code false} if not.
         *  @param  format  The format.
         *  @param  args    The arguments.
         *  @return This {@code Builder} instance.
         *
         *  @since 0.0.6
         *
         *  @deprecated Got obsolete with the introduction of
         *      {@link JavaComposer}.
         */
        @Deprecated( since = "0.2.0", forRemoval = true )
        @API( status = DEPRECATED, since = "0.0.6" )
        public Builder addNamedCode( final boolean addDebugOutput, final String format, final Map<String,?> args );

        /**
         *  Adds a parameter for the method.
         *
         *  @note   No debug info is added when a parameter is added.
         *
         *  @param  parameterSpec   The parameter.
         *  @return This {@code Builder} instance.
         */
        public Builder addParameter( final ParameterSpec parameterSpec );

        /**
         *  Adds a parameter for the method.
         *
         *  @note   No debug info is added when a parameter is added.
         *
         *  @param  type    The type of the parameter.
         *  @param  name    The name of the parameter.
         *  @param  modifiers   The modifiers for the parameter.
         *  @return This {@code Builder} instance.
         */
        public Builder addParameter( final Type type, final String name, final Modifier... modifiers );

        /**
         *  Adds a parameter for the method.
         *
         *  @note   No debug info is added when a parameter is added.
         *
         *  @param  type    The type of the parameter.
         *  @param  name    The name of the parameter.
         *  @param  modifiers   The modifiers for the parameter.
         *  @return This {@code Builder} instance.
         */
        public Builder addParameter( final TypeName type, final String name, final Modifier... modifiers );

        /**
         *  Adds parameters for the method.
         *
         *  @note   No debug info is added when a parameter is added.
         *
         *  @param  parameterSpecs  The parameters.
         *  @return This {@code Builder} instance.
         */
        @SuppressWarnings( "UnusedReturnValue" )
        public Builder addParameters( final Iterable<? extends ParameterSpec> parameterSpecs );

        /**
         *  Adds a statement to the code for the method.
         *
         *  @param  statement   The statement.
         *  @return This {@code Builder} instance.
         *
         *  @deprecated The code fails when the
         *      {@link CodeBlock}
         *      was created with calls to
         *      {@link CodeBlock.Builder#addStatement(String, Object...)}.
         *      Use
         *      {@link MethodSpec.Builder#addCode(CodeBlock)}
         *      instead.
         */
        @Deprecated( since = "0.1.0", forRemoval = false )
        public Builder addStatement( final CodeBlock statement );

        /**
         *  Adds a statement to the code for the method.
         *
         *  @param  format  The format.
         *  @param  args    The arguments.
         *  @return This {@code Builder} instance.
         */
        public Builder addStatement( final String format, final Object... args );

        /**
         *  Adds a statement to the code for the method.
         *
         *  @param  addDebugOutput  {@code true} if debug output should be
         *      added to the generated code, {@code false} if not.
         *  @param  format  The format.
         *  @param  args    The arguments.
         *  @return This {@code Builder} instance.
         *
         *  @since 0.0.6
         *
         *  @deprecated Got obsolete with the introduction of
         *      {@link JavaComposer}.
         */
        @Deprecated( since = "0.2.0", forRemoval = true )
        @API( status = DEPRECATED, since = "0.0.6" )
        public Builder addStatement( final boolean addDebugOutput, final String format, final Object... args );

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
        public MethodSpec.Builder addStaticImport( final Class<?> clazz, final String... names );

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
        public MethodSpec.Builder addStaticImport( final ClassName className, final String... names );

        /**
         *  Adds a static import for the given {@code enum} value.
         *
         *  @param  constant    The {@code enum} value.
         *  @return This {@code Builder} instance.
         *
         *  @since 0.2.0
         */
        @API( status = STABLE, since = "0.2.0" )
        public MethodSpec.Builder addStaticImport( final Enum<?> constant );

        /**
         *  Adds a type variable for the method.
         *
         *  @note   No debug info is added when a type variable is added.
         *
         *  @param  typeVariable    The type variable.
         *  @return This {@code Builder} instance.
         */
        public Builder addTypeVariable( final TypeVariableName typeVariable );

        /**
         *  Adds type variables for the method.
         *
         *  @note   No debug info is added when a parameter is added.
         *
         *  @param  typeVariables   The type variables.
         *  @return This {@code Builder} instance.
         */
        @SuppressWarnings( "UnusedReturnValue" )
        public Builder addTypeVariables( final Iterable<TypeVariableName> typeVariables );

        /**
         *  Adds the beginning of a control flow for the method.
         *
         *  @param  controlFlow The control flow construct and its code, such
         *      as &quot;{@code if (foo == 5)}&quot;; it should not contain
         *      braces or newline characters.
         *  @param  args    The arguments.
         *  @return This {@code Builder} instance.
         *
         *  @see #endControlFlow()
         *  @see #endControlFlow(String, Object...)
         *  @see #nextControlFlow(String, Object...)
         */
        public Builder beginControlFlow( final String controlFlow, final Object... args );

        /**
         *  Adds the beginning of a control flow for the method.
         *
         *  @param  addDebugOutput  {@code true} if debug output should be
         *      added to the generated code, {@code false} if not.
         *  @param  controlFlow The control flow construct and its code, such
         *      as &quot;{@code if (foo == 5)}&quot;; it should not contain
         *      braces or newline characters.
         *  @param  args    The arguments.
         *  @return This {@code Builder} instance.
         *
         *  @see #endControlFlow()
         *  @see #endControlFlow(String, Object...)
         *  @see #endControlFlow(boolean,String, Object...)
         *  @see #nextControlFlow(String, Object...)
         *  @see #nextControlFlow(boolean,String, Object...)
         *
         *  @since 0.0.6
         *
         *  @deprecated Got obsolete with the introduction of
         *      {@link JavaComposer}.
         */
        @Deprecated( since = "0.2.0", forRemoval = true )
        @API( status = DEPRECATED, since = "0.0.6" )
        public Builder beginControlFlow( final boolean addDebugOutput, final String controlFlow, final Object... args );

        /**
         *  Creates a new
         *  {@link MethodSpec}
         *  instance from the components that have been added to this builder.
         *
         *  @return The {@code MethodSpec} instance.
         */
        public MethodSpec build();

        /**
         *  Sets the default value for this method.
         *
         *  @param  defaultValue    The default value.
         *  @return This {@code Builder} instance.
         */
        public Builder defaultValue( final CodeBlock defaultValue );

        /**
         *  Sets the default value for this method.
         *
         *  @param  format  The format.
         *  @param  args    The arguments.
         *  @return This {@code Builder} instance.
         */
        public Builder defaultValue( final String format, final Object... args );

        /**
         *  Ends the current control flow for the method.
         *
         *  @return This {@code Builder} instance.
         *
         *  @see #beginControlFlow(String, Object...)
         *  @see #endControlFlow(String, Object...)
         *  @see #nextControlFlow(String, Object...)
         */
        public Builder endControlFlow();

        /**
         *  Ends the current control flow for the method; this version is only
         *  used for {@code do-while} constructs.
         *
         *  @param  controlFlow The optional control flow construct and its
         *      code, such as &quot;{@code while(foo == 20)}&quot;; it should
         *      not contain braces or newline characters.
         *  @param  args    The arguments.
         *  @return This {@code Builder} instance.
         *
         *  @see #beginControlFlow(String, Object...)
         *  @see #endControlFlow()
         */
        public Builder endControlFlow( final String controlFlow, final Object... args );

        /**
         *  Ends the current control flow for the method; this version is only
         *  used for {@code do-while} constructs.
         *
         *  @param  addDebugOutput  {@code true} if debug output should be
         *      added to the generated code, {@code false} if not.
         *  @param  controlFlow The optional control flow construct and its
         *      code, such as &quot;{@code while(foo == 20)}&quot;; it should
         *      not contain braces or newline characters.
         *  @param  args    The arguments.
         *  @return This {@code Builder} instance.
         *
         *  @see #beginControlFlow(String, Object...)
         *  @see #beginControlFlow(boolean,String, Object...)
         *  @see #endControlFlow()
         *
         *  @since 0.0.6
         *
         *  @deprecated Got obsolete with the introduction of
         *      {@link JavaComposer}.
         */
        @Deprecated( since = "0.2.0", forRemoval = true )
        @API( status = DEPRECATED, since = "0.0.6" )
        public Builder endControlFlow( final boolean addDebugOutput, final String controlFlow, final Object... args );

        /**
         *  Begins another control flow for the method.
         *
         *  @param  controlFlow The control flow construct and its code, such
         *      as &quot;{@code else if (foo == 10)}&quot;; it should not
         *      contain braces or newline characters.
         *  @param  args    The arguments.
         *  @return This {@code Builder} instance.
         *
         *  @see #beginControlFlow(String, Object...)
         *  @see #endControlFlow()
         */
        public Builder nextControlFlow( final String controlFlow, final Object... args );

        /**
         *  Begins another control flow for the method.
         *
         *  @param  addDebugOutput  {@code true} if debug output should be
         *      added to the generated code, {@code false} if not.
         *  @param  controlFlow The control flow construct and its code, such
         *      as &quot;{@code else if (foo == 10)}&quot;; it should not
         *      contain braces or newline characters.
         *  @param  args    The arguments.
         *  @return This {@code Builder} instance.
         *
         *  @see #beginControlFlow(String, Object...)
         *  @see #beginControlFlow(boolean,String, Object...)
         *  @see #endControlFlow()
         *
         *  @since 0.0.6
         *
         *  @deprecated Got obsolete with the introduction of
         *      {@link JavaComposer}.
         */
        @Deprecated( since = "0.2.0", forRemoval = true )
        @API( status = DEPRECATED, since = "0.0.6" )
        public Builder nextControlFlow( final boolean addDebugOutput, final String controlFlow, final Object... args );

        /**
         *  Sets the return type for the method.
         *
         *  @param  returnType  The return type.
         *  @return This {@code Builder} instance.
         */
        public Builder returns( final Type returnType );

        /**
         *  Sets the return type for the method and adds the respective Javadoc
         *  comment.
         *
         *  @param  returnType  The return type.
         *  @param  format  The format for the return comment.
         *  @param  args    The arguments for the return comment.
         *  @return This {@code Builder} instance.
         */
        public Builder returns( final Type returnType, final String format, final Object... args );

        /**
         *  Sets the return type for the method.
         *
         *  @param  returnType  The return type.
         *  @return This {@code Builder} instance.
         */
        public Builder returns( final TypeName returnType );

        /**
         *  Sets the return type for the method and adds the respective Javadoc
         *  comment.
         *
         *  @param  returnType  The return type.
         *  @param  format  The format for the return comment.
         *  @param  args    The arguments for the return comment.
         *  @return This {@code Builder} instance.
         */
        public Builder returns( final TypeName returnType, final String format, final Object... args  );

        /**
         *  Sets the flag that indicates whether a parameter (the last one) is
         *  a {@code varargs} parameter to {@code true}.
         *
         *  @return This {@code Builder} instance.
         */
        public default Builder varargs() { return varargs( true ); }

        /**
         *  Sets the flag that indicates whether a parameter (the last one) is
         *  a {@code varargs} parameter.
         *
         *  @param  varargs {@code true} if the last parameter is a
         *      {@code varargs} parameter, {@code false} if not.
         *  @return This {@code Builder} instance.
         */
        public Builder varargs( final boolean varargs );
    }
    //  interface Builder

        /*-----------*\
    ====** Constants **========================================================
        \*-----------*/
    /**
     *  The &quot;name&quot; for a method that is in fact a constructor:
     *  {@value}.
     */
    public static final String CONSTRUCTOR = "<init>";

        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  Creates a builder that builds an instance of {@code MethodSpec} for a
     *  constructor.
     *
     *  @return The builder.
     *
     *  @deprecated Replaced by
     *      {@link JavaComposer#constructorBuilder()}
     */
    @SuppressWarnings( {"removal", "StaticMethodOnlyUsedInOneClass"} )
    @Deprecated( since = "0.2.0", forRemoval = true )
    @API( status = DEPRECATED, since = "0.0.5" )
    public static Builder constructorBuilder() { return MethodSpecImpl.constructorBuilder(); }

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
     *  Checks whether the method has the given modifier.
     *
     *  @param  modifier    The modifier.
     *  @return {@code true} if the given modifier has been applied to this
     *      method, {@code false} otherwise.
     */
    public boolean hasModifier( final Modifier modifier );

    /**
     *  Checks whether this method is a constructor.
     *
     *  @return {@code true} if the method is a constructor, {@code false} if
     *      it is a regular method.
     */
    public boolean isConstructor();

    /**
     *  Returns a builder for a regular method.
     *
     *  @param  name    The name for the method.
     *  @return The builder.
     *
     *  @deprecated Replaced by
     *      {@link JavaComposer#constructorBuilder()}
     */
    @SuppressWarnings( "removal" )
    @Deprecated( since = "0.2.0", forRemoval = true )
    @API( status = DEPRECATED, since = "0.0.5" )
    public static Builder methodBuilder( final CharSequence name ) { return MethodSpecImpl.methodBuilder( name ); }

    /**
     *  Returns the modifiers of this method.
     *
     *  @return The modifiers.
     */
    public Set<Modifier> modifiers();

    /**
     *  Returns the name of this method.
     *
     *  @return The name
     */
    public String name();

    /**
     *  <p>{@summary Returns a new method spec builder for a method that
     *  overrides the given method.}</p>
     *  <p>This new builder will copy visibility modifiers, type parameters,
     *  return type, name, parameters, and throws declarations. An
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
     *
     *  @deprecated Replaced by
     *      {@link JavaComposer#overridingMethodBuilder(ExecutableElement)}.
     */
    @SuppressWarnings( "removal" )
    @Deprecated( since = "0.2.0", forRemoval = true )
    @API( status = DEPRECATED, since = "0.0.5" )
    public static Builder overriding( final ExecutableElement method )
    {
        final var retValue = MethodSpecImpl.overriding( method );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  overriding()

    /**
     *  <p>{@summary Returns a new method spec builder that overrides the given
     *  method as a member of of the given enclosing class.} This will resolve
     *  type parameters: for example overriding
     *  {@link Comparable#compareTo}
     *  in a type that implements {@code Comparable<Movie>}, the {@code T}
     *  parameter will be resolved to {@code Movie}.</p>
     *  <p>This will copy its visibility modifiers, type parameters, return
     *  type, name, parameters, and throws declarations. An
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
     *  @param  types   The type variables.
     *  @return The builder.
     *
     *  @deprecated Replaced by
     *      {@link JavaComposer#overridingMethodBuilder(ExecutableElement, DeclaredType, Types)}.
     */
    @SuppressWarnings( "removal" )
    @Deprecated( since = "0.2.0", forRemoval = true )
    @API( status = DEPRECATED, since = "0.0.5" )
    public static Builder overriding( final ExecutableElement method, final DeclaredType enclosing, final Types types )
    {
        final var retValue = MethodSpecImpl.overriding( method, enclosing, types );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  overriding()

    /**
     *  <p>{@summary Returns a new method spec builder for a method that
     *  overrides the given method.}</p>
     *  <p>This new builder will copy visibility modifiers, type parameters,
     *  return type, name, parameters, and throws declarations. An
     *  {@link Override}
     *  annotation will be added, but any other annotation will be omitted;
     *  this is consistent with the behaviour of
     *  {@link #overriding(ExecutableElement)}
     *  and
     *  {@link #overriding(ExecutableElement, DeclaredType, Types)}.</p>
     *
     *  @param  method  The method to override.
     *  @return The builder.
     *
     *  @deprecated Replaced by
     *      {@link JavaComposer#overridingMethodBuilder(Method)}.
     */
    @SuppressWarnings( "removal" )
    @Deprecated( since = "0.2.0", forRemoval = true )
    @API( status = DEPRECATED, since = "0.0.8" )
    public static Builder overriding( final Method method )
    {
        final var retValue = MethodSpecImpl.overriding( method );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  overriding()

    /**
     *  Returns the parameters for this method.
     *
     *  @return The parameters.
     */
    public Collection<ParameterSpec> parameters();

    /**
     *  Returns the signature for this method.
     *
     *  @return The signature.
     */
    public String signature();

    /**
     *  Return the return type for this method.
     *
     *  @return The return type; will never be {@code null}, not even for a
     *      constructor.
     */
    public TypeName returnType();

    /**
     *  Returns a builder that is initialised with all the components of this
     *  method. Use this to implement a method from an interface.
     *
     *  @return The builder.
     *
     *  @see #toBuilder(boolean)
     */
    public default Builder toBuilder() { return toBuilder( false ); }

    /**
     *  <p>{@summary Returns a builder that is initialised with all the
     *  components of this method, like the result of a call to
     *  {@link #toBuilder},
     *  but without the body code, if specified that way.}</p>
     *  <p>If the body is not copied, the method will be marked as
     *  {@link Modifier#ABSTRACT ABSTRACT},
     *  too.</p>
     *
     *  @param  omitCode    {@code true} if the body code should not be copied,
     *      {@code false} otherwise; in the latter case, the result is the same
     *      as for
     *      {@link #toBuilder()}.
     *  @return The builder.
     */
    @API( status = STABLE, since = "0.0.8" )
    public Builder toBuilder( final boolean omitCode );

    /**
     *  {@inheritDoc}
     */
    @Override
    public String toString();
}
//  interface MethodSpec

/*
 *  End of File
 */