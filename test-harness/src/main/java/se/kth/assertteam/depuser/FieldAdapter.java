package se.kth.assertteam.depuser;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Opcodes;

public class FieldAdapter extends FieldVisitor implements Opcodes  {

	LibrariesUser lu;
	String className;
	String name;

	public FieldAdapter(final FieldVisitor fv, LibrariesUser lu, String className, String name) {
		super(ASM5, fv);
		this.lu = lu;
		this.className = className;
		this.name = name;
	}

	@Override
	public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
		lu.insertIfPartOfPackages(ClassAdapter.extractByteCodeTypeDesc(desc), "", className, name);
		return super.visitAnnotation(desc, visible);
	}

}
