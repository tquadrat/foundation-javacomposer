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

package org.tquadrat.foundation.javacomposer;

import static org.apiguardian.api.API.Status.STABLE;

import javax.lang.model.element.Modifier;
import java.util.Set;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.javacomposer.internal.FieldSpecImpl;

/**
 *  The specification for a generated field declaration.
 *
 *  @author Square,Inc.
 *  @modified   Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: FieldSpec.java 1085 2024-01-05 16:23:28Z tquadrat $
 *  @since 0.0.5
 *
 *  @UMLGraph.link
 */
@ClassVersion( sourceVersion = "$Id: FieldSpec.java 1085 2024-01-05 16:23:28Z tquadrat $" )
@API( status = STABLE, since = "0.0.5" )
public sealed interface FieldSpec
    permits FieldSpecImpl
{
        /*---------------*\
    ====** Inner Classes **====================================================
        \*---------------*/
    /**
     *  The specification of a builder for an instance of an implementation for
     *  {@link FieldSpec}
     *
     *  @author Square,Inc.
     *  @modified   Thomas Thrien - thomas.thrien@tquadrat.org
     *  @version $Id: FieldSpec.java 1085 2024-01-05 16:23:28Z tquadrat $
     *  @since 0.0.5
     *
     *  @UMLGraph.link
     */
    @SuppressWarnings( "InnerClassOfInterface" )
    @ClassVersion( sourceVersion = "$Id: FieldSpec.java 1085 2024-01-05 16:23:28Z tquadrat $" )
    @API( status = STABLE, since = "0.0.5" )
    public static sealed interface Builder
        permits FieldSpecImpl.BuilderImpl
    {
            /*---------*\
        ====** Methods **======================================================
            \*---------*/
        /**
         *  Adds an annotation for the field.
         *
         *  @param  annotationSpec  The annotation.
         *  @return This {@code Builder} instance.
         */
        public Builder addAnnotation( final AnnotationSpec annotationSpec );

        /**
         *  Adds an annotation for the field.
         *
         *  @param  annotation  The annotation.
         *  @return This {@code Builder} instance.
         */
        public Builder addAnnotation( final Class<?> annotation );

        /**
         *  Adds an annotation for the field.
         *
         *  @param  annotation  The annotation.
         *  @return This {@code Builder} instance.
         */
        public Builder addAnnotation( final ClassName annotation );

        /**
         *  Adds annotations for the field.
         *
         *  @param  annotationSpecs The annotations.
         *  @return This {@code Builder} instance.
         */
        public Builder addAnnotations( final Iterable<AnnotationSpec> annotationSpecs );

        /**
         *  Adds a Javadoc comment for the field.
         *
         *  @param  block   The comment block.
         *  @return This {@code Builder} instance.
         */
        public Builder addJavadoc( final CodeBlock block );

        /**
         *  Adds a Javadoc comment for the field.
         *
         *  @param  format  The format.
         *  @param  args    The arguments.
         *  @return This {@code Builder} instance.
         */
        public Builder addJavadoc( final String format, final Object... args );

        /**
         *  Adds modifiers for the field.
         *
         *  @param  modifiers   The modifiers.
         *  @return This {@code Builder} instance.
         */
        public Builder addModifiers( final Modifier... modifiers );

        /**
         *  Builds a new
         *  {@link FieldSpec}
         *  instance from the added components.
         *
         *  @return The {@code FieldSpec} instance.
         */
        public FieldSpec build();

        /**
         *  Sets the initializer for the field.
         *
         *  @param  codeBlock   The code that initialises the field.
         *  @return This {@code Builder} instance.
         */
        public Builder initializer( final CodeBlock codeBlock );

        /**
         *  Sets the initializer for the field.
         *
         *  @param  format  The format.
         *  @param  args    The arguments.
         *  @return This {@code Builder} instance.
         */
        public Builder initializer( final String format, final Object... args );
    }
    //  interface Builder

        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
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
     *  Checks whether the given modifier was applied to this field.
     *
     *  @param  modifier    The modifier.
     *  @return {@code true} if the given modifier has been applied to this
     *      field.
     */
    public boolean hasModifier( final Modifier modifier );

    /**
     *  Returns the modifiers for this field.
     *
     *  @return The modifiers.
     */
    public Set<Modifier> modifiers();


    /**
     *  Returns the name for this field.
     *
     *  @return The name.
     */
    public String name();

    /**
     *  Returns a builder that is already initialised with the components that
     *  built this field.
     *
     *  @return The builder.
     */
    public Builder toBuilder();

    /**
     *  {@inheritDoc}
     */
    @Override
    public String toString();

    /**
     *  Returns the type of the field.
     *
     *  @return The type.
     */
    public TypeName type();
}
//  interface FieldSpec

/*
 *  End of File
 */