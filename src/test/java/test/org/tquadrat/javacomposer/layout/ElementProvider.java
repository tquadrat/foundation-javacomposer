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

package test.org.tquadrat.javacomposer.layout;

import static javax.lang.model.element.Modifier.ABSTRACT;
import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;
import static org.tquadrat.foundation.javacomposer.JavaComposer.JAVADOC_TAG_VALUE;
import static org.tquadrat.foundation.javacomposer.Primitives.INT;
import static org.tquadrat.foundation.javacomposer.Primitives.VOID;
import static org.tquadrat.foundation.lang.CommonConstants.EMPTY_STRING;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.annotation.UtilityClass;
import org.tquadrat.foundation.exception.PrivateConstructorForStaticClassCalledError;
import org.tquadrat.foundation.javacomposer.ClassName;
import org.tquadrat.foundation.javacomposer.FieldSpec;
import org.tquadrat.foundation.javacomposer.JavaComposer;
import org.tquadrat.foundation.javacomposer.JavaFile;
import org.tquadrat.foundation.javacomposer.TypeSpec;

/**
 *  Create the elements that are used by the layout tests. Changing one of the
 *  methods in this class may break various tests.
 *
 *  @author Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: ElementProvider.java 943 2021-12-21 01:34:32Z tquadrat $
 */
@SuppressWarnings( "UseOfObsoleteDateTimeApi" )
@UtilityClass
@ClassVersion( sourceVersion = "$Id: ElementProvider.java 943 2021-12-21 01:34:32Z tquadrat $" )
public final class ElementProvider
{
        /*--------------*\
    ====** Constructors **=====================================================
        \*--------------*/
    /**
     *  No instance allowed for this class.
     */
    private ElementProvider() { throw new PrivateConstructorForStaticClassCalledError( ElementProvider.class ); }

        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  Creates an annotation.
     *
     *  @param  composer    The factory for the JavaComposer artefacts.
     *  @return The
     *      {@link TypeSpec}
     *      for an annotation.
     */
    public static final TypeSpec createAnnotation( final JavaComposer composer )
    {
        final var member1 = composer.methodBuilder( "member1" )
            .addModifiers( PUBLIC, ABSTRACT )
            .returns( String.class )
            .build();
        final var member2 = composer.methodBuilder( "member2" )
            .addModifiers( PUBLIC, ABSTRACT )
            .addJavadoc( "The second member for this annotation.\n" )
            .returns( String.class )
            .defaultValue( "$S", "Fußpilz" )
            .build();
        final var retValue = composer.annotationTypeBuilder( "TestAnnotation" )
            .addMethod( member1 )
            .addMethod( member2 )
            .build();

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  createAnnotation()

    /**
     *  Creates a class.
     *
     *  @param  composer    The factory for the JavaComposer artefacts.
     *  @return The
     *      {@link TypeSpec}
     *      for a regular class.
     */
    public static final TypeSpec createClass( final JavaComposer composer )
    {
        final var annotation1 = composer.annotationBuilder( ClassName.from( "org.tquadrat.foundation", "Option" ) )
            .forceInline( true )
            .addMember( "member1", "$S", "value" )
            .addMember( "member2", "$S", "value" )
            .build();

        final var annotation2 = composer.annotationBuilder( ClassName.from( "org.tquadrat.foundation", "Param" ) )
            .forceInline( false )
            .addMember( "member1", "$S", "value" )
            .addMember( "member2", "$S", "value" )
            .build();

        final var innerInterface = composer.interfaceBuilder( "InnerInterface" )
            .addModifiers( STATIC )
            .addAnnotation( composer.createClassVersionAnnotation() )
            .addJavadoc( "This is an inner interface.\n" )
            .addJavadoc( "\n@since 10\n" )
            .build();
        final var innerClass = composer.classBuilder( "InnerClass" )
            .addModifiers( PRIVATE )
            .addAnnotation( composer.createClassVersionAnnotation() )
            .addJavadoc( "This is an inner class.\n" )
            .addJavadoc( "\n@since 10\n" )
            .build();

        final var attribute = composer.fieldBuilder( UUID.class, "m_Id", PRIVATE, FINAL )
            .addAnnotation( annotation1 )
            .addAnnotation( annotation2 )
            .build();
        final var staticAttribute = composer.fieldBuilder( innerClass, "m_StaticField", PRIVATE, STATIC )
            .build();
        final var property = composer.fieldBuilder( INT, "m_Property", PRIVATE )
            .addJavadoc( "A property.\n" )
            .build();
        var code = composer.codeBlockBuilder()
            .add( "$T.getDefault()", Locale.class )
            .build();
        final var initialized = composer.fieldBuilder( Locale.class, "m_Locale", PRIVATE )
            .initializer( code )
            .build();

        final var parameter = composer.parameterBuilder( UUID.class, "uuid", FINAL )
            .addJavadoc( "The id" )
            .build();
        code = composer.codeBlockBuilder()
            .addStatement( "$N = $N", attribute, parameter )
            .build();
        final var setter = composer.methodBuilder( "setter" )
            .addCode( code )
            .addModifiers( PUBLIC, FINAL )
            .addParameter( parameter )
            .returns( VOID )
            .build();

        code = composer.codeBlockBuilder()
            .beginControlFlow( "if( $L )", "flag" )
            .addStatement( "doThis()" )
            .nextControlFlow( "else" )
            .addStatement( "doSomethingDifferent()" )
            .endControlFlow()
            .build();
        final var method1 = composer.methodBuilder( "method1" )
            .returns( VOID )
            .addCode( code )
            .build();

        code = composer.codeBlockBuilder()
            .addStatement( "return $S", "value" )
            .build();
        final var method2 = composer.methodBuilder( "method2" )
            .returns( String.class )
            .addCode( code )
            .build();

        //---* This is not a constant as it does not have an initializer *-----
        final var finalStaticAttribute = composer.fieldBuilder( innerClass, "m_FinalStaticField", PRIVATE, FINAL, STATIC )
            .build();

        final var retValue = composer.classBuilder( "TestClass" )
            .addModifiers( PUBLIC )
            .addMethod( method1 )
            .addMethod( setter )
            .addAnnotation( composer.createClassVersionAnnotation() )
            .addType( innerInterface )
            .addType( innerClass )
            .addMethod( composer.constructorBuilder().build() )
            .addProperty( property, false )
            .addProperty( initialized, false )
            .addField( staticAttribute )
            .addField( finalStaticAttribute )
            .addFields( createConstants( composer ) )
            .addField( attribute )
            .addMethod( method2 )
            .addInitializerBlock( composer.codeBlockOf( "$N = new $T.randomUUID();\n", attribute, UUID.class ) )
            .addStaticBlock( composer.codeBlockOf( "$N = new $N();\n", staticAttribute, innerClass ) )
            .build();

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  createClass()

    /**
     *  Creates some constants.
     *
     *  @param  composer    The factory for the JavaComposer artefacts.
     *  @return The field specs for some constant.
     */
    @SuppressWarnings( "unused" )
    public static final Collection<FieldSpec> createConstants( final JavaComposer composer )
    {
        final Collection<FieldSpec> retValue = new ArrayList<>( 4 );

        retValue.add( composer.createConstant( "STRING_CONSTANT1", "value", composer.codeBlockOf( "A String constant: $L\n", JAVADOC_TAG_VALUE ) ) );
        retValue.add( composer.createConstant( "STRING_CONSTANT2", "other_value", composer.codeBlockOf( "Another String constant" ) ) );
        retValue.add( composer.createConstant( "INTEGER_CONSTANT", 42, null ) );
        retValue.add( composer.createConstant( "DATE_CONSTANT", Date.class, composer.codeBlockOf( "$T.now()", Date.class ), null ) );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  createConstant()

    /**
     *  Creates an enum class.
     *
     *  @param  composer    The factory for the JavaComposer artefacts.
     *  @return The
     *      {@link TypeSpec}
     *      for an enum class.
     */
    public static final TypeSpec createEnum( final JavaComposer composer )
    {
        final var method = composer.createToStringMethod( composer.codeBlockOf( "return $L;\n", "name()" ) );

        final var anonymousClass = composer.anonymousClassBuilder( EMPTY_STRING )
            .addJavadoc( "A value that overrides $N()", method )
            .addMethod( composer.createToStringMethod( composer.codeBlockOf( "return $S;\n", "something else" ) ) )
            .build();
        final var retValue = composer.enumBuilder( "TestEnum" )
            .addAnnotation( composer.createClassVersionAnnotation() )
            .addEnumConstant( "VALUE1" )
            .addEnumConstant( "VALUE2", "Another Value" )
            .addEnumConstant( "VALUE3", anonymousClass )
            .addMethod( method )
            .build();

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  createEnum()

    /**
     *  Creates an interface.
     *
     *  @param  composer    The factory for the JavaComposer artefacts.
     *  @return The
     *      {@link TypeSpec}
     *      for an interface.
     */
    public static final TypeSpec createInterface( final JavaComposer composer )
    {
        final var innerClass = composer.classBuilder( "InnerClass" )
                .addModifiers( PUBLIC, STATIC )
                .addAnnotation( composer.createClassVersionAnnotation() )
                .addJavadoc( "This is an inner class.\n" )
                .addJavadoc( "\n@since 10\n" )
                .build();
        final var retValue = composer.interfaceBuilder( "TestInterface" )
            .addModifiers( PUBLIC )
            .addAnnotation( composer.createClassVersionAnnotation() )
            .addType( innerClass )
            .addFields( createConstants( composer ) )
            .addMethod( composer.createToStringBuilder().addModifiers( ABSTRACT ).build() )
            .build();

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  createInterface()

    /**
     *  Creates a
     *  {@link JavaFile}
     *  for the given type.
     *  with the given layout.
     *
     *  @param  composer    The factory for the JavaComposer artefacts.
     *  @param  typeSpec    The type spec.
     *  @return The {@code JavaFile} instance.
     */
    public static final JavaFile createJavaFile( final JavaComposer composer, final TypeSpec typeSpec )
    {
        final var retValue = composer.javaFileBuilder( "org.tquadrat.test", typeSpec )
            .build();

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  createJavaFile()
}
//  class ElementProvider

/*
 *  End of File
 */