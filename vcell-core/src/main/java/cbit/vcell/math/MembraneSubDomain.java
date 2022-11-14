/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.math;
import java.util.*;

import org.vcell.util.CommentStringTokenizer;
import org.vcell.util.Compare;
import org.vcell.util.Matchable;

import cbit.vcell.geometry.SurfaceClass;
import cbit.vcell.math.ComputeNormalComponentEquation.NormalComponent;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
/**
 * This class was generated by a SmartGuide.
 * 
 */
@SuppressWarnings("serial")
public class MembraneSubDomain extends SubDomain implements SubDomain.DomainWithBoundaryConditions {
	
	@Override
	protected String startToken() {
		return VCML.MembraneSubDomain;
	}


	/**
	 * list of symbols from future VCML versions that can be ignored
	 */
	private static final Map<String,Integer> FUTURE_VCML_SYMBOLS;
	static {
		FUTURE_VCML_SYMBOLS = new HashMap<String, Integer>( );
		FUTURE_VCML_SYMBOLS.put("Name", 1);
	}
	
	private Vector<JumpCondition> jumpConditionList = new Vector<JumpCondition>();
	private CompartmentSubDomain insideCompartment = null;
	private CompartmentSubDomain outsideCompartment = null;

	private BoundaryConditionType boundaryConditionTypeXp = BoundaryConditionType.getDIRICHLET();
	private BoundaryConditionType boundaryConditionTypeXm = BoundaryConditionType.getDIRICHLET();
	private BoundaryConditionType boundaryConditionTypeYp = BoundaryConditionType.getDIRICHLET();
	private BoundaryConditionType boundaryConditionTypeYm = BoundaryConditionType.getDIRICHLET();
	private BoundaryConditionType boundaryConditionTypeZp = BoundaryConditionType.getDIRICHLET();
	private BoundaryConditionType boundaryConditionTypeZm = BoundaryConditionType.getDIRICHLET();
	/**
	 * membrane, not advection
	 */
	private Expression velocityX = null;
	/**
	 * membrane, not advection
	 */
	private Expression velocityY = null;
	
	
/**
 * This method was created by a SmartGuide.
 * @param inside cbit.vcell.math.CompartmentSubDomain
 * @param outside cbit.vcell.math.CompartmentSubDomain
 */
public MembraneSubDomain(CompartmentSubDomain inside, CompartmentSubDomain outside, String name) {
//	super(SurfaceClass.createName(inside.getName(), outside.getName()));
	super(name);
	this.insideCompartment = inside;
	this.outsideCompartment = outside;
}

	public void getAllExpressions(List<Expression> expressionList, MathDescription mathDescription){
		super.getAllExpressions0(expressionList, mathDescription);
		if (this.velocityX!=null) expressionList.add(this.velocityX);
		if (this.velocityY!=null) expressionList.add(this.velocityY);
	}


/**
 * helper class for creating {@link MembraneSubDomain} from VCML tokens
 * the superclass field name is immutable and must be determined before creating the class
 * Therefore do the necessary parsing to determine name components in this class
 */
private static class Builder {
	final String name;
	final String beforeComment;
	final CompartmentSubDomain insideCompartment;
	final CompartmentSubDomain outsideCompartment;
	final MathDescription provider;
	final CommentStringTokenizer tokens;
	
	private Builder(MathDescription provider, org.vcell.util.Token token, CommentStringTokenizer tokens) throws MathException, ExpressionException { 
		this.provider = provider;
		this.tokens = tokens;
		beforeComment = token.getBeforeComment();
		insideCompartment = provider.getCompartmentSubDomain(tokens.nextToken());
		if (insideCompartment == null){
			throw new MathFormatException("defined membrane subdomain without a corresponding inside volume subdomain first");
		}	
		outsideCompartment = provider.getCompartmentSubDomain(tokens.nextToken());
		if (outsideCompartment == null){
			throw new MathFormatException("defined membrane subdomain without a corresponding outside volume subdomain first");
		}	
		name = SurfaceClass.createName(insideCompartment.getName(), outsideCompartment.getName());
	}
}

public void swapInsideOutsideSubdomains() {
	// only used in MathDescription.compare function
	CompartmentSubDomain tempSubdomain = insideCompartment;
	insideCompartment = outsideCompartment;
	outsideCompartment = tempSubdomain;
}

/**
 * create from VCML input
 * @param provider creating object 
 * @param token current token
 * @param tokens token container
 * @return new object
 * @throws MathException
 * @throws ExpressionException
 */
public static MembraneSubDomain create(MathDescription provider, org.vcell.util.Token token, CommentStringTokenizer tokens) throws MathException, ExpressionException { 
	MembraneSubDomain msb = new MembraneSubDomain(new Builder(provider,token,tokens));
	return msb;
}

/**
 * implements {@link #create(MathDescription, org.vcell.util.Token, CommentStringTokenizer)}
 */
private MembraneSubDomain(Builder b) throws MathException, ExpressionException { 
	super(b.name);
	setBeforeComment(b.beforeComment);
	insideCompartment = b.insideCompartment;
	outsideCompartment = b.outsideCompartment;
	parseBlock(b.provider,b.tokens);
}

/*
public MembraneSubDomain(MathDescription provider, org.vcell.util.Token token, CommentStringTokenizer tokens) throws MathException, ExpressionException { 
	super("");
	setBeforeComment(token.getBeforeComment());

	insideCompartment = provider.getCompartmentSubDomain(tokens.nextToken());
	if (insideCompartment == null){
		throw new MathFormatException("defined membrane subdomain without a corresponding inside volume subdomain first");
	}	
	outsideCompartment = provider.getCompartmentSubDomain(tokens.nextToken());
	if (outsideCompartment == null){
		throw new MathFormatException("defined membrane subdomain without a corresponding outside volume subdomain first");
	}	
	name = SurfaceClass.createName(insideCompartment.getName(), outsideCompartment.getName());
	parseBlock(provider,tokens);
}
*/

@Override
protected void parse(MathDescription mathDesc, String tokenString,
		CommentStringTokenizer tokens) throws MathException,
		ExpressionException {
	if (tokenString.equalsIgnoreCase(VCML.OdeEquation)){
		tokenString = tokens.nextToken();
		Variable var = mathDesc.getVariable(tokenString);
		if (var == null){
			throw new MathFormatException("variable "+tokenString+" not defined");
		}	
		if (!(var instanceof MemVariable)){
			throw new MathFormatException("variable "+tokenString+" not a "+VCML.MembraneVariable);
		}	
		OdeEquation ode = new OdeEquation((MemVariable)var, null,null);
		ode.read(tokens, mathDesc);
		addEquation(ode);
		return;
	}			
	if (tokenString.equalsIgnoreCase(VCML.BoundaryXm)){
		String type = tokens.nextToken();
		boundaryConditionTypeXm = new BoundaryConditionType(type);
		return;
	}			
	if (tokenString.equalsIgnoreCase(VCML.BoundaryXp)){
		String type = tokens.nextToken();
		boundaryConditionTypeXp = new BoundaryConditionType(type);
		return;
	}			
	if (tokenString.equalsIgnoreCase(VCML.BoundaryYm)){
		String type = tokens.nextToken();
		boundaryConditionTypeYm = new BoundaryConditionType(type);
		return;
	}			
	if (tokenString.equalsIgnoreCase(VCML.BoundaryYp)){
		String type = tokens.nextToken();
		boundaryConditionTypeYp = new BoundaryConditionType(type);
		return;
	}			
	if (tokenString.equalsIgnoreCase(VCML.BoundaryZm)){
		String type = tokens.nextToken();
		boundaryConditionTypeZm = new BoundaryConditionType(type);
		return;
	}			
	if (tokenString.equalsIgnoreCase(VCML.BoundaryZp)){
		String type = tokens.nextToken();
		boundaryConditionTypeZp = new BoundaryConditionType(type);
		return;
	}			
	if (tokenString.equalsIgnoreCase(VCML.PdeEquation)){
		tokenString = tokens.nextToken();
		Variable var = mathDesc.getVariable(tokenString);
		if (var == null){
			throw new MathFormatException("variable "+tokenString+" not defined");
		}	
		if (!(var instanceof MemVariable)){
			throw new MathFormatException("variable "+tokenString+" not a MembraneVariable");
		}	
		PdeEquation pde = new PdeEquation((MemVariable)var);
		pde.read(tokens, mathDesc);
		addEquation(pde);
		return;
	}			
	if (tokenString.equalsIgnoreCase(VCML.MembraneRegionEquation)){
		tokenString = tokens.nextToken();
		Variable var = mathDesc.getVariable(tokenString);
		if (var == null){
			throw new MathFormatException("variable "+tokenString+" not defined");
		}	
		if (!(var instanceof MembraneRegionVariable)){
			throw new MathFormatException("variable "+tokenString+" not a "+VCML.MembraneRegionVariable);
		}	
		MembraneRegionEquation mre = new MembraneRegionEquation((MembraneRegionVariable)var, null);
		mre.read(tokens, mathDesc);
		addEquation(mre);
		return;
	}
	if (tokenString.equalsIgnoreCase(VCML.ComputeNormalX) ||
		tokenString.equalsIgnoreCase(VCML.ComputeNormalY) ||
		tokenString.equalsIgnoreCase(VCML.ComputeNormalZ)){
		NormalComponent normalComponent = null;
		if (tokenString.equalsIgnoreCase(VCML.ComputeNormalX)){
			normalComponent = NormalComponent.X;
		}else if (tokenString.equalsIgnoreCase(VCML.ComputeNormalY)){
			normalComponent = NormalComponent.Y;
		}else if (tokenString.equalsIgnoreCase(VCML.ComputeNormalZ)){
			normalComponent = NormalComponent.Z;
		}
		tokenString = tokens.nextToken();
		Variable var = mathDesc.getVariable(tokenString);
		if (var == null){
			throw new MathFormatException("variable "+tokenString+" not defined");
		}	
		if (!(var instanceof MemVariable)){
			throw new MathFormatException("variable "+tokenString+" not a "+VCML.MembraneVariable);
		}	
		ComputeNormalComponentEquation computeNormalComponentEquation = new ComputeNormalComponentEquation((MemVariable)var, normalComponent);
		computeNormalComponentEquation.read(tokens, mathDesc);
		addEquation(computeNormalComponentEquation);
		return;
	}
	if (tokenString.equalsIgnoreCase(VCML.ParticleProperties)){
		ParticleProperties pp = new ParticleProperties(mathDesc, tokens);
		if(pp.getVariable().getDomain().getName().equals(this.getName())){
			addParticleProperties(pp);
		}else{
			throw new MathException("Variable (" + pp.getVariable().getName() + ") is defined in domain " + pp.getVariable().getDomain().getName() +
									". \nHowever the variable particle properties of " + pp.getVariable().getName() + " is defined in domain " + this.getName() + ". \nPlease check your model.");
		}	
		return;
	}
	if (tokenString.equalsIgnoreCase(VCML.ParticleJumpProcess)){
		ParticleJumpProcess particleJumpProcess = ParticleJumpProcess.fromVCML(mathDesc, tokens);
		addParticleJumpProcess(particleJumpProcess);
		return;
	}			
	if (tokenString.equalsIgnoreCase(VCML.JumpCondition)){
		tokenString = tokens.nextToken();
		Variable var = mathDesc.getVariable(tokenString);
		if (var == null){
			throw new MathFormatException("variable "+tokenString+" not defined");
		}	
		JumpCondition jump = null;
		if (var instanceof VolVariable) {
			jump = new JumpCondition((VolVariable)var);
		} else if (var instanceof VolumeRegionVariable) {
			jump = new JumpCondition((VolumeRegionVariable)var);
		} else {
			throw new MathException("variable "+tokenString+" is neither a VolumeVariable nor a VolumeRegionVariable");
		}
		jump.read(tokens, mathDesc);
		addJumpCondition(jump);
		return;
	}			
	if (tokenString.equalsIgnoreCase(VCML.FastSystem)){
		FastSystem fs = new FastSystem(mathDesc);
		fs.read(tokens, mathDesc);
		setFastSystem(fs);
		return;
	}			
	if (tokenString.equalsIgnoreCase(VCML.VelocityX)){
		velocityX = parseAndBind(mathDesc, tokens);
		return;
	}
	if (tokenString.equalsIgnoreCase(VCML.VelocityY)){
		velocityY = parseAndBind(mathDesc, tokens);
		return;
	}
	if (FUTURE_VCML_SYMBOLS.containsKey(tokenString)) {
		final int numberAdditionalTokensToSkip = FUTURE_VCML_SYMBOLS.get(tokenString);
		for (int i = 0; i < numberAdditionalTokensToSkip; i++) { 
			tokens.nextToken();
		}
		return;
	}
	throw new MathFormatException("unexpected identifier "+tokenString);
}

/**
 * This method was created by a SmartGuide.
 * @param equation cbit.vcell.math.Equation
 */
public void addEquation(Equation equation) throws MathException {
	if (equation instanceof JumpCondition){
		addJumpCondition((JumpCondition)equation);
	}else{
		super.addEquation(equation);
	}
}


/**
 * This method was created by a SmartGuide.
 * @param equation cbit.vcell.math.Equation
 */
public void addJumpCondition(JumpCondition jumpCondition) throws MathException {
	if (getJumpCondition(jumpCondition.getVariable()) != null){
		throw new MathException("jumpCondition for variable "+jumpCondition.getVariable()+" already exists");
	}
	jumpConditionList.addElement(jumpCondition);	
}

public void removeJumpCondition(Variable variable){
	JumpCondition jumpCondition = getJumpCondition(variable);
	if (jumpCondition!=null){
		jumpConditionList.removeElement(jumpCondition);
	}
}

/**
 * This method was created in VisualAge.
 * @return boolean
 * @param object java.lang.Object
 */
public boolean compareEqual(Matchable object) {
	if (!super.compareEqual0(object)){
		return false;
	}
	if (!Compare.isEqualFields(this,object)) {
		return false;
	}
	// compare jumpConditions
	//
	MembraneSubDomain msd = (MembraneSubDomain)object;
	if (!Compare.isEqual(jumpConditionList,msd.jumpConditionList)){
		return false;
	}
	return true;

//	MembraneSubDomain msd = null;
//	if (!(object instanceof MembraneSubDomain)){
//		return false;
//	}else{
//		msd = (MembraneSubDomain)object;
//	}
//	//
//	// compare jumpConditions
//	//
//	if (!Compare.isEqual(jumpConditionList,msd.jumpConditionList)){
//		return false;
//	}
//	//
//	// compare insideCompartment
//	//
//	if (insideCompartment==null){
//		if (msd.insideCompartment!=null){
//			return false;
//		}
//	}else if (!Compare.isEqual(insideCompartment,msd.insideCompartment)){
//		return false;
//	}
//	//
//	// compare outsideCompartment
//	//
//	if (outsideCompartment==null){
//		if (msd.outsideCompartment!=null){
//			return false;
//		}
//	}else if (!Compare.isEqual(outsideCompartment,msd.outsideCompartment)){
//		return false;
//	}
//	
//	//
//	// compare boundaryConditions
//	//
//	if (!Compare.isEqual(boundaryConditionTypeXp,msd.boundaryConditionTypeXp)){
//		return false;
//	}
//	if (!Compare.isEqual(boundaryConditionTypeXm,msd.boundaryConditionTypeXm)){
//		return false;
//	}
//	if (!Compare.isEqual(boundaryConditionTypeYp,msd.boundaryConditionTypeYp)){
//		return false;
//	}
//	if (!Compare.isEqual(boundaryConditionTypeYm,msd.boundaryConditionTypeYm)){
//		return false;
//	}
//	if (!Compare.isEqual(boundaryConditionTypeZp,msd.boundaryConditionTypeZp)){
//		return false;
//	}
//	if (!Compare.isEqual(boundaryConditionTypeZm,msd.boundaryConditionTypeZm)){
//		return false;
//	}
//	return true;
}

public Expression getVelocityX() {
	return velocityX;
}

public Expression getVelocityY() {
	return velocityY;
}

public void setVelocityX(Expression velocityX) {
	this.velocityX = velocityX;
}

public void setVelocityY(Expression velocityY) {
	this.velocityY = velocityY;
}

public boolean isMoving( ) {
	return Expression.notZero(velocityX) || Expression.notZero(velocityY);
}

/**
 * This method was created by a SmartGuide.
 * @return java.lang.String
 */
public BoundaryConditionType getBoundaryConditionXm() {
	return boundaryConditionTypeXm;
}


/**
 * This method was created by a SmartGuide.
 * @return java.lang.String
 */
public BoundaryConditionType getBoundaryConditionXp() {
	return boundaryConditionTypeXp;
}


/**
 * This method was created by a SmartGuide.
 * @return java.lang.String
 */
public BoundaryConditionType getBoundaryConditionYm() {
	return boundaryConditionTypeYm;
}


/**
 * This method was created by a SmartGuide.
 * @return java.lang.String
 */
public BoundaryConditionType getBoundaryConditionYp() {
	return boundaryConditionTypeYp;
}


/**
 * This method was created by a SmartGuide.
 * @return java.lang.String
 */
public BoundaryConditionType getBoundaryConditionZm() {
	return boundaryConditionTypeZm;
}


/**
 * This method was created by a SmartGuide.
 * @return java.lang.String
 */
public BoundaryConditionType getBoundaryConditionZp() {
	return boundaryConditionTypeZp;
}


/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.math.CompartmentSubDomain
 */
public CompartmentSubDomain getInsideCompartment() {
	return insideCompartment;
}


/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.math.JumpCondition
 * @param volVar cbit.vcell.math.VolVariable
 * @exception java.lang.Exception The exception description.
 */
public JumpCondition getJumpCondition(Variable volVar) {
	Enumeration<JumpCondition> enum1 = jumpConditionList.elements();
	while (enum1.hasMoreElements()){
		JumpCondition jump = enum1.nextElement();
		if (jump.getVariable().getName().equals(volVar.getName())){
			return jump;
		}
	}
	return null;
}


/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.math.JumpCondition
 */
public Enumeration<JumpCondition> getJumpConditions() {
	return jumpConditionList.elements();
}


/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.math.CompartmentSubDomain
 */
public CompartmentSubDomain getOutsideCompartment() {
	return outsideCompartment;
}


/**
 * This method was created by a SmartGuide.
 * @return java.lang.String
 */
public String getVCML(int spatialDimension) {
	StringBuilder buffer = new StringBuilder();
	buffer.append(VCML.MembraneSubDomain+" "+insideCompartment.getName()+" "+outsideCompartment.getName()+" {\n");
	buffer.append("\t"+VCML.Name+"\t "+getName()+"\n");
	if (spatialDimension>=1){
		buffer.append("\t"+VCML.BoundaryXm+"\t "+boundaryConditionTypeXm.boundaryTypeStringValue()+"\n");
		buffer.append("\t"+VCML.BoundaryXp+"\t "+boundaryConditionTypeXp.boundaryTypeStringValue()+"\n");
		addExpression(buffer, VCML.VelocityX,velocityX); 
	}
	if (spatialDimension>=2){
		buffer.append("\t"+VCML.BoundaryYm+"\t "+boundaryConditionTypeYm.boundaryTypeStringValue()+"\n");
		buffer.append("\t"+VCML.BoundaryYp+"\t "+boundaryConditionTypeYp.boundaryTypeStringValue()+"\n");
		addExpression(buffer, VCML.VelocityY,velocityY); 
	}
	if (spatialDimension==3){
		buffer.append("\t"+VCML.BoundaryZm+"\t "+boundaryConditionTypeZm.boundaryTypeStringValue()+"\n");
		buffer.append("\t"+VCML.BoundaryZp+"\t "+boundaryConditionTypeZp.boundaryTypeStringValue()+"\n");
	}
	Enumeration<Equation> enum1 = getEquations();
	while (enum1.hasMoreElements()){
		Equation equ = enum1.nextElement();
		buffer.append(equ.getVCML());
	}	
	//particle initial conditions
	for (ParticleProperties pp : getParticleProperties()){
		buffer.append(pp.getVCML(spatialDimension));
		buffer.append("\n");
	}
	//Jump processes
	for (ParticleJumpProcess particleJumpProcess : getParticleJumpProcesses()){
		buffer.append(particleJumpProcess.getVCML());
		buffer.append("\n");
	}
	Enumeration<JumpCondition> enum2 = getJumpConditions();
	while (enum2.hasMoreElements()){
		JumpCondition jc = enum2.nextElement();
		buffer.append(jc.getVCML());
	}	
	if (getFastSystem()!=null){
		buffer.append(getFastSystem().getVCML());
	}
	buffer.append("}\n");
	return buffer.toString();		
}

private static void addExpression(StringBuilder buffer, String keyword, Expression exp) {
	if (exp != null) {
		buffer.append('\t');
		buffer.append(keyword);
		buffer.append("\t\t");
		buffer.append(exp.infix());
		buffer.append(";\n");
		
	}
	
}






/**
 * This method was created in VisualAge.
 * @param equation cbit.vcell.math.Equation
 */
public void replaceJumpCondition(JumpCondition jumpCondition) throws MathException {
	JumpCondition currentJumpCondition = getJumpCondition((VolVariable)jumpCondition.getVariable());
	if (currentJumpCondition!=null){
		jumpConditionList.removeElement(currentJumpCondition);
	}
	addJumpCondition(jumpCondition);
}


/**
 * This method was created by a SmartGuide.
 * @return java.lang.String
 */
public void setBoundaryConditionXm(BoundaryConditionType bc) {
	boundaryConditionTypeXm = bc;
}


/**
 * This method was created by a SmartGuide.
 * @return java.lang.String
 */
public void setBoundaryConditionXp(BoundaryConditionType bc) {
	boundaryConditionTypeXp = bc;
}


/**
 * This method was created by a SmartGuide.
 * @return java.lang.String
 */
public void setBoundaryConditionYm(BoundaryConditionType bc) {
	boundaryConditionTypeYm = bc;
}


/**
 * This method was created by a SmartGuide.
 * @return java.lang.String
 */
public void setBoundaryConditionYp(BoundaryConditionType bc) {
	boundaryConditionTypeYp = bc;
}


/**
 * This method was created by a SmartGuide.
 * @return java.lang.String
 */
public void setBoundaryConditionZm(BoundaryConditionType bc) {
	boundaryConditionTypeZm = bc;
}


/**
 * This method was created by a SmartGuide.
 * @return java.lang.String
 */
public void setBoundaryConditionZp(BoundaryConditionType bc) {
	boundaryConditionTypeZp = bc;
}
}
