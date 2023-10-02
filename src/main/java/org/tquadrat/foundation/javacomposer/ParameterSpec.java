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

import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;
import java.lang.reflect.Type;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.javacomposer.internal.ParameterSpecImpl;

/**
 *  The specification for a generated parameter declaration.
 *
 *  @author Square,Inc.
 *  @modified Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: ParameterSpec.java 1068 2023-09-28 21:42:28Z tquadrat $
 *  @since 0.0.5
 *
 *  @UMLGraph.link
 */
@ClassVersion( sourceVersion = "$Id: ParameterSpec.java 1068 2023-09-28 21:42:28Z tquadrat $" )
@API( status = STABLE, since = "0.0.5" )
public interface ParameterSpec
{
        /*---------------*\
    ====** Inner Classes **====================================================
        \*---------------*/
    /**
     *  The builder for an instance of
     *  {@link ParameterSpec}
     *
     *  @author Square,Inc.
     *  @modified   Thomas Thrien - thomas.thrien@tquadrat.org
     *  @version $Id: ParameterSpec.java 1068 2023-09-28 21:42:28Z tquadrat $
     *  @since 0.0.5
     *
     *  @UMLGraph.link
     */
    @SuppressWarnings( "InnerClassOfInterface" )
    @ClassVersion( sourceVersion = "$Id: ParameterSpec.java 1068 2023-09-28 21:42:28Z tquadrat $" )
    @API( status = STABLE, since = "0.0.5" )
    public static interface Builder
    {
            /*---------*\
        ====** Methods **======================================================
            \*---------*/
        /**
         *  Adds an annotation for the parameter.
         *
         *  @param  annotationSpec  The annotation.
         *  @return This {@code Builder} instance.
         */
        public Builder addAnnotation( final AnnotationSpec annotationSpec );

        /**
         *  Adds an annotation for the parameter.
         *
         *  @param  annotation  The annotation.
         *  @return This {@code Builder} instance.
         */
        public Builder addAnnotation( final Class<?> annotation );

        /**
         *  Adds an annotation for the parameter.
         *
         *  @param  annotationClassName The name of the annotation class.
         *  @return This {@code Builder} instance.
         */
        public Builder addAnnotation( final ClassName annotationClassName );

        /**
         *  Adds annotations for the parameter.
         *
         *  @param  annotationSpecs The annotation.
         *  @return This {@code Builder} instance.
         */
        public Builder addAnnotations( final Iterable<AnnotationSpec> annotationSpecs );

        /**
         *  Adds text to the JavaDoc for the parameter.
         *
         *  @param  block   The comment text.
         *  @return This {@code Builder} instance.
         */
        public Builder addJavadoc( final CodeBlock block );

        /**
         *  Adds text to the Javadoc for the parameter.
         *
         *  @param  format  The format.
         *  @param  args    The arguments.
         *  @return This {@code Builder} instance.
         */
        public Builder addJavadoc( final String format, final Object... args );

        /**
         *  Adds modifiers for the parameter.
         *
         *  @param  modifiers   The modifiers.
         *  @return This {@code Builder} instance.
         */
        public Builder addModifiers( final Iterable<Modifier> modifiers );

        /**
         *  Adds modifiers for the parameter.
         *
         *  @param  modifiers   The modifiers.
         *  @return This {@code Builder} instance.
         */
        public Builder addModifiers( final Modifier... modifiers );

        /**
         *  Builds a new
         *  {@link ParameterSpec}
         *  instance from the added components.
         *
         *  @return The new {@code ParameterSpec} instance.
         */
        public ParameterSpec build();
    }
    //  interface Builder

        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  Creates a builder for a new
     *  {@code ParameterSpec}
     *  instance.
     *
     *  @param  type    The type of the new parameter.
     *  @param  name    The name of the new parameter.
     *  @param  modifiers   The modifiers for the new parameter.
     *  @return The builder.
     *
     *  @deprecated Replaced by
     *      {@link JavaComposer#parameterBuilder(Type, CharSequence, Modifier...)}.
     */
    @SuppressWarnings( "removal" )
    @Deprecated( since = "0.2.0", forRemoval = true )
    @API( status = DEPRECATED, since = "0.0.5" )
    public static Builder builder( final Type type, final CharSequence name, final Modifier... modifiers )
    {
        final var retValue = ParameterSpecImpl.builder( type, name, modifiers );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  builder()

    /**
     *  Creates a builder for a new
     *  {@code ParameterSpec}
     *  instance.
     *
     *  @param  type    The type of the new parameter.
     *  @param  name    The name of the new parameter.
     *  @param  modifiers   The modifiers for the new parameter.
     *  @return The builder.
     *
     *  @deprecated Replaced by
     *      {@link JavaComposer#parameterBuilder(TypeName, CharSequence, Modifier...)}.
     */
    @SuppressWarnings( "removal" )
    @Deprecated( since = "0.2.0", forRemoval = true )
    @API( status = DEPRECATED, since = "0.0.5" )
    public static Builder builder( final TypeName type, final CharSequence name, final Modifier... modifiers )
    {
        final var retValue = ParameterSpecImpl.builder( type, name, modifiers );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  builder()

    /**
     *  {@inheritDoc}
     */
    @Override
    public boolean equals( final Object o );

    /**
     *  Creates an instance of {@code ParameterSpec} from the given
     *  {@link VariableElement}
     *  instance.
     *
     *  @param  element The variable element.
     *  @return The parameter spec.
     *
     *  @deprecated Replaced by
     *      {@link JavaComposer#createParameter(VariableElement)}.
     */
    @Deprecated( since = "0.2.0", forRemoval = true )
    @API( status = DEPRECATED, since = "0.0.5" )
    @SuppressWarnings( {"removal","ClassReferencesSubclass"} )
    public static ParameterSpec get( final VariableElement element )
    {
        final var retValue = ParameterSpecImpl.get( element );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  get()

    /**
     *  {@inheritDoc}
     */
    @Override
    public int hashCode();

    /**
     *  Checks whether this parameter has the given modifier.
     *
     *  @param  modifier    The modifier.
     *  @return {@code true} if the parameter has the given modifier,
     *      {@code false} if not.
     */
    public boolean hasModifier( final Modifier modifier );

    /**
     *  Returns the name of this parameter.
     *
     *  @return The name.
     */
    public String name();

    /**
     *  Creates a new
     *  {@code ParameterSpec}
     *  instance for the given arguments.
     *
     *  @param  type    The type of the new parameter.
     *  @param  name    The name of the new parameter.
     *  @param  modifiers   The modifiers for the new parameter.
     *  @return The parameter specification.
     *
     *  @deprecated Replaced by
     *      {@link JavaComposer#parameterOf(Type, CharSequence, Modifier...)}.
     */
    @Deprecated( since = "0.2.0", forRemoval = true )
    @API( status = DEPRECATED, since = "0.0.5" )
    @SuppressWarnings( {"removal","ClassReferencesSubclass"} )
    public static ParameterSpec of( final Type type, final CharSequence name, final Modifier... modifiers )
    {
        final var retValue = ParameterSpecImpl.of( type, name, modifiers );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  of()

    /**
     *  Creates a new
     *  {@code ParameterSpec}
     *  instance for the given arguments.
     *
     *  @param  type    The type of the new parameter.
     *  @param  name    The name of the new parameter.
     *  @param  modifiers   The modifiers for the new parameter.
     *  @return The parameter specification.
     *
     *  @deprecated Replaced by
     *      {@link JavaComposer#parameterOf(TypeName, CharSequence, Modifier...)}.
     */
    @Deprecated( since = "0.2.0", forRemoval = true )
    @API( status = DEPRECATED, since = "0.0.5" )
    @SuppressWarnings( {"removal","ClassReferencesSubclass"} )
    public static ParameterSpec of( final TypeName type, final CharSequence name, final Modifier... modifiers )
    {
        final var retValue = ParameterSpecImpl.of( type, name, modifiers );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  of()

    /**
     *  Returns a builder that is initialised with the components of this
     *  parameter.
     *
     *  @return The builder.
     */
    public Builder toBuilder();

    /**
     *  Returns a builder for a parameter with the given type and name, and
     *  that is initialised with the components of this parameter.
     *
     *  @param  type    The type for the new parameter.
     *  @param  name    The name for the new parameter.
     *  @return The builder.
     *
     *  @deprecated Replaced by
     *      {@link #toBuilder(TypeName, CharSequence, boolean)}.
     */
    @Deprecated( since = "0.2.0", forRemoval = true )
    public default Builder toBuilder( final TypeName type, final CharSequence name )
    {
        final var retValue = toBuilder( type, name, true ); // this preserves to original behaviour

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  toBuilder()

    /**
     *  Returns a builder for a parameter with the given type and name, and
     *  that is initialised with the components of this parameter.
     *
     *  @param  type    The type for the new parameter.
     *  @param  name    The name for the new parameter.
     *  @param  keepJavadoc {@code true} if an existing Javadoc comment should
     *      be preserved, {@code false} if it should be dropped.
     *  @return The builder.
     */
    public Builder toBuilder( final TypeName type, final CharSequence name, final boolean keepJavadoc );

    /**
     *  {@inheritDoc}
     */
    @Override
    public String toString();

    /**
     *  Returns the type of this parameter.
     *
     *  @return The type
     */
    public TypeName type();
}
//  interface ParameterSpec

/*
 *  End of File
 */