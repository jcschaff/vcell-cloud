package cbit.vcell.model;

import java.beans.PropertyVetoException;
/*�
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
�*/
import java.util.*;
import cbit.vcell.parser.*;
import cbit.util.*;
import java.beans.PropertyChangeEvent;
/**
 * This class was generated by a SmartGuide.
 * 
 */
public class MassActionKinetics extends DistributedKinetics {
/**
 * MassActionKinetics constructor comment.
 * @param name java.lang.String
 * @param exp cbit.vcell.parser.Expression
 */
public MassActionKinetics(ReactionStep reactionStep) throws ExpressionException {
	super(KineticsDescription.MassAction.getName(),reactionStep);
	try {
		KineticsParameter rateParm = new KineticsParameter(getDefaultParameterName(ROLE_ReactionRate),new Expression(0.0),ROLE_ReactionRate,null);
		KineticsParameter currentParm = new KineticsParameter(getDefaultParameterName(ROLE_CurrentDensity),new Expression(0.0),ROLE_CurrentDensity,null);
		KineticsParameter kf = new KineticsParameter(getDefaultParameterName(ROLE_KForward),new Expression(0.0),ROLE_KForward,null);
		KineticsParameter kr = new KineticsParameter(getDefaultParameterName(ROLE_KReverse),new Expression(0.0),ROLE_KReverse,null);

		if (reactionStep.getStructure() instanceof Membrane){
			setKineticsParameters(new KineticsParameter[] { rateParm, currentParm, kf, kr });
		}else{
			setKineticsParameters(new KineticsParameter[] { rateParm, kf, kr });
		}
		updateGeneratedExpressions();
		refreshUnits();
	}catch (PropertyVetoException e){
		e.printStackTrace(System.out);
		throw new RuntimeException("unexpected exception: "+e.getMessage());
	}
	
	if (!(reactionStep instanceof SimpleReaction)){
		throw new IllegalArgumentException("expecting SimpleReaction for mass action kinetics type");
	}
}
/**
 * Checks for internal representation of objects, not keys from database
 * @return boolean
 * @param obj java.lang.Object
 */
public boolean compareEqual(org.vcell.util.Matchable obj) {
	if (obj == this){
		return true;
	}
	if (!(obj instanceof MassActionKinetics)){
		return false;
	}
	
	MassActionKinetics mak = (MassActionKinetics)obj;

	if (!compareEqual0(mak)){
		return false;
	}
	
	return true;
}
/**
 * Insert the method's description here.
 * Creation date: (8/6/2002 3:37:07 PM)
 * @return cbit.vcell.model.KineticsParameter
 */
public KineticsParameter getForwardRateParameter() {
	return getKineticsParameterFromRole(ROLE_KForward);
}
/**
 * Insert the method's description here.
 * Creation date: (8/6/2002 9:52:55 AM)
 * @return cbit.vcell.model.KineticsDescription
 */
public KineticsDescription getKineticsDescription() {
	return KineticsDescription.MassAction;
}
/**
 * Insert the method's description here.
 * Creation date: (8/6/2002 3:37:07 PM)
 * @return cbit.vcell.model.KineticsParameter
 */
public KineticsParameter getReverseRateParameter() {
	return getKineticsParameterFromRole(ROLE_KReverse);
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
		
		Kinetics.KineticsParameter rateParm = getReactionRateParameter();
		Kinetics.KineticsParameter currentDensityParm = getCurrentDensityParameter();
		Kinetics.KineticsParameter forwardRateParm = getForwardRateParameter();
		Kinetics.KineticsParameter reverseRateParm = getReverseRateParameter();

		if (getReactionStep().getStructure() instanceof Membrane){
			rateParm.setUnitDefinition(cbit.vcell.units.VCUnitDefinition.UNIT_molecules_per_um2_per_s);
			if (currentDensityParm!=null){
				currentDensityParm.setUnitDefinition(cbit.vcell.units.VCUnitDefinition.UNIT_pA_per_um2);
			}
		}else if (getReactionStep().getStructure() instanceof Feature){
			rateParm.setUnitDefinition(cbit.vcell.units.VCUnitDefinition.UNIT_uM_per_s);
		}else{
			throw new RuntimeException("unexpected structure type "+getReactionStep().getStructure()+" in MassActionKinetics.refreshUnits()");
		}
		
		cbit.vcell.units.VCUnitDefinition kfUnits = rateParm.getUnitDefinition();
		cbit.vcell.units.VCUnitDefinition krUnits = rateParm.getUnitDefinition();
		ReactionParticipant reactionParticipants[] = getReactionStep().getReactionParticipants();
		for (int i = 0; i < reactionParticipants.length; i++){
			if (reactionParticipants[i] instanceof Reactant){
				cbit.vcell.units.VCUnitDefinition reactantUnit = reactionParticipants[i].getSpeciesContext().getUnitDefinition();
				if (reactionParticipants[i].getStoichiometry()!=1){
					reactantUnit = reactantUnit.raiseTo(new ucar.units.RationalNumber(reactionParticipants[i].getStoichiometry()));
				}
				kfUnits = kfUnits.divideBy(reactantUnit);
			}else if (reactionParticipants[i] instanceof Product){
				cbit.vcell.units.VCUnitDefinition productUnit = reactionParticipants[i].getSpeciesContext().getUnitDefinition();
				if (reactionParticipants[i].getStoichiometry()!=1){
					productUnit = productUnit.raiseTo(new ucar.units.RationalNumber(reactionParticipants[i].getStoichiometry()));
				}
				krUnits = krUnits.divideBy(productUnit);
			}
		}
		if (forwardRateParm!=null && !kfUnits.compareEqual(forwardRateParm.getUnitDefinition())){
			forwardRateParm.setUnitDefinition(kfUnits);
		}
		if (reverseRateParm!=null && !krUnits.compareEqual(reverseRateParm.getUnitDefinition())){
			reverseRateParm.setUnitDefinition(krUnits);
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
	KineticsParameter kf = getKineticsParameterFromRole(ROLE_KForward);
	KineticsParameter kr = getKineticsParameterFromRole(ROLE_KReverse);
	if (currentParm==null && rateParm==null){
		return;
	}
	
	Membrane.MembraneVoltage V = null;
	if (getReactionStep().getStructure() instanceof Membrane){
		V = ((Membrane)getReactionStep().getStructure()).getMembraneVoltage();
	}
			
	ReactionParticipant rp_Array[] = getReactionStep().getReactionParticipants();
	Expression forwardRateTerm = new Expression(kf.getName());
	Expression reverseRateTerm = new Expression(kr.getName());
	int reactantCount = 0;
	int productCount = 0;
	for (int i = 0; i < rp_Array.length; i++) {
		Expression term = null;
		if (rp_Array[i] instanceof Reactant){
			String speciesContextName = rp_Array[i].getName();
			reactantCount++;
			if (rp_Array[i].getStoichiometry() < 1){
				throw new ExpressionException("reactant must have stoichiometry of at least 1");
			}else if (rp_Array[i].getStoichiometry() == 1){
				term = new Expression(speciesContextName);
			}else{
				term = Expression.power(new Expression(speciesContextName),new Expression(rp_Array[i].getStoichiometry()));
			}	
			forwardRateTerm = Expression.mult(forwardRateTerm,term);	
		}else if (rp_Array[i] instanceof Product){
			String speciesContextName = rp_Array[i].getName();
			productCount++;
			if (rp_Array[i].getStoichiometry() < 1){
				throw new RuntimeException("product must have stoichiometry of at least 1");
			}else if (rp_Array[i].getStoichiometry() == 1){
				term = new Expression(speciesContextName);
			}else{
				term = Expression.power(new Expression(speciesContextName),new Expression(rp_Array[i].getStoichiometry()));
			}	
			reverseRateTerm = Expression.mult(reverseRateTerm,term);	
		}	
	}

	Expression newRateExp = null;
	if (reactantCount > 0 && productCount > 0){
		newRateExp = Expression.add(forwardRateTerm,Expression.negate(reverseRateTerm));
	}else if (reactantCount > 0){
		newRateExp = forwardRateTerm;
	}else if (productCount > 0){
		newRateExp = Expression.negate(reverseRateTerm);
	}else{
		newRateExp = new Expression(0.0);
	}
	newRateExp.bindExpression(getReactionStep());
	rateParm.setExpression(newRateExp);
	
	if (getReactionStep().getPhysicsOptions() == ReactionStep.PHYSICS_MOLECULAR_AND_ELECTRICAL){
		Expression tempCurrentExpression = null;
		int z = (int)getReactionStep().getChargeCarrierValence().getConstantValue();
		ReservedSymbol F = ReservedSymbol.FARADAY_CONSTANT;
		ReservedSymbol F_nmol = ReservedSymbol.FARADAY_CONSTANT_NMOLE;
		ReservedSymbol N_PMOLE = ReservedSymbol.N_PMOLE;
		if (getReactionStep() instanceof SimpleReaction){
			tempCurrentExpression = Expression.mult(new Expression("("+z+"*"+F.getName()+"/"+N_PMOLE.getName()+")"), new Expression(rateParm.getName()));
		}else{
			tempCurrentExpression = Expression.mult(new Expression(z+"*"+F_nmol.getName()), new Expression(rateParm.getName()));
		}
		tempCurrentExpression.bindExpression(getReactionStep());
		if (currentParm == null){
			addKineticsParameter(new KineticsParameter(getDefaultParameterName(ROLE_CurrentDensity),tempCurrentExpression,ROLE_CurrentDensity,cbit.vcell.units.VCUnitDefinition.UNIT_pA_per_um2));
		}else{
			currentParm.setExpression(tempCurrentExpression);
		}
	}else{
		if (currentParm != null && !currentParm.getExpression().isZero()){
			//removeKineticsParameter(currentParm);
			currentParm.setExpression(new Expression(0.0));
		}
	}
}
}
