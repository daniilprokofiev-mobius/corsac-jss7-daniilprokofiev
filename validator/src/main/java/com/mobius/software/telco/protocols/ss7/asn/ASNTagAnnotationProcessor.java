package com.mobius.software.telco.protocols.ss7.asn;

/*
 * Mobius Software LTD
 * Copyright 2019, Mobius Software LTD and individual contributors
 * by the @authors tag.
 *
 * This program is free software: you can redistribute it and/or modify
 * under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

/**
*
* @author yulian oifa
*
*/

import io.netty.buffer.ByteBuf;

import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.tools.Diagnostic;

import com.mobius.software.telco.protocols.ss7.asn.annotations.ASNDecode;
import com.mobius.software.telco.protocols.ss7.asn.annotations.ASNEncode;
import com.mobius.software.telco.protocols.ss7.asn.annotations.ASNGenericMapping;
import com.mobius.software.telco.protocols.ss7.asn.annotations.ASNLength;
import com.mobius.software.telco.protocols.ss7.asn.annotations.ASNTag;
import com.mobius.software.telco.protocols.ss7.asn.annotations.ASNValidate;
import com.mobius.software.telco.protocols.ss7.asn.annotations.ASNWrappedTag;

@SupportedAnnotationTypes( { "com.mobius.software.telco.protocols.ss7.asn.annotations.ASNTag", "com.mobius.software.telco.protocols.ss7.asn.annotations.ASNWrappedTag" } )
public class ASNTagAnnotationProcessor extends AbstractProcessor {
	private static final String EXPECTED_ENCODE_DECODE_PARAM=ByteBuf.class.getCanonicalName();
	private static final String EXPECTED_BOOLEAN_PARAM=Boolean.class.getCanonicalName();
	private static final String EXPECTED_INTEGER_PARAM=Integer.class.getCanonicalName();
	private static final String EXPECTED_PARSER_PARAM=ASNParser.class.getCanonicalName();
	
	@Override
	public boolean process(Set<? extends TypeElement> annotations,RoundEnvironment roundEnv) {
		Set<? extends Element> annotatedWrappedElements = roundEnv.getElementsAnnotatedWith(ASNWrappedTag.class);
		
		for(Element annotatedElement:annotatedWrappedElements) {
			if(!(annotatedElement instanceof TypeElement)) {
				processingEnv.getMessager().printMessage( Diagnostic.Kind.ERROR,String.format( "Element '%s' is annotated as @ASNWrappedTag, however its not a class", annotatedElement.getSimpleName()));
			}
		}
		
		Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(ASNTag.class);
		
		for(Element annotatedElement:annotatedElements) {
			if(!(annotatedElement instanceof TypeElement)) {
				processingEnv.getMessager().printMessage( Diagnostic.Kind.ERROR,String.format( "Element '%s' is annotated as @ASNTag, however its not a class", annotatedElement.getSimpleName()));
			}
		}
		
		return validateInnerStructure(annotatedWrappedElements) && validateInnerStructure(annotatedElements);
	}
	
	public boolean validateInnerStructure(Set<? extends Element> annotatedElements) {
		for(Element annotatedElement:annotatedElements) {
			final TypeElement typeElement = ( TypeElement )annotatedElement;
			
			String className=typeElement.getQualifiedName().toString();		
			if(annotatedElement.getKind()!=ElementKind.INTERFACE) {
				List<? extends Element> innerElements=annotatedElement.getEnclosedElements();	
				List<ExecutableElement> methods=ElementFilter.methodsIn(innerElements);
				List<VariableElement> fields=ElementFilter.fieldsIn(innerElements);
								
				TypeMirror mirror=typeElement.getSuperclass();
				while(mirror.getKind()!=TypeKind.NONE)
				{					
					TypeElement parentTypeElement = (TypeElement) processingEnv.getTypeUtils().asElement(mirror);					
					innerElements=parentTypeElement.getEnclosedElements();				
					methods.addAll(ElementFilter.methodsIn(innerElements));
					fields.addAll(ElementFilter.fieldsIn(innerElements));
					mirror=parentTypeElement.getSuperclass();
				}
				
				Boolean hasSubTag=false;
				Integer validateTags=0;
				Integer lengthTags=0;
				Integer encodeTags=0;
				Integer decodeTags=0;
				Integer mappingTags=0;
				
				for(VariableElement field:fields) {
					Element fieldElement=processingEnv.getTypeUtils().asElement(field.asType());
					if(fieldElement instanceof TypeElement) {
						ASNTag innerTag=null;
						if(((TypeElement)fieldElement).getQualifiedName().toString().equals(List.class.getCanonicalName())) {
							if (field.asType().getKind() == TypeKind.DECLARED) {
								List<? extends TypeMirror> args =((DeclaredType) field.asType()).getTypeArguments();
								if(args.size()==1) {
									innerTag=processingEnv.getTypeUtils().asElement(args.get(0)).getAnnotation(ASNTag.class);									
								}
							}
						}
						else
							innerTag=((TypeElement)fieldElement).getAnnotation(ASNTag.class);
						
						if(innerTag!=null) {										
							hasSubTag=true;
							break;
						}
					}
				}
				
				for(ExecutableElement method:methods) {
					List<? extends VariableElement> params=method.getParameters();
					ASNValidate validate=method.getAnnotation(ASNValidate.class);
					if(validate!=null) {
						validateTags+=1;					
						if(params.size()!=0)
							processingEnv.getMessager().printMessage( Diagnostic.Kind.ERROR,String.format( "Element '%s' is annotated as @ASNTag, however its method %s annoted with @ASNValidate should not have any parameters", className, method.getSimpleName()));						
					}
					
					ASNLength length=method.getAnnotation(ASNLength.class);
					if(length!=null) {
						lengthTags+=1;					
						if(params.size()!=1)
							processingEnv.getMessager().printMessage( Diagnostic.Kind.ERROR,String.format( "Element '%s' is annotated as @ASNTag, however its method %s annoted with @ASNLength should have only one parameter", className, method.getSimpleName()));
						else {
							String realParamName=((TypeElement)processingEnv.getTypeUtils().asElement(params.get(0).asType())).getQualifiedName().toString();
							if(!realParamName.equals(EXPECTED_PARSER_PARAM))
									processingEnv.getMessager().printMessage( Diagnostic.Kind.ERROR,String.format( "Element '%s' is annotated as @ASNTag, however its method %s annoted with @ASNLength should have only one parameter with type %s", className, method.getSimpleName(), EXPECTED_PARSER_PARAM));
						}
					}
					
					ASNEncode encode=method.getAnnotation(ASNEncode.class);
					if(encode!=null) {
						encodeTags+=1;	
						
						if(params.size()!=2)
							processingEnv.getMessager().printMessage( Diagnostic.Kind.ERROR,String.format( "Element '%s' is annotated as @ASNTag, however its method %s annoted with @ASNEncode should have two parameters", className, method.getSimpleName()));
						else {
							String realParamName=((TypeElement)processingEnv.getTypeUtils().asElement(params.get(0).asType())).getQualifiedName().toString();
							if(!realParamName.equals(EXPECTED_PARSER_PARAM))
									processingEnv.getMessager().printMessage( Diagnostic.Kind.ERROR,String.format( "Element '%s' is annotated as @ASNTag, however its method %s annoted with @ASNEncode should have two parameter3 with type %s %s", className, method.getSimpleName(), EXPECTED_PARSER_PARAM, EXPECTED_ENCODE_DECODE_PARAM));
							
							realParamName=((TypeElement)processingEnv.getTypeUtils().asElement(params.get(1).asType())).getQualifiedName().toString();
							if(!realParamName.equals(EXPECTED_ENCODE_DECODE_PARAM))
									processingEnv.getMessager().printMessage( Diagnostic.Kind.ERROR,String.format( "Element '%s' is annotated as @ASNTag, however its method %s annoted with @ASNEncode should have two parameter3 with type %s %s", className, method.getSimpleName(), EXPECTED_PARSER_PARAM, EXPECTED_ENCODE_DECODE_PARAM));
						}
					}
					
					ASNDecode decode=method.getAnnotation(ASNDecode.class);
					if(decode!=null) {
						//we may have decode multiple times for Generic
						decodeTags=1;					
	
						if(params.size()!=6)
							processingEnv.getMessager().printMessage( Diagnostic.Kind.ERROR,String.format( "Element '%s' is annotated as @ASNTag, however its method %s annoted with @ASNDecode should have six parameters", className, method.getSimpleName()));
						else {
							String firstParamName=((TypeElement)processingEnv.getTypeUtils().asElement(params.get(0).asType())).getQualifiedName().toString();
							String thirdParamName=((TypeElement)processingEnv.getTypeUtils().asElement(params.get(2).asType())).getQualifiedName().toString();
							String fifthParamName=((TypeElement)processingEnv.getTypeUtils().asElement(params.get(4).asType())).getQualifiedName().toString();
							String sixthParamName=((TypeElement)processingEnv.getTypeUtils().asElement(params.get(5).asType())).getQualifiedName().toString();
							if(!firstParamName.equals(EXPECTED_PARSER_PARAM))
								processingEnv.getMessager().printMessage( Diagnostic.Kind.ERROR,String.format( "Element '%s' is annotated as @ASNTag, however its method %s annoted with @ASNDecode should have six parameters with type %s ANY %s ConcurrentHashMap %s %s", className, method.getSimpleName(), EXPECTED_PARSER_PARAM, EXPECTED_ENCODE_DECODE_PARAM, EXPECTED_BOOLEAN_PARAM, EXPECTED_INTEGER_PARAM));							
						
							if(!thirdParamName.equals(EXPECTED_ENCODE_DECODE_PARAM))
									processingEnv.getMessager().printMessage( Diagnostic.Kind.ERROR,String.format( "Element '%s' is annotated as @ASNTag, however its method %s annoted with @ASNDecode should have six parameters with type %s ANY %s ConcurrentHashMap %s %s", className, method.getSimpleName(), EXPECTED_PARSER_PARAM, EXPECTED_ENCODE_DECODE_PARAM, EXPECTED_BOOLEAN_PARAM, EXPECTED_INTEGER_PARAM));							
							
							if(!fifthParamName.equals(EXPECTED_BOOLEAN_PARAM))
								processingEnv.getMessager().printMessage( Diagnostic.Kind.ERROR,String.format( "Element '%s' is annotated as @ASNTag, however its method %s annoted with @ASNDecode should have six parameters with type %s ANY %s ConcurrentHashMap %s %s", className, method.getSimpleName(), EXPECTED_PARSER_PARAM, EXPECTED_ENCODE_DECODE_PARAM, EXPECTED_BOOLEAN_PARAM, EXPECTED_INTEGER_PARAM));
							
							if(!sixthParamName.equals(EXPECTED_INTEGER_PARAM))
								processingEnv.getMessager().printMessage( Diagnostic.Kind.ERROR,String.format( "Element '%s' is annotated as @ASNTag, however its method %s annoted with @ASNDecode should have six parameters with type %s ANY %s ConcurrentHashMap %s %s", className, method.getSimpleName(), EXPECTED_PARSER_PARAM, EXPECTED_ENCODE_DECODE_PARAM, EXPECTED_BOOLEAN_PARAM, EXPECTED_INTEGER_PARAM));
						}
					}
					
					ASNGenericMapping mapping=method.getAnnotation(ASNGenericMapping.class);
					if(mapping!=null) {
						mappingTags+=1;					
						if(params.size()!=1)
							processingEnv.getMessager().printMessage( Diagnostic.Kind.ERROR,String.format( "Element '%s' is annotated as @ASNTag, however its method %s annoted with @ASNGenericMapping should have one parameter", className, method.getSimpleName()));
						else {
							String realParamName=((TypeElement)processingEnv.getTypeUtils().asElement(params.get(0).asType())).getQualifiedName().toString();
							if(!realParamName.equals(EXPECTED_PARSER_PARAM))
									processingEnv.getMessager().printMessage( Diagnostic.Kind.ERROR,String.format( "Element '%s' is annotated as @ASNTag, however its method %s annoted with @ASNGenericMapping should have only one parameter with type %s", className, method.getSimpleName(), EXPECTED_PARSER_PARAM));
						}
					}
				}
				
				if(validateTags>1)
					processingEnv.getMessager().printMessage( Diagnostic.Kind.ERROR,String.format( "Class '%s' is annotated as @ASNTag, however has multiple methods found with ASNValidate annotation", className));
				
				if(lengthTags>1)
					processingEnv.getMessager().printMessage( Diagnostic.Kind.ERROR,String.format( "Class '%s' is annotated as @ASNTag, however has multiple methods found with ASNLength annotation", className));
				
				if(encodeTags>1)
					processingEnv.getMessager().printMessage( Diagnostic.Kind.ERROR,String.format( "Class '%s' is annotated as @ASNTag, however has multiple methods found with ASNEncode annotation", className));
					
				if(decodeTags>1)
						processingEnv.getMessager().printMessage( Diagnostic.Kind.ERROR,String.format( "Class '%s' is annotated as @ASNTag, however has multiple methods found with ASNDecode annotation", className));
				
				if(mappingTags>1)
					processingEnv.getMessager().printMessage( Diagnostic.Kind.ERROR,String.format( "Class '%s' is annotated as @ASNTag, however has multiple methods found with ASNGenericMapping annotation", className));
			
				if(!hasSubTag && lengthTags==0) 
					processingEnv.getMessager().printMessage( Diagnostic.Kind.ERROR,String.format( "Class '%s' is annotated as @ASNTag, however no method found with ASNLength annotation", className)); 			        
				
				if(!hasSubTag && encodeTags==0)
					processingEnv.getMessager().printMessage( Diagnostic.Kind.ERROR,String.format( "Class '%s' is annotated as @ASNTag, however no method found with ASNEncode annotation", className));
				
				if(!hasSubTag && decodeTags==0)
					processingEnv.getMessager().printMessage( Diagnostic.Kind.ERROR,String.format( "Class '%s' is annotated as @ASNTag, however no method found with ASNDecode annotation", className));
			}
		}	
		
		return true;
	}
}