package cbit.image;
/*�
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
�*/
import cbit.vcell.geometry.Coordinate;
import cbit.vcell.math.CoordinateIndex;
import cbit.util.Extent;
import cbit.util.Origin;
import java.lang.reflect.Array;
import cbit.util.Range;
import cbit.vcell.solvers.CartesianMesh;
/**
 * Insert the type's description here.
 * Creation date: (10/5/00 11:14:42 AM)
 * @author: 
 */
public class SourceDataInfo {
	private int type;
	private Object data;
	private int startIndex;
	private int xSize;
	private int xIncrement;
	private int ySize;
	private int yIncrement;
	private int zSize;
	private int zIncrement;
	private Extent extent;
	private Origin origin;
	private Range minmax;
	//
	public static final int RAW_VALUE_TYPE = 0;
	public static final int INT_RGB_TYPE = 1;
	public static final int INDEX_TYPE = 2;

/**
 * ImageSourceDataInfo constructor comment.
 */
public SourceDataInfo(int argType, Object argData, Extent argExtent,Origin argOrigin,Range argMinMax,int argStartIndex, int argXSize, int argXIncrement, int argYSize, int argYIncrement, int argZSize, int argZIncrement) {
	super();
	type = argType;
	data = argData;
	startIndex = argStartIndex;
	xSize = argXSize;
	xIncrement = argXIncrement;
	ySize = argYSize;
	yIncrement = argYIncrement;
	zSize = argZSize;
	zIncrement = argZIncrement;
	if(argExtent == null){
		extent = new Extent(argXSize-1,argYSize-1,argZSize-1);
	}else{
		extent = argExtent;
	}
	if(argOrigin == null){
		origin = new Origin(0,0,0);
	}else{
		origin = argOrigin;
	}
	minmax = argMinMax;
	if (!isConsistent()) {
		throw new IllegalArgumentException("SourceDataInfo arguments are incosistent");
	}
}


/**
 * ImageSourceDataInfo constructor comment.
 */
public SourceDataInfo(int argType,Object argData,Range dataRange,int argStartIndex,int argXSize,int argXIncrement,double argXOrigin,double argXExtent,int argYSize,int argYIncrement,double argYOrigin,double argYExtent) {
	this(argType,argData,new Extent(argXExtent,argYExtent,1),new Origin(argXOrigin,argYOrigin,0),dataRange,argStartIndex,argXSize,argXIncrement,argYSize,argYIncrement,1,0);
}


/**
 * Insert the method's description here.
 * Creation date: (10/11/00 10:47:09 AM)
 */
private int calculateDataIndex(int x, int y, int z) {
    return startIndex + (x * xIncrement) + (y * yIncrement) + (z * zIncrement);
}

public int calculateWorldIndex(CoordinateIndex ci){
	return ci.x+(ci.y*xSize) + (ci.z*xSize*ySize);
}

/**
 * Insert the method's description here.
 * Creation date: (10/5/00 5:13:52 PM)
 * @return java.lang.Object
 */
public Object getData() {
	return data;
}


/**
 * Insert the method's description here.
 * Creation date: (10/11/00 10:44:50 AM)
 * @return double
 * @param x int
 * @param y int
 * @param z int
 */
public int getDataAsTypeIndex(int x, int y, int z) {
    if (data instanceof int[]) {
        return getDataAsTypeInt(x, y, z);
    } else {
        byte[] dataAsByte = (byte[]) data;
        return dataAsByte[calculateDataIndex(x, y, z)] & 0xFF;
    }
}


/**
 * Insert the method's description here.
 * Creation date: (10/11/00 10:44:50 AM)
 * @return double
 * @param x int
 * @param y int
 * @param z int
 */
private int getDataAsTypeInt(int x, int y, int z) {
	int[] dataAsInt = (int[])data;
	return dataAsInt[calculateDataIndex(x,y,z)];
}


/**
 * Insert the method's description here.
 * Creation date: (10/11/00 10:44:50 AM)
 * @return double
 * @param x int
 * @param y int
 * @param z int
 */
public int getDataAsTypeIntRGB(int x, int y, int z) {
	return getDataAsTypeInt(x,y,z);
}


/**
 * Insert the method's description here.
 * Creation date: (10/11/00 10:44:50 AM)
 * @return double
 * @param x int
 * @param y int
 * @param z int
 */
public double getDataAsTypeRaw(int x, int y, int z) {
	double[] dataAsDouble = (double[])data;
	return dataAsDouble[calculateDataIndex(x,y,z)];
}


/**
 * Insert the method's description here.
 * Creation date: (10/11/00 12:23:15 PM)
 */
public cbit.vcell.math.CoordinateIndex getDataIndexFromUnitized(double unitizedX, double unitizedY, double unitizedZ) {
	if(unitizedX < 0 || unitizedX > 1 || unitizedY < 0 || unitizedY > 1 || unitizedZ < 0 || unitizedZ > 1){
		return null;
	}
	int xCoordIndex = (getXSize()==1)?(0):((int) Math.round(unitizedX * (getXSize()-1)));
	int yCoordIndex = (getYSize()==1)?(0):((int) Math.round(unitizedY * (getYSize()-1)));
	int zCoordIndex = (getZSize()==1)?(0):((int) Math.round(unitizedZ * (getZSize()-1)));
	return new cbit.vcell.math.CoordinateIndex(xCoordIndex,yCoordIndex,zCoordIndex);
}


/**
 * Insert the method's description here.
 * Creation date: (10/11/00 12:23:15 PM)
 */
public cbit.vcell.math.CoordinateIndex getDataIndexFromWorldCoordinate(Coordinate worldCoordinate) {
	
	double unitizedX = (worldCoordinate.getX()-origin.getX())/extent.getX();
	double unitizedY = (worldCoordinate.getY()-origin.getY())/extent.getY();
	double unitizedZ = (worldCoordinate.getZ()-origin.getZ())/extent.getZ();
	
	if(unitizedX < 0){unitizedX = 0;}
	if(unitizedX > 1){unitizedX = 1;}
	if(unitizedY < 0){unitizedY = 0;}
	if(unitizedY > 1){unitizedY = 1;}
	if(unitizedZ < 0){unitizedZ = 0;}
	if(unitizedZ > 1){unitizedZ = 1;}

	//int xCoordIndex = (getXSize()==1)?(0):((int) Math.round(unitizedX * (getXSize()-1)));
	//int yCoordIndex = (getYSize()==1)?(0):((int) Math.round(unitizedY * (getYSize()-1)));
	//int zCoordIndex = (getZSize()==1)?(0):((int) Math.round(unitizedZ * (getZSize()-1)));
	//return new cbit.vcell.math.CoordinateIndex(xCoordIndex,yCoordIndex,zCoordIndex);
	return getDataIndexFromUnitized(unitizedX,unitizedY,unitizedZ);
	
}


/**
 * Insert the method's description here.
 * Creation date: (11/10/2000 4:56:07 PM)
 * @return java.lang.String
 * @param x int
 * @param y int
 * @param z int
 */
public String getDataValueAsString(int x, int y, int z) {
	String result = null;
	switch (getType()) {
		case RAW_VALUE_TYPE :
			result = "Value = " + getDataAsTypeRaw(x, y, z);
			break;
		case INDEX_TYPE :
			result = "Index = " + getDataAsTypeIndex(x, y, z);
			break;
		case INT_RGB_TYPE :
			int argb = getDataAsTypeIntRGB(x, y, z);
			int red = (argb & 0xFF0000) >> 16;
			int grn = (argb & 0xFF00) >> 8;
			int blu = (argb & 0xFF);
			result = "RGB = " + "(" + red + "," + grn + "," + blu + ")";
			break;
	}
	return result;
}


/**
 * Insert the method's description here.
 * Creation date: (10/5/00 12:07:52 PM)
 * @return int
 */
public Extent getExtent() {
	return extent;
}


/**
 * Insert the method's description here.
 * Creation date: (10/5/00 12:07:52 PM)
 * @return int
 */
public Range getMinMax() {
	return minmax;
}


/**
 * Insert the method's description here.
 * Creation date: (10/5/00 12:07:52 PM)
 * @return int
 */
public Origin getOrigin() {
	return origin;
}


/**
 * Insert the method's description here.
 * Creation date: (10/5/00 4:40:53 PM)
 */
public int getStartIndex() {
    return startIndex;
}


/**
 * Insert the method's description here.
 * Creation date: (10/5/00 12:30:42 PM)
 * @return int
 */
public int getType() {
	return type;
}


/**
 * Insert the method's description here.
 * Creation date: (10/11/00 12:23:15 PM)
 */
public Coordinate getWorldCoordinateFromIndex(cbit.vcell.math.CoordinateIndex ci) {
	double x = (getXSize()==1)?CartesianMesh.coordComponentFromSinglePlanePolicy(origin,extent,Coordinate.X_AXIS):(((double)ci.x/(double)(getXSize()-1))*extent.getX() + origin.getX());
	double y = (getYSize()==1)?CartesianMesh.coordComponentFromSinglePlanePolicy(origin,extent,Coordinate.Y_AXIS):(((double)ci.y/(double)(getYSize()-1))*extent.getY() + origin.getY());
	double z = (getZSize()==1)?CartesianMesh.coordComponentFromSinglePlanePolicy(origin,extent,Coordinate.Z_AXIS):(((double)ci.z/(double)(getZSize()-1))*extent.getZ() + origin.getZ());
	return new Coordinate(x, y, z);
}


/**
 * Insert the method's description here.
 * Creation date: (10/11/00 12:23:15 PM)
 */
public Coordinate getWorldCoordinateFromUnitized(double unitizedX, double unitizedY, double unitizedZ) {

	//return new Coordinate(origin.getX() + (unitizedX * extent.getX()), origin.getY() + (unitizedY * extent.getY()), origin.getZ() + (unitizedZ * extent.getZ()));
	double x = (getXSize()==1?CartesianMesh.coordComponentFromSinglePlanePolicy(origin,extent,Coordinate.X_AXIS):origin.getX() + (unitizedX * extent.getX()));
	double y = (getYSize()==1?CartesianMesh.coordComponentFromSinglePlanePolicy(origin,extent,Coordinate.Y_AXIS):origin.getY() + (unitizedY * extent.getY()));
	double z = (getZSize()==1?CartesianMesh.coordComponentFromSinglePlanePolicy(origin,extent,Coordinate.Z_AXIS):origin.getZ() + (unitizedZ * extent.getZ()));
	return new Coordinate(x,y,z);
}


/**
 * Insert the method's description here.
 * Creation date: (10/5/00 4:33:29 PM)
 * @return int
 */
public int getXIncrement() {
	return xIncrement;
}


/**
 * Insert the method's description here.
 * Creation date: (10/5/00 12:07:52 PM)
 * @return int
 */
public int getXSize() {
	return xSize;
}


/**
 * Insert the method's description here.
 * Creation date: (10/5/00 4:33:29 PM)
 * @return int
 */
public int getYIncrement() {
	return yIncrement;
}


/**
 * Insert the method's description here.
 * Creation date: (10/5/00 12:07:52 PM)
 * @return int
 */
public int getYSize() {
	return ySize;
}


/**
 * Insert the method's description here.
 * Creation date: (10/5/00 4:33:29 PM)
 * @return int
 */
public int getZIncrement() {
	return zIncrement;
}


/**
 * Insert the method's description here.
 * Creation date: (10/5/00 12:07:52 PM)
 * @return int
 */
public int getZSize() {
	return zSize;
}


/**
 * Insert the method's description here.
 * Creation date: (10/11/00 10:35:11 AM)
 * @return boolean
 */
private boolean isConsistent() {
	if (type != RAW_VALUE_TYPE && type != INT_RGB_TYPE && type != INDEX_TYPE) {
		return false;
	}
	if (xSize <= 0 || ySize <= 0 || zSize <= 0) {
		return false;
	}
	if (data != null) {
		if (!data.getClass().isArray()) {
			return false;
		}
		if ((calculateDataIndex(xSize - 1, ySize - 1, zSize - 1)) >= Array.getLength(data)) {
			return false;
		}
		if (type == RAW_VALUE_TYPE && !(data instanceof double[])) {
			return false;
		}
		if (type == INT_RGB_TYPE && !(data instanceof int[])) {
			return false;
		}
		if (type == INDEX_TYPE && (!(data instanceof int[]) && !(data instanceof byte[]))) {
			return false;
		}
	}
	
	return true;
}


/**
 * Insert the method's description here.
 * Creation date: (8/19/2005 1:07:56 PM)
 * @return boolean
 */
public boolean isDataNull() {
	return data == null;
}


/**
 * Insert the method's description here.
 * Creation date: (10/5/00 12:20:54 PM)
 * @return boolean
 */
public boolean needsColorConversion() {
	return !(type == INT_RGB_TYPE);
}
}