package se.kth.assertteam.depuser;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.signature.SignatureVisitor;

public class SignatureAdapter extends SignatureVisitor implements Opcodes {

	LibrariesUser lu;
	String className;
	String from;

	public SignatureAdapter(LibrariesUser lu, String className, String from) {
		super(ASM5);
		this.lu = lu;
		this.className = className;
	}

	@Override
	public void  visitClassType(String name) {
		super.visitClassType(name);
		lu.insertIfPartOfPackages(name, "", className, from);
	}
}
