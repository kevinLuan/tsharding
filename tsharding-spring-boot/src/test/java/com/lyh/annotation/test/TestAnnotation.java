package com.lyh.annotation.test;

import java.io.IOException;

import com.mogujie.route.rule.RouteRule;
import com.mogujie.trade.db.DataSourceRouting;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.BooleanMemberValue;
import javassist.bytecode.annotation.ClassMemberValue;
import javassist.bytecode.annotation.IntegerMemberValue;
import javassist.bytecode.annotation.StringMemberValue;

public class TestAnnotation {
	public static void main(String[] args) throws NoSuchFieldException, SecurityException, IllegalArgumentException,
			IllegalAccessException, CannotCompileException, IOException {
		test();
	}

	static void test() throws CannotCompileException, IOException {
		ClassPool pool = ClassPool.getDefault();
		// create the class
		System.out.println(TestMapper.class.getName());
		CtClass cc = pool.makeClass(TestMapper.class.getName() + "$Proxy");
		ClassFile ccFile = cc.getClassFile();
		ConstPool constpool = ccFile.getConstPool();

		// create the annotation
		AnnotationsAttribute attr = new AnnotationsAttribute(constpool, AnnotationsAttribute.visibleTag);
		Annotation annot = new Annotation(DataSourceRouting.class.getName(), constpool);
		annot.addMemberValue("dataSource", new StringMemberValue("myDB", ccFile.getConstPool()));
		annot.addMemberValue("table", new IntegerMemberValue(ccFile.getConstPool(), 1));
		// annot.addMemberValue("value", new
		// IntegerMemberValue(ccFile.getConstPool(), 0));
		attr.addAnnotation(annot);
		ccFile.addAttribute(attr);

		// create the method
		CtMethod mthd = CtNewMethod.make("public Integer getInteger() { return 100; }", cc);
		cc.addMethod(mthd);
		mthd.getMethodInfo().addAttribute(attr);
		cc.writeFile("./");
		// generate the class
		Class<?> clazz = cc.toClass();

		// length is zero
		java.lang.annotation.Annotation[] annots = clazz.getAnnotations();
		System.out.println(annots.length);
		System.out.println(clazz.getName());
	}

	public Annotation buildAnnotation(DataSourceRouting routing, ConstPool constpool, String database, int databases,
			String table, int tables, boolean isReadWriteSplitting, Class<? extends RouteRule<?>> routeRule) {
		Annotation annot = new Annotation(DataSourceRouting.class.getName(), constpool);
		annot.addMemberValue("dataSource", new StringMemberValue(database, constpool));
		annot.addMemberValue("databases", new IntegerMemberValue(constpool, databases));
		annot.addMemberValue("table", new StringMemberValue(table, constpool));
		annot.addMemberValue("tables", new IntegerMemberValue(constpool, tables));
		annot.addMemberValue("isReadWriteSplitting", new BooleanMemberValue(isReadWriteSplitting, constpool));
		annot.addMemberValue("routeRule", new ClassMemberValue(routeRule.getName(), constpool));
		return annot;
	}
}
