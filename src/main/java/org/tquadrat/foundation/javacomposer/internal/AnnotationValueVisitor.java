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

import static org.apiguardian.api.API.Status.INTERNAL;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.SimpleAnnotationValueVisitor14;
import java.util.List;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.javacomposer.internal.AnnotationSpecImpl.BuilderImpl;

/**
 *  Annotation value visitor adding members to the given builder instance.
 *
 *  @author Square, Inc.
 *  @version $Id: AnnotationValueVisitor.java 1085 2024-01-05 16:23:28Z tquadrat $
 *  @modified Thomas Thrien - thomas.thrien@tquadrat.org
 *  @UMLGraph.link
 *  @since 0.0.5
 */
@ClassVersion( sourceVersion = "$Id: AnnotationValueVisitor.java 1085 2024-01-05 16:23:28Z tquadrat $" )
@API( status = INTERNAL, since = "0.0.5" )
public class AnnotationValueVisitor extends SimpleAnnotationValueVisitor14<BuilderImpl, String>
{
        /*------------*\
    ====** Attributes **=======================================================
        \*------------*/
    /**
     * The builder.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    private final BuilderImpl m_Builder;

        /*--------------*\
    ====** Constructors **=====================================================
        \*--------------*/
    /**
     * Creates a new {@code Visitor} instance.
     *
     * @param builder The builder that takes the new members.
     */
    public AnnotationValueVisitor( @SuppressWarnings( "UseOfConcreteClass" ) final BuilderImpl builder )
    {
        super( builder );
        m_Builder = builder;
    }   //  Visitor()

        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     * {@inheritDoc}
     */
    @Override
    protected final BuilderImpl defaultAction( final Object o, final String name )
    {
        m_Builder.addMemberForValue( name, o );

        //---* Done *----------------------------------------------------------
        return m_Builder;
    }   //  defaultAction()

    /**
     * {@inheritDoc}
     */
    @Override
    public final BuilderImpl visitAnnotation( final AnnotationMirror a, final String name )
    {
        m_Builder.addMember( name, "$L", m_Builder.getFactory().createAnnotation( a ) );

        //---* Done *----------------------------------------------------------
        return m_Builder;
    }   //  visitAnnotation()

    /**
     * {@inheritDoc}
     */
    @Override
    public final BuilderImpl visitEnumConstant( final VariableElement c, final String name )
    {
        m_Builder.addMember( name, "$T.$L", c.asType(), c.getSimpleName() );

        //---* Done *----------------------------------------------------------
        return m_Builder;
    }   //  visitEnumConstant()

    /**
     * {@inheritDoc}
     */
    @Override
    public final BuilderImpl visitType( final TypeMirror t, final String name )
    {
        m_Builder.addMember( name, "$T.class", t );

        //---* Done *----------------------------------------------------------
        return m_Builder;
    }   //  visitType()

    /**
     * {@inheritDoc}
     */
    @Override
    public final BuilderImpl visitArray( final List<? extends AnnotationValue> values, final String name )
    {
        for( final var value : values )
        {
            value.accept( this, name );
        }

        //---* Done *----------------------------------------------------------
        return m_Builder;
    }   //  visitArray()
}
//  class AnnotationValueVisitor

/*
 *  End of File
 */