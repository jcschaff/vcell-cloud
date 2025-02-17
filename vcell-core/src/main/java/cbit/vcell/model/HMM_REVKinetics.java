/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.model;

import java.beans.PropertyVetoException;
import java.util.List;

import org.vcell.util.Issue;
import org.vcell.util.Issue.IssueCategory;
import org.vcell.util.IssueContext;

import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import org.vcell.util.Relatable;
import org.vcell.util.RelationVisitor;

/**
 * This class was generated by a SmartGuide.
 * 
 */
public class HMM_REVKinetics extends DistributedKinetics {

public HMM_REVKinetics(SimpleReaction simpleReaction) throws ExpressionException {
	super(KineticsDescription.HMM_reversible.getName(),simpleReaction);
	try {
		KineticsParameter rateParm = new KineticsParameter(getDefaultParameterName(ROLE_ReactionRate),new Expression(0.0),ROLE_ReactionRate,null);
		KineticsParameter currentParm = new KineticsParameter(getDefaultParameterName(ROLE_CurrentDensity),new Expression(0.0),ROLE_CurrentDensity,null);
		KineticsParameter kmFwdParm = new KineticsParameter(getDefaultParameterName(ROLE_KmFwd),new Expression(0.0),ROLE_KmFwd,null);
		KineticsParameter vMaxFwdParm = new KineticsParameter(getDefaultParameterName(ROLE_VmaxFwd),new Expression(0.0),ROLE_VmaxFwd,null);
		KineticsParameter kmRevParm = new KineticsParameter(getDefaultParameterName(ROLE_KmRev),new Expression(0.0),ROLE_KmRev,null);
		KineticsParameter vMaxRevParm = new KineticsParameter(getDefaultParameterName(ROLE_VmaxRev),new Expression(0.0),ROLE_VmaxRev,null);
		KineticsParameter netChargeValence = new KineticsParameter(getDefaultParameterName(ROLE_NetChargeValence),new Expression(1.0),ROLE_NetChargeValence,null);

		if (simpleReaction.getStructure() instanceof Membrane){
			setKineticsParameters(new KineticsParameter[] { rateParm, currentParm, netChargeValence, kmFwdParm, vMaxFwdParm, kmRevParm, vMaxRevParm });
		}else{
			setKineticsParameters(new KineticsParameter[] { rateParm, kmFwdParm, vMaxFwdParm, kmRevParm, vMaxRevParm });
		}
		updateGeneratedExpressions();
		refreshUnits();
	}catch (PropertyVetoException e){
		e.printStackTrace(System.out);
		throw new RuntimeException("unexpected exception: "+e.getMessage());
	}
}

	@Override
	public boolean compareEqual(org.vcell.util.Matchable obj) {
		if (obj == this){
			return true;
		}
		if (!(obj instanceof HMM_REVKinetics)){
			return false;
		}

		HMM_REVKinetics hmm = (HMM_REVKinetics)obj;

		if (!compareEqual0(hmm)){
			return false;
		}

		return true;
	}

	@Override
	public boolean relate(Relatable obj, RelationVisitor rv) {
		if (obj == this){
			return true;
		}
		if (!(obj instanceof HMM_REVKinetics)){
			return false;
		}

		HMM_REVKinetics hmm = (HMM_REVKinetics)obj;

		if (!relate0(hmm, rv)){
			return false;
		}

		return true;
	}
/**
 * Insert the method's description here.
 * Creation date: (5/12/2004 3:26:54 PM)
 * @return cbit.util.Issue[]
 */
@Override
public void gatherIssues(IssueContext issueContext, List<Issue> issueList){
	
	super.gatherIssues(issueContext, issueList);
	
	//
	// check for correct number of reactants and products
	//
	int reactantCount=0;
	int productCount=0;
	ReactionParticipant reactionParticipants[] = getReactionStep().getReactionParticipants();
	for (int i = 0; i < reactionParticipants.length; i++){
		if (reactionParticipants[i] instanceof Reactant){
			reactantCount++;
		}
		if (reactionParticipants[i] instanceof Product){
			productCount++;
		}
	}
	if (reactantCount!=1){
		issueList.add(new Issue(getReactionStep(),issueContext,IssueCategory.KineticsApplicability,"HMM Reversible Kinetics must have exactly one reactant",Issue.SEVERITY_ERROR));
	}
	if (productCount!=1){
		issueList.add(new Issue(getReactionStep(),issueContext,IssueCategory.KineticsApplicability,"HMM Reversible Kinetics must have exactly one product",Issue.SEVERITY_ERROR));
	}
}
/**
 * Insert the method's description here.
 * Creation date: (8/6/2002 9:52:55 AM)
 * @return cbit.vcell.model.KineticsDescription
 */
public KineticsDescription getKineticsDescription() {
	return KineticsDescription.HMM_reversible;
}
/**
 * Insert the method's description here.
 * Creation date: (8/6/2002 3:48:16 PM)
 * @return cbit.vcell.model.KineticsParameter
 */
public KineticsParameter getKmFwdParameter() {
	return getKineticsParameterFromRole(ROLE_KmFwd);
}
/**
 * Insert the method's description here.
 * Creation date: (8/6/2002 3:48:16 PM)
 * @return cbit.vcell.model.KineticsParameter
 */
public KineticsParameter getKmRevParameter() {
	return getKineticsParameterFromRole(ROLE_KmRev);
}
/**
 * Insert the method's description here.
 * Creation date: (8/6/2002 3:48:16 PM)
 * @return cbit.vcell.model.KineticsParameter
 */
public KineticsParameter getVmaxFwdParameter() {
	return getKineticsParameterFromRole(ROLE_VmaxFwd);
}
/**
 * Insert the method's description here.
 * Creation date: (8/6/2002 3:48:16 PM)
 * @return cbit.vcell.model.KineticsParameter
 */
public KineticsParameter getVmaxRevParameter() {
	return getKineticsParameterFromRole(ROLE_VmaxRev);
}
/**
 * Insert the method's description here.
 * Creation date: (3/31/2004 3:56:05 PM)
 */
protected void refreshUnits() {
	if (bRefreshingUnits){
		return;
	}
	try {
		bRefreshingUnits=true;
		
		Reactant R0 = null;
		int reactantCount = 0;
		Product P0 = null;
		int productCount = 0;
		ReactionParticipant reactionParticipants[] = getReactionStep().getReactionParticipants();
		for (int i = 0; i < reactionParticipants.length; i++){
			if (reactionParticipants[i] instanceof Reactant){
				reactantCount++;
				R0 = (Reactant)reactionParticipants[i];
			}
			if (reactionParticipants[i] instanceof Product){
				productCount++;
				P0 = (Product)reactionParticipants[i];
			}
		}

		Kinetics.KineticsParameter rateParm = getReactionRateParameter();
		Kinetics.KineticsParameter currentDensityParm = getCurrentDensityParameter();
		KineticsParameter chargeValenceParm = getChargeValenceParameter();
		Kinetics.KineticsParameter kmFwdParm = getKmFwdParameter();
		Kinetics.KineticsParameter vmaxFwdParm = getVmaxFwdParameter();
		Kinetics.KineticsParameter kmRevParm = getKmRevParameter();
		Kinetics.KineticsParameter vmaxRevParm = getVmaxRevParameter();
		Model model = getReactionStep().getModel();
		if (model != null) {
			ModelUnitSystem modelUnitSystem = model.getUnitSystem();
			if (getReactionStep().getStructure() instanceof Membrane){
				if (rateParm!=null){
					rateParm.setUnitDefinition(modelUnitSystem.getMembraneReactionRateUnit());
				}
				if (currentDensityParm!=null){
					currentDensityParm.setUnitDefinition(modelUnitSystem.getCurrentDensityUnit());
				}
				if (chargeValenceParm!=null){
					chargeValenceParm.setUnitDefinition(modelUnitSystem.getInstance_DIMENSIONLESS());
				}
				if (vmaxFwdParm!=null){
					vmaxFwdParm.setUnitDefinition(modelUnitSystem.getMembraneReactionRateUnit());
				}
				if (vmaxRevParm!=null){
					vmaxRevParm.setUnitDefinition(modelUnitSystem.getMembraneReactionRateUnit());
				}
				if (kmFwdParm!=null){
					if (R0!=null){
						kmFwdParm.setUnitDefinition(R0.getSpeciesContext().getUnitDefinition());
					}else{
						kmFwdParm.setUnitDefinition(modelUnitSystem.getMembraneConcentrationUnit());
					}
				}
				if (kmRevParm!=null){
					if (P0!=null){
						kmRevParm.setUnitDefinition(P0.getSpeciesContext().getUnitDefinition());
					}else{
						kmRevParm.setUnitDefinition(modelUnitSystem.getMembraneConcentrationUnit());
					}
				}
			}else{
				if (rateParm!=null){
					rateParm.setUnitDefinition(modelUnitSystem.getVolumeReactionRateUnit());
				}
				if (vmaxFwdParm!=null){
					vmaxFwdParm.setUnitDefinition(modelUnitSystem.getVolumeReactionRateUnit());
				}
				if (vmaxRevParm!=null){
					vmaxRevParm.setUnitDefinition(modelUnitSystem.getVolumeReactionRateUnit());
				}
				if (kmFwdParm!=null){
					if (R0!=null){
						kmFwdParm.setUnitDefinition(R0.getSpeciesContext().getUnitDefinition());
					}else{
						kmFwdParm.setUnitDefinition(modelUnitSystem.getVolumeConcentrationUnit());
					}
				}
				if (kmRevParm!=null){
					if (P0!=null){
						kmRevParm.setUnitDefinition(P0.getSpeciesContext().getUnitDefinition());
					}else{
						kmRevParm.setUnitDefinition(modelUnitSystem.getVolumeConcentrationUnit());
					}
				}
			}
		}
	}finally{
		bRefreshingUnits=false;
	}
}
/**
 * Insert the method's description here.
 * Creation date: (10/19/2003 12:05:14 AM)
 * @exception cbit.vcell.parser.ExpressionException The exception description.
 */
protected void updateGeneratedExpressions() throws cbit.vcell.parser.ExpressionException, PropertyVetoException {
	KineticsParameter rateParm = getKineticsParameterFromRole(ROLE_ReactionRate);
	KineticsParameter currentParm = getKineticsParameterFromRole(ROLE_CurrentDensity);
	if (currentParm==null && rateParm==null){
		return;
	}
	KineticsParameter kmFwd = getKineticsParameterFromRole(ROLE_KmFwd);
	KineticsParameter vMaxFwd = getKineticsParameterFromRole(ROLE_VmaxFwd);
	KineticsParameter kmRev = getKineticsParameterFromRole(ROLE_KmRev);
	KineticsParameter vMaxRev = getKineticsParameterFromRole(ROLE_VmaxRev);
		
	ReactionParticipant reactionParticipants[] = getReactionStep().getReactionParticipants();
	Reactant R0 = null;
	Product P0 = null;
	for (int i = 0; i < reactionParticipants.length; i++){
		if (reactionParticipants[i] instanceof Reactant && R0 == null){
			R0 = (Reactant)reactionParticipants[i];
		}
		if (reactionParticipants[i] instanceof Product && P0 == null){
			P0 = (Product)reactionParticipants[i];
		}
	}
	if (R0==null){
		System.out.println("HMM_REVKinetics.updateGeneratedExpressions(): HMM_REVKinetics must have exactly one reactant");
		return;
		//throw new RuntimeException("HMM_REVKinetics must have exactly one reactant");
	}
	if (P0==null){
		System.out.println("HMM_REVKinetics.updateGeneratedExpressions(): HMM_REVKinetics must have exactly one product");
		return;
		//throw new RuntimeException("HMM_REVKinetics must have exactly one product");
	}
	
	// PRIMARY REACTION RATE
	//	new Expression("(A0*R0/A1 - A2*P0/A3)/(1 + R0/A1 + P0/A3)"),  where Ai = { "VmaxFwd","KmFwd", "VmaxRev", "KmRev" }
	Expression vMaxFwd_Exp = getSymbolExpression(vMaxFwd);
	Expression vMaxRev_Exp = getSymbolExpression(vMaxRev);
	Expression kmFwd_Exp = getSymbolExpression(kmFwd);
	Expression kmRev_Exp = getSymbolExpression(kmRev);
	Expression R0_exp = getSymbolExpression(R0.getSpeciesContext());
	Expression P0_exp = getSymbolExpression(P0.getSpeciesContext());
	
	Expression R_over_fwd = Expression.div(R0_exp, kmFwd_Exp);
	Expression P_over_rev = Expression.div(P0_exp, kmRev_Exp);
	
	Expression numerator = Expression.add(Expression.mult(R_over_fwd, vMaxFwd_Exp), Expression.negate(Expression.mult(P_over_rev, vMaxRev_Exp)));
	Expression denominator = Expression.add(new Expression(1.0), R_over_fwd, P_over_rev);
	
	Expression newRateExp = Expression.div(numerator, denominator).flatten();	
//	Expression newRateExp = new Expression("("+vMaxFwd.getName()+"*"+R0.getName()+"/"+kmFwd.getName()+" - "+vMaxRev.getName()+"*"+P0.getName()+"/"+kmRev.getName()+")/"+
//			"(1.0 + "+R0.getName()+"/"+kmFwd.getName()+" + "+P0.getName()+"/"+kmRev.getName()+")");
	rateParm.setExpression(newRateExp);

	
	
	// SECONDARY CURRENT DENSITY
	// update from reaction rate
	updateInwardCurrentDensityFromReactionRate();
}
}
