package com.scholastic.sbam.server.database.codegen;

// Generated Jun 16, 2011 5:48:55 PM by Hibernate Tools 3.2.4.GA

/**
 * UcnConversionId generated by hbm2java
 */
public class UcnConversionId implements java.io.Serializable {

	private int ucn;
	private int ucnSuffix;

	public UcnConversionId() {
	}

	public UcnConversionId(int ucn, int ucnSuffix) {
		this.ucn = ucn;
		this.ucnSuffix = ucnSuffix;
	}

	public int getUcn() {
		return this.ucn;
	}

	public void setUcn(int ucn) {
		this.ucn = ucn;
	}

	public int getUcnSuffix() {
		return this.ucnSuffix;
	}

	public void setUcnSuffix(int ucnSuffix) {
		this.ucnSuffix = ucnSuffix;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof UcnConversionId))
			return false;
		UcnConversionId castOther = (UcnConversionId) other;

		return (this.getUcn() == castOther.getUcn())
				&& (this.getUcnSuffix() == castOther.getUcnSuffix());
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result + this.getUcn();
		result = 37 * result + this.getUcnSuffix();
		return result;
	}

}
